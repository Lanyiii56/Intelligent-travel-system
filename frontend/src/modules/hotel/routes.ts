/**
 * 酒店模块路由
 */
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/hotels',
    name: 'Hotels',
    component: () => import('@/pages/Hotels.vue'),
    meta: { title: '酒店预订' }
  },
  {
    path: '/hotels/:id',
    name: 'HotelDetail',
    component: () => import('@/pages/HotelDetail.vue'),
    meta: { title: '酒店详情' }
  }
]

export default routes
