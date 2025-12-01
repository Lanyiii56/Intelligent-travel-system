<template>
  <div class="reservation-page">
    <!-- 返回按钮 -->
    <router-link :to="spotId ? `/spots/${spotId}` : '/spots'" class="back-btn">
      <span>←</span> 返回
    </router-link>

    <div class="reservation-container" v-if="spot">
      <!-- 景点信息卡片 -->
      <div class="spot-card">
        <div class="spot-image">
          <img v-if="spot.imageUrl" :src="spot.imageUrl" :alt="spot.name" />
          <div v-else class="spot-placeholder">🏞️</div>
        </div>
        <div class="spot-info">
          <h1 class="spot-name">{{ spot.name }}</h1>
          <div class="spot-meta">
            <span class="meta-item">⏱️ {{ spot.playTime }} 分钟</span>
            <span class="meta-item">👥 {{ spot.ageMin }}-{{ spot.ageMax }}岁</span>
          </div>
        </div>
      </div>

      <!-- 预订表单 -->
      <div class="order-card">
        <h2 class="card-title">
          <span>🎫</span> 预订门票
        </h2>

        <div class="price-info">
          <div class="price-row">
            <span class="price-label">票价范围</span>
            <span class="price-range">¥{{ spot.priceMin }} ~ ¥{{ spot.priceMax }}</span>
          </div>
          <div class="price-row highlight">
            <span class="price-label">当前票价</span>
            <span class="current-price">¥{{ spot.priceMin }}</span>
          </div>
        </div>

        <div class="divider"></div>

        <div class="quantity-section">
          <label class="quantity-label">购买数量</label>
          <div class="quantity-control">
            <button class="qty-btn" @click="decreaseCount" :disabled="count <= 1">−</button>
            <input type="number" v-model.number="count" min="1" class="qty-input" />
            <button class="qty-btn" @click="increaseCount">+</button>
          </div>
        </div>

        <div class="divider"></div>

        <div class="total-section">
          <div class="total-row">
            <span class="total-label">订单总额</span>
            <span class="total-value">¥{{ total }}</span>
          </div>
          <p class="total-hint">含 {{ count }} 张门票</p>
        </div>

        <button @click="create" class="btn-submit" :disabled="submitting">
          <span v-if="submitting" class="loading-spinner"></span>
          <template v-else>
            <span>✨</span> 确认预订
          </template>
        </button>

        <div class="order-tips">
          <h4>📋 预订须知</h4>
          <ul>
            <li>门票当日有效，过期作废</li>
            <li>请携带有效身份证件入园</li>
            <li>支持随时退款，无手续费</li>
          </ul>
        </div>
      </div>
    </div>

    <!-- 无景点状态 -->
    <div v-else class="empty-state-card">
      <div class="empty-icon">🎫</div>
      <h2>请选择景点</h2>
      <p>您还没有选择要预订的景点</p>
      <router-link to="/spots" class="btn-explore">
        <span>🏞️</span> 浏览景点
      </router-link>
    </div>

    <!-- 成功弹窗 -->
    <div v-if="showSuccess" class="success-modal">
      <div class="success-content">
        <div class="success-icon">🎉</div>
        <h2>预订成功！</h2>
        <p>订单号：#{{ orderId }}</p>
        <div class="success-actions">
          <router-link to="/profile" class="btn-view-order">查看订单</router-link>
          <router-link to="/spots" class="btn-continue">继续浏览</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { getSpot } from '@/api/spots';
import { createOrder } from '@/api/order';

const route = useRoute();
const spotId = Number(route.query.spotId || 0);
const spot = ref<any | null>(null);
const count = ref(1);
const submitting = ref(false);
const showSuccess = ref(false);
const orderId = ref(0);

onMounted(async () => {
  if (spotId) {
    const res = await getSpot(spotId);
    spot.value = res.data;
  }
});

const total = computed(() => {
  if (!spot.value) return '0.00';
  const p = spot.value.priceMin || 0;
  return (p * count.value).toFixed(2);
});

function increaseCount() {
  count.value++;
}

function decreaseCount() {
  if (count.value > 1) count.value--;
}

async function create() {
  if (!spotId) return;
  submitting.value = true;
  try {
    const amount = Number((spot.value.priceMin || 0) * count.value);
    const res = await createOrder(1, spotId, amount);
    orderId.value = res.data.id;
    showSuccess.value = true;
  } catch (e) {
    console.error(e);
    alert('预订失败，请重试');
  } finally {
    submitting.value = false;
  }
}
</script>

<style scoped>
.reservation-page {
  animation: fadeInUp 0.5s ease;
  max-width: 800px;
  margin: 0 auto;
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

.reservation-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 景点卡片 */
.spot-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  display: flex;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
}

.spot-image {
  width: 200px;
  height: 150px;
  flex-shrink: 0;
  overflow: hidden;
}

.spot-image img {
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
  font-size: 48px;
}

.spot-info {
  padding: 24px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.spot-name {
  font-size: 24px;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 12px;
}

.spot-meta {
  display: flex;
  gap: 16px;
}

.meta-item {
  font-size: 14px;
  color: #666;
}

/* 订单卡片 */
.order-card {
  background: white;
  border-radius: 20px;
  padding: 32px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
}

.card-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 22px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 28px;
}

.price-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price-label {
  font-size: 15px;
  color: #666;
}

.price-range {
  font-size: 16px;
  color: #888;
}

.price-row.highlight .price-label {
  font-weight: 600;
  color: #1a1a2e;
}

.current-price {
  font-size: 28px;
  font-weight: 700;
  color: #667eea;
}

.divider {
  height: 1px;
  background: #eee;
  margin: 24px 0;
}

.quantity-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.quantity-label {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a2e;
}

.quantity-control {
  display: flex;
  align-items: center;
  gap: 4px;
}

.qty-btn {
  width: 44px;
  height: 44px;
  padding: 0;
  background: #f5f5f5;
  color: #666;
  font-size: 20px;
  font-weight: 600;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.qty-btn:hover:not(:disabled) {
  background: #eee;
}

.qty-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.qty-input {
  width: 80px;
  height: 44px;
  text-align: center;
  font-size: 18px;
  font-weight: 600;
  border: 2px solid #eee;
  border-radius: 12px;
}

.qty-input:focus {
  border-color: #667eea;
}

.total-section {
  text-align: right;
}

.total-row {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
}

.total-label {
  font-size: 16px;
  color: #666;
}

.total-value {
  font-size: 36px;
  font-weight: 700;
  color: #667eea;
}

.total-hint {
  font-size: 13px;
  color: #888;
  margin-top: 4px;
}

.btn-submit {
  width: 100%;
  height: 56px;
  margin-top: 28px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-size: 18px;
  font-weight: 600;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  transition: all 0.3s;
}

.btn-submit:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.4);
}

.btn-submit:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.loading-spinner {
  width: 24px;
  height: 24px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.order-tips {
  margin-top: 28px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
}

.order-tips h4 {
  font-size: 15px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 12px;
}

.order-tips ul {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.order-tips li {
  font-size: 14px;
  color: #666;
  padding-left: 16px;
  position: relative;
}

.order-tips li::before {
  content: '•';
  position: absolute;
  left: 0;
  color: #667eea;
}

/* 空状态 */
.empty-state-card {
  background: white;
  border-radius: 24px;
  padding: 80px 40px;
  text-align: center;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
}

.empty-icon {
  font-size: 80px;
  margin-bottom: 24px;
}

.empty-state-card h2 {
  font-size: 24px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 8px;
}

.empty-state-card p {
  font-size: 16px;
  color: #888;
  margin-bottom: 24px;
}

.btn-explore {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 14px 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-size: 16px;
  font-weight: 600;
  border-radius: 14px;
  transition: all 0.3s;
}

.btn-explore:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
  color: white;
}

/* 成功弹窗 */
.success-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.success-content {
  background: white;
  border-radius: 24px;
  padding: 48px;
  text-align: center;
  max-width: 400px;
  animation: scaleIn 0.3s ease;
}

@keyframes scaleIn {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.success-icon {
  font-size: 72px;
  margin-bottom: 20px;
}

.success-content h2 {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 8px;
}

.success-content p {
  font-size: 16px;
  color: #888;
  margin-bottom: 28px;
}

.success-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.btn-view-order {
  padding: 14px 28px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-weight: 600;
  border-radius: 12px;
  transition: all 0.3s;
}

.btn-view-order:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
  color: white;
}

.btn-continue {
  padding: 14px 28px;
  background: #f5f5f5;
  color: #666;
  font-weight: 600;
  border-radius: 12px;
  transition: all 0.3s;
}

.btn-continue:hover {
  background: #eee;
}

@media (max-width: 640px) {
  .spot-card {
    flex-direction: column;
  }
  
  .spot-image {
    width: 100%;
    height: 180px;
  }
  
  .quantity-section {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }
  
  .quantity-control {
    justify-content: center;
  }
  
  .total-section {
    text-align: center;
  }
  
  .total-row {
    flex-direction: column;
    gap: 8px;
  }
}
</style>
