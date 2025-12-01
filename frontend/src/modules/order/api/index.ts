/**
 * 模块D: 订单社交模块 - API
 * 负责人: 成员4
 */
import api from '@/api';

// ============ 类型定义 ============
export type OrderStatus = 'pending' | 'paid' | 'confirmed' | 'completed' | 'cancelled';

export interface Order {
  id: number;
  userId: number;
  spotId: number;
  spotName: string;
  spotImage?: string;
  visitDate: string;
  peopleCount: number;
  totalPrice: number;
  status: OrderStatus;
  contactName: string;
  contactPhone: string;
  remark?: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateOrderRequest {
  spotId: number;
  visitDate: string;
  peopleCount: number;
  contactName: string;
  contactPhone: string;
  remark?: string;
}

export interface Comment {
  id: number;
  userId: number;
  username: string;
  avatar?: string;
  spotId: number;
  content: string;
  rating: number;        // 评分 1-5
  images?: string[];
  createdAt: string;
  likes: number;
}

export interface CreateCommentRequest {
  spotId: number;
  content: string;
  rating: number;
  images?: string[];
}

export interface Favorite {
  id: number;
  userId: number;
  spotId: number;
  spotName: string;
  spotImage: string;
  createdAt: string;
}

// ============ 订单 API ============

/** 创建订单 */
export const createOrder = (data: CreateOrderRequest) => {
  return api.post<Order>('/orders', data);
};

/** 获取订单列表 */
export const getOrders = (userId: number) => {
  return api.get<Order[]>(`/orders/user/${userId}`);
};

/** 获取订单详情 */
export const getOrderById = (id: number) => {
  return api.get<Order>(`/orders/${id}`);
};

/** 取消订单 */
export const cancelOrder = (id: number) => {
  return api.put(`/orders/${id}/cancel`);
};

/** 支付订单 */
export const payOrder = (id: number) => {
  return api.put(`/orders/${id}/pay`);
};

// ============ 评论 API ============

/** 获取景点评论 */
export const getComments = (spotId: number) => {
  return api.get<Comment[]>(`/social/comments/${spotId}`);
};

/** 发表评论 */
export const createComment = (data: CreateCommentRequest) => {
  return api.post<Comment>('/social/comments', data);
};

/** 删除评论 */
export const deleteComment = (id: number) => {
  return api.delete(`/social/comments/${id}`);
};

/** 点赞评论 */
export const likeComment = (id: number) => {
  return api.post(`/social/comments/${id}/like`);
};

// ============ 收藏 API ============

/** 获取收藏列表 */
export const getFavorites = (userId: number) => {
  return api.get<Favorite[]>(`/social/favorites/${userId}`);
};

/** 添加收藏 */
export const addFavorite = (spotId: number) => {
  return api.post('/social/favorites', { spotId });
};

/** 取消收藏 */
export const removeFavorite = (spotId: number) => {
  return api.delete(`/social/favorites/${spotId}`);
};

/** 检查是否已收藏 */
export const checkFavorite = (spotId: number) => {
  return api.get<boolean>(`/social/favorites/check/${spotId}`);
};
