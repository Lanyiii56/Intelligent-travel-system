package com.learningassistant.backend.modules.hotel.repository;

import com.learningassistant.backend.modules.hotel.model.HotelOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelOrderRepository extends JpaRepository<HotelOrder, Long> {
    
    List<HotelOrder> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<HotelOrder> findByHotelId(Long hotelId);
    
    List<HotelOrder> findByUserIdAndStatus(Long userId, HotelOrder.OrderStatus status);
}
