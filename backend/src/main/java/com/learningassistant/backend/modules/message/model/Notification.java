package com.learningassistant.backend.modules.message.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 系统通知实体
 * 模块: message (社交模块)
 * 类型: like(点赞), follow(关注), comment(评论), system(系统)
 */
@Entity
@Table(name = "notifications")
@Data
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 接收者ID
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 触发者ID (系统通知为空)
    @Column(name = "from_user_id")
    private Long fromUserId;

    // 通知类型: like, follow, comment, system
    @Column(length = 20, nullable = false)
    private String type;

    // 通知标题
    private String title;

    // 通知内容
    @Column(columnDefinition = "TEXT")
    private String content;

    // 关联的资源ID (如景点ID、评论ID等)
    @Column(name = "target_id")
    private Long targetId;

    // 关联的资源类型: spot, comment, food
    @Column(name = "target_type", length = 20)
    private String targetType;

    // 是否已读
    @Column(name = "is_read")
    private Boolean isRead = false;

    // 创建时间
    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();
}
