package com.learningassistant.backend.modules.recommend.controller;

import com.learningassistant.backend.modules.recommend.dto.RecommendRequest;
import com.learningassistant.backend.modules.recommend.dto.RecommendResponse;
import com.learningassistant.backend.modules.recommend.model.RecommendLog;
import com.learningassistant.backend.modules.recommend.model.SavedItinerary;
import com.learningassistant.backend.modules.recommend.service.RecommendService;
import com.learningassistant.backend.modules.recommend.service.SmartRecommendService;
import com.learningassistant.backend.modules.spot.model.Spot;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 推荐控制器
 * 模块: recommend (成员3)
 */
@RestController
@RequestMapping("/api/recommend")
public class RecommendController {

    @Autowired
    private RecommendService recommendService;

    @Autowired
    private SmartRecommendService smartRecommendService;

    /**
     * 智能推荐接口
     * POST /api/recommend/smart
     */
    @PostMapping("/smart")
    public ResponseEntity<RecommendResponse> smartRecommend(@Valid @RequestBody RecommendRequest request) {
        RecommendResponse response = smartRecommendService.recommend(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 简单推荐接口（兼容旧版）
     * GET /api/recommend?userId=1&age=25&time=480
     */
    @GetMapping
    public ResponseEntity<List<Spot>> recommend(
            @RequestParam Long userId,
            @RequestParam Integer age,
            @RequestParam Integer time) {
        List<Spot> spots = recommendService.recommend(userId, age, time);
        return ResponseEntity.ok(spots);
    }

    /**
     * 获取用户推荐历史
     * GET /api/recommend/history/{userId}
     */
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<RecommendLog>> getHistory(@PathVariable Long userId) {
        List<RecommendLog> history = recommendService.getUserHistory(userId);
        return ResponseEntity.ok(history);
    }

    /**
     * 保存行程
     * POST /api/recommend/itinerary/save
     */
    @PostMapping("/itinerary/save")
    public ResponseEntity<?> saveItinerary(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        String name = (String) request.get("name");
        String itineraryData = (String) request.get("itineraryData");
        
        SavedItinerary saved = smartRecommendService.saveItinerary(userId, name, itineraryData);
        return ResponseEntity.ok(Map.of("message", "行程保存成功", "id", saved.getId()));
    }

    /**
     * 获取用户保存的行程列表
     * GET /api/recommend/itinerary/{userId}
     */
    @GetMapping("/itinerary/{userId}")
    public ResponseEntity<List<SavedItinerary>> getUserItineraries(@PathVariable Long userId) {
        List<SavedItinerary> itineraries = smartRecommendService.getUserItineraries(userId);
        return ResponseEntity.ok(itineraries);
    }

    /**
     * 删除保存的行程
     * DELETE /api/recommend/itinerary/{id}
     */
    @DeleteMapping("/itinerary/{id}")
    public ResponseEntity<?> deleteItinerary(@PathVariable Long id) {
        smartRecommendService.deleteItinerary(id);
        return ResponseEntity.ok(Map.of("message", "行程删除成功"));
    }

    /**
     * 获取热门推荐（基于推荐次数）
     * GET /api/recommend/popular
     */
    @GetMapping("/popular")
    public ResponseEntity<List<Spot>> getPopularSpots(@RequestParam(defaultValue = "10") Integer limit) {
        List<Spot> popularSpots = recommendService.getPopularSpots(limit);
        return ResponseEntity.ok(popularSpots);
    }
}
