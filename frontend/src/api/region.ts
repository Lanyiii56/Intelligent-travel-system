// src/api/region.ts
import api from './request';

// 地区类型定义（扁平结构）
export interface Region {
  id: number;
  name: string;
  description?: string;
  imageUrl?: string;
  status?: string;
}

// 获取所有地区
export function getAllRegions() {
  return api.get<Region[]>('/regions');
}

// 根据ID获取地区
export function getRegionById(id: number) {
  return api.get<Region>(`/regions/${id}`);
}
