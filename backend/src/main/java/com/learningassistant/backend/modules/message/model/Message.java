package com.learningassistant.backend.modules.message.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 私信消息实体
 * 模块: message (社交模块)
 */
@Entity
@Table(name = "messages")
@Data
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 发送者ID
    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    // 接收者ID
    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    // 消息内容
    @Column(columnDefinition = "TEXT")
    private String content;

    // 消息类型: text, image, system
    @Column(length = 20)
    private String type = "text";

    // 是否已读
    @Column(name = "is_read")
    private Boolean isRead = false;

    // 创建时间
    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();
}
