package com.learningassistant.backend.modules.recommend.service;

import com.learningassistant.backend.modules.recommend.model.RecommendLog;
import com.learningassistant.backend.modules.recommend.repository.RecommendLogRepository;
import com.learningassistant.backend.modules.spot.model.Spot;
import com.learningassistant.backend.modules.spot.repository.SpotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 基础推荐服务
 * 模块: recommend (成员3)
 */
@Service
public class RecommendService {

    @Autowired
    private SpotRepository spotRepository;

    @Autowired
    private RecommendLogRepository recommendLogRepository;

    /**
     * 基础推荐：根据年龄和时间筛选
     */
    public List<Spot> recommend(Long userId, Integer age, Integer timeLimit) {
        List<Spot> all = spotRepository.findAll();

        List<Spot> filtered = all.stream()
                .filter(s -> isAgeSuitable(s, age))
                .filter(s -> s.getPlayTime() != null && s.getPlayTime() <= timeLimit)
                .sorted(Comparator.comparing(Spot::getPlayTime))
                .limit(10)
                .toList();

        // 记录推荐日志
        filtered.forEach(spot -> {
            RecommendLog log = new RecommendLog();
            log.setUserId(userId);
            log.setSpotId(spot.getId());
            log.setReason("年龄适配 + 时间适配");
            recommendLogRepository.save(log);
        });

        return filtered;
    }

    /**
     * 获取用户推荐历史
     */
    public List<RecommendLog> getUserHistory(Long userId) {
        return recommendLogRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }

    /**
     * 获取热门景点（基于推荐次数）
     */
    public List<Spot> getPopularSpots(Integer limit) {
        // 统计每个景点被推荐的次数
        List<RecommendLog> allLogs = recommendLogRepository.findAll();
        
        Map<Long, Long> spotCountMap = allLogs.stream()
                .collect(Collectors.groupingBy(RecommendLog::getSpotId, Collectors.counting()));
        
        // 按推荐次数排序
        List<Long> popularSpotIds = spotCountMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .toList();
        
        // 如果没有推荐记录，返回随机景点
        if (popularSpotIds.isEmpty()) {
            return spotRepository.findAll().stream()
                    .limit(limit)
                    .toList();
        }
        
        // 获取景点详情
        List<Spot> result = new ArrayList<>();
        for (Long spotId : popularSpotIds) {
            spotRepository.findById(spotId).ifPresent(result::add);
        }
        
        return result;
    }

    /**
     * 检查年龄是否适合
     */
    private boolean isAgeSuitable(Spot spot, Integer age) {
        if (spot.getAgeMin() == null || spot.getAgeMax() == null) {
            return true;
        }
        return age >= spot.getAgeMin() && age <= spot.getAgeMax();
    }
}
