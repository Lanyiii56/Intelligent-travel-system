/**
 * 模块C: 智能推荐模块 - 路由配置
 * 负责人: 成员3
 */
import type { RouteRecordRaw } from 'vue-router';

const recommendRoutes: RouteRecordRaw[] = [
  {
    path: '/recommend',
    name: 'Recommend',
    component: () => import('@/pages/Recommend.vue'),
    meta: { title: '旅游推荐' }
  },
  {
    path: '/smart-recommend',
    name: 'SmartRecommend',
    component: () => import('@/pages/SmartRecommend.vue'),
    meta: { title: '智能推荐' }
  },
];

export default recommendRoutes;
