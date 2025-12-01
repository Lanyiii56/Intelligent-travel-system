package com.learningassistant.backend.modules.spot.repository;

import com.learningassistant.backend.modules.spot.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 地区数据访问
 * 模块: spot (成员2)
 */
public interface RegionRepository extends JpaRepository<Region, Integer> {
    List<Region> findByParentId(Integer parentId);
    List<Region> findByType(String type);
    List<Region> findByParentIdIsNull();
}
