<template>
  <div class="profile-page">
    <!-- 用户信息卡片 -->
    <div class="user-card">
      <div class="user-avatar" @click="showAvatarUpload = true">
        <img v-if="authStore.avatar" :src="authStore.avatar" alt="头像" />
        <span v-else>{{ authStore.nickname?.charAt(0) || '游' }}</span>
        <div class="avatar-overlay">
          <span>📷</span>
        </div>
      </div>
      <div class="user-info">
        <h1 class="user-name">{{ authStore.nickname || '旅行者' }}</h1>
        <p class="user-desc">{{ userMotto }}</p>
        <div class="user-level">
          <span class="level-badge">{{ userLevel.badge }}</span>
          <span class="level-name">{{ userLevel.name }}</span>
          <div class="level-progress">
            <div class="level-bar" :style="{ width: userLevel.progress + '%' }"></div>
          </div>
        </div>
        <div class="user-actions">
          <button class="btn-edit" @click="showEditProfile = true">✏️ 编辑资料</button>
          <button class="btn-settings" @click="showSettings = true">⚙️ 设置</button>
          <button class="btn-logout" @click="handleLogout">🚪 退出</button>
        </div>
      </div>
      <div class="user-stats">
        <div class="stat-item">
          <span class="stat-value">{{ favs.length }}</span>
          <span class="stat-label">收藏</span>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <span class="stat-value">{{ orders.length }}</span>
          <span class="stat-label">订单</span>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <span class="stat-value">{{ itineraries.length }}</span>
          <span class="stat-label">行程</span>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <span class="stat-value">{{ totalDistance }}</span>
          <span class="stat-label">公里</span>
        </div>
      </div>
    </div>
    
    <!-- 成就徽章 -->
    <div class="achievements-card">
      <div class="achievements-header">
        <h2>🏆 我的成就</h2>
        <span class="achievements-count">{{ unlockedAchievements.length }}/{{ achievements.length }}</span>
      </div>
      <div class="achievements-list">
        <div 
          v-for="achievement in achievements" 
          :key="achievement.id" 
          class="achievement-item"
          :class="{ unlocked: achievement.unlocked }"
        >
          <span class="achievement-icon">{{ achievement.icon }}</span>
          <div class="achievement-info">
            <span class="achievement-name">{{ achievement.name }}</span>
            <span class="achievement-desc">{{ achievement.description }}</span>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 编辑资料弹窗 -->
    <el-dialog v-model="showEditProfile" title="编辑资料" width="400px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="昵称">
          <el-input v-model="editForm.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="年龄">
          <el-input-number v-model="editForm.age" :min="1" :max="120" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="editForm.gender">
            <el-radio value="male">男</el-radio>
            <el-radio value="female">女</el-radio>
            <el-radio value="other">保密</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditProfile = false">取消</el-button>
        <el-button type="primary" @click="saveProfile">保存</el-button>
      </template>
    </el-dialog>

    <!-- 内容区域 -->
    <div class="content-grid">
      <!-- 景点收藏 -->
      <div class="section-card">
        <div class="section-header">
          <h2 class="section-title">
            <span class="section-icon">🏞️</span>
            景点收藏
          </h2>
          <span class="section-count">{{ favs.length }} 个景点</span>
        </div>
        
        <div v-if="loading" class="loading-state">
          <div class="loading-spinner"></div>
        </div>
        
        <div v-else-if="favs.length === 0" class="empty-state">
          <div class="empty-icon">💝</div>
          <p>还没有收藏的景点</p>
          <router-link to="/spots" class="btn-explore">去发现</router-link>
        </div>
        
        <div v-else class="favorites-list">
          <div v-for="f in favs" :key="f.id" class="favorite-item">
            <div class="favorite-icon">📍</div>
            <div class="favorite-info">
              <span class="favorite-name">景点 #{{ f.spotId }}</span>
              <span class="favorite-date">{{ formatDate(f.createdAt) }}</span>
            </div>
            <router-link :to="`/spots/${f.spotId}`" class="btn-view">
              查看 →
            </router-link>
          </div>
        </div>
      </div>
      
      <!-- 行程收藏 -->
      <div class="section-card">
        <div class="section-header">
          <h2 class="section-title">
            <span class="section-icon">🗺️</span>
            行程收藏
          </h2>
          <span class="section-count">{{ itineraries.length }} 个行程</span>
        </div>
        
        <div v-if="loading" class="loading-state">
          <div class="loading-spinner"></div>
        </div>
        
        <div v-else-if="itineraries.length === 0" class="empty-state">
          <div class="empty-icon">🧭</div>
          <p>还没有收藏的行程</p>
          <router-link to="/recommend" class="btn-explore">去规划</router-link>
        </div>
        
        <div v-else class="itineraries-list">
          <div v-for="(item, index) in itineraries" :key="index" class="itinerary-item">
            <div class="itinerary-header">
              <span class="itinerary-name">{{ item.name }}</span>
              <span class="itinerary-date">{{ formatDate(item.createdAt) }}</span>
            </div>
            <div class="itinerary-body">
              <div class="itinerary-spots">
                <span v-for="(spot, i) in item.spots?.slice(0, 3)" :key="i" class="spot-tag">
                  {{ spot.name }}
                </span>
                <span v-if="item.spots?.length > 3" class="spot-more">
                  +{{ item.spots.length - 3 }}
                </span>
              </div>
              <div class="itinerary-info">
                <span>{{ item.spots?.length || 0 }} 个景点</span>
                <span>约 ¥{{ item.totalCost || 0 }}</span>
              </div>
            </div>
            <div class="itinerary-actions">
              <button class="btn-view-itinerary" @click="viewItinerary(item)">
                查看详情
              </button>
              <button class="btn-delete-itinerary" @click="deleteItinerary(index)">
                删除
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 订单列表 -->
      <div class="section-card">
        <div class="section-header">
          <h2 class="section-title">
            <span class="section-icon">🎫</span>
            我的订单
          </h2>
          <span class="section-count">{{ orders.length }} 个订单</span>
        </div>
        
        <div v-if="loading" class="loading-state">
          <div class="loading-spinner"></div>
        </div>
        
        <div v-else-if="orders.length === 0" class="empty-state">
          <div class="empty-icon">📋</div>
          <p>还没有订单记录</p>
          <router-link to="/spots" class="btn-explore">去预订</router-link>
        </div>
        
        <div v-else class="orders-list">
          <div v-for="o in orders" :key="o.id" class="order-item">
            <div class="order-header">
              <span class="order-id">订单 #{{ o.id }}</span>
              <span 
                class="order-status" 
                :class="getStatusClass(o.orderStatus)"
              >
                {{ getStatusText(o.orderStatus) }}
              </span>
            </div>
            <div class="order-body">
              <div class="order-detail">
                <span class="detail-label">景点</span>
                <span class="detail-value">景点 #{{ o.spotId }}</span>
              </div>
              <div class="order-detail">
                <span class="detail-label">金额</span>
                <span class="detail-value price">¥{{ o.amount }}</span>
              </div>
            </div>
            <div class="order-footer">
              <span class="order-time">{{ formatDate(o.createdAt) }}</span>
              <router-link :to="`/spots/${o.spotId}`" class="btn-view-order">
                查看景点
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { favoriteList } from '@/api/social';
import { listOrders } from '@/api/order';
import { getUserItineraries, deleteItinerary as deleteItineraryApi } from '@/api/recommend';
import { updateProfile as updateProfileApi } from '@/api/auth';
import { useAuthStore } from '@/modules/auth/store';
import { ElMessage, ElMessageBox } from 'element-plus';

// 组件名称，用于 keep-alive 缓存
defineOptions({
  name: 'Profile'
});

const router = useRouter();
const authStore = useAuthStore();

const favs = ref<any[]>([]);
const orders = ref<any[]>([]);
const itineraries = ref<any[]>([]);
const loading = ref(true);
const showEditProfile = ref(false);
const showSettings = ref(false);
const showAvatarUpload = ref(false);

// 用户座右铭
const mottos = [
  '探索世界，发现美好',
  '生活不止眼前的苟且，还有诗和远方',
  '旅行是心灵的阅读',
  '世界那么大，我想去看看',
  '每一次旅行都是一次成长'
];
const userMotto = ref(mottos[Math.floor(Math.random() * mottos.length)]);

// 计算总行程距离
const totalDistance = computed(() => {
  let total = 0;
  itineraries.value.forEach(item => {
    if (item.route?.totalDistance) {
      total += item.route.totalDistance;
    }
  });
  return total.toFixed(1);
});

// 用户等级计算
const userLevel = computed(() => {
  const points = favs.value.length * 10 + orders.value.length * 20 + itineraries.value.length * 30;
  
  if (points >= 500) return { badge: '🌟', name: '旅行大师', progress: 100 };
  if (points >= 300) return { badge: '✨', name: '资深玩家', progress: (points - 300) / 200 * 100 };
  if (points >= 150) return { badge: '🎯', name: '探索达人', progress: (points - 150) / 150 * 100 };
  if (points >= 50) return { badge: '🚀', name: '旅行新手', progress: (points - 50) / 100 * 100 };
  return { badge: '🌱', name: '萌新旅者', progress: points / 50 * 100 };
});

// 成就系统
const achievements = computed(() => [
  { id: 1, icon: '🎒', name: '初次启程', description: '完成第一次行程规划', unlocked: itineraries.value.length >= 1 },
  { id: 2, icon: '❤️', name: '收藏达人', description: '收藏 5 个景点', unlocked: favs.value.length >= 5 },
  { id: 3, icon: '🎫', name: '消费先锋', description: '完成首次订单', unlocked: orders.value.length >= 1 },
  { id: 4, icon: '🗺️', name: '规划专家', description: '规划 3 条行程', unlocked: itineraries.value.length >= 3 },
  { id: 5, icon: '🏃', name: '行走达人', description: '累计行程超过 50 公里', unlocked: parseFloat(totalDistance.value) >= 50 },
  { id: 6, icon: '💎', name: 'VIP 会员', description: '订单金额累计超过 500 元', unlocked: orders.value.reduce((sum, o) => sum + (o.amount || 0), 0) >= 500 },
]);

const unlockedAchievements = computed(() => achievements.value.filter(a => a.unlocked));

const editForm = reactive({
  nickname: '',
  age: undefined as number | undefined,
  gender: ''
});

onMounted(async () => {
  // 初始化编辑表单
  if (authStore.user) {
    editForm.nickname = authStore.user.nickname || '';
  }
  
  // 检查是否登录
  if (!authStore.isLoggedIn || !authStore.user?.id) {
    ElMessage.warning('请先登录');
    router.push('/login');
    return;
  }
  
  try {
    const userId = authStore.user.id;
    const f = await favoriteList(userId);
    favs.value = f.data || [];
    const o = await listOrders(userId);
    orders.value = o.data || [];
    
    // 从数据库获取保存的行程
    try {
      const res = await getUserItineraries(userId);
      itineraries.value = (res.data || []).map((item: any) => {
        // 解析 itineraryData JSON
        let data: any = {};
        try {
          data = JSON.parse(item.itineraryData || '{}');
        } catch (e) {
          console.error('解析行程数据失败', e);
        }
        
        // 兼容新旧数据格式
        const resultData = data.result || data;
        const formData = data.formData || null;
        
        return {
          id: item.id,
          name: item.name,
          formData: formData,  // 保存的表单数据
          spots: resultData.spots || [],
          totalCost: resultData.cost?.totalCost || 0,
          createdAt: item.createTime
        };
      });
    } catch (e) {
      console.log('从数据库获取行程失败，尝试本地存储');
      // 降级到本地存储（按用户区分）
      const savedItineraries = localStorage.getItem(`travel_itineraries_${userId}`);
      if (savedItineraries) {
        itineraries.value = JSON.parse(savedItineraries);
      }
    }
  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
});

function handleLogout() {
  authStore.logout();
  ElMessage.success('已退出登录');
  router.push('/login');
}

async function saveProfile() {
  try {
    const userId = authStore.user?.id;
    if (!userId) {
      ElMessage.error('请先登录');
      return;
    }
    
    // 调用后端 API 更新资料
    await updateProfileApi(userId, {
      nickname: editForm.nickname,
      age: editForm.age,
      gender: editForm.gender
    });
    
    // 更新本地状态
    await authStore.updateProfile({
      nickname: editForm.nickname
    });
    
    ElMessage.success('资料已更新');
    showEditProfile.value = false;
  } catch (e) {
    ElMessage.error('更新失败');
  }
}

function formatDate(date: string) {
  if (!date) return '刚刚';
  return new Date(date).toLocaleDateString('zh-CN');
}

// 查看行程详情
function viewItinerary(item: any) {
  console.log('viewItinerary 被调用，数据:', item);
  
  if (item.formData) {
    // 新格式：有表单数据，跳转到智能推荐页面并自动填充表单重新推荐
    const params = new URLSearchParams();
    params.set('regionId', item.formData.regionId);
    params.set('age', item.formData.age);
    params.set('playTimeHours', item.formData.playTimeHours);
    params.set('peopleCount', item.formData.peopleCount);
    if (item.formData.budget) params.set('budget', item.formData.budget);
    if (item.formData.preference) params.set('preference', item.formData.preference);
    params.set('autoRecommend', 'true');  // 自动触发推荐
    
    // 跳转到正确的智能推荐页面
    router.push('/smart-recommend?' + params.toString());
  } else {
    // 旧格式：没有表单数据，提示用户
    ElMessage.info('该行程为旧版本保存，请重新生成');
  }
}

// 删除行程
async function deleteItinerary(index: number) {
  const item = itineraries.value[index];
  
  try {
    await ElMessageBox.confirm('确定要删除这个行程吗？', '删除确认', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    // 如果有数据库 ID，从数据库删除
    if (item.id) {
      await deleteItineraryApi(item.id);
    }
    
    itineraries.value.splice(index, 1);
    ElMessage.success('行程已删除');
  } catch (e) {
    // 用户取消删除
  }
}

function getStatusClass(status: string) {
  const map: Record<string, string> = {
    'PENDING': 'status-pending',
    'PAID': 'status-paid',
    'COMPLETED': 'status-completed',
    'CANCELLED': 'status-cancelled'
  };
  return map[status] || 'status-pending';
}

function getStatusText(status: string) {
  const map: Record<string, string> = {
    'PENDING': '待支付',
    'PAID': '已支付',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  };
  return map[status] || status;
}
</script>

<style scoped>
.profile-page {
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

/* 用户卡片 */
.user-card {
  background: white;
  border-radius: 24px;
  padding: 40px;
  margin-bottom: 32px;
  display: flex;
  align-items: center;
  gap: 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
}

.user-avatar {
  width: 100px;
  height: 100px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48px;
  flex-shrink: 0;
  color: white;
  font-weight: 700;
  overflow: hidden;
  position: relative;
  cursor: pointer;
  transition: transform 0.3s;
}

.user-avatar:hover {
  transform: scale(1.05);
}

.user-avatar:hover .avatar-overlay {
  opacity: 1;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
  font-size: 24px;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-info {
  flex: 1;
}

/* 用户等级 */
.user-level {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 8px 0;
}

.level-badge {
  font-size: 20px;
}

.level-name {
  font-size: 14px;
  color: #667eea;
  font-weight: 500;
}

.level-progress {
  width: 100px;
  height: 6px;
  background: #e0e0e0;
  border-radius: 3px;
  overflow: hidden;
}

.level-bar {
  height: 100%;
  background: linear-gradient(90deg, #667eea, #764ba2);
  border-radius: 3px;
  transition: width 0.5s ease;
}

.user-actions {
  display: flex;
  gap: 12px;
  margin-top: 12px;
}

.btn-settings {
  padding: 8px 16px;
  background: #f0f0f0;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.btn-settings:hover {
  background: #e0e0e0;
}

/* 成就卡片 */
.achievements-card {
  background: white;
  border-radius: 24px;
  padding: 24px;
  margin-bottom: 32px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
}

.achievements-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.achievements-header h2 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.achievements-count {
  font-size: 14px;
  color: #999;
}

.achievements-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 16px;
}

.achievement-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #f8f8f8;
  border-radius: 16px;
  opacity: 0.5;
  filter: grayscale(1);
  transition: all 0.3s;
}

.achievement-item.unlocked {
  opacity: 1;
  filter: grayscale(0);
  background: linear-gradient(135deg, #f5f3ff 0%, #fdf2f8 100%);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.15);
}

.achievement-icon {
  font-size: 32px;
}

.achievement-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.achievement-name {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.achievement-desc {
  font-size: 12px;
  color: #999;
}

.btn-edit {
  padding: 8px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-edit:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.btn-logout {
  padding: 8px 20px;
  background: #f5f5f5;
  color: #666;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-logout:hover {
  background: #eee;
}

.user-name {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 8px;
}

.user-desc {
  font-size: 15px;
  color: #888;
}

.user-stats {
  display: flex;
  align-items: center;
  gap: 32px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 32px;
  font-weight: 700;
  color: #667eea;
}

.stat-label {
  font-size: 14px;
  color: #888;
}

.stat-divider {
  width: 1px;
  height: 40px;
  background: #eee;
}

/* 内容网格 */
.content-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 24px;
}

/* 区块卡片 */
.section-card {
  background: white;
  border-radius: 20px;
  padding: 28px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 20px;
  font-weight: 600;
  color: #1a1a2e;
}

.section-icon {
  font-size: 24px;
}

.section-count {
  font-size: 13px;
  color: #888;
  background: #f5f5f5;
  padding: 6px 14px;
  border-radius: 20px;
}

/* 加载状态 */
.loading-state {
  display: flex;
  justify-content: center;
  padding: 40px;
}

.loading-spinner {
  width: 32px;
  height: 32px;
  border: 3px solid #e0e0e0;
  border-top-color: #667eea;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 40px 20px;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 12px;
}

.empty-state p {
  color: #888;
  margin-bottom: 16px;
}

.btn-explore {
  display: inline-block;
  padding: 10px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 10px;
  font-weight: 500;
  transition: all 0.3s;
}

.btn-explore:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
  color: white;
}

/* 收藏列表 */
.favorites-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.favorite-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 12px;
  transition: all 0.3s;
}

.favorite-item:hover {
  background: #f0f0f0;
}

.favorite-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.favorite-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.favorite-name {
  font-weight: 600;
  color: #1a1a2e;
}

.favorite-date {
  font-size: 13px;
  color: #888;
}

.btn-view {
  padding: 8px 16px;
  color: #667eea;
  font-weight: 500;
  font-size: 14px;
  transition: all 0.3s;
}

.btn-view:hover {
  color: #764ba2;
}

/* 订单列表 */
.orders-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-item {
  background: #f8f9fa;
  border-radius: 16px;
  padding: 20px;
  transition: all 0.3s;
}

.order-item:hover {
  background: #f0f0f0;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.order-id {
  font-weight: 600;
  color: #1a1a2e;
}

.order-status {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.status-pending {
  background: #fef3c7;
  color: #d97706;
}

.status-paid {
  background: #dbeafe;
  color: #2563eb;
}

.status-completed {
  background: #d1fae5;
  color: #059669;
}

.status-cancelled {
  background: #fee2e2;
  color: #dc2626;
}

.order-body {
  display: flex;
  gap: 24px;
  margin-bottom: 16px;
}

.order-detail {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-label {
  font-size: 13px;
  color: #888;
}

.detail-value {
  font-weight: 600;
  color: #1a1a2e;
}

.detail-value.price {
  color: #667eea;
  font-size: 18px;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #eee;
}

.order-time {
  font-size: 13px;
  color: #888;
}

.btn-view-order {
  color: #667eea;
  font-weight: 500;
  font-size: 14px;
}

.btn-view-order:hover {
  color: #764ba2;
}

/* 行程列表 */
.itineraries-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.itinerary-item {
  background: #f8f9fa;
  border-radius: 16px;
  padding: 20px;
  transition: all 0.3s;
}

.itinerary-item:hover {
  background: #f0f0f0;
}

.itinerary-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.itinerary-name {
  font-weight: 600;
  font-size: 16px;
  color: #1a1a2e;
}

.itinerary-date {
  font-size: 13px;
  color: #888;
}

.itinerary-body {
  margin-bottom: 16px;
}

.itinerary-spots {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.spot-tag {
  padding: 4px 12px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  color: #667eea;
  border-radius: 20px;
  font-size: 12px;
}

.spot-more {
  padding: 4px 12px;
  background: #e8e8e8;
  color: #888;
  border-radius: 20px;
  font-size: 12px;
}

.itinerary-info {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #666;
}

.itinerary-actions {
  display: flex;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid #eee;
}

.btn-view-itinerary {
  padding: 8px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-view-itinerary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.btn-delete-itinerary {
  padding: 8px 20px;
  background: #f5f5f5;
  color: #999;
  border: none;
  border-radius: 8px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-delete-itinerary:hover {
  background: #fee2e2;
  color: #dc2626;
}

@media (max-width: 768px) {
  .user-card {
    flex-direction: column;
    text-align: center;
    padding: 32px 24px;
  }
  
  .user-stats {
    width: 100%;
    justify-content: center;
  }
  
  .content-grid {
    grid-template-columns: 1fr;
  }
}
</style>
