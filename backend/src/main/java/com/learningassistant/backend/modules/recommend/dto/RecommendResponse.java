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
}
