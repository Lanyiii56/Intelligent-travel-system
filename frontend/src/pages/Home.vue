<template>
  <div class="home-page">
    <!-- Hero 区域 -->
    <section class="hero">
      <div class="hero-content">
        <div class="hero-badge">🌟 智游天下</div>
        <h1 class="hero-title">探索世界的美好</h1>
        <p class="hero-subtitle">智能推荐 · 个性化行程 · 发现地域特色</p>
        
        <!-- 搜索框 -->
        <div class="hero-search">
          <input 
            v-model="searchKeyword" 
            type="text" 
            placeholder="搜索景点、美食、目的地..." 
            @keyup.enter="handleSearch"
          />
          <button class="search-btn" @click="handleSearch">
            🔍 搜索
          </button>
        </div>
        
        <div class="hero-actions">
          <router-link to="/recommend" class="btn-hero-primary">
            <span>✨</span> 智能推荐
          </router-link>
          <router-link to="/spots" class="btn-hero-secondary">
            <span>🏞️</span> 浏览景点
          </router-link>
        </div>
        
        <!-- 快捷标签 -->
        <div class="quick-tags">
          <span class="tag" @click="searchByTag('自然风光')">🏔️ 自然风光</span>
          <span class="tag" @click="searchByTag('历史古迹')">🏛️ 历史古迹</span>
          <span class="tag" @click="searchByTag('美食')">🍜 美食探店</span>
          <span class="tag" @click="searchByTag('亲子')">👨‍👩‍👧 亲子游</span>
        </div>
      </div>
      <div class="hero-decoration">
        <div class="floating-card card-1">🏔️</div>
        <div class="floating-card card-2">🌊</div>
        <div class="floating-card card-3">🏯</div>
        <div class="floating-card card-4">🎡</div>
      </div>
    </section>
    
    <!-- 数据统计 -->
    <section class="stats-section">
      <div class="stat-item">
        <span class="stat-number">{{ animatedStats.spots }}+</span>
        <span class="stat-label">精选景点</span>
      </div>
      <div class="stat-divider"></div>
      <div class="stat-item">
        <span class="stat-number">{{ animatedStats.users }}+</span>
        <span class="stat-label">用户信赖</span>
      </div>
      <div class="stat-divider"></div>
      <div class="stat-item">
        <span class="stat-number">{{ animatedStats.itineraries }}+</span>
        <span class="stat-label">行程规划</span>
      </div>
      <div class="stat-divider"></div>
      <div class="stat-item">
        <span class="stat-number">{{ animatedStats.regions }}+</span>
        <span class="stat-label">覆盖地区</span>
      </div>
    </section>

    <!-- 热门景点 -->
    <section class="hot-spots">
      <div class="section-header">
        <h2 class="section-title">🔥 热门景点</h2>
        <router-link to="/spots" class="view-all">查看全部 →</router-link>
      </div>
      
      <div v-if="loading" class="loading-state">
        <div class="loading-spinner"></div>
        <p>加载中...</p>
      </div>
      
      <div v-else-if="spots.length === 0" class="empty-state">
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
            <div class="spot-badge" v-if="s.rating >= 4.5">精选</div>
          </div>
          <div class="spot-info">
            <h3 class="spot-name">{{ s.name }}</h3>
            <p class="spot-desc">{{ s.description?.slice(0, 40) || '探索美丽风景' }}...</p>
            <div class="spot-meta">
              <span class="spot-rating" v-if="s.rating">⭐ {{ s.rating }}</span>
              <span class="spot-time">⏱️ {{ s.playTime || 60 }}分钟</span>
              <span class="spot-price" v-if="s.priceMin">💰 ¥{{ s.priceMin }}起</span>
            </div>
          </div>
        </router-link>
      </div>
    </section>
    
    <!-- 推荐行程 -->
    <section class="recommend-section">
      <div class="section-header">
        <h2 class="section-title">🗺️ 推荐行程</h2>
        <router-link to="/recommend" class="view-all">定制行程 →</router-link>
      </div>
      <div class="recommend-cards">
        <div class="recommend-card" @click="goToRecommend('one-day')">
          <div class="recommend-icon">☀️</div>
          <div class="recommend-info">
            <h3>一日游</h3>
            <p>轻松惬意，精华景点一网打尽</p>
          </div>
          <span class="recommend-arrow">→</span>
        </div>
        <div class="recommend-card" @click="goToRecommend('weekend')">
          <div class="recommend-icon">🌙</div>
          <div class="recommend-info">
            <h3>周末游</h3>
            <p>两天一夜，深度体验当地风情</p>
          </div>
          <span class="recommend-arrow">→</span>
        </div>
        <div class="recommend-card" @click="goToRecommend('family')">
          <div class="recommend-icon">👨‍👩‍👧‍👦</div>
          <div class="recommend-info">
            <h3>亲子游</h3>
            <p>寓教于乐，亲子时光更美好</p>
          </div>
          <span class="recommend-arrow">→</span>
        </div>
      </div>
    </section>

    <!-- 功能特色 -->
    <section class="features">
      <h2 class="section-title">✨ 为什么选择我们</h2>
      <div class="features-grid">
        <div class="feature-card">
          <div class="feature-icon">🎯</div>
          <h3>智能推荐</h3>
          <p>AI 算法根据年龄、时间、偏好智能匹配最适合的景点和路线</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">📍</div>
          <h3>地域特色</h3>
          <p>深度挖掘当地文化，推荐地道美食，体验原汁原味的风土人情</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">🗺️</div>
          <h3>路线优化</h3>
          <p>智能规划最优路线，节省时间和体力，让旅行更轻松</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">💳</div>
          <h3>便捷预订</h3>
          <p>一键预订门票，实时查看订单状态，省时省心</p>
        </div>
      </div>
    </section>
    
    <!-- 用户评价 -->
    <section class="testimonials">
      <h2 class="section-title">💬 用户评价</h2>
      <div class="testimonials-grid">
        <div class="testimonial-card">
          <div class="testimonial-content">
            "智能推荐真的很准！根据我的时间和喜好推荐的行程非常合理，省去了很多规划时间。"
          </div>
          <div class="testimonial-author">
            <div class="author-avatar">👩</div>
            <div class="author-info">
              <span class="author-name">小美</span>
              <span class="author-rating">⭐⭐⭐⭐⭐</span>
            </div>
          </div>
        </div>
        <div class="testimonial-card">
          <div class="testimonial-content">
            "带孩子出游用这个APP规划行程，推荐的亲子景点都很棒，孩子玩得很开心！"
          </div>
          <div class="testimonial-author">
            <div class="author-avatar">👨</div>
            <div class="author-info">
              <span class="author-name">张先生</span>
              <span class="author-rating">⭐⭐⭐⭐⭐</span>
            </div>
          </div>
        </div>
        <div class="testimonial-card">
          <div class="testimonial-content">
            "路线规划功能太实用了，自动计算距离和时间，再也不用担心走冤枉路了。"
          </div>
          <div class="testimonial-author">
            <div class="author-avatar">👧</div>
            <div class="author-info">
              <span class="author-name">旅行达人</span>
              <span class="author-rating">⭐⭐⭐⭐⭐</span>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { listSpots } from '@/api/spots';

const router = useRouter();
const spots = ref<any[]>([]);
const loading = ref(true);
const searchKeyword = ref('');

// 动画数字统计
const animatedStats = reactive({
  spots: 0,
  users: 0,
  itineraries: 0,
  regions: 0
});

// 目标数字
const targetStats = {
  spots: 100,
  users: 5000,
  itineraries: 2000,
  regions: 20
};

// 数字动画
function animateNumber(key: keyof typeof animatedStats, target: number) {
  const duration = 2000;
  const steps = 60;
  const increment = target / steps;
  let current = 0;
  
  const timer = setInterval(() => {
    current += increment;
    if (current >= target) {
      animatedStats[key] = target;
      clearInterval(timer);
    } else {
      animatedStats[key] = Math.floor(current);
    }
  }, duration / steps);
}

// 搜索功能
function handleSearch() {
  if (searchKeyword.value.trim()) {
    router.push(`/spots?keyword=${encodeURIComponent(searchKeyword.value)}`);
  }
}

// 标签搜索
function searchByTag(tag: string) {
  router.push(`/spots?tag=${encodeURIComponent(tag)}`);
}

// 跳转到推荐页面
function goToRecommend(type: string) {
  const params: Record<string, string> = {};
  
  switch (type) {
    case 'one-day':
      params.playTimeHours = '8';
      break;
    case 'weekend':
      params.playTimeHours = '16';
      break;
    case 'family':
      params.preference = '亲子';
      break;
  }
  
  const query = new URLSearchParams(params).toString();
  router.push(`/recommend${query ? '?' + query : ''}`);
}

onMounted(async () => {
  // 启动数字动画
  Object.keys(targetStats).forEach(key => {
    animateNumber(key as keyof typeof animatedStats, targetStats[key as keyof typeof targetStats]);
  });
  
  try {
    const res = await listSpots();
    spots.value = res.data || [];
  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped>
.home-page {
  animation: fadeInUp 0.5s ease;
}

/* Hero 区域 */
.hero {
  background: linear-gradient(135deg, #ffffff 0%, #f5f3ff 100%);
  border-radius: 24px;
  padding: 60px 40px;
  margin-bottom: 24px;
  position: relative;
  overflow: hidden;
}

.hero-content {
  position: relative;
  z-index: 2;
  max-width: 600px;
}

.hero-badge {
  display: inline-block;
  padding: 8px 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 16px;
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
  margin-bottom: 24px;
}

/* 搜索框 */
.hero-search {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  max-width: 500px;
}

.hero-search input {
  flex: 1;
  padding: 16px 24px;
  border: 2px solid #e0e0e0;
  border-radius: 16px;
  font-size: 16px;
  transition: all 0.3s;
}

.hero-search input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
}

.search-btn {
  padding: 16px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 16px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.search-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.hero-actions {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}

/* 快捷标签 */
.quick-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.quick-tags .tag {
  padding: 8px 16px;
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.quick-tags .tag:hover {
  background: #667eea;
  color: white;
  border-color: #667eea;
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
.card-4 { right: 140px; top: -20px; animation-delay: 1.5s; }

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

/* 数据统计 */
.stats-section {
  display: flex;
  justify-content: space-around;
  align-items: center;
  background: white;
  border-radius: 24px;
  padding: 32px 40px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.stats-section .stat-item {
  text-align: center;
}

.stats-section .stat-number {
  display: block;
  font-size: 36px;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stats-section .stat-label {
  font-size: 14px;
  color: #666;
  margin-top: 4px;
}

.stats-section .stat-divider {
  width: 1px;
  height: 40px;
  background: #e0e0e0;
}

/* 热门景点 */
.hot-spots {
  background: white;
  border-radius: 24px;
  padding: 40px;
  margin-bottom: 24px;
}

/* 加载状态 */
.loading-state {
  text-align: center;
  padding: 60px 0;
  color: #999;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #f0f0f0;
  border-top-color: #667eea;
  border-radius: 50%;
  margin: 0 auto 16px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
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

.spot-image {
  position: relative;
}

.spot-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 4px 12px;
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a5a 100%);
  color: white;
  font-size: 12px;
  font-weight: 600;
  border-radius: 12px;
}

.spot-info {
  padding: 20px;
}

.spot-name {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 8px;
}

.spot-desc {
  font-size: 13px;
  color: #888;
  margin-bottom: 12px;
  line-height: 1.5;
}

.spot-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 13px;
  color: #666;
}

.spot-rating {
  color: #f5a623;
  font-weight: 500;
}

/* 推荐行程 */
.recommend-section {
  background: white;
  border-radius: 24px;
  padding: 40px;
  margin-bottom: 24px;
}

.recommend-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
}

.recommend-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 24px;
  background: linear-gradient(135deg, #f5f3ff 0%, #fdf2f8 100%);
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s;
}

.recommend-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 30px rgba(102, 126, 234, 0.15);
}

.recommend-icon {
  font-size: 40px;
}

.recommend-info {
  flex: 1;
}

.recommend-info h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 4px;
}

.recommend-info p {
  font-size: 14px;
  color: #666;
}

.recommend-arrow {
  font-size: 20px;
  color: #667eea;
  transition: transform 0.3s;
}

.recommend-card:hover .recommend-arrow {
  transform: translateX(4px);
}

/* 功能特色 */
.features {
  background: white;
  border-radius: 24px;
  padding: 40px;
  margin-bottom: 24px;
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

/* 用户评价 */
.testimonials {
  background: white;
  border-radius: 24px;
  padding: 40px;
  text-align: center;
}

.testimonials .section-title {
  margin-bottom: 40px;
}

.testimonials-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
}

.testimonial-card {
  background: #f8f9fa;
  border-radius: 16px;
  padding: 24px;
  text-align: left;
  transition: all 0.3s;
}

.testimonial-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.08);
}

.testimonial-content {
  font-size: 15px;
  color: #444;
  line-height: 1.8;
  margin-bottom: 20px;
  font-style: italic;
}

.testimonial-author {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-avatar {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.author-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.author-name {
  font-size: 15px;
  font-weight: 600;
  color: #1a1a2e;
}

.author-rating {
  font-size: 12px;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 0;
}

.empty-state-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.empty-state-text {
  font-size: 16px;
  color: #999;
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
  
  .hero-search {
    flex-direction: column;
  }
  
  .stats-section {
    flex-wrap: wrap;
    gap: 20px;
  }
  
  .stats-section .stat-divider {
    display: none;
  }
  
  .stats-section .stat-item {
    flex: 1 1 40%;
  }
  
  .quick-tags {
    justify-content: center;
  }
}
</style>
