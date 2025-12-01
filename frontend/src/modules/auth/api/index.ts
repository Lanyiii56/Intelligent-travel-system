/**
 * 模块A: 用户认证模块 - API
 * 负责人: 成员1
 */
import api from '@/api';

// ============ 类型定义 ============
export interface User {
  id: number;
  username: string;
  nickname: string;
  avatar?: string;
  age?: number;
  gender?: string;
  email?: string;
  phone?: string;
  createdAt?: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  user: User;
}

export interface RegisterRequest {
  username: string;
  password: string;
  nickname: string;
  email?: string;
}

// ============ API 接口 ============

/** 用户登录 */
export const login = (data: LoginRequest) => {
  return api.post<LoginResponse>('/auth/login', data);
};

/** 用户注册 */
export const register = (data: RegisterRequest) => {
  return api.post<User>('/auth/register', data);
};

/** 获取当前用户信息 */
export const getCurrentUser = () => {
  return api.get<User>('/auth/me');
};

/** 更新用户信息 */
export const updateProfile = (data: Partial<User>) => {
  return api.put<User>('/auth/profile', data);
};

/** 修改密码 */
export const changePassword = (oldPassword: string, newPassword: string) => {
  return api.post('/auth/change-password', { oldPassword, newPassword });
};

/** 上传头像 */
export const uploadAvatar = (file: File) => {
  const formData = new FormData();
  formData.append('file', file);
  return api.post<{ url: string }>('/auth/avatar', formData);
};

/** 退出登录 */
export const logout = () => {
  return api.post('/auth/logout');
};
