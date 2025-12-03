/**
 * 模块: 消息社交模块 - 路由配置
 */
import type { RouteRecordRaw } from 'vue-router';

const messageRoutes: RouteRecordRaw[] = [
  {
    path: '/messages',
    name: 'Messages',
    component: () => import('@/pages/Messages.vue'),
    meta: { title: '消息', requiresAuth: true }
  },
  {
    path: '/chat/:userId',
    name: 'Chat',
    component: () => import('@/pages/Chat.vue'),
    meta: { title: '聊天', requiresAuth: true }
  },
  {
    path: '/user/:userId',
    name: 'UserProfile',
    component: () => import('@/pages/UserProfile.vue'),
    meta: { title: '用户主页' }
  },
];

export default messageRoutes;
