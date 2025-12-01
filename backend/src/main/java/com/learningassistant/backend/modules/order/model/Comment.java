package com.learningassistant.backend.modules.order.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 评论实体
 * 模块: order (成员4)
 */
@Entity
@Table(name = "comments")
@Data
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long spotId;

    @Column(columnDefinition = "text")
    private String content;

    private LocalDateTime createTime = LocalDateTime.now();
}
