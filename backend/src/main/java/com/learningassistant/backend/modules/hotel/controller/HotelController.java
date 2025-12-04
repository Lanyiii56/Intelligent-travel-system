package com.learningassistant.backend.modules.hotel.controller;

import com.learningassistant.backend.modules.hotel.model.Hotel;
import com.learningassistant.backend.modules.hotel.model.HotelRoom;
import com.learningassistant.backend.modules.hotel.model.HotelOrder;
import com.learningassistant.backend.modules.hotel.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    @Autowired
    private HotelService hotelService;

    // ==================== 酒店查询 ====================

    @GetMapping
    public ResponseEntity<?> getAllHotels(
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer stars,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String sort
    ) {
        List<Hotel> hotels;
        
        // 关键词搜索优先
        if (keyword != null && !keyword.isEmpty()) {
            hotels = hotelService.searchHotels(keyword);
            // 如果有星级筛选，进一步过滤
            if (stars != null) {
                final Integer starFilter = stars;
                hotels = hotels.stream()
                    .filter(h -> h.getStars() != null && h.getStars().equals(starFilter))
                    .toList();
            }
        } 
        // 有地区限制
        else if (regionId != null) {
            if (stars != null) {
                // 地区 + 星级 + 排序
                if ("price".equals(sort)) {
                    hotels = hotelService.getHotelsByRegionAndStarsOrderByPrice(regionId, stars);
                } else if ("rating".equals(sort)) {
                    hotels = hotelService.getHotelsByRegionAndStarsOrderByRating(regionId, stars);
                } else {
                    hotels = hotelService.getHotelsByRegionAndStars(regionId, stars);
                }
            } else if (minPrice != null && maxPrice != null) {
                hotels = hotelService.getHotelsByRegionAndPriceRange(regionId, minPrice, maxPrice);
            } else if ("rating".equals(sort)) {
                hotels = hotelService.getHotelsByRegionOrderByRating(regionId);
            } else if ("price".equals(sort)) {
                hotels = hotelService.getHotelsByRegionOrderByPrice(regionId);
            } else {
                hotels = hotelService.getHotelsByRegion(regionId);
            }
        } 
        // 无地区限制
        else {
            if (stars != null) {
                // 星级 + 排序
                if ("price".equals(sort)) {
                    hotels = hotelService.getHotelsByStarsOrderByPrice(stars);
                } else if ("rating".equals(sort)) {
                    hotels = hotelService.getHotelsByStarsOrderByRating(stars);
                } else {
                    hotels = hotelService.getHotelsByStars(stars);
                }
            } else if ("price".equals(sort)) {
                hotels = hotelService.getAllHotelsOrderByPrice();
            } else if ("rating".equals(sort)) {
                hotels = hotelService.getAllHotelsOrderByRating();
            } else {
                hotels = hotelService.getAllHotels();
            }
        }
        
        return ResponseEntity.ok(hotels);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getHotelById(@PathVariable Long id) {
        return hotelService.getHotelById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nearby")
    public ResponseEntity<?> getNearbyHotels(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "5") Double radius
    ) {
        List<Hotel> hotels = hotelService.findNearbyHotels(latitude, longitude, radius);
        return ResponseEntity.ok(hotels);
    }

    // ==================== 房型查询 ====================

    @GetMapping("/{hotelId}/rooms")
    public ResponseEntity<?> getHotelRooms(
            @PathVariable Long hotelId,
            @RequestParam(defaultValue = "false") Boolean availableOnly
    ) {
        List<HotelRoom> rooms;
        if (availableOnly) {
            rooms = hotelService.getAvailableRooms(hotelId);
        } else {
            rooms = hotelService.getRoomsByHotelId(hotelId);
        }
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<?> getRoomById(@PathVariable Long roomId) {
        return hotelService.getRoomById(roomId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ==================== 订单管理 ====================

    @PostMapping("/orders")
    public ResponseEntity<?> createOrder(@RequestBody HotelOrder order) {
        try {
            HotelOrder created = hotelService.createOrder(order);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/orders/user/{userId}")
    public ResponseEntity<?> getUserOrders(@PathVariable Long userId) {
        List<HotelOrder> orders = hotelService.getUserOrders(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable Long orderId) {
        return hotelService.getOrderById(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/orders/{orderId}/pay")
    public ResponseEntity<?> payOrder(@PathVariable Long orderId) {
        HotelOrder order = hotelService.payOrder(orderId);
        if (order != null) {
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/orders/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId) {
        HotelOrder order = hotelService.cancelOrder(orderId);
        if (order != null) {
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.notFound().build();
    }

    // ==================== 外部预订回调 ====================

    /**
     * 创建外部预订记录
     * 用户在外部平台完成预订后，调用此接口记录订单信息
     */
    @PostMapping("/orders/external")
    public ResponseEntity<?> createExternalOrder(@RequestBody Map<String, Object> orderData) {
        try {
            HotelOrder order = hotelService.createExternalOrder(orderData);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 更新外部订单状态
     * 外部平台回调或用户手动更新
     */
    @PostMapping("/orders/{orderId}/external-update")
    public ResponseEntity<?> updateExternalOrder(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> updateData
    ) {
        try {
            HotelOrder order = hotelService.updateExternalOrderInfo(orderId, updateData);
            if (order != null) {
                return ResponseEntity.ok(order);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 获取酒店的外部预订链接
     */
    @GetMapping("/{hotelId}/booking-links")
    public ResponseEntity<?> getBookingLinks(
            @PathVariable Long hotelId,
            @RequestParam(required = false) String checkIn,
            @RequestParam(required = false) String checkOut
    ) {
        return hotelService.getHotelById(hotelId)
                .map(hotel -> {
                    Map<String, String> links = new java.util.HashMap<>();
                    if (hotel.getCtripUrl() != null) links.put("ctrip", hotel.getCtripUrl());
                    if (hotel.getMeituanUrl() != null) links.put("meituan", hotel.getMeituanUrl());
                    if (hotel.getQunarUrl() != null) links.put("qunar", hotel.getQunarUrl());
                    if (hotel.getBookingUrl() != null) links.put("booking", hotel.getBookingUrl());
                    
                    // 如果没有配置链接，生成默认搜索链接
                    if (links.isEmpty()) {
                        String hotelName = hotel.getName();
                        links.put("ctrip", "https://hotels.ctrip.com/hotels/list?keyword=" + hotelName);
                        links.put("meituan", "https://hotel.meituan.com/search?keyword=" + hotelName);
                        links.put("qunar", "https://hotel.qunar.com/city/hangzhou/q-" + hotelName);
                    }
                    
                    return ResponseEntity.ok(links);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
