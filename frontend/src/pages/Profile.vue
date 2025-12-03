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
        <div class="stat-item clickable" @click="showFollowList('following')">
          <span class="stat-value">{{ followStats.following }}</span>
          <span class="stat-label">关注</span>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item clickable" @click="showFollowList('followers')">
          <span class="stat-value">{{ followStats.followers }}</span>
          <span class="stat-label">粉丝</span>
        </div>
        <div class="stat-divider"></div>
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
    
    <!-- 关注/粉丝列表弹窗 -->
    <div class="follow-modal" v-if="showFollowModal" @click.self="closeFollowModal">
      <div class="follow-modal-content">
        <div class="follow-modal-header">
          <h3>{{ followModalType === 'following' ? '我的关注' : '我的粉丝' }}</h3>
          <button class="close-btn" @click="closeFollowModal">✕</button>
        </div>
        <div class="follow-list" v-if="followList.length > 0">
          <div 
            class="follow-item" 
            v-for="item in followList" 
            :key="item.userId"
          >
            <img 
              :src="item.avatar || defaultAvatar" 
              class="follow-avatar"
              @click="goToUserProfile(item.userId)"
            />
            <div class="follow-info" @click="goToUserProfile(item.userId)">
              <span class="follow-nickname">{{ item.nickname }}</span>
              <span class="follow-motto" v-if="item.motto">{{ item.motto }}</span>
            </div>
            <button 
              class="follow-action-btn"
              @click="startChat(item.userId)"
            >
              💬 私信
            </button>
          </div>
        </div>
        <div class="empty-follow" v-else>
          <div class="empty-icon">{{ followModalType === 'following' ? '👥' : '💝' }}</div>
          <p>{{ followModalType === 'following' ? '还没有关注任何人' : '还没有粉丝' }}</p>
          <span v-if="followModalType === 'following'">去发现有趣的用户吧</span>
        </div>
      </div>
    </div>

    <!-- 头像上传弹窗 -->
    <el-dialog v-model="showAvatarUpload" title="更换头像" width="450px" class="avatar-dialog">
      <div class="avatar-upload-content">
        <div class="current-avatar">
          <img v-if="previewAvatar || authStore.avatar" :src="previewAvatar || authStore.avatar" alt="当前头像" />
          <span v-else class="avatar-placeholder">{{ authStore.nickname?.charAt(0) || '游' }}</span>
        </div>
        <div class="avatar-options">
          <div class="upload-section">
            <input 
              type="file" 
              ref="avatarInput" 
              accept="image/*" 
              @change="handleAvatarSelect" 
              style="display: none"
            />
            <button class="upload-btn" @click="$refs.avatarInput.click()">
              📁 选择图片
            </button>
            <p class="upload-tip">支持 JPG、PNG 格式，大小不超过 2MB</p>
          </div>
          <div class="preset-avatars">
            <p class="preset-title">或选择预设头像</p>
            <div class="preset-grid">
              <img 
                v-for="(avatar, index) in presetAvatars" 
                :key="index"
                :src="avatar"
                :class="{ selected: previewAvatar === avatar }"
                @click="selectPresetAvatar(avatar)"
              />
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="cancelAvatarUpload">取消</el-button>
        <el-button type="primary" @click="saveAvatar" :loading="avatarUploading">保存头像</el-button>
      </template>
    </el-dialog>

    <!-- 编辑资料弹窗 - 增强版 -->
    <el-dialog v-model="showEditProfile" title="编辑资料" width="500px" class="edit-profile-dialog">
      <el-form :model="editForm" label-width="90px" class="edit-form">
        <el-divider content-position="left">基本信息</el-divider>
        <el-form-item label="昵称">
          <el-input v-model="editForm.nickname" placeholder="请输入昵称" maxlength="20" show-word-limit />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="editForm.gender">
            <el-radio value="male">👨 男</el-radio>
            <el-radio value="female">👩 女</el-radio>
            <el-radio value="other">🧑 保密</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="生日">
          <el-date-picker 
            v-model="editForm.birthday" 
            type="date" 
            placeholder="选择生日"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            :disabled-date="disabledDate"
          />
        </el-form-item>
        <el-form-item label="所在地">
          <el-cascader
            v-model="editForm.location"
            :options="locationOptions"
            placeholder="选择所在地"
            clearable
          />
        </el-form-item>
        
        <el-divider content-position="left">个人介绍</el-divider>
        <el-form-item label="座右铭">
          <el-input 
            v-model="editForm.motto" 
            type="textarea" 
            :rows="2" 
            placeholder="一句话介绍自己" 
            maxlength="50"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="个人简介">
          <el-input 
            v-model="editForm.bio" 
            type="textarea" 
            :rows="3" 
            placeholder="详细介绍一下自己吧" 
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
        
        <el-divider content-position="left">联系方式</el-divider>
        <el-form-item label="手机号">
          <el-input v-model="editForm.phone" placeholder="请输入手机号">
            <template #prefix>📱</template>
          </el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="editForm.email" placeholder="请输入邮箱">
            <template #prefix>📧</template>
          </el-input>
        </el-form-item>
        
        <el-divider content-position="left">旅行偏好</el-divider>
        <el-form-item label="旅行风格">
          <el-checkbox-group v-model="editForm.travelStyles">
            <el-checkbox value="adventure">🏔️ 探险</el-checkbox>
            <el-checkbox value="relax">🏖️ 休闲</el-checkbox>
            <el-checkbox value="culture">🏛️ 文化</el-checkbox>
            <el-checkbox value="food">🍜 美食</el-checkbox>
            <el-checkbox value="photography">📷 摄影</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="常用交通">
          <el-select v-model="editForm.preferredTransport" placeholder="选择偏好交通方式">
            <el-option value="flight" label="✈️ 飞机" />
            <el-option value="train" label="🚄 高铁" />
            <el-option value="car" label="🚗 自驾" />
            <el-option value="bus" label="🚌 大巴" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditProfile = false">取消</el-button>
        <el-button type="primary" @click="saveProfile" :loading="profileSaving">保存资料</el-button>
      </template>
    </el-dialog>

    <!-- 设置弹窗 -->
    <el-dialog v-model="showSettings" title="设置" width="500px" class="settings-dialog">
      <div class="settings-content">
        <!-- 账号安全 -->
        <div class="settings-section">
          <h3 class="section-title">🔐 账号安全</h3>
          <div class="settings-item" @click="showChangePassword = true">
            <div class="item-left">
              <span class="item-icon">🔑</span>
              <span class="item-label">修改密码</span>
            </div>
            <span class="item-arrow">›</span>
          </div>
          <div class="settings-item">
            <div class="item-left">
              <span class="item-icon">📱</span>
              <span class="item-label">绑定手机</span>
            </div>
            <span class="item-value">{{ editForm.phone || '未绑定' }}</span>
          </div>
          <div class="settings-item">
            <div class="item-left">
              <span class="item-icon">📧</span>
              <span class="item-label">绑定邮箱</span>
            </div>
            <span class="item-value">{{ editForm.email || '未绑定' }}</span>
          </div>
        </div>

        <!-- 隐私设置 -->
        <div class="settings-section">
          <h3 class="section-title">🛡️ 隐私设置</h3>
          <div class="settings-item">
            <div class="item-left">
              <span class="item-icon">👁️</span>
              <span class="item-label">公开个人主页</span>
            </div>
            <el-switch v-model="privacySettings.publicProfile" />
          </div>
          <div class="settings-item">
            <div class="item-left">
              <span class="item-icon">📍</span>
              <span class="item-label">显示所在地</span>
            </div>
            <el-switch v-model="privacySettings.showLocation" />
          </div>
          <div class="settings-item">
            <div class="item-left">
              <span class="item-icon">❤️</span>
              <span class="item-label">公开收藏列表</span>
            </div>
            <el-switch v-model="privacySettings.publicFavorites" />
          </div>
          <div class="settings-item">
            <div class="item-left">
              <span class="item-icon">🗺️</span>
              <span class="item-label">公开行程记录</span>
            </div>
            <el-switch v-model="privacySettings.publicItineraries" />
          </div>
        </div>

        <!-- 通知设置 -->
        <div class="settings-section">
          <h3 class="section-title">🔔 通知设置</h3>
          <div class="settings-item">
            <div class="item-left">
              <span class="item-icon">💬</span>
              <span class="item-label">私信通知</span>
            </div>
            <el-switch v-model="notificationSettings.message" />
          </div>
          <div class="settings-item">
            <div class="item-left">
              <span class="item-icon">👥</span>
              <span class="item-label">新粉丝通知</span>
            </div>
            <el-switch v-model="notificationSettings.newFollower" />
          </div>
          <div class="settings-item">
            <div class="item-left">
              <span class="item-icon">📢</span>
              <span class="item-label">系统公告</span>
            </div>
            <el-switch v-model="notificationSettings.systemNotice" />
          </div>
          <div class="settings-item">
            <div class="item-left">
              <span class="item-icon">🎁</span>
              <span class="item-label">优惠活动</span>
            </div>
            <el-switch v-model="notificationSettings.promotion" />
          </div>
        </div>

        <!-- 其他设置 -->
        <div class="settings-section">
          <h3 class="section-title">⚙️ 其他</h3>
          <div class="settings-item" @click="clearCache">
            <div class="item-left">
              <span class="item-icon">🗑️</span>
              <span class="item-label">清除缓存</span>
            </div>
            <span class="item-value">{{ cacheSize }}</span>
          </div>
          <div class="settings-item">
            <div class="item-left">
              <span class="item-icon">🌙</span>
              <span class="item-label">深色模式</span>
            </div>
            <el-switch v-model="otherSettings.darkMode" @change="toggleDarkMode" />
          </div>
          <div class="settings-item">
            <div class="item-left">
              <span class="item-icon">📖</span>
              <span class="item-label">版本信息</span>
            </div>
            <span class="item-value">v1.0.0</span>
          </div>
          <div class="settings-item" @click="showAbout = true">
            <div class="item-left">
              <span class="item-icon">ℹ️</span>
              <span class="item-label">关于我们</span>
            </div>
            <span class="item-arrow">›</span>
          </div>
        </div>

        <!-- 危险操作 -->
        <div class="settings-section danger-section">
          <h3 class="section-title">⚠️ 危险操作</h3>
          <div class="settings-item danger" @click="confirmLogout">
            <div class="item-left">
              <span class="item-icon">🚪</span>
              <span class="item-label">退出登录</span>
            </div>
            <span class="item-arrow">›</span>
          </div>
          <div class="settings-item danger" @click="confirmDeleteAccount">
            <div class="item-left">
              <span class="item-icon">💀</span>
              <span class="item-label">注销账号</span>
            </div>
            <span class="item-arrow">›</span>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="showSettings = false">关闭</el-button>
        <el-button type="primary" @click="saveSettings">保存设置</el-button>
      </template>
    </el-dialog>

    <!-- 修改密码弹窗 -->
    <el-dialog v-model="showChangePassword" title="修改密码" width="400px">
      <el-form :model="passwordForm" label-width="100px">
        <el-form-item label="当前密码">
          <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入当前密码" show-password />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password />
        </el-form-item>
        <el-form-item label="确认新密码">
          <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showChangePassword = false">取消</el-button>
        <el-button type="primary" @click="changePassword">确认修改</el-button>
      </template>
    </el-dialog>

    <!-- 关于我们弹窗 -->
    <el-dialog v-model="showAbout" title="关于我们" width="400px">
      <div class="about-content">
        <div class="about-logo">🌍</div>
        <h2>智能旅游系统</h2>
        <p class="about-version">版本 1.0.0</p>
        <p class="about-desc">
          智能旅游系统是一款基于 AI 的旅行规划助手，
          帮助您轻松规划完美的旅行行程。
        </p>
        <div class="about-features">
          <div class="feature-item">🗺️ 智能行程规划</div>
          <div class="feature-item">🏨 酒店预订</div>
          <div class="feature-item">🍜 美食推荐</div>
          <div class="feature-item">👥 社交互动</div>
        </div>
        <p class="about-copyright">© 2024 智能旅游系统团队</p>
      </div>
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
import * as messageApi from '@/modules/message/api';

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
const showChangePassword = ref(false);
const showAbout = ref(false);
const profileSaving = ref(false);
const avatarUploading = ref(false);
const previewAvatar = ref('');
const avatarInput = ref<HTMLInputElement | null>(null);
const cacheSize = ref('2.3 MB');

// 社交相关状态
const followStats = ref({ following: 0, followers: 0 });
const showFollowModal = ref(false);
const followModalType = ref<'following' | 'followers'>('following');
const followList = ref<any[]>([]);
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';

// 预设头像列表
const presetAvatars = [
  'https://api.dicebear.com/7.x/adventurer/svg?seed=Felix',
  'https://api.dicebear.com/7.x/adventurer/svg?seed=Aneka',
  'https://api.dicebear.com/7.x/adventurer/svg?seed=Bailey',
  'https://api.dicebear.com/7.x/adventurer/svg?seed=Coco',
  'https://api.dicebear.com/7.x/adventurer/svg?seed=Daisy',
  'https://api.dicebear.com/7.x/adventurer/svg?seed=Eliza',
  'https://api.dicebear.com/7.x/adventurer/svg?seed=Ginger',
  'https://api.dicebear.com/7.x/adventurer/svg?seed=Harley',
];

// 隐私设置
const privacySettings = reactive({
  publicProfile: true,
  showLocation: true,
  publicFavorites: true,
  publicItineraries: false,
});

// 通知设置
const notificationSettings = reactive({
  message: true,
  newFollower: true,
  systemNotice: true,
  promotion: false,
});

// 其他设置
const otherSettings = reactive({
  darkMode: false,
});

// 密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
});

// 地区选项
const locationOptions = [
  {
    value: 'beijing',
    label: '北京',
    children: [{ value: 'beijing', label: '北京市' }]
  },
  {
    value: 'shanghai',
    label: '上海',
    children: [{ value: 'shanghai', label: '上海市' }]
  },
  {
    value: 'sichuan',
    label: '四川',
    children: [
      { value: 'chengdu', label: '成都市' },
      { value: 'bindingmianyang', label: '绵阳市' },
      { value: 'yibin', label: '宜宾市' },
      { value: 'leshan', label: '乐山市' },
    ]
  },
  {
    value: 'zhejiang',
    label: '浙江',
    children: [
      { value: 'hangzhou', label: '杭州市' },
      { value: 'ningbo', label: '宁波市' },
      { value: 'wenzhou', label: '温州市' },
    ]
  },
  {
    value: 'guangdong',
    label: '广东',
    children: [
      { value: 'guangzhou', label: '广州市' },
      { value: 'shenzhen', label: '深圳市' },
      { value: 'zhuhai', label: '珠海市' },
    ]
  },
  {
    value: 'jiangsu',
    label: '江苏',
    children: [
      { value: 'nanjing', label: '南京市' },
      { value: 'suzhou', label: '苏州市' },
      { value: 'wuxi', label: '无锡市' },
    ]
  },
];

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
  gender: '',
  birthday: '',
  location: [] as string[],
  motto: '',
  bio: '',
  phone: '',
  email: '',
  travelStyles: [] as string[],
  preferredTransport: '',
});

// 禁用未来日期
function disabledDate(time: Date) {
  return time.getTime() > Date.now();
}

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
    
    // 加载关注统计
    loadFollowStats();
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
  profileSaving.value = true;
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
      gender: editForm.gender,
      birthday: editForm.birthday,
      location: editForm.location?.join('/'),
      motto: editForm.motto,
      bio: editForm.bio,
      phone: editForm.phone,
      email: editForm.email,
    });
    
    // 更新本地状态
    await authStore.updateProfile({
      nickname: editForm.nickname,
      motto: editForm.motto,
    });
    
    // 保存旅行偏好到本地
    localStorage.setItem('travel_preferences', JSON.stringify({
      travelStyles: editForm.travelStyles,
      preferredTransport: editForm.preferredTransport,
    }));
    
    // 更新座右铭显示
    if (editForm.motto) {
      userMotto.value = editForm.motto;
    }
    
    ElMessage.success('资料已更新');
    showEditProfile.value = false;
  } catch (e) {
    ElMessage.error('更新失败');
  } finally {
    profileSaving.value = false;
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

// ==================== 社交相关方法 ====================

// 加载关注统计
async function loadFollowStats() {
  if (!authStore.user?.id) return;
  try {
    const res = await messageApi.getFollowStats(authStore.user.id);
    followStats.value = res.data || { following: 0, followers: 0 };
  } catch (error) {
    console.error('加载关注统计失败:', error);
  }
}

// 显示关注/粉丝列表
async function showFollowList(type: 'following' | 'followers') {
  if (!authStore.user?.id) return;
  followModalType.value = type;
  showFollowModal.value = true;
  
  try {
    if (type === 'following') {
      const res = await messageApi.getFollowingList(authStore.user.id);
      followList.value = res.data || [];
    } else {
      const res = await messageApi.getFollowersList(authStore.user.id);
      followList.value = res.data || [];
    }
  } catch (error) {
    console.error('加载关注列表失败:', error);
    followList.value = [];
  }
}

// 关闭关注列表弹窗
function closeFollowModal() {
  showFollowModal.value = false;
  followList.value = [];
}

// 跳转到用户主页
function goToUserProfile(userId: number) {
  closeFollowModal();
  router.push(`/user/${userId}`);
}

// 发起私信
function startChat(userId: number) {
  closeFollowModal();
  router.push(`/chat/${userId}`);
}

// ==================== 头像相关方法 ====================

// 选择头像文件
function handleAvatarSelect(event: Event) {
  const input = event.target as HTMLInputElement;
  if (input.files && input.files[0]) {
    const file = input.files[0];
    
    // 检查文件大小
    if (file.size > 2 * 1024 * 1024) {
      ElMessage.error('图片大小不能超过 2MB');
      return;
    }
    
    // 检查文件类型
    if (!file.type.startsWith('image/')) {
      ElMessage.error('请选择图片文件');
      return;
    }
    
    // 预览图片
    const reader = new FileReader();
    reader.onload = (e) => {
      previewAvatar.value = e.target?.result as string;
    };
    reader.readAsDataURL(file);
  }
}

// 选择预设头像
function selectPresetAvatar(avatar: string) {
  previewAvatar.value = avatar;
}

// 取消头像上传
function cancelAvatarUpload() {
  showAvatarUpload.value = false;
  previewAvatar.value = '';
}

// 保存头像
async function saveAvatar() {
  if (!previewAvatar.value) {
    ElMessage.warning('请先选择头像');
    return;
  }
  
  avatarUploading.value = true;
  try {
    // 更新头像到 store
    await authStore.updateProfile({
      avatar: previewAvatar.value
    });
    
    ElMessage.success('头像更新成功');
    showAvatarUpload.value = false;
    previewAvatar.value = '';
  } catch (e) {
    ElMessage.error('头像更新失败');
  } finally {
    avatarUploading.value = false;
  }
}

// ==================== 设置相关方法 ====================

// 保存设置
function saveSettings() {
  // 保存到本地存储
  localStorage.setItem('privacy_settings', JSON.stringify(privacySettings));
  localStorage.setItem('notification_settings', JSON.stringify(notificationSettings));
  localStorage.setItem('other_settings', JSON.stringify(otherSettings));
  
  ElMessage.success('设置已保存');
  showSettings.value = false;
}

// 清除缓存
function clearCache() {
  ElMessageBox.confirm('确定要清除所有缓存数据吗？', '清除缓存', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 清除本地存储（保留登录信息）
    const token = localStorage.getItem('token');
    const user = localStorage.getItem('user');
    localStorage.clear();
    if (token) localStorage.setItem('token', token);
    if (user) localStorage.setItem('user', user);
    
    cacheSize.value = '0 KB';
    ElMessage.success('缓存已清除');
  }).catch(() => {});
}

// 切换深色模式
function toggleDarkMode(value: boolean) {
  if (value) {
    document.documentElement.classList.add('dark');
  } else {
    document.documentElement.classList.remove('dark');
  }
  localStorage.setItem('dark_mode', String(value));
}

// 确认退出登录
function confirmLogout() {
  ElMessageBox.confirm('确定要退出登录吗？', '退出登录', {
    confirmButtonText: '确定退出',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    handleLogout();
  }).catch(() => {});
}

// 确认注销账号
function confirmDeleteAccount() {
  ElMessageBox.confirm(
    '注销账号后，您的所有数据将被永久删除且无法恢复。确定要注销吗？',
    '注销账号',
    {
      confirmButtonText: '确定注销',
      cancelButtonText: '取消',
      type: 'error',
      confirmButtonClass: 'el-button--danger'
    }
  ).then(() => {
    ElMessage.info('账号注销功能暂未开放');
  }).catch(() => {});
}

// 修改密码
async function changePassword() {
  if (!passwordForm.oldPassword) {
    ElMessage.warning('请输入当前密码');
    return;
  }
  if (!passwordForm.newPassword) {
    ElMessage.warning('请输入新密码');
    return;
  }
  if (passwordForm.newPassword.length < 6) {
    ElMessage.warning('新密码长度至少 6 位');
    return;
  }
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.warning('两次输入的密码不一致');
    return;
  }
  
  try {
    // TODO: 调用后端 API 修改密码
    ElMessage.success('密码修改成功，请重新登录');
    showChangePassword.value = false;
    // 清空表单
    passwordForm.oldPassword = '';
    passwordForm.newPassword = '';
    passwordForm.confirmPassword = '';
    // 退出登录
    setTimeout(() => {
      handleLogout();
    }, 1500);
  } catch (e) {
    ElMessage.error('密码修改失败');
  }
}

// 加载设置
function loadSettings() {
  // 从本地存储加载设置
  const savedPrivacy = localStorage.getItem('privacy_settings');
  const savedNotification = localStorage.getItem('notification_settings');
  const savedOther = localStorage.getItem('other_settings');
  
  if (savedPrivacy) {
    Object.assign(privacySettings, JSON.parse(savedPrivacy));
  }
  if (savedNotification) {
    Object.assign(notificationSettings, JSON.parse(savedNotification));
  }
  if (savedOther) {
    Object.assign(otherSettings, JSON.parse(savedOther));
    // 应用深色模式
    if (otherSettings.darkMode) {
      document.documentElement.classList.add('dark');
    }
  }
}

// 初始化时加载设置
loadSettings();
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

/* 统计项可点击样式 */
.stat-item.clickable {
  cursor: pointer;
  transition: all 0.2s;
  padding: 8px 12px;
  border-radius: 8px;
  margin: -8px -12px;
}

.stat-item.clickable:hover {
  background: rgba(102, 126, 234, 0.1);
}

.stat-item.clickable:hover .stat-value {
  color: #667eea;
}

/* 关注列表弹窗 */
.follow-modal {
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

.follow-modal-content {
  background: white;
  border-radius: 20px;
  width: 100%;
  max-width: 400px;
  max-height: 70vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.follow-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.follow-modal-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.close-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #f5f5f5;
  border: none;
  cursor: pointer;
  font-size: 16px;
  color: #666;
  transition: all 0.2s;
}

.close-btn:hover {
  background: #eee;
}

.follow-list {
  flex: 1;
  overflow-y: auto;
  padding: 12px 0;
}

.follow-item {
  display: flex;
  align-items: center;
  padding: 12px 24px;
  transition: background 0.2s;
}

.follow-item:hover {
  background: #fafafa;
}

.follow-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 12px;
  cursor: pointer;
  transition: transform 0.2s;
}

.follow-avatar:hover {
  transform: scale(1.05);
}

.follow-info {
  flex: 1;
  min-width: 0;
  cursor: pointer;
}

.follow-nickname {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  display: block;
}

.follow-motto {
  font-size: 13px;
  color: #999;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.follow-action-btn {
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 13px;
  border: none;
  cursor: pointer;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  transition: all 0.3s;
}

.follow-action-btn:hover {
  transform: scale(1.02);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.empty-follow {
  text-align: center;
  padding: 60px 20px;
}

.empty-follow .empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-follow p {
  font-size: 16px;
  color: #666;
  margin-bottom: 8px;
}

.empty-follow span {
  font-size: 14px;
  color: #999;
}

/* ==================== 头像上传弹窗样式 ==================== */
.avatar-upload-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24px;
}

.current-avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.3);
}

.current-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  font-size: 48px;
  color: white;
  font-weight: 700;
}

.avatar-options {
  width: 100%;
}

.upload-section {
  text-align: center;
  margin-bottom: 24px;
}

.upload-btn {
  padding: 12px 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 25px;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.3s;
}

.upload-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.upload-tip {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
}

.preset-avatars {
  border-top: 1px solid #f0f0f0;
  padding-top: 20px;
}

.preset-title {
  font-size: 14px;
  color: #666;
  text-align: center;
  margin-bottom: 16px;
}

.preset-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.preset-grid img {
  width: 100%;
  aspect-ratio: 1;
  border-radius: 50%;
  cursor: pointer;
  border: 3px solid transparent;
  transition: all 0.3s;
  background: #f5f5f5;
}

.preset-grid img:hover {
  transform: scale(1.1);
}

.preset-grid img.selected {
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

/* ==================== 编辑资料弹窗样式 ==================== */
.edit-form :deep(.el-divider__text) {
  font-size: 14px;
  color: #667eea;
  font-weight: 600;
}

.edit-form :deep(.el-checkbox-group) {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.edit-form :deep(.el-checkbox) {
  margin-right: 0;
}

/* ==================== 设置弹窗样式 ==================== */
.settings-content {
  max-height: 60vh;
  overflow-y: auto;
}

.settings-section {
  margin-bottom: 24px;
}

.settings-section .section-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
  padding-left: 4px;
}

.settings-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  background: #f8f9fa;
  border-radius: 12px;
  margin-bottom: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.settings-item:hover {
  background: #f0f0f0;
}

.item-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.item-icon {
  font-size: 18px;
}

.item-label {
  font-size: 14px;
  color: #333;
}

.item-value {
  font-size: 13px;
  color: #999;
}

.item-arrow {
  font-size: 18px;
  color: #ccc;
}

.danger-section .settings-item.danger {
  background: #fef2f2;
}

.danger-section .settings-item.danger:hover {
  background: #fee2e2;
}

.danger-section .settings-item.danger .item-label {
  color: #dc2626;
}

/* ==================== 关于我们弹窗样式 ==================== */
.about-content {
  text-align: center;
  padding: 20px 0;
}

.about-logo {
  font-size: 64px;
  margin-bottom: 16px;
}

.about-content h2 {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin-bottom: 8px;
}

.about-version {
  font-size: 14px;
  color: #999;
  margin-bottom: 20px;
}

.about-desc {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 24px;
  padding: 0 20px;
}

.about-features {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-bottom: 24px;
}

.feature-item {
  padding: 12px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  border-radius: 12px;
  font-size: 14px;
  color: #667eea;
}

.about-copyright {
  font-size: 12px;
  color: #999;
}

/* ==================== 深色模式样式 ==================== */
:global(.dark) .profile-page {
  background: #1a1a2e;
}

:global(.dark) .user-card,
:global(.dark) .section-card,
:global(.dark) .achievements-card {
  background: #2d2d44;
  color: #e0e0e0;
}

:global(.dark) .user-name,
:global(.dark) .section-title {
  color: #fff;
}

:global(.dark) .settings-item {
  background: #3d3d5c;
}

:global(.dark) .settings-item:hover {
  background: #4d4d6c;
}

:global(.dark) .item-label {
  color: #e0e0e0;
}
</style>
