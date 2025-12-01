package com.learningassistant.backend.modules.order.repository;

import com.learningassistant.backend.modules.order.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * 评论数据访问
 * 模块: order (成员4)
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findBySpotId(Long spotId);
}
