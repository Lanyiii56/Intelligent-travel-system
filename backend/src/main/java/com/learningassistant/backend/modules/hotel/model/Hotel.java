package com.learningassistant.backend.modules.hotel.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 酒店实体
 */
@Entity
@Table(name = "hotels")
@Data
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "text")
    private String description;

    private Integer regionId;
    
    private String address;

    private Double priceMin;  // 最低价格
    private Double priceMax;  // 最高价格

    private Double rating;    // 评分 (1-5)
    private Integer stars;    // 星级 (1-5)

    private Double latitude;
    private Double longitude;

    @Column(columnDefinition = "text")
    private String imageUrl;
    
    @Column(columnDefinition = "text")
    private String facilities;  // 设施，JSON格式存储
    
    private String phone;
    
    private String checkInTime;   // 入住时间
    private String checkOutTime;  // 退房时间
    
    // 外部预订平台链接
    private String ctripUrl;      // 携程链接
    private String meituanUrl;    // 美团链接
    private String qunarUrl;      // 去哪儿链接
    private String bookingUrl;    // Booking链接
}
