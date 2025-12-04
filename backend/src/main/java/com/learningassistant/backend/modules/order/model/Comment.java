package com.learningassistant.backend.modules.order.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论实体
 * 模块: order (成员4)
 */
@Entity
@Table(name = "travel_comment")
@Data
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String userName;
    private Long spotId;

    @Column(columnDefinition = "text")
    private String content;
    
    @Column(columnDefinition = "INTEGER DEFAULT 5")
    private Integer rating = 5;
    
    @Column(columnDefinition = "INTEGER DEFAULT 0")
    private Integer likes = 0;
    
    // 父评论ID（用于回复功能）
    private Long parentId;
    
    // 回复的用户ID
    private Long replyToUserId;
    
    // 回复的用户名
    private String replyToUserName;

    private LocalDateTime createdAt = LocalDateTime.now();
    
    // 回复列表（非持久化字段，用于返回给前端）
    @Transient
    private List<Comment> replies;
}
