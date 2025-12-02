package com.learningassistant.backend.modules.food.controller;

import com.learningassistant.backend.modules.food.model.Food;
import com.learningassistant.backend.modules.food.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 美食控制器
 */
@RestController
@RequestMapping("/api/foods")
public class FoodController {

    @Autowired
    private FoodService foodService;

    /**
     * 获取所有美食
     */
    @GetMapping
    public ResponseEntity<List<Food>> getAll() {
        return ResponseEntity.ok(foodService.findAll());
    }

    /**
     * 根据ID获取美食详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Food> getById(@PathVariable Long id) {
        return foodService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 根据地区获取美食列表
     */
    @GetMapping("/region/{regionId}")
    public ResponseEntity<List<Food>> getByRegion(@PathVariable Integer regionId) {
        return ResponseEntity.ok(foodService.findByRegionIdOrderByRating(regionId));
    }

    /**
     * 根据地区和类别获取美食
     */
    @GetMapping("/region/{regionId}/category/{category}")
    public ResponseEntity<List<Food>> getByRegionAndCategory(
            @PathVariable Integer regionId,
            @PathVariable String category) {
        return ResponseEntity.ok(foodService.findByRegionIdAndCategory(regionId, category));
    }

    /**
     * 为行程推荐美食
     */
    @GetMapping("/recommend")
    public ResponseEntity<List<Food>> recommendForTrip(
            @RequestParam Integer regionId,
            @RequestParam(required = false) Double budget,
            @RequestParam(defaultValue = "3") Integer count) {
        return ResponseEntity.ok(foodService.recommendForTrip(regionId, budget, count));
    }
}
