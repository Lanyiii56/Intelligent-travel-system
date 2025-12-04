package com.learningassistant.backend.modules.food.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 美食实体
 * 模块: food (美食推荐)
 * 映射到 TravelAdmin 的 travel_food 表
 */
@Entity
@Table(name = "travel_food")
@Data
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "region_id")
    private Long regionId;

    private String category; // 美食类型：川菜、粤菜、小吃、火锅等

    @Column(name = "price_min")
    private BigDecimal priceMin;

    @Column(name = "price_max")
    private BigDecimal priceMax;

    private String address;

    @Column(name = "open_time")
    private String openTime;

    @Column(name = "dining_time")
    private Integer diningTime; // 用餐时长(分钟)

    @Column(columnDefinition = "DECIMAL(3,2) DEFAULT 0")
    private BigDecimal rating;

    @Column(name = "comment_count", columnDefinition = "INTEGER DEFAULT 0")
    private Integer commentCount = 0;

    private BigDecimal latitude;
    private BigDecimal longitude;

    @Column(name = "image_url", columnDefinition = "text")
    private String imageUrl;

    private String tags; // 标签，逗号分隔：网红店,老字号,必吃榜等

    @Column(columnDefinition = "CHAR(1) DEFAULT '0'")
    private String status = "0";

    @Column(name = "create_time")
    private LocalDateTime createTime;
}
