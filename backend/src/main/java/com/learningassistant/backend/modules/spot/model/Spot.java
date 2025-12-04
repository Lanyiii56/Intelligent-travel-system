package com.learningassistant.backend.modules.spot.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 景点实体
 * 模块: spot (成员2)
 * 映射到 TravelAdmin 的 travel_spot 表
 */
@Entity
@Table(name = "travel_spot")
@Data
public class Spot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "region_id")
    private Long regionId;

    private String address;

    @Column(name = "ticket_price")
    private BigDecimal ticketPrice;

    @Column(name = "open_time")
    private String openTime;

    @Column(name = "close_time")
    private String closeTime;

    @Column(name = "recommended_duration")
    private Integer recommendedDuration; // 游玩时长(分钟)

    private BigDecimal latitude;
    private BigDecimal longitude;

    @Column(name = "image_url", columnDefinition = "text")
    private String imageUrl;

    private String tags;

    @Column(columnDefinition = "DECIMAL(3,2) DEFAULT 0")
    private BigDecimal rating;

    @Column(name = "comment_count", columnDefinition = "INTEGER DEFAULT 0")
    private Integer commentCount = 0;

    @Column(columnDefinition = "CHAR(1) DEFAULT '0'")
    private String status = "0";

    @Column(name = "create_time")
    private LocalDateTime createTime;
}
