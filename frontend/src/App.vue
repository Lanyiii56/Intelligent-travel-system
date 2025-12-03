<template>
  <div id="app">
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
          <router-link to="/recommend" class="nav-link">
            <span class="nav-icon">✨</span>
            <span>推荐</span>
          </router-link>
          <router-link to="/profile" class="nav-link">
            <span class="nav-icon">👤</span>
            <span>我的</span>
          </router-link>
        </nav>
        <div class="header-actions">
          <router-link to="/login" class="login-btn" v-if="!isLoggedIn">登录</router-link>
          <button v-else class="logout-btn" @click="handleLogout">退出</button>
        </div>
      </div>
    </header>
    <main class="main-content">
      <router-view />
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
import { useAuth } from '@/composables/useAuth';
import { computed } from 'vue';

const { isLoggedIn, logout } = useAuth();

function handleLogout() {
  logout();
}
</script>

<style>
@import '@/assets/style.css';

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
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
}

.footer {
  background: #f5f5f5;
  border-top: 1px solid #e8e8e8;
  padding: 24px 20px;
  text-align: center;
  color: #999;
  font-size: 12px;
  margin-top: auto;
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
