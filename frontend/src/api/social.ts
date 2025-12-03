// src/api/social.ts
import api from './request';

export function toggleFavorite(userId: number, spotId: number) {
  return api.post('/social/favorite', null, { params: { userId, spotId } });
}

export function favoriteList(userId: number) {
  return api.get(`/social/favorite/${userId}`);
}

export function addComment(userId: number, spotId: number, content: string, rating: number = 5, userName?: string) {
  return api.post('/social/comment', null, { params: { userId, spotId, content, rating, userName } });
}

export function listComments(spotId: number) {
  return api.get(`/social/comment/${spotId}`);
}

// 点赞评论
export function likeComment(commentId: number) {
  return api.post(`/social/comment/${commentId}/like`);
}

// 回复评论
export function replyComment(parentId: number, userId: number, spotId: number, content: string, userName?: string) {
  return api.post(`/social/comment/${parentId}/reply`, null, { params: { userId, spotId, content, userName } });
}
