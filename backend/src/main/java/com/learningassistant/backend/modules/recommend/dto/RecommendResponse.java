package com.learningassistant.backend.modules.recommend.dto;

import lombok.Data;
import java.util.List;

/**
 * 智能推荐响应
 * 模块: recommend (成员3)
 */
@Data
public class RecommendResponse {

    private List<SpotRecommend> spots;
    private RouteInfo route;
    private CostEstimate cost;
    private String summary;
    private List<TransportInfo> transportation; // AI 推荐的交通信息
    private Boolean aiPowered; // 是否为 AI 推荐

    @Data
    public static class SpotRecommend {
        private Long id;
        private String name;
        private String description;
        private String imageUrl;
        private Integer playTime;
        private Double priceMin;
        private Double priceMax;
        private String reason;
        private Double latitude;
        private Double longitude;
        private Integer order;
    }

    @Data
    public static class RouteInfo {
        private List<RoutePoint> points;
        private Integer totalTime;
        private Double totalDistance;
        private String suggestion;
    }

    @Data
    public static class RoutePoint {
        private Long spotId;
        private String spotName;
        private Double latitude;
        private Double longitude;
        private Integer order;
        private Integer stayTime;
        private Integer travelTime;
    }

    @Data
    public static class CostEstimate {
        private Double ticketCost;
        private Double transportCost;
        private Double mealCost;
        private Double totalCost;
        private Double perPersonCost;
        private String breakdown;
    }
    
    /**
     * AI 推荐的交通信息
     */
    @Data
    public static class TransportInfo {
        private String from;          // 起点景点名
        private String to;            // 终点景点名
        private String method;        // 交通方式：步行/公交/地铁/打车
        private Integer duration;     // 预计时间（分钟）
        private Double distance;      // 预计距离（公里）
        private Double cost;          // 预计费用（元）
        private List<TransportStep> steps;  // 详细步骤
        private String tips;          // 交通建议
    }
    
    /**
     * 交通详细步骤（类似高德地图）
     */
    @Data
    public static class TransportStep {
        private String type;          // 步行/公交/地铁
        private String instruction;   // 详细指引
        private String line;          // 公交或地铁线路名
        private Integer stations;     // 乘坐站数
        private String startStation;  // 上车站名
        private String endStation;    // 下车站名
        private Integer duration;     // 该步骤时间（分钟）
    }
}
