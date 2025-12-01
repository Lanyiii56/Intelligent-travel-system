<!--
  模块C: 智能推荐模块
  负责人: 成员3
  组件: 旅游地图组件
-->
<template>
  <div class="travel-map">
    <!-- 从原 TravelMap.vue 迁移内容 -->
    <!-- 成员3 在此开发 -->
    <div ref="mapContainer" class="map-container"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
// 成员3 的代码
const mapContainer = ref<HTMLElement>();
</script>

<style scoped>
.travel-map {
  width: 100%;
  height: 400px;
}
.map-container {
  width: 100%;
  height: 100%;
}
</style>
