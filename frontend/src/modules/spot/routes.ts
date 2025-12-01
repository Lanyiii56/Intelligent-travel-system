/**
 * 模块B: 景点管理模块 - 路由配置
 * 负责人: 成员2
 */
import type { RouteRecordRaw } from 'vue-router';

const spotRoutes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/pages/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/spots',
    name: 'Spots',
    component: () => import('@/pages/Spots.vue'),
    meta: { title: '景点列表' }
  },
  {
    path: '/spots/:id',
    name: 'SpotDetail',
    component: () => import('@/pages/SpotDetail.vue'),
    meta: { title: '景点详情' }
  },
];

export default spotRoutes;
