/**
 * 公共模块: API 基础配置
 * 维护人: 全员共同维护
 * 
 * ⚠️ 注意: 此文件为公共文件，修改前请与团队沟通
 */
import axios from 'axios';
import { ElMessage } from 'element-plus';

// 创建 axios 实例
const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器 - 添加 Token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('travel_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器 - 统一错误处理
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    const message = error.response?.data?.message || '请求失败，请稍后重试';
    
    // 401 未授权 - 跳转登录
    if (error.response?.status === 401) {
      localStorage.removeItem('travel_token');
      localStorage.removeItem('travel_user');
      window.location.href = '/login';
      return Promise.reject(error);
    }
    
    // 显示错误提示
    ElMessage.error(message);
    return Promise.reject(error);
  }
);

export default api;
