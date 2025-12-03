package com.learningassistant.backend.modules.hotel.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 酒店房型实体
 */
@Entity
@Table(name = "hotel_rooms")
@Data
public class HotelRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long hotelId;

    private String name;          // 房型名称，如"标准双床房"
    
    @Column(columnDefinition = "text")
    private String description;

    private Double price;         // 每晚价格
    
    private Integer bedType;      // 床型: 1-大床, 2-双床, 3-单床
    private Integer maxGuests;    // 最大入住人数
    private Double area;          // 房间面积(平方米)
    
    private Boolean hasWindow;    // 是否有窗
    private Boolean hasBreakfast; // 是否含早餐
    private Boolean canCancel;    // 是否可取消
    
    private Integer stock;        // 库存数量
    
    @Column(columnDefinition = "text")
    private String imageUrl;
    
    @Column(columnDefinition = "text")
    private String facilities;    // 房间设施，JSON格式
}
