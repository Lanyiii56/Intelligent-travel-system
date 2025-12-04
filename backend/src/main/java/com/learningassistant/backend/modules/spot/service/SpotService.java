package com.learningassistant.backend.modules.spot.service;

import com.learningassistant.backend.modules.spot.model.Spot;
import com.learningassistant.backend.modules.spot.repository.SpotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 景点服务
 * 模块: spot (成员2)
 */
@Service
public class SpotService {

    @Autowired
    private SpotRepository spotRepository;

    public List<Spot> listAll() {
        return spotRepository.findAll();
    }

    public Spot getById(Long id) {
        return spotRepository.findById(id).orElse(null);
    }

    public List<Spot> findByRegion(Long regionId) {
        return spotRepository.findByRegionId(regionId);
    }
}
