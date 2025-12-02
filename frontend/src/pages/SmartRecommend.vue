<template>
  <div class="smart-recommend-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1 class="page-title">🗺️ 智能旅游规划</h1>
      <p class="page-subtitle">根据您的年龄、时间、人数，智能推荐最佳行程</p>
    </div>

    <!-- 推荐表单 -->
    <el-card class="form-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <el-icon><Location /></el-icon>
          <span>填写您的出行信息</span>
        </div>
      </template>

      <el-form :model="form" label-width="100px" class="recommend-form">
        <el-row :gutter="24">
          <el-col :xs="24" :sm="12" :md="6">
            <el-form-item label="选择地区">
              <el-select v-model="form.regionId" placeholder="请选择地区" style="width: 100%">
                <el-option
                  v-for="region in regions"
                  :key="region.id"
                  :label="region.name"
                  :value="region.id"
                />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="12" :md="6">
            <el-form-item label="您的年龄">
              <el-input-number
                v-model="form.age"
                :min="1"
                :max="120"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="12" :md="6">
            <el-form-item label="游玩时间">
              <el-select v-model="form.playTime" placeholder="选择时间" style="width: 100%">
                <el-option label="半天 (4小时)" :value="240" />
                <el-option label="一天 (8小时)" :value="480" />
                <el-option label="两天" :value="960" />
                <el-option label="三天" :value="1440" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="12" :md="6">
            <el-form-item label="出行人数">
              <el-input-number
                v-model="form.peopleCount"
                :min="1"
                :max="50"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :xs="24" :sm="12">
            <el-form-item label="预算(可选)">
              <el-input-number
                v-model="form.budget"
                :min="0"
                :step="100"
                placeholder="不限"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="12">
            <el-form-item label="偏好类型">
              <el-select v-model="form.preference" placeholder="不限" clearable style="width: 100%">
                <el-option label="观光游览" value="观光" />
                <el-option label="游乐体验" value="游乐" />
                <el-option label="文化历史" value="文化" />
                <el-option label="美食探店" value="美食" />
                <el-option label="购物休闲" value="购物" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <div class="form-actions">
          <el-button type="primary" size="large" @click="onRecommend" :loading="loading">
            <el-icon><MagicStick /></el-icon>
            开始智能规划
          </el-button>
        </div>
      </el-form>
    </el-card>

    <!-- 推荐结果 -->
    <div v-if="result" class="result-section">
      <!-- 推荐总结 -->
      <div class="summary-row">
        <el-alert
          :title="result.summary"
          type="success"
          :closable="false"
          show-icon
          class="summary-alert"
        />
        <el-button type="warning" @click="onSaveItinerary" class="save-btn">
          <el-icon><Star /></el-icon>
          保存行程
        </el-button>
      </div>

      <el-row :gutter="24">
        <!-- 左侧：推荐景点列表 -->
        <el-col :xs="24" :lg="14">
          <el-card class="spots-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <el-icon><Place /></el-icon>
                <span>推荐景点 ({{ result.spots.length }}个)</span>
              </div>
            </template>

            <el-timeline>
              <el-timeline-item
                v-for="spot in result.spots"
                :key="spot.id"
                :timestamp="`第${spot.order}站 · 建议游玩${spot.playTime}分钟`"
                placement="top"
                type="primary"
                :hollow="true"
              >
                <el-card class="spot-item" shadow="hover" @click="goToSpot(spot.id)">
                  <div class="spot-content">
                    <div class="spot-image">
                      <img v-if="spot.imageUrl" :src="spot.imageUrl" :alt="spot.name" />
                      <div v-else class="spot-placeholder">🏞️</div>
                    </div>
                    <div class="spot-info">
                      <h3 class="spot-name">{{ spot.name }}</h3>
                      <p class="spot-reason">
                        <el-tag type="success" size="small">推荐理由</el-tag>
                        {{ spot.reason }}
                      </p>
                      <div class="spot-meta">
                        <span class="meta-item">
                          <el-icon><Timer /></el-icon>
                          {{ spot.playTime }}分钟
                        </span>
                        <span class="meta-item" v-if="spot.priceMax">
                          <el-icon><Wallet /></el-icon>
                          ¥{{ spot.priceMin }}-{{ spot.priceMax }}
                        </span>
                      </div>
                    </div>
                  </div>
                </el-card>
              </el-timeline-item>
            </el-timeline>
          </el-card>
        </el-col>

        <!-- 右侧：路线和费用 -->
        <el-col :xs="24" :lg="10">
          <!-- 路线信息 -->
          <el-card class="route-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <el-icon><Guide /></el-icon>
                <span>路线规划</span>
              </div>
            </template>

            <div class="route-stats">
              <div class="stat-item">
                <div class="stat-value">{{ formatTime(result.route.totalTime) }}</div>
                <div class="stat-label">预计总时长</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ result.route.totalDistance }}km</div>
                <div class="stat-label">总路程</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ result.spots.length }}</div>
                <div class="stat-label">景点数量</div>
              </div>
            </div>

            <el-divider />

            <div class="route-suggestion">
              <el-icon><InfoFilled /></el-icon>
              <span>{{ result.route.suggestion }}</span>
            </div>

            <!-- 路线步骤 -->
            <el-steps direction="vertical" :active="result.route.points.length" class="route-steps">
              <el-step
                v-for="(point, index) in result.route.points"
                :key="point.spotId"
                :title="point.spotName"
                :description="`停留${point.stayTime}分钟${point.travelTime > 0 ? ' → 车程' + point.travelTime + '分钟' : ''}`"
              />
            </el-steps>
          </el-card>

          <!-- 费用预估 -->
          <el-card class="cost-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <el-icon><Money /></el-icon>
                <span>费用预估</span>
              </div>
            </template>

            <div class="cost-breakdown">
              <div class="cost-item">
                <span class="cost-label">门票费用</span>
                <span class="cost-value">¥{{ result.cost.ticketCost }}</span>
              </div>
              <div class="cost-item">
                <span class="cost-label">交通费用</span>
                <span class="cost-value">¥{{ result.cost.transportCost }}</span>
              </div>
              <div class="cost-item">
                <span class="cost-label">餐饮费用</span>
                <span class="cost-value">¥{{ result.cost.mealCost }}</span>
              </div>
              <el-divider />
              <div class="cost-item total">
                <span class="cost-label">总费用</span>
                <span class="cost-value">¥{{ result.cost.totalCost }}</span>
              </div>
              <div class="cost-item per-person">
                <span class="cost-label">人均费用</span>
                <span class="cost-value highlight">¥{{ result.cost.perPersonCost }}</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 地图展示 -->
      <el-card class="map-card" shadow="hover" v-if="result.spots.length > 0">
        <template #header>
          <div class="card-header">
            <el-icon><MapLocation /></el-icon>
            <span>路线地图</span>
          </div>
        </template>
        <TravelMap :spots="result.spots" :route-info="result.route" />
      </el-card>
    </div>

    <!-- 空状态 -->
    <el-empty v-else-if="hasSearched && !loading" description="暂无匹配的推荐结果，请调整筛选条件" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import {
  Location, MagicStick, Place, Timer, Wallet, Guide, Money, InfoFilled, MapLocation, Star
} from '@element-plus/icons-vue';
import { smartRecommend, saveItinerary, type SmartRecommendResponse } from '@/api/recommend';
import { getAllRegions, type Region } from '@/api/region';
import TravelMap from '@/components/TravelMap.vue';

const router = useRouter();

const form = reactive({
  regionId: null as number | null,
  age: 25,
  playTime: 480,
  peopleCount: 2,
  budget: undefined as number | undefined,
  preference: ''
});

const regions = ref<Region[]>([]);
const result = ref<SmartRecommendResponse | null>(null);
const loading = ref(false);
const hasSearched = ref(false);

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
    const res = await smartRecommend({
      regionId: form.regionId,
      age: form.age,
      playTime: form.playTime,
      peopleCount: form.peopleCount,
      budget: form.budget,
      preference: form.preference
    });
    result.value = res.data;

    if (result.value?.spots.length === 0) {
      ElMessage.info('未找到符合条件的景点，请调整筛选条件');
    }
  } catch (e) {
    console.error(e);
    ElMessage.error('获取推荐失败，请稍后重试');
    result.value = null;
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
  if (!result.value || result.value.spots.length === 0) {
    ElMessage.warning('没有可保存的行程');
    return;
  }

  // 简单实现：使用固定userId=1，实际应从登录状态获取
  const userId = 1;
  const name = `${getRegionName(form.regionId)}${form.playTime >= 480 ? '一日游' : '半日游'}`;
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
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.page-title {
  font-size: 36px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 10px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.page-subtitle {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.9);
}

.form-card {
  margin-bottom: 30px;
  border-radius: 16px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
}

.form-actions {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.result-section {
  margin-top: 30px;
}

.summary-row {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.summary-alert {
  flex: 1;
  border-radius: 12px;
}

.save-btn {
  flex-shrink: 0;
}

.spots-card,
.route-card,
.cost-card,
.map-card {
  border-radius: 16px;
  margin-bottom: 20px;
}

.map-card {
  margin-top: 24px;
}

.spot-item {
  cursor: pointer;
  transition: all 0.3s;
  border-radius: 12px;
}

.spot-item:hover {
  transform: translateX(8px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.spot-content {
  display: flex;
  gap: 16px;
}

.spot-image {
  width: 120px;
  height: 90px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
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
  font-size: 36px;
}

.spot-info {
  flex: 1;
}

.spot-name {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 8px;
}

.spot-reason {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.spot-meta {
  display: flex;
  gap: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #888;
}

.route-stats {
  display: flex;
  justify-content: space-around;
  text-align: center;
}

.stat-item {
  padding: 10px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #667eea;
}

.stat-label {
  font-size: 13px;
  color: #888;
  margin-top: 4px;
}

.route-suggestion {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
  font-size: 14px;
  color: #666;
  margin-bottom: 20px;
}

.route-steps {
  padding: 0 10px;
}

.cost-breakdown {
  padding: 10px 0;
}

.cost-item {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
}

.cost-label {
  color: #666;
}

.cost-value {
  font-weight: 600;
  color: #333;
}

.cost-item.total {
  font-size: 16px;
}

.cost-item.total .cost-value {
  color: #667eea;
  font-size: 20px;
}

.cost-item.per-person .cost-value.highlight {
  color: #e74c3c;
  font-size: 24px;
}

@media (max-width: 768px) {
  .page-title {
    font-size: 28px;
  }

  .spot-content {
    flex-direction: column;
  }

  .spot-image {
    width: 100%;
    height: 150px;
  }
}
</style>
