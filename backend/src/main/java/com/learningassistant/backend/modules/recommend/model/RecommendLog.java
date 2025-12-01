package com.learningassistant.backend.modules.recommend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 推荐日志实体
 * 模块: recommend (成员3)
 */
@Entity
@Table(name = "recommend_logs")
@Data
public class RecommendLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long spotId;

    @Column(columnDefinition = "text")
    private String reason;

    private LocalDateTime createTime = LocalDateTime.now();
}
