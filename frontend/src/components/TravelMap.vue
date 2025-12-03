<template>
  <div class="travel-map-container">
    <div ref="mapContainer" class="map-container"></div>
    
    <!-- 路线信息面板 -->
    <div class="route-panel" v-if="actualRouteInfo">
      <div class="panel-header">
        <el-icon><Guide /></el-icon>
        <span>路线概览</span>
        <span class="route-type estimate">距离估算</span>
      </div>
      <div class="panel-content">
        <div class="route-stat">
          <span class="stat-label">直线距离</span>
          <span class="stat-value highlight">
            {{ actualRouteInfo.distance }} km
          </span>
        </div>
        <div class="route-stat">
          <span class="stat-label">预估路程</span>
          <span class="stat-value">
            约 {{ (actualRouteInfo.distance * 1.4).toFixed(1) }} km
          </span>
        </div>
        <div class="route-divider"></div>
        <div class="transport-title">预计出行时间（参考）</div>
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
        <div class="route-note">
          * 时间为估算值，实际出行请参考导航软件
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, onUnmounted } from 'vue';
import AMapLoader from '@amap/amap-jsapi-loader';
import { Guide } from '@element-plus/icons-vue';
import type { RouteInfo, SpotRecommend, TransportInfo } from '@/api/recommend';
import { AMAP_CONFIG } from '@/config/amap';

const props = defineProps<{
  spots: SpotRecommend[];
  routeInfo?: RouteInfo;
  transportation?: TransportInfo[];  // AI 推荐的交通信息
  aiPowered?: boolean;               // 是否为 AI 推荐
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

  // 绘制路线 - 直接使用直线连接（高德免费版不支持路线规划API）
  if (positions.length > 1) {
    drawFallbackPolyline(positions);
    
    // 计算直线距离并估算各交通方式时间
    const straightDistance = calculateStraightDistance(positions);
    actualRouteInfo.value = calculateAllTransportTimes(straightDistance);
    
    emit('route-calculated', {
      distance: straightDistance,
      time: actualRouteInfo.value.drivingTime.min
    });
  }

  // 调整视野
  if (positions.length > 0) {
    map.setFitView(markers, false, [50, 50, 50, 50]);
  }
}

// 注意：高德路线规划API需要付费，已禁用
// 现在直接使用直线连接和距离估算

// 计算各种交通方式的时间（含拥堵误差，保守估算）
function calculateAllTransportTimes(distanceKm: number, actualDrivingTime?: number): RouteEstimate {
  // 直线距离转实际道路距离的系数（道路通常比直线长40%-80%，取较大值更保守）
  const roadFactor = 1.6;
  const roadDistance = distanceKm * roadFactor;
  
  // 根据距离判断是城市内还是城际出行
  const isLongDistance = distanceKm > 15; // 超过15km视为长途
  
  // 驾车时间计算（考虑红绿灯、找停车位等）
  // 城市平均速度更低：25-30km/h，长途：50km/h
  const drivingSpeed = isLongDistance ? 50 : 25;
  const baseDrivingTime = actualDrivingTime || Math.round(roadDistance / drivingSpeed * 60);
  
  // 拥堵系数：城市内拥堵影响更大，增加误差范围
  const trafficFactor = {
    min: 1.2,                           // 畅通也要预留缓冲
    max: isLongDistance ? 1.8 : 2.2     // 拥堵：长途+80%，城市+120%
  };
  
  const drivingTime: TimeRange = {
    min: Math.round(baseDrivingTime * trafficFactor.min),
    max: Math.round(baseDrivingTime * trafficFactor.max)
  };
  
  // 公交时间计算：基于驾车时间的倍数（含等车、换乘、绕路）
  // 城市公交约为驾车的2-3倍，长途大巴约为驾车的1.5-2倍
  const transitMultiplier = isLongDistance 
    ? { min: 1.5, max: 2.5 }   // 长途大巴（含候车）
    : { min: 2.0, max: 3.5 };  // 城市公交（含等车、换乘、步行到站）
  
  const transitTime: TimeRange = {
    min: Math.round(baseDrivingTime * transitMultiplier.min),
    max: Math.round(baseDrivingTime * transitMultiplier.max)
  };
  
  // 骑行时间：平均速度8-10km/h（考虑等红灯、上坡、观光等）
  const cyclingTime = Math.round(roadDistance / 8 * 60);
  
  // 步行时间：旅游步行速度约2.5-3km/h（边走边看、拍照、休息）
  const walkingTime = Math.round(roadDistance / 2.5 * 60);
  
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

// 获取交通方式对应的颜色
function getTransportColor(method?: string): string {
  const colors: Record<string, string> = {
    '步行': '#10b981',    // 绿色
    '公交': '#3b82f6',    // 蓝色
    '地铁': '#8b5cf6',    // 紫色
    '打车': '#f59e0b',    // 橙色
    '骑行': '#06b6d4',    // 青色
    '自驾': '#ef4444'     // 红色
  };
  return colors[method || ''] || '#667eea';  // 默认紫蓝色
}

// ============ 以下为高德路线规划API相关代码（需付费，已禁用）============
// 如果将来开通付费服务，可以取消注释使用

/*
// 规划单段路线（需要高德付费API）
function planSingleRoute(start: [number, number], end: [number, number], index: number): Promise<{ distance: number; time: number } | null> {
  return new Promise((resolve) => {
    const transportMethod = props.transportation?.[index]?.method;
    const routeColor = getTransportColor(transportMethod);
    
    const drivingInstance = new AMap.Driving({
      policy: AMap.DrivingPolicy.LEAST_TIME
    });

    drivingInstance.search(
      new AMap.LngLat(start[0], start[1]),
      new AMap.LngLat(end[0], end[1]),
      (status: string, result: any) => {
        if (status === 'complete' && result.routes && result.routes[0]) {
          const route = result.routes[0];
          const path = parseRouteToPath(route);
          if (path.length > 0) {
            const polyline = new AMap.Polyline({
              path: path,
              strokeColor: routeColor,
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
          resolve({ distance: route.distance, time: route.time });
        } else {
          resolve(null);
        }
      }
    );
  });
}

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
*/
// ============ 高德付费API代码结束 ============

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

.route-note {
  margin-top: 10px;
  padding-top: 8px;
  border-top: 1px dashed #eee;
  font-size: 11px;
  color: #999;
  line-height: 1.4;
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

/* AI 交通方式标签 */
.transport-label {
  display: flex;
  align-items: center;
  gap: 4px;
  background: white;
  padding: 4px 10px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  font-size: 12px;
  white-space: nowrap;
}

.transport-label .transport-icon {
  font-size: 14px;
}

.transport-label .transport-text {
  color: #666;
  font-weight: 500;
}
</style>
