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
    public List<Spot> byRegion(@PathVariable Long regionId) {
        return spotService.findByRegion(regionId);
    }
    
    /**
     * 搜索景点（支持关键词、地区筛选）
     */
    @GetMapping("/search")
    public List<Spot> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long regionId
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
        
        return spots;
    }
    
    /**
     * 获取热门景点
     */
    @GetMapping("/hot")
    public List<Spot> hot(@RequestParam(defaultValue = "10") int limit) {
        List<Spot> spots = spotService.listAll();
        // 按评分排序，取前N个作为热门
        return spots.stream()
                .sorted((a, b) -> {
                    if (a.getRating() == null) return 1;
                    if (b.getRating() == null) return -1;
                    return b.getRating().compareTo(a.getRating());
                })
                .limit(limit)
                .toList();
    }
}
