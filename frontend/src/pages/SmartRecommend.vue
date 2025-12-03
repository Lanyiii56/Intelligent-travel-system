<template>
  <div class="smart-recommend-page">
    <!-- 渐变背景头部 -->
    <div class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">智能行程规划</h1>
        <p class="hero-subtitle">根据您的需求，定制专属旅行路线</p>
      </div>

      <!-- 搜索卡片 -->
      <div class="search-card">
        <!-- 基本信息 -->
        <div class="form-section">
          <h3 class="section-title">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/>
              <circle cx="12" cy="10" r="3"/>
            </svg>
            基本信息
          </h3>
          <div class="form-grid">
            <div class="form-item">
              <label>目的地</label>
              <el-select 
                v-model="form.regionId" 
                placeholder="输入城市名搜索，如：宜宾、四川" 
                size="large"
                filterable
                :filter-method="filterRegions"
                @visible-change="onSelectVisibleChange"
              >
                <el-option-group
                  v-for="province in filteredRegions"
                  :key="province.id"
                  :label="province.name"
                >
                  <!-- 如果省份没有子城市，直接显示省份本身 -->
                  <el-option
                    v-if="province.cities.length === 0"
                    :label="province.name"
                    :value="province.id"
                  />
                  <!-- 显示子城市 -->
                  <el-option
                    v-for="city in province.cities"
                    :key="city.id"
                    :label="city.name"
                    :value="city.id"
                  />
                </el-option-group>
              </el-select>
            </div>

            <div class="form-item">
              <label>年龄</label>
              <el-input v-model.number="form.age" placeholder="请输入年龄" size="large" type="number" :min="1" :max="120" />
            </div>

            <div class="form-item">
              <label>游玩时长（小时）</label>
              <el-input v-model.number="form.playTimeHours" placeholder="请输入时长" size="large" type="number" :min="1" :max="72" />
            </div>

            <div class="form-item">
              <label>人数</label>
              <el-input-number v-model="form.peopleCount" :min="1" :max="50" controls-position="right" size="large" />
            </div>
          </div>
        </div>

        <!-- 偏好设置 -->
        <div class="form-section">
          <h3 class="section-title">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="3"/>
              <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"/>
            </svg>
            偏好设置
            <span class="optional">选填</span>
          </h3>
          <div class="form-grid two-cols">
            <div class="form-item">
              <label>预算上限</label>
              <el-input-number
                v-model="form.budget"
                :min="0"
                :step="100"
                placeholder="不限"
                controls-position="right"
                size="large"
              />
            </div>

            <div class="form-item">
              <label>游玩偏好</label>
              <el-select v-model="form.preference" placeholder="不限" clearable size="large">
                <el-option label="自然风光" value="观光" />
                <el-option label="主题乐园" value="游乐" />
                <el-option label="人文历史" value="文化" />
                <el-option label="美食之旅" value="美食" />
                <el-option label="休闲购物" value="购物" />
              </el-select>
            </div>
          </div>
        </div>

        <!-- 搜索按钮 -->
        <button class="btn-search" @click="onRecommend" :disabled="loading">
          <svg v-if="!loading" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z"/>
          </svg>
          <span class="loading-spinner" v-else></span>
          {{ loading ? '规划中...' : '生成行程' }}
        </button>
      </div>
    </div>

    <!-- 结果区域 -->
    <div class="results-wrapper" v-if="result">
      <!-- 行程概览 -->
      <div class="trip-summary">
        <p>{{ result.summary }}</p>
        <button class="btn-save" @click="onSaveItinerary">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/>
          </svg>
          收藏行程
        </button>
      </div>

      <!-- 统计数据 -->
      <div class="stats-row">
        <div class="stat-item">
          <span class="stat-value">{{ formatTime(result.route.totalTime) }}</span>
          <span class="stat-label">总时长</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">{{ result.route.totalDistance }}km</span>
          <span class="stat-label">总路程</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">{{ result.spots.length }}</span>
          <span class="stat-label">景点数</span>
        </div>
        <div class="stat-item highlight">
          <span class="stat-value">¥{{ result.cost.perPersonCost }}</span>
          <span class="stat-label">人均费用</span>
        </div>
      </div>

      <!-- 主内容区 -->
      <div class="content-grid">
        <!-- 左侧：行程时间线 -->
        <div class="itinerary-panel">
          <h3 class="panel-title">行程安排</h3>
          
          <div class="timeline">
            <div 
              v-for="(spot, index) in result.spots" 
              :key="spot.id" 
              class="timeline-item"
              @click="goToSpot(spot.id)"
            >
              <div class="timeline-dot">
                <span>{{ index + 1 }}</span>
              </div>
              <div class="timeline-card">
                <div class="card-img">
                  <img v-if="spot.imageUrl" :src="spot.imageUrl" :alt="spot.name" />
                  <div v-else class="img-placeholder"></div>
                </div>
                <div class="card-info">
                  <h4>{{ spot.name }}</h4>
                  <p>{{ spot.reason }}</p>
                  <div class="card-tags">
                    <span class="tag time">{{ spot.playTime }}分钟</span>
                    <span class="tag price" v-if="spot.priceMax">¥{{ spot.priceMin }}-{{ spot.priceMax }}</span>
                  </div>
                </div>
              </div>
              <div class="timeline-line" v-if="index < result.spots.length - 1">
                <span class="travel-info">{{ result.route.points[index]?.travelTime || 15 }}分钟</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧：费用和建议 -->
        <div class="side-panel">
          <!-- 路线建议 -->
          <div class="suggestion-box">
            <h4>
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="10"/>
                <line x1="12" y1="16" x2="12" y2="12"/>
                <line x1="12" y1="8" x2="12.01" y2="8"/>
              </svg>
              路线建议
            </h4>
            <p>{{ result.route.suggestion }}</p>
          </div>

          <!-- 费用明细 -->
          <div class="cost-box">
            <h4>费用明细</h4>
            <div class="cost-items">
              <div class="cost-item">
                <span>门票费用</span>
                <span>¥{{ result.cost.ticketCost }}</span>
              </div>
              <div class="cost-item">
                <span>交通费用</span>
                <span>¥{{ result.cost.transportCost }}</span>
              </div>
              <div class="cost-item">
                <span>餐饮费用</span>
                <span>¥{{ result.cost.mealCost }}</span>
              </div>
            </div>
            <div class="cost-total">
              <span>总计</span>
              <span class="total-amount">¥{{ result.cost.totalCost }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 美食推荐 -->
      <div class="food-panel" v-if="foods.length > 0">
        <h3 class="panel-title">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M18 8h1a4 4 0 0 1 0 8h-1"/>
            <path d="M2 8h16v9a4 4 0 0 1-4 4H6a4 4 0 0 1-4-4V8z"/>
            <line x1="6" y1="1" x2="6" y2="4"/>
            <line x1="10" y1="1" x2="10" y2="4"/>
            <line x1="14" y1="1" x2="14" y2="4"/>
          </svg>
          当地美食推荐
        </h3>
        <div class="food-grid">
          <div v-for="food in foods" :key="food.id" class="food-card">
            <div class="food-img">
              <img v-if="food.imageUrl" :src="food.imageUrl" :alt="food.name" />
              <div v-else class="img-placeholder"></div>
              <span class="food-rating">{{ food.rating }}</span>
            </div>
            <div class="food-info">
              <h4>{{ food.name }}</h4>
              <p class="food-category">{{ food.category }}</p>
              <p class="food-desc">{{ food.description }}</p>
              <div class="food-meta">
                <span class="food-price">¥{{ food.priceMin }}-{{ food.priceMax }}/人</span>
                <span class="food-tags" v-if="food.tags">
                  {{ food.tags.split(',').slice(0, 2).join(' · ') }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 地图 -->
      <div class="map-panel" v-if="result.spots.length > 0">
        <h3 class="panel-title">路线地图</h3>
        <div class="map-wrapper">
          <TravelMap :spots="result.spots" :route-info="result.route" />
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else-if="hasSearched && !loading" class="empty-section">
      <div class="empty-card">
        <div class="empty-icon">🔍</div>
        <p>未找到符合条件的行程</p>
        <span>试试调整筛选条件</span>
      </div>
    </div>

    <!-- 初始状态 -->
    <div v-else-if="!hasSearched" class="initial-section">
      <div class="initial-card">
        <h2>开始规划您的旅程</h2>
        <p>选择目的地和出行信息，我们将为您生成最优行程</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { smartRecommend, saveItinerary, type SmartRecommendResponse } from '@/api/recommend';
import { getAllRegions, type Region } from '@/api/region';
import { recommendFoods, type Food } from '@/api/food';
import TravelMap from '@/components/TravelMap.vue';
import { useAuthStore } from '@/modules/auth/store';

const router = useRouter();
const authStore = useAuthStore();

const form = reactive({
  regionId: null as number | null,
  age: 25,
  playTimeHours: 8, // 用小时作为输入单位
  peopleCount: 2,
  budget: undefined as number | undefined,
  preference: ''
});

// 计算属性：将小时转换为分钟
const playTimeMinutes = computed(() => form.playTimeHours * 60);

const regions = ref<Region[]>([]);
const result = ref<SmartRecommendResponse | null>(null);
const foods = ref<Food[]>([]);
const loading = ref(false);
const hasSearched = ref(false);

// 将地区按省份分组
interface GroupedRegion {
  id: number;
  name: string;
  cities: Region[];
}

// 搜索关键词
const searchKeyword = ref('');

const groupedRegions = computed<GroupedRegion[]>(() => {
  // 获取所有省份（parentId 为空）
  const provinces = regions.value.filter(r => !r.parentId);
  
  return provinces.map(province => ({
    id: province.id,
    name: province.name,
    cities: regions.value.filter(r => r.parentId === province.id)
  }));
});

// 过滤后的地区列表
const filteredRegions = computed<GroupedRegion[]>(() => {
  const keyword = searchKeyword.value.trim().toLowerCase();
  
  // 如果没有搜索关键词，返回所有地区
  if (!keyword) {
    return groupedRegions.value;
  }
  
  const result: GroupedRegion[] = [];
  
  for (const province of groupedRegions.value) {
    // 检查省份名是否匹配
    const provinceMatch = province.name.toLowerCase().includes(keyword);
    
    // 过滤匹配的城市
    const matchedCities = province.cities.filter(city => 
      city.name.toLowerCase().includes(keyword)
    );
    
    // 如果省份匹配，显示该省份下所有城市
    if (provinceMatch) {
      result.push({
        id: province.id,
        name: province.name,
        cities: province.cities
      });
    } 
    // 如果有匹配的城市，只显示匹配的城市
    else if (matchedCities.length > 0) {
      result.push({
        id: province.id,
        name: province.name,
        cities: matchedCities
      });
    }
  }
  
  return result;
});

// 搜索过滤方法
function filterRegions(query: string) {
  searchKeyword.value = query;
}

// 下拉框显示/隐藏时重置搜索
function onSelectVisibleChange(visible: boolean) {
  if (!visible) {
    searchKeyword.value = '';
  }
}

onMounted(async () => {
  try {
    const res = await getAllRegions();
    regions.value = res.data || [];
  } catch (e) {
    console.error('获取地区失败', e);
  }
});

async function onRecommend() {
  if (!form.regionId) {
    ElMessage.warning('请选择地区');
    return;
  }

  loading.value = true;
  hasSearched.value = true;

  try {
    // 获取景点推荐
    const res = await smartRecommend({
      regionId: form.regionId,
      age: form.age,
      playTime: playTimeMinutes.value,
      peopleCount: form.peopleCount,
      budget: form.budget,
      preference: form.preference
    });
    result.value = res.data;

    // 获取美食推荐
    try {
      const foodRes = await recommendFoods(
        form.regionId,
        form.budget ? form.budget / form.peopleCount : undefined,
        3
      );
      console.log('美食推荐响应:', foodRes);
      foods.value = foodRes.data || [];
      console.log('foods.value:', foods.value);
    } catch (foodErr) {
      console.error('获取美食推荐失败', foodErr);
      foods.value = [];
    }

    if (result.value?.spots.length === 0) {
      ElMessage.info('未找到符合条件的景点，请调整筛选条件');
    }
  } catch (e) {
    console.error(e);
    ElMessage.error('获取推荐失败，请稍后重试');
    result.value = null;
    foods.value = [];
  } finally {
    loading.value = false;
  }
}

function formatTime(minutes: number): string {
  const hours = Math.floor(minutes / 60);
  const mins = minutes % 60;
  if (hours > 0) {
    return mins > 0 ? `${hours}小时${mins}分` : `${hours}小时`;
  }
  return `${mins}分钟`;
}

function goToSpot(id: number) {
  router.push(`/spots/${id}`);
}

// 保存行程
async function onSaveItinerary() {
  // 检查登录状态
  if (!authStore.isLoggedIn) {
    ElMessage.warning('请先登录后再收藏行程');
    router.push('/login');
    return;
  }

  if (!result.value || result.value.spots.length === 0) {
    ElMessage.warning('没有可保存的行程');
    return;
  }

  const userId = authStore.user?.id;
  if (!userId) {
    ElMessage.warning('用户信息获取失败，请重新登录');
    return;
  }

  const name = `${getRegionName(form.regionId)}${playTimeMinutes.value >= 480 ? '一日游' : '半日游'}`;
  const itineraryData = JSON.stringify(result.value);

  try {
    await saveItinerary(userId, name, itineraryData);
    ElMessage.success('行程保存成功！');
  } catch (e) {
    console.error(e);
    ElMessage.error('保存失败，请稍后重试');
  }
}

function getRegionName(regionId: number | null): string {
  if (!regionId) return '';
  const region = regions.value.find(r => r.id === regionId);
  return region ? region.name : '';
}
</script>

<style scoped>
.smart-recommend-page {
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
  margin-bottom: 40px;
}

.hero-title {
  font-size: 36px;
  font-weight: 600;
  color: #fff;
  margin-bottom: 12px;
}

.hero-subtitle {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.85);
}

/* 搜索卡片 */
.search-card {
  background: #fff;
  border-radius: 20px;
  padding: 32px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
}

.form-section {
  margin-bottom: 28px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 20px;
}

.section-title svg {
  color: #7c3aed;
}

.optional {
  font-size: 12px;
  font-weight: 400;
  color: #9ca3af;
  margin-left: 8px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.form-grid.two-cols {
  grid-template-columns: repeat(2, 1fr);
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-item label {
  font-size: 14px;
  font-weight: 500;
  color: #6b7280;
}

.form-item :deep(.el-select),
.form-item :deep(.el-input-number),
.form-item :deep(.el-input) {
  width: 100%;
}

/* 通用输入框样式 */
.form-item :deep(.el-input .el-input__wrapper) {
  background: #f3f4f6 !important;
  box-shadow: none !important;
  border: none !important;
  border-radius: 10px;
  padding: 8px 12px;
  min-height: 42px;
}

.form-item :deep(.el-input .el-input__wrapper:hover) {
  box-shadow: 0 0 0 2px #7c3aed !important;
}

.form-item :deep(.el-input .el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px #7c3aed !important;
}

.form-item :deep(.el-input .el-input__inner) {
  color: #1f2937 !important;
  -webkit-text-fill-color: #1f2937 !important;
}

.form-item :deep(.el-input .el-input__inner::placeholder) {
  color: #9ca3af !important;
  -webkit-text-fill-color: #9ca3af !important;
}

/* 隐藏 number 输入框的上下箭头 */
.form-item :deep(.el-input input[type="number"]::-webkit-inner-spin-button),
.form-item :deep(.el-input input[type="number"]::-webkit-outer-spin-button) {
  -webkit-appearance: none;
  margin: 0;
}

.form-item :deep(.el-input input[type="number"]) {
  -moz-appearance: textfield;
}

/* Select 下拉框样式 */
.form-item :deep(.el-select) {
  width: 100%;
}

.form-item :deep(.el-select .el-select__wrapper) {
  background: #f3f4f6 !important;
  box-shadow: none !important;
  border: none !important;
  border-radius: 10px;
  padding: 8px 12px;
  min-height: 42px;
}

.form-item :deep(.el-select .el-select__wrapper:hover),
.form-item :deep(.el-select .el-select__wrapper.is-hovering) {
  box-shadow: 0 0 0 2px #7c3aed !important;
}

.form-item :deep(.el-select .el-select__wrapper.is-focused) {
  box-shadow: 0 0 0 2px #7c3aed !important;
}

/* 选中的文字颜色 */
.form-item :deep(.el-select .el-select__selection),
.form-item :deep(.el-select .el-select__selected-item),
.form-item :deep(.el-select .el-input__inner) {
  color: #1f2937 !important;
  -webkit-text-fill-color: #1f2937 !important;
}

/* 搜索输入框样式 - 聚焦时也要显示文字 */
.form-item :deep(.el-select .el-select__input) {
  color: #1f2937 !important;
  -webkit-text-fill-color: #1f2937 !important;
  background: transparent !important;
}

/* 聚焦时隐藏 placeholder，显示搜索框 */
.form-item :deep(.el-select .el-select__wrapper.is-focused .el-select__selected-item) {
  color: #1f2937 !important;
  -webkit-text-fill-color: #1f2937 !important;
}

/* placeholder 颜色 */
.form-item :deep(.el-select .el-select__placeholder),
.form-item :deep(.el-select .el-input__inner::placeholder) {
  color: #9ca3af !important;
  -webkit-text-fill-color: #9ca3af !important;
}

/* 下拉箭头颜色 */
.form-item :deep(.el-select .el-select__caret),
.form-item :deep(.el-select .el-select__suffix) {
  color: #6b7280 !important;
}

/* 修复 filterable 模式下输入框背景覆盖问题 */
.form-item :deep(.el-select .el-select__input-wrapper) {
  background: transparent !important;
}

.form-item :deep(.el-select .el-select__input-wrapper input) {
  background: transparent !important;
  color: #1f2937 !important;
  -webkit-text-fill-color: #1f2937 !important;
}

/* 搜索按钮 */
.btn-search {
  width: 100%;
  padding: 16px;
  background: linear-gradient(135deg, #7c3aed 0%, #a855f7 100%);
  color: #fff;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  margin-top: 8px;
  transition: transform 0.2s, box-shadow 0.2s;
}

.btn-search:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(124, 58, 237, 0.4);
}

.btn-search:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.loading-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 结果区域 */
.results-wrapper {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 24px 40px;
}

/* 行程概览 */
.trip-summary {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  padding: 20px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.trip-summary p {
  flex: 1;
  font-size: 15px;
  color: #374151;
  line-height: 1.6;
}

.btn-save {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: linear-gradient(135deg, #7c3aed 0%, #a855f7 100%);
  color: #fff;
  border: none;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: transform 0.2s;
  white-space: nowrap;
  margin-left: 20px;
}

.btn-save:hover {
  transform: translateY(-2px);
}

/* 统计数据 */
.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.stat-item {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  padding: 20px;
  text-align: center;
}

.stat-item.highlight {
  background: linear-gradient(135deg, #7c3aed 0%, #a855f7 100%);
}

.stat-item.highlight .stat-value,
.stat-item.highlight .stat-label {
  color: #fff;
}

.stat-value {
  display: block;
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
}

.stat-label {
  font-size: 13px;
  color: #6b7280;
  margin-top: 4px;
}

/* 主内容区 */
.content-grid {
  display: grid;
  grid-template-columns: 1fr 340px;
  gap: 20px;
}

/* 行程面板 */
.itinerary-panel {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  padding: 24px;
}

.panel-title {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 24px;
}

/* 时间线 */
.timeline {
  position: relative;
}

.timeline-item {
  position: relative;
  padding-left: 48px;
  padding-bottom: 24px;
  cursor: pointer;
}

.timeline-dot {
  position: absolute;
  left: 0;
  top: 0;
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #7c3aed 0%, #a855f7 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(124, 58, 237, 0.3);
}

.timeline-dot span {
  color: #fff;
  font-size: 14px;
  font-weight: 700;
}

.timeline-card {
  display: flex;
  gap: 16px;
  padding: 16px;
  background: #f9fafb;
  border-radius: 12px;
  transition: all 0.2s;
}

.timeline-card:hover {
  background: #f3f4f6;
  transform: translateX(4px);
}

.card-img {
  width: 100px;
  height: 75px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.card-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.img-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #e5e7eb 0%, #f3f4f6 100%);
}

.card-info {
  flex: 1;
  min-width: 0;
}

.card-info h4 {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 6px;
}

.card-info p {
  font-size: 13px;
  color: #6b7280;
  line-height: 1.5;
  margin-bottom: 10px;
}

.card-tags {
  display: flex;
  gap: 8px;
}

.tag {
  padding: 4px 10px;
  background: #fff;
  border-radius: 6px;
  font-size: 12px;
  color: #6b7280;
}

.tag.time {
  color: #7c3aed;
}

.tag.price {
  color: #dc2626;
}

.timeline-line {
  position: absolute;
  left: 15px;
  top: 40px;
  bottom: 0;
  width: 2px;
  background: #e5e7eb;
}

.travel-info {
  position: absolute;
  left: 24px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 11px;
  color: #9ca3af;
  background: #fff;
  padding: 2px 8px;
  border-radius: 4px;
}

/* 侧边面板 */
.side-panel {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.suggestion-box {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  padding: 20px;
}

.suggestion-box h4 {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 12px;
}

.suggestion-box h4 svg {
  color: #7c3aed;
}

.suggestion-box p {
  font-size: 14px;
  color: #6b7280;
  line-height: 1.6;
}

.cost-box {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  padding: 20px;
}

.cost-box h4 {
  font-size: 15px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 16px;
}

.cost-items {
  border-bottom: 1px solid #e5e7eb;
  padding-bottom: 12px;
  margin-bottom: 12px;
}

.cost-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  font-size: 14px;
  color: #6b7280;
}

.cost-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 4px;
}

.total-amount {
  font-size: 24px;
  font-weight: 700;
  color: #7c3aed;
}

/* 美食推荐 */
.food-panel {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  padding: 24px;
  margin-top: 20px;
}

.food-panel .panel-title {
  display: flex;
  align-items: center;
  gap: 10px;
}

.food-panel .panel-title svg {
  color: #f59e0b;
}

.food-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.food-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: transform 0.2s, box-shadow 0.2s;
}

.food-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.food-img {
  position: relative;
  height: 140px;
  overflow: hidden;
}

.food-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.food-rating {
  position: absolute;
  top: 10px;
  right: 10px;
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  color: #fff;
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
}

.food-info {
  padding: 16px;
}

.food-info h4 {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 4px;
}

.food-category {
  font-size: 12px;
  color: #f59e0b;
  font-weight: 500;
  margin-bottom: 8px;
}

.food-desc {
  font-size: 13px;
  color: #6b7280;
  line-height: 1.5;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.food-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.food-price {
  font-size: 14px;
  font-weight: 600;
  color: #dc2626;
}

.food-tags {
  font-size: 12px;
  color: #9ca3af;
}

/* 地图 */
.map-panel {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  padding: 24px;
  margin-top: 20px;
}

.map-wrapper {
  border-radius: 12px;
  overflow: hidden;
}

/* 空状态 */
.empty-section {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 24px 40px;
}

.empty-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  padding: 60px;
  text-align: center;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-card p {
  font-size: 18px;
  font-weight: 500;
  color: #1f2937;
  margin-bottom: 8px;
}

.empty-card span {
  color: #6b7280;
}

/* 初始状态 */
.initial-section {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 24px 40px;
}

.initial-card {
  background: linear-gradient(135deg, rgba(255,255,255,0.2) 0%, rgba(255,255,255,0.1) 100%);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 60px;
  text-align: center;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.initial-card h2 {
  font-size: 28px;
  font-weight: 600;
  color: #fff;
  margin-bottom: 16px;
}

.initial-card p {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.85);
}

/* 响应式 */
@media (max-width: 992px) {
  .content-grid {
    grid-template-columns: 1fr;
  }
  
  .side-panel {
    order: -1;
  }
  
  .stats-row {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .hero-section {
    padding: 40px 16px 32px;
  }
  
  .hero-title {
    font-size: 28px;
  }
  
  .search-card {
    padding: 24px;
  }
  
  .form-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .form-grid.two-cols {
    grid-template-columns: 1fr;
  }
  
  .results-wrapper,
  .empty-section,
  .initial-section {
    padding: 0 16px 32px;
  }
  
  .trip-summary {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
  
  .btn-save {
    margin-left: 0;
  }
  
  .timeline-card {
    flex-direction: column;
  }
  
  .card-img {
    width: 100%;
    height: 140px;
  }
}

@media (max-width: 480px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
  
  .stats-row {
    grid-template-columns: 1fr;
  }
}
</style>
