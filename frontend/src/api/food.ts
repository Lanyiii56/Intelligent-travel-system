/**
 * 美食相关 API
 */
import request from './index';

export interface Food {
  id: number;
  name: string;
  description: string;
  regionId: number;
  category: string;
  priceMin: number;
  priceMax: number;
  address: string;
  openTime: string;
  diningTime: number;
  rating: number;
  latitude: number;
  longitude: number;
  imageUrl: string;
  tags: string;
}

/**
 * 获取所有美食
 */
export function getAllFoods() {
  return request.get<Food[]>('/foods');
}

/**
 * 根据ID获取美食详情
 */
export function getFoodById(id: number) {
  return request.get<Food>(`/foods/${id}`);
}

/**
 * 根据地区获取美食列表
 */
export function getFoodsByRegion(regionId: number) {
  return request.get<Food[]>(`/foods/region/${regionId}`);
}

/**
 * 根据地区和类别获取美食
 */
export function getFoodsByRegionAndCategory(regionId: number, category: string) {
  return request.get<Food[]>(`/foods/region/${regionId}/category/${category}`);
}

/**
 * 为行程推荐美食
 */
export function recommendFoods(regionId: number, budget?: number, count: number = 3) {
  return request.get<Food[]>('/foods/recommend', {
    params: { regionId, budget, count }
  });
}
