/**
 * 模块D: 订单社交模块 - 路由配置
 * 负责人: 成员4
 */
import type { RouteRecordRaw } from 'vue-router';

const orderRoutes: RouteRecordRaw[] = [
  {
    path: '/reservation',
    name: 'Reservation',
    component: () => import('@/pages/Reservation.vue'),
    meta: { title: '我的预订', requiresAuth: true }
  },
];

export default orderRoutes;
