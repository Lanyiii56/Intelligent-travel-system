package com.learningassistant.backend.modules.order.controller;

import com.learningassistant.backend.modules.order.model.Comment;
import com.learningassistant.backend.modules.order.model.Favorite;
import com.learningassistant.backend.modules.order.service.SocialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 社交控制器 (评论、收藏)
 * 模块: order (成员4)
 */
@RestController
@RequestMapping("/api/social")
public class SocialController {

    @Autowired
    private SocialService socialService;

    @PostMapping("/favorite")
    public Favorite fav(@RequestParam Long userId, @RequestParam Long spotId) {
        return socialService.toggleFavorite(userId, spotId);
    }

    @GetMapping("/favorite/{userId}")
    public List<Favorite> favList(@PathVariable Long userId) {
        return socialService.listFavorites(userId);
    }

    @PostMapping("/comment")
    public Comment comment(
            @RequestParam Long userId, 
            @RequestParam Long spotId, 
            @RequestParam String content,
            @RequestParam(defaultValue = "5") Integer rating,
            @RequestParam(required = false) String userName
    ) {
        return socialService.addComment(userId, spotId, content, rating, userName);
    }

    @GetMapping("/comment/{spotId}")
    public List<Comment> comments(@PathVariable Long spotId) {
        return socialService.listComments(spotId);
    }
    
    /**
     * 点赞评论
     */
    @PostMapping("/comment/{commentId}/like")
    public Comment likeComment(@PathVariable Long commentId) {
        return socialService.likeComment(commentId);
    }
    
    /**
     * 回复评论
     */
    @PostMapping("/comment/{parentId}/reply")
    public Comment replyComment(
            @PathVariable Long parentId,
            @RequestParam Long userId,
            @RequestParam Long spotId,
            @RequestParam String content,
            @RequestParam(required = false) String userName
    ) {
        return socialService.replyComment(parentId, userId, spotId, content, userName);
    }
}
