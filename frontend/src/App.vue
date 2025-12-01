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
      <p>© 2024 智游天下 - 发现世界的美好</p>
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
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  padding: 24px 20px;
  text-align: center;
  color: #666;
  font-size: 14px;
  margin-top: auto;
}

.footer p {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-weight: 500;
}
</style>
