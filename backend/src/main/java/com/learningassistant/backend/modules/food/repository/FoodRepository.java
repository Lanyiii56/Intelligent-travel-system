package com.learningassistant.backend.modules.food.repository;

import com.learningassistant.backend.modules.food.model.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * 美食数据访问层
 */
@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {

    /**
     * 根据地区ID查询美食
     */
    List<Food> findByRegionId(Long regionId);

    /**
     * 根据地区ID和类别查询美食
     */
    List<Food> findByRegionIdAndCategory(Long regionId, String category);

    /**
     * 根据地区ID查询美食，按评分排序
     */
    @Query("SELECT f FROM Food f WHERE f.regionId = :regionId ORDER BY f.rating DESC")
    List<Food> findByRegionIdOrderByRating(@Param("regionId") Long regionId);

    /**
     * 根据价格范围查询
     */
    @Query("SELECT f FROM Food f WHERE f.regionId = :regionId AND f.priceMax <= :maxPrice")
    List<Food> findByRegionIdAndPriceMax(@Param("regionId") Long regionId, @Param("maxPrice") BigDecimal maxPrice);
}
