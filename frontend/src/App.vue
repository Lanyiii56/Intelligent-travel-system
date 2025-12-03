<template>
  <div id="app">
    <!-- 全局装饰元素 -->
    <div class="global-decorations">
      <span class="g-deco g-deco-1">✈️</span>
      <span class="g-deco g-deco-2">🏝️</span>
      <span class="g-deco g-deco-3">🎒</span>
      <span class="g-deco g-deco-4">🗺️</span>
      <span class="g-deco g-deco-5">⛰️</span>
      <span class="g-deco g-deco-6">🌴</span>
      <span class="g-deco g-deco-7">🚂</span>
      <span class="g-deco g-deco-8">🏰</span>
      <span class="g-deco g-deco-9">🎡</span>
      <span class="g-deco g-deco-10">🌊</span>
      <div class="g-circle g-circle-1"></div>
      <div class="g-circle g-circle-2"></div>
      <div class="g-circle g-circle-3"></div>
      <div class="g-circle g-circle-4"></div>
    </div>
    
    <!-- 新颖的导航栏 -->
    <header class="header" :class="{ 'header-scrolled': isScrolled }">
      <div class="header-content">
        <!-- Logo 区域 -->
        <router-link to="/" class="logo-wrapper">
          <div class="logo-container">
            <div class="logo-globe">
              <span class="globe-icon">🌍</span>
              <div class="globe-ring"></div>
            </div>
            <div class="logo-text-wrapper">
              <span class="logo-text">智游天下</span>
              <span class="logo-slogan">探索世界的美好</span>
            </div>
          </div>
        </router-link>
        
        <!-- 中间导航区域 -->
        <nav class="nav-container">
          <div class="nav-track">
            <div class="nav-indicator" :style="indicatorStyle"></div>
            <router-link 
              v-for="item in navItems" 
              :key="item.path" 
              :to="item.path" 
              class="nav-item"
              :class="{ active: isActive(item.path) }"
              @mouseenter="handleNavHover(item, $event)"
              @mouseleave="handleNavLeave"
            >
              <span class="nav-item-icon">{{ item.icon }}</span>
              <span class="nav-item-text">{{ item.name }}</span>
              <span class="nav-item-badge" v-if="item.badge">{{ item.badge }}</span>
            </router-link>
          </div>
        </nav>
        
        <!-- 右侧操作区 -->
        <div class="header-actions">
          <!-- 搜索按钮 -->
          <button class="action-btn search-btn" @click="toggleSearch" title="搜索">
            <span class="btn-icon">🔍</span>
          </button>
          
          <!-- 通知按钮 -->
          <button class="action-btn notify-btn" v-if="authStore.isLoggedIn" title="通知">
            <span class="btn-icon">🔔</span>
            <span class="notify-dot" v-if="hasNotification"></span>
          </button>
          
          <!-- 用户区域 -->
          <div class="user-area" v-if="authStore.isLoggedIn">
            <div class="user-avatar-wrapper" @click="toggleUserMenu">
              <img 
                :src="authStore.user?.avatar || 'https://api.dicebear.com/7.x/avataaars/svg?seed=' + authStore.user?.username" 
                class="user-avatar"
                alt="头像"
              />
              <div class="avatar-status online"></div>
            </div>
            <!-- 用户下拉菜单 -->
            <transition name="dropdown">
              <div class="user-dropdown" v-if="showUserMenu" @mouseleave="showUserMenu = false">
                <div class="dropdown-header">
                  <img 
                    :src="authStore.user?.avatar || 'https://api.dicebear.com/7.x/avataaars/svg?seed=' + authStore.user?.username" 
                    class="dropdown-avatar"
                  />
                  <div class="dropdown-user-info">
                    <span class="dropdown-username">{{ authStore.user?.nickname || authStore.user?.username }}</span>
                    <span class="dropdown-motto">{{ authStore.user?.motto || '探索世界，发现美好' }}</span>
                  </div>
                </div>
                <div class="dropdown-divider"></div>
                <router-link to="/profile" class="dropdown-item" @click="showUserMenu = false">
                  <span>👤</span> 个人中心
                </router-link>
                <router-link to="/profile?tab=favorites" class="dropdown-item" @click="showUserMenu = false">
                  <span>❤️</span> 我的收藏
                </router-link>
                <router-link to="/profile?tab=orders" class="dropdown-item" @click="showUserMenu = false">
                  <span>📋</span> 我的订单
                </router-link>
                <router-link to="/profile?tab=itineraries" class="dropdown-item" @click="showUserMenu = false">
                  <span>🗺️</span> 我的行程
                </router-link>
                <div class="dropdown-divider"></div>
                <button class="dropdown-item logout-item" @click="handleLogout">
                  <span>🚪</span> 退出登录
                </button>
              </div>
            </transition>
          </div>
          
          <!-- 未登录状态 -->
          <div class="auth-buttons" v-else>
            <router-link to="/login" class="auth-btn login-btn">
              <span>登录</span>
            </router-link>
            <router-link to="/register" class="auth-btn register-btn">
              <span>注册</span>
            </router-link>
          </div>
        </div>
      </div>
      
      <!-- 搜索框展开区域 -->
      <transition name="search-expand">
        <div class="search-overlay" v-if="showSearch" @click.self="showSearch = false">
          <div class="search-container">
            <div class="search-input-wrapper">
              <span class="search-icon">🔍</span>
              <input 
                type="text" 
                v-model="searchKeyword" 
                placeholder="搜索景点、美食、酒店..."
                class="search-input"
                @keyup.enter="handleSearch"
                ref="searchInput"
              />
              <button class="search-close" @click="showSearch = false">✕</button>
            </div>
            <div class="search-suggestions" v-if="searchKeyword">
              <div class="suggestion-item" @click="searchByType('spot')">
                <span>🏞️</span> 在景点中搜索 "{{ searchKeyword }}"
              </div>
              <div class="suggestion-item" @click="searchByType('food')">
                <span>🍜</span> 在美食中搜索 "{{ searchKeyword }}"
              </div>
              <div class="suggestion-item" @click="searchByType('hotel')">
                <span>🏨</span> 在酒店中搜索 "{{ searchKeyword }}"
              </div>
            </div>
            <div class="search-hot" v-else>
              <span class="hot-title">🔥 热门搜索</span>
              <div class="hot-tags">
                <span class="hot-tag" @click="searchKeyword = '西湖'">西湖</span>
                <span class="hot-tag" @click="searchKeyword = '故宫'">故宫</span>
                <span class="hot-tag" @click="searchKeyword = '火锅'">火锅</span>
                <span class="hot-tag" @click="searchKeyword = '民宿'">民宿</span>
                <span class="hot-tag" @click="searchKeyword = '亲子游'">亲子游</span>
              </div>
            </div>
          </div>
        </div>
      </transition>
    </header>
    <main class="main-content">
      <router-view v-slot="{ Component, route }">
        <keep-alive :include="['SmartRecommend', 'Profile']">
          <component :is="Component" :key="route.path" />
        </keep-alive>
      </router-view>
    </main>
    <footer class="footer">
      <!-- 页脚链接区 -->
      <div class="footer-links">
        <a href="#">联系客服</a>
        <span class="divider">|</span>
        <a href="#">开放平台</a>
        <span class="divider">|</span>
        <a href="#">法律声明</a>
        <span class="divider">|</span>
        <a href="#">廉正举报</a>
        <span class="divider">|</span>
        <a href="#">隐私政策</a>
      </div>
      
      <!-- 版权信息 -->
      <div class="footer-copyright">
        <span>智游天下版权所有 2024-现在</span>
        <span class="divider">|</span>
        <span>增值电信业务经营许可证：川B2-20240001</span>
        <span class="divider">|</span>
        <span>川公网安备 51000002000001号</span>
      </div>
      
      <!-- 合作伙伴 -->
      <div class="footer-partners">
        <a href="#">携程旅行</a>
        <span class="divider">|</span>
        <a href="#">去哪儿网</a>
        <span class="divider">|</span>
        <a href="#">飞猪旅行</a>
        <span class="divider">|</span>
        <a href="#">马蜂窝</a>
        <span class="divider">|</span>
        <a href="#">途牛旅游</a>
        <span class="divider">|</span>
        <a href="#">同程旅行</a>
        <span class="divider">|</span>
        <a href="#">高德地图</a>
        <span class="divider">|</span>
        <a href="#">美团</a>
      </div>
      
      <!-- 认证图标 -->
      <div class="footer-badges">
        <div class="badge-item">
          <img src="https://img.alicdn.com/tfs/TB1..50QpXXXXX7XpXXXXXXXXXX-40-40.png" alt="诚信网站" />
          <span>诚信网站</span>
        </div>
        <div class="badge-item">
          <img src="https://img.alicdn.com/tfs/TB1..50QpXXXXX7XpXXXXXXXXXX-40-40.png" alt="网警" />
          <span>网上有害信息举报</span>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useAuthStore } from '@/modules/auth/store';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

// 导航项配置
interface NavItem {
  path: string;
  name: string;
  icon: string;
  badge?: string;
}

const navItems: NavItem[] = [
  { path: '/', name: '首页', icon: '🏠' },
  { path: '/spots', name: '景点', icon: '🏞️' },
  { path: '/hotels', name: '酒店', icon: '🏨' },
  { path: '/smart-recommend', name: '智能推荐', icon: '✨' },
  { path: '/messages', name: '消息', icon: '💬' },
];

// 状态
const isScrolled = ref(false);
const showUserMenu = ref(false);
const showSearch = ref(false);
const searchKeyword = ref('');
const hasNotification = ref(true); // 模拟有新通知
const searchInput = ref<HTMLInputElement | null>(null);

// 导航指示器
const indicatorStyle = ref({ left: '0px', width: '0px' });
const hoverIndicator = ref({ left: '0px', width: '0px' });

// 判断当前路由是否激活
function isActive(path: string): boolean {
  if (path === '/') {
    return route.path === '/';
  }
  return route.path.startsWith(path);
}

// 导航悬停效果
function handleNavHover(item: any, event: MouseEvent) {
  const target = event.currentTarget as HTMLElement;
  hoverIndicator.value = {
    left: `${target.offsetLeft}px`,
    width: `${target.offsetWidth}px`
  };
}

function handleNavLeave() {
  // 恢复到当前激活项
  updateIndicator();
}

// 更新指示器位置
function updateIndicator() {
  nextTick(() => {
    const activeItem = document.querySelector('.nav-item.active') as HTMLElement;
    if (activeItem) {
      indicatorStyle.value = {
        left: `${activeItem.offsetLeft}px`,
        width: `${activeItem.offsetWidth}px`
      };
    }
  });
}

// 切换用户菜单
function toggleUserMenu() {
  showUserMenu.value = !showUserMenu.value;
}

// 切换搜索
function toggleSearch() {
  showSearch.value = !showSearch.value;
  if (showSearch.value) {
    nextTick(() => {
      searchInput.value?.focus();
    });
  }
}

// 搜索
function handleSearch() {
  if (searchKeyword.value.trim()) {
    router.push(`/spots?keyword=${encodeURIComponent(searchKeyword.value)}`);
    showSearch.value = false;
    searchKeyword.value = '';
  }
}

// 按类型搜索
function searchByType(type: string) {
  if (searchKeyword.value.trim()) {
    const routes: Record<string, string> = {
      spot: '/spots',
      food: '/spots',
      hotel: '/hotels'
    };
    router.push(`${routes[type]}?keyword=${encodeURIComponent(searchKeyword.value)}`);
    showSearch.value = false;
    searchKeyword.value = '';
  }
}

// 退出登录
function handleLogout() {
  showUserMenu.value = false;
  authStore.logout();
  router.push('/');
}

// 滚动监听
function handleScroll() {
  isScrolled.value = window.scrollY > 20;
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll);
  updateIndicator();
});

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
});

// 监听路由变化更新指示器
router.afterEach(() => {
  updateIndicator();
});
</script>

<style>
@import '@/assets/style.css';

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  position: relative;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

/* 全局装饰元素 */
.global-decorations {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}

.g-deco {
  position: absolute;
  font-size: 28px;
  opacity: 0.25;
  animation: g-float 8s ease-in-out infinite;
}

.g-deco-1 { top: 15%; left: 3%; animation-delay: 0s; font-size: 36px; }
.g-deco-2 { top: 35%; left: 5%; animation-delay: 1.2s; font-size: 30px; }
.g-deco-3 { top: 55%; left: 2%; animation-delay: 2.4s; font-size: 24px; }
.g-deco-4 { top: 75%; left: 4%; animation-delay: 0.6s; font-size: 32px; }
.g-deco-5 { top: 20%; right: 3%; animation-delay: 1.8s; font-size: 34px; }
.g-deco-6 { top: 40%; right: 5%; animation-delay: 3s; font-size: 28px; }
.g-deco-7 { top: 60%; right: 2%; animation-delay: 0.9s; font-size: 26px; }
.g-deco-8 { top: 80%; right: 4%; animation-delay: 2.1s; font-size: 30px; }
.g-deco-9 { top: 10%; left: 50%; animation-delay: 1.5s; font-size: 22px; }
.g-deco-10 { bottom: 10%; left: 50%; animation-delay: 2.7s; font-size: 24px; }

.g-circle {
  position: absolute;
  border-radius: 50%;
  border: 3px solid rgba(255, 255, 255, 0.15);
  animation: g-pulse 6s ease-in-out infinite;
}

.g-circle-1 {
  width: 300px;
  height: 300px;
  top: 10%;
  left: -100px;
}

.g-circle-2 {
  width: 200px;
  height: 200px;
  top: 50%;
  right: -50px;
  animation-delay: 1.5s;
}

.g-circle-3 {
  width: 150px;
  height: 150px;
  bottom: 20%;
  left: 5%;
  animation-delay: 3s;
}

.g-circle-4 {
  width: 250px;
  height: 250px;
  bottom: 5%;
  right: 10%;
  animation-delay: 4.5s;
}

@keyframes g-float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-15px) rotate(3deg); }
}

@keyframes g-pulse {
  0%, 100% { transform: scale(1); opacity: 0.15; }
  50% { transform: scale(1.08); opacity: 0.25; }
}

/* ==================== 新颖导航栏样式 ==================== */
.header {
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(20px);
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 100;
  transition: all 0.3s ease;
}

.header-scrolled {
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.12);
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 30px;
  height: 72px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

/* Logo 区域 */
.logo-wrapper {
  text-decoration: none;
}

.logo-container {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-globe {
  position: relative;
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.globe-icon {
  font-size: 32px;
  animation: globe-spin 20s linear infinite;
}

@keyframes globe-spin {
  from { transform: rotateY(0deg); }
  to { transform: rotateY(360deg); }
}

.globe-ring {
  position: absolute;
  width: 100%;
  height: 100%;
  border: 2px solid transparent;
  border-top-color: #667eea;
  border-radius: 50%;
  animation: ring-rotate 3s linear infinite;
}

@keyframes ring-rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.logo-text-wrapper {
  display: flex;
  flex-direction: column;
}

.logo-text {
  font-size: 22px;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  line-height: 1.2;
}

.logo-slogan {
  font-size: 11px;
  color: #999;
  letter-spacing: 1px;
}

/* 导航区域 */
.nav-container {
  flex: 1;
  display: flex;
  justify-content: center;
  padding: 0 40px;
}

.nav-track {
  position: relative;
  display: flex;
  gap: 6px;
  padding: 6px;
  background: #f5f5f7;
  border-radius: 30px;
}

.nav-indicator {
  position: absolute;
  top: 6px;
  height: calc(100% - 12px);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 24px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.nav-item {
  position: relative;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border-radius: 24px;
  color: #666;
  font-weight: 500;
  font-size: 14px;
  transition: all 0.3s ease;
  z-index: 1;
}

.nav-item:hover {
  color: #667eea;
}

.nav-item.active {
  color: white;
}

.nav-item-icon {
  font-size: 16px;
  transition: transform 0.3s ease;
}

.nav-item:hover .nav-item-icon {
  transform: scale(1.2);
}

.nav-item-badge {
  position: absolute;
  top: 4px;
  right: 8px;
  min-width: 18px;
  height: 18px;
  background: #ff4757;
  color: white;
  font-size: 11px;
  font-weight: 600;
  border-radius: 9px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 5px;
}

/* 右侧操作区 */
.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.action-btn {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  background: #f5f5f7;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}

.action-btn:hover {
  background: #eee;
  transform: scale(1.05);
}

.btn-icon {
  font-size: 18px;
}

.notify-dot {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 10px;
  height: 10px;
  background: #ff4757;
  border-radius: 50%;
  border: 2px solid white;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.2); opacity: 0.8; }
}

/* 用户区域 */
.user-area {
  position: relative;
}

.user-avatar-wrapper {
  position: relative;
  cursor: pointer;
  padding: 3px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  transition: all 0.3s ease;
}

.user-avatar-wrapper:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.user-avatar {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid white;
}

.avatar-status {
  position: absolute;
  bottom: 2px;
  right: 2px;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 2px solid white;
}

.avatar-status.online {
  background: #2ed573;
}

/* 用户下拉菜单 */
.user-dropdown {
  position: absolute;
  top: calc(100% + 12px);
  right: 0;
  width: 260px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
  padding: 8px;
  z-index: 200;
}

.dropdown-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 12px;
}

.dropdown-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
}

.dropdown-user-info {
  flex: 1;
  overflow: hidden;
}

.dropdown-username {
  display: block;
  font-size: 16px;
  font-weight: 600;
  color: #1a1a2e;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.dropdown-motto {
  display: block;
  font-size: 12px;
  color: #999;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.dropdown-divider {
  height: 1px;
  background: #f0f0f0;
  margin: 4px 0;
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 10px;
  color: #555;
  font-size: 14px;
  transition: all 0.2s ease;
  cursor: pointer;
  border: none;
  background: none;
  width: 100%;
  text-align: left;
}

.dropdown-item:hover {
  background: #f5f5f7;
  color: #667eea;
}

.dropdown-item.logout-item {
  color: #ff4757;
}

.dropdown-item.logout-item:hover {
  background: #fff5f5;
  color: #ff4757;
}

/* 下拉菜单动画 */
.dropdown-enter-active,
.dropdown-leave-active {
  transition: all 0.3s ease;
}

.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* 登录注册按钮 */
.auth-buttons {
  display: flex;
  gap: 10px;
}

.auth-btn {
  padding: 10px 22px;
  border-radius: 25px;
  font-weight: 600;
  font-size: 14px;
  transition: all 0.3s ease;
}

.auth-btn.login-btn {
  background: transparent;
  color: #667eea;
  border: 2px solid #667eea;
}

.auth-btn.login-btn:hover {
  background: rgba(102, 126, 234, 0.1);
}

.auth-btn.register-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
}

.auth-btn.register-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
  color: white;
}

/* 搜索覆盖层 */
.search-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(5px);
  z-index: 300;
  display: flex;
  justify-content: center;
  padding-top: 100px;
}

.search-container {
  width: 600px;
  background: white;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: search-pop 0.3s ease;
  height: fit-content;
}

@keyframes search-pop {
  from {
    opacity: 0;
    transform: scale(0.95) translateY(-20px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

.search-input-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  background: #f5f5f7;
  border-radius: 16px;
  margin-bottom: 20px;
}

.search-icon {
  font-size: 20px;
}

.search-input {
  flex: 1;
  border: none;
  background: none;
  font-size: 18px;
  outline: none;
  color: #1a1a2e;
}

.search-input::placeholder {
  color: #999;
}

.search-close {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #e0e0e0;
  border: none;
  cursor: pointer;
  font-size: 14px;
  color: #666;
  transition: all 0.2s;
}

.search-close:hover {
  background: #d0d0d0;
}

.search-suggestions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.suggestion-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  color: #555;
}

.suggestion-item:hover {
  background: #f5f5f7;
  color: #667eea;
}

.search-hot {
  padding-top: 10px;
}

.hot-title {
  font-size: 14px;
  color: #999;
  margin-bottom: 12px;
  display: block;
}

.hot-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.hot-tag {
  padding: 8px 16px;
  background: #f5f5f7;
  border-radius: 20px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}

.hot-tag:hover {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

/* 搜索动画 */
.search-expand-enter-active,
.search-expand-leave-active {
  transition: all 0.3s ease;
}

.search-expand-enter-from,
.search-expand-leave-to {
  opacity: 0;
}

/* 响应式 */
@media (max-width: 1024px) {
  .nav-container {
    display: none;
  }
  
  .logo-slogan {
    display: none;
  }
}

@media (max-width: 768px) {
  .header-content {
    padding: 0 16px;
  }
  
  .auth-buttons {
    gap: 6px;
  }
  
  .auth-btn {
    padding: 8px 16px;
    font-size: 13px;
  }
}

.main-content {
  flex: 1;
  padding: 40px 20px;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
  position: relative;
  z-index: 1;
}

.footer {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-top: 1px solid rgba(255, 255, 255, 0.2);
  padding: 24px 20px;
  text-align: center;
  color: #666;
  font-size: 12px;
  margin-top: auto;
  position: relative;
  z-index: 1;
}

.footer-links {
  margin-bottom: 12px;
}

.footer-links a {
  color: #666;
  transition: color 0.2s;
}

.footer-links a:hover {
  color: #667eea;
}

.footer .divider {
  margin: 0 12px;
  color: #ddd;
}

.footer-copyright {
  margin-bottom: 12px;
  color: #999;
}

.footer-partners {
  margin-bottom: 16px;
}

.footer-partners a {
  color: #999;
  transition: color 0.2s;
}

.footer-partners a:hover {
  color: #667eea;
}

.footer-badges {
  display: flex;
  justify-content: center;
  gap: 40px;
  margin-top: 16px;
}

.badge-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #bbb;
}

.badge-item img {
  width: 28px;
  height: 28px;
  opacity: 0.6;
}
</style>
