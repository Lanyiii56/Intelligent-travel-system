<template>
  <div class="spots-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1 class="page-title">🏞️ 发现景点</h1>
      <p class="page-subtitle">探索精选目的地，开启您的旅程</p>
    </div>

    <!-- 搜索区域 -->
    <div class="search-card">
      <div class="search-box">
        <span class="search-icon">🔍</span>
        <input 
          type="text" 
          v-model="searchKeyword" 
          placeholder="搜索景点名称..."
          @keyup.enter="onSearch"
        />
        <button v-if="searchKeyword" class="clear-btn" @click="searchKeyword = ''; onSearch()">✕</button>
      </div>
    </div>

    <!-- 筛选区域 -->
    <div class="filter-card">
      <div class="filter-header">
        <span class="filter-icon">⚙️</span>
        <span class="filter-title">筛选条件</span>
        <button class="toggle-filter" @click="showFilter = !showFilter">
          {{ showFilter ? '收起' : '展开' }}
        </button>
      </div>
      <div class="filter-body" v-show="showFilter">
        <!-- 地区选择 -->
        <div class="filter-row">
          <div class="filter-group region-filter">
            <label class="filter-label">目的地</label>
            <el-cascader
              v-model="selectedRegion"
              :options="regionOptions"
              :props="{ value: 'id', label: 'name', children: 'cities', emitPath: false }"
              placeholder="选择地区"
              clearable
              filterable
              @change="onSearch"
            />
          </div>
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
        </div>
        <!-- 价格筛选 -->
        <div class="filter-row">
          <div class="filter-group">
            <label class="filter-label">最低价格</label>
            <div class="input-with-suffix">
              <input type="number" v-model.number="minPrice" min="0" placeholder="不限" />
              <span class="suffix">元</span>
            </div>
          </div>
          <div class="filter-group">
            <label class="filter-label">最高价格</label>
            <div class="input-with-suffix">
              <input type="number" v-model.number="maxPrice" min="0" placeholder="不限" />
              <span class="suffix">元</span>
            </div>
          </div>
          <div class="filter-actions">
            <button @click="onSearch" class="btn-filter">
              <span>✨</span> 筛选
            </button>
            <button @click="resetFilter" class="btn-reset">
              <span>🔄</span> 重置
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 景点列表 -->
    <div class="spots-section">
      <div class="spots-header">
        <span class="spots-count">共 {{ totalCount }} 个景点</span>
        <div class="sort-options">
          <span 
            class="sort-item" 
            :class="{ active: sortBy === 'default' }"
            @click="sortBy = 'default'; onSearch()"
          >默认</span>
          <span 
            class="sort-item" 
            :class="{ active: sortBy === 'price' }"
            @click="sortBy = 'price'; onSearch()"
          >价格</span>
          <span 
            class="sort-item" 
            :class="{ active: sortBy === 'time' }"
            @click="sortBy = 'time'; onSearch()"
          >时长</span>
        </div>
      </div>
      
      <div v-if="loading" class="loading-state">
        <div class="loading-spinner"></div>
        <span>加载中...</span>
      </div>
      
      <div v-else-if="spots.length === 0" class="empty-state">
        <div class="empty-state-icon">🏝️</div>
        <p class="empty-state-text">暂无符合条件的景点</p>
        <button @click="resetFilter" class="btn-reset">查看全部景点</button>
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
            <div class="spot-rating" v-if="s.rating">
              <span>⭐</span> {{ s.rating?.toFixed(1) || '4.5' }}
            </div>
          </div>
          <div class="spot-content">
            <h3 class="spot-name">{{ s.name }}</h3>
            <div class="spot-location" v-if="s.regionName">
              <span>📍</span> {{ s.regionName }}
            </div>
            <div class="spot-tags">
              <span class="tag">⏱️ {{ s.playTime }}分钟</span>
              <span class="tag">👥 {{ s.ageMin }}-{{ s.ageMax }}岁</span>
            </div>
            <p class="spot-desc" v-if="s.description">
              {{ s.description.replace(/<[^>]*>/g, '').slice(0, 60) }}...
            </p>
          </div>
          <div class="spot-footer">
            <div class="spot-price">
              <span class="price-label">门票</span>
              <span class="price-value">¥{{ s.priceMin }}-{{ s.priceMax }}</span>
            </div>
            <span class="view-detail">查看详情 →</span>
          </div>
        </router-link>
      </div>
      
      <!-- 分页 -->
      <div class="pagination" v-if="totalPages > 1">
        <button 
          class="page-btn" 
          :disabled="currentPage === 1"
          @click="goToPage(currentPage - 1)"
        >上一页</button>
        <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
        <button 
          class="page-btn" 
          :disabled="currentPage === totalPages"
          @click="goToPage(currentPage + 1)"
        >下一页</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { listSpots, searchSpots, type SpotSearchParams } from '@/api/spots';
import { getAllRegions } from '@/api/region';

// 组件名称
defineOptions({
  name: 'Spots'
});

const spots = ref<any[]>([]);
const loading = ref(false);
const showFilter = ref(true);

// 搜索条件
const searchKeyword = ref('');
const selectedRegion = ref<number | null>(null);
const age = ref<number | undefined>(undefined);
const time = ref<number | undefined>(undefined);
const minPrice = ref<number | undefined>(undefined);
const maxPrice = ref<number | undefined>(undefined);
const sortBy = ref('default');

// 分页
const currentPage = ref(1);
const pageSize = ref(12);
const totalCount = ref(0);
const totalPages = computed(() => Math.ceil(totalCount.value / pageSize.value));

// 地区数据
const regions = ref<any[]>([]);
const regionOptions = computed(() => {
  return regions.value.map(province => ({
    id: province.id,
    name: province.name,
    cities: province.cities?.map((city: any) => ({
      id: city.id,
      name: city.name
    })) || []
  }));
});

// 加载地区数据
async function loadRegions() {
  try {
    const res = await getAllRegions();
    regions.value = res.data || [];
  } catch (e) {
    console.error('加载地区失败', e);
  }
}

// 搜索景点
async function onSearch() {
  loading.value = true;
  try {
    const params: SpotSearchParams = {
      page: currentPage.value - 1,
      size: pageSize.value
    };
    
    if (searchKeyword.value) params.keyword = searchKeyword.value;
    if (selectedRegion.value) params.regionId = selectedRegion.value;
    if (age.value) params.age = age.value;
    if (time.value) params.time = time.value;
    if (minPrice.value !== undefined) params.minPrice = minPrice.value;
    if (maxPrice.value !== undefined) params.maxPrice = maxPrice.value;
    
    const res = await searchSpots(params);
    spots.value = res.data || [];
    totalCount.value = spots.value.length;
    
    // 前端排序
    if (sortBy.value === 'price') {
      spots.value.sort((a, b) => a.priceMin - b.priceMin);
    } else if (sortBy.value === 'time') {
      spots.value.sort((a, b) => a.playTime - b.playTime);
    }
  } catch (e) {
    console.error('搜索失败', e);
    // 降级到全量加载
    await loadAll();
  } finally {
    loading.value = false;
  }
}

// 加载全部景点
async function loadAll() {
  loading.value = true;
  try {
    const res = await listSpots();
    let data = res.data || [];
    
    // 前端筛选
    if (searchKeyword.value) {
      data = data.filter((s: any) => s.name.includes(searchKeyword.value));
    }
    if (selectedRegion.value) {
      data = data.filter((s: any) => s.regionId === selectedRegion.value);
    }
    if (age.value) {
      data = data.filter((s: any) => age.value! >= s.ageMin && age.value! <= s.ageMax);
    }
    if (time.value) {
      data = data.filter((s: any) => s.playTime <= time.value!);
    }
    if (minPrice.value !== undefined) {
      data = data.filter((s: any) => s.priceMin >= minPrice.value!);
    }
    if (maxPrice.value !== undefined) {
      data = data.filter((s: any) => s.priceMax <= maxPrice.value!);
    }
    
    // 排序
    if (sortBy.value === 'price') {
      data.sort((a: any, b: any) => a.priceMin - b.priceMin);
    } else if (sortBy.value === 'time') {
      data.sort((a: any, b: any) => a.playTime - b.playTime);
    }
    
    totalCount.value = data.length;
    
    // 分页
    const start = (currentPage.value - 1) * pageSize.value;
    spots.value = data.slice(start, start + pageSize.value);
  } catch (e) {
    console.error('加载失败', e);
  } finally {
    loading.value = false;
  }
}

// 重置筛选
function resetFilter() {
  searchKeyword.value = '';
  selectedRegion.value = null;
  age.value = undefined;
  time.value = undefined;
  minPrice.value = undefined;
  maxPrice.value = undefined;
  sortBy.value = 'default';
  currentPage.value = 1;
  loadAll();
}

// 翻页
function goToPage(page: number) {
  currentPage.value = page;
  loadAll();
  window.scrollTo({ top: 0, behavior: 'smooth' });
}

onMounted(() => {
  loadRegions();
  loadAll();
});
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

/* 搜索卡片 */
.search-card {
  background: white;
  border-radius: 20px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
}

.search-box {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  background: #f8f9fa;
  border-radius: 12px;
  border: 2px solid transparent;
  transition: all 0.3s;
}

.search-box:focus-within {
  border-color: #667eea;
  background: white;
}

.search-icon {
  font-size: 20px;
}

.search-box input {
  flex: 1;
  border: none;
  background: transparent;
  font-size: 16px;
  outline: none;
}

.clear-btn {
  padding: 4px 8px;
  background: #e0e0e0;
  border-radius: 50%;
  font-size: 12px;
  color: #666;
  cursor: pointer;
}

.clear-btn:hover {
  background: #ccc;
}

/* 筛选卡片 */
.filter-card {
  background: white;
  border-radius: 20px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
}

.filter-header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.filter-icon {
  font-size: 20px;
}

.filter-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a2e;
  flex: 1;
}

.toggle-filter {
  padding: 6px 16px;
  background: #f5f5f5;
  border-radius: 8px;
  font-size: 13px;
  color: #666;
  cursor: pointer;
}

.toggle-filter:hover {
  background: #eee;
}

.filter-body {
  margin-top: 20px;
}

.filter-row {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: flex-end;
  margin-bottom: 16px;
}

.filter-row:last-child {
  margin-bottom: 0;
}

.filter-group {
  flex: 1;
  min-width: 140px;
}

.region-filter {
  min-width: 200px;
}

.region-filter :deep(.el-cascader) {
  width: 100%;
}

.filter-label {
  display: block;
  font-size: 13px;
  font-weight: 500;
  color: #666;
  margin-bottom: 8px;
}

.input-with-suffix {
  position: relative;
}

.input-with-suffix input {
  width: 100%;
  padding: 12px 50px 12px 16px;
  border: 1px solid #e0e0e0;
  border-radius: 10px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.input-with-suffix input:focus {
  outline: none;
  border-color: #667eea;
}

.input-with-suffix .suffix {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  color: #999;
  font-size: 13px;
}

.filter-actions {
  display: flex;
  gap: 12px;
}

.btn-filter {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 12px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 10px;
  font-weight: 600;
  font-size: 14px;
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
  padding: 12px 24px;
  background: #f5f5f5;
  color: #666;
  border-radius: 10px;
  font-weight: 600;
  font-size: 14px;
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.spots-count {
  font-size: 14px;
  color: #888;
}

.sort-options {
  display: flex;
  gap: 8px;
}

.sort-item {
  padding: 6px 16px;
  border-radius: 8px;
  font-size: 13px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
}

.sort-item:hover {
  background: #f5f5f5;
}

.sort-item.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
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

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.empty-state-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.empty-state-text {
  color: #888;
  margin-bottom: 20px;
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
  left: 12px;
  padding: 6px 12px;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
}

.spot-rating {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 6px 12px;
  background: rgba(255, 193, 7, 0.95);
  color: #333;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 4px;
}

.spot-content {
  padding: 20px;
  flex: 1;
}

.spot-name {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 8px;
}

.spot-location {
  font-size: 13px;
  color: #888;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  gap: 4px;
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
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.spot-price {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.price-label {
  font-size: 11px;
  color: #999;
}

.price-value {
  font-size: 16px;
  font-weight: 700;
  color: #ff6b6b;
}

.view-detail {
  font-size: 14px;
  color: #667eea;
  font-weight: 500;
}

.spot-card:hover .view-detail {
  color: #764ba2;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #eee;
}

.page-btn {
  padding: 10px 24px;
  background: #f5f5f5;
  border-radius: 10px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
}

.page-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: 14px;
  color: #666;
}

@media (max-width: 768px) {
  .filter-row {
    flex-direction: column;
  }
  
  .filter-group {
    width: 100%;
  }
  
  .filter-actions {
    width: 100%;
  }
  
  .btn-filter, .btn-reset {
    flex: 1;
    justify-content: center;
  }
  
  .spots-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
}
</style>
