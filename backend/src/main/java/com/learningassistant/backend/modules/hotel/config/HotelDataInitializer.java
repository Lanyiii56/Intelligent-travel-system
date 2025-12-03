package com.learningassistant.backend.modules.hotel.config;

import com.learningassistant.backend.modules.hotel.model.Hotel;
import com.learningassistant.backend.modules.hotel.model.HotelRoom;
import com.learningassistant.backend.modules.hotel.repository.HotelRepository;
import com.learningassistant.backend.modules.hotel.repository.HotelRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 酒店数据初始化器
 * 应用启动时自动检查并初始化酒店数据
 */
@Component
public class HotelDataInitializer implements CommandLineRunner {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private HotelRoomRepository roomRepository;

    @Override
    public void run(String... args) {
        // 如果已有数据则跳过
        if (hotelRepository.count() > 0) {
            System.out.println("酒店数据已存在，跳过初始化");
            return;
        }

        System.out.println("开始初始化酒店数据...");
        
        // ==================== 杭州酒店 (regionId=19) ====================
        Hotel h1 = createHotel("杭州西湖国宾馆", "坐落于西湖畔，拥有绝美湖景，是杭州最具历史底蕴的高端酒店之一。", 
            19, "杭州市西湖区杨公堤18号", 1280.0, 3880.0, 4.9, 5, 30.2395, 120.1308,
            "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800",
            "[\"免费WiFi\",\"游泳池\",\"健身房\",\"SPA\",\"餐厅\",\"会议室\",\"停车场\"]", "0571-87979889");
        
        Hotel h2 = createHotel("杭州柏悦酒店", "位于钱江新城CBD核心，俯瞰钱塘江，现代奢华风格。",
            19, "杭州市江干区钱江路1366号", 1580.0, 4280.0, 4.8, 5, 30.2456, 120.2134,
            "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?w=800",
            "[\"免费WiFi\",\"游泳池\",\"健身房\",\"SPA\",\"餐厅\",\"酒吧\"]", "0571-87961234");
        
        Hotel h3 = createHotel("全季酒店(西湖店)", "简约舒适，性价比之选，靠近西湖景区。",
            19, "杭州市上城区延安路398号", 328.0, 528.0, 4.5, 3, 30.2456, 120.1567,
            "https://images.unsplash.com/photo-1590490360182-c33d57733427?w=800",
            "[\"免费WiFi\",\"空调\",\"24小时前台\"]", "0571-87654321");
        
        Hotel h4 = createHotel("如家酒店(火车东站店)", "紧邻杭州东站，出行便捷，经济实惠。",
            19, "杭州市江干区新塘路108号", 218.0, 398.0, 4.3, 2, 30.2912, 120.2345,
            "https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=800",
            "[\"免费WiFi\",\"空调\",\"24小时前台\",\"停车场\"]", "0571-86543210");

        // ==================== 北京酒店 (regionId=1) ====================
        Hotel h5 = createHotel("北京王府井文华东方酒店", "位于王府井核心地段，毗邻故宫，极致奢华体验。",
            1, "北京市东城区王府井大街269号", 2880.0, 8880.0, 4.9, 5, 39.9139, 116.4074,
            "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800",
            "[\"免费WiFi\",\"游泳池\",\"健身房\",\"SPA\",\"米其林餐厅\",\"管家服务\"]", "010-85098888");
        
        Hotel h6 = createHotel("北京瑰丽酒店", "朝阳CBD核心，现代艺术与奢华的完美融合。",
            1, "北京市朝阳区建国门外大街1号", 2580.0, 6880.0, 4.9, 5, 39.9087, 116.4605,
            "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?w=800",
            "[\"免费WiFi\",\"游泳池\",\"健身房\",\"SPA\",\"餐厅\",\"酒吧\"]", "010-65368888");
        
        Hotel h7 = createHotel("全季酒店(北京王府井店)", "王府井步行街旁，购物观光便捷。",
            1, "北京市东城区王府井大街138号", 398.0, 598.0, 4.5, 3, 39.9145, 116.4098,
            "https://images.unsplash.com/photo-1590490360182-c33d57733427?w=800",
            "[\"免费WiFi\",\"空调\",\"24小时前台\"]", "010-65288888");
        
        Hotel h8 = createHotel("亚朵酒店(北京三里屯店)", "三里屯商圈，时尚潮流地标。",
            1, "北京市朝阳区工体北路8号", 458.0, 758.0, 4.7, 4, 39.9366, 116.4545,
            "https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?w=800",
            "[\"免费WiFi\",\"阅读空间\",\"健身房\",\"24小时前台\"]", "010-64178888");

        // ==================== 宜宾酒店 (regionId=53) ====================
        Hotel h9 = createHotel("宜宾万达文华酒店", "临港新区地标，俯瞰长江，商务度假首选。",
            53, "四川省宜宾市临港经济开发区长江大道1号", 688.0, 1688.0, 4.8, 5, 28.7519, 104.6417,
            "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800",
            "[\"免费WiFi\",\"游泳池\",\"健身房\",\"SPA\",\"餐厅\",\"会议室\"]", "0831-8888888");
        
        Hotel h10 = createHotel("宜宾蜀南竹海度假酒店", "蜀南竹海景区内，竹林环绕，天然氧吧。",
            53, "四川省宜宾市长宁县蜀南竹海景区", 488.0, 1288.0, 4.7, 4, 28.5833, 104.9167,
            "https://images.unsplash.com/photo-1571896349842-33c89424de2d?w=800",
            "[\"免费WiFi\",\"餐厅\",\"茶室\",\"停车场\",\"景区直通\"]", "0831-4888888");
        
        Hotel h11 = createHotel("宜宾李庄古镇民宿", "李庄古镇内，体验川南古镇风情。",
            53, "四川省宜宾市翠屏区李庄镇正街88号", 268.0, 568.0, 4.5, 3, 28.8167, 104.5833,
            "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?w=800",
            "[\"免费WiFi\",\"空调\",\"古镇景观\",\"特色早餐\"]", "0831-2588888");
        
        Hotel h12 = createHotel("宜宾五粮液酒店", "五粮液集团旗下，品味酒都文化。",
            53, "四川省宜宾市翠屏区五粮液大道1号", 588.0, 1088.0, 4.6, 4, 28.7789, 104.6512,
            "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=800",
            "[\"免费WiFi\",\"健身房\",\"餐厅\",\"酒文化展厅\",\"停车场\"]", "0831-3588888");
        
        // ==================== 上海酒店 (regionId=2) ====================
        Hotel h13 = createHotel("上海外滩华尔道夫酒店", "外滩地标建筑，百年历史，极致奢华。",
            2, "上海市黄浦区中山东一路2号", 2580.0, 8880.0, 4.9, 5, 31.2400, 121.4900,
            "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800",
            "[\"免费WiFi\",\"游泳池\",\"健身房\",\"SPA\",\"米其林餐厅\"]", "021-63229988");
        
        Hotel h14 = createHotel("上海浦东丽思卡尔顿酒店", "陆家嘴金融中心，俯瞰黄浦江。",
            2, "上海市浦东新区世纪大道8号", 2280.0, 6880.0, 4.8, 5, 31.2350, 121.5050,
            "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?w=800",
            "[\"免费WiFi\",\"游泳池\",\"健身房\",\"SPA\",\"餐厅\"]", "021-20201888");
        
        Hotel h15 = createHotel("全季酒店(南京路店)", "南京路步行街旁，购物便捷。",
            2, "上海市黄浦区南京东路388号", 388.0, 588.0, 4.5, 3, 31.2380, 121.4800,
            "https://images.unsplash.com/photo-1590490360182-c33d57733427?w=800",
            "[\"免费WiFi\",\"空调\",\"24小时前台\"]", "021-63508888");
        
        // ==================== 成都酒店 (regionId=15) ====================
        Hotel h16 = createHotel("成都香格里拉大酒店", "春熙路商圈，购物美食便捷。",
            15, "成都市锦江区滨江东路9号", 1080.0, 3280.0, 4.8, 5, 30.6550, 104.0800,
            "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800",
            "[\"免费WiFi\",\"游泳池\",\"健身房\",\"SPA\",\"餐厅\"]", "028-88889999");
        
        Hotel h17 = createHotel("成都宽窄巷子亚朵酒店", "紧邻宽窄巷子，体验老成都风情。",
            15, "成都市青羊区长顺街88号", 458.0, 758.0, 4.7, 4, 30.6700, 104.0550,
            "https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?w=800",
            "[\"免费WiFi\",\"阅读空间\",\"24小时前台\"]", "028-86668888");
        
        Hotel h18 = createHotel("如家酒店(春熙路店)", "春熙路核心，经济实惠。",
            15, "成都市锦江区红星路三段88号", 198.0, 358.0, 4.3, 2, 30.6580, 104.0820,
            "https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=800",
            "[\"免费WiFi\",\"空调\",\"24小时前台\"]", "028-86778888");

        // 创建房型
        createRoomsForHotel(h1);
        createRoomsForHotel(h2);
        createRoomsForHotel(h3);
        createRoomsForHotel(h4);
        createRoomsForHotel(h5);
        createRoomsForHotel(h6);
        createRoomsForHotel(h7);
        createRoomsForHotel(h8);
        createRoomsForHotel(h9);
        createRoomsForHotel(h10);
        createRoomsForHotel(h11);
        createRoomsForHotel(h12);
        createRoomsForHotel(h13);
        createRoomsForHotel(h14);
        createRoomsForHotel(h15);
        createRoomsForHotel(h16);
        createRoomsForHotel(h17);
        createRoomsForHotel(h18);

        System.out.println("✅ 酒店数据初始化完成！共 " + hotelRepository.count() + " 家酒店");
    }

    private Hotel createHotel(String name, String description, Integer regionId, String address,
                              Double priceMin, Double priceMax, Double rating, Integer stars,
                              Double latitude, Double longitude, String imageUrl, String facilities, String phone) {
        Hotel hotel = new Hotel();
        hotel.setName(name);
        hotel.setDescription(description);
        hotel.setRegionId(regionId);
        hotel.setAddress(address);
        hotel.setPriceMin(priceMin);
        hotel.setPriceMax(priceMax);
        hotel.setRating(rating);
        hotel.setStars(stars);
        hotel.setLatitude(latitude);
        hotel.setLongitude(longitude);
        hotel.setImageUrl(imageUrl);
        hotel.setFacilities(facilities);
        hotel.setPhone(phone);
        hotel.setCheckInTime("14:00");
        hotel.setCheckOutTime("12:00");
        return hotelRepository.save(hotel);
    }

    private void createRoomsForHotel(Hotel hotel) {
        // 根据酒店星级创建不同房型
        if (hotel.getStars() >= 5) {
            createRoom(hotel.getId(), "豪华大床房", "1.8米大床，城市景观", hotel.getPriceMin(), 1, 2, 45.0, true, true);
            createRoom(hotel.getId(), "行政套房", "独立客厅，管家服务", hotel.getPriceMin() * 1.5, 1, 2, 70.0, true, true);
            createRoom(hotel.getId(), "总统套房", "顶层奢华体验", hotel.getPriceMax(), 1, 4, 120.0, true, true);
        } else if (hotel.getStars() >= 4) {
            createRoom(hotel.getId(), "标准大床房", "1.8米大床", hotel.getPriceMin(), 1, 2, 30.0, true, false);
            createRoom(hotel.getId(), "豪华双床房", "两张1.2米床", hotel.getPriceMin() * 1.2, 2, 2, 35.0, true, true);
            createRoom(hotel.getId(), "商务套房", "独立客厅", hotel.getPriceMax(), 1, 2, 50.0, true, true);
        } else {
            createRoom(hotel.getId(), "经济大床房", "1.5米大床", hotel.getPriceMin(), 1, 2, 20.0, true, false);
            createRoom(hotel.getId(), "标准双床房", "两张1.2米床", hotel.getPriceMin() * 1.1, 2, 2, 22.0, true, false);
            createRoom(hotel.getId(), "家庭房", "适合家庭出行", hotel.getPriceMax(), 2, 3, 28.0, true, true);
        }
    }

    private void createRoom(Long hotelId, String name, String description, Double price,
                            Integer bedType, Integer maxGuests, Double area, Boolean hasWindow, Boolean hasBreakfast) {
        HotelRoom room = new HotelRoom();
        room.setHotelId(hotelId);
        room.setName(name);
        room.setDescription(description);
        room.setPrice(price);
        room.setBedType(bedType);
        room.setMaxGuests(maxGuests);
        room.setArea(area);
        room.setHasWindow(hasWindow);
        room.setHasBreakfast(hasBreakfast);
        room.setCanCancel(true);
        room.setStock(10);
        room.setImageUrl("https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=400");
        room.setFacilities("[\"空调\",\"电视\",\"免费WiFi\"]");
        roomRepository.save(room);
    }
}
