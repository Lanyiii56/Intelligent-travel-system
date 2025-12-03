package com.learningassistant.backend.modules.message.repository;

import com.learningassistant.backend.modules.message.model.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 关注关系Repository
 */
@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    
    // 查找关注关系
    Optional<Follow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);
    
    // 检查是否已关注
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);
    
    // 获取用户的关注列表
    List<Follow> findByFollowerIdOrderByCreateTimeDesc(Long followerId);
    
    // 获取用户的粉丝列表
    List<Follow> findByFollowingIdOrderByCreateTimeDesc(Long followingId);
    
    // 获取关注数量
    @Query("SELECT COUNT(f) FROM Follow f WHERE f.followerId = :userId")
    Long countFollowing(@Param("userId") Long userId);
    
    // 获取粉丝数量
    @Query("SELECT COUNT(f) FROM Follow f WHERE f.followingId = :userId")
    Long countFollowers(@Param("userId") Long userId);
    
    // 删除关注关系
    void deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);
}
