package com.learningassistant.backend.modules.order.repository;

import com.learningassistant.backend.modules.order.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * 收藏数据访问
 * 模块: order (成员4)
 */
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserId(Long userId);
    Favorite findByUserIdAndSpotId(Long userId, Long spotId);
}
