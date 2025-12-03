package com.learningassistant.backend.modules.hotel.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 酒店订单实体
 */
@Entity
@Table(name = "hotel_orders")
@Data
public class HotelOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long hotelId;
    private Long roomId;

    private LocalDate checkInDate;   // 入住日期
    private LocalDate checkOutDate;  // 退房日期
    private Integer nights;          // 入住晚数
    
    private Integer roomCount;       // 房间数量
    private Integer guestCount;      // 入住人数
    
    private String guestName;        // 入住人姓名
    private String guestPhone;       // 入住人电话
    
    private Double totalPrice;       // 总价
    
    @Enumerated(EnumType.STRING)
    private OrderStatus status;      // 订单状态
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Column(columnDefinition = "text")
    private String remark;           // 备注
    
    // 外部订单信息
    private String externalPlatform;  // 外部平台名称: ctrip, meituan, qunar, booking
    private String externalOrderId;   // 外部订单号
    private String externalOrderUrl;  // 外部订单详情链接
    private String confirmationCode;  // 确认码
    
    public enum OrderStatus {
        PENDING,     // 待支付
        PAID,        // 已支付
        CONFIRMED,   // 已确认
        CHECKED_IN,  // 已入住
        COMPLETED,   // 已完成
        CANCELLED    // 已取消
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = OrderStatus.PENDING;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
