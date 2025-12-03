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
    
    <header class="header">
      <div class="header-content">
        <div class="logo">
          <span class="logo-icon">🌍</span>
          <span class="logo-text">智游天下</span>
        </div>
        <nav class="nav">
          <router-link to="/" class="nav-link">
            <span class="nav-icon">🏠</span>
            <span>首页</span>
          </router-link>
          <router-link to="/spots" class="nav-link">
            <span class="nav-icon">🏞️</span>
            <span>景点</span>
          </router-link>
          <router-link to="/smart-recommend" class="nav-link">
            <span class="nav-icon">✨</span>
            <span>推荐</span>
          </router-link>
          <router-link to="/profile" class="nav-link">
            <span class="nav-icon">👤</span>
            <span>我的</span>
          </router-link>
        </nav>
        <div class="header-actions">
          <router-link to="/login" class="login-btn" v-if="!authStore.isLoggedIn">登录</router-link>
          <button v-else class="logout-btn" @click="handleLogout">退出</button>
        </div>
      </div>
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
import { useAuthStore } from '@/modules/auth/store';

const authStore = useAuthStore();

function handleLogout() {
  authStore.logout();
}
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

.header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
}

.logo-icon {
  font-size: 28px;
}

.logo-text {
  font-size: 22px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.nav {
  display: flex;
  gap: 8px;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 18px;
  border-radius: 25px;
  color: #555;
  font-weight: 500;
  transition: all 0.3s ease;
}

.nav-link:hover {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
}

.nav-link.router-link-active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.nav-icon {
  font-size: 16px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.login-btn {
  padding: 10px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 25px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
  color: white;
}

.logout-btn {
  padding: 10px 24px;
  background: #f5f5f5;
  color: #666;
  border-radius: 25px;
  font-weight: 500;
}

.logout-btn:hover {
  background: #eee;
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
