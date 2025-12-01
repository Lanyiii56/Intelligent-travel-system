/**
 * 模块B: 景点管理模块 - API
 * 负责人: 成员2
 */
import api from '@/api';

// ============ 类型定义 ============
export interface Spot {
  id: number;
  name: string;
  description: string;
  regionId: number;
  priceMin: number;
  priceMax: number;
  openTime: string;
  playTime: number;      // 游玩时长(分钟)
  ageMin: number;
  ageMax: number;
  latitude: number;
  longitude: number;
  imageUrl: string;
  rating?: number;
  commentCount?: number;
}

export interface Region {
  id: number;
  name: string;
  parentId?: number;
  type: 'province' | 'city' | 'district';
  children?: Region[];
}

export interface SpotFilter {
  regionId?: number;
  keyword?: string;
  priceMin?: number;
  priceMax?: number;
  ageMin?: number;
  ageMax?: number;
  page?: number;
  size?: number;
}

export interface PageResult<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

// ============ 景点 API ============

/** 获取景点列表 */
export const getSpots = (params?: SpotFilter) => {
  return api.get<Spot[]>('/spots', { params });
};

/** 获取景点详情 */
export const getSpotById = (id: number) => {
  return api.get<Spot>(`/spots/${id}`);
};

/** 按地区获取景点 */
export const getSpotsByRegion = (regionId: number) => {
  return api.get<Spot[]>(`/spots/region/${regionId}`);
};

/** 搜索景点 */
export const searchSpots = (keyword: string) => {
  return api.get<Spot[]>('/spots/search', { params: { keyword } });
};

/** 获取热门景点 */
export const getHotSpots = (limit = 10) => {
  return api.get<Spot[]>('/spots/hot', { params: { limit } });
};

// ============ 地区 API ============

/** 获取所有地区 */
export const getAllRegions = () => {
  return api.get<Region[]>('/regions');
};

/** 获取省份列表 */
export const getProvinces = () => {
  return api.get<Region[]>('/regions/provinces');
};

/** 获取城市列表 */
export const getCities = (provinceId: number) => {
  return api.get<Region[]>(`/regions/${provinceId}/cities`);
};

/** 获取区县列表 */
export const getDistricts = (cityId: number) => {
  return api.get<Region[]>(`/regions/${cityId}/areas`);
};
