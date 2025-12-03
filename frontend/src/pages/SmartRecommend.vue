<template>
  <div class="smart-recommend-page">
    <!-- 装饰元素 -->
    <div class="decorations">
      <span class="deco deco-1">✈️</span>
      <span class="deco deco-2">🏝️</span>
      <span class="deco deco-3">🎒</span>
      <span class="deco deco-4">🗺️</span>
      <span class="deco deco-5">⛰️</span>
      <span class="deco deco-6">🌴</span>
      <span class="deco deco-7">🚂</span>
      <span class="deco deco-8">🏰</span>
      <div class="deco-circle deco-circle-1"></div>
      <div class="deco-circle deco-circle-2"></div>
      <div class="deco-circle deco-circle-3"></div>
    </div>
    
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
      <div class="trip-summary" :class="{ 'ai-powered': isAIRecommendation }">
        <div class="summary-badge" v-if="isAIRecommendation">
          <span class="ai-icon">🤖</span>
          <span>AI 智能推荐</span>
        </div>
        <p>{{ displaySummary }}</p>
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
        <div class="stat-item" :class="getCostLevel(result.cost.perPersonCost)">
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
                  <a 
                    v-if="spot.longitude && spot.latitude"
                    :href="`https://uri.amap.com/marker?position=${spot.longitude},${spot.latitude}&name=${encodeURIComponent(spot.name)}`"
                    target="_blank"
                    class="nav-btn-small"
                    @click.stop
                  >
                    🧭 导航
                  </a>
                </div>
              </div>
              <!-- 交通信息（AI 推荐时显示详细信息） -->
              <div class="timeline-line" v-if="index < result.spots.length - 1">
                <div class="transport-detail" v-if="result.transportation && result.transportation[index]" @click.stop="toggleTransportDetail(index)">
                  <div class="transport-summary">
                    <span class="transport-method">{{ getTransportIcon(result.transportation[index].method) }} {{ result.transportation[index].method }}</span>
                    <span class="transport-duration">{{ result.transportation[index].duration }}分钟</span>
                    <span class="transport-distance">{{ result.transportation[index].distance }}km</span>
                    <span class="transport-cost" v-if="result.transportation[index].cost > 0">¥{{ result.transportation[index].cost }}</span>
                    <span class="expand-icon" v-if="result.transportation[index].steps?.length">{{ expandedTransport[index] ? '▲' : '▼' }}</span>
                  </div>
                  <!-- 详细步骤（点击展开） -->
                  <div class="transport-steps" v-if="expandedTransport[index] && result.transportation[index].steps?.length">
                    <div class="step-item" v-for="(step, stepIdx) in result.transportation[index].steps" :key="stepIdx">
                      <span class="step-icon">{{ getStepIcon(step.type) }}</span>
                      <div class="step-content">
                        <div class="step-main">
                          <span class="step-line" v-if="step.line">{{ step.line }}</span>
                          <span class="step-instruction">{{ step.instruction }}</span>
                        </div>
                        <div class="step-detail" v-if="step.startStation && step.endStation">
                          <span>{{ step.startStation }}</span>
                          <span class="step-arrow">→</span>
                          <span>{{ step.endStation }}</span>
                          <span class="step-stations" v-if="step.stations">({{ step.stations }}站)</span>
                        </div>
                        <span class="step-duration">{{ step.duration }}分钟</span>
                      </div>
                    </div>
                    <div class="transport-tips" v-if="result.transportation[index].tips">
                      💡 {{ result.transportation[index].tips }}
                    </div>
                  </div>
                </div>
                <span class="travel-info" v-else>{{ result.route.points[index]?.travelTime || 15 }}分钟</span>
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

      <!-- 路线地图 -->
      <div class="route-map-panel">
        <h3 class="panel-title">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/>
            <circle cx="12" cy="10" r="3"/>
          </svg>
          路线地图
          <a 
            v-if="result.spots.length > 0"
            :href="getRouteNavUrl()"
            target="_blank"
            class="nav-all-btn"
          >
            🗺️ 在高德地图中查看完整路线
          </a>
        </h3>
        <div class="map-container">
          <div id="route-map" class="route-map"></div>
        </div>
        <div class="map-legend">
          <div class="legend-item" v-for="(spot, index) in result.spots" :key="spot.id">
            <span class="legend-number">{{ index + 1 }}</span>
            <span class="legend-name">{{ spot.name }}</span>
            <a 
              v-if="spot.longitude && spot.latitude"
              :href="`https://uri.amap.com/marker?position=${spot.longitude},${spot.latitude}&name=${encodeURIComponent(spot.name)}`"
              target="_blank"
              class="legend-nav"
              @click.stop
            >
              导航
            </a>
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
          <div v-for="food in foods" :key="food.id" class="food-card" @click="goToFoodDetail(food.id)">
            <div class="food-img">
              <img v-if="food.imageUrl" :src="food.imageUrl" :alt="food.name" />
              <div v-else class="img-placeholder">🍜</div>
              <span class="food-rating">⭐ {{ food.rating }}</span>
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
              <div class="food-action">
                <span class="view-detail">查看详情 →</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 地图 -->
      <div class="map-panel" v-if="result.spots.length > 0">
        <h3 class="panel-title">
          路线地图
          <span class="ai-route-badge" v-if="result.aiPowered">🤖 AI 路线规划</span>
        </h3>
        <div class="map-legend" v-if="result.transportation && result.transportation.length > 0">
          <span class="legend-item" v-for="(t, i) in result.transportation" :key="i">
            <span class="legend-dot" :style="{ background: getTransportColor(t.method) }"></span>
            {{ getTransportIcon(t.method) }} {{ t.method }}
          </span>
        </div>
        <div class="map-wrapper">
          <TravelMap 
            :spots="result.spots" 
            :route-info="result.route"
            :transportation="result.transportation"
            :ai-powered="result.aiPowered"
          />
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
import { ref, reactive, computed, onMounted, onBeforeMount } from 'vue';
import { useRouter, useRoute, onBeforeRouteUpdate } from 'vue-router';
import { ElMessage } from 'element-plus';
import { smartRecommend, saveItinerary, getUserItineraries, type SmartRecommendResponse } from '@/api/recommend';
import { getAllRegions, type Region } from '@/api/region';
import { recommendFoods, type Food } from '@/api/food';
import TravelMap from '@/components/TravelMap.vue';
import { useAuthStore } from '@/modules/auth/store';
import { AMAP_CONFIG } from '@/config/amap';

// 组件名称，用于 keep-alive 缓存
defineOptions({
  name: 'SmartRecommend'
});

const router = useRouter();
const route = useRoute();
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

// 交通详情展开状态
const expandedTransport = ref<Record<number, boolean>>({});

// 判断是否为 AI 推荐
const isAIRecommendation = computed(() => {
  return result.value?.summary?.includes('AI') || result.value?.summary?.includes('🤖');
});

// 显示的摘要（去掉前缀）
const displaySummary = computed(() => {
  if (!result.value?.summary) return '';
  return result.value.summary.replace('🤖 AI智能推荐：', '').replace('【AI智能推荐】', '');
});

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
  console.log('=== SmartRecommend onMounted ===');
  console.log('当前 URL:', window.location.href);
  console.log('route.query:', route.query);
  
  // 检查是否有自动推荐参数
  if (route.query.autoRecommend === 'true') {
    console.log('检测到 autoRecommend 参数！');
  }
  
  // 先获取地区数据
  try {
    const res = await getAllRegions();
    regions.value = res.data || [];
    console.log('地区数据加载完成，共', regions.value.length, '个');
  } catch (e) {
    console.error('获取地区失败', e);
  }
  
  // 检查是否需要自动推荐
  const query = route.query;
  if (query.autoRecommend === 'true' && query.regionId) {
    console.log('=== 检测到自动推荐参数 ===');
    
    // 填充表单
    form.regionId = Number(query.regionId);
    form.age = Number(query.age) || 25;
    form.playTimeHours = Number(query.playTimeHours) || 8;
    form.peopleCount = Number(query.peopleCount) || 2;
    form.budget = query.budget ? Number(query.budget) : undefined;
    form.preference = (query.preference as string) || '';
    
    console.log('表单已填充:', JSON.stringify({
      regionId: form.regionId,
      age: form.age,
      playTimeHours: form.playTimeHours,
      peopleCount: form.peopleCount
    }));
    
    // 调用推荐接口
    console.log('开始调用 onRecommend...');
    onRecommend().then(() => {
      console.log('onRecommend 完成，清除 URL 参数');
      router.replace('/recommend');
    });
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
    console.log('推荐结果:', result.value);
    console.log('交通信息:', result.value?.transportation);
    
    // 重置展开状态
    expandedTransport.value = {};

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
    } else {
      // 初始化路线地图
      initRouteMap();
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

// ==================== 地图相关 ====================
let routeMap: any = null;

// 生成完整路线导航链接
function getRouteNavUrl(): string {
  if (!result.value?.spots.length) return '';
  
  const spots = result.value.spots.filter(s => s.longitude && s.latitude);
  if (spots.length === 0) return '';
  
  // 高德地图路线规划 URL
  // 格式: https://uri.amap.com/navigation?from=lng,lat,name&to=lng,lat,name&via=lng,lat,name
  const from = spots[0];
  const to = spots[spots.length - 1];
  
  let url = `https://uri.amap.com/navigation?from=${from.longitude},${from.latitude},${encodeURIComponent(from.name)}&to=${to.longitude},${to.latitude},${encodeURIComponent(to.name)}`;
  
  // 添加途经点
  if (spots.length > 2) {
    const via = spots.slice(1, -1).map(s => `${s.longitude},${s.latitude},${encodeURIComponent(s.name)}`).join(';');
    url += `&via=${via}`;
  }
  
  url += '&mode=car&callnative=1';
  return url;
}

// 初始化路线地图
function initRouteMap() {
  if (!result.value?.spots.length) return;
  
  const spots = result.value.spots.filter(s => s.longitude && s.latitude);
  if (spots.length === 0) return;
  
  // 延迟执行确保 DOM 已渲染
  setTimeout(() => {
    if (!window.AMap) {
      loadAMapScript().then(() => createRouteMap(spots));
    } else {
      createRouteMap(spots);
    }
  }, 200);
}

function loadAMapScript(): Promise<void> {
  return new Promise((resolve, reject) => {
    if (window.AMap) {
      resolve();
      return;
    }
    
    const script = document.createElement('script');
    script.src = `https://webapi.amap.com/maps?v=2.0&key=${AMAP_CONFIG.key}&plugin=AMap.Marker,AMap.InfoWindow,AMap.Polyline`;
    script.onload = () => resolve();
    script.onerror = () => reject(new Error('高德地图加载失败'));
    document.head.appendChild(script);
  });
}

function createRouteMap(spots: any[]) {
  const container = document.getElementById('route-map');
  if (!container || !window.AMap) return;
  
  // 计算地图中心点
  const lngs = spots.map(s => s.longitude);
  const lats = spots.map(s => s.latitude);
  const centerLng = (Math.min(...lngs) + Math.max(...lngs)) / 2;
  const centerLat = (Math.min(...lats) + Math.max(...lats)) / 2;
  
  // 创建地图
  routeMap = new window.AMap.Map('route-map', {
    zoom: 13,
    center: [centerLng, centerLat],
    viewMode: '2D'
  });
  
  // 添加标记点
  const markers: any[] = [];
  spots.forEach((spot, index) => {
    const marker = new window.AMap.Marker({
      position: [spot.longitude, spot.latitude],
      title: spot.name,
      label: {
        content: `<div style="background:#667eea;color:white;padding:2px 8px;border-radius:10px;font-size:12px;font-weight:bold;">${index + 1}</div>`,
        offset: new window.AMap.Pixel(-12, -35)
      }
    });
    
    // 信息窗口
    const infoWindow = new window.AMap.InfoWindow({
      content: `
        <div style="padding: 8px;">
          <h4 style="margin: 0 0 5px 0; font-size: 14px;">${index + 1}. ${spot.name}</h4>
          <p style="margin: 0; color: #666; font-size: 12px;">${spot.reason || ''}</p>
        </div>
      `,
      offset: new window.AMap.Pixel(0, -30)
    });
    
    marker.on('click', () => {
      infoWindow.open(routeMap, marker.getPosition());
    });
    
    markers.push(marker);
    routeMap.add(marker);
  });
  
  // 绘制路线连接线
  if (spots.length > 1) {
    const path = spots.map(s => [s.longitude, s.latitude]);
    const polyline = new window.AMap.Polyline({
      path: path,
      strokeColor: '#667eea',
      strokeWeight: 4,
      strokeOpacity: 0.8,
      strokeStyle: 'solid',
      lineJoin: 'round'
    });
    routeMap.add(polyline);
  }
  
  // 自动调整视野
  routeMap.setFitView(markers);
}

// 声明全局 AMap 类型
declare global {
  interface Window {
    AMap: any;
  }
}

// 获取交通方式图标
function getTransportIcon(method: string): string {
  const icons: Record<string, string> = {
    '步行': '🚶',
    '公交': '🚌',
    '地铁': '🚇',
    '打车': '🚕',
    '骑行': '🚲',
    '自驾': '🚗'
  };
  return icons[method] || '🚶';
}

// 获取交通方式颜色（与地图保持一致）
function getTransportColor(method: string): string {
  const colors: Record<string, string> = {
    '步行': '#10b981',    // 绿色
    '公交': '#3b82f6',    // 蓝色
    '地铁': '#8b5cf6',    // 紫色
    '打车': '#f59e0b',    // 橙色
    '骑行': '#06b6d4',    // 青色
    '自驾': '#ef4444'     // 红色
  };
  return colors[method] || '#667eea';
}

// 获取步骤图标
function getStepIcon(type: string): string {
  const icons: Record<string, string> = {
    '步行': '🚶',
    '公交': '🚌',
    '地铁': '🚇',
    '换乘': '🔄'
  };
  return icons[type] || '📍';
}

// 切换交通详情展开状态
function toggleTransportDetail(index: number) {
  expandedTransport.value[index] = !expandedTransport.value[index];
}

// 根据人均消费获取价格等级样式
function getCostLevel(cost: number): string {
  if (cost <= 100) {
    return 'cost-low';      // 低消费 - 绿色
  } else if (cost <= 300) {
    return 'cost-medium';   // 中等消费 - 黄色
  } else {
    return 'cost-high';     // 高消费 - 红色
  }
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
  
  // 保存表单信息和结果，以便后续重新加载
  const dataToSave = {
    formData: {
      regionId: form.regionId,
      age: form.age,
      playTimeHours: form.playTimeHours,
      peopleCount: form.peopleCount,
      budget: form.budget,
      preference: form.preference
    },
    result: result.value
  };
  const itineraryData = JSON.stringify(dataToSave);
  
  // 检查是否已收藏相同行程
  try {
    const existingRes = await getUserItineraries(userId);
    const existingItineraries = existingRes.data || [];
    
    // 比较景点ID列表是否相同
    const currentSpotIds = result.value.spots.map(s => s.id).sort().join(',');
    
    for (const existing of existingItineraries) {
      try {
        const existingData = JSON.parse(existing.itineraryData || '{}');
        const existingSpotIds = (existingData.spots || []).map((s: any) => s.id).sort().join(',');
        
        if (currentSpotIds === existingSpotIds) {
          ElMessage.warning('该行程已收藏过，无需重复收藏');
          return;
        }
      } catch (e) {
        // 解析失败，继续检查下一个
      }
    }
  } catch (e) {
    // 获取已有行程失败，继续保存
    console.log('检查重复失败，继续保存');
  }
  
  // 保存到数据库
  try {
    await saveItinerary(userId, name, itineraryData);
    ElMessage.success('行程收藏成功！可在"我的"页面查看');
  } catch (e) {
    console.error('保存失败', e);
    ElMessage.error('保存失败，请稍后重试');
  }
}

function getRegionName(regionId: number | null): string {
  if (!regionId) return '';
  const region = regions.value.find(r => r.id === regionId);
  return region ? region.name : '';
}

// 跳转到美食详情页
function goToFoodDetail(foodId: number) {
  router.push(`/foods/${foodId}`);
}
</script>

<style scoped>
.smart-recommend-page {
  min-height: calc(100vh - 80px);
  background: linear-gradient(180deg, #7c3aed 0%, #a855f7 50%, #ec4899 100%);
  border-radius: 40px;
  overflow: hidden;
  margin: 0 -20px;
  position: relative;
}

/* 装饰元素容器 */
.decorations {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  overflow: hidden;
}

/* 浮动 emoji 图标 */
.deco {
  position: absolute;
  font-size: 32px;
  opacity: 0.15;
  animation: float 6s ease-in-out infinite;
}

.deco-1 { top: 10%; left: 5%; animation-delay: 0s; font-size: 40px; }
.deco-2 { top: 25%; left: 8%; animation-delay: 1s; }
.deco-3 { top: 45%; left: 3%; animation-delay: 2s; font-size: 28px; }
.deco-4 { top: 65%; left: 6%; animation-delay: 0.5s; font-size: 36px; }
.deco-5 { top: 15%; right: 5%; animation-delay: 1.5s; font-size: 38px; }
.deco-6 { top: 35%; right: 7%; animation-delay: 2.5s; }
.deco-7 { top: 55%; right: 4%; animation-delay: 0.8s; font-size: 30px; }
.deco-8 { top: 75%; right: 6%; animation-delay: 1.8s; font-size: 34px; }

/* 装饰圆圈 */
.deco-circle {
  position: absolute;
  border-radius: 50%;
  border: 2px solid rgba(255, 255, 255, 0.1);
  animation: pulse 4s ease-in-out infinite;
}

.deco-circle-1 {
  width: 200px;
  height: 200px;
  top: 20%;
  left: -50px;
  animation-delay: 0s;
}

.deco-circle-2 {
  width: 150px;
  height: 150px;
  top: 60%;
  right: -30px;
  animation-delay: 1s;
}

.deco-circle-3 {
  width: 100px;
  height: 100px;
  bottom: 15%;
  left: 2%;
  animation-delay: 2s;
}

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(5deg); }
}

@keyframes pulse {
  0%, 100% { transform: scale(1); opacity: 0.1; }
  50% { transform: scale(1.1); opacity: 0.2; }
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
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 28px;
  padding: 36px;
  box-shadow: 0 8px 32px rgba(124, 58, 237, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.6);
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

/* AI 推荐样式 */
.trip-summary.ai-powered {
  background: linear-gradient(135deg, #f5f3ff 0%, #ede9fe 100%);
  border: 2px solid rgba(124, 58, 237, 0.3);
}

.trip-summary.ai-powered p {
  color: #1f2937;
  font-weight: 500;
}

.summary-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  background: linear-gradient(135deg, #7c3aed 0%, #a855f7 100%);
  color: #fff;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 12px;
}

.ai-icon {
  font-size: 16px;
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

/* 人均消费 - 低消费（绿色）≤100元 */
.stat-item.cost-low {
  background: linear-gradient(135deg, #10b981 0%, #34d399 100%);
}

.stat-item.cost-low .stat-value,
.stat-item.cost-low .stat-label {
  color: #fff;
}

/* 人均消费 - 中等消费（黄色）100-300元 */
.stat-item.cost-medium {
  background: linear-gradient(135deg, #f59e0b 0%, #fbbf24 100%);
}

.stat-item.cost-medium .stat-value,
.stat-item.cost-medium .stat-label {
  color: #fff;
}

/* 人均消费 - 高消费（红色）>300元 */
.stat-item.cost-high {
  background: linear-gradient(135deg, #ef4444 0%, #f87171 100%);
}

.stat-item.cost-high .stat-value,
.stat-item.cost-high .stat-label {
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
  position: relative;
  margin: 16px 0 16px 15px;
  padding-left: 24px;
  min-height: 50px;
  border-left: 2px solid #e5e7eb;
}

.travel-info {
  display: inline-block;
  font-size: 12px;
  color: #9ca3af;
  background: #f9fafb;
  padding: 6px 12px;
  border-radius: 8px;
}

/* AI 交通信息 */
.transport-info {
  position: absolute;
  left: 24px;
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  align-items: center;
  gap: 8px;
  background: linear-gradient(135deg, rgba(124, 58, 237, 0.1) 0%, rgba(168, 85, 247, 0.1) 100%);
  padding: 6px 12px;
  border-radius: 8px;
  border: 1px solid rgba(124, 58, 237, 0.2);
}

.transport-method {
  font-size: 12px;
  font-weight: 600;
  color: #7c3aed;
}

.transport-duration {
  font-size: 11px;
  color: #6b7280;
  background: #fff;
  padding: 2px 6px;
  border-radius: 4px;
}

.transport-distance {
  font-size: 11px;
  color: #9ca3af;
}

.transport-cost {
  font-size: 11px;
  color: #dc2626;
  font-weight: 500;
}

/* 交通详情（可展开） */
.transport-detail {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  min-width: 300px;
  max-width: 400px;
}

.transport-summary {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 14px;
  background: linear-gradient(135deg, rgba(124, 58, 237, 0.05) 0%, rgba(168, 85, 247, 0.05) 100%);
  border-radius: 12px;
}

.expand-icon {
  font-size: 10px;
  color: #9ca3af;
  margin-left: auto;
}

.transport-steps {
  padding: 12px 14px;
  border-top: 1px solid #f3f4f6;
}

.step-item {
  display: flex;
  gap: 10px;
  padding: 8px 0;
  border-bottom: 1px dashed #e5e7eb;
}

.step-item:last-child {
  border-bottom: none;
}

.step-icon {
  font-size: 18px;
  flex-shrink: 0;
}

.step-content {
  flex: 1;
  min-width: 0;
}

.step-main {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.step-line {
  font-size: 13px;
  font-weight: 600;
  color: #7c3aed;
  background: rgba(124, 58, 237, 0.1);
  padding: 2px 8px;
  border-radius: 4px;
}

.step-instruction {
  font-size: 13px;
  color: #374151;
}

.step-detail {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #6b7280;
}

.step-arrow {
  color: #9ca3af;
}

.step-stations {
  color: #9ca3af;
}

.step-duration {
  font-size: 11px;
  color: #9ca3af;
  margin-top: 4px;
}

.transport-tips {
  margin-top: 10px;
  padding: 8px 10px;
  background: #fffbeb;
  border-radius: 8px;
  font-size: 12px;
  color: #92400e;
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
  cursor: pointer;
}

.food-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.food-card:hover .view-detail {
  color: #667eea;
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

.food-action {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.view-detail {
  font-size: 13px;
  color: #999;
  transition: color 0.3s;
}

.food-img .img-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #ff6b6b 0%, #ffa500 100%);
  font-size: 48px;
}

/* 地图 */
.map-panel {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  padding: 24px;
  margin-top: 20px;
}

.map-panel .panel-title {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.ai-route-badge {
  font-size: 12px;
  font-weight: 600;
  color: #7c3aed;
  background: linear-gradient(135deg, rgba(124, 58, 237, 0.1) 0%, rgba(168, 85, 247, 0.1) 100%);
  padding: 4px 12px;
  border-radius: 12px;
  border: 1px solid rgba(124, 58, 237, 0.2);
}

.map-legend {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 16px;
  padding: 12px 16px;
  background: #f9fafb;
  border-radius: 10px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #4b5563;
}

.legend-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
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

/* 导航按钮样式 */
.nav-btn-small {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-decoration: none;
  border-radius: 15px;
  font-size: 12px;
  font-weight: 500;
  margin-top: 8px;
  transition: all 0.3s;
}

.nav-btn-small:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

/* 路线地图面板 */
.route-map-panel {
  background: white;
  border-radius: 20px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.route-map-panel .panel-title {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.nav-all-btn {
  margin-left: auto;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-decoration: none;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  transition: all 0.3s;
}

.nav-all-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.map-container {
  border-radius: 16px;
  overflow: hidden;
  margin-bottom: 16px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.route-map {
  width: 100%;
  height: 350px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8eb 100%);
}

.route-map:empty::before {
  content: '🗺️ 地图加载中...';
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #999;
  font-size: 16px;
}

.map-legend {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: #f8f9fa;
  border-radius: 10px;
  font-size: 13px;
}

.legend-number {
  width: 22px;
  height: 22px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
}

.legend-name {
  color: #333;
  font-weight: 500;
}

.legend-nav {
  color: #667eea;
  text-decoration: none;
  font-size: 12px;
  padding: 2px 8px;
  background: rgba(102, 126, 234, 0.1);
  border-radius: 10px;
  transition: all 0.2s;
}

.legend-nav:hover {
  background: rgba(102, 126, 234, 0.2);
}

@media (max-width: 768px) {
  .route-map {
    height: 280px;
  }
  
  .nav-all-btn {
    margin-left: 0;
    margin-top: 10px;
    width: 100%;
    justify-content: center;
  }
  
  .route-map-panel .panel-title {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
