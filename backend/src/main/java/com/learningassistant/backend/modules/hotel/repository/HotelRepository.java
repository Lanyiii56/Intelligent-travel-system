package com.learningassistant.backend.modules.hotel.repository;

import com.learningassistant.backend.modules.hotel.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
    
    List<Hotel> findByRegionId(Integer regionId);
    
    // 按名称模糊搜索
    @Query("SELECT h FROM Hotel h WHERE h.name LIKE %:keyword% OR h.address LIKE %:keyword%")
    List<Hotel> searchByKeyword(@Param("keyword") String keyword);
    
    // 按区域和价格范围查询
    @Query("SELECT h FROM Hotel h WHERE h.regionId = :regionId AND h.priceMin >= :minPrice AND h.priceMax <= :maxPrice")
    List<Hotel> findByRegionAndPriceRange(
        @Param("regionId") Integer regionId,
        @Param("minPrice") Double minPrice,
        @Param("maxPrice") Double maxPrice
    );
    
    // 按星级查询
    List<Hotel> findByRegionIdAndStars(Integer regionId, Integer stars);
    
    // 按星级查询（不限地区）
    List<Hotel> findByStars(Integer stars);
    
    // 按星级查询并按价格排序
    List<Hotel> findByStarsOrderByPriceMinAsc(Integer stars);
    
    // 按星级查询并按评分排序
    List<Hotel> findByStarsOrderByRatingDesc(Integer stars);
    
    // 按地区和星级查询并按价格排序
    List<Hotel> findByRegionIdAndStarsOrderByPriceMinAsc(Integer regionId, Integer stars);
    
    // 按地区和星级查询并按评分排序
    List<Hotel> findByRegionIdAndStarsOrderByRatingDesc(Integer regionId, Integer stars);
    
    // 按评分排序
    List<Hotel> findByRegionIdOrderByRatingDesc(Integer regionId);
    
    // 按价格排序
    List<Hotel> findByRegionIdOrderByPriceMinAsc(Integer regionId);
    
    // 全部酒店按价格排序
    List<Hotel> findAllByOrderByPriceMinAsc();
    
    // 全部酒店按评分排序
    List<Hotel> findAllByOrderByRatingDesc();
    
    // 查找附近酒店（基于经纬度）
    @Query("SELECT h FROM Hotel h WHERE " +
           "h.latitude BETWEEN :minLat AND :maxLat AND " +
           "h.longitude BETWEEN :minLng AND :maxLng")
    List<Hotel> findNearby(
        @Param("minLat") Double minLat,
        @Param("maxLat") Double maxLat,
        @Param("minLng") Double minLng,
        @Param("maxLng") Double maxLng
    );
}
