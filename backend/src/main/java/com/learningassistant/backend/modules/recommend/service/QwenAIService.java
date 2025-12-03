package com.learningassistant.backend.modules.recommend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * 通义千问 AI 服务
 * 模块: recommend (成员3)
 */
@Service
public class QwenAIService {

    @Value("${qwen.api.key:}")
    private String apiKey;

    @Value("${qwen.api.url:https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation}")
    private String apiUrl;

    @Value("${qwen.api.model:qwen-turbo}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 调用通义千问生成旅行推荐
     */
    public String generateTravelRecommendation(String destination, int age, int playTimeMinutes, 
                                                int peopleCount, Double budget, String preference,
                                                List<Map<String, Object>> availableSpots) {
        if (apiKey == null || apiKey.isEmpty()) {
            return null; // API Key 未配置，使用默认推荐逻辑
        }

        try {
            String prompt = buildPrompt(destination, age, playTimeMinutes, peopleCount, budget, preference, availableSpots);
            return callQwenAPI(prompt);
        } catch (Exception e) {
            System.err.println("通义千问 API 调用失败: " + e.getMessage());
            return null;
        }
    }

    /**
     * 构建提示词
     */
    private String buildPrompt(String destination, int age, int playTimeMinutes, 
                               int peopleCount, Double budget, String preference,
                               List<Map<String, Object>> availableSpots) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是一个专业的旅行规划师和交通专家。请根据以下信息为用户规划一日游行程：\n\n");
        sb.append("【用户信息】\n");
        sb.append("- 目的地城市：").append(destination).append("\n");
        sb.append("- 年龄：").append(age).append("岁\n");
        sb.append("- 可用游玩时间：").append(playTimeMinutes / 60).append("小时\n");
        sb.append("- 人数：").append(peopleCount).append("人\n");
        if (budget != null && budget > 0) {
            sb.append("- 总预算：").append(budget).append("元\n");
        }
        if (preference != null && !preference.isEmpty()) {
            sb.append("- 游玩偏好：").append(preference).append("\n");
        }

        sb.append("\n【可选景点及坐标】\n");
        for (int i = 0; i < availableSpots.size(); i++) {
            Map<String, Object> spot = availableSpots.get(i);
            sb.append(i + 1).append(". ").append(spot.get("name"));
            sb.append("\n   - 游玩时间：约").append(spot.get("playTime")).append("分钟");
            sb.append("\n   - 门票：").append(spot.get("priceMin")).append("-").append(spot.get("priceMax")).append("元");
            sb.append("\n   - 坐标：(").append(spot.get("latitude")).append(", ").append(spot.get("longitude")).append(")");
            sb.append("\n   - 简介：").append(spot.get("description")).append("\n");
        }

        sb.append("\n请完成以下任务：\n");
        sb.append("1. 选择最适合用户的景点组合（考虑时间、预算、年龄）\n");
        sb.append("2. 规划最优游览顺序（考虑地理位置，减少往返）\n");
        sb.append("3. 给出详细的公共交通方案（像高德地图一样详细）\n");
        sb.append("4. 给出每个景点的个性化推荐理由\n\n");
        
        sb.append("请以JSON格式返回，格式如下：\n");
        sb.append("{\n");
        sb.append("  \"selectedSpots\": [景点序号数组，按游览顺序排列，如[2,1,4]],\n");
        sb.append("  \"reasons\": {\"景点名\": \"针对用户年龄和偏好的推荐理由\"},\n");
        sb.append("  \"transportation\": [\n");
        sb.append("    {\n");
        sb.append("      \"from\": \"起点景点名\",\n");
        sb.append("      \"to\": \"终点景点名\",\n");
        sb.append("      \"method\": \"公交/地铁/步行/打车\",\n");
        sb.append("      \"duration\": 预计总时间(分钟数字),\n");
        sb.append("      \"distance\": 预计距离(公里数字),\n");
        sb.append("      \"cost\": 预计费用(元数字),\n");
        sb.append("      \"steps\": [\n");
        sb.append("        {\n");
        sb.append("          \"type\": \"步行/公交/地铁\",\n");
        sb.append("          \"instruction\": \"详细指引，如：步行200米到XX站\",\n");
        sb.append("          \"line\": \"公交或地铁线路名，如：地铁1号线/公交101路\",\n");
        sb.append("          \"stations\": 乘坐站数(数字),\n");
        sb.append("          \"startStation\": \"上车站名\",\n");
        sb.append("          \"endStation\": \"下车站名\",\n");
        sb.append("          \"duration\": 该步骤时间(分钟数字)\n");
        sb.append("        }\n");
        sb.append("      ],\n");
        sb.append("      \"tips\": \"交通建议，如高峰期拥挤提醒等\"\n");
        sb.append("    }\n");
        sb.append("  ],\n");
        sb.append("  \"summary\": \"整体行程建议，包含时间安排和注意事项\"\n");
        sb.append("}\n");
        sb.append("注意：steps数组要包含完整的出行步骤，包括步行换乘。只返回JSON，不要其他内容。");

        return sb.toString();
    }

    /**
     * 调用通义千问 API
     */
    private String callQwenAPI(String prompt) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        
        Map<String, Object> input = new HashMap<>();
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        messages.add(userMessage);
        input.put("messages", messages);
        requestBody.put("input", input);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("result_format", "message");
        requestBody.put("parameters", parameters);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
            apiUrl,
            HttpMethod.POST,
            entity,
            String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode output = root.path("output");
            JsonNode choices = output.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                return choices.get(0).path("message").path("content").asText();
            }
        }

        return null;
    }

    /**
     * 解析 AI 返回的 JSON
     */
    public Map<String, Object> parseAIResponse(String aiResponse) {
        if (aiResponse == null || aiResponse.isEmpty()) {
            return null;
        }

        try {
            // 提取 JSON 部分（AI 可能返回额外文字）
            int start = aiResponse.indexOf("{");
            int end = aiResponse.lastIndexOf("}");
            if (start >= 0 && end > start) {
                String json = aiResponse.substring(start, end + 1);
                return objectMapper.readValue(json, Map.class);
            }
        } catch (Exception e) {
            System.err.println("解析 AI 响应失败: " + e.getMessage());
        }

        return null;
    }

    /**
     * 检查 API 是否可用
     */
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isEmpty();
    }
}
