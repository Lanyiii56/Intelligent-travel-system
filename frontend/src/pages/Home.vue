<template>
  <div class="home-page">
    <!-- Hero 区域 -->
    <section class="hero">
      <div class="hero-content">
        <h1 class="hero-title">探索世界的美好</h1>
        <p class="hero-subtitle">智能推荐 · 个性化行程 · 发现地域特色</p>
        <div class="hero-actions">
          <router-link to="/recommend" class="btn-hero-primary">
            <span>✨</span> 智能推荐
          </router-link>
          <router-link to="/spots" class="btn-hero-secondary">
            <span>🏞️</span> 浏览景点
          </router-link>
        </div>
      </div>
      <div class="hero-decoration">
        <div class="floating-card card-1">🏔️</div>
        <div class="floating-card card-2">🌊</div>
        <div class="floating-card card-3">🏯</div>
      </div>
    </section>

    <!-- 热门景点 -->
    <section class="hot-spots">
      <div class="section-header">
        <h2 class="section-title">🔥 热门景点</h2>
        <router-link to="/spots" class="view-all">查看全部 →</router-link>
      </div>
      
      <div v-if="spots.length === 0" class="empty-state">
        <div class="empty-state-icon">🏝️</div>
        <p class="empty-state-text">暂无景点数据</p>
      </div>
      
      <div v-else class="spots-grid">
        <router-link 
          v-for="s in spots.slice(0, 6)" 
          :key="s.id" 
          :to="`/spots/${s.id}`"
          class="spot-card"
        >
          <div class="spot-image">
            <img v-if="s.imageUrl" :src="s.imageUrl" :alt="s.name" />
            <div v-else class="spot-placeholder">🏞️</div>
          </div>
          <div class="spot-info">
            <h3 class="spot-name">{{ s.name }}</h3>
            <div class="spot-meta">
              <span class="spot-time">⏱️ {{ s.playTime }} 分钟</span>
              <span class="spot-price" v-if="s.priceMin">💰 ¥{{ s.priceMin }}起</span>
            </div>
          </div>
        </router-link>
      </div>
    </section>

    <!-- 功能特色 -->
    <section class="features">
      <h2 class="section-title">为什么选择我们</h2>
      <div class="features-grid">
        <div class="feature-card">
          <div class="feature-icon">🎯</div>
          <h3>智能推荐</h3>
          <p>根据年龄、时间智能匹配最适合的景点</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">📍</div>
          <h3>地域特色</h3>
          <p>深度挖掘当地文化，体验原汁原味</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">💳</div>
          <h3>便捷预订</h3>
          <p>一键预订门票，省时省心</p>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { listSpots } from '@/api/spots';

const spots = ref<any[]>([]);

onMounted(async () => {
  try {
    const res = await listSpots();
    spots.value = res.data || [];
  } catch (e) {
    console.error(e);
  }
});
</script>

<style scoped>
.home-page {
  animation: fadeInUp 0.5s ease;
}

/* Hero 区域 */
.hero {
  background: white;
  border-radius: 24px;
  padding: 60px 40px;
  margin-bottom: 40px;
  position: relative;
  overflow: hidden;
}

.hero-content {
  position: relative;
  z-index: 2;
  max-width: 600px;
}

.hero-title {
  font-size: 48px;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 16px;
  line-height: 1.2;
}

.hero-subtitle {
  font-size: 18px;
  color: #666;
  margin-bottom: 32px;
}

.hero-actions {
  display: flex;
  gap: 16px;
}

.btn-hero-primary,
.btn-hero-secondary {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 16px 32px;
  border-radius: 16px;
  font-size: 16px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.btn-hero-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 8px 30px rgba(102, 126, 234, 0.4);
}

.btn-hero-primary:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 40px rgba(102, 126, 234, 0.5);
  color: white;
}

.btn-hero-secondary {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
}

.btn-hero-secondary:hover {
  background: rgba(102, 126, 234, 0.2);
}

.hero-decoration {
  position: absolute;
  right: 40px;
  top: 50%;
  transform: translateY(-50%);
}

.floating-card {
  position: absolute;
  width: 80px;
  height: 80px;
  background: white;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  animation: float 3s ease-in-out infinite;
}

.card-1 { right: 0; top: -60px; animation-delay: 0s; }
.card-2 { right: 100px; top: 20px; animation-delay: 0.5s; }
.card-3 { right: 40px; top: 100px; animation-delay: 1s; }

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

/* 热门景点 */
.hot-spots {
  background: white;
  border-radius: 24px;
  padding: 40px;
  margin-bottom: 40px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.section-title {
  font-size: 24px;
  font-weight: 700;
  color: #1a1a2e;
}

.view-all {
  color: #667eea;
  font-weight: 500;
  transition: all 0.3s;
}

.view-all:hover {
  transform: translateX(4px);
}

.spots-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.spot-card {
  background: #f8f9fa;
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.spot-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.1);
}

.spot-image {
  height: 160px;
  overflow: hidden;
}

.spot-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.spot-card:hover .spot-image img {
  transform: scale(1.05);
}

.spot-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  font-size: 48px;
}

.spot-info {
  padding: 20px;
}

.spot-name {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 12px;
}

.spot-meta {
  display: flex;
  gap: 16px;
  font-size: 14px;
  color: #666;
}

/* 功能特色 */
.features {
  background: white;
  border-radius: 24px;
  padding: 40px;
  text-align: center;
}

.features .section-title {
  margin-bottom: 40px;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 32px;
}

.feature-card {
  padding: 32px 24px;
  border-radius: 16px;
  background: #f8f9fa;
  transition: all 0.3s ease;
}

.feature-card:hover {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
}

.feature-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.feature-card h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 8px;
}

.feature-card p {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
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

/* 响应式 */
@media (max-width: 768px) {
  .hero {
    padding: 40px 24px;
  }
  
  .hero-title {
    font-size: 32px;
  }
  
  .hero-decoration {
    display: none;
  }
  
  .hero-actions {
    flex-direction: column;
  }
  
  .btn-hero-primary,
  .btn-hero-secondary {
    justify-content: center;
  }
}
</style>
