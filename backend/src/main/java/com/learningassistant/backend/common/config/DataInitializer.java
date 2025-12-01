package com.learningassistant.backend.common.config;

import com.learningassistant.backend.modules.spot.model.Region;
import com.learningassistant.backend.modules.spot.model.Spot;
import com.learningassistant.backend.modules.spot.repository.RegionRepository;
import com.learningassistant.backend.modules.spot.repository.SpotRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 数据初始化配置
 * 公共模块 (全员维护)
 */
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(RegionRepository regionRepository, SpotRepository spotRepository) {
        return args -> {
            // 如果已有数据则跳过
            if (regionRepository.count() > 0) {
                return;
            }

            // 初始化地区数据
            Region beijing = new Region();
            beijing.setName("北京");
            beijing.setType("province");
            regionRepository.save(beijing);

            Region shanghai = new Region();
            shanghai.setName("上海");
            shanghai.setType("province");
            regionRepository.save(shanghai);

            Region sichuan = new Region();
            sichuan.setName("四川");
            sichuan.setType("province");
            regionRepository.save(sichuan);

            Region zhejiang = new Region();
            zhejiang.setName("浙江");
            zhejiang.setType("province");
            regionRepository.save(zhejiang);

            // 初始化景点数据
            createSpot(spotRepository, "故宫博物院", "中国明清两代的皇家宫殿", beijing.getId(), 60.0, 60.0, "08:30-17:00", 180, 3, 80, 39.9163, 116.3972);
            createSpot(spotRepository, "天坛公园", "明清皇帝祭天的场所", beijing.getId(), 15.0, 35.0, "06:00-21:00", 120, 5, 75, 39.8822, 116.4066);
            createSpot(spotRepository, "颐和园", "中国清朝时期皇家园林", beijing.getId(), 30.0, 60.0, "06:30-18:00", 240, 8, 70, 39.9999, 116.2755);
            createSpot(spotRepository, "长城(八达岭)", "世界文化遗产", beijing.getId(), 40.0, 40.0, "06:30-19:00", 300, 10, 65, 40.3598, 116.0201);

            createSpot(spotRepository, "外滩", "上海标志性景观", shanghai.getId(), 0.0, 0.0, "全天", 60, 1, 90, 31.2400, 121.4900);
            createSpot(spotRepository, "东方明珠", "上海地标建筑", shanghai.getId(), 180.0, 220.0, "08:00-21:30", 120, 5, 80, 31.2397, 121.4998);
            createSpot(spotRepository, "上海迪士尼", "中国大陆首座迪士尼乐园", shanghai.getId(), 399.0, 769.0, "08:30-20:30", 480, 3, 60, 31.1434, 121.6580);
            createSpot(spotRepository, "豫园", "江南古典园林", shanghai.getId(), 40.0, 40.0, "08:30-17:00", 90, 10, 85, 31.2272, 121.4924);

            createSpot(spotRepository, "九寨沟", "世界自然遗产", sichuan.getId(), 169.0, 250.0, "07:00-18:00", 480, 8, 70, 33.2600, 103.9200);
            createSpot(spotRepository, "峨眉山", "中国四大佛教名山之一", sichuan.getId(), 160.0, 185.0, "06:00-18:00", 360, 12, 65, 29.5500, 103.4800);
            createSpot(spotRepository, "都江堰", "世界文化遗产", sichuan.getId(), 80.0, 90.0, "08:00-18:00", 180, 8, 75, 31.0000, 103.6100);
            createSpot(spotRepository, "成都大熊猫基地", "大熊猫繁育研究基地", sichuan.getId(), 55.0, 55.0, "07:30-18:00", 180, 1, 90, 30.7400, 104.1500);

            createSpot(spotRepository, "西湖", "世界文化遗产", zhejiang.getId(), 0.0, 0.0, "全天", 240, 1, 90, 30.2500, 120.1500);
            createSpot(spotRepository, "乌镇", "江南水乡古镇", zhejiang.getId(), 150.0, 190.0, "09:00-22:00", 300, 10, 80, 30.7500, 120.4900);
            createSpot(spotRepository, "普陀山", "中国四大佛教名山之一", zhejiang.getId(), 160.0, 200.0, "06:00-18:00", 360, 15, 70, 30.0100, 122.3800);
            createSpot(spotRepository, "千岛湖", "国家5A级旅游景区", zhejiang.getId(), 150.0, 180.0, "08:00-17:00", 300, 5, 75, 29.6000, 118.9500);

            System.out.println("✅ 初始化数据完成: 4个地区, 16个景点");
        };
    }

    private void createSpot(SpotRepository repo, String name, String desc, Integer regionId,
                           Double priceMin, Double priceMax, String openTime, Integer playTime,
                           Integer ageMin, Integer ageMax, Double lat, Double lng) {
        Spot spot = new Spot();
        spot.setName(name);
        spot.setDescription(desc);
        spot.setRegionId(regionId);
        spot.setPriceMin(priceMin);
        spot.setPriceMax(priceMax);
        spot.setOpenTime(openTime);
        spot.setPlayTime(playTime);
        spot.setAgeMin(ageMin);
        spot.setAgeMax(ageMax);
        spot.setLatitude(lat);
        spot.setLongitude(lng);
        spot.setImageUrl("https://picsum.photos/400/300?random=" + name.hashCode());
        repo.save(spot);
    }
}
