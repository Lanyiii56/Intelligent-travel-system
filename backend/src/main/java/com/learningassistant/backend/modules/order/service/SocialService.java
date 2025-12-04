package com.learningassistant.backend.modules.order.service;

import com.learningassistant.backend.modules.order.model.Comment;
import com.learningassistant.backend.modules.order.model.Favorite;
import com.learningassistant.backend.modules.order.repository.CommentRepository;
import com.learningassistant.backend.modules.order.repository.FavoriteRepository;
import com.learningassistant.backend.modules.spot.model.Spot;
import com.learningassistant.backend.modules.spot.repository.SpotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 社交服务 (评论、收藏)
 * 模块: order (成员4)
 */
@Service
public class SocialService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private SpotRepository spotRepository;

    public Favorite toggleFavorite(Long userId, Long spotId) {
        Favorite exists = favoriteRepository.findByUserIdAndSpotId(userId, spotId);
        if (exists != null) {
            favoriteRepository.delete(exists);
            return null;
        }
        Favorite f = new Favorite();
        f.setUserId(userId);
        f.setSpotId(spotId);
        return favoriteRepository.save(f);
    }

    public List<Favorite> listFavorites(Long userId) {
        return favoriteRepository.findByUserId(userId);
    }

    public Comment addComment(Long userId, Long spotId, String content, Integer rating, String userName) {
        Comment c = new Comment();
        c.setUserId(userId);
        c.setSpotId(spotId);
        c.setContent(content);
        c.setRating(rating != null ? rating : 5);
        c.setUserName(userName);
        Comment saved = commentRepository.save(c);
        
        // 更新景点的评分和评论数
        updateSpotRating(spotId);
        
        return saved;
    }
    
    /**
     * 更新景点的评分和评论数
     */
    private void updateSpotRating(Long spotId) {
        Spot spot = spotRepository.findById(spotId).orElse(null);
        if (spot != null) {
            // 计算平均评分
            Double avgRating = commentRepository.calculateAverageRating(spotId);
            spot.setRating(avgRating != null ? BigDecimal.valueOf(avgRating).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
            
            // 统计评论数量
            Long count = commentRepository.countBySpotIdAndParentIdIsNull(spotId);
            spot.setCommentCount(count != null ? count.intValue() : 0);
            
            spotRepository.save(spot);
        }
    }

    public List<Comment> listComments(Long spotId) {
        // 只返回顶级评论（parentId 为 null）
        List<Comment> topComments = commentRepository.findBySpotIdAndParentIdIsNullOrderByCreatedAtDesc(spotId);
        // 为每条评论加载回复
        for (Comment comment : topComments) {
            List<Comment> replies = commentRepository.findByParentIdOrderByCreatedAtAsc(comment.getId());
            comment.setReplies(replies);
        }
        return topComments;
    }
    
    /**
     * 点赞评论
     */
    public Comment likeComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment != null) {
            comment.setLikes(comment.getLikes() + 1);
            return commentRepository.save(comment);
        }
        return null;
    }
    
    /**
     * 回复评论
     */
    public Comment replyComment(Long parentId, Long userId, Long spotId, String content, String userName) {
        Comment parent = commentRepository.findById(parentId).orElse(null);
        if (parent == null) return null;
        
        Comment reply = new Comment();
        reply.setUserId(userId);
        reply.setUserName(userName);
        reply.setSpotId(spotId);
        reply.setContent(content);
        reply.setParentId(parentId);
        reply.setReplyToUserId(parent.getUserId());
        reply.setReplyToUserName(parent.getUserName()); // 保存被回复者的用户名
        reply.setRating(null); // 回复不需要评分
        return commentRepository.save(reply);
    }
    
    /**
     * 获取评论的回复列表
     */
    public List<Comment> getReplies(Long parentId) {
        return commentRepository.findByParentIdOrderByCreatedAtAsc(parentId);
    }
}
