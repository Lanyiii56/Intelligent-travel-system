package com.learningassistant.backend.modules.order.service;

import com.learningassistant.backend.modules.order.model.Comment;
import com.learningassistant.backend.modules.order.model.Favorite;
import com.learningassistant.backend.modules.order.repository.CommentRepository;
import com.learningassistant.backend.modules.order.repository.FavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Comment addComment(Long userId, Long spotId, String content) {
        Comment c = new Comment();
        c.setUserId(userId);
        c.setSpotId(spotId);
        c.setContent(content);
        return commentRepository.save(c);
    }

    public List<Comment> listComments(Long spotId) {
        return commentRepository.findBySpotId(spotId);
    }
}
