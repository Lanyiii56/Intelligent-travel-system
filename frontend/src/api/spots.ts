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

