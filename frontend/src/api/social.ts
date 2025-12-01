// src/api/social.ts
import api from './request';

export function toggleFavorite(userId: number, spotId: number) {
  return api.post('/social/favorite', null, { params: { userId, spotId } });
}

export function favoriteList(userId: number) {
  return api.get(`/social/favorite/${userId}`);
}

export function addComment(userId: number, spotId: number, content: string) {
  return api.post('/social/comment', null, { params: { userId, spotId, content } });
}

export function listComments(spotId: number) {
  return api.get(`/social/comment/${spotId}`);
}
