package com.learningassistant.backend.modules.food.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 美食评论实体
 */
@Entity
@Table(name = "food_comments")
@Data
public class FoodComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "food_id", nullable = false)
    private Long foodId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_name")
    private String userName;

    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "INTEGER DEFAULT 0")
    private Integer likes = 0;
    
    // 父评论ID（用于回复功能）
    @Column(name = "parent_id")
    private Long parentId;
    
    // 回复的用户ID
    @Column(name = "reply_to_user_id")
    private Long replyToUserId;
    
    // 回复的用户名
    @Column(name = "reply_to_user")
    private String replyToUser;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    // 回复列表（非持久化字段，用于返回给前端）
    @Transient
    private List<FoodComment> replies;
}
