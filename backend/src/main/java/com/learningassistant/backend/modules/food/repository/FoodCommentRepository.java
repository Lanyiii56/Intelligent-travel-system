package com.learningassistant.backend.modules.food.repository;

import com.learningassistant.backend.modules.food.model.FoodComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    
    /**
     * 计算美食的平均评分（只计算顶级评论，不包括回复）
     */
    @Query("SELECT AVG(c.rating) FROM FoodComment c WHERE c.foodId = :foodId AND c.parentId IS NULL AND c.rating IS NOT NULL")
    Double calculateAverageRating(@Param("foodId") Long foodId);
    
    /**
     * 统计美食的评论数量（只计算顶级评论）
     */
    @Query("SELECT COUNT(c) FROM FoodComment c WHERE c.foodId = :foodId AND c.parentId IS NULL")
    Long countTopLevelComments(@Param("foodId") Long foodId);
}
