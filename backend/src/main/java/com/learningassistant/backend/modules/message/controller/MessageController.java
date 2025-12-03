package com.learningassistant.backend.modules.message.controller;

import com.learningassistant.backend.modules.message.model.Message;
import com.learningassistant.backend.modules.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息控制器
 * 模块: message (社交模块)
 */
@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MessageController {
    
    private final MessageService messageService;
    
    // ==================== 私信相关 ====================
    
    /**
     * 发送私信
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(
            @RequestParam Long senderId,
            @RequestParam Long receiverId,
            @RequestParam String content,
            @RequestParam(required = false, defaultValue = "text") String type) {
        Message message = messageService.sendMessage(senderId, receiverId, content, type);
        return ResponseEntity.ok(message);
    }
    
    /**
     * 获取会话列表
     */
    @GetMapping("/conversations/{userId}")
    public ResponseEntity<?> getConversationList(@PathVariable Long userId) {
        return ResponseEntity.ok(messageService.getConversationList(userId));
    }
    
    /**
     * 获取与某用户的聊天记录
     */
    @GetMapping("/conversation")
    public ResponseEntity<?> getConversation(
            @RequestParam Long userId,
            @RequestParam Long otherUserId) {
        return ResponseEntity.ok(messageService.getConversation(userId, otherUserId));
    }
    
    /**
     * 获取未读消息数
     */
    @GetMapping("/unread/{userId}")
    public ResponseEntity<?> getUnreadCount(@PathVariable Long userId) {
        return ResponseEntity.ok(messageService.getUnreadCount(userId));
    }
    
    // ==================== 通知相关 ====================
    
    /**
     * 获取通知列表
     */
    @GetMapping("/notifications/{userId}")
    public ResponseEntity<?> getNotifications(
            @PathVariable Long userId,
            @RequestParam(required = false) String type) {
        return ResponseEntity.ok(messageService.getNotifications(userId, type));
    }
    
    /**
     * 标记通知为已读
     */
    @PostMapping("/notification/{notificationId}/read")
    public ResponseEntity<?> markNotificationRead(@PathVariable Long notificationId) {
        messageService.markNotificationRead(notificationId);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 标记所有通知为已读
     */
    @PostMapping("/notifications/{userId}/read-all")
    public ResponseEntity<?> markAllNotificationsRead(
            @PathVariable Long userId,
            @RequestParam(required = false) String type) {
        messageService.markAllNotificationsRead(userId, type);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 获取通知统计
     */
    @GetMapping("/notifications/{userId}/stats")
    public ResponseEntity<?> getNotificationStats(@PathVariable Long userId) {
        return ResponseEntity.ok(messageService.getNotificationStats(userId));
    }
    
    // ==================== 关注相关 ====================
    
    /**
     * 关注/取消关注
     */
    @PostMapping("/follow")
    public ResponseEntity<?> toggleFollow(
            @RequestParam Long followerId,
            @RequestParam Long followingId) {
        boolean isFollowing = messageService.toggleFollow(followerId, followingId);
        Map<String, Object> result = new HashMap<>();
        result.put("isFollowing", isFollowing);
        result.put("message", isFollowing ? "关注成功" : "已取消关注");
        return ResponseEntity.ok(result);
    }
    
    /**
     * 检查是否已关注
     */
    @GetMapping("/follow/check")
    public ResponseEntity<?> checkFollow(
            @RequestParam Long followerId,
            @RequestParam Long followingId) {
        return ResponseEntity.ok(messageService.isFollowing(followerId, followingId));
    }
    
    /**
     * 获取关注列表
     */
    @GetMapping("/following/{userId}")
    public ResponseEntity<?> getFollowingList(@PathVariable Long userId) {
        return ResponseEntity.ok(messageService.getFollowingList(userId));
    }
    
    /**
     * 获取粉丝列表
     */
    @GetMapping("/followers/{userId}")
    public ResponseEntity<?> getFollowersList(@PathVariable Long userId) {
        return ResponseEntity.ok(messageService.getFollowersList(userId));
    }
    
    /**
     * 获取关注统计
     */
    @GetMapping("/follow/{userId}/stats")
    public ResponseEntity<?> getFollowStats(@PathVariable Long userId) {
        return ResponseEntity.ok(messageService.getFollowStats(userId));
    }
    
    // ==================== 综合统计 ====================
    
    /**
     * 获取消息中心统计（未读私信+未读通知）
     */
    @GetMapping("/stats/{userId}")
    public ResponseEntity<?> getMessageStats(@PathVariable Long userId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("unreadMessages", messageService.getUnreadCount(userId));
        stats.put("notifications", messageService.getNotificationStats(userId));
        stats.put("follow", messageService.getFollowStats(userId));
        return ResponseEntity.ok(stats);
    }
}
