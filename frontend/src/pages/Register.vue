<template>
  <div class="register-container">
    <div class="register-card">
      <div class="register-header">
        <div class="register-icon">🎉</div>
        <h2 class="register-title">创建账户</h2>
        <p class="register-subtitle">加入智游天下，开启精彩旅程</p>
      </div>
      
      <form @submit.prevent="onSubmit" class="register-form">
        <div class="form-group">
          <label class="form-label">用户名 <span class="required">*</span></label>
          <div class="input-wrapper">
            <span class="input-icon">👤</span>
            <input 
              v-model="form.username" 
              placeholder="请输入用户名（4-20位字母数字）" 
              required 
              class="form-input"
              maxlength="20"
            />
          </div>
          <p class="input-hint" v-if="form.username && !isUsernameValid">用户名需为4-20位字母、数字或下划线</p>
        </div>
        
        <div class="form-group">
          <label class="form-label">密码 <span class="required">*</span></label>
          <div class="input-wrapper">
            <span class="input-icon">🔒</span>
            <input 
              v-model="form.password" 
              type="password" 
              placeholder="请输入密码（6-20位）" 
              required 
              class="form-input"
              maxlength="20"
            />
          </div>
          <div class="password-strength" v-if="form.password">
            <div class="strength-bar">
              <div class="strength-fill" :class="passwordStrengthClass" :style="{ width: passwordStrength + '%' }"></div>
            </div>
            <span class="strength-text" :class="passwordStrengthClass">{{ passwordStrengthText }}</span>
          </div>
        </div>
        
        <div class="form-group">
          <label class="form-label">确认密码 <span class="required">*</span></label>
          <div class="input-wrapper">
            <span class="input-icon">🔐</span>
            <input 
              v-model="form.confirmPassword" 
              type="password" 
              placeholder="请再次输入密码" 
              required 
              class="form-input"
            />
          </div>
          <p class="input-hint error" v-if="form.confirmPassword && form.password !== form.confirmPassword">两次密码输入不一致</p>
        </div>
        
        <div class="form-row">
          <div class="form-group half">
            <label class="form-label">昵称</label>
            <div class="input-wrapper">
              <span class="input-icon">😊</span>
              <input 
                v-model="form.nickname" 
                placeholder="您的昵称" 
                class="form-input"
                maxlength="20"
              />
            </div>
          </div>
          
          <div class="form-group half">
            <label class="form-label">年龄</label>
            <div class="input-wrapper">
              <span class="input-icon">🎂</span>
              <input 
                v-model.number="form.age" 
                type="number" 
                placeholder="年龄" 
                class="form-input"
                min="1"
                max="120"
              />
            </div>
          </div>
        </div>
        
        <div class="form-group">
          <label class="form-label">性别</label>
          <div class="gender-options">
            <label class="gender-option" :class="{ active: form.gender === 'male' }">
              <input type="radio" v-model="form.gender" value="male" />
              <span class="gender-icon">👨</span>
              <span>男</span>
            </label>
            <label class="gender-option" :class="{ active: form.gender === 'female' }">
              <input type="radio" v-model="form.gender" value="female" />
              <span class="gender-icon">👩</span>
              <span>女</span>
            </label>
            <label class="gender-option" :class="{ active: form.gender === 'other' }">
              <input type="radio" v-model="form.gender" value="other" />
              <span class="gender-icon">🧑</span>
              <span>保密</span>
            </label>
          </div>
        </div>
        
        <div class="form-group">
          <label class="agreement">
            <input type="checkbox" v-model="agreed" />
            <span>我已阅读并同意 <a href="#" @click.prevent="showTerms">《用户协议》</a> 和 <a href="#" @click.prevent="showPrivacy">《隐私政策》</a></span>
          </label>
        </div>
        
        <div v-if="msg" class="message" :class="{ error: isError, success: !isError }">
          <span>{{ isError ? '⚠️' : '✅' }}</span> {{ msg }}
        </div>
        
        <button type="submit" class="register-btn" :disabled="loading || !canSubmit">
          <span v-if="loading" class="loading-spinner"></span>
          <span v-else>立即注册</span>
        </button>
      </form>
      
      <div class="register-footer">
        <p>已有账户？<router-link to="/login">立即登录</router-link></p>
      </div>
    </div>
    
    <div class="register-decoration">
      <div class="deco-circle circle-1"></div>
      <div class="deco-circle circle-2"></div>
      <div class="deco-circle circle-3"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/modules/auth/store';
import { ElMessage } from 'element-plus';

const router = useRouter();
const authStore = useAuthStore();

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  age: undefined as number | undefined,
  gender: ''
});

const msg = ref('');
const isError = ref(false);
const loading = ref(false);
const agreed = ref(false);

// 用户名验证
const isUsernameValid = computed(() => {
  return /^[a-zA-Z0-9_]{4,20}$/.test(form.username);
});

// 密码强度
const passwordStrength = computed(() => {
  const pwd = form.password;
  if (!pwd) return 0;
  let strength = 0;
  if (pwd.length >= 6) strength += 25;
  if (pwd.length >= 10) strength += 15;
  if (/[a-z]/.test(pwd)) strength += 15;
  if (/[A-Z]/.test(pwd)) strength += 15;
  if (/[0-9]/.test(pwd)) strength += 15;
  if (/[^a-zA-Z0-9]/.test(pwd)) strength += 15;
  return Math.min(strength, 100);
});

const passwordStrengthClass = computed(() => {
  if (passwordStrength.value < 40) return 'weak';
  if (passwordStrength.value < 70) return 'medium';
  return 'strong';
});

const passwordStrengthText = computed(() => {
  if (passwordStrength.value < 40) return '弱';
  if (passwordStrength.value < 70) return '中';
  return '强';
});

// 是否可以提交
const canSubmit = computed(() => {
  return form.username && 
         form.password && 
         form.password.length >= 6 &&
         form.password === form.confirmPassword && 
         agreed.value &&
         isUsernameValid.value;
});

async function onSubmit() {
  if (!canSubmit.value) return;
  
  loading.value = true;
  msg.value = '';
  isError.value = false;
  
  try {
    await authStore.register({
      username: form.username,
      password: form.password,
      nickname: form.nickname || form.username
    });
    
    msg.value = '注册成功！正在跳转到登录页...';
    isError.value = false;
    
    ElMessage.success('注册成功！');
    
    setTimeout(() => {
      router.push('/login');
    }, 1500);
  } catch (e: any) {
    msg.value = e?.response?.data || '注册失败，请稍后重试';
    isError.value = true;
  } finally {
    loading.value = false;
  }
}

function showTerms() {
  ElMessage.info('用户协议内容');
}

function showPrivacy() {
  ElMessage.info('隐私政策内容');
}
</script>

<style scoped>
.register-container {
  min-height: calc(100vh - 200px);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  padding: 40px 20px;
}

.register-card {
  background: white;
  border-radius: 24px;
  padding: 40px;
  width: 100%;
  max-width: 480px;
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

.register-header {
  text-align: center;
  margin-bottom: 32px;
}

.register-icon {
  font-size: 48px;
  margin-bottom: 12px;
}

.register-title {
  font-size: 26px;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 8px;
}

.register-subtitle {
  font-size: 14px;
  color: #888;
}

.register-form {
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group.half {
  flex: 1;
}

.form-row {
  display: flex;
  gap: 16px;
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #444;
  margin-bottom: 8px;
}

.required {
  color: #e74c3c;
}

.input-wrapper {
  position: relative;
}

.input-icon {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 16px;
  z-index: 1;
}

.form-input {
  width: 100%;
  padding: 12px 12px 12px 44px;
  height: 48px;
  font-size: 14px;
  border: 2px solid #e8e8e8;
  border-radius: 12px;
  transition: all 0.3s ease;
  background: #fafafa;
}

.form-input:focus {
  border-color: #667eea;
  background: white;
  outline: none;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.input-hint {
  font-size: 12px;
  color: #888;
  margin-top: 6px;
}

.input-hint.error {
  color: #e74c3c;
}

/* 密码强度 */
.password-strength {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 8px;
}

.strength-bar {
  flex: 1;
  height: 4px;
  background: #e8e8e8;
  border-radius: 2px;
  overflow: hidden;
}

.strength-fill {
  height: 100%;
  border-radius: 2px;
  transition: all 0.3s ease;
}

.strength-fill.weak { background: #e74c3c; }
.strength-fill.medium { background: #f39c12; }
.strength-fill.strong { background: #27ae60; }

.strength-text {
  font-size: 12px;
  font-weight: 600;
}

.strength-text.weak { color: #e74c3c; }
.strength-text.medium { color: #f39c12; }
.strength-text.strong { color: #27ae60; }

/* 性别选择 */
.gender-options {
  display: flex;
  gap: 12px;
}

.gender-option {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 12px;
  border: 2px solid #e8e8e8;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #fafafa;
}

.gender-option input {
  display: none;
}

.gender-option:hover {
  border-color: #667eea;
}

.gender-option.active {
  border-color: #667eea;
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
}

.gender-icon {
  font-size: 18px;
}

/* 协议 */
.agreement {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  font-size: 13px;
  color: #666;
  cursor: pointer;
}

.agreement input {
  margin-top: 3px;
}

.agreement a {
  color: #667eea;
}

.agreement a:hover {
  text-decoration: underline;
}

/* 消息 */
.message {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 14px;
  border-radius: 10px;
  font-size: 14px;
  margin-bottom: 20px;
}

.message.error {
  background: #fef2f2;
  border: 1px solid #fecaca;
  color: #dc2626;
}

.message.success {
  background: #f0fdf4;
  border: 1px solid #bbf7d0;
  color: #16a34a;
}

/* 注册按钮 */
.register-btn {
  width: 100%;
  height: 50px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  border: none;
  cursor: pointer;
}

.register-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.register-btn:disabled {
  opacity: 0.6;
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

.register-footer {
  text-align: center;
  font-size: 14px;
  color: #888;
}

.register-footer a {
  color: #667eea;
  font-weight: 600;
}

.register-footer a:hover {
  text-decoration: underline;
}

/* 装饰元素 */
.register-decoration {
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
  background: rgba(102, 126, 234, 0.05);
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
  .register-card {
    padding: 28px 20px;
  }
  
  .register-title {
    font-size: 22px;
  }
  
  .form-row {
    flex-direction: column;
    gap: 0;
  }
  
  .gender-options {
    flex-wrap: wrap;
  }
}
</style>
