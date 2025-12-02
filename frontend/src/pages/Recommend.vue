<template>
  <div class="recommend-page">
    <!-- 渐变背景头部 -->
    <div class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">发现好去处</h1>
        <p class="hero-subtitle">根据您的时间和偏好，精准推荐最合适的景点</p>
      </div>

      <!-- 双卡片布局 -->
      <div class="cards-container">
        <!-- 智能规划卡片 - 主推 -->
        <router-link to="/smart-recommend" class="feature-card main-card">
          <div class="card-badge">推荐</div>
          <div class="card-icon">
            <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/>
              <circle cx="12" cy="10" r="3"/>
            </svg>
          </div>
          <h2 class="card-title">智能行程规划</h2>
          <p class="card-desc">选择目的地、人数、预算，AI为您生成完整行程方案</p>
          <ul class="card-features">
            <li>
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="20,6 9,17 4,12"/>
              </svg>
              多景点路线规划
            </li>
            <li>
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="20,6 9,17 4,12"/>
              </svg>
              费用预估明细
            </li>
            <li>
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="20,6 9,17 4,12"/>
              </svg>
              地图路线展示
            </li>
          </ul>
          <div class="card-action">
            开始规划
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="9,18 15,12 9,6"/>
            </svg>
          </div>
        </router-link>

        <!-- 快速推荐卡片 -->
        <div class="feature-card quick-card">
          <div class="card-icon small">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="11" cy="11" r="8"/>
              <path d="m21 21-4.35-4.35"/>
            </svg>
          </div>
          <h3 class="card-title small">快速推荐</h3>
          <p class="card-desc small">输入年龄和时间，快速获取景点推荐</p>
          
          <div class="quick-form">
            <div class="form-row">
              <div class="form-field">
                <label>年龄</label>
                <div class="input-box">
                  <input type="number" v-model.number="age" min="1" max="100" />
                  <span class="unit">岁</span>
                </div>
              </div>
              <div class="form-field">
                <label>时间</label>
                <div class="input-box">
                  <input type="number" v-model.number="time" min="30" max="480" step="30" />
                  <span class="unit">分钟</span>
                </div>
              </div>
            </div>
            <button class="btn-quick" @click="onRecommend" :disabled="loading">
              {{ loading ? '查找中...' : '查找景点' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 结果区域 -->
    <div class="results-wrapper" v-if="hasSearched">
      <!-- 加载状态 -->
      <div v-if="loading" class="state-loading">
        <div class="loader"></div>
        <span>正在为您匹配景点...</span>
      </div>

      <!-- 空状态 -->
      <div v-else-if="result.length === 0" class="state-empty">
        <div class="empty-icon">🔍</div>
        <p>没有找到符合条件的景点</p>
        <span>试试调整年龄或时间</span>
      </div>

      <!-- 结果列表 -->
      <div v-else class="results-section">
        <div class="results-header">
          <h2>为您推荐</h2>
          <span class="results-count">共 {{ result.length }} 个景点</span>
        </div>
        
        <div class="spots-grid">
          <router-link 
            v-for="(spot, index) in result" 
            :key="spot.id" 
            :to="`/spots/${spot.id}`"
            class="spot-card"
          >
            <div class="spot-image">
              <img v-if="spot.imageUrl" :src="spot.imageUrl" :alt="spot.name" />
              <div v-else class="image-placeholder"></div>
              <span class="spot-rank" :class="getRankClass(index)">{{ index + 1 }}</span>
            </div>
            <div class="spot-body">
              <h3 class="spot-name">{{ spot.name }}</h3>
              <p class="spot-desc" v-if="spot.description">
                {{ spot.description.replace(/<[^>]*>/g, '').slice(0, 60) }}...
              </p>
              <div class="spot-meta">
                <span class="meta-item">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="12" cy="12" r="10"/>
                    <polyline points="12,6 12,12 16,14"/>
                  </svg>
                  {{ spot.playTime }}分钟
                </span>
                <span class="meta-item price" v-if="spot.priceMin">
                  ¥{{ spot.priceMin }}起
                </span>
              </div>
            </div>
          </router-link>
        </div>
      </div>
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

function getRankClass(index: number): string {
  if (index === 0) return 'gold';
  if (index === 1) return 'silver';
  if (index === 2) return 'bronze';
  return '';
}
</script>

<style scoped>
.recommend-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #7c3aed 0%, #a855f7 50%, #ec4899 100%);
}

/* Hero 区域 */
.hero-section {
  padding: 60px 24px 40px;
  max-width: 1000px;
  margin: 0 auto;
}

.hero-content {
  text-align: center;
  margin-bottom: 48px;
}

.hero-title {
  font-size: 40px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 12px;
}

.hero-subtitle {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.9);
}

/* 双卡片布局 */
.cards-container {
  display: grid;
  grid-template-columns: 1.2fr 1fr;
  gap: 24px;
}

/* 功能卡片通用样式 */
.feature-card {
  background: #fff;
  border-radius: 24px;
  padding: 32px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  text-decoration: none;
  transition: all 0.3s;
}

/* 主推卡片 - 智能规划 */
.main-card {
  position: relative;
  display: flex;
  flex-direction: column;
  border: 3px solid transparent;
  background: linear-gradient(#fff, #fff) padding-box,
              linear-gradient(135deg, #7c3aed, #ec4899) border-box;
}

.main-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 30px 80px rgba(124, 58, 237, 0.3);
}

.card-badge {
  position: absolute;
  top: -12px;
  left: 24px;
  padding: 6px 16px;
  background: linear-gradient(135deg, #7c3aed 0%, #a855f7 100%);
  color: #fff;
  font-size: 13px;
  font-weight: 600;
  border-radius: 20px;
  box-shadow: 0 4px 12px rgba(124, 58, 237, 0.4);
}

.card-icon {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #7c3aed 0%, #a855f7 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
}

.card-icon svg {
  color: #fff;
}

.card-icon.small {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: #f3f4f6;
}

.card-icon.small svg {
  color: #6b7280;
}

.card-title {
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 12px;
}

.card-title.small {
  font-size: 18px;
  margin-bottom: 8px;
}

.card-desc {
  font-size: 15px;
  color: #6b7280;
  line-height: 1.6;
  margin-bottom: 20px;
}

.card-desc.small {
  font-size: 14px;
  margin-bottom: 16px;
}

.card-features {
  list-style: none;
  padding: 0;
  margin: 0 0 24px 0;
  flex: 1;
}

.card-features li {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 0;
  font-size: 14px;
  color: #374151;
}

.card-features li svg {
  color: #7c3aed;
  flex-shrink: 0;
}

.card-action {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 16px;
  background: linear-gradient(135deg, #7c3aed 0%, #a855f7 100%);
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  transition: all 0.2s;
}

.main-card:hover .card-action {
  box-shadow: 0 8px 25px rgba(124, 58, 237, 0.4);
}

.card-action svg {
  transition: transform 0.2s;
}

.main-card:hover .card-action svg {
  transform: translateX(4px);
}

/* 快速推荐卡片 */
.quick-card {
  display: flex;
  flex-direction: column;
}

.quick-form {
  margin-top: auto;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-bottom: 16px;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-field label {
  font-size: 13px;
  font-weight: 500;
  color: #6b7280;
}

.input-box {
  display: flex;
  align-items: center;
  background: #f3f4f6;
  border-radius: 10px;
  padding: 0 12px;
  border: 2px solid transparent;
  transition: border-color 0.2s;
}

.input-box:focus-within {
  border-color: #7c3aed;
}

.input-box input {
  flex: 1;
  padding: 12px 0;
  border: none;
  background: transparent;
  font-size: 16px;
  font-weight: 500;
  color: #1f2937;
  width: 100%;
}

.input-box input:focus {
  outline: none;
}

.unit {
  color: #9ca3af;
  font-size: 14px;
}

.btn-quick {
  width: 100%;
  padding: 14px;
  background: #1f2937;
  color: #fff;
  border: none;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-quick:hover:not(:disabled) {
  background: #374151;
}

.btn-quick:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 结果区域 */
.results-wrapper {
  max-width: 900px;
  margin: 0 auto;
  padding: 0 24px 40px;
}

.state-loading {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 60px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.loader {
  width: 40px;
  height: 40px;
  border: 3px solid #e5e7eb;
  border-top-color: #7c3aed;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

.state-loading span {
  color: #6b7280;
  font-size: 15px;
}

.state-empty {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 60px;
  text-align: center;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.state-empty p {
  font-size: 18px;
  font-weight: 500;
  color: #1f2937;
  margin-bottom: 8px;
}

.state-empty span {
  color: #6b7280;
}

/* 结果列表 */
.results-section {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  overflow: hidden;
}

.results-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e5e7eb;
}

.results-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
}

.results-count {
  font-size: 14px;
  color: #6b7280;
}

.spots-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
  padding: 20px;
}

.spot-card {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: transform 0.2s, box-shadow 0.2s;
}

.spot-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.12);
}

.spot-image {
  position: relative;
  aspect-ratio: 16/10;
  overflow: hidden;
}

.spot-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #e5e7eb 0%, #f3f4f6 100%);
}

.spot-rank {
  position: absolute;
  top: 12px;
  left: 12px;
  width: 28px;
  height: 28px;
  background: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 700;
  color: #6b7280;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.spot-rank.gold {
  background: linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%);
  color: #fff;
}

.spot-rank.silver {
  background: linear-gradient(135deg, #9ca3af 0%, #6b7280 100%);
  color: #fff;
}

.spot-rank.bronze {
  background: linear-gradient(135deg, #d97706 0%, #b45309 100%);
  color: #fff;
}

.spot-body {
  padding: 16px;
}

.spot-name {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 8px;
}

.spot-desc {
  font-size: 13px;
  color: #6b7280;
  line-height: 1.5;
  margin-bottom: 12px;
}

.spot-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #6b7280;
}

.meta-item svg {
  opacity: 0.7;
}

.meta-item.price {
  color: #dc2626;
  font-weight: 600;
}

/* 响应式 */
@media (max-width: 900px) {
  .cards-container {
    grid-template-columns: 1fr;
  }
  
  .main-card {
    order: 1;
  }
  
  .quick-card {
    order: 2;
  }
}

@media (max-width: 640px) {
  .hero-section {
    padding: 40px 16px 32px;
  }
  
  .hero-title {
    font-size: 28px;
  }
  
  .feature-card {
    padding: 24px;
  }
  
  .form-row {
    grid-template-columns: 1fr;
  }
  
  .results-wrapper {
    padding: 0 16px 32px;
  }
  
  .spots-grid {
    grid-template-columns: 1fr;
  }
}
</style>
