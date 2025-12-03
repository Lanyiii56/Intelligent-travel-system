package com.learningassistant.backend.modules.order.repository;

import com.learningassistant.backend.modules.order.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

/**
 * 评论数据访问
 * 模块: order (成员4)
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findBySpotId(Long spotId);
    
    // 获取顶级评论（按时间倒序）
    List<Comment> findBySpotIdAndParentIdIsNullOrderByCreatedAtDesc(Long spotId);
    
    // 获取某评论的回复（按时间正序）
    List<Comment> findByParentIdOrderByCreatedAtAsc(Long parentId);
    
    // 计算景点的平均评分（只计算顶级评论，不包括回复）
    @Query("SELECT AVG(c.rating) FROM Comment c WHERE c.spotId = :spotId AND c.parentId IS NULL AND c.rating IS NOT NULL")
    Double calculateAverageRating(@Param("spotId") Long spotId);
    
    // 统计景点的评论数量（只计算顶级评论）
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.spotId = :spotId AND c.parentId IS NULL")
    Long countBySpotIdAndParentIdIsNull(@Param("spotId") Long spotId);
}
