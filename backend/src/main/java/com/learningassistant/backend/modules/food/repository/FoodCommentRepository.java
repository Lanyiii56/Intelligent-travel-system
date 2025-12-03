package com.learningassistant.backend.modules.food.repository;

import com.learningassistant.backend.modules.food.model.FoodComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 美食评论数据访问层
 */
@Repository
public interface FoodCommentRepository extends JpaRepository<FoodComment, Long> {
    
    /**
     * 根据美食ID获取评论列表，按时间倒序
     */
    List<FoodComment> findByFoodIdOrderByCreatedAtDesc(Long foodId);
    
    /**
     * 根据用户ID获取评论列表
     */
    List<FoodComment> findByUserId(Long userId);
    
    /**
     * 统计美食的评论数量
     */
    long countByFoodId(Long foodId);
    
    /**
     * 根据评分筛选评论
     */
    List<FoodComment> findByFoodIdAndRatingGreaterThanEqualOrderByCreatedAtDesc(Long foodId, Integer rating);
    
    /**
     * 根据评分筛选评论（差评）
     */
    List<FoodComment> findByFoodIdAndRatingLessThanEqualOrderByCreatedAtDesc(Long foodId, Integer rating);
    
    /**
     * 统计某评分的数量
     */
    long countByFoodIdAndRating(Long foodId, Integer rating);
    
    /**
     * 获取顶级评论（parentId 为 null）
     */
    List<FoodComment> findByFoodIdAndParentIdIsNullOrderByCreatedAtDesc(Long foodId);
    
    /**
     * 获取某评论的回复列表
     */
    List<FoodComment> findByParentIdOrderByCreatedAtAsc(Long parentId);
}
