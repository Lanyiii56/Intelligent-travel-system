/**
 * 模块A: 用户认证模块 - 状态管理
 * 负责人: 成员1
 */
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import type { User } from '../api';
import * as authApi from '../api';

const TOKEN_KEY = 'travel_token';
const USER_KEY = 'travel_user';

export const useAuthStore = defineStore('auth', () => {
  // ============ 状态 ============
  const token = ref<string | null>(localStorage.getItem(TOKEN_KEY));
  const user = ref<User | null>(
    JSON.parse(localStorage.getItem(USER_KEY) || 'null')
  );

  // ============ 计算属性 ============
  const isLoggedIn = computed(() => !!token.value);
  const username = computed(() => user.value?.username || '');
  const nickname = computed(() => user.value?.nickname || '游客');
  const avatar = computed(() => user.value?.avatar || '');

  // ============ 方法 ============
  
  /** 登录 */
  async function login(username: string, password: string) {
    const res = await authApi.login({ username, password });
    token.value = res.data.token;
    user.value = res.data.user;
    localStorage.setItem(TOKEN_KEY, res.data.token);
    localStorage.setItem(USER_KEY, JSON.stringify(res.data.user));
    return res.data;
  }

  /** 注册 */
  async function register(data: authApi.RegisterRequest) {
    const res = await authApi.register(data);
    return res.data;
  }

  /** 获取用户信息 */
  async function fetchUser() {
    if (!token.value) return null;
    const res = await authApi.getCurrentUser();
    user.value = res.data;
    localStorage.setItem(USER_KEY, JSON.stringify(res.data));
    return res.data;
  }

  /** 更新用户信息 */
  async function updateProfile(data: Partial<User>) {
    const res = await authApi.updateProfile(data);
    user.value = res.data;
    localStorage.setItem(USER_KEY, JSON.stringify(res.data));
    return res.data;
  }

  /** 退出登录 */
  function logout() {
    token.value = null;
    user.value = null;
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
  }

  return {
    // 状态
    token,
    user,
    // 计算属性
    isLoggedIn,
    username,
    nickname,
    avatar,
    // 方法
    login,
    register,
    fetchUser,
    updateProfile,
    logout,
  };
});
