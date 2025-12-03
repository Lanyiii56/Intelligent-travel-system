<template>
  <div class="hotel-detail-page">
    <!-- 返回按钮 -->
    <button class="back-btn" @click="router.back()">← 返回</button>

    <!-- 加载状态 -->
    <div class="loading-state" v-if="loading">
      <div class="loading-spinner"></div>
      <span>加载中...</span>
    </div>

    <template v-else-if="hotel">
      <!-- 酒店头图 -->
      <div class="hotel-header">
        <img :src="hotel.imageUrl || defaultImage" :alt="hotel.name" class="header-image" />
        <div class="header-overlay">
          <div class="hotel-stars">
            <span v-for="i in hotel.stars" :key="i">⭐</span>
          </div>
          <h1 class="hotel-name">{{ hotel.name }}</h1>
          <p class="hotel-address">📍 {{ hotel.address }}</p>
        </div>
      </div>

      <!-- 酒店信息 -->
      <div class="hotel-info-card">
        <div class="info-row">
          <div class="info-item">
            <span class="info-label">评分</span>
            <span class="info-value rating">{{ hotel.rating?.toFixed(1) || '暂无' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">入住时间</span>
            <span class="info-value">{{ hotel.checkInTime || '14:00' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">退房时间</span>
            <span class="info-value">{{ hotel.checkOutTime || '12:00' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">联系电话</span>
            <span class="info-value">{{ hotel.phone || '暂无' }}</span>
          </div>
        </div>
      </div>

      <!-- 酒店设施 -->
      <div class="section-card" v-if="facilities.length > 0">
        <h2 class="section-title">🏠 酒店设施</h2>
        <div class="facilities-grid">
          <div class="facility-item" v-for="(facility, index) in facilities" :key="index">
            <span class="facility-icon">{{ getFacilityIcon(facility) }}</span>
            <span class="facility-name">{{ facility }}</span>
          </div>
        </div>
      </div>

      <!-- 酒店介绍 -->
      <div class="section-card" v-if="hotel.description">
        <h2 class="section-title">📝 酒店介绍</h2>
        <p class="hotel-description">{{ hotel.description }}</p>
      </div>

      <!-- 酒店位置地图 -->
      <div class="section-card">
        <h2 class="section-title">📍 酒店位置</h2>
        <div class="location-info">
          <p class="address">{{ hotel.address }}</p>
          <a 
            :href="`https://uri.amap.com/marker?position=${hotel.longitude},${hotel.latitude}&name=${encodeURIComponent(hotel.name)}`"
            target="_blank"
            class="nav-btn"
          >
            🧭 导航到这里
          </a>
        </div>
        <div class="map-container" ref="mapContainer">
          <div id="hotel-map" class="hotel-map"></div>
        </div>
      </div>

      <!-- 房型列表 -->
      <div class="section-card">
        <h2 class="section-title">🛏️ 房型选择</h2>
        
        <!-- 日期选择 -->
        <div class="date-picker">
          <div class="date-item">
            <label>入住日期</label>
            <input type="date" v-model="checkInDate" :min="today" />
          </div>
          <div class="date-item">
            <label>退房日期</label>
            <input type="date" v-model="checkOutDate" :min="minCheckOutDate" />
          </div>
          <div class="nights-info">
            共 <strong>{{ nights }}</strong> 晚
          </div>
        </div>

        <div class="rooms-list" v-if="rooms.length > 0">
          <div class="room-card" v-for="room in rooms" :key="room.id">
            <div class="room-image">
              <img :src="room.imageUrl || defaultRoomImage" :alt="room.name" />
            </div>
            <div class="room-info">
              <h3 class="room-name">{{ room.name }}</h3>
              <div class="room-details">
                <span class="detail-tag">{{ getBedTypeName(room.bedType) }}</span>
                <span class="detail-tag">{{ room.area }}㎡</span>
                <span class="detail-tag">可住{{ room.maxGuests }}人</span>
                <span class="detail-tag" v-if="room.hasWindow">有窗</span>
                <span class="detail-tag breakfast" v-if="room.hasBreakfast">含早餐</span>
              </div>
              <p class="room-description" v-if="room.description">{{ room.description }}</p>
              <div class="room-stock" :class="{ low: room.stock <= 3 }">
                {{ room.stock > 0 ? `剩余 ${room.stock} 间` : '已售罄' }}
              </div>
            </div>
            <div class="room-action">
              <div class="room-price">
                <span class="price-label">¥</span>
                <span class="price-value">{{ room.price }}</span>
                <span class="price-unit">/晚</span>
              </div>
              <div class="total-price" v-if="nights > 1">
                {{ nights }}晚共 ¥{{ (room.price * nights).toFixed(0) }}
              </div>
              <button 
                class="btn-book" 
                :disabled="room.stock <= 0"
                @click="openBookingModal(room)"
              >
                {{ room.stock > 0 ? '预订' : '已满' }}
              </button>
            </div>
          </div>
        </div>

        <div class="empty-rooms" v-else>
          <p>暂无可用房型</p>
        </div>
      </div>

      <!-- 外部预订入口 -->
      <div class="section-card external-booking">
        <h2 class="section-title">🔗 第三方平台预订</h2>
        <p class="external-tip">您也可以通过以下平台预订，预订完成后可在本站记录订单信息</p>
        <div class="platform-links">
          <a 
            v-for="(link, platform) in bookingLinks" 
            :key="platform"
            :href="link"
            target="_blank"
            class="platform-btn"
            :class="platform"
            @click="recordExternalClick(platform)"
          >
            <span class="platform-icon">{{ getPlatformIcon(platform) }}</span>
            <span class="platform-name">{{ getPlatformName(platform) }}</span>
          </a>
        </div>
        <button class="btn-record-order" @click="showExternalOrderModal = true">
          📝 已在其他平台预订？点击记录订单
        </button>
      </div>
    </template>

    <!-- 预订弹窗 -->
    <div class="booking-modal" v-if="showBookingModal" @click.self="showBookingModal = false">
      <div class="modal-content">
        <div class="modal-header">
          <h3>确认预订</h3>
          <button class="close-btn" @click="showBookingModal = false">✕</button>
        </div>
        
        <div class="modal-body">
          <div class="booking-summary">
            <div class="summary-item">
              <span class="label">酒店</span>
              <span class="value">{{ hotel?.name }}</span>
            </div>
            <div class="summary-item">
              <span class="label">房型</span>
              <span class="value">{{ selectedRoom?.name }}</span>
            </div>
            <div class="summary-item">
              <span class="label">入住日期</span>
              <span class="value">{{ checkInDate }}</span>
            </div>
            <div class="summary-item">
              <span class="label">退房日期</span>
              <span class="value">{{ checkOutDate }}</span>
            </div>
            <div class="summary-item">
              <span class="label">入住晚数</span>
              <span class="value">{{ nights }}晚</span>
            </div>
          </div>

          <div class="booking-form">
            <div class="form-item">
              <label>房间数量</label>
              <div class="number-input">
                <button @click="bookingForm.roomCount = Math.max(1, bookingForm.roomCount - 1)">-</button>
                <span>{{ bookingForm.roomCount }}</span>
                <button @click="bookingForm.roomCount = Math.min(selectedRoom?.stock || 1, bookingForm.roomCount + 1)">+</button>
              </div>
            </div>
            <div class="form-item">
              <label>入住人数</label>
              <div class="number-input">
                <button @click="bookingForm.guestCount = Math.max(1, bookingForm.guestCount - 1)">-</button>
                <span>{{ bookingForm.guestCount }}</span>
                <button @click="bookingForm.guestCount = Math.min((selectedRoom?.maxGuests || 2) * bookingForm.roomCount, bookingForm.guestCount + 1)">+</button>
              </div>
            </div>
            <div class="form-item">
              <label>入住人姓名</label>
              <input type="text" v-model="bookingForm.guestName" placeholder="请输入入住人姓名" />
            </div>
            <div class="form-item">
              <label>联系电话</label>
              <input type="tel" v-model="bookingForm.guestPhone" placeholder="请输入联系电话" />
            </div>
            <div class="form-item">
              <label>备注</label>
              <textarea v-model="bookingForm.remark" placeholder="如有特殊需求请备注"></textarea>
            </div>
          </div>

          <div class="booking-total">
            <span class="total-label">订单总价</span>
            <span class="total-price">¥{{ totalPrice.toFixed(0) }}</span>
          </div>
        </div>

        <div class="modal-footer">
          <button class="btn-cancel" @click="showBookingModal = false">取消</button>
          <button class="btn-confirm" @click="submitBooking" :disabled="submitting">
            {{ submitting ? '提交中...' : '确认预订' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 外部订单记录弹窗 -->
    <div class="booking-modal" v-if="showExternalOrderModal" @click.self="showExternalOrderModal = false">
      <div class="modal-content">
        <div class="modal-header">
          <h3>📝 记录外部订单</h3>
          <button class="close-btn" @click="showExternalOrderModal = false">✕</button>
        </div>
        
        <div class="modal-body">
          <p class="external-modal-tip">在其他平台完成预订后，填写订单信息以便统一管理</p>
          
          <div class="booking-form">
            <div class="form-item">
              <label>预订平台 *</label>
              <select v-model="externalForm.platform">
                <option value="">请选择平台</option>
                <option value="ctrip">携程</option>
                <option value="meituan">美团</option>
                <option value="qunar">去哪儿</option>
                <option value="booking">Booking</option>
                <option value="other">其他平台</option>
              </select>
            </div>
            <div class="form-item">
              <label>入住日期 *</label>
              <input type="date" v-model="externalForm.checkInDate" :min="today" />
            </div>
            <div class="form-item">
              <label>退房日期 *</label>
              <input type="date" v-model="externalForm.checkOutDate" :min="externalForm.checkInDate || today" />
            </div>
            <div class="form-item">
              <label>外部订单号</label>
              <input type="text" v-model="externalForm.externalOrderId" placeholder="平台订单号（选填）" />
            </div>
            <div class="form-item">
              <label>确认码</label>
              <input type="text" v-model="externalForm.confirmationCode" placeholder="酒店确认码（选填）" />
            </div>
            <div class="form-item">
              <label>订单金额</label>
              <input type="number" v-model="externalForm.totalPrice" placeholder="订单总金额（选填）" />
            </div>
            <div class="form-item">
              <label>入住人姓名</label>
              <input type="text" v-model="externalForm.guestName" placeholder="入住人姓名（选填）" />
            </div>
            <div class="form-item">
              <label>备注</label>
              <textarea v-model="externalForm.remark" placeholder="其他备注信息"></textarea>
            </div>
          </div>
        </div>

        <div class="modal-footer">
          <button class="btn-cancel" @click="showExternalOrderModal = false">取消</button>
          <button class="btn-confirm" @click="submitExternalOrder" :disabled="submittingExternal">
            {{ submittingExternal ? '提交中...' : '记录订单' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useAuthStore } from '@/modules/auth/store';
import { 
  getHotelById, 
  getHotelRooms, 
  createHotelOrder,
  getBookingLinks,
  createExternalOrder,
  getBedTypeName,
  type Hotel, 
  type HotelRoom,
  type BookingLinks
} from '@/api/hotel';
import { AMAP_CONFIG } from '@/config/amap';
import { ElMessage } from 'element-plus';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

const hotel = ref<Hotel | null>(null);
const rooms = ref<HotelRoom[]>([]);
const loading = ref(true);
const defaultImage = 'https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800';
const defaultRoomImage = 'https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=400';

// 日期相关
const today = new Date().toISOString().split('T')[0];
const checkInDate = ref(today);
const checkOutDate = ref('');

// 预订相关
const showBookingModal = ref(false);
const selectedRoom = ref<HotelRoom | null>(null);
const submitting = ref(false);

const bookingForm = reactive({
  roomCount: 1,
  guestCount: 2,
  guestName: '',
  guestPhone: '',
  remark: ''
});

// 外部预订相关
const bookingLinks = ref<BookingLinks>({});
const showExternalOrderModal = ref(false);
const submittingExternal = ref(false);

const externalForm = reactive({
  platform: '',
  checkInDate: '',
  checkOutDate: '',
  externalOrderId: '',
  confirmationCode: '',
  totalPrice: undefined as number | undefined,
  guestName: '',
  remark: ''
});

// 计算属性
const minCheckOutDate = computed(() => {
  if (!checkInDate.value) return today;
  const date = new Date(checkInDate.value);
  date.setDate(date.getDate() + 1);
  return date.toISOString().split('T')[0];
});

const nights = computed(() => {
  if (!checkInDate.value || !checkOutDate.value) return 1;
  const start = new Date(checkInDate.value);
  const end = new Date(checkOutDate.value);
  const diff = Math.ceil((end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24));
  return diff > 0 ? diff : 1;
});

const facilities = computed(() => {
  if (!hotel.value?.facilities) return [];
  try {
    return JSON.parse(hotel.value.facilities);
  } catch {
    return hotel.value.facilities.split(',');
  }
});

const totalPrice = computed(() => {
  if (!selectedRoom.value) return 0;
  return selectedRoom.value.price * nights.value * bookingForm.roomCount;
});

// 初始化退房日期
watch(checkInDate, (newVal) => {
  if (newVal) {
    const date = new Date(newVal);
    date.setDate(date.getDate() + 1);
    checkOutDate.value = date.toISOString().split('T')[0];
  }
});

onMounted(async () => {
  const hotelId = Number(route.params.id);
  if (!hotelId) {
    router.push('/hotels');
    return;
  }
  
  // 初始化退房日期
  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);
  checkOutDate.value = tomorrow.toISOString().split('T')[0];
  
  await loadHotelData(hotelId);
});

async function loadHotelData(hotelId: number) {
  loading.value = true;
  try {
    const [hotelRes, roomsRes, linksRes] = await Promise.all([
      getHotelById(hotelId),
      getHotelRooms(hotelId),
      getBookingLinks(hotelId)
    ]);
    hotel.value = hotelRes.data;
    rooms.value = roomsRes.data || [];
    bookingLinks.value = linksRes.data || {};
    
    // 初始化地图
    if (hotel.value?.latitude && hotel.value?.longitude) {
      setTimeout(() => initMap(), 100);
    }
  } catch (error) {
    console.error('加载酒店数据失败:', error);
    ElMessage.error('加载失败');
  } finally {
    loading.value = false;
  }
}

// 地图相关
let map: any = null;

function initMap() {
  if (!hotel.value || !window.AMap) {
    // 如果高德地图未加载，动态加载
    loadAMapScript().then(() => {
      createMap();
    });
  } else {
    createMap();
  }
}

function loadAMapScript(): Promise<void> {
  return new Promise((resolve, reject) => {
    if (window.AMap) {
      resolve();
      return;
    }
    
    const script = document.createElement('script');
    script.src = `https://webapi.amap.com/maps?v=2.0&key=${AMAP_CONFIG.key}&plugin=AMap.Marker,AMap.InfoWindow`;
    script.onload = () => resolve();
    script.onerror = () => reject(new Error('高德地图加载失败'));
    document.head.appendChild(script);
  });
}

function createMap() {
  if (!hotel.value || !window.AMap) return;
  
  const container = document.getElementById('hotel-map');
  if (!container) return;
  
  // 创建地图
  map = new window.AMap.Map('hotel-map', {
    zoom: 15,
    center: [hotel.value.longitude, hotel.value.latitude],
    viewMode: '2D'
  });
  
  // 添加标记
  const marker = new window.AMap.Marker({
    position: [hotel.value.longitude, hotel.value.latitude],
    title: hotel.value.name
  });
  map.add(marker);
  
  // 添加信息窗口
  const infoWindow = new window.AMap.InfoWindow({
    content: `
      <div style="padding: 10px;">
        <h4 style="margin: 0 0 5px 0;">${hotel.value.name}</h4>
        <p style="margin: 0; color: #666; font-size: 12px;">${hotel.value.address}</p>
      </div>
    `,
    offset: new window.AMap.Pixel(0, -30)
  });
  
  marker.on('click', () => {
    infoWindow.open(map, marker.getPosition());
  });
  
  // 默认打开信息窗口
  infoWindow.open(map, marker.getPosition());
}

// 声明全局 AMap 类型
declare global {
  interface Window {
    AMap: any;
  }
}

function getFacilityIcon(facility: string): string {
  const icons: Record<string, string> = {
    'WiFi': '📶',
    '免费WiFi': '📶',
    '停车场': '🅿️',
    '免费停车': '🅿️',
    '游泳池': '🏊',
    '健身房': '💪',
    '餐厅': '🍽️',
    '酒吧': '🍸',
    '会议室': '💼',
    '洗衣服务': '👔',
    '行李寄存': '🧳',
    '24小时前台': '🛎️',
    '空调': '❄️',
    '电梯': '🛗',
    'SPA': '💆',
    '接机服务': '✈️'
  };
  return icons[facility] || '✓';
}

function openBookingModal(room: HotelRoom) {
  if (!authStore.isLoggedIn) {
    ElMessage.warning('请先登录');
    router.push('/login');
    return;
  }
  
  selectedRoom.value = room;
  bookingForm.roomCount = 1;
  bookingForm.guestCount = Math.min(2, room.maxGuests);
  bookingForm.guestName = authStore.user?.nickname || '';
  bookingForm.guestPhone = '';
  bookingForm.remark = '';
  showBookingModal.value = true;
}

async function submitBooking() {
  if (!selectedRoom.value || !hotel.value) return;
  
  if (!bookingForm.guestName.trim()) {
    ElMessage.warning('请输入入住人姓名');
    return;
  }
  if (!bookingForm.guestPhone.trim()) {
    ElMessage.warning('请输入联系电话');
    return;
  }
  
  submitting.value = true;
  try {
    await createHotelOrder({
      userId: authStore.user!.id,
      hotelId: hotel.value.id,
      roomId: selectedRoom.value.id,
      checkInDate: checkInDate.value,
      checkOutDate: checkOutDate.value,
      roomCount: bookingForm.roomCount,
      guestCount: bookingForm.guestCount,
      guestName: bookingForm.guestName,
      guestPhone: bookingForm.guestPhone,
      remark: bookingForm.remark
    });
    
    ElMessage.success('预订成功！');
    showBookingModal.value = false;
    
    // 刷新房型数据
    await loadHotelData(hotel.value.id);
  } catch (error: any) {
    ElMessage.error(error.response?.data?.error || '预订失败');
  } finally {
    submitting.value = false;
  }
}

// ==================== 外部预订相关 ====================

function getPlatformIcon(platform: string): string {
  const icons: Record<string, string> = {
    ctrip: '🏨',
    meituan: '🍊',
    qunar: '✈️',
    booking: '🅱️'
  };
  return icons[platform] || '🔗';
}

function getPlatformName(platform: string): string {
  const names: Record<string, string> = {
    ctrip: '携程',
    meituan: '美团',
    qunar: '去哪儿',
    booking: 'Booking'
  };
  return names[platform] || platform;
}

function recordExternalClick(platform: string) {
  // 记录用户点击了哪个平台，可用于统计
  console.log(`用户点击了 ${platform} 预订链接`);
}

async function submitExternalOrder() {
  if (!hotel.value || !authStore.isLoggedIn) return;
  
  if (!externalForm.platform) {
    ElMessage.warning('请选择预订平台');
    return;
  }
  if (!externalForm.checkInDate || !externalForm.checkOutDate) {
    ElMessage.warning('请选择入住和退房日期');
    return;
  }
  
  submittingExternal.value = true;
  try {
    await createExternalOrder({
      userId: authStore.user!.id,
      hotelId: hotel.value.id,
      checkInDate: externalForm.checkInDate,
      checkOutDate: externalForm.checkOutDate,
      platform: externalForm.platform,
      externalOrderId: externalForm.externalOrderId || undefined,
      confirmationCode: externalForm.confirmationCode || undefined,
      totalPrice: externalForm.totalPrice,
      guestName: externalForm.guestName || undefined,
      remark: externalForm.remark || undefined
    });
    
    ElMessage.success('订单记录成功！');
    showExternalOrderModal.value = false;
    
    // 重置表单
    externalForm.platform = '';
    externalForm.checkInDate = '';
    externalForm.checkOutDate = '';
    externalForm.externalOrderId = '';
    externalForm.confirmationCode = '';
    externalForm.totalPrice = undefined;
    externalForm.guestName = '';
    externalForm.remark = '';
  } catch (error: any) {
    ElMessage.error(error.response?.data?.error || '记录失败');
  } finally {
    submittingExternal.value = false;
  }
}
</script>

<style scoped>
.hotel-detail-page {
  animation: fadeInUp 0.5s ease;
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.back-btn {
  position: fixed;
  top: 100px;
  left: 20px;
  z-index: 100;
  padding: 10px 20px;
  background: rgba(255, 255, 255, 0.95);
  border: none;
  border-radius: 25px;
  font-size: 14px;
  cursor: pointer;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
}

.back-btn:hover {
  transform: translateX(-5px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
}

/* 酒店头部 */
.hotel-header {
  position: relative;
  height: 350px;
  border-radius: 24px;
  overflow: hidden;
  margin-bottom: 24px;
}

.header-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.header-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 40px 30px 30px;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.8));
  color: white;
}

.hotel-stars {
  margin-bottom: 8px;
}

.hotel-name {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 8px;
}

.hotel-address {
  font-size: 15px;
  opacity: 0.9;
}

/* 信息卡片 */
.hotel-info-card {
  background: white;
  border-radius: 20px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.info-row {
  display: flex;
  justify-content: space-around;
  flex-wrap: wrap;
  gap: 20px;
}

.info-item {
  text-align: center;
}

.info-label {
  display: block;
  font-size: 13px;
  color: #999;
  margin-bottom: 4px;
}

.info-value {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.info-value.rating {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  font-size: 24px;
}

/* 通用卡片 */
.section-card {
  background: white;
  border-radius: 20px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
}

/* 设施 */
.facilities-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 16px;
}

.facility-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 12px;
}

.facility-icon {
  font-size: 20px;
}

.facility-name {
  font-size: 14px;
  color: #666;
}

/* 酒店介绍 */
.hotel-description {
  font-size: 15px;
  color: #666;
  line-height: 1.8;
}

/* 日期选择 */
.date-picker {
  display: flex;
  gap: 20px;
  align-items: center;
  margin-bottom: 24px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 12px;
}

.date-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.date-item label {
  font-size: 13px;
  color: #666;
}

.date-item input {
  padding: 10px 14px;
  border: 2px solid #e0e0e0;
  border-radius: 10px;
  font-size: 14px;
}

.date-item input:focus {
  outline: none;
  border-color: #667eea;
}

.nights-info {
  margin-left: auto;
  font-size: 15px;
  color: #666;
}

.nights-info strong {
  color: #667eea;
  font-size: 20px;
}

/* 房型列表 */
.rooms-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.room-card {
  display: flex;
  gap: 20px;
  padding: 20px;
  background: #fafafa;
  border-radius: 16px;
  transition: all 0.3s;
}

.room-card:hover {
  background: #f5f5f5;
}

.room-image {
  width: 180px;
  height: 120px;
  border-radius: 12px;
  overflow: hidden;
  flex-shrink: 0;
}

.room-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.room-info {
  flex: 1;
  min-width: 0;
}

.room-name {
  font-size: 17px;
  font-weight: 600;
  color: #333;
  margin-bottom: 10px;
}

.room-details {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 10px;
}

.detail-tag {
  padding: 4px 10px;
  background: #e8e8e8;
  border-radius: 6px;
  font-size: 12px;
  color: #666;
}

.detail-tag.breakfast {
  background: #e8f5e9;
  color: #2e7d32;
}

.room-description {
  font-size: 13px;
  color: #999;
  margin-bottom: 8px;
}

.room-stock {
  font-size: 13px;
  color: #4caf50;
}

.room-stock.low {
  color: #ff9800;
}

.room-action {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: space-between;
  min-width: 120px;
}

.room-price {
  display: flex;
  align-items: baseline;
}

.room-price .price-label {
  color: #ff6b6b;
  font-size: 14px;
}

.room-price .price-value {
  color: #ff6b6b;
  font-size: 26px;
  font-weight: 700;
}

.room-price .price-unit {
  color: #999;
  font-size: 12px;
}

.total-price {
  font-size: 13px;
  color: #999;
}

.btn-book {
  padding: 12px 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 25px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-book:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.btn-book:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.empty-rooms {
  text-align: center;
  padding: 40px;
  color: #999;
}

/* 预订弹窗 */
.booking-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.modal-content {
  background: white;
  border-radius: 24px;
  width: 100%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.modal-header h3 {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
}

.close-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #f5f5f5;
  border: none;
  font-size: 18px;
  cursor: pointer;
  transition: background 0.2s;
}

.close-btn:hover {
  background: #eee;
}

.modal-body {
  padding: 24px;
}

.booking-summary {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 24px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
}

.summary-item .label {
  color: #666;
}

.summary-item .value {
  font-weight: 500;
  color: #333;
}

.booking-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-item label {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.form-item input,
.form-item textarea {
  padding: 12px 16px;
  border: 2px solid #e0e0e0;
  border-radius: 10px;
  font-size: 15px;
  transition: border-color 0.3s;
}

.form-item input:focus,
.form-item textarea:focus {
  outline: none;
  border-color: #667eea;
}

.form-item textarea {
  min-height: 80px;
  resize: vertical;
}

.number-input {
  display: flex;
  align-items: center;
  gap: 16px;
}

.number-input button {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: 2px solid #e0e0e0;
  background: white;
  font-size: 18px;
  cursor: pointer;
  transition: all 0.2s;
}

.number-input button:hover {
  border-color: #667eea;
  color: #667eea;
}

.number-input span {
  font-size: 18px;
  font-weight: 600;
  min-width: 30px;
  text-align: center;
}

.booking-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 0;
  margin-top: 16px;
  border-top: 2px solid #f0f0f0;
}

.total-label {
  font-size: 16px;
  color: #666;
}

.booking-total .total-price {
  font-size: 28px;
  font-weight: 700;
  color: #ff6b6b;
}

.modal-footer {
  display: flex;
  gap: 12px;
  padding: 24px;
  border-top: 1px solid #f0f0f0;
}

.btn-cancel {
  flex: 1;
  padding: 14px;
  background: #f5f5f5;
  border: none;
  border-radius: 12px;
  font-size: 15px;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-cancel:hover {
  background: #eee;
}

.btn-confirm {
  flex: 2;
  padding: 14px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-confirm:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.btn-confirm:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

/* 加载状态 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px;
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

/* 响应式 */
@media (max-width: 768px) {
  .hotel-header {
    height: 250px;
    border-radius: 0;
    margin: -20px -20px 24px;
  }
  
  .back-btn {
    top: 80px;
    left: 10px;
  }
  
  .room-card {
    flex-direction: column;
  }
  
  .room-image {
    width: 100%;
    height: 160px;
  }
  
  .room-action {
    flex-direction: row;
    align-items: center;
    width: 100%;
    margin-top: 12px;
    padding-top: 12px;
    border-top: 1px solid #eee;
  }
  
  .date-picker {
    flex-direction: column;
    align-items: stretch;
  }
  
  .nights-info {
    margin-left: 0;
    text-align: center;
  }
}

/* 外部预订区域 */
.external-booking {
  background: linear-gradient(135deg, #f8f9ff 0%, #fff5f5 100%);
}

.external-tip {
  color: #666;
  font-size: 14px;
  margin-bottom: 20px;
}

.platform-links {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 20px;
}

.platform-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  border-radius: 12px;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
}

.platform-btn.ctrip {
  background: linear-gradient(135deg, #2681ff 0%, #1a5dc9 100%);
  color: white;
}

.platform-btn.meituan {
  background: linear-gradient(135deg, #ffb800 0%, #ff9500 100%);
  color: white;
}

.platform-btn.qunar {
  background: linear-gradient(135deg, #00c8dc 0%, #009eb3 100%);
  color: white;
}

.platform-btn.booking {
  background: linear-gradient(135deg, #003580 0%, #00224f 100%);
  color: white;
}

.platform-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
}

.platform-icon {
  font-size: 18px;
}

.btn-record-order {
  width: 100%;
  padding: 14px;
  background: white;
  border: 2px dashed #ddd;
  border-radius: 12px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-record-order:hover {
  border-color: #667eea;
  color: #667eea;
  background: #f8f9ff;
}

.external-modal-tip {
  color: #666;
  font-size: 14px;
  margin-bottom: 20px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
}

.form-item select {
  padding: 12px 16px;
  border: 2px solid #e0e0e0;
  border-radius: 10px;
  font-size: 15px;
  transition: border-color 0.3s;
  background: white;
}

.form-item select:focus {
  outline: none;
  border-color: #667eea;
}

/* 地图样式 */
.location-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 12px;
}

.location-info .address {
  font-size: 15px;
  color: #666;
  margin: 0;
}

.nav-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-decoration: none;
  border-radius: 25px;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
}

.nav-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.map-container {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.hotel-map {
  width: 100%;
  height: 300px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8eb 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
}

/* 地图加载前的占位 */
.hotel-map:empty::before {
  content: '🗺️ 地图加载中...';
  font-size: 16px;
}

@media (max-width: 768px) {
  .location-info {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .hotel-map {
    height: 250px;
  }
}
</style>
