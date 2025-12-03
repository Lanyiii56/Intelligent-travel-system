/**
 * 认证状态管理 composable
 * 统一使用 travel_token 作为 token key
 * 模块: auth (成员1)
 */
import { ref, computed } from 'vue';
import router from '@/router';

const TOKEN_KEY = 'travel_token';
const USER_KEY = 'travel_user';

// 用户信息类型
export interface UserInfo {
  id: number;
  username: string;
  nickname: string;
  avatarUrl?: string;
}

const token = ref<string | null>(localStorage.getItem(TOKEN_KEY));
const user = ref<UserInfo | null>(
  localStorage.getItem(USER_KEY) 
    ? JSON.parse(localStorage.getItem(USER_KEY)!) 
    : null
);

export function useAuth() {
  // 设置 token
  function setToken(t: string) {
    token.value = t;
    localStorage.setItem(TOKEN_KEY, t);
  }
  
  // 设置用户信息
  function setUser(u: UserInfo) {
    user.value = u;
    localStorage.setItem(USER_KEY, JSON.stringify(u));
  }
  
  // 登出
  function logout() {
    token.value = null;
    user.value = null;
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    router.push('/login');
  }
  
  // 是否已登录
  const isLoggedIn = computed(() => !!token.value);
  
  // 获取用户昵称
  const nickname = computed(() => user.value?.nickname || user.value?.username || '游客');
  
  // 获取用户头像
  const avatarUrl = computed(() => user.value?.avatarUrl || '');
  
  return { 
    token, 
    user,
    setToken, 
    setUser,
    logout, 
    isLoggedIn,
    nickname,
    avatarUrl
  };
}
