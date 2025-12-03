/**
 * 美食评论 API
 */
import request from './index';

export interface FoodComment {
  id: number;
  foodId: number;
  userId: number;
  userName?: string;
  rating: number;
  content: string;
  likes: number;
  isLiked?: boolean;
  parentId?: number;
  replyToUserId?: number;
  replyToUser?: string;
  replies?: FoodComment[];
  createdAt: string;
}

export interface AddCommentReq {
  userId: number;
  userName?: string;
  rating: number;
  content: string;
}

export interface ReplyCommentReq {
  userId: number;
  userName?: string;
  content: string;
}

/**
 * 获取美食评论列表
 */
export function getFoodComments(foodId: number) {
  return request.get<FoodComment[]>(`/foods/${foodId}/comments`);
}

/**
 * 添加美食评论
 */
export function addFoodComment(foodId: number, data: AddCommentReq) {
  return request.post(`/foods/${foodId}/comments`, data);
}

/**
 * 点赞评论
 */
export function likeFoodComment(commentId: number) {
  return request.post(`/food-comments/${commentId}/like`);
}

/**
 * 回复评论
 */
export function replyFoodComment(commentId: number, data: ReplyCommentReq) {
  return request.post(`/food-comments/${commentId}/reply`, data);
}

/**
 * 获取评论的回复列表
 */
export function getFoodCommentReplies(commentId: number) {
  return request.get<FoodComment[]>(`/food-comments/${commentId}/replies`);
}

/**
 * 删除评论
 */
export function deleteFoodComment(commentId: number) {
  return request.delete(`/food-comments/${commentId}`);
}
