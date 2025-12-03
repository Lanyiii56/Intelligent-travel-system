<template>
  <div class="messages-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">消息</h1>
      <div class="header-actions">
        <button class="action-btn" @click="showSearchModal = true">
          <span class="icon">🔍</span>
        </button>
        <button class="action-btn" @click="refreshData">
          <span class="icon">🔄</span>
        </button>
      </div>
    </div>

    <!-- 搜索用户入口 -->
    <div class="search-bar" @click="showSearchModal = true">
      <span class="search-icon">🔍</span>
      <span class="search-placeholder">搜索用户</span>
    </div>

    <!-- 通知入口卡片 -->
    <div class="notification-cards">
      <div class="notification-card likes" @click="showNotifications('like')">
        <div class="card-icon">❤️</div>
        <div class="card-label">赞和收藏</div>
        <div class="badge" v-if="(stats.notifications?.like ?? 0) > 0">{{ stats.notifications?.like }}</div>
      </div>
      <div class="notification-card follows" @click="showNotifications('follow')">
        <div class="card-icon">👤</div>
        <div class="card-label">新增关注</div>
        <div class="badge" v-if="(stats.notifications?.follow ?? 0) > 0">{{ stats.notifications?.follow }}</div>
      </div>
      <div class="notification-card comments" @click="showNotifications('comment')">
        <div class="card-icon">💬</div>
        <div class="card-label">评论和@</div>
        <div class="badge" v-if="(stats.notifications?.comment ?? 0) > 0">{{ stats.notifications?.comment }}</div>
      </div>
    </div>

    <!-- 我的关注 -->
    <div class="my-following-section">
      <div class="section-header">
        <h2>我的关注</h2>
        <span class="following-count">{{ followStats.following }}人</span>
      </div>
      <div class="following-list" v-if="followingList.length > 0">
        <div 
          class="following-item" 
          v-for="user in followingList.slice(0, 10)" 
          :key="user.userId"
          @click="goToUserProfile(user.userId)"
        >
          <img :src="user.avatar || defaultAvatar" class="following-avatar" />
          <span class="following-name">{{ user.nickname }}</span>
        </div>
        <div class="following-more" v-if="followingList.length > 10" @click="showFollowingModal = true">
          <span class="more-icon">+{{ followingList.length - 10 }}</span>
        </div>
      </div>
      <div class="empty-following" v-else>
        <span>还没有关注任何人</span>
      </div>
    </div>

    <!-- 会话列表 -->
    <div class="conversations-section">
      <div class="section-header">
        <h2>私信</h2>
        <span class="unread-total" v-if="(stats.unreadMessages ?? 0) > 0">{{ stats.unreadMessages }}条未读</span>
      </div>

      <!-- 加载状态 -->
      <div class="loading-state" v-if="loading">
        <div class="loading-spinner"></div>
        <span>加载中...</span>
      </div>

      <!-- 空状态 -->
      <div class="empty-state" v-else-if="conversations.length === 0">
        <div class="empty-icon">💬</div>
        <p>暂无私信消息</p>
        <span class="empty-hint">去关注感兴趣的用户，开始聊天吧</span>
      </div>

      <!-- 会话列表 -->
      <div class="conversation-list" v-else>
        <div 
          class="conversation-item" 
          v-for="conv in conversations" 
          :key="conv.userId"
        >
          <div class="avatar-wrapper" @click.stop="goToUserProfile(conv.userId)">
            <img 
              :src="conv.avatar || defaultAvatar" 
              :alt="conv.nickname"
              class="avatar clickable"
            />
            <div class="online-dot" v-if="isOnline(conv.userId)"></div>
          </div>
          <div class="conv-content" @click="openChat(conv.userId)">
            <div class="conv-header">
              <span class="nickname">{{ conv.nickname }}</span>
              <span class="time">{{ formatTime(conv.lastMessageTime) }}</span>
            </div>
            <div class="conv-preview">
              <span class="last-message">{{ conv.lastMessage }}</span>
              <span class="unread-badge" v-if="conv.unreadCount > 0">{{ conv.unreadCount }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 通知弹窗 -->
    <div class="notification-modal" v-if="showNotificationModal" @click.self="closeNotificationModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ notificationTitle }}</h3>
          <button class="close-btn" @click="closeNotificationModal">✕</button>
        </div>
        <div class="modal-actions" v-if="notifications.length > 0">
          <button class="mark-all-read" @click="markAllRead">全部已读</button>
        </div>
        <div class="notification-list" v-if="notifications.length > 0">
          <div 
            class="notification-item" 
            v-for="notif in notifications" 
            :key="notif.id"
            :class="{ unread: !notif.isRead }"
            @click="handleNotificationClick(notif)"
          >
            <img 
              :src="notif.fromUserAvatar || defaultAvatar" 
              class="notif-avatar"
              v-if="notif.fromUserId"
            />
            <div class="notif-icon" v-else>
              {{ getNotificationIcon(notif.type) }}
            </div>
            <div class="notif-content">
              <div class="notif-text">
                <span class="notif-user" v-if="notif.fromUserNickname">{{ notif.fromUserNickname }}</span>
                {{ notif.content }}
              </div>
              <div class="notif-time">{{ formatTime(notif.createTime) }}</div>
            </div>
          </div>
        </div>
        <div class="empty-notifications" v-else>
          <div class="empty-icon">📭</div>
          <p>暂无{{ notificationTitle }}</p>
        </div>
      </div>
    </div>

    <!-- 搜索用户弹窗 -->
    <div class="search-modal" v-if="showSearchModal" @click.self="closeSearchModal">
      <div class="search-modal-content">
        <div class="search-modal-header">
          <div class="search-input-wrapper">
            <span class="search-input-icon">🔍</span>
            <input 
              type="text" 
              v-model="searchKeyword"
              placeholder="搜索用户名或昵称"
              class="search-input"
              autofocus
            />
            <button v-if="searchKeyword" class="clear-btn" @click="searchKeyword = ''">✕</button>
          </div>
          <button class="cancel-btn" @click="closeSearchModal">取消</button>
        </div>
        
        <div class="search-results">
          <!-- 搜索中 -->
          <div class="searching-state" v-if="searching">
            <div class="loading-spinner small"></div>
            <span>搜索中...</span>
          </div>
          
          <!-- 搜索结果 -->
          <div class="result-list" v-else-if="searchResults.length > 0">
            <div 
              class="result-item" 
              v-for="user in searchResults" 
              :key="user.id"
            >
              <img 
                :src="user.avatarUrl || defaultAvatar" 
                class="result-avatar"
                @click="goToUserProfile(user.id)"
              />
              <div class="result-info" @click="goToUserProfile(user.id)">
                <span class="result-nickname">{{ user.nickname || user.username }}</span>
                <span class="result-username">@{{ user.username }}</span>
              </div>
              <div class="result-actions">
                <button class="action-chat" @click="startChatWithUser(user.id)">💬</button>
                <button class="action-profile" @click="goToUserProfile(user.id)">👤</button>
              </div>
            </div>
          </div>
          
          <!-- 无结果 -->
          <div class="no-results" v-else-if="searchKeyword && !searching">
            <div class="no-results-icon">🔍</div>
            <p>未找到相关用户</p>
            <span>试试其他关键词</span>
          </div>
          
          <!-- 初始状态 -->
          <div class="search-hint" v-else>
            <div class="hint-icon">👥</div>
            <p>搜索用户</p>
            <span>输入用户名或昵称查找</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 关注列表弹窗 -->
    <div class="following-modal" v-if="showFollowingModal" @click.self="showFollowingModal = false">
      <div class="following-modal-content">
        <div class="following-modal-header">
          <h3>我的关注</h3>
          <button class="close-btn" @click="showFollowingModal = false">✕</button>
        </div>
        <div class="following-modal-list">
          <div 
            class="following-modal-item" 
            v-for="user in followingList" 
            :key="user.userId"
          >
            <img 
              :src="user.avatar || defaultAvatar" 
              class="following-modal-avatar"
              @click="goToUserProfile(user.userId); showFollowingModal = false"
            />
            <div class="following-modal-info" @click="goToUserProfile(user.userId); showFollowingModal = false">
              <span class="following-modal-nickname">{{ user.nickname }}</span>
              <span class="following-modal-motto" v-if="user.motto">{{ user.motto }}</span>
            </div>
            <button class="chat-btn" @click="startChatWithUser(user.userId); showFollowingModal = false">
              💬 私信
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/modules/auth/store';
import * as messageApi from '@/modules/message/api';
import type { Conversation, Notification, MessageStats } from '@/modules/message/api';
import api from '@/api/request';

const router = useRouter();
const authStore = useAuthStore();

// 状态
const loading = ref(false);
const conversations = ref<Conversation[]>([]);
const notifications = ref<Notification[]>([]);
const stats = ref<Partial<MessageStats>>({});
const showNotificationModal = ref(false);
const currentNotificationType = ref<string>('');

// 搜索相关状态
const showSearchModal = ref(false);
const searchKeyword = ref('');
const searchResults = ref<any[]>([]);
const searching = ref(false);

// 关注相关状态
const followStats = ref({ following: 0, followers: 0 });
const followingList = ref<any[]>([]);
const showFollowingModal = ref(false);

// 默认头像
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';

// 计算属性
const notificationTitle = computed(() => {
  const titles: Record<string, string> = {
    like: '赞和收藏',
    follow: '新增关注',
    comment: '评论和@',
    system: '系统通知'
  };
  return titles[currentNotificationType.value] || '通知';
});

// 获取用户ID
const userId = computed(() => authStore.user?.id);

// 生命周期
onMounted(() => {
  if (userId.value) {
    loadData();
  }
});

// 方法
async function loadData() {
  loading.value = true;
  try {
    await Promise.all([
      loadConversations(),
      loadStats(),
      loadFollowingData()
    ]);
  } finally {
    loading.value = false;
  }
}

// 加载关注数据
async function loadFollowingData() {
  if (!userId.value) return;
  try {
    // 加载关注统计
    const statsRes = await messageApi.getFollowStats(userId.value);
    followStats.value = statsRes.data || { following: 0, followers: 0 };
    
    // 加载关注列表
    const listRes = await messageApi.getFollowingList(userId.value);
    followingList.value = listRes.data || [];
  } catch (error) {
    console.error('加载关注数据失败:', error);
  }
}

async function loadConversations() {
  if (!userId.value) return;
  try {
    const res = await messageApi.getConversationList(userId.value);
    conversations.value = res.data || [];
  } catch (error) {
    console.error('加载会话列表失败:', error);
  }
}

async function loadStats() {
  if (!userId.value) return;
  try {
    const res = await messageApi.getMessageStats(userId.value);
    stats.value = res.data || {};
  } catch (error) {
    console.error('加载统计失败:', error);
  }
}

async function loadNotifications(type: string) {
  if (!userId.value) return;
  try {
    const res = await messageApi.getNotifications(userId.value, type);
    notifications.value = res.data || [];
  } catch (error) {
    console.error('加载通知失败:', error);
  }
}

function refreshData() {
  loadData();
}

function showNotifications(type: string) {
  currentNotificationType.value = type;
  showNotificationModal.value = true;
  loadNotifications(type);
}

function closeNotificationModal() {
  showNotificationModal.value = false;
  notifications.value = [];
}

async function markAllRead() {
  if (!userId.value) return;
  try {
    await messageApi.markAllNotificationsRead(userId.value, currentNotificationType.value);
    notifications.value = notifications.value.map(n => ({ ...n, isRead: true }));
    loadStats();
  } catch (error) {
    console.error('标记已读失败:', error);
  }
}

async function handleNotificationClick(notif: Notification) {
  if (!notif.isRead) {
    try {
      await messageApi.markNotificationRead(notif.id);
      notif.isRead = true;
      loadStats();
    } catch (error) {
      console.error('标记已读失败:', error);
    }
  }
  
  // 根据通知类型跳转
  if (notif.targetType === 'spot' && notif.targetId) {
    router.push(`/spot/${notif.targetId}`);
    closeNotificationModal();
  } else if (notif.fromUserId) {
    openChat(notif.fromUserId);
    closeNotificationModal();
  }
}

function openChat(targetUserId: number) {
  router.push(`/chat/${targetUserId}`);
}

function isOnline(_userId: number): boolean {
  // 模拟在线状态，实际应该从后端获取
  return Math.random() > 0.7;
}

function formatTime(timeStr: string): string {
  if (!timeStr) return '';
  
  const date = new Date(timeStr);
  const now = new Date();
  const diff = now.getTime() - date.getTime();
  const days = Math.floor(diff / (1000 * 60 * 60 * 24));
  
  if (days === 0) {
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    return `${hours}:${minutes}`;
  } else if (days === 1) {
    return '昨天';
  } else if (days < 7) {
    const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
    return weekdays[date.getDay()];
  } else {
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    return `${month}-${day}`;
  }
}

function getNotificationIcon(type: string): string {
  const icons: Record<string, string> = {
    like: '❤️',
    follow: '👤',
    comment: '💬',
    system: '🔔'
  };
  return icons[type] || '📢';
}

// 搜索相关方法
let searchTimer: ReturnType<typeof setTimeout> | null = null;

watch(searchKeyword, (val) => {
  if (searchTimer) clearTimeout(searchTimer);
  if (!val.trim()) {
    searchResults.value = [];
    return;
  }
  searchTimer = setTimeout(() => {
    searchUsers();
  }, 300);
});

async function searchUsers() {
  if (!searchKeyword.value.trim()) return;
  searching.value = true;
  try {
    const res = await api.get('/auth/users/search', { params: { keyword: searchKeyword.value.trim() } });
    searchResults.value = res.data || [];
  } catch (error) {
    console.error('搜索用户失败:', error);
    searchResults.value = [];
  } finally {
    searching.value = false;
  }
}

function closeSearchModal() {
  showSearchModal.value = false;
  searchKeyword.value = '';
  searchResults.value = [];
}

function goToUserProfile(userId: number) {
  closeSearchModal();
  router.push(`/user/${userId}`);
}

function startChatWithUser(userId: number) {
  closeSearchModal();
  router.push(`/chat/${userId}`);
}
</script>

<style scoped>
.messages-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #333;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.action-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.9);
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.action-btn:hover {
  background: white;
  transform: scale(1.05);
}

.action-btn .icon {
  font-size: 18px;
}

/* 通知入口卡片 */
.notification-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 32px;
}

.notification-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  padding: 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
}

.notification-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
}

.notification-card.likes .card-icon {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a5a 100%);
}

.notification-card.follows .card-icon {
  background: linear-gradient(135deg, #4ecdc4 0%, #44a08d 100%);
}

.notification-card.comments .card-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.card-icon {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin: 0 auto 12px;
  color: white;
}

.card-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: #ff4757;
  color: white;
  font-size: 12px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 10px;
  min-width: 20px;
}

/* 会话列表区域 */
.conversations-section {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.section-header h2 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.unread-total {
  font-size: 13px;
  color: #ff4757;
  font-weight: 500;
}

/* 加载状态 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px;
  color: #999;
}

.loading-spinner {
  width: 32px;
  height: 32px;
  border: 3px solid #f0f0f0;
  border-top-color: #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 12px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-state p {
  font-size: 16px;
  color: #666;
  margin-bottom: 8px;
}

.empty-hint {
  font-size: 14px;
  color: #999;
}

/* 会话列表 */
.conversation-list {
  display: flex;
  flex-direction: column;
}

.conversation-item {
  display: flex;
  align-items: center;
  padding: 16px 0;
  cursor: pointer;
  transition: all 0.2s;
  border-bottom: 1px solid #f5f5f5;
}

.conversation-item:last-child {
  border-bottom: none;
}

.conversation-item:hover {
  background: #fafafa;
  margin: 0 -24px;
  padding: 16px 24px;
  border-radius: 12px;
}

.avatar-wrapper {
  position: relative;
  margin-right: 14px;
}

.avatar {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  object-fit: cover;
}

.avatar.clickable {
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.avatar.clickable:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.online-dot {
  position: absolute;
  bottom: 2px;
  right: 2px;
  width: 12px;
  height: 12px;
  background: #4cd964;
  border: 2px solid white;
  border-radius: 50%;
}

.conv-content {
  flex: 1;
  min-width: 0;
}

.conv-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.nickname {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.time {
  font-size: 12px;
  color: #999;
}

.conv-preview {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.last-message {
  font-size: 14px;
  color: #999;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 280px;
}

.unread-badge {
  background: #ff4757;
  color: white;
  font-size: 12px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 10px;
  min-width: 20px;
  text-align: center;
}

/* 通知弹窗 */
.notification-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.modal-content {
  background: white;
  border-radius: 20px;
  width: 100%;
  max-width: 500px;
  max-height: 80vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.modal-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.close-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #f5f5f5;
  border: none;
  cursor: pointer;
  font-size: 16px;
  color: #666;
  transition: all 0.2s;
}

.close-btn:hover {
  background: #eee;
}

.modal-actions {
  padding: 12px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.mark-all-read {
  background: none;
  border: none;
  color: #667eea;
  font-size: 14px;
  cursor: pointer;
  padding: 0;
}

.mark-all-read:hover {
  text-decoration: underline;
}

.notification-list {
  flex: 1;
  overflow-y: auto;
  padding: 12px 0;
}

.notification-item {
  display: flex;
  align-items: flex-start;
  padding: 14px 24px;
  cursor: pointer;
  transition: background 0.2s;
}

.notification-item:hover {
  background: #fafafa;
}

.notification-item.unread {
  background: #f8f9ff;
}

.notif-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 12px;
}

.notif-icon {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  margin-right: 12px;
}

.notif-content {
  flex: 1;
  min-width: 0;
}

.notif-text {
  font-size: 14px;
  color: #333;
  line-height: 1.5;
  margin-bottom: 4px;
}

.notif-user {
  font-weight: 600;
  margin-right: 4px;
}

.notif-time {
  font-size: 12px;
  color: #999;
}

.empty-notifications {
  text-align: center;
  padding: 60px 20px;
}

.empty-notifications .empty-icon {
  font-size: 48px;
  margin-bottom: 12px;
}

.empty-notifications p {
  font-size: 14px;
  color: #999;
}

/* 响应式 */
@media (max-width: 600px) {
  .messages-page {
    padding: 16px;
  }
  
  .notification-cards {
    gap: 12px;
  }
  
  .notification-card {
    padding: 16px 12px;
  }
  
  .card-icon {
    width: 48px;
    height: 48px;
    font-size: 20px;
  }
  
  .card-label {
    font-size: 12px;
  }
  
  .conversations-section {
    padding: 16px;
  }
  
  .conversation-item:hover {
    margin: 0 -16px;
    padding: 16px;
  }
}

/* 搜索栏 */
.search-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 25px;
  padding: 14px 20px;
  margin-bottom: 24px;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.search-bar:hover {
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.search-icon {
  font-size: 18px;
}

.search-placeholder {
  color: #999;
  font-size: 15px;
}

/* 搜索弹窗 */
.search-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: flex-start;
  justify-content: center;
  z-index: 1000;
  padding: 60px 20px 20px;
}

.search-modal-content {
  background: white;
  border-radius: 20px;
  width: 100%;
  max-width: 500px;
  max-height: 70vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.search-modal-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.search-input-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  background: #f5f5f5;
  border-radius: 20px;
  padding: 0 16px;
}

.search-input-icon {
  font-size: 16px;
  margin-right: 8px;
}

.search-input {
  flex: 1;
  border: none;
  background: none;
  padding: 12px 0;
  font-size: 15px;
  outline: none;
}

.search-input::placeholder {
  color: #999;
}

.clear-btn {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #ccc;
  border: none;
  color: white;
  font-size: 12px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cancel-btn {
  background: none;
  border: none;
  color: #667eea;
  font-size: 15px;
  cursor: pointer;
  padding: 8px;
}

.search-results {
  flex: 1;
  overflow-y: auto;
  padding: 16px 0;
}

.searching-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px 20px;
  color: #999;
}

.loading-spinner.small {
  width: 24px;
  height: 24px;
  border-width: 2px;
  margin-bottom: 12px;
}

.result-list {
  display: flex;
  flex-direction: column;
}

.result-item {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  transition: background 0.2s;
}

.result-item:hover {
  background: #fafafa;
}

.result-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 12px;
  cursor: pointer;
}

.result-info {
  flex: 1;
  min-width: 0;
  cursor: pointer;
}

.result-nickname {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  display: block;
}

.result-username {
  font-size: 13px;
  color: #999;
}

.result-actions {
  display: flex;
  gap: 8px;
}

.action-chat,
.action-profile {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: none;
  background: #f5f5f5;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.2s;
}

.action-chat:hover {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.action-profile:hover {
  background: #eee;
}

.no-results,
.search-hint {
  text-align: center;
  padding: 60px 20px;
}

.no-results-icon,
.hint-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.no-results p,
.search-hint p {
  font-size: 16px;
  color: #666;
  margin-bottom: 8px;
}

.no-results span,
.search-hint span {
  font-size: 14px;
  color: #999;
}

/* 我的关注区域 */
.my-following-section {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 20px 24px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.my-following-section .section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.my-following-section .section-header h2 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.following-count {
  font-size: 13px;
  color: #667eea;
  font-weight: 500;
}

.following-list {
  display: flex;
  gap: 16px;
  overflow-x: auto;
  padding: 8px 0;
}

.following-list::-webkit-scrollbar {
  display: none;
}

.following-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  flex-shrink: 0;
}

.following-avatar {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s, box-shadow 0.2s;
}

.following-item:hover .following-avatar {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.following-name {
  font-size: 12px;
  color: #666;
  max-width: 60px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  text-align: center;
}

.following-more {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
  flex-shrink: 0;
}

.more-icon {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  transition: transform 0.2s;
}

.following-more:hover .more-icon {
  transform: scale(1.05);
}

.empty-following {
  text-align: center;
  padding: 20px;
  color: #999;
  font-size: 14px;
}

/* 关注列表弹窗 */
.following-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.following-modal-content {
  background: white;
  border-radius: 20px;
  width: 100%;
  max-width: 400px;
  max-height: 70vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.following-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.following-modal-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.following-modal-list {
  flex: 1;
  overflow-y: auto;
  padding: 12px 0;
}

.following-modal-item {
  display: flex;
  align-items: center;
  padding: 12px 24px;
  transition: background 0.2s;
}

.following-modal-item:hover {
  background: #fafafa;
}

.following-modal-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 12px;
  cursor: pointer;
}

.following-modal-info {
  flex: 1;
  min-width: 0;
  cursor: pointer;
}

.following-modal-nickname {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  display: block;
}

.following-modal-motto {
  font-size: 13px;
  color: #999;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.chat-btn {
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 13px;
  border: none;
  cursor: pointer;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  transition: all 0.3s;
}

.chat-btn:hover {
  transform: scale(1.02);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}
</style>
