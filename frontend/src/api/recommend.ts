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

// 交通详细步骤（类似高德地图）
export interface TransportStep {
  type: string;          // 步行/公交/地铁
  instruction: string;   // 详细指引
  line: string;          // 公交或地铁线路名
  stations: number;      // 乘坐站数
  startStation: string;  // 上车站名
  endStation: string;    // 下车站名
  duration: number;      // 该步骤时间（分钟）
}

// AI 交通信息
export interface TransportInfo {
  from: string;          // 起点景点名
  to: string;            // 终点景点名
  method: string;        // 交通方式：步行/公交/地铁/打车
  duration: number;      // 预计时间（分钟）
  distance: number;      // 预计距离（公里）
  cost: number;          // 预计费用（元）
  steps?: TransportStep[]; // 详细步骤
  tips: string;          // 交通建议
}

// 智能推荐响应
export interface SmartRecommendResponse {
  spots: SpotRecommend[];
  route: RouteInfo;
  cost: CostEstimate;
  summary: string;
  transportation?: TransportInfo[];  // AI 推荐的交通信息
  aiPowered?: boolean;               // 是否为 AI 推荐
}

// 智能推荐接口
export function smartRecommend(request: SmartRecommendRequest) {
  return api.post<SmartRecommendResponse>('/recommend/smart', request);
}

// 简单推荐(保留原有接口)
export function recommend(userId: number, age: number, time: number) {
  return api.get<Spot[]>('/recommend', { params: { userId, age, time } });
}

// 获取推荐历史
export function recommendHistory(userId: number) {
  return api.get('/recommend/history/' + userId);
}

// 获取热门景点
export function getPopularSpots(limit: number = 10) {
  return api.get<Spot[]>('/recommend/popular', { params: { limit } });
}

// 保存的行程
export interface SavedItinerary {
  id: number;
  userId: number;
  name: string;
  itineraryData: string;
  createTime: string;
  updateTime: string;
}

// 保存行程
export function saveItinerary(userId: number, name: string, itineraryData: string) {
  return api.post('/recommend/itinerary/save', { userId, name, itineraryData });
}

// 获取用户保存的行程
export function getUserItineraries(userId: number) {
  return api.get<SavedItinerary[]>(`/recommend/itinerary/${userId}`);
}

// 删除行程
export function deleteItinerary(id: number) {
  return api.delete(`/recommend/itinerary/${id}`);
}
