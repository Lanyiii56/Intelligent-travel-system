package com.learningassistant.backend.common.config;

import com.learningassistant.backend.modules.spot.repository.RegionRepository;
import com.learningassistant.backend.modules.spot.repository.SpotRepository;
import com.learningassistant.backend.modules.food.repository.FoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 数据初始化配置
 * 公共模块 (全员维护)
 * 
 * 数据通过 application.properties 中的 spring.sql.init 配置自动加载
 * SQL 文件位置:
 * - mapper/spot/regions_data.sql (地区数据，包含宜宾等所有城市)
 * - mapper/spot/spots_data.sql (景点数据)
 * - mapper/food/foods_data.sql (美食数据)
 * 
 * 此类仅用于启动时打印数据统计信息
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private SpotRepository spotRepository;

    @Autowired
    private FoodRepository foodRepository;

    @Override
    public void run(String... args) {
        System.out.println("✅ 数据初始化状态:");
        System.out.println("   - 地区: " + regionRepository.count() + " 条");
        System.out.println("   - 景点: " + spotRepository.count() + " 条");
        System.out.println("   - 美食: " + foodRepository.count() + " 条");
    }
}
