package com.learningassistant.backend.modules.recommend.service;

import com.learningassistant.backend.modules.recommend.model.RecommendLog;
import com.learningassistant.backend.modules.recommend.repository.RecommendLogRepository;
import com.learningassistant.backend.modules.spot.model.Spot;
import com.learningassistant.backend.modules.spot.repository.SpotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<Spot> recommend(Long userId, Integer age, Integer timeLimit) {
        List<Spot> all = spotRepository.findAll();

        List<Spot> filtered = all.stream()
                .filter(s -> age >= s.getAgeMin() && age <= s.getAgeMax())
                .filter(s -> s.getPlayTime() <= timeLimit)
                .toList();

        filtered.forEach(spot -> {
            RecommendLog log = new RecommendLog();
            log.setUserId(userId);
            log.setSpotId(spot.getId());
            log.setReason("年龄适配 + 时间适配");
            recommendLogRepository.save(log);
        });

        return filtered;
    }

    public List<RecommendLog> getUserHistory(Long userId) {
        return recommendLogRepository.findByUserId(userId);
    }
}
