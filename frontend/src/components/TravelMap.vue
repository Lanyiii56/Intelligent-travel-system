<template>
  <div class="travel-map-container">
    <div ref="mapContainer" class="map-container"></div>
    
    <!-- 路线信息面板 -->
    <div class="route-panel" v-if="actualRouteInfo">
      <div class="panel-header">
        <el-icon><Guide /></el-icon>
        <span>路线概览</span>
        <span class="route-type estimate">估算</span>
      </div>
      <div class="panel-content">
        <div class="route-stat">
          <span class="stat-label">总距离</span>
          <span class="stat-value highlight">
            {{ actualRouteInfo.distance }} km
          </span>
        </div>
        <div class="route-divider"></div>
        <div class="transport-title">预计出行时间</div>
        <div class="route-stat">
          <span class="stat-label">🚗 驾车</span>
          <span class="stat-value">{{ formatTimeRange(actualRouteInfo.drivingTime) }}</span>
        </div>
        <div class="route-stat">
          <span class="stat-label">🚌 公交</span>
          <span class="stat-value">{{ formatTimeRange(actualRouteInfo.transitTime) }}</span>
        </div>
        <div class="route-stat">
          <span class="stat-label">🚴 骑行</span>
          <span class="stat-value">{{ formatTime(actualRouteInfo.cyclingTime) }}</span>
        </div>
        <div class="route-stat">
          <span class="stat-label">🚶 步行</span>
          <span class="stat-value">{{ formatTime(actualRouteInfo.walkingTime) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, onUnmounted } from 'vue';
import AMapLoader from '@amap/amap-jsapi-loader';
import { Guide } from '@element-plus/icons-vue';
import type { RouteInfo, SpotRecommend } from '@/api/recommend';
import { AMAP_CONFIG } from '@/config/amap';

const props = defineProps<{
  spots: SpotRecommend[];
  routeInfo?: RouteInfo;
}>();

const emit = defineEmits<{
  (e: 'route-calculated', data: { distance: number; time: number }): void;
}>();

// 时间范围类型（考虑拥堵等因素）
interface TimeRange {
  min: number;  // 最短时间（畅通）
  max: number;  // 最长时间（拥堵）
}

// 路线信息类型
interface RouteEstimate {
  distance: number;       // 总距离（公里）
  drivingTime: TimeRange; // 驾车时间范围（分钟）
  transitTime: TimeRange; // 公交时间范围（分钟）
  cyclingTime: number;    // 骑行时间（分钟，受拥堵影响小）
  walkingTime: number;    // 步行时间（分钟，不受拥堵影响）
}

// 实际路线信息
const actualRouteInfo = ref<RouteEstimate | null>(null);

const mapContainer = ref<HTMLElement>();
let map: any = null;
let AMap: any = null;
let markers: any[] = [];
let routePolylines: any[] = [];

onMounted(async () => {
  try {
    AMap = await AMapLoader.load({
      key: AMAP_CONFIG.key,
      version: AMAP_CONFIG.version,
      plugins: AMAP_CONFIG.plugins
    });

    initMap();
  } catch (e) {
    console.error('地图加载失败:', e);
  }
});

function initMap() {
  if (!mapContainer.value || !AMap) return;

  map = new AMap.Map(mapContainer.value, {
    zoom: 12,
    center: [116.397428, 39.90923], // 默认北京
    mapStyle: 'amap://styles/fresh'
  });

  if (props.spots.length > 0) {
    renderSpots();
  }
}

function renderSpots() {
  if (!map || !AMap) return;

  // 清除旧标记和路线
  clearMarkers();

  const positions: [number, number][] = [];

  props.spots.forEach((spot, index) => {
    if (!spot.longitude || !spot.latitude) return;

    const position: [number, number] = [spot.longitude, spot.latitude];
    positions.push(position);

    // 创建自定义标记
    const markerContent = document.createElement('div');
    markerContent.className = 'custom-marker';
    markerContent.innerHTML = `<div class="marker-label">${index + 1}</div>`;

    const marker = new AMap.Marker({
      position,
      title: spot.name,
      content: markerContent,
      offset: new AMap.Pixel(-14, -14)
    });

    // 信息窗口
    const infoWindow = new AMap.InfoWindow({
      content: `
        <div class="info-window">
          <h4>${spot.name}</h4>
          <p>第${spot.order}站 · 游玩${spot.playTime}分钟</p>
          <p class="reason">${spot.reason}</p>
        </div>
      `,
      offset: new AMap.Pixel(0, -30)
    });

    marker.on('click', () => {
      infoWindow.open(map, position);
    });

    marker.setMap(map);
    markers.push(marker);
  });

  // 绘制路线
  if (positions.length > 1) {
    // 尝试使用驾车路线规划，如果失败则使用直线
    planDrivingRoute(positions);
  }

  // 调整视野
  if (positions.length > 0) {
    map.setFitView(markers, false, [50, 50, 50, 50]);
  }
}

// 驾车路线规划 - 分段规划每两个相邻景点之间的路线
async function planDrivingRoute(positions: [number, number][]) {
  if (!AMap || positions.length < 2) return;

  let totalDistance = 0;
  let totalTime = 0;
  let allSuccess = true;

  // 分段规划：每两个相邻景点之间规划一条路线
  for (let i = 0; i < positions.length - 1; i++) {
    const start = positions[i];
    const end = positions[i + 1];
    
    try {
      const result = await planSingleRoute(start, end, i);
      if (result) {
        totalDistance += result.distance;
        totalTime += result.time;
      } else {
        allSuccess = false;
      }
    } catch (e) {
      console.error(`第${i + 1}段路线规划失败:`, e);
      allSuccess = false;
    }
  }

  if (allSuccess && totalDistance > 0) {
    const distanceKm = parseFloat((totalDistance / 1000).toFixed(1));
    // 高德返回的时间单位是秒，需要转换为分钟
    const drivingMin = Math.round(totalTime / 60);
    
    actualRouteInfo.value = calculateAllTransportTimes(distanceKm, drivingMin);
    
    emit('route-calculated', {
      distance: distanceKm,
      time: drivingMin
    });
    
    console.log(`总驾车距离: ${distanceKm} km, 驾车时间: ${drivingMin} 分钟`);
  } else {
    // 如果路线规划失败，显示直线并估算距离
    console.log('路线规划失败，使用直线连接');
    drawFallbackPolyline(positions);
    
    // 计算直线距离并估算各交通方式时间
    const straightDistance = calculateStraightDistance(positions);
    actualRouteInfo.value = calculateAllTransportTimes(straightDistance);
  }
}

// 计算各种交通方式的时间（含拥堵误差）
function calculateAllTransportTimes(distanceKm: number, actualDrivingTime?: number): RouteEstimate {
  // 直线距离转实际道路距离的系数（道路通常比直线长30%-50%）
  const roadFactor = 1.4;
  const roadDistance = distanceKm * roadFactor;
  
  // 根据距离判断是城市内还是城际出行
  const isLongDistance = distanceKm > 20; // 超过20km视为长途
  
  // 驾车时间计算
  const drivingSpeed = isLongDistance ? 60 : 35; // 长途60km/h，城市35km/h
  const baseDrivingTime = actualDrivingTime || Math.round(roadDistance / drivingSpeed * 60);
  
  // 拥堵系数：城市内拥堵影响更大
  const trafficFactor = {
    min: 1.0,                           // 畅通
    max: isLongDistance ? 1.3 : 1.5     // 拥堵：长途+30%，城市+50%
  };
  
  const drivingTime: TimeRange = {
    min: Math.round(baseDrivingTime * trafficFactor.min),
    max: Math.round(baseDrivingTime * trafficFactor.max)
  };
  
  // 公交时间计算：基于驾车时间的倍数
  // 城市公交约为驾车的1.5-2倍，长途大巴约为驾车的1.2-1.5倍
  const transitMultiplier = isLongDistance 
    ? { min: 1.2, max: 1.5 }   // 长途大巴
    : { min: 1.5, max: 2.2 };  // 城市公交（含等车、换乘）
  
  const transitTime: TimeRange = {
    min: Math.round(baseDrivingTime * transitMultiplier.min),
    max: Math.round(baseDrivingTime * transitMultiplier.max)
  };
  
  // 骑行时间：平均速度12-18km/h，取中间值15km/h
  const cyclingTime = Math.round(roadDistance / 15 * 60);
  
  // 步行时间：平均速度5km/h
  const walkingTime = Math.round(roadDistance / 5 * 60);
  
  return {
    distance: distanceKm,
    drivingTime,
    transitTime,
    cyclingTime,
    walkingTime
  };
}

// 计算直线距离（公里）
function calculateStraightDistance(positions: [number, number][]): number {
  let total = 0;
  for (let i = 0; i < positions.length - 1; i++) {
    total += getDistance(positions[i], positions[i + 1]);
  }
  return parseFloat(total.toFixed(1));
}

// 计算两点间距离（Haversine公式）
function getDistance(p1: [number, number], p2: [number, number]): number {
  const R = 6371; // 地球半径（公里）
  const dLat = (p2[1] - p1[1]) * Math.PI / 180;
  const dLon = (p2[0] - p1[0]) * Math.PI / 180;
  const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.cos(p1[1] * Math.PI / 180) * Math.cos(p2[1] * Math.PI / 180) *
    Math.sin(dLon / 2) * Math.sin(dLon / 2);
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  return R * c;
}

// 规划单段路线
function planSingleRoute(start: [number, number], end: [number, number], index: number): Promise<{ distance: number; time: number } | null> {
  return new Promise((resolve) => {
    const drivingInstance = new AMap.Driving({
      policy: AMap.DrivingPolicy.LEAST_TIME
    });

    drivingInstance.search(
      new AMap.LngLat(start[0], start[1]),
      new AMap.LngLat(end[0], end[1]),
      (status: string, result: any) => {
        if (status === 'complete' && result.routes && result.routes[0]) {
          const route = result.routes[0];
          
          // 绘制路线
          const path = parseRouteToPath(route);
          if (path.length > 0) {
            const polyline = new AMap.Polyline({
              path: path,
              strokeColor: '#667eea',
              strokeWeight: 6,
              strokeOpacity: 0.9,
              lineJoin: 'round',
              lineCap: 'round',
              showDir: true,
              dirColor: '#ffffff'
            });
            polyline.setMap(map);
            routePolylines.push(polyline);
          }
          
          resolve({
            distance: route.distance,
            time: route.time
          });
        } else {
          console.error(`段落${index + 1}路线规划失败:`, status, result);
          resolve(null);
        }
      }
    );
  });
}

// 解析路线结果为路径点数组
function parseRouteToPath(route: any): [number, number][] {
  const path: [number, number][] = [];
  
  if (route.steps) {
    route.steps.forEach((step: any) => {
      if (step.path) {
        step.path.forEach((point: any) => {
          path.push([point.lng, point.lat]);
        });
      }
    });
  }
  
  return path;
}

// 备用：直线连接（当路线规划失败时使用）
function drawFallbackPolyline(positions: [number, number][]) {
  const polyline = new AMap.Polyline({
    path: positions,
    strokeColor: '#667eea',
    strokeWeight: 4,
    strokeOpacity: 0.8,
    lineJoin: 'round',
    lineCap: 'round',
    showDir: true,
    strokeStyle: 'dashed' // 虚线表示非真实路线
  });
  polyline.setMap(map);
  routePolylines.push(polyline);
}

function clearMarkers() {
  // 清除标记
  markers.forEach(m => m.setMap(null));
  markers = [];
  
  // 清除路线折线
  routePolylines.forEach(p => p.setMap(null));
  routePolylines = [];
  
  // 重置路线信息
  actualRouteInfo.value = null;
}

function formatTime(minutes: number): string {
  const hours = Math.floor(minutes / 60);
  const mins = minutes % 60;
  if (hours > 0) {
    return mins > 0 ? `${hours}小时${mins}分` : `${hours}小时`;
  }
  return `${mins}分钟`;
}

// 格式化时间范围（考虑拥堵）
function formatTimeRange(range: TimeRange): string {
  if (range.min === range.max) {
    return formatTime(range.min);
  }
  
  const minHours = Math.floor(range.min / 60);
  const maxHours = Math.floor(range.max / 60);
  
  // 如果都在1小时内，显示分钟范围
  if (minHours === 0 && maxHours === 0) {
    return `${range.min}-${range.max}分钟`;
  }
  
  // 如果跨小时，分别格式化
  return `${formatTime(range.min)} - ${formatTime(range.max)}`;
}

watch(() => props.spots, () => {
  if (map) {
    renderSpots();
  }
}, { deep: true });

onUnmounted(() => {
  if (map) {
    map.destroy();
  }
});
</script>

<style scoped>
.travel-map-container {
  position: relative;
  width: 100%;
  height: 400px;
  border-radius: 12px;
  overflow: hidden;
}

.map-container {
  width: 100%;
  height: 100%;
}

.route-panel {
  position: absolute;
  top: 16px;
  right: 16px;
  background: white;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  min-width: 160px;
}

.panel-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #eee;
}

.route-type {
  margin-left: auto;
  font-size: 11px;
  font-weight: 500;
  color: #667eea;
  background: rgba(102, 126, 234, 0.1);
  padding: 2px 8px;
  border-radius: 10px;
}

.route-type.estimate {
  color: #f59e0b;
  background: rgba(245, 158, 11, 0.1);
}

.route-divider {
  height: 1px;
  background: #eee;
  margin: 8px 0;
}

.transport-title {
  font-size: 12px;
  color: #999;
  margin-bottom: 6px;
}

.route-stat {
  display: flex;
  justify-content: space-between;
  padding: 4px 0;
}

.stat-label {
  color: #666;
  font-size: 13px;
}

.stat-value.highlight {
  font-size: 16px;
}

.stat-value {
  font-weight: 600;
  color: #667eea;
}
</style>

<style>
/* 全局样式 - 地图标记 */
.marker-label {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 14px;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.4);
}

.info-window {
  padding: 8px;
  max-width: 200px;
}

.info-window h4 {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 15px;
}

.info-window p {
  margin: 4px 0;
  color: #666;
  font-size: 13px;
}

.info-window .reason {
  color: #667eea;
  font-size: 12px;
}
</style>
