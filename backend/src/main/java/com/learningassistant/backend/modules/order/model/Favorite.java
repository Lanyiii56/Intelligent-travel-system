package com.learningassistant.backend.modules.order.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 收藏实体
 * 模块: order (成员4)
 */
@Entity
@Table(name = "favorites")
@Data
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long spotId;
    private LocalDateTime createTime = LocalDateTime.now();
}
