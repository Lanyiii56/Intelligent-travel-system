package com.learningassistant.backend.modules.spot.controller;

import com.learningassistant.backend.modules.spot.model.Spot;
import com.learningassistant.backend.modules.spot.service.SpotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 景点控制器
 * 模块: spot (成员2)
 */
@RestController
@RequestMapping("/api/spots")
public class SpotController {

    @Autowired
    private SpotService spotService;

    @GetMapping
    public List<Spot> all() {
        return spotService.listAll();
    }

    @GetMapping("/{id}")
    public Spot get(@PathVariable Long id) {
        return spotService.getById(id);
    }

    @GetMapping("/region/{regionId}")
    public List<Spot> byRegion(@PathVariable Integer regionId) {
        return spotService.findByRegion(regionId);
    }

    @GetMapping("/filter")
    public List<Spot> filter(@RequestParam int age, @RequestParam int time) {
        return spotService.findSuitableSpots(age, time);
    }
    
    /**
     * 搜索景点（支持关键词、地区、年龄、时间、价格筛选）
     */
    @GetMapping("/search")
    public List<Spot> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer regionId,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) Integer time,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice
    ) {
        List<Spot> spots = spotService.listAll();
        
        // 关键词筛选
        if (keyword != null && !keyword.isEmpty()) {
            String kw = keyword.toLowerCase();
            spots = spots.stream()
                    .filter(s -> s.getName().toLowerCase().contains(kw) 
                            || (s.getDescription() != null && s.getDescription().toLowerCase().contains(kw)))
                    .toList();
        }
        
        // 地区筛选
        if (regionId != null) {
            spots = spots.stream()
                    .filter(s -> s.getRegionId() != null && s.getRegionId().equals(regionId))
                    .toList();
        }
        
        // 年龄筛选
        if (age != null) {
            spots = spots.stream()
                    .filter(s -> age >= s.getAgeMin() && age <= s.getAgeMax())
                    .toList();
        }
        
        // 时间筛选
        if (time != null) {
            spots = spots.stream()
                    .filter(s -> s.getPlayTime() <= time)
                    .toList();
        }
        
        // 价格筛选
        if (minPrice != null) {
            spots = spots.stream()
                    .filter(s -> s.getPriceMin() >= minPrice)
                    .toList();
        }
        if (maxPrice != null) {
            spots = spots.stream()
                    .filter(s -> s.getPriceMax() <= maxPrice)
                    .toList();
        }
        
        return spots;
    }
    
    /**
     * 获取热门景点
     */
    @GetMapping("/hot")
    public List<Spot> hot(@RequestParam(defaultValue = "10") int limit) {
        List<Spot> spots = spotService.listAll();
        // 按价格排序，取前N个作为热门
        return spots.stream()
                .sorted((a, b) -> Double.compare(b.getPriceMin(), a.getPriceMin()))
                .limit(limit)
                .toList();
    }
}
