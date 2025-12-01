<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <div class="login-icon">🔐</div>
        <h2 class="login-title">欢迎回来</h2>
        <p class="login-subtitle">登录您的账户，开始探索之旅</p>
      </div>
      
      <form @submit.prevent="onSubmit" class="login-form">
        <div class="form-group">
          <label class="form-label">用户名</label>
          <div class="input-wrapper">
            <span class="input-icon">👤</span>
            <input 
              v-model="username" 
              placeholder="请输入用户名" 
              required 
              class="form-input"
            />
          </div>
        </div>
        
        <div class="form-group">
          <label class="form-label">密码</label>
          <div class="input-wrapper">
            <span class="input-icon">🔒</span>
            <input 
              v-model="password" 
              type="password" 
              placeholder="请输入密码" 
              required 
              class="form-input"
            />
          </div>
        </div>
        
        <div v-if="msg" class="error-message">
          <span>⚠️</span> {{ msg }}
        </div>
        
        <button type="submit" class="login-btn" :disabled="loading">
          <span v-if="loading" class="loading-spinner"></span>
          <span v-else>登录</span>
        </button>
      </form>
      
      <div class="login-footer">
        <p>还没有账户？<a href="#">立即注册</a></p>
      </div>
    </div>
    
    <div class="login-decoration">
      <div class="deco-circle circle-1"></div>
      <div class="deco-circle circle-2"></div>
      <div class="deco-circle circle-3"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { login } from '@/api/auth';
import { useAuth } from '@/composables/useAuth';
import router from '@/router';

const username = ref('');
const password = ref('');
const msg = ref('');
const loading = ref(false);

const { setToken } = useAuth();

async function onSubmit() {
  loading.value = true;
  msg.value = '';
  try {
    const res = await login({ username: username.value, password: password.value });
    const token = res.data.token || res.data;
    setToken(token);
    router.push('/');
  } catch (e: any) {
    msg.value = e?.response?.data || '登录失败，请检查用户名和密码';
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.login-container {
  min-height: calc(100vh - 200px);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  padding: 40px 20px;
}

.login-card {
  background: white;
  border-radius: 24px;
  padding: 48px 40px;
  width: 100%;
  max-width: 420px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  position: relative;
  z-index: 2;
  animation: slideUp 0.5s ease;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-header {
  text-align: center;
  margin-bottom: 36px;
}

.login-icon {
  font-size: 56px;
  margin-bottom: 16px;
}

.login-title {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 8px;
}

.login-subtitle {
  font-size: 15px;
  color: #888;
}

.login-form {
  margin-bottom: 24px;
}

.form-group {
  margin-bottom: 24px;
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #444;
  margin-bottom: 10px;
}

.input-wrapper {
  position: relative;
}

.input-icon {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 18px;
  z-index: 1;
}

.form-input {
  padding-left: 48px !important;
  height: 52px;
  font-size: 15px;
  border-radius: 14px;
}

.error-message {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 16px;
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: 12px;
  color: #dc2626;
  font-size: 14px;
  margin-bottom: 24px;
}

.login-btn {
  width: 100%;
  height: 52px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-size: 16px;
  font-weight: 600;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.login-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.login-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.loading-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.login-footer {
  text-align: center;
  font-size: 14px;
  color: #888;
}

.login-footer a {
  color: #667eea;
  font-weight: 600;
}

.login-footer a:hover {
  text-decoration: underline;
}

/* 装饰元素 */
.login-decoration {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  overflow: hidden;
  z-index: 1;
}

.deco-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
}

.circle-1 {
  width: 300px;
  height: 300px;
  top: -100px;
  right: -100px;
}

.circle-2 {
  width: 200px;
  height: 200px;
  bottom: 100px;
  left: -50px;
}

.circle-3 {
  width: 150px;
  height: 150px;
  bottom: -50px;
  right: 20%;
}

@media (max-width: 480px) {
  .login-card {
    padding: 36px 24px;
  }
  
  .login-title {
    font-size: 24px;
  }
}
</style>
