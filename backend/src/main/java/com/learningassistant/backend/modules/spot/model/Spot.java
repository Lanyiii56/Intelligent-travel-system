package com.learningassistant.backend.modules.spot.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 景点实体
 * 模块: spot (成员2)
 */
@Entity
@Table(name = "spots")
@Data
public class Spot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "text")
    private String description;

    private Integer regionId;

    private Double priceMin;
    private Double priceMax;

    private String openTime;
    private Integer playTime; // 游玩时长(分钟)

    private Integer ageMin;
    private Integer ageMax;

    private Double latitude;
    private Double longitude;

    @Column(columnDefinition = "text")
    private String imageUrl;
    
    // 评分（根据用户评论动态计算）
    @Column(columnDefinition = "DOUBLE PRECISION DEFAULT 0")
    private Double rating = 0.0;
    
    // 评论数量
    @Column(columnDefinition = "INTEGER DEFAULT 0")
    private Integer commentCount = 0;
}
