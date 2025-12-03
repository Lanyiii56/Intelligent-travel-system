package com.learningassistant.backend.modules.hotel.repository;

import com.learningassistant.backend.modules.hotel.model.HotelRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HotelRoomRepository extends JpaRepository<HotelRoom, Long> {
    
    List<HotelRoom> findByHotelId(Long hotelId);
    
    // 查询有库存的房型
    @Query("SELECT r FROM HotelRoom r WHERE r.hotelId = :hotelId AND r.stock > 0")
    List<HotelRoom> findAvailableByHotelId(@Param("hotelId") Long hotelId);
    
    // 按价格排序
    List<HotelRoom> findByHotelIdOrderByPriceAsc(Long hotelId);
    
    // 按床型查询
    List<HotelRoom> findByHotelIdAndBedType(Long hotelId, Integer bedType);
}
