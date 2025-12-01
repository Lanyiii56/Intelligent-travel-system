package com.learningassistant.backend.modules.spot.controller;

import com.learningassistant.backend.modules.spot.model.Region;
import com.learningassistant.backend.modules.spot.repository.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地区控制器
 * 模块: spot (成员2)
 */
@RestController
@RequestMapping("/api/regions")
public class RegionController {

    @Autowired
    private RegionRepository regionRepository;

    @GetMapping
    public List<Region> getAllRegions() {
        return regionRepository.findAll();
    }

    @GetMapping("/provinces")
    public List<Region> getProvinces() {
        return regionRepository.findByParentIdIsNull();
    }

    @GetMapping("/{provinceId}/cities")
    public List<Region> getCities(@PathVariable Integer provinceId) {
        return regionRepository.findByParentId(provinceId);
    }

    @GetMapping("/{cityId}/areas")
    public List<Region> getAreas(@PathVariable Integer cityId) {
        return regionRepository.findByParentId(cityId);
    }

    @GetMapping("/type/{type}")
    public List<Region> getByType(@PathVariable String type) {
        return regionRepository.findByType(type);
    }
}
