/**
 * 公共模块: 路由配置
 * 维护人: 全员共同维护
 * 
 * ⚠️ 注意: 此文件整合各模块路由，一般不需要直接修改
 *         请在各自模块的 routes.ts 中添加路由
 */
import { createRouter, createWebHistory } from 'vue-router'

// 导入各模块路由
import authRoutes from '@/modules/auth/routes'
import spotRoutes from '@/modules/spot/routes'
import recommendRoutes from '@/modules/recommend/routes'
import orderRoutes from '@/modules/order/routes'

// 合并所有路由
const routes = [
  ...spotRoutes,       // 成员2: 首页、景点列表、详情
  ...authRoutes,       // 成员1: 登录、注册、个人中心
  ...recommendRoutes,  // 成员3: 推荐、智能推荐
  ...orderRoutes,      // 成员4: 预订、收藏
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 路由守卫：需要登录的页面检查 token
router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('travel_token')
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
