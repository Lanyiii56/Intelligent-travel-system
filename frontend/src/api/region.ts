// src/api/region.ts
import api from './request';

// 地区类型定义
export interface Region {
  id: number;
  name: string;
  parentId: number | null;
  type: 'province' | 'city' | 'area';
}

// 获取所有省份
export function getProvinces() {
  return api.get<Region[]>('/regions/provinces');
}

// 获取省份下的城市
export function getCities(provinceId: number) {
  return api.get<Region[]>(`/regions/${provinceId}/cities`);
}

// 获取城市下的区县
export function getAreas(cityId: number) {
  return api.get<Region[]>(`/regions/${cityId}/areas`);
}

// 获取所有地区(树形结构)
export function getAllRegions() {
  return api.get<Region[]>('/regions');
}
