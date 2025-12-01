<template>
  <div class="detail-page" v-if="spot">
    <!-- 返回按钮 -->
    <router-link to="/spots" class="back-btn">
      <span>←</span> 返回景点列表
    </router-link>

    <!-- 主要内容区 -->
    <div class="detail-container">
      <!-- 左侧：图片和信息 -->
      <div class="detail-main">
        <div class="spot-hero">
          <div class="spot-image-wrapper">
            <img v-if="spot.imageUrl" :src="spot.imageUrl" :alt="spot.name" class="spot-image" />
            <div v-else class="spot-placeholder">🏞️</div>
          </div>
          <div class="spot-hero-content">
            <h1 class="spot-title">{{ spot.name }}</h1>
            <div class="spot-meta">
              <span class="meta-item">⏱️ {{ spot.playTime }} 分钟</span>
              <span class="meta-item">👥 {{ spot.ageMin }}-{{ spot.ageMax }}岁</span>
            </div>
          </div>
        </div>

        <!-- 价格和操作 -->
        <div class="action-card">
          <div class="price-section">
            <span class="price-label">门票价格</span>
            <div class="price-range">
              <span class="price-value">¥{{ spot.priceMin }}</span>
              <span class="price-separator">~</span>
              <span class="price-value">¥{{ spot.priceMax }}</span>
            </div>
          </div>
          <div class="action-buttons">
            <button @click="favorite" class="btn-favorite" :class="{ 'is-favorited': isFavorited }">
              <span>{{ isFavorited ? '❤️' : '🤍' }}</span>
              {{ isFavorited ? '已收藏' : '收藏' }}
            </button>
            <button @click="toReserve" class="btn-reserve">
              <span>🎫</span> 立即预订
            </button>
          </div>
        </div>

        <!-- 景点介绍 -->
        <div class="info-card">
          <h2 class="card-title">📖 景点介绍</h2>
          <div class="description" v-html="spot.description"></div>
        </div>

        <!-- 评论区 -->
        <div class="comments-card">
          <h2 class="card-title">💬 游客评论 ({{ comments.length }})</h2>
          
          <!-- 发表评论 -->
          <div class="comment-form">
            <textarea 
              v-model="newComment" 
              placeholder="分享您的旅行体验..."
              class="comment-input"
              rows="3"
            ></textarea>
            <button @click="postComment" class="btn-submit" :disabled="!newComment.trim()">
              发表评论
            </button>
          </div>

          <!-- 评论列表 -->
          <div v-if="comments.length === 0" class="empty-comments">
            <span class="empty-icon">💭</span>
            <p>暂无评论，来做第一个评论的人吧！</p>
          </div>
          <div v-else class="comments-list">
            <div v-for="c in comments" :key="c.id" class="comment-item">
              <div class="comment-avatar">👤</div>
              <div class="comment-body">
                <div class="comment-header">
                  <span class="comment-author">游客{{ c.userId || c.id }}</span>
                  <span class="comment-time">{{ formatTime(c.createdAt) }}</span>
                </div>
                <p class="comment-content">{{ c.content }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：快捷信息 -->
      <div class="detail-sidebar">
        <div class="sidebar-card">
          <h3 class="sidebar-title">📍 快捷信息</h3>
          <div class="info-list">
            <div class="info-item">
              <span class="info-icon">⏰</span>
              <div class="info-content">
                <span class="info-label">游玩时长</span>
                <span class="info-value">{{ spot.playTime }} 分钟</span>
              </div>
            </div>
            <div class="info-item">
              <span class="info-icon">👨‍👩‍👧‍👦</span>
              <div class="info-content">
                <span class="info-label">适合年龄</span>
                <span class="info-value">{{ spot.ageMin }}-{{ spot.ageMax }}岁</span>
              </div>
            </div>
            <div class="info-item">
              <span class="info-icon">💰</span>
              <div class="info-content">
                <span class="info-label">票价范围</span>
                <span class="info-value">¥{{ spot.priceMin }}-{{ spot.priceMax }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="sidebar-card tips-card">
          <h3 class="sidebar-title">💡 温馨提示</h3>
          <ul class="tips-list">
            <li>建议提前预订门票</li>
            <li>请携带有效身份证件</li>
            <li>注意天气变化，做好防护</li>
          </ul>
        </div>
      </div>
    </div>
  </div>

  <!-- 加载状态 -->
  <div v-else class="loading-container">
    <div class="loading-card">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getSpot } from '@/api/spots';
import { listComments, addComment, toggleFavorite } from '@/api/social';

const route = useRoute();
const router = useRouter();
const id = Number(route.params.id);
const spot = ref<any | null>(null);
const comments = ref<any[]>([]);
const newComment = ref('');
const isFavorited = ref(false);

async function load() {
  const res = await getSpot(id);
  spot.value = res.data;
  const c = await listComments(id);
  comments.value = c.data || [];
}

async function favorite() {
  await toggleFavorite(1, id);
  isFavorited.value = !isFavorited.value;
}

function toReserve() {
  router.push({ path: '/reservation', query: { spotId: id } });
}

async function postComment() {
  if (!newComment.value.trim()) return;
  await addComment(1, id, newComment.value);
  newComment.value = '';
  await load();
}

function formatTime(time: string) {
  if (!time) return '刚刚';
  return new Date(time).toLocaleDateString('zh-CN');
}

onMounted(load);
</script>

<style scoped>
.detail-page {
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

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  color: #667eea;
  font-weight: 500;
  margin-bottom: 24px;
  transition: all 0.3s;
}

.back-btn:hover {
  background: white;
  transform: translateX(-4px);
}

.detail-container {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 32px;
}

/* 主要内容区 */
.detail-main {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.spot-hero {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
}

.spot-image-wrapper {
  height: 320px;
  overflow: hidden;
}

.spot-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.spot-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  font-size: 80px;
}

.spot-hero-content {
  padding: 28px;
}

.spot-title {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 16px;
}

.spot-meta {
  display: flex;
  gap: 20px;
}

.meta-item {
  font-size: 15px;
  color: #666;
}

/* 操作卡片 */
.action-card {
  background: white;
  border-radius: 20px;
  padding: 28px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
}

.price-label {
  font-size: 14px;
  color: #888;
  display: block;
  margin-bottom: 8px;
}

.price-range {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.price-value {
  font-size: 28px;
  font-weight: 700;
  color: #667eea;
}

.price-separator {
  color: #ccc;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

.btn-favorite {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 24px;
  background: #f5f5f5;
  color: #666;
  border-radius: 12px;
  font-weight: 600;
  transition: all 0.3s;
}

.btn-favorite:hover {
  background: #eee;
}

.btn-favorite.is-favorited {
  background: #fef2f2;
  color: #ef4444;
}

.btn-reserve {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px;
  font-weight: 600;
  transition: all 0.3s;
}

.btn-reserve:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

/* 信息卡片 */
.info-card, .comments-card {
  background: white;
  border-radius: 20px;
  padding: 28px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
}

.card-title {
  font-size: 20px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 20px;
}

.description {
  font-size: 15px;
  line-height: 1.8;
  color: #555;
}

/* 评论区 */
.comment-form {
  margin-bottom: 28px;
}

.comment-input {
  resize: none;
  margin-bottom: 12px;
}

.btn-submit {
  padding: 12px 28px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 10px;
  font-weight: 600;
  transition: all 0.3s;
}

.btn-submit:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.btn-submit:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.empty-comments {
  text-align: center;
  padding: 40px;
  color: #999;
}

.empty-icon {
  font-size: 48px;
  display: block;
  margin-bottom: 12px;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comment-item {
  display: flex;
  gap: 16px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
}

.comment-avatar {
  width: 44px;
  height: 44px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.comment-body {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.comment-author {
  font-weight: 600;
  color: #1a1a2e;
}

.comment-time {
  font-size: 13px;
  color: #999;
}

.comment-content {
  font-size: 14px;
  color: #555;
  line-height: 1.6;
}

/* 侧边栏 */
.detail-sidebar {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.sidebar-card {
  background: white;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
}

.sidebar-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 20px;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 14px;
}

.info-icon {
  width: 44px;
  height: 44px;
  background: rgba(102, 126, 234, 0.1);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.info-content {
  display: flex;
  flex-direction: column;
}

.info-label {
  font-size: 13px;
  color: #888;
}

.info-value {
  font-size: 15px;
  font-weight: 600;
  color: #1a1a2e;
}

.tips-list {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.tips-list li {
  font-size: 14px;
  color: #666;
  padding-left: 20px;
  position: relative;
}

.tips-list li::before {
  content: '•';
  position: absolute;
  left: 0;
  color: #667eea;
}

/* 加载状态 */
.loading-container {
  display: flex;
  justify-content: center;
  padding: 80px 20px;
}

.loading-card {
  background: white;
  border-radius: 20px;
  padding: 60px 80px;
  text-align: center;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
}

.loading-spinner {
  width: 48px;
  height: 48px;
  border: 4px solid #e0e0e0;
  border-top-color: #667eea;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

@media (max-width: 1024px) {
  .detail-container {
    grid-template-columns: 1fr;
  }
  
  .detail-sidebar {
    flex-direction: row;
    flex-wrap: wrap;
  }
  
  .sidebar-card {
    flex: 1;
    min-width: 280px;
  }
}

@media (max-width: 640px) {
  .action-card {
    flex-direction: column;
    gap: 20px;
    align-items: stretch;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .btn-favorite, .btn-reserve {
    justify-content: center;
  }
}
</style>
