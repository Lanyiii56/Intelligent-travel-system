/**
 * 模块C: 智能推荐模块 - API
 * 负责人: 成员3
 */
import api from '@/api';

// ============ 类型定义 ============
export interface RecommendRequest {
  regionId: number;
  age: number;
  playTime: number;      // 游玩时间(分钟)
  peopleCount: number;   // 人数
  budget?: number;       // 预算(可选)
  preference?: string;   // 偏好(可选)
}

export interface SpotRecommend {
  id: number;
  name: string;
  description: string;
  imageUrl: string;
  playTime: number;
  order: number;         // 游玩顺序
  reason: string;        // 推荐理由
  priceMin: number;
  priceMax: number;
  latitude: number;
  longitude: number;
}

export interface RoutePoint {
  spotId: number;
  spotName: string;
  stayTime: number;      // 停留时间
  travelTime: number;    // 到下一站的交通时间
}

export interface RouteInfo {
  totalDistance: number; // 总距离(km)
  totalTime: number;     // 总时间(分钟)
  suggestion: string;    // 路线建议
  points: RoutePoint[];
}

export interface CostEstimate {
  ticketCost: number;    // 门票费用
  transportCost: number; // 交通费用
  mealCost: number;      // 餐饮费用
  totalCost: number;     // 总费用
  perPersonCost: number; // 人均费用
}

export interface RecommendResponse {
  spots: SpotRecommend[];
  route: RouteInfo;
  cost: CostEstimate;
  summary: string;
}

export interface RecommendHistory {
  id: number;
  userId: number;
  regionName: string;
  age: number;
  playTime: number;
  peopleCount: number;
  createdAt: string;
}

// ============ API 接口 ============

/** 智能推荐 */
export const smartRecommend = (data: RecommendRequest) => {
  return api.post<RecommendResponse>('/recommend/smart', data);
};

/** 基础推荐 */
export const getRecommend = (userId: number, age: number, playTime: number) => {
  return api.get('/recommend', { params: { userId, age, playTime } });
};

/** 获取推荐历史 */
export const getRecommendHistory = (userId: number) => {
  return api.get<RecommendHistory[]>(`/recommend/history/${userId}`);
};
