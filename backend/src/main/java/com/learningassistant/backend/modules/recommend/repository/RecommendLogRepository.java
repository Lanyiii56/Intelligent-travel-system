package com.learningassistant.backend.modules.recommend.repository;

import com.learningassistant.backend.modules.recommend.model.RecommendLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * 推荐日志数据访问
 * 模块: recommend (成员3)
 */
public interface RecommendLogRepository extends JpaRepository<RecommendLog, Long> {
    List<RecommendLog> findByUserId(Long userId);
    
    List<RecommendLog> findByUserIdOrderByCreateTimeDesc(Long userId);
}
