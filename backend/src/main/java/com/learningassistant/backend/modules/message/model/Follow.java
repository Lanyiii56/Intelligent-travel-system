package com.learningassistant.backend.modules.message.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户关注关系实体
 * 模块: message (社交模块)
 */
@Entity
@Table(name = "follows", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"follower_id", "following_id"})
})
@Data
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 关注者ID (粉丝)
    @Column(name = "follower_id", nullable = false)
    private Long followerId;

    // 被关注者ID
    @Column(name = "following_id", nullable = false)
    private Long followingId;

    // 创建时间
    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();
}
