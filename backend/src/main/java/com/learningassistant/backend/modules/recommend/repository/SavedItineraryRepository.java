package com.learningassistant.backend.modules.recommend.repository;

import com.learningassistant.backend.modules.recommend.model.SavedItinerary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 保存行程仓库
 * 模块: recommend (成员3)
 */
@Repository
public interface SavedItineraryRepository extends JpaRepository<SavedItinerary, Long> {
    
    List<SavedItinerary> findByUserIdOrderByCreateTimeDesc(Long userId);
}
