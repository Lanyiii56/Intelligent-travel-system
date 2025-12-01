<template>
  <div class="travel-map-container">
    <div ref="mapContainer" class="map-container"></div>
    
    <!-- 路线信息面板 -->
    <div class="route-panel" v-if="routeInfo">
      <div class="panel-header">
        <el-icon><Guide /></el-icon>
        <span>路线概览</span>
      </div>
      <div class="panel-content">
        <div class="route-stat">
          <span class="stat-label">总距离</span>
          <span class="stat-value">{{ routeInfo.totalDistance }} km</span>
        </div>
        <div class="route-stat">
          <span class="stat-label">预计时间</span>
          <span class="stat-value">{{ formatTime(routeInfo.totalTime) }}</span>
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

const mapContainer = ref<HTMLElement>();
let map: any = null;
let AMap: any = null;
let markers: any[] = [];
let polyline: any = null;

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

  // 清除旧标记
  clearMarkers();

  const path: [number, number][] = [];

  props.spots.forEach((spot, index) => {
    if (!spot.longitude || !spot.latitude) return;

    const position: [number, number] = [spot.longitude, spot.latitude];
    path.push(position);

    // 创建标记
    const marker = new AMap.Marker({
      position,
      title: spot.name,
      label: {
        content: `<div class="marker-label">${index + 1}</div>`,
        direction: 'top'
      }
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
  if (path.length > 1) {
    polyline = new AMap.Polyline({
      path,
      strokeColor: '#667eea',
      strokeWeight: 4,
      strokeOpacity: 0.8,
      lineJoin: 'round',
      lineCap: 'round',
      showDir: true
    });
    polyline.setMap(map);
  }

  // 调整视野
  if (path.length > 0) {
    map.setFitView(markers);
  }
}

function clearMarkers() {
  markers.forEach(m => m.setMap(null));
  markers = [];
  if (polyline) {
    polyline.setMap(null);
    polyline = null;
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

.route-stat {
  display: flex;
  justify-content: space-between;
  padding: 6px 0;
}

.stat-label {
  color: #888;
  font-size: 13px;
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
