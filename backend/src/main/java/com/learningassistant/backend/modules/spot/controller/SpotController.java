package com.learningassistant.backend.modules.spot.controller;

import com.learningassistant.backend.modules.spot.model.Spot;
import com.learningassistant.backend.modules.spot.service.SpotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 景点控制器
 * 模块: spot (成员2)
 */
@RestController
@RequestMapping("/api/spots")
public class SpotController {

    @Autowired
    private SpotService spotService;

    @GetMapping
    public List<Spot> all() {
        return spotService.listAll();
    }

    @GetMapping("/{id}")
    public Spot get(@PathVariable Long id) {
        return spotService.getById(id);
    }

    @GetMapping("/region/{regionId}")
    public List<Spot> byRegion(@PathVariable Integer regionId) {
        return spotService.findByRegion(regionId);
    }

    @GetMapping("/filter")
    public List<Spot> filter(@RequestParam int age, @RequestParam int time) {
        return spotService.findSuitableSpots(age, time);
    }
}
