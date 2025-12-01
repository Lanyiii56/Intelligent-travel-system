/**
 * 认证状态管理 composable
 * 统一使用 travel_token 作为 token key
 */
import { ref } from 'vue';
import router from '@/router';

const TOKEN_KEY = 'travel_token';
const token = ref<string | null>(localStorage.getItem(TOKEN_KEY));

export function useAuth() {
  function setToken(t: string) {
    token.value = t;
    localStorage.setItem(TOKEN_KEY, t);
  }
  function logout() {
    token.value = null;
    localStorage.removeItem(TOKEN_KEY);
    router.push('/login');
  }
  function isLoggedIn() {
    return !!token.value;
  }
  return { token, setToken, logout, isLoggedIn };
}
