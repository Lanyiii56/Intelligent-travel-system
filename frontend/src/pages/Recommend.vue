<template>
  <div class="recommend-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1 class="page-title">✨ 智能推荐</h1>
      <p class="page-subtitle">根据您的偏好，为您量身定制完美行程</p>
    </div>

    <!-- 推荐表单 -->
    <div class="recommend-form-card">
      <div class="form-header">
        <span class="form-icon">🎯</span>
        <div>
          <h2 class="form-title">告诉我们您的需求</h2>
          <p class="form-desc">我们将为您推荐最适合的景点</p>
        </div>
      </div>
      
      <div class="form-body">
        <div class="form-row">
          <div class="form-group">
            <label class="form-label">
              <span class="label-icon">👤</span>
              您的年龄
            </label>
            <div class="input-wrapper">
              <input type="number" v-model.number="age" min="1" max="100" />
              <span class="input-suffix">岁</span>
            </div>
          </div>
          
          <div class="form-group">
            <label class="form-label">
              <span class="label-icon">⏰</span>
              可用时间
            </label>
            <div class="input-wrapper">
              <input type="number" v-model.number="time" min="30" step="30" />
              <span class="input-suffix">分钟</span>
            </div>
          </div>
        </div>
        
        <button @click="onRecommend" class="btn-recommend" :disabled="loading">
          <span v-if="loading" class="loading-spinner"></span>
          <template v-else>
            <span>🚀</span> 获取推荐
          </template>
        </button>
      </div>
    </div>

    <!-- 推荐结果 -->
    <div class="results-section" v-if="hasSearched">
      <div class="results-header">
        <h2 class="results-title">
          <span>🎁</span> 为您推荐
        </h2>
        <span class="results-count" v-if="result.length > 0">
          共 {{ result.length }} 个景点
        </span>
      </div>

      <div v-if="loading" class="loading-state">
        <div class="loading-spinner large"></div>
        <p>正在为您智能匹配...</p>
      </div>

      <div v-else-if="result.length === 0" class="empty-state">
        <div class="empty-icon">🔍</div>
        <h3>暂无匹配结果</h3>
        <p>尝试调整筛选条件，发现更多精彩</p>
      </div>

      <div v-else class="results-grid">
        <router-link 
          v-for="(s, index) in result" 
          :key="s.id" 
          :to="`/spots/${s.id}`"
          class="result-card"
          :style="{ animationDelay: `${index * 0.1}s` }"
        >
          <div class="card-rank">{{ index + 1 }}</div>
          <div class="card-image">
            <img v-if="s.imageUrl" :src="s.imageUrl" :alt="s.name" />
            <div v-else class="card-placeholder">🏞️</div>
          </div>
          <div class="card-content">
            <h3 class="card-title">{{ s.name }}</h3>
            <div class="card-info">
              <span class="info-tag">⏱️ {{ s.playTime }}分钟</span>
              <span class="info-tag" v-if="s.priceMin">💰 ¥{{ s.priceMin }}起</span>
            </div>
            <p class="card-desc" v-if="s.description">
              {{ s.description.replace(/<[^>]*>/g, '').slice(0, 50) }}...
            </p>
          </div>
          <div class="card-action">
            <span>查看详情</span>
            <span class="arrow">→</span>
          </div>
        </router-link>
      </div>
    </div>

    <!-- 初始状态提示 -->
    <div v-else class="initial-state">
      <div class="initial-icon">🗺️</div>
      <h3>开始您的探索之旅</h3>
      <p>填写上方信息，获取个性化推荐</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { recommend } from '@/api/recommend';

const age = ref(25);
const time = ref(240);
const result = ref<any[]>([]);
const loading = ref(false);
const hasSearched = ref(false);

async function onRecommend() {
  loading.value = true;
  hasSearched.value = true;
  try {
    const res = await recommend(1, age.value, time.value);
    result.value = res.data || [];
  } catch (e) {
    console.error(e);
    result.value = [];
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.recommend-page {
  animation: fadeInUp 0.5s ease;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
}

.page-title {
  font-size: 40px;
  font-weight: 700;
  color: white;
  margin-bottom: 12px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.page-subtitle {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.9);
}

/* 推荐表单卡片 */
.recommend-form-card {
  background: white;
  border-radius: 24px;
  padding: 36px;
  margin-bottom: 40px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
}

.form-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 32px;
}

.form-icon {
  font-size: 48px;
}

.form-title {
  font-size: 22px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 4px;
}

.form-desc {
  font-size: 14px;
  color: #888;
}

.form-body {
  display: flex;
  flex-direction: column;
  gap: 28px;
}

.form-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 24px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.form-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 600;
  color: #444;
}

.label-icon {
  font-size: 18px;
}

.input-wrapper {
  position: relative;
}

.input-wrapper input {
  padding-right: 60px;
  height: 56px;
  font-size: 18px;
  font-weight: 600;
}

.input-suffix {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  color: #888;
  font-size: 15px;
}

.btn-recommend {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  width: 100%;
  max-width: 300px;
  margin: 0 auto;
  padding: 18px 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-size: 18px;
  font-weight: 600;
  border-radius: 16px;
  transition: all 0.3s ease;
}

.btn-recommend:hover:not(:disabled) {
  transform: translateY(-3px);
  box-shadow: 0 12px 35px rgba(102, 126, 234, 0.4);
}

.btn-recommend:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

/* 结果区域 */
.results-section {
  background: white;
  border-radius: 24px;
  padding: 36px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
}

.results-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28px;
}

.results-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 24px;
  font-weight: 600;
  color: #1a1a2e;
}

.results-count {
  font-size: 14px;
  color: #888;
  background: #f5f5f5;
  padding: 8px 16px;
  border-radius: 20px;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60px;
  color: #667eea;
}

.loading-spinner {
  width: 24px;
  height: 24px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

.loading-spinner.large {
  width: 48px;
  height: 48px;
  border: 4px solid #e0e0e0;
  border-top-color: #667eea;
  margin-bottom: 20px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.empty-state h3 {
  font-size: 20px;
  color: #1a1a2e;
  margin-bottom: 8px;
}

.empty-state p {
  color: #888;
}

.results-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
}

.result-card {
  background: #f8f9fa;
  border-radius: 20px;
  overflow: hidden;
  position: relative;
  transition: all 0.3s ease;
  animation: cardFadeIn 0.5s ease forwards;
  opacity: 0;
}

@keyframes cardFadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.result-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.12);
}

.card-rank {
  position: absolute;
  top: 16px;
  left: 16px;
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 16px;
  z-index: 2;
}

.card-image {
  height: 180px;
  overflow: hidden;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.result-card:hover .card-image img {
  transform: scale(1.08);
}

.card-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  font-size: 56px;
}

.card-content {
  padding: 20px;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 12px;
}

.card-info {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.info-tag {
  padding: 4px 10px;
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
  border-radius: 6px;
  font-size: 12px;
}

.card-desc {
  font-size: 14px;
  color: #888;
  line-height: 1.5;
}

.card-action {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-top: 1px solid #eee;
  color: #667eea;
  font-weight: 500;
  font-size: 14px;
}

.arrow {
  transition: transform 0.3s;
}

.result-card:hover .arrow {
  transform: translateX(4px);
}

/* 初始状态 */
.initial-state {
  background: white;
  border-radius: 24px;
  padding: 80px 40px;
  text-align: center;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
}

.initial-icon {
  font-size: 80px;
  margin-bottom: 24px;
}

.initial-state h3 {
  font-size: 24px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 12px;
}

.initial-state p {
  font-size: 16px;
  color: #888;
}

@media (max-width: 768px) {
  .page-title {
    font-size: 32px;
  }
  
  .recommend-form-card {
    padding: 24px;
  }
  
  .form-header {
    flex-direction: column;
    text-align: center;
  }
}
</style>
