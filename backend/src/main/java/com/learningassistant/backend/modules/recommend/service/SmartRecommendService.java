package com.learningassistant.backend.modules.recommend.service;

import com.learningassistant.backend.modules.recommend.dto.RecommendRequest;
import com.learningassistant.backend.modules.recommend.dto.RecommendResponse;
import com.learningassistant.backend.modules.recommend.dto.RecommendResponse.*;
import com.learningassistant.backend.modules.recommend.model.SavedItinerary;
import com.learningassistant.backend.modules.recommend.repository.SavedItineraryRepository;
import com.learningassistant.backend.modules.spot.model.Spot;
import com.learningassistant.backend.modules.spot.repository.SpotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 智能推荐服务
 * 模块: recommend (成员3)
 */
@Service
public class SmartRecommendService {

    @Autowired
    private SpotRepository spotRepository;

    @Autowired
    private SavedItineraryRepository savedItineraryRepository;

    public RecommendResponse recommend(RecommendRequest request) {
        RecommendResponse response = new RecommendResponse();

        List<Spot> suitableSpots = filterSpotsByConditions(request);
        List<SpotRecommend> recommendedSpots = planRoute(suitableSpots, request);
        response.setSpots(recommendedSpots);

        RouteInfo routeInfo = generateRouteInfo(recommendedSpots);
        response.setRoute(routeInfo);

        CostEstimate costEstimate = calculateCost(recommendedSpots, request.getPeopleCount());
        response.setCost(costEstimate);

        response.setSummary(generateSummary(request, recommendedSpots, costEstimate));

        return response;
    }

    private List<Spot> filterSpotsByConditions(RecommendRequest request) {
        List<Spot> allSpots = spotRepository.findByRegionId(request.getRegionId());

        return allSpots.stream()
                .filter(spot -> isAgeSuitable(spot, request.getAge()))
                .filter(spot -> spot.getPlayTime() != null && spot.getPlayTime() <= request.getPlayTime())
                .filter(spot -> isBudgetSuitable(spot, request.getBudget(), request.getPeopleCount()))
                .filter(spot -> isPreferenceSuitable(spot, request.getPreference()))
                .sorted(Comparator.comparing((Spot s) -> calculateSpotScore(s)).reversed())
                .collect(Collectors.toList());
    }

    private boolean isAgeSuitable(Spot spot, Integer age) {
        if (spot.getAgeMin() == null || spot.getAgeMax() == null) return true;
        return age >= spot.getAgeMin() && age <= spot.getAgeMax();
    }

    private boolean isBudgetSuitable(Spot spot, Double budget, Integer peopleCount) {
        if (budget == null || budget <= 0) return true;
        if (spot.getPriceMax() == null) return true;
        double perPersonBudget = budget / peopleCount;
        return spot.getPriceMax() <= perPersonBudget * 0.5;
    }

    private boolean isPreferenceSuitable(Spot spot, String preference) {
        if (preference == null || preference.isEmpty()) return true;
        if (spot.getDescription() == null) return true;
        return spot.getDescription().contains(preference) || spot.getName().contains(preference);
    }

    private double calculateSpotScore(Spot spot) {
        double score = 50;
        if (spot.getPriceMin() != null) {
            score += Math.max(0, 20 - (spot.getPriceMin() / 50));
        }
        if (spot.getPlayTime() != null && spot.getPlayTime() <= 120) {
            score += 10;
        }
        return score;
    }

    /**
     * 智能路线规划
     * 根据年龄、时长、距离等因素规划最优路线
     */
    private List<SpotRecommend> planRoute(List<Spot> spots, RecommendRequest request) {
        List<SpotRecommend> result = new ArrayList<>();
        int remainingTime = request.getPlayTime();
        int age = request.getAge();
        int order = 1;

        // 根据年龄调整游玩节奏
        double paceMultiplier = calculatePaceMultiplier(age);
        
        // 按距离排序，优化路线（简单贪心算法）
        List<Spot> sortedSpots = optimizeRouteOrder(spots);

        for (Spot spot : sortedSpots) {
            if (spot.getPlayTime() == null) continue;
            
            // 根据年龄调整实际游玩时间
            int adjustedPlayTime = (int) (spot.getPlayTime() * paceMultiplier);
            
            // 计算到下一个景点的交通时间
            int travelTime = result.isEmpty() ? 0 : calculateTravelTime(result.get(result.size() - 1), spot);
            
            // 根据年龄添加休息时间
            int restTime = calculateRestTime(age, result.size());
            
            int totalNeeded = adjustedPlayTime + travelTime + restTime;

            if (remainingTime >= totalNeeded) {
                SpotRecommend sr = convertToSpotRecommend(spot, order++);
                sr.setReason(generateRecommendReason(spot, age));
                result.add(sr);
                remainingTime -= totalNeeded;
            }
        }
        
        // 如果时间充裕，添加用餐建议
        if (request.getPlayTime() >= 240 && result.size() >= 2) {
            addMealSuggestion(result, request.getPlayTime());
        }
        
        return result;
    }

    /**
     * 根据年龄计算游玩节奏系数
     */
    private double calculatePaceMultiplier(int age) {
        if (age < 12) return 0.8;       // 儿童：节奏快，时间短
        if (age < 25) return 0.9;       // 年轻人：精力充沛
        if (age < 45) return 1.0;       // 中年人：正常节奏
        if (age < 60) return 1.2;       // 中老年：稍慢
        return 1.4;                      // 老年人：慢节奏
    }

    /**
     * 计算休息时间（根据年龄和已游玩景点数）
     */
    private int calculateRestTime(int age, int visitedCount) {
        if (visitedCount == 0) return 0;
        
        int baseRest = 10; // 基础休息时间
        if (age < 12) return baseRest;
        if (age < 45) return baseRest + 5;
        if (age < 60) return baseRest + 15;
        return baseRest + 25; // 老年人需要更多休息
    }

    /**
     * 计算两个景点之间的交通时间（基于距离估算）
     */
    private int calculateTravelTime(SpotRecommend from, Spot to) {
        if (from.getLatitude() == null || to.getLatitude() == null) {
            return 30; // 默认30分钟
        }
        double distance = calculateDistance(
            from.getLatitude(), from.getLongitude(),
            to.getLatitude(), to.getLongitude()
        );
        // 假设平均速度30km/h（考虑城市交通）
        return Math.max(15, (int) (distance / 30 * 60));
    }

    /**
     * 计算两点之间的距离（公里）
     */
    private double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            return 5.0; // 默认5公里
        }
        double R = 6371; // 地球半径（公里）
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    /**
     * 优化路线顺序（最近邻算法）
     */
    private List<Spot> optimizeRouteOrder(List<Spot> spots) {
        if (spots.size() <= 2) return new ArrayList<>(spots);
        
        List<Spot> result = new ArrayList<>();
        List<Spot> remaining = new ArrayList<>(spots);
        
        // 从第一个景点开始
        result.add(remaining.remove(0));
        
        while (!remaining.isEmpty()) {
            Spot last = result.get(result.size() - 1);
            Spot nearest = findNearestSpot(last, remaining);
            result.add(nearest);
            remaining.remove(nearest);
        }
        
        return result;
    }

    /**
     * 找到最近的景点
     */
    private Spot findNearestSpot(Spot from, List<Spot> candidates) {
        Spot nearest = candidates.get(0);
        double minDistance = Double.MAX_VALUE;
        
        for (Spot spot : candidates) {
            double distance = calculateDistance(
                from.getLatitude(), from.getLongitude(),
                spot.getLatitude(), spot.getLongitude()
            );
            if (distance < minDistance) {
                minDistance = distance;
                nearest = spot;
            }
        }
        return nearest;
    }

    /**
     * 添加用餐建议
     */
    private void addMealSuggestion(List<SpotRecommend> spots, int totalTime) {
        if (spots.isEmpty()) return;
        
        // 在中间位置添加用餐提示
        int mealIndex = spots.size() / 2;
        if (mealIndex < spots.size()) {
            SpotRecommend spot = spots.get(mealIndex);
            String currentReason = spot.getReason();
            if (totalTime >= 360) { // 6小时以上建议午餐
                spot.setReason(currentReason + " 【建议在此附近用午餐】");
            }
        }
    }

    private SpotRecommend convertToSpotRecommend(Spot spot, int order) {
        SpotRecommend sr = new SpotRecommend();
        sr.setId(spot.getId());
        sr.setName(spot.getName());
        sr.setDescription(spot.getDescription());
        sr.setImageUrl(spot.getImageUrl());
        sr.setPlayTime(spot.getPlayTime());
        sr.setPriceMin(spot.getPriceMin());
        sr.setPriceMax(spot.getPriceMax());
        sr.setLatitude(spot.getLatitude());
        sr.setLongitude(spot.getLongitude());
        sr.setOrder(order);
        return sr;
    }

    private String generateRecommendReason(Spot spot, Integer age) {
        StringBuilder reason = new StringBuilder();
        if (age < 12) reason.append("适合儿童游玩，");
        else if (age < 25) reason.append("年轻人热门打卡地，");
        else if (age < 45) reason.append("适合家庭出游，");
        else if (age < 60) reason.append("休闲放松好去处，");
        else reason.append("适合老年人慢游，");

        if (spot.getPlayTime() != null && spot.getPlayTime() <= 60) reason.append("游玩时间适中");
        else reason.append("内容丰富值得深度游");
        return reason.toString();
    }

    /**
     * 生成详细路线信息
     */
    private RouteInfo generateRouteInfo(List<SpotRecommend> spots) {
        RouteInfo routeInfo = new RouteInfo();
        List<RoutePoint> points = new ArrayList<>();
        int totalTime = 0;
        double totalDistance = 0;

        for (int i = 0; i < spots.size(); i++) {
            SpotRecommend spot = spots.get(i);
            RoutePoint point = new RoutePoint();
            point.setSpotId(spot.getId());
            point.setSpotName(spot.getName());
            point.setLatitude(spot.getLatitude());
            point.setLongitude(spot.getLongitude());
            point.setOrder(i + 1);
            point.setStayTime(spot.getPlayTime() != null ? spot.getPlayTime() : 60);
            
            // 计算实际交通时间
            int travelTime = 0;
            if (i < spots.size() - 1) {
                SpotRecommend nextSpot = spots.get(i + 1);
                double distance = calculateDistance(
                    spot.getLatitude(), spot.getLongitude(),
                    nextSpot.getLatitude(), nextSpot.getLongitude()
                );
                travelTime = Math.max(15, (int) (distance / 30 * 60));
                totalDistance += distance;
            }
            point.setTravelTime(travelTime);

            totalTime += point.getStayTime() + travelTime;
            points.add(point);
        }

        routeInfo.setPoints(points);
        routeInfo.setTotalTime(totalTime);
        routeInfo.setTotalDistance(Math.round(totalDistance * 10) / 10.0);
        routeInfo.setSuggestion(generateRouteSuggestion(spots.size(), totalTime, totalDistance));
        return routeInfo;
    }

    /**
     * 生成路线建议
     */
    private String generateRouteSuggestion(int spotCount, int totalTime, double totalDistance) {
        StringBuilder suggestion = new StringBuilder();
        
        // 行程强度评估
        if (spotCount <= 2) {
            suggestion.append("轻松休闲游，时间充裕可深度体验。");
        } else if (spotCount <= 4) {
            suggestion.append("行程适中，节奏舒适。");
        } else {
            suggestion.append("行程较紧凑，建议早出发。");
        }
        
        // 交通建议
        if (totalDistance > 20) {
            suggestion.append("景点较分散，建议打车或自驾。");
        } else if (totalDistance > 10) {
            suggestion.append("可选择公交或打车出行。");
        } else {
            suggestion.append("景点较集中，可步行或骑行。");
        }
        
        // 时间建议
        if (totalTime > 480) {
            suggestion.append("全天行程，请注意休息和用餐。");
        } else if (totalTime > 240) {
            suggestion.append("半天行程，可安排午餐后出发。");
        }
        
        return suggestion.toString();
    }

    private CostEstimate calculateCost(List<SpotRecommend> spots, Integer peopleCount) {
        CostEstimate cost = new CostEstimate();
        double ticketCost = spots.stream().mapToDouble(s -> s.getPriceMax() != null ? s.getPriceMax() : 0).sum() * peopleCount;
        double transportCost = spots.size() * 10.0 * peopleCount;
        double mealCost = 50.0 * peopleCount;
        double totalCost = ticketCost + transportCost + mealCost;

        cost.setTicketCost(ticketCost);
        cost.setTransportCost(transportCost);
        cost.setMealCost(mealCost);
        cost.setTotalCost(totalCost);
        cost.setPerPersonCost(totalCost / peopleCount);
        cost.setBreakdown(String.format("门票: ¥%.0f, 交通: ¥%.0f, 餐饮: ¥%.0f", ticketCost, transportCost, mealCost));
        return cost;
    }

    private String generateSummary(RecommendRequest request, List<SpotRecommend> spots, CostEstimate cost) {
        if (spots.isEmpty()) return "抱歉，根据您的条件暂未找到合适的景点";
        return String.format("为您推荐了%d个适合%d岁游客的景点，%d人总费用约¥%.0f",
                spots.size(), request.getAge(), request.getPeopleCount(), cost.getTotalCost());
    }

    /**
     * 保存行程
     */
    public SavedItinerary saveItinerary(Long userId, String name, String itineraryData) {
        SavedItinerary itinerary = new SavedItinerary();
        itinerary.setUserId(userId);
        itinerary.setName(name);
        itinerary.setItineraryData(itineraryData);
        itinerary.setCreateTime(LocalDateTime.now());
        itinerary.setUpdateTime(LocalDateTime.now());
        return savedItineraryRepository.save(itinerary);
    }

    /**
     * 获取用户保存的行程
     */
    public List<SavedItinerary> getUserItineraries(Long userId) {
        return savedItineraryRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }

    /**
     * 删除行程
     */
    public void deleteItinerary(Long id) {
        savedItineraryRepository.deleteById(id);
    }
}
