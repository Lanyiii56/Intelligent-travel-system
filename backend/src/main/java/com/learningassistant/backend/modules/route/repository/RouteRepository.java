package com.learningassistant.backend.modules.route.repository;

import com.learningassistant.backend.modules.route.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 路线数据访问
 * 模块: route (路线规划)
 */
@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    
    List<Route> findByUserIdOrderByCreateTimeDesc(Long userId);
    
    List<Route> findByStatusOrderByCreateTimeDesc(String status);
}
