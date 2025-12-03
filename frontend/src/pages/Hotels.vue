<template>
  <div class="hotels-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-decoration">
        <span class="deco-icon deco-1">🏨</span>
        <span class="deco-icon deco-2">🛏️</span>
        <span class="deco-icon deco-3">🌟</span>
        <span class="deco-icon deco-4">🔑</span>
      </div>
      <div class="title-wrapper">
        <span class="title-icon">🏨</span>
        <h1 class="page-title">酒店预订</h1>
      </div>
      <p class="page-subtitle">精选优质酒店，让旅途更舒适</p>
      <div class="header-tags">
        <span class="header-tag">🌟 品质保障</span>
        <span class="header-tag">💰 优惠价格</span>
        <span class="header-tag">🛎️ 贴心服务</span>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-section">
      <div class="search-row">
        <div class="city-selector">
          <label>📍 选择城市</label>
          <select v-model="filters.regionId" @change="loadHotels" class="city-select">
            <option :value="undefined">全部城市</option>
            <optgroup 
              v-for="province in groupedRegions" 
              :key="province.id" 
              :label="province.name"
            >
              <option 
                v-for="city in province.cities" 
                :key="city.id" 
                :value="city.id"
              >
                {{ city.name }}
              </option>
            </optgroup>
          </select>
        </div>
        <div class="search-bar">
          <input 
            type="text" 
            v-model="searchKeyword" 
            placeholder="搜索酒店名称..."
            @keyup.enter="handleSearch"
          />
          <button class="search-btn" @click="handleSearch">🔍</button>
        </div>
      </div>
      
      <div class="filter-options">
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
      
      <!-- 当前筛选条件 -->
      <div class="active-filters" v-if="filters.regionId || filters.stars">
        <span class="filter-label">当前筛选：</span>
        <span class="filter-tag" v-if="filters.regionId">
          {{ getRegionName(filters.regionId) }}
          <button @click="clearRegion">×</button>
        </span>
        <span class="filter-tag" v-if="filters.stars">
          {{ filters.stars }}星级
          <button @click="clearStars">×</button>
        </span>
        <button class="clear-all" @click="resetFilters">清除全部</button>
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
import { ref, reactive, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { getHotels, type Hotel, type HotelSearchParams } from '@/api/hotel';
import { getAllRegions, type Region } from '@/api/region';

const router = useRouter();
const route = useRoute();

const hotels = ref<Hotel[]>([]);
const regions = ref<Region[]>([]);
const loading = ref(false);
const searchKeyword = ref('');
const defaultImage = 'https://images.unsplash.com/photo-1566073771259-6a8506099945?w=400';

const filters = reactive<HotelSearchParams>({
  regionId: undefined,
  stars: undefined,
  sort: undefined
});

// 将地区按省份分组
interface GroupedRegion {
  id: number;
  name: string;
  cities: Region[];
}

const groupedRegions = computed<GroupedRegion[]>(() => {
  const provinces = regions.value.filter(r => !r.parentId);
  return provinces.map(province => ({
    id: province.id,
    name: province.name,
    cities: regions.value.filter(r => r.parentId === province.id)
  })).filter(p => p.cities.length > 0);
});

// 获取地区名称
function getRegionName(regionId: number | undefined): string {
  if (!regionId) return '';
  const region = regions.value.find(r => r.id === regionId);
  return region ? region.name : '';
}

// 清除单个筛选
function clearRegion() {
  filters.regionId = undefined;
  loadHotels();
}

function clearStars() {
  filters.stars = undefined;
  loadHotels();
}

// 从路由参数获取区域ID
onMounted(async () => {
  // 加载地区数据
  try {
    const res = await getAllRegions();
    regions.value = res.data || [];
  } catch (e) {
    console.error('获取地区失败', e);
  }
  
  if (route.query.regionId) {
    filters.regionId = Number(route.query.regionId);
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  border-radius: 24px;
  padding: 48px 24px 40px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.3);
}

/* 装饰浮动图标 */
.header-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  overflow: hidden;
}

.deco-icon {
  position: absolute;
  font-size: 24px;
  opacity: 0.2;
  animation: float 4s ease-in-out infinite;
}

.deco-1 { top: 15%; left: 10%; animation-delay: 0s; font-size: 28px; }
.deco-2 { top: 25%; right: 12%; animation-delay: 1s; font-size: 22px; }
.deco-3 { bottom: 20%; left: 15%; animation-delay: 0.5s; font-size: 26px; }
.deco-4 { bottom: 25%; right: 10%; animation-delay: 1.5s; font-size: 20px; }

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-10px) rotate(5deg); }
}

.title-wrapper {
  display: inline-flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 12px;
}

.title-icon {
  font-size: 48px;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.2));
  animation: bounce 2s ease-in-out infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
}

.page-title {
  font-size: 42px;
  font-weight: 800;
  color: #fff;
  text-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  letter-spacing: 4px;
  margin: 0;
}

.page-subtitle {
  color: rgba(255, 255, 255, 0.9);
  font-size: 18px;
  margin-bottom: 20px;
  font-weight: 500;
}

.header-tags {
  display: flex;
  justify-content: center;
  gap: 16px;
  flex-wrap: wrap;
}

.header-tag {
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  color: #fff;
  padding: 8px 18px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  border: 1px solid rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
}

.header-tag:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
}

/* 筛选区域 */
.filter-section {
  background: white;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.search-row {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
  align-items: flex-end;
}

.city-selector {
  flex: 0 0 240px;
}

.city-selector label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 8px;
}

.city-select {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #667eea;
  border-radius: 12px;
  font-size: 15px;
  background: linear-gradient(135deg, #f5f3ff 0%, #ede9fe 100%);
  color: #374151;
  cursor: pointer;
  transition: all 0.3s;
}

.city-select:focus {
  outline: none;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.3);
}

.city-select:hover {
  border-color: #764ba2;
}

.search-bar {
  flex: 1;
  display: flex;
  gap: 12px;
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

/* 当前筛选条件 */
.active-filters {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
  flex-wrap: wrap;
}

.filter-label {
  font-size: 14px;
  color: #6b7280;
}

.filter-tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
}

.filter-tag button {
  background: rgba(255, 255, 255, 0.3);
  border: none;
  color: white;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  cursor: pointer;
  font-size: 14px;
  line-height: 1;
  transition: background 0.2s;
}

.filter-tag button:hover {
  background: rgba(255, 255, 255, 0.5);
}

.clear-all {
  margin-left: auto;
  padding: 6px 14px;
  background: #f3f4f6;
  border: none;
  border-radius: 8px;
  color: #6b7280;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.clear-all:hover {
  background: #e5e7eb;
  color: #374151;
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
