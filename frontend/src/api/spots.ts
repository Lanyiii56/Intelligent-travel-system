import api from './request';

// 景点类型定义
export interface Spot {
  id: number;
  name: string;
  description: string;
  regionId: number;
  priceMin: number;
  priceMax: number;
  openTime: string;
  playTime: number;
  ageMin: number;
  ageMax: number;
  latitude: number;
  longitude: number;
  imageUrl: string;
  rating?: number;
  images?: string[];
}

// 分页响应
export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

// 搜索参数
export interface SpotSearchParams {
  keyword?: string;
  regionId?: number;
  age?: number;
  time?: number;
  minPrice?: number;
  maxPrice?: number;
  page?: number;
  size?: number;
}

export function listSpots() {
  return api.get<Spot[]>('/spots');
}

export function getSpot(id: number) {
  return api.get<Spot>(`/spots/${id}`);
}

export function listByRegion(regionId: number) {
  return api.get<Spot[]>(`/spots/region/${regionId}`);
}

export function filterSpots(age: number, time: number) {
  return api.get<Spot[]>('/spots/filter', { params: { age, time } });
}

// 搜索景点（支持关键词、地区、分页）
export function searchSpots(params: SpotSearchParams) {
  return api.get<Spot[]>('/spots/search', { params });
}

// 获取热门景点
export function getHotSpots(limit: number = 10) {
  return api.get<Spot[]>('/spots/hot', { params: { limit } });
}

// 获取景点评论统计
export function getSpotStats(spotId: number) {
  return api.get<{ avgRating: number; commentCount: number }>(`/spots/${spotId}/stats`);
}

