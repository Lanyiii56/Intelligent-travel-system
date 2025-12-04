package com.learningassistant.backend.modules.recommend.service;

import com.learningassistant.backend.modules.recommend.dto.RecommendRequest;
import com.learningassistant.backend.modules.recommend.dto.RecommendResponse;
import com.learningassistant.backend.modules.recommend.dto.RecommendResponse.*;
import com.learningassistant.backend.modules.recommend.model.SavedItinerary;
import com.learningassistant.backend.modules.recommend.repository.SavedItineraryRepository;
import com.learningassistant.backend.modules.spot.model.Region;
import com.learningassistant.backend.modules.spot.model.Spot;
import com.learningassistant.backend.modules.spot.repository.RegionRepository;
import com.learningassistant.backend.modules.spot.repository.SpotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 智能推荐服务
 * 模块: recommend (成员3)
 * 集成通义千问 AI 实现智能推荐
 */
@Service
public class SmartRecommendService {

    @Autowired
    private SpotRepository spotRepository;

    @Autowired
    private SavedItineraryRepository savedItineraryRepository;

    @Autowired
    private QwenAIService qwenAIService;
    
    @Autowired
    private RegionRepository regionRepository;

    // 存储 AI 返回的摘要和交通信息
    private String lastAISummary = null;
    private List<TransportInfo> lastTransportation = null;

    public RecommendResponse recommend(RecommendRequest request) {
        RecommendResponse response = new RecommendResponse();

        // 获取符合条件的景点（放宽筛选条件，让 AI 有更多选择）
        List<Spot> suitableSpots = filterSpotsByConditionsForAI(request);
        
        List<SpotRecommend> recommendedSpots;
        lastAISummary = null;
        lastTransportation = null;
        
        // 尝试使用 AI 推荐
        if (qwenAIService.isAvailable() && suitableSpots.size() > 0) {
            try {
                System.out.println("正在调用通义千问 AI 进行智能推荐...");
                recommendedSpots = getAIRecommendation(suitableSpots, request);
                if (recommendedSpots != null && !recommendedSpots.isEmpty()) {
                    // AI 推荐成功
                    System.out.println("AI 推荐成功，返回 " + recommendedSpots.size() + " 个景点");
                    response.setSpots(recommendedSpots);
                    response.setAiPowered(true);
                    
                    RouteInfo routeInfo = generateRouteInfo(recommendedSpots);
                    response.setRoute(routeInfo);
                    
                    CostEstimate costEstimate = calculateCost(recommendedSpots, request.getPeopleCount());
                    // 如果有 AI 交通信息，更新交通费用
                    if (lastTransportation != null && !lastTransportation.isEmpty()) {
                        response.setTransportation(lastTransportation);
                        double totalTransportCost = lastTransportation.stream()
                            .mapToDouble(t -> t.getCost() != null ? t.getCost() : 0)
                            .sum();
                        costEstimate.setTransportCost(totalTransportCost * request.getPeopleCount());
                        costEstimate.setTotalCost(costEstimate.getTicketCost() + costEstimate.getTransportCost() + costEstimate.getMealCost());
                        costEstimate.setPerPersonCost(costEstimate.getTotalCost() / request.getPeopleCount());
                    }
                    response.setCost(costEstimate);
                    
                    // 使用 AI 生成的摘要
                    if (lastAISummary != null && !lastAISummary.isEmpty()) {
                        response.setSummary("🤖 AI智能推荐：" + lastAISummary);
                    } else {
                        response.setSummary("🤖 AI智能推荐：" + generateSummary(request, recommendedSpots, costEstimate));
                    }
                    return response;
                }
            } catch (Exception e) {
                System.err.println("AI 推荐失败，使用默认算法: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        // 默认推荐逻辑
        System.out.println("使用默认算法进行推荐...");
        suitableSpots = filterSpotsByConditions(request);
        recommendedSpots = planRoute(suitableSpots, request);
        response.setSpots(recommendedSpots);

        RouteInfo routeInfo = generateRouteInfo(recommendedSpots);
        response.setRoute(routeInfo);

        CostEstimate costEstimate = calculateCost(recommendedSpots, request.getPeopleCount());
        response.setCost(costEstimate);

        response.setSummary(generateSummary(request, recommendedSpots, costEstimate));

        return response;
    }
    
    /**
     * 为 AI 推荐放宽筛选条件
     */
    private List<Spot> filterSpotsByConditionsForAI(RecommendRequest request) {
        List<Spot> allSpots = spotRepository.findByRegionId(request.getRegionId());
        
        // 只做基本筛选，让 AI 有更多选择空间
        return allSpots.stream()
                .filter(spot -> spot.getRecommendedDuration() != null && spot.getRecommendedDuration() > 0)
                .sorted(Comparator.comparing((Spot s) -> calculateSpotScore(s)).reversed())
                .limit(10) // 最多给 AI 10 个景点选择
                .collect(Collectors.toList());
    }
    
    /**
     * 使用通义千问 AI 进行智能推荐
     */
    private List<SpotRecommend> getAIRecommendation(List<Spot> availableSpots, RecommendRequest request) {
        // 构建景点信息列表（包含坐标用于路线规划）
        List<Map<String, Object>> spotInfoList = new ArrayList<>();
        for (Spot spot : availableSpots) {
            Map<String, Object> info = new HashMap<>();
            info.put("id", spot.getId());
            info.put("name", spot.getName());
            info.put("description", spot.getDescription() != null ? spot.getDescription() : "暂无介绍");
            info.put("playTime", spot.getRecommendedDuration() != null ? spot.getRecommendedDuration() : 60);
            info.put("priceMin", spot.getTicketPrice() != null ? spot.getTicketPrice() : BigDecimal.ZERO);
            info.put("priceMax", spot.getTicketPrice() != null ? spot.getTicketPrice() : BigDecimal.ZERO);
            info.put("latitude", spot.getLatitude() != null ? spot.getLatitude() : BigDecimal.ZERO);
            info.put("longitude", spot.getLongitude() != null ? spot.getLongitude() : BigDecimal.ZERO);
            spotInfoList.add(info);
        }
        
        // 获取目的地名称
        String destination = "未知";
        if (!availableSpots.isEmpty() && availableSpots.get(0).getRegionId() != null) {
            Region region = regionRepository.findById(availableSpots.get(0).getRegionId()).orElse(null);
            if (region != null && region.getName() != null) {
                destination = region.getName();
            }
        }
        
        // 调用 AI
        String aiResponse = qwenAIService.generateTravelRecommendation(
            destination,
            request.getAge(),
            request.getPlayTime(),
            request.getPeopleCount(),
            request.getBudget(),
            request.getPreference(),
            spotInfoList
        );
        
        if (aiResponse == null) {
            return null;
        }
        
        // 解析 AI 响应
        Map<String, Object> parsed = qwenAIService.parseAIResponse(aiResponse);
        if (parsed == null) {
            return null;
        }
        
        // 获取 AI 选择的景点序号
        List<Integer> selectedIndices = (List<Integer>) parsed.get("selectedSpots");
        Map<String, String> reasons = (Map<String, String>) parsed.get("reasons");
        
        // 保存 AI 生成的摘要
        if (parsed.get("summary") != null) {
            lastAISummary = parsed.get("summary").toString();
        }
        
        // 解析交通信息（包含详细步骤）
        if (parsed.get("transportation") != null) {
            try {
                List<Map<String, Object>> transportList = (List<Map<String, Object>>) parsed.get("transportation");
                lastTransportation = new ArrayList<>();
                for (Map<String, Object> t : transportList) {
                    TransportInfo info = new TransportInfo();
                    info.setFrom(t.get("from") != null ? t.get("from").toString() : "");
                    info.setTo(t.get("to") != null ? t.get("to").toString() : "");
                    info.setMethod(t.get("method") != null ? t.get("method").toString() : "步行");
                    info.setDuration(t.get("duration") != null ? ((Number) t.get("duration")).intValue() : 10);
                    info.setDistance(t.get("distance") != null ? ((Number) t.get("distance")).doubleValue() : 1.0);
                    info.setCost(t.get("cost") != null ? ((Number) t.get("cost")).doubleValue() : 0.0);
                    info.setTips(t.get("tips") != null ? t.get("tips").toString() : "");
                    
                    // 解析详细步骤
                    if (t.get("steps") != null) {
                        List<Map<String, Object>> stepsList = (List<Map<String, Object>>) t.get("steps");
                        List<TransportStep> steps = new ArrayList<>();
                        for (Map<String, Object> s : stepsList) {
                            TransportStep step = new TransportStep();
                            step.setType(s.get("type") != null ? s.get("type").toString() : "步行");
                            step.setInstruction(s.get("instruction") != null ? s.get("instruction").toString() : "");
                            step.setLine(s.get("line") != null ? s.get("line").toString() : "");
                            step.setStations(s.get("stations") != null ? ((Number) s.get("stations")).intValue() : 0);
                            step.setStartStation(s.get("startStation") != null ? s.get("startStation").toString() : "");
                            step.setEndStation(s.get("endStation") != null ? s.get("endStation").toString() : "");
                            step.setDuration(s.get("duration") != null ? ((Number) s.get("duration")).intValue() : 0);
                            steps.add(step);
                        }
                        info.setSteps(steps);
                    }
                    
                    lastTransportation.add(info);
                }
                System.out.println("AI 返回 " + lastTransportation.size() + " 条交通信息");
            } catch (Exception e) {
                System.err.println("解析交通信息失败: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        if (selectedIndices == null || selectedIndices.isEmpty()) {
            return null;
        }
        
        // 构建推荐结果
        List<SpotRecommend> result = new ArrayList<>();
        int order = 1;
        int totalTime = 0;
        
        for (Integer index : selectedIndices) {
            if (index < 1 || index > availableSpots.size()) continue;
            
            Spot spot = availableSpots.get(index - 1);
            
            // 检查时间限制
            int playTime = spot.getRecommendedDuration() != null ? spot.getRecommendedDuration() : 60;
            if (totalTime + playTime > request.getPlayTime()) {
                break;
            }
            
            SpotRecommend recommend = new SpotRecommend();
            recommend.setId(spot.getId());
            recommend.setName(spot.getName());
            recommend.setDescription(spot.getDescription());
            recommend.setImageUrl(spot.getImageUrl());
            recommend.setPlayTime(playTime);
            recommend.setPriceMin(spot.getTicketPrice() != null ? spot.getTicketPrice().doubleValue() : 0);
            recommend.setPriceMax(spot.getTicketPrice() != null ? spot.getTicketPrice().doubleValue() : 0);
            recommend.setLatitude(spot.getLatitude() != null ? spot.getLatitude().doubleValue() : null);
            recommend.setLongitude(spot.getLongitude() != null ? spot.getLongitude().doubleValue() : null);
            recommend.setOrder(order++);
            
            // 设置 AI 生成的推荐理由
            String reason = reasons != null ? reasons.get(spot.getName()) : null;
            recommend.setReason(reason != null ? reason : generateDefaultReason(spot, request.getAge()));
            
            result.add(recommend);
            totalTime += playTime;
        }
        
        return result.isEmpty() ? null : result;
    }

    private List<Spot> filterSpotsByConditions(RecommendRequest request) {
        List<Spot> allSpots = spotRepository.findByRegionId(request.getRegionId());

        return allSpots.stream()
                .filter(spot -> isAgeSuitable(spot, request.getAge()))
                .filter(spot -> spot.getRecommendedDuration() != null && spot.getRecommendedDuration() <= request.getPlayTime())
                .filter(spot -> isBudgetSuitable(spot, request.getBudget(), request.getPeopleCount()))
                .filter(spot -> isPreferenceSuitable(spot, request.getPreference()))
                .sorted(Comparator.comparing((Spot s) -> calculateSpotScore(s)).reversed())
                .collect(Collectors.toList());
    }

    private boolean isAgeSuitable(Spot spot, Integer age) {
        // 新表结构没有 ageMin/ageMax 字段，默认适合所有年龄
        return true;
    }

    private boolean isBudgetSuitable(Spot spot, Double budget, Integer peopleCount) {
        if (budget == null || budget <= 0) return true;
        if (spot.getTicketPrice() == null) return true;
        double perPersonBudget = budget / peopleCount;
        return spot.getTicketPrice().doubleValue() <= perPersonBudget * 0.5;
    }

    private boolean isPreferenceSuitable(Spot spot, String preference) {
        if (preference == null || preference.isEmpty()) return true;
        if (spot.getDescription() == null) return true;
        return spot.getDescription().contains(preference) || spot.getName().contains(preference);
    }

    private double calculateSpotScore(Spot spot) {
        double score = 50;
        if (spot.getTicketPrice() != null) {
            score += Math.max(0, 20 - (spot.getTicketPrice().doubleValue() / 50));
        }
        if (spot.getRecommendedDuration() != null && spot.getRecommendedDuration() <= 120) {
            score += 10;
        }
        return score;
    }
    
    /**
     * 生成默认推荐理由
     */
    private String generateDefaultReason(Spot spot, Integer age) {
        StringBuilder reason = new StringBuilder();
        
        if (age != null) {
            if (age < 18) {
                reason.append("适合青少年游玩，");
            } else if (age < 35) {
                reason.append("适合年轻人探索，");
            } else if (age < 55) {
                reason.append("适合中年人休闲，");
            } else {
                reason.append("适合老年人放松，");
            }
        }
        
        if (spot.getRecommendedDuration() != null) {
            if (spot.getRecommendedDuration() <= 60) {
                reason.append("游玩时间短，轻松愉快");
            } else if (spot.getRecommendedDuration() <= 120) {
                reason.append("游玩时间适中，体验丰富");
            } else {
                reason.append("可深度游玩，值得细细品味");
            }
        }
        
        return reason.length() > 0 ? reason.toString() : "值得一游的好去处";
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
            if (spot.getRecommendedDuration() == null) continue;
            
            // 根据年龄调整实际游玩时间
            int adjustedPlayTime = (int) (spot.getRecommendedDuration() * paceMultiplier);
            
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
            to.getLatitude() != null ? to.getLatitude().doubleValue() : null, 
            to.getLongitude() != null ? to.getLongitude().doubleValue() : null
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
                from.getLatitude() != null ? from.getLatitude().doubleValue() : null, 
                from.getLongitude() != null ? from.getLongitude().doubleValue() : null,
                spot.getLatitude() != null ? spot.getLatitude().doubleValue() : null, 
                spot.getLongitude() != null ? spot.getLongitude().doubleValue() : null
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
        sr.setPlayTime(spot.getRecommendedDuration());
        sr.setPriceMin(spot.getTicketPrice() != null ? spot.getTicketPrice().doubleValue() : null);
        sr.setPriceMax(spot.getTicketPrice() != null ? spot.getTicketPrice().doubleValue() : null);
        sr.setLatitude(spot.getLatitude() != null ? spot.getLatitude().doubleValue() : null);
        sr.setLongitude(spot.getLongitude() != null ? spot.getLongitude().doubleValue() : null);
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

        if (spot.getRecommendedDuration() != null && spot.getRecommendedDuration() <= 60) reason.append("游玩时间适中");
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
