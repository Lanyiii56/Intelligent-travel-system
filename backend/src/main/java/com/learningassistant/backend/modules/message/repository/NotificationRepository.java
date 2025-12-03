package com.learningassistant.backend.modules.message.repository;

import com.learningassistant.backend.modules.message.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 通知Repository
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // 获取用户的所有通知
    List<Notification> findByUserIdOrderByCreateTimeDesc(Long userId);
    
    // 按类型获取通知
    List<Notification> findByUserIdAndTypeOrderByCreateTimeDesc(Long userId, String type);
    
    // 获取未读通知数量
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = :userId AND n.isRead = false")
    Long countUnread(@Param("userId") Long userId);
    
    // 按类型获取未读通知数量
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = :userId AND n.type = :type AND n.isRead = false")
    Long countUnreadByType(@Param("userId") Long userId, @Param("type") String type);
}
