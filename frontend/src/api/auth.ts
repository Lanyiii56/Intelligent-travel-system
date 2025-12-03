// src/api/auth.ts
import api from './request';

export interface LoginReq { 
  username: string; 
  password: string; 
}

export interface RegisterReq { 
  username: string; 
  password: string; 
  nickname?: string; 
  age?: number;
  gender?: string;
}

export interface LoginRes {
  token: string;
  user: {
    id: number;
    username: string;
    nickname: string;
    avatarUrl?: string;
  };
}

export interface RegisterRes {
  id: number;
  username: string;
  nickname: string;
}

export function login(req: LoginReq) {
  return api.post<LoginRes>('/auth/login', req);
}

export function register(req: RegisterReq) {
  return api.post<RegisterRes>('/auth/register', req);
}

// 更新用户资料
export interface UpdateProfileReq {
  nickname?: string;
  age?: number;
  gender?: string;
  motto?: string;
}

export function updateProfile(userId: number, req: UpdateProfileReq) {
  return api.put(`/auth/profile/${userId}`, req);
}

// 获取用户详细信息
export interface UserDetailRes {
  id: number;
  username: string;
  nickname: string;
  avatarUrl?: string;
  age?: number;
  gender?: string;
  createTime?: string;
}

export function getUserInfo(userId: number) {
  return api.get<UserDetailRes>(`/auth/user/${userId}`);
}

// 更新头像
export function updateAvatar(userId: number, avatarUrl: string) {
  return api.put(`/auth/avatar/${userId}`, { avatarUrl });
}
