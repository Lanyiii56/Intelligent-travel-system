package com.learningassistant.backend.modules.message.repository;

import com.learningassistant.backend.modules.message.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 私信消息Repository
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    // 获取两个用户之间的聊天记录
    @Query("SELECT m FROM Message m WHERE (m.senderId = :userId1 AND m.receiverId = :userId2) " +
           "OR (m.senderId = :userId2 AND m.receiverId = :userId1) ORDER BY m.createTime ASC")
    List<Message> findConversation(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
    
    // 获取用户的所有会话列表（最新消息）
    @Query(value = "SELECT * FROM messages m1 WHERE m1.id IN " +
           "(SELECT MAX(m2.id) FROM messages m2 WHERE m2.sender_id = :userId OR m2.receiver_id = :userId " +
           "GROUP BY CASE WHEN m2.sender_id = :userId THEN m2.receiver_id ELSE m2.sender_id END) " +
           "ORDER BY m1.create_time DESC", nativeQuery = true)
    List<Message> findConversationList(@Param("userId") Long userId);
    
    // 获取未读消息数量
    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiverId = :userId AND m.isRead = false")
    Long countUnread(@Param("userId") Long userId);
    
    // 获取与某用户的未读消息数量
    @Query("SELECT COUNT(m) FROM Message m WHERE m.senderId = :senderId AND m.receiverId = :receiverId AND m.isRead = false")
    Long countUnreadFrom(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);
}
