package com.learningassistant.backend.modules.hotel.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 酒店实体
 * 映射到 TravelAdmin 的 travel_hotel 表
 */
@Entity
@Table(name = "travel_hotel")
@Data
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "region_id")
    private Long regionId;
    
    private String address;

    @Column(name = "price_min")
    private BigDecimal priceMin;  // 最低价格

    @Column(name = "price_max")
    private BigDecimal priceMax;  // 最高价格

    @Column(columnDefinition = "DECIMAL(3,2) DEFAULT 0")
    private BigDecimal rating;
    
    @Column(name = "comment_count", columnDefinition = "INTEGER DEFAULT 0")
    private Integer commentCount = 0;
    
    private Integer stars;    // 星级 (1-5)

    private BigDecimal latitude;
    private BigDecimal longitude;

    @Column(name = "image_url", columnDefinition = "text")
    private String imageUrl;
    
    @Column(columnDefinition = "text")
    private String facilities;  // 设施，JSON格式存储
    
    private String phone;
    
    @Column(name = "check_in_time")
    private String checkInTime;   // 入住时间

    @Column(name = "check_out_time")
    private String checkOutTime;  // 退房时间
    
    // 外部预订平台链接
    @Column(name = "ctrip_url")
    private String ctripUrl;      // 携程链接

    @Column(name = "meituan_url")
    private String meituanUrl;    // 美团链接

    @Column(name = "qunar_url")
    private String qunarUrl;      // 去哪儿链接

    @Column(name = "booking_url")
    private String bookingUrl;    // Booking链接

    @Column(columnDefinition = "CHAR(1) DEFAULT '0'")
    private String status = "0";

    @Column(name = "create_time")
    private LocalDateTime createTime;
}
