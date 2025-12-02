package com.learningassistant.backend.modules.food.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 美食实体
 * 模块: food (美食推荐)
 */
@Entity
@Table(name = "foods")
@Data
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "text")
    private String description;

    private Integer regionId;

    private String category; // 美食类型：川菜、粤菜、小吃、火锅等

    private Double priceMin;
    private Double priceMax;

    private String address;

    private String openTime;

    private Integer diningTime; // 用餐时长(分钟)

    private Double rating; // 评分

    private Double latitude;
    private Double longitude;

    @Column(columnDefinition = "text")
    private String imageUrl;

    private String tags; // 标签，逗号分隔：网红店,老字号,必吃榜等
}
