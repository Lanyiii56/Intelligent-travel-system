<template>
  <div class="spots-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1 class="page-title">🏞️ 发现景点</h1>
      <p class="page-subtitle">探索精选目的地，开启您的旅程</p>
    </div>

    <!-- 筛选区域 -->
    <div class="filter-card">
      <div class="filter-header">
        <span class="filter-icon">🔍</span>
        <span class="filter-title">智能筛选</span>
      </div>
      <div class="filter-body">
        <div class="filter-group">
          <label class="filter-label">年龄</label>
          <div class="input-with-suffix">
            <input type="number" v-model.number="age" min="1" max="100" />
            <span class="suffix">岁</span>
          </div>
        </div>
        <div class="filter-group">
          <label class="filter-label">可用时间</label>
          <div class="input-with-suffix">
            <input type="number" v-model.number="time" min="30" step="30" />
            <span class="suffix">分钟</span>
          </div>
        </div>
        <div class="filter-actions">
          <button @click="onFilter" class="btn-filter">
            <span>✨</span> 筛选
          </button>
          <button @click="reload" class="btn-reset">
            <span>🔄</span> 重置
          </button>
        </div>
      </div>
    </div>

    <!-- 景点列表 -->
    <div class="spots-section">
      <div class="spots-header">
        <span class="spots-count">共 {{ spots.length }} 个景点</span>
      </div>
      
      <div v-if="loading" class="loading-state">
        <div class="loading-spinner"></div>
        <span>加载中...</span>
      </div>
      
      <div v-else-if="spots.length === 0" class="empty-state">
        <div class="empty-state-icon">🏝️</div>
        <p class="empty-state-text">暂无符合条件的景点</p>
        <button @click="reload" class="btn-reset">查看全部景点</button>
      </div>
      
      <div v-else class="spots-grid">
        <router-link 
          v-for="s in spots" 
          :key="s.id" 
          :to="`/spots/${s.id}`"
          class="spot-card"
        >
          <div class="spot-image">
            <img v-if="s.imageUrl" :src="s.imageUrl" :alt="s.name" />
            <div v-else class="spot-placeholder">🏞️</div>
            <div class="spot-badge" v-if="s.priceMin">¥{{ s.priceMin }}起</div>
          </div>
          <div class="spot-content">
            <h3 class="spot-name">{{ s.name }}</h3>
            <div class="spot-tags">
              <span class="tag">⏱️ {{ s.playTime }}分钟</span>
              <span class="tag">👥 {{ s.ageMin }}-{{ s.ageMax }}岁</span>
            </div>
            <p class="spot-desc" v-if="s.description">
              {{ s.description.replace(/<[^>]*>/g, '').slice(0, 60) }}...
            </p>
          </div>
          <div class="spot-footer">
            <span class="view-detail">查看详情 →</span>
          </div>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { listSpots, filterSpots } from '@/api/spots';

const spots = ref<any[]>([]);
const age = ref<number>(20);
const time = ref<number>(180);
const loading = ref(false);

async function reload() {
  loading.value = true;
  try {
    const res = await listSpots();
    spots.value = res.data || [];
  } catch (e) { 
    console.error(e); 
  } finally {
    loading.value = false;
  }
}

async function onFilter() {
  loading.value = true;
  try {
    const res = await filterSpots(age.value, time.value);
    spots.value = res.data || [];
  } catch (e) { 
    console.error(e); 
  } finally {
    loading.value = false;
  }
}

onMounted(reload);
</script>

<style scoped>
.spots-page {
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
  margin-bottom: 32px;
}

.page-title {
  font-size: 36px;
  font-weight: 700;
  color: white;
  margin-bottom: 8px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.page-subtitle {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.9);
}

/* 筛选卡片 */
.filter-card {
  background: white;
  border-radius: 20px;
  padding: 24px;
  margin-bottom: 32px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
}

.filter-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}

.filter-icon {
  font-size: 24px;
}

.filter-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a2e;
}

.filter-body {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  align-items: flex-end;
}

.filter-group {
  flex: 1;
  min-width: 150px;
}

.filter-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #666;
  margin-bottom: 8px;
}

.input-with-suffix {
  position: relative;
}

.input-with-suffix input {
  padding-right: 50px;
}

.input-with-suffix .suffix {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  color: #999;
  font-size: 14px;
}

.filter-actions {
  display: flex;
  gap: 12px;
}

.btn-filter {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 14px 28px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.btn-filter:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.btn-reset {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 14px 28px;
  background: #f5f5f5;
  color: #666;
  border-radius: 12px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.btn-reset:hover {
  background: #eee;
}

/* 景点区域 */
.spots-section {
  background: white;
  border-radius: 20px;
  padding: 32px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
}

.spots-header {
  margin-bottom: 24px;
}

.spots-count {
  font-size: 14px;
  color: #888;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px;
  color: #667eea;
  gap: 16px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #e0e0e0;
  border-top-color: #667eea;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.spots-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}

.spot-card {
  background: #f8f9fa;
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
}

.spot-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 16px 50px rgba(0, 0, 0, 0.12);
}

.spot-image {
  height: 180px;
  position: relative;
  overflow: hidden;
}

.spot-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.spot-card:hover .spot-image img {
  transform: scale(1.08);
}

.spot-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  font-size: 56px;
}

.spot-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 6px 12px;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
}

.spot-content {
  padding: 20px;
  flex: 1;
}

.spot-name {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 12px;
}

.spot-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.tag {
  padding: 4px 10px;
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
  border-radius: 6px;
  font-size: 12px;
}

.spot-desc {
  font-size: 14px;
  color: #888;
  line-height: 1.5;
}

.spot-footer {
  padding: 16px 20px;
  border-top: 1px solid #eee;
}

.view-detail {
  font-size: 14px;
  color: #667eea;
  font-weight: 500;
}

.spot-card:hover .view-detail {
  color: #764ba2;
}

@media (max-width: 768px) {
  .filter-body {
    flex-direction: column;
  }
  
  .filter-actions {
    width: 100%;
  }
  
  .btn-filter, .btn-reset {
    flex: 1;
    justify-content: center;
  }
}
</style>
