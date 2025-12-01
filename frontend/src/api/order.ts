// src/api/order.ts
import api from './request';

export function createOrder(userId: number, spotId: number, amount: number) {
  return api.post('/order/create', null, { params: { userId, spotId, amount } });
}

export function payOrder(orderId: number) {
  return api.post(`/order/pay/${orderId}`);
}

export function listOrders(userId: number) {
  return api.get(`/order/list/${userId}`);
}
