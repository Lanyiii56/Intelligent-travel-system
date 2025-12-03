package com.learningassistant.backend.modules.auth.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户成就实体
 * 记录用户解锁的成就
 */
@Entity
@Table(name = "user_achievements", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "achievement_id"})
})
@Data
public class UserAchievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "achievement_id", nullable = false, length = 50)
    private String achievementId;

    @Column(name = "achievement_name", nullable = false, length = 100)
    private String achievementName;

    @Column(name = "achievement_icon", length = 50)
    private String achievementIcon;

    @Column(name = "unlocked_at")
    private LocalDateTime unlockedAt = LocalDateTime.now();
}
