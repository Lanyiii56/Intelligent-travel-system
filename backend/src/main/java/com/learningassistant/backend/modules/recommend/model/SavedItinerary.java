package com.learningassistant.backend.modules.recommend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 保存的行程实体
 * 模块: recommend (成员3)
 */
@Entity
@Table(name = "saved_itineraries")
@Data
public class SavedItinerary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    
    private String name;

    @Column(columnDefinition = "text")
    private String itineraryData; // JSON格式存储行程数据

    private LocalDateTime createTime = LocalDateTime.now();
    
    private LocalDateTime updateTime = LocalDateTime.now();
}
