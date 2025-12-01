/**
 * 模块A: 用户认证模块 - 路由配置
 * 负责人: 成员1
 */
import type { RouteRecordRaw } from 'vue-router';

const authRoutes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/pages/Login.vue'),
    meta: { title: '登录', guest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/pages/Login.vue'),
    meta: { title: '注册', guest: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/pages/Profile.vue'),
    meta: { title: '个人中心', requiresAuth: true }
  },
];

export default authRoutes;
