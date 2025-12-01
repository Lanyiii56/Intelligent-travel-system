// src/api/recommend.ts
import api from './request';
import type { Spot } from './spots';

// 智能推荐请求参数
export interface SmartRecommendRequest {
  regionId: number;
  age: number;
  playTime: number; // 分钟
  peopleCount: number;
  budget?: number;
  preference?: string;
}

// 推荐景点
export interface SpotRecommend {
  id: number;
  name: string;
  description: string;
  imageUrl: string;
  playTime: number;
  priceMin: number;
  priceMax: number;
  reason: string;
  latitude: number;
  longitude: number;
  order: number;
}

// 路线点
export interface RoutePoint {
  spotId: number;
  spotName: string;
  latitude: number;
  longitude: number;
  order: number;
  stayTime: number;
  travelTime: number;
}

// 路线信息
export interface RouteInfo {
  points: RoutePoint[];
  totalTime: number;
  totalDistance: number;
  suggestion: string;
}

// 费用预估
export interface CostEstimate {
  ticketCost: number;
  transportCost: number;
  mealCost: number;
  totalCost: number;
  perPersonCost: number;
  breakdown: string;
}

// 智能推荐响应
export interface SmartRecommendResponse {
  spots: SpotRecommend[];
  route: RouteInfo;
  cost: CostEstimate;
  summary: string;
}

// 智能推荐接口
export function smartRecommend(request: SmartRecommendRequest) {
  return api.post<SmartRecommendResponse>('/recommend/smart', request);
}

// 简单推荐(保留原有接口)
export function recommend(userId: number, age: number, time: number) {
  return api.get<Spot[]>('/recommend', { params: { userId, age, time } });
}

export function recommendHistory(userId: number) {
  return api.get('/recommend/history/' + userId);
}
