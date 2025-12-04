// src/api/order.ts
import api from './request';

export interface CreateOrderParams {
  userId: number;
  spotId: number;
  amount: number;
  visitDate?: string;
  visitorCount?: number;
  contactName?: string;
  contactPhone?: string;
}

export function createOrder(params: CreateOrderParams) {
  return api.post('/order/create', params);
}

export function payOrder(orderId: number) {
  return api.post(`/order/pay/${orderId}`);
}

export function cancelOrder(orderId: number) {
  return api.post(`/order/cancel/${orderId}`);
}

export function getOrder(orderId: number) {
  return api.get(`/order/${orderId}`);
}

export function listOrders(userId: number) {
  return api.get(`/order/list/${userId}`);
}
