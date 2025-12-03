<template>
  <div class="hotels-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">🏨 酒店预订</h1>
      <p class="page-subtitle">精选优质酒店，让旅途更舒适</p>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-section">
      <div class="search-bar">
        <input 
          type="text" 
          v-model="searchKeyword" 
          placeholder="搜索酒店名称或地址..."
          @keyup.enter="handleSearch"
        />
        <button class="search-btn" @click="handleSearch">🔍</button>
      </div>
      
      <div class="filter-options">
        <select v-model="filters.regionId" @change="loadHotels">
          <option :value="undefined">全部区域</option>
          <option :value="1">市中心</option>
          <option :value="2">景区周边</option>
          <option :value="3">商业区</option>
          <option :value="4">交通枢纽</option>
        </select>
        
        <select v-model="filters.stars" @change="loadHotels">
          <option :value="undefined">全部星级</option>
          <option :value="5">五星级</option>
          <option :value="4">四星级</option>
          <option :value="3">三星级</option>
          <option :value="2">经济型</option>
        </select>
        
        <select v-model="filters.sort" @change="loadHotels">
          <option value="">默认排序</option>
          <option value="rating">评分最高</option>
          <option value="price">价格最低</option>
        </select>
      </div>
    </div>

    <!-- 加载状态 -->
    <div class="loading-state" v-if="loading">
      <div class="loading-spinner"></div>
      <span>加载中...</span>
    </div>

    <!-- 酒店列表 -->
    <div class="hotels-grid" v-else-if="hotels.length > 0">
      <div 
        class="hotel-card" 
        v-for="hotel in hotels" 
        :key="hotel.id"
        @click="goToDetail(hotel.id)"
      >
        <div class="hotel-image">
          <img :src="hotel.imageUrl || defaultImage" :alt="hotel.name" />
          <div class="hotel-stars">
            <span v-for="i in hotel.stars" :key="i">⭐</span>
          </div>
        </div>
        <div class="hotel-info">
          <h3 class="hotel-name">{{ hotel.name }}</h3>
          <p class="hotel-address">📍 {{ hotel.address }}</p>
          <div class="hotel-rating">
            <span class="rating-score">{{ hotel.rating?.toFixed(1) || '暂无' }}</span>
            <span class="rating-text">{{ getRatingText(hotel.rating) }}</span>
          </div>
          <div class="hotel-facilities" v-if="hotel.facilities">
            <span 
              class="facility-tag" 
              v-for="(facility, index) in parseFacilities(hotel.facilities).slice(0, 3)" 
              :key="index"
            >
              {{ facility }}
            </span>
          </div>
          <div class="hotel-price">
            <span class="price-label">¥</span>
            <span class="price-value">{{ hotel.priceMin }}</span>
            <span class="price-unit">起/晚</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div class="empty-state" v-else>
      <div class="empty-icon">🏨</div>
      <p>暂无符合条件的酒店</p>
      <button class="btn-reset" @click="resetFilters">重置筛选</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { getHotels, type Hotel, type HotelSearchParams } from '@/api/hotel';

const router = useRouter();
const route = useRoute();

const hotels = ref<Hotel[]>([]);
const loading = ref(false);
const searchKeyword = ref('');
const defaultImage = 'https://images.unsplash.com/photo-1566073771259-6a8506099945?w=400';

const filters = reactive<HotelSearchParams>({
  regionId: undefined,
  stars: undefined,
  sort: undefined
});

// 从路由参数获取区域ID
onMounted(() => {
  if (route.query.regionId) {
    filters.regionId = Number(route.query.regionId);
  }
  if (route.query.lat && route.query.lng) {
    // 如果有经纬度参数，可以用于附近搜索
  }
  loadHotels();
});

async function loadHotels() {
  loading.value = true;
  try {
    const params: HotelSearchParams = { ...filters };
    if (searchKeyword.value) {
      params.keyword = searchKeyword.value;
    }
    const res = await getHotels(params);
    hotels.value = res.data || [];
  } catch (error) {
    console.error('加载酒店列表失败:', error);
  } finally {
    loading.value = false;
  }
}

function handleSearch() {
  loadHotels();
}

function resetFilters() {
  filters.regionId = undefined;
  filters.stars = undefined;
  filters.sort = undefined;
  searchKeyword.value = '';
  loadHotels();
}

function goToDetail(hotelId: number) {
  router.push(`/hotels/${hotelId}`);
}

function getRatingText(rating: number | undefined): string {
  if (!rating) return '';
  if (rating >= 4.5) return '超赞';
  if (rating >= 4.0) return '很棒';
  if (rating >= 3.5) return '不错';
  if (rating >= 3.0) return '一般';
  return '待提升';
}

function parseFacilities(facilities: string): string[] {
  try {
    return JSON.parse(facilities);
  } catch {
    return facilities.split(',');
  }
}
</script>

<style scoped>
.hotels-page {
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
  font-size: 32px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin-bottom: 8px;
}

.page-subtitle {
  color: #666;
  font-size: 16px;
}

/* 筛选区域 */
.filter-section {
  background: white;
  border-radius: 16px;
  padding: 20px 24px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.search-bar input {
  flex: 1;
  padding: 12px 16px;
  border: 2px solid #f0f0f0;
  border-radius: 12px;
  font-size: 15px;
  transition: border-color 0.3s;
}

.search-bar input:focus {
  outline: none;
  border-color: #667eea;
}

.search-btn {
  padding: 12px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.search-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.filter-options {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.filter-options select {
  padding: 10px 16px;
  border: 2px solid #f0f0f0;
  border-radius: 10px;
  font-size: 14px;
  background: white;
  cursor: pointer;
  transition: border-color 0.3s;
}

.filter-options select:focus {
  outline: none;
  border-color: #667eea;
}

/* 酒店列表 */
.hotels-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
}

.hotel-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.hotel-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.hotel-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.hotel-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s;
}

.hotel-card:hover .hotel-image img {
  transform: scale(1.1);
}

.hotel-stars {
  position: absolute;
  top: 12px;
  left: 12px;
  background: rgba(0, 0, 0, 0.6);
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
}

.hotel-info {
  padding: 20px;
}

.hotel-name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.hotel-address {
  font-size: 14px;
  color: #666;
  margin-bottom: 12px;
}

.hotel-rating {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.rating-score {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 4px 10px;
  border-radius: 6px;
  font-weight: 600;
  font-size: 14px;
}

.rating-text {
  color: #667eea;
  font-size: 14px;
  font-weight: 500;
}

.hotel-facilities {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}

.facility-tag {
  background: #f5f5f5;
  color: #666;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
}

.hotel-price {
  display: flex;
  align-items: baseline;
  gap: 2px;
}

.price-label {
  color: #ff6b6b;
  font-size: 14px;
}

.price-value {
  color: #ff6b6b;
  font-size: 24px;
  font-weight: 700;
}

.price-unit {
  color: #999;
  font-size: 12px;
}

/* 加载状态 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px;
  color: #666;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #f0f0f0;
  border-top-color: #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 80px 20px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.empty-state p {
  color: #666;
  font-size: 16px;
  margin-bottom: 24px;
}

.btn-reset {
  padding: 12px 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 15px;
  cursor: pointer;
  transition: transform 0.2s;
}

.btn-reset:hover {
  transform: translateY(-2px);
}

/* 响应式 */
@media (max-width: 768px) {
  .filter-options {
    flex-direction: column;
  }
  
  .filter-options select {
    width: 100%;
  }
  
  .hotels-grid {
    grid-template-columns: 1fr;
  }
}
</style>
