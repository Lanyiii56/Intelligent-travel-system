<template>
  <div class="profile-page">
    <!-- 用户信息卡片 -->
    <div class="user-card">
      <div class="user-avatar">
        <span>👤</span>
      </div>
      <div class="user-info">
        <h1 class="user-name">旅行者</h1>
        <p class="user-desc">探索世界，发现美好</p>
      </div>
      <div class="user-stats">
        <div class="stat-item">
          <span class="stat-value">{{ favs.length }}</span>
          <span class="stat-label">收藏</span>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <span class="stat-value">{{ orders.length }}</span>
          <span class="stat-label">订单</span>
        </div>
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="content-grid">
      <!-- 收藏列表 -->
      <div class="section-card">
        <div class="section-header">
          <h2 class="section-title">
            <span class="section-icon">❤️</span>
            我的收藏
          </h2>
          <span class="section-count">{{ favs.length }} 个景点</span>
        </div>
        
        <div v-if="loading" class="loading-state">
          <div class="loading-spinner"></div>
        </div>
        
        <div v-else-if="favs.length === 0" class="empty-state">
          <div class="empty-icon">💝</div>
          <p>还没有收藏的景点</p>
          <router-link to="/spots" class="btn-explore">去发现</router-link>
        </div>
        
        <div v-else class="favorites-list">
          <div v-for="f in favs" :key="f.id" class="favorite-item">
            <div class="favorite-icon">🏞️</div>
            <div class="favorite-info">
              <span class="favorite-name">景点 #{{ f.spotId }}</span>
              <span class="favorite-date">{{ formatDate(f.createdAt) }}</span>
            </div>
            <router-link :to="`/spots/${f.spotId}`" class="btn-view">
              查看 →
            </router-link>
          </div>
        </div>
      </div>

      <!-- 订单列表 -->
      <div class="section-card">
        <div class="section-header">
          <h2 class="section-title">
            <span class="section-icon">🎫</span>
            我的订单
          </h2>
          <span class="section-count">{{ orders.length }} 个订单</span>
        </div>
        
        <div v-if="loading" class="loading-state">
          <div class="loading-spinner"></div>
        </div>
        
        <div v-else-if="orders.length === 0" class="empty-state">
          <div class="empty-icon">📋</div>
          <p>还没有订单记录</p>
          <router-link to="/spots" class="btn-explore">去预订</router-link>
        </div>
        
        <div v-else class="orders-list">
          <div v-for="o in orders" :key="o.id" class="order-item">
            <div class="order-header">
              <span class="order-id">订单 #{{ o.id }}</span>
              <span 
                class="order-status" 
                :class="getStatusClass(o.orderStatus)"
              >
                {{ getStatusText(o.orderStatus) }}
              </span>
            </div>
            <div class="order-body">
              <div class="order-detail">
                <span class="detail-label">景点</span>
                <span class="detail-value">景点 #{{ o.spotId }}</span>
              </div>
              <div class="order-detail">
                <span class="detail-label">金额</span>
                <span class="detail-value price">¥{{ o.amount }}</span>
              </div>
            </div>
            <div class="order-footer">
              <span class="order-time">{{ formatDate(o.createdAt) }}</span>
              <router-link :to="`/spots/${o.spotId}`" class="btn-view-order">
                查看景点
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { favoriteList } from '@/api/social';
import { listOrders } from '@/api/order';

const favs = ref<any[]>([]);
const orders = ref<any[]>([]);
const loading = ref(true);

onMounted(async () => {
  try {
    const f = await favoriteList(1);
    favs.value = f.data || [];
    const o = await listOrders(1);
    orders.value = o.data || [];
  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
});

function formatDate(date: string) {
  if (!date) return '刚刚';
  return new Date(date).toLocaleDateString('zh-CN');
}

function getStatusClass(status: string) {
  const map: Record<string, string> = {
    'PENDING': 'status-pending',
    'PAID': 'status-paid',
    'COMPLETED': 'status-completed',
    'CANCELLED': 'status-cancelled'
  };
  return map[status] || 'status-pending';
}

function getStatusText(status: string) {
  const map: Record<string, string> = {
    'PENDING': '待支付',
    'PAID': '已支付',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  };
  return map[status] || status;
}
</script>

<style scoped>
.profile-page {
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

/* 用户卡片 */
.user-card {
  background: white;
  border-radius: 24px;
  padding: 40px;
  margin-bottom: 32px;
  display: flex;
  align-items: center;
  gap: 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
}

.user-avatar {
  width: 100px;
  height: 100px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48px;
  flex-shrink: 0;
}

.user-info {
  flex: 1;
}

.user-name {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 8px;
}

.user-desc {
  font-size: 15px;
  color: #888;
}

.user-stats {
  display: flex;
  align-items: center;
  gap: 32px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 32px;
  font-weight: 700;
  color: #667eea;
}

.stat-label {
  font-size: 14px;
  color: #888;
}

.stat-divider {
  width: 1px;
  height: 40px;
  background: #eee;
}

/* 内容网格 */
.content-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 24px;
}

/* 区块卡片 */
.section-card {
  background: white;
  border-radius: 20px;
  padding: 28px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 20px;
  font-weight: 600;
  color: #1a1a2e;
}

.section-icon {
  font-size: 24px;
}

.section-count {
  font-size: 13px;
  color: #888;
  background: #f5f5f5;
  padding: 6px 14px;
  border-radius: 20px;
}

/* 加载状态 */
.loading-state {
  display: flex;
  justify-content: center;
  padding: 40px;
}

.loading-spinner {
  width: 32px;
  height: 32px;
  border: 3px solid #e0e0e0;
  border-top-color: #667eea;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 40px 20px;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 12px;
}

.empty-state p {
  color: #888;
  margin-bottom: 16px;
}

.btn-explore {
  display: inline-block;
  padding: 10px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 10px;
  font-weight: 500;
  transition: all 0.3s;
}

.btn-explore:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
  color: white;
}

/* 收藏列表 */
.favorites-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.favorite-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 12px;
  transition: all 0.3s;
}

.favorite-item:hover {
  background: #f0f0f0;
}

.favorite-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.favorite-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.favorite-name {
  font-weight: 600;
  color: #1a1a2e;
}

.favorite-date {
  font-size: 13px;
  color: #888;
}

.btn-view {
  padding: 8px 16px;
  color: #667eea;
  font-weight: 500;
  font-size: 14px;
  transition: all 0.3s;
}

.btn-view:hover {
  color: #764ba2;
}

/* 订单列表 */
.orders-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-item {
  background: #f8f9fa;
  border-radius: 16px;
  padding: 20px;
  transition: all 0.3s;
}

.order-item:hover {
  background: #f0f0f0;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.order-id {
  font-weight: 600;
  color: #1a1a2e;
}

.order-status {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.status-pending {
  background: #fef3c7;
  color: #d97706;
}

.status-paid {
  background: #dbeafe;
  color: #2563eb;
}

.status-completed {
  background: #d1fae5;
  color: #059669;
}

.status-cancelled {
  background: #fee2e2;
  color: #dc2626;
}

.order-body {
  display: flex;
  gap: 24px;
  margin-bottom: 16px;
}

.order-detail {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-label {
  font-size: 13px;
  color: #888;
}

.detail-value {
  font-weight: 600;
  color: #1a1a2e;
}

.detail-value.price {
  color: #667eea;
  font-size: 18px;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #eee;
}

.order-time {
  font-size: 13px;
  color: #888;
}

.btn-view-order {
  color: #667eea;
  font-weight: 500;
  font-size: 14px;
}

.btn-view-order:hover {
  color: #764ba2;
}

@media (max-width: 768px) {
  .user-card {
    flex-direction: column;
    text-align: center;
    padding: 32px 24px;
  }
  
  .user-stats {
    width: 100%;
    justify-content: center;
  }
  
  .content-grid {
    grid-template-columns: 1fr;
  }
}
</style>
