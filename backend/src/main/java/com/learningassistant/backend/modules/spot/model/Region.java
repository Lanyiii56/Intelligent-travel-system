package com.learningassistant.backend.modules.spot.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 地区实体
 * 模块: spot (成员2)
 * 映射到 TravelAdmin 的 travel_region 表
 */
@Entity
@Table(name = "travel_region")
@Data
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(columnDefinition = "CHAR(1) DEFAULT '0'")
    private String status = "0";

    @Column(name = "create_time")
    private LocalDateTime createTime;
}
