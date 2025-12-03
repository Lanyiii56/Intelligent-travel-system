package com.learningassistant.backend.modules.food.controller;

import com.learningassistant.backend.modules.food.model.Food;
import com.learningassistant.backend.modules.food.model.FoodComment;
import com.learningassistant.backend.modules.food.service.FoodService;
import com.learningassistant.backend.modules.food.repository.FoodCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 美食控制器
 */
@RestController
@RequestMapping("/api/foods")
public class FoodController {

    @Autowired
    private FoodService foodService;
    
    @Autowired
    private FoodCommentRepository foodCommentRepository;

    /**
     * 获取所有美食
     */
    @GetMapping
    public ResponseEntity<List<Food>> getAll() {
        return ResponseEntity.ok(foodService.findAll());
    }

    /**
     * 根据ID获取美食详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Food> getById(@PathVariable Long id) {
        return foodService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 根据地区获取美食列表
     */
    @GetMapping("/region/{regionId}")
    public ResponseEntity<List<Food>> getByRegion(@PathVariable Integer regionId) {
        return ResponseEntity.ok(foodService.findByRegionIdOrderByRating(regionId));
    }

    /**
     * 根据地区和类别获取美食
     */
    @GetMapping("/region/{regionId}/category/{category}")
    public ResponseEntity<List<Food>> getByRegionAndCategory(
            @PathVariable Integer regionId,
            @PathVariable String category) {
        return ResponseEntity.ok(foodService.findByRegionIdAndCategory(regionId, category));
    }

    /**
     * 为行程推荐美食
     */
    @GetMapping("/recommend")
    public ResponseEntity<List<Food>> recommendForTrip(
            @RequestParam Integer regionId,
            @RequestParam(required = false) Double budget,
            @RequestParam(defaultValue = "3") Integer count) {
        return ResponseEntity.ok(foodService.recommendForTrip(regionId, budget, count));
    }
    
    // ==================== 评论相关接口 ====================
    
    /**
     * 获取美食评论列表（只返回顶级评论，带回复）
     */
    @GetMapping("/{foodId}/comments")
    public ResponseEntity<List<FoodComment>> getComments(@PathVariable Long foodId) {
        // 获取顶级评论
        List<FoodComment> comments = foodCommentRepository.findByFoodIdAndParentIdIsNullOrderByCreatedAtDesc(foodId);
        // 为每条评论加载回复
        for (FoodComment comment : comments) {
            List<FoodComment> replies = foodCommentRepository.findByParentIdOrderByCreatedAtAsc(comment.getId());
            comment.setReplies(replies);
        }
        return ResponseEntity.ok(comments);
    }
    
    /**
     * 添加美食评论
     */
    @PostMapping("/{foodId}/comments")
    public ResponseEntity<?> addComment(@PathVariable Long foodId, @RequestBody AddCommentRequest req) {
        FoodComment comment = new FoodComment();
        comment.setFoodId(foodId);
        comment.setUserId(req.getUserId());
        comment.setUserName(req.getUserName());
        comment.setRating(req.getRating());
        comment.setContent(req.getContent());
        
        FoodComment saved = foodCommentRepository.save(comment);
        return ResponseEntity.ok(saved);
    }
    
    /**
     * 点赞评论
     */
    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<?> likeComment(@PathVariable Long commentId) {
        return foodCommentRepository.findById(commentId)
                .map(comment -> {
                    comment.setLikes(comment.getLikes() + 1);
                    foodCommentRepository.save(comment);
                    return ResponseEntity.ok(comment);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 删除评论
     */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        if (foodCommentRepository.existsById(commentId)) {
            foodCommentRepository.deleteById(commentId);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * 回复评论
     */
    @PostMapping("/comments/{commentId}/reply")
    public ResponseEntity<?> replyComment(@PathVariable Long commentId, @RequestBody ReplyCommentRequest req) {
        return foodCommentRepository.findById(commentId)
                .map(parent -> {
                    FoodComment reply = new FoodComment();
                    reply.setFoodId(parent.getFoodId());
                    reply.setUserId(req.getUserId());
                    reply.setUserName(req.getUserName());
                    reply.setContent(req.getContent());
                    reply.setRating(0); // 回复不需要评分
                    reply.setParentId(commentId);
                    reply.setReplyToUserId(parent.getUserId());
                    reply.setReplyToUser(parent.getUserName());
                    
                    FoodComment saved = foodCommentRepository.save(reply);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 获取评论的回复列表
     */
    @GetMapping("/comments/{commentId}/replies")
    public ResponseEntity<List<FoodComment>> getReplies(@PathVariable Long commentId) {
        return ResponseEntity.ok(foodCommentRepository.findByParentIdOrderByCreatedAtAsc(commentId));
    }
    
    /**
     * 获取评论统计
     */
    @GetMapping("/{foodId}/comments/stats")
    public ResponseEntity<CommentStatsResponse> getCommentStats(@PathVariable Long foodId) {
        List<FoodComment> comments = foodCommentRepository.findByFoodIdOrderByCreatedAtDesc(foodId);
        
        CommentStatsResponse stats = new CommentStatsResponse();
        stats.setTotal(comments.size());
        
        if (comments.isEmpty()) {
            stats.setAverageRating(0.0);
        } else {
            double avg = comments.stream()
                    .mapToInt(FoodComment::getRating)
                    .average()
                    .orElse(0.0);
            stats.setAverageRating(Math.round(avg * 10) / 10.0);
        }
        
        // 统计各星级数量
        stats.setStar5(foodCommentRepository.countByFoodIdAndRating(foodId, 5));
        stats.setStar4(foodCommentRepository.countByFoodIdAndRating(foodId, 4));
        stats.setStar3(foodCommentRepository.countByFoodIdAndRating(foodId, 3));
        stats.setStar2(foodCommentRepository.countByFoodIdAndRating(foodId, 2));
        stats.setStar1(foodCommentRepository.countByFoodIdAndRating(foodId, 1));
        
        return ResponseEntity.ok(stats);
    }
    
    // 评论统计响应 DTO
    public static class CommentStatsResponse {
        private int total;
        private double averageRating;
        private long star5;
        private long star4;
        private long star3;
        private long star2;
        private long star1;
        
        public int getTotal() { return total; }
        public void setTotal(int total) { this.total = total; }
        public double getAverageRating() { return averageRating; }
        public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
        public long getStar5() { return star5; }
        public void setStar5(long star5) { this.star5 = star5; }
        public long getStar4() { return star4; }
        public void setStar4(long star4) { this.star4 = star4; }
        public long getStar3() { return star3; }
        public void setStar3(long star3) { this.star3 = star3; }
        public long getStar2() { return star2; }
        public void setStar2(long star2) { this.star2 = star2; }
        public long getStar1() { return star1; }
        public void setStar1(long star1) { this.star1 = star1; }
    }
    
    // 添加评论请求 DTO（已包含 userName 字段）
    public static class AddCommentRequest {
        private Long userId;
        private String userName;
        private Integer rating;
        private String content;
        
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
        public Integer getRating() { return rating; }
        public void setRating(Integer rating) { this.rating = rating; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
    
    // 回复评论请求 DTO
    public static class ReplyCommentRequest {
        private Long userId;
        private String userName;
        private String content;
        
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
}
