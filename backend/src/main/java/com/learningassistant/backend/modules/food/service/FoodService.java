package com.learningassistant.backend.modules.food.service;

import com.learningassistant.backend.modules.food.model.Food;
import com.learningassistant.backend.modules.food.repository.FoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 美食服务层
 */
@Service
public class FoodService {

    @Autowired
    private FoodRepository foodRepository;

    /**
     * 获取所有美食
     */
    public List<Food> findAll() {
        return foodRepository.findAll();
    }

    /**
     * 根据ID获取美食
     */
    public Optional<Food> findById(Long id) {
        return foodRepository.findById(id);
    }

    /**
     * 根据地区获取美食
     */
    public List<Food> findByRegionId(Integer regionId) {
        return foodRepository.findByRegionId(regionId);
    }

    /**
     * 根据地区获取美食（按评分排序）
     */
    public List<Food> findByRegionIdOrderByRating(Integer regionId) {
        return foodRepository.findByRegionIdOrderByRating(regionId);
    }

    /**
     * 根据地区和类别获取美食
     */
    public List<Food> findByRegionIdAndCategory(Integer regionId, String category) {
        return foodRepository.findByRegionIdAndCategory(regionId, category);
    }

    /**
     * 根据地区和价格上限获取美食
     */
    public List<Food> findByRegionIdAndPriceMax(Integer regionId, Double maxPrice) {
        return foodRepository.findByRegionIdAndPriceMax(regionId, maxPrice);
    }

    /**
     * 为行程推荐美食
     * @param regionId 地区ID
     * @param budget 人均预算（可选）
     * @param count 推荐数量
     */
    public List<Food> recommendForTrip(Integer regionId, Double budget, int count) {
        List<Food> foods;
        if (budget != null && budget > 0) {
            foods = foodRepository.findByRegionIdAndPriceMax(regionId, budget);
        } else {
            foods = foodRepository.findByRegionIdOrderByRating(regionId);
        }
        
        // 限制返回数量
        if (foods.size() > count) {
            return foods.subList(0, count);
        }
        return foods;
    }

    /**
     * 保存美食
     */
    public Food save(Food food) {
        return foodRepository.save(food);
    }

    /**
     * 删除美食
     */
    public void deleteById(Long id) {
        foodRepository.deleteById(id);
    }
}
