/**
 * 模块: 消息社交模块 - API
 * 功能: 私信、通知、关注
 */
import api from '@/api/request';

// ==================== 类型定义 ====================

export interface Message {
  id: number;
  senderId: number;
  receiverId: number;
  content: string;
  type: string;
  createTime: string;
  isMine?: boolean;
}

export interface Conversation {
  userId: number;
  nickname: string;
  avatar: string;
  lastMessage: string;
  lastMessageTime: string;
  unreadCount: number;
}

export interface Notification {
  id: number;
  type: 'like' | 'follow' | 'comment' | 'system';
  title: string;
  content: string;
  targetId?: number;
  targetType?: string;
  isRead: boolean;
  createTime: string;
  fromUserId?: number;
  fromUserNickname?: string;
  fromUserAvatar?: string;
}

export interface FollowUser {
  userId: number;
  nickname: string;
  avatar: string;
  motto?: string;
  followTime: string;
  isFollowing?: boolean;
}

export interface NotificationStats {
  total: number;
  like: number;
  follow: number;
  comment: number;
  system: number;
}

export interface FollowStats {
  following: number;
  followers: number;
}

export interface MessageStats {
  unreadMessages: number;
  notifications: NotificationStats;
  follow: FollowStats;
}

// ==================== 私信 API ====================

/** 发送私信 */
export function sendMessage(senderId: number, receiverId: number, content: string, type: string = 'text') {
  return api.post('/message/send', null, { params: { senderId, receiverId, content, type } });
}

/** 获取会话列表 */
export function getConversationList(userId: number) {
  return api.get<Conversation[]>(`/message/conversations/${userId}`);
}

/** 获取聊天记录 */
export function getConversation(userId: number, otherUserId: number) {
  return api.get<Message[]>('/message/conversation', { params: { userId, otherUserId } });
}

/** 获取未读消息数 */
export function getUnreadCount(userId: number) {
  return api.get<number>(`/message/unread/${userId}`);
}

// ==================== 通知 API ====================

/** 获取通知列表 */
export function getNotifications(userId: number, type?: string) {
  return api.get<Notification[]>(`/message/notifications/${userId}`, { params: { type } });
}

/** 标记通知已读 */
export function markNotificationRead(notificationId: number) {
  return api.post(`/message/notification/${notificationId}/read`);
}

/** 标记所有通知已读 */
export function markAllNotificationsRead(userId: number, type?: string) {
  return api.post(`/message/notifications/${userId}/read-all`, null, { params: { type } });
}

/** 获取通知统计 */
export function getNotificationStats(userId: number) {
  return api.get<NotificationStats>(`/message/notifications/${userId}/stats`);
}

// ==================== 关注 API ====================

/** 关注/取消关注 */
export function toggleFollow(followerId: number, followingId: number) {
  return api.post<{ isFollowing: boolean; message: string }>('/message/follow', null, { params: { followerId, followingId } });
}

/** 检查是否已关注 */
export function checkFollow(followerId: number, followingId: number) {
  return api.get<boolean>('/message/follow/check', { params: { followerId, followingId } });
}

/** 获取关注列表 */
export function getFollowingList(userId: number) {
  return api.get<FollowUser[]>(`/message/following/${userId}`);
}

/** 获取粉丝列表 */
export function getFollowersList(userId: number) {
  return api.get<FollowUser[]>(`/message/followers/${userId}`);
}

/** 获取关注统计 */
export function getFollowStats(userId: number) {
  return api.get<FollowStats>(`/message/follow/${userId}/stats`);
}

// ==================== 综合 API ====================

/** 获取消息中心统计 */
export function getMessageStats(userId: number) {
  return api.get<MessageStats>(`/message/stats/${userId}`);
}
