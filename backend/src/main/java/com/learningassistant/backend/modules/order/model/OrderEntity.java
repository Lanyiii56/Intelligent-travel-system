package com.learningassistant.backend.modules.order.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 订单实体
 * 模块: order (成员4)
 * 映射到 TravelAdmin 的 travel_order 表
 */
@Entity
@Table(name = "travel_order")
@Data
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long spotId;
    private BigDecimal amount;
    private String orderStatus = "PENDING"; // PENDING / PAID / CANCELLED
    private LocalDate visitDate;
    private Integer visitorCount = 1;
    private String contactName;
    private String contactPhone;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
}
