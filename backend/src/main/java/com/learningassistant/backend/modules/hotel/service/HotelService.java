package com.learningassistant.backend.modules.hotel.service;

import com.learningassistant.backend.modules.hotel.model.Hotel;
import com.learningassistant.backend.modules.hotel.model.HotelRoom;
import com.learningassistant.backend.modules.hotel.model.HotelOrder;
import com.learningassistant.backend.modules.hotel.repository.HotelRepository;
import com.learningassistant.backend.modules.hotel.repository.HotelRoomRepository;
import com.learningassistant.backend.modules.hotel.repository.HotelOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class HotelService {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private HotelRoomRepository roomRepository;

    @Autowired
    private HotelOrderRepository orderRepository;

    // ==================== 酒店查询 ====================

    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
    }

    public Optional<Hotel> getHotelById(Long id) {
        return hotelRepository.findById(id);
    }

    public List<Hotel> getHotelsByRegion(Long regionId) {
        return hotelRepository.findByRegionId(regionId);
    }

    public List<Hotel> searchHotels(String keyword) {
        return hotelRepository.searchByKeyword(keyword);
    }

    public List<Hotel> getHotelsByRegionAndPriceRange(Long regionId, BigDecimal minPrice, BigDecimal maxPrice) {
        return hotelRepository.findByRegionAndPriceRange(regionId, minPrice, maxPrice);
    }

    public List<Hotel> getHotelsByRegionAndStars(Long regionId, Integer stars) {
        return hotelRepository.findByRegionIdAndStars(regionId, stars);
    }
    
    public List<Hotel> getHotelsByRegionAndStarsOrderByPrice(Long regionId, Integer stars) {
        return hotelRepository.findByRegionIdAndStarsOrderByPriceMinAsc(regionId, stars);
    }
    
    public List<Hotel> getHotelsByRegionAndStarsOrderByRating(Long regionId, Integer stars) {
        return hotelRepository.findByRegionIdAndStarsOrderByRatingDesc(regionId, stars);
    }
    
    public List<Hotel> getHotelsByStars(Integer stars) {
        return hotelRepository.findByStars(stars);
    }
    
    public List<Hotel> getHotelsByStarsOrderByPrice(Integer stars) {
        return hotelRepository.findByStarsOrderByPriceMinAsc(stars);
    }
    
    public List<Hotel> getHotelsByStarsOrderByRating(Integer stars) {
        return hotelRepository.findByStarsOrderByRatingDesc(stars);
    }

    public List<Hotel> getHotelsByRegionOrderByRating(Long regionId) {
        return hotelRepository.findByRegionIdOrderByRatingDesc(regionId);
    }

    public List<Hotel> getHotelsByRegionOrderByPrice(Long regionId) {
        return hotelRepository.findByRegionIdOrderByPriceMinAsc(regionId);
    }
    
    public List<Hotel> getAllHotelsOrderByPrice() {
        return hotelRepository.findAllByOrderByPriceMinAsc();
    }
    
    public List<Hotel> getAllHotelsOrderByRating() {
        return hotelRepository.findAllByOrderByRatingDesc();
    }

    /**
     * 查找附近酒店
     * @param latitude 中心纬度
     * @param longitude 中心经度
     * @param radiusKm 半径(公里)
     */
    public List<Hotel> findNearbyHotels(Double latitude, Double longitude, Double radiusKm) {
        // 简单计算：1度约等于111公里
        double latDelta = radiusKm / 111.0;
        double lngDelta = radiusKm / (111.0 * Math.cos(Math.toRadians(latitude)));
        
        return hotelRepository.findNearby(
            BigDecimal.valueOf(latitude - latDelta),
            BigDecimal.valueOf(latitude + latDelta),
            BigDecimal.valueOf(longitude - lngDelta),
            BigDecimal.valueOf(longitude + lngDelta)
        );
    }

    // ==================== 房型查询 ====================

    public List<HotelRoom> getRoomsByHotelId(Long hotelId) {
        return roomRepository.findByHotelId(hotelId);
    }

    public List<HotelRoom> getAvailableRooms(Long hotelId) {
        return roomRepository.findAvailableByHotelId(hotelId);
    }

    public Optional<HotelRoom> getRoomById(Long roomId) {
        return roomRepository.findById(roomId);
    }

    // ==================== 订单管理 ====================

    @Transactional
    public HotelOrder createOrder(HotelOrder order) {
        // 计算入住晚数
        if (order.getCheckInDate() != null && order.getCheckOutDate() != null) {
            long nights = ChronoUnit.DAYS.between(order.getCheckInDate(), order.getCheckOutDate());
            order.setNights((int) nights);
        }
        
        // 计算总价
        Optional<HotelRoom> roomOpt = roomRepository.findById(order.getRoomId());
        if (roomOpt.isPresent()) {
            HotelRoom room = roomOpt.get();
            double totalPrice = room.getPrice() * order.getNights() * order.getRoomCount();
            order.setTotalPrice(totalPrice);
            
            // 减少库存
            if (room.getStock() >= order.getRoomCount()) {
                room.setStock(room.getStock() - order.getRoomCount());
                roomRepository.save(room);
            } else {
                throw new RuntimeException("房间库存不足");
            }
        }
        
        return orderRepository.save(order);
    }

    public List<HotelOrder> getUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Optional<HotelOrder> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    @Transactional
    public HotelOrder updateOrderStatus(Long orderId, HotelOrder.OrderStatus status) {
        Optional<HotelOrder> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            HotelOrder order = orderOpt.get();
            
            // 如果取消订单，恢复库存
            if (status == HotelOrder.OrderStatus.CANCELLED && 
                order.getStatus() != HotelOrder.OrderStatus.CANCELLED) {
                Optional<HotelRoom> roomOpt = roomRepository.findById(order.getRoomId());
                if (roomOpt.isPresent()) {
                    HotelRoom room = roomOpt.get();
                    room.setStock(room.getStock() + order.getRoomCount());
                    roomRepository.save(room);
                }
            }
            
            order.setStatus(status);
            return orderRepository.save(order);
        }
        return null;
    }

    @Transactional
    public HotelOrder payOrder(Long orderId) {
        return updateOrderStatus(orderId, HotelOrder.OrderStatus.PAID);
    }

    @Transactional
    public HotelOrder cancelOrder(Long orderId) {
        return updateOrderStatus(orderId, HotelOrder.OrderStatus.CANCELLED);
    }

    // ==================== 外部订单管理 ====================

    /**
     * 创建外部预订记录
     */
    @Transactional
    public HotelOrder createExternalOrder(Map<String, Object> orderData) {
        HotelOrder order = new HotelOrder();
        
        order.setUserId(Long.valueOf(orderData.get("userId").toString()));
        order.setHotelId(Long.valueOf(orderData.get("hotelId").toString()));
        
        // 房型ID可选（外部预订可能没有对应的房型）
        if (orderData.get("roomId") != null) {
            order.setRoomId(Long.valueOf(orderData.get("roomId").toString()));
        }
        
        // 日期信息
        if (orderData.get("checkInDate") != null) {
            order.setCheckInDate(java.time.LocalDate.parse(orderData.get("checkInDate").toString()));
        }
        if (orderData.get("checkOutDate") != null) {
            order.setCheckOutDate(java.time.LocalDate.parse(orderData.get("checkOutDate").toString()));
        }
        if (order.getCheckInDate() != null && order.getCheckOutDate() != null) {
            long nights = java.time.temporal.ChronoUnit.DAYS.between(order.getCheckInDate(), order.getCheckOutDate());
            order.setNights((int) nights);
        }
        
        // 入住信息
        order.setRoomCount(orderData.get("roomCount") != null ? 
            Integer.valueOf(orderData.get("roomCount").toString()) : 1);
        order.setGuestCount(orderData.get("guestCount") != null ? 
            Integer.valueOf(orderData.get("guestCount").toString()) : 2);
        order.setGuestName(orderData.get("guestName") != null ? 
            orderData.get("guestName").toString() : "");
        order.setGuestPhone(orderData.get("guestPhone") != null ? 
            orderData.get("guestPhone").toString() : "");
        
        // 价格
        if (orderData.get("totalPrice") != null) {
            order.setTotalPrice(Double.valueOf(orderData.get("totalPrice").toString()));
        }
        
        // 外部订单信息
        order.setExternalPlatform(orderData.get("platform") != null ? 
            orderData.get("platform").toString() : null);
        order.setExternalOrderId(orderData.get("externalOrderId") != null ? 
            orderData.get("externalOrderId").toString() : null);
        order.setExternalOrderUrl(orderData.get("externalOrderUrl") != null ? 
            orderData.get("externalOrderUrl").toString() : null);
        order.setConfirmationCode(orderData.get("confirmationCode") != null ? 
            orderData.get("confirmationCode").toString() : null);
        
        // 外部订单默认已支付状态
        order.setStatus(HotelOrder.OrderStatus.PAID);
        
        order.setRemark(orderData.get("remark") != null ? 
            orderData.get("remark").toString() : "外部平台预订");
        
        return orderRepository.save(order);
    }

    /**
     * 更新外部订单信息
     */
    @Transactional
    public HotelOrder updateExternalOrderInfo(Long orderId, Map<String, String> updateData) {
        Optional<HotelOrder> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            HotelOrder order = orderOpt.get();
            
            if (updateData.containsKey("externalOrderId")) {
                order.setExternalOrderId(updateData.get("externalOrderId"));
            }
            if (updateData.containsKey("externalOrderUrl")) {
                order.setExternalOrderUrl(updateData.get("externalOrderUrl"));
            }
            if (updateData.containsKey("confirmationCode")) {
                order.setConfirmationCode(updateData.get("confirmationCode"));
            }
            if (updateData.containsKey("status")) {
                order.setStatus(HotelOrder.OrderStatus.valueOf(updateData.get("status")));
            }
            
            return orderRepository.save(order);
        }
        return null;
    }
}
