package com.learningassistant.backend.modules.message.service;

import com.learningassistant.backend.modules.auth.model.User;
import com.learningassistant.backend.modules.auth.repository.UserRepository;
import com.learningassistant.backend.modules.message.model.Follow;
import com.learningassistant.backend.modules.message.model.Message;
import com.learningassistant.backend.modules.message.model.Notification;
import com.learningassistant.backend.modules.message.repository.FollowRepository;
import com.learningassistant.backend.modules.message.repository.MessageRepository;
import com.learningassistant.backend.modules.message.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 消息服务
 */
@Service
@RequiredArgsConstructor
public class MessageService {
    
    private final MessageRepository messageRepository;
    private final NotificationRepository notificationRepository;
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    
    // ==================== 私信相关 ====================
    
    /**
     * 发送私信
     */
    @Transactional
    public Message sendMessage(Long senderId, Long receiverId, String content, String type) {
        Message message = new Message();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setType(type != null ? type : "text");
        message.setCreateTime(LocalDateTime.now());
        return messageRepository.save(message);
    }
    
    /**
     * 获取会话列表
     */
    public List<Map<String, Object>> getConversationList(Long userId) {
        List<Message> messages = messageRepository.findConversationList(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Message msg : messages) {
            Map<String, Object> conversation = new HashMap<>();
            Long otherUserId = msg.getSenderId().equals(userId) ? msg.getReceiverId() : msg.getSenderId();
            
            // 获取对方用户信息
            User otherUser = userRepository.findById(otherUserId).orElse(null);
            if (otherUser != null) {
                conversation.put("userId", otherUser.getId());
                conversation.put("nickname", otherUser.getNickname() != null ? otherUser.getNickname() : otherUser.getUsername());
                conversation.put("avatar", otherUser.getAvatarUrl());
            }
            
            conversation.put("lastMessage", msg.getContent());
            conversation.put("lastMessageTime", msg.getCreateTime());
            conversation.put("unreadCount", messageRepository.countUnreadFrom(otherUserId, userId));
            
            result.add(conversation);
        }
        
        return result;
    }
    
    /**
     * 获取与某用户的聊天记录
     */
    public List<Map<String, Object>> getConversation(Long userId, Long otherUserId) {
        List<Message> messages = messageRepository.findConversation(userId, otherUserId);
        
        // 标记消息为已读
        messages.stream()
            .filter(m -> m.getReceiverId().equals(userId) && !m.getIsRead())
            .forEach(m -> {
                m.setIsRead(true);
                messageRepository.save(m);
            });
        
        return messages.stream().map(msg -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", msg.getId());
            map.put("senderId", msg.getSenderId());
            map.put("receiverId", msg.getReceiverId());
            map.put("content", msg.getContent());
            map.put("type", msg.getType());
            map.put("createTime", msg.getCreateTime());
            map.put("isMine", msg.getSenderId().equals(userId));
            return map;
        }).collect(Collectors.toList());
    }
    
    /**
     * 获取未读消息总数
     */
    public Long getUnreadCount(Long userId) {
        return messageRepository.countUnread(userId);
    }
    
    // ==================== 通知相关 ====================
    
    /**
     * 创建通知
     */
    @Transactional
    public Notification createNotification(Long userId, Long fromUserId, String type, 
                                           String title, String content, Long targetId, String targetType) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setFromUserId(fromUserId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setTargetId(targetId);
        notification.setTargetType(targetType);
        notification.setCreateTime(LocalDateTime.now());
        return notificationRepository.save(notification);
    }
    
    /**
     * 获取用户的通知列表
     */
    public List<Map<String, Object>> getNotifications(Long userId, String type) {
        List<Notification> notifications;
        if (type != null && !type.isEmpty()) {
            notifications = notificationRepository.findByUserIdAndTypeOrderByCreateTimeDesc(userId, type);
        } else {
            notifications = notificationRepository.findByUserIdOrderByCreateTimeDesc(userId);
        }
        
        return notifications.stream().map(n -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", n.getId());
            map.put("type", n.getType());
            map.put("title", n.getTitle());
            map.put("content", n.getContent());
            map.put("targetId", n.getTargetId());
            map.put("targetType", n.getTargetType());
            map.put("isRead", n.getIsRead());
            map.put("createTime", n.getCreateTime());
            
            // 获取触发者信息
            if (n.getFromUserId() != null) {
                User fromUser = userRepository.findById(n.getFromUserId()).orElse(null);
                if (fromUser != null) {
                    map.put("fromUserId", fromUser.getId());
                    map.put("fromUserNickname", fromUser.getNickname() != null ? fromUser.getNickname() : fromUser.getUsername());
                    map.put("fromUserAvatar", fromUser.getAvatarUrl());
                }
            }
            
            return map;
        }).collect(Collectors.toList());
    }
    
    /**
     * 标记通知为已读
     */
    @Transactional
    public void markNotificationRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setIsRead(true);
            notificationRepository.save(n);
        });
    }
    
    /**
     * 标记所有通知为已读
     */
    @Transactional
    public void markAllNotificationsRead(Long userId, String type) {
        List<Notification> notifications;
        if (type != null && !type.isEmpty()) {
            notifications = notificationRepository.findByUserIdAndTypeOrderByCreateTimeDesc(userId, type);
        } else {
            notifications = notificationRepository.findByUserIdOrderByCreateTimeDesc(userId);
        }
        
        notifications.stream()
            .filter(n -> !n.getIsRead())
            .forEach(n -> {
                n.setIsRead(true);
                notificationRepository.save(n);
            });
    }
    
    /**
     * 获取通知统计
     */
    public Map<String, Long> getNotificationStats(Long userId) {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", notificationRepository.countUnread(userId));
        stats.put("like", notificationRepository.countUnreadByType(userId, "like"));
        stats.put("follow", notificationRepository.countUnreadByType(userId, "follow"));
        stats.put("comment", notificationRepository.countUnreadByType(userId, "comment"));
        stats.put("system", notificationRepository.countUnreadByType(userId, "system"));
        return stats;
    }
    
    // ==================== 关注相关 ====================
    
    /**
     * 关注用户
     */
    @Transactional
    public boolean toggleFollow(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new IllegalArgumentException("不能关注自己");
        }
        
        Optional<Follow> existing = followRepository.findByFollowerIdAndFollowingId(followerId, followingId);
        
        if (existing.isPresent()) {
            // 取消关注
            followRepository.delete(existing.get());
            return false;
        } else {
            // 添加关注
            Follow follow = new Follow();
            follow.setFollowerId(followerId);
            follow.setFollowingId(followingId);
            follow.setCreateTime(LocalDateTime.now());
            followRepository.save(follow);
            
            // 创建关注通知
            User follower = userRepository.findById(followerId).orElse(null);
            if (follower != null) {
                createNotification(followingId, followerId, "follow", 
                    "新增关注", 
                    (follower.getNickname() != null ? follower.getNickname() : follower.getUsername()) + " 关注了你",
                    null, null);
            }
            
            return true;
        }
    }
    
    /**
     * 检查是否已关注
     */
    public boolean isFollowing(Long followerId, Long followingId) {
        return followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }
    
    /**
     * 获取关注列表
     */
    public List<Map<String, Object>> getFollowingList(Long userId) {
        List<Follow> follows = followRepository.findByFollowerIdOrderByCreateTimeDesc(userId);
        return follows.stream().map(f -> {
            Map<String, Object> map = new HashMap<>();
            User user = userRepository.findById(f.getFollowingId()).orElse(null);
            if (user != null) {
                map.put("userId", user.getId());
                map.put("nickname", user.getNickname() != null ? user.getNickname() : user.getUsername());
                map.put("avatar", user.getAvatarUrl());
                map.put("motto", user.getMotto());
            }
            map.put("followTime", f.getCreateTime());
            return map;
        }).collect(Collectors.toList());
    }
    
    /**
     * 获取粉丝列表
     */
    public List<Map<String, Object>> getFollowersList(Long userId) {
        List<Follow> follows = followRepository.findByFollowingIdOrderByCreateTimeDesc(userId);
        return follows.stream().map(f -> {
            Map<String, Object> map = new HashMap<>();
            User user = userRepository.findById(f.getFollowerId()).orElse(null);
            if (user != null) {
                map.put("userId", user.getId());
                map.put("nickname", user.getNickname() != null ? user.getNickname() : user.getUsername());
                map.put("avatar", user.getAvatarUrl());
                map.put("motto", user.getMotto());
                // 检查是否互相关注
                map.put("isFollowing", followRepository.existsByFollowerIdAndFollowingId(userId, user.getId()));
            }
            map.put("followTime", f.getCreateTime());
            return map;
        }).collect(Collectors.toList());
    }
    
    /**
     * 获取关注统计
     */
    public Map<String, Long> getFollowStats(Long userId) {
        Map<String, Long> stats = new HashMap<>();
        stats.put("following", followRepository.countFollowing(userId));
        stats.put("followers", followRepository.countFollowers(userId));
        return stats;
    }
}
