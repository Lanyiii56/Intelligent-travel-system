package com.learningassistant.backend.modules.recommend.service;

import com.learningassistant.backend.modules.recommend.dto.RecommendRequest;
import com.learningassistant.backend.modules.recommend.dto.RecommendResponse;
import com.learningassistant.backend.modules.recommend.dto.RecommendResponse.*;
import com.learningassistant.backend.modules.spot.model.Spot;
import com.learningassistant.backend.modules.spot.repository.SpotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
                .sorted(Comparator.comparing(this::calculateSpotScore).reversed())
                .collect(Collectors.toList());
    }

    private boolean isAgeSuitable(Spot spot, Integer age) {
        if (spot.getAgeMin() == null || spot.getAgeMax() == null) return true;
        return age >= spot.getAgeMin() && age <= spot.getAgeMax();
    }

    private double calculateSpotScore(Spot spot) {
        double score = 0;
        if (spot.getPriceMin() != null) {
            score += 10 - (spot.getPriceMin() / 100);
        }
        return score;
    }

    private List<SpotRecommend> planRoute(List<Spot> spots, RecommendRequest request) {
        List<SpotRecommend> result = new ArrayList<>();
        int remainingTime = request.getPlayTime();
        int order = 1;

        for (Spot spot : spots) {
            if (spot.getPlayTime() == null) continue;
            int travelTime = result.isEmpty() ? 0 : 30;
            int totalNeeded = spot.getPlayTime() + travelTime;

            if (remainingTime >= totalNeeded) {
                SpotRecommend sr = convertToSpotRecommend(spot, order++);
                sr.setReason(generateRecommendReason(spot, request.getAge()));
                result.add(sr);
                remainingTime -= totalNeeded;
            }
        }
        return result;
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
            point.setStayTime(spot.getPlayTime());
            point.setTravelTime(i < spots.size() - 1 ? 30 : 0);

            if (i < spots.size() - 1) totalTime += 30;
            totalTime += spot.getPlayTime();
            if (i > 0) totalDistance += 5;
            points.add(point);
        }

        routeInfo.setPoints(points);
        routeInfo.setTotalTime(totalTime);
        routeInfo.setTotalDistance(totalDistance);
        routeInfo.setSuggestion(spots.size() <= 2 ? "行程轻松" : spots.size() <= 4 ? "行程适中" : "行程紧凑");
        return routeInfo;
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
}
