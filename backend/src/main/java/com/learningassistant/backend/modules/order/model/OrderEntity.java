package com.learningassistant.backend.modules.order.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 订单实体
 * 模块: order (成员4)
 */
@Entity
@Table(name = "orders")
@Data
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long spotId;
    private Double amount;
    private String orderStatus; // pending / paid / cancelled
    private LocalDateTime createTime = LocalDateTime.now();
    private LocalDateTime payTime;
}
