package com.learningassistant.backend.modules.spot.repository;

import com.learningassistant.backend.modules.spot.model.Spot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 景点数据访问
 * 模块: spot (成员2)
 */
public interface SpotRepository extends JpaRepository<Spot, Long> {
    List<Spot> findByRegionId(Long regionId);
}
