// src/api/auth.ts
import api from './request';

export interface LoginReq { username: string; password: string; }
export interface RegisterReq { username: string; password: string; nickname?: string; age?: number; }

export function login(req: LoginReq) {
  return api.post('/auth/login', req);
}

export function register(req: RegisterReq) {
  return api.post('/auth/register', req);
}
