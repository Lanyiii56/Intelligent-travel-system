package com.learningassistant.backend.modules.route.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 路线实体
 * 模块: route (路线规划)
 */
@Entity
@Table(name = "routes")
@Data
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    
    private String name;
    
    private String description;

    @Column(columnDefinition = "text")
    private String spotIds; // 景点ID列表，逗号分隔

    private Integer totalTime; // 总时长（分钟）
    
    private Double totalDistance; // 总距离（公里）
    
    private Double estimatedCost; // 预估费用

    private Integer peopleCount; // 人数
    
    private String status; // draft, published, archived

    private LocalDateTime createTime = LocalDateTime.now();
    
    private LocalDateTime updateTime = LocalDateTime.now();
}
