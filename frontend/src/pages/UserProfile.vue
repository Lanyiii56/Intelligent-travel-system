<template>
  <div class="user-profile-page">
    <!-- 加载状态 -->
    <div class="loading-state" v-if="loading">
      <div class="loading-spinner"></div>
      <span>加载中...</span>
    </div>

    <!-- 用户不存在 -->
    <div class="not-found" v-else-if="!user">
      <div class="not-found-icon">😕</div>
      <h2>用户不存在</h2>
      <p>该用户可能已注销或不存在</p>
      <button class="back-btn" @click="goBack">返回</button>
    </div>

    <!-- 用户主页内容 -->
    <div class="profile-content" v-else>
      <!-- 用户信息卡片 -->
      <div class="user-card">
        <div class="user-header">
          <div class="avatar-section">
            <img :src="user.avatarUrl || defaultAvatar" class="user-avatar" />
            <div class="online-status" :class="{ online: isOnline }"></div>
          </div>
          <div class="user-info">
            <h1 class="nickname">{{ user.nickname || user.username }}</h1>
            <p class="username">@{{ user.username }}</p>
            <p class="motto" v-if="user.motto">{{ user.motto }}</p>
            <div class="user-meta">
              <span v-if="user.gender" class="meta-item">
                {{ user.gender === 'male' ? '👨' : user.gender === 'female' ? '👩' : '🧑' }}
                {{ user.gender === 'male' ? '男' : user.gender === 'female' ? '女' : '保密' }}
              </span>
              <span v-if="user.age" class="meta-item">🎂 {{ user.age }}岁</span>
              <span class="meta-item">📅 {{ formatDate(user.createTime) }} 加入</span>
            </div>
          </div>
        </div>

        <!-- 关注统计 -->
        <div class="follow-stats">
          <div class="stat-item" @click="showFollowList('following')">
            <span class="stat-value">{{ followStats.following || 0 }}</span>
            <span class="stat-label">关注</span>
          </div>
          <div class="stat-item" @click="showFollowList('followers')">
            <span class="stat-value">{{ followStats.followers || 0 }}</span>
            <span class="stat-label">粉丝</span>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="action-buttons" v-if="!isCurrentUser">
          <button 
            class="follow-btn" 
            :class="{ following: isFollowing }"
            @click="handleFollow"
            :disabled="followLoading"
          >
            {{ isFollowing ? '已关注' : '+ 关注' }}
          </button>
          <button class="message-btn" @click="openChat">
            💬 私信
          </button>
        </div>
        <div class="action-buttons" v-else>
          <button class="edit-btn" @click="goToProfile">
            ✏️ 编辑资料
          </button>
        </div>
      </div>

      <!-- 用户动态/内容区域 -->
      <div class="user-content">
        <div class="content-tabs">
          <button 
            class="tab-btn" 
            :class="{ active: activeTab === 'posts' }"
            @click="activeTab = 'posts'"
          >
            动态
          </button>
          <button 
            class="tab-btn" 
            :class="{ active: activeTab === 'favorites' }"
            @click="activeTab = 'favorites'"
          >
            收藏
          </button>
        </div>

        <div class="content-area">
          <div class="empty-content">
            <div class="empty-icon">📝</div>
            <p>暂无{{ activeTab === 'posts' ? '动态' : '收藏' }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 关注列表弹窗 -->
    <div class="follow-modal" v-if="showFollowModal" @click.self="closeFollowModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ followModalType === 'following' ? '关注列表' : '粉丝列表' }}</h3>
          <button class="close-btn" @click="closeFollowModal">✕</button>
        </div>
        <div class="follow-list" v-if="followList.length > 0">
          <div 
            class="follow-item" 
            v-for="item in followList" 
            :key="item.userId"
            @click="goToUser(item.userId)"
          >
            <img :src="item.avatar || defaultAvatar" class="follow-avatar" />
            <div class="follow-info">
              <span class="follow-nickname">{{ item.nickname }}</span>
              <span class="follow-motto" v-if="item.motto">{{ item.motto }}</span>
            </div>
            <button 
              v-if="followModalType === 'followers' && !isCurrentUserInList(item.userId)"
              class="follow-back-btn"
              :class="{ following: item.isFollowing }"
              @click.stop="handleFollowBack(item)"
            >
              {{ item.isFollowing ? '已关注' : '回关' }}
            </button>
          </div>
        </div>
        <div class="empty-follow" v-else>
          <p>暂无{{ followModalType === 'following' ? '关注' : '粉丝' }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '@/modules/auth/store';
import * as messageApi from '@/modules/message/api';
import api from '@/api/request';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

// 状态
const loading = ref(true);
const user = ref<any>(null);
const isFollowing = ref(false);
const followLoading = ref(false);
const followStats = ref({ following: 0, followers: 0 });
const activeTab = ref('posts');
const showFollowModal = ref(false);
const followModalType = ref<'following' | 'followers'>('following');
const followList = ref<any[]>([]);

// 默认头像
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';

// 计算属性
const targetUserId = computed(() => Number(route.params.userId));
const currentUserId = computed(() => authStore.user?.id);
const isCurrentUser = computed(() => currentUserId.value === targetUserId.value);
const isOnline = computed(() => Math.random() > 0.5);

// 监听路由变化
watch(() => route.params.userId, () => {
  loadUserData();
});

// 生命周期
onMounted(() => {
  loadUserData();
});

// 方法
async function loadUserData() {
  loading.value = true;
  try {
    await Promise.all([
      loadUserInfo(),
      loadFollowStatus(),
      loadFollowStats()
    ]);
  } finally {
    loading.value = false;
  }
}

async function loadUserInfo() {
  try {
    const res = await api.get(`/auth/user/${targetUserId.value}`);
    user.value = res.data;
  } catch (error) {
    console.error('加载用户信息失败:', error);
    user.value = null;
  }
}

async function loadFollowStatus() {
  if (!currentUserId.value || isCurrentUser.value) return;
  try {
    const res = await messageApi.checkFollow(currentUserId.value, targetUserId.value);
    isFollowing.value = res.data;
  } catch (error) {
    console.error('检查关注状态失败:', error);
  }
}

async function loadFollowStats() {
  try {
    const res = await messageApi.getFollowStats(targetUserId.value);
    followStats.value = res.data;
  } catch (error) {
    console.error('加载关注统计失败:', error);
  }
}

async function handleFollow() {
  if (!currentUserId.value) {
    router.push('/login');
    return;
  }
  
  followLoading.value = true;
  try {
    const res = await messageApi.toggleFollow(currentUserId.value, targetUserId.value);
    isFollowing.value = res.data.isFollowing;
    // 更新粉丝数
    if (isFollowing.value) {
      followStats.value.followers++;
    } else {
      followStats.value.followers--;
    }
  } catch (error) {
    console.error('关注操作失败:', error);
  } finally {
    followLoading.value = false;
  }
}

function openChat() {
  if (!currentUserId.value) {
    router.push('/login');
    return;
  }
  router.push(`/chat/${targetUserId.value}`);
}

function goToProfile() {
  router.push('/profile');
}

function goBack() {
  router.back();
}

async function showFollowList(type: 'following' | 'followers') {
  followModalType.value = type;
  showFollowModal.value = true;
  
  try {
    if (type === 'following') {
      const res = await messageApi.getFollowingList(targetUserId.value);
      followList.value = res.data || [];
    } else {
      const res = await messageApi.getFollowersList(targetUserId.value);
      followList.value = res.data || [];
    }
  } catch (error) {
    console.error('加载关注列表失败:', error);
    followList.value = [];
  }
}

function closeFollowModal() {
  showFollowModal.value = false;
  followList.value = [];
}

function goToUser(userId: number) {
  closeFollowModal();
  router.push(`/user/${userId}`);
}

function isCurrentUserInList(userId: number): boolean {
  return userId === currentUserId.value;
}

async function handleFollowBack(item: any) {
  if (!currentUserId.value) return;
  
  try {
    const res = await messageApi.toggleFollow(currentUserId.value, item.userId);
    item.isFollowing = res.data.isFollowing;
  } catch (error) {
    console.error('关注操作失败:', error);
  }
}

function formatDate(dateStr: string): string {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return `${date.getFullYear()}年${date.getMonth() + 1}月`;
}
</script>

<style scoped>
.user-profile-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

/* 加载状态 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 20px;
  color: #999;
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

/* 用户不存在 */
.not-found {
  text-align: center;
  padding: 100px 20px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
}

.not-found-icon {
  font-size: 64px;
  margin-bottom: 20px;
}

.not-found h2 {
  font-size: 24px;
  color: #333;
  margin-bottom: 12px;
}

.not-found p {
  color: #999;
  margin-bottom: 24px;
}

.back-btn {
  padding: 12px 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 25px;
  font-size: 16px;
  cursor: pointer;
}

/* 用户卡片 */
.user-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 32px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.user-header {
  display: flex;
  gap: 24px;
  margin-bottom: 24px;
}

.avatar-section {
  position: relative;
  flex-shrink: 0;
}

.user-avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid white;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.online-status {
  position: absolute;
  bottom: 8px;
  right: 8px;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: #ccc;
  border: 3px solid white;
}

.online-status.online {
  background: #4cd964;
}

.user-info {
  flex: 1;
}

.nickname {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin: 0 0 4px 0;
}

.username {
  font-size: 14px;
  color: #999;
  margin: 0 0 12px 0;
}

.motto {
  font-size: 15px;
  color: #666;
  margin: 0 0 12px 0;
  line-height: 1.5;
}

.user-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.meta-item {
  font-size: 13px;
  color: #999;
}

/* 关注统计 */
.follow-stats {
  display: flex;
  gap: 40px;
  padding: 20px 0;
  border-top: 1px solid #f0f0f0;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 24px;
}

.stat-item {
  cursor: pointer;
  transition: opacity 0.2s;
}

.stat-item:hover {
  opacity: 0.7;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  display: block;
}

.stat-label {
  font-size: 14px;
  color: #999;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 16px;
}

.follow-btn {
  flex: 1;
  padding: 14px 24px;
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a5a 100%);
  color: white;
  border: none;
  border-radius: 25px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.follow-btn.following {
  background: #f5f5f5;
  color: #666;
}

.follow-btn:hover:not(.following) {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(255, 107, 107, 0.4);
}

.message-btn {
  flex: 1;
  padding: 14px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 25px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.message-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.edit-btn {
  flex: 1;
  padding: 14px 24px;
  background: #f5f5f5;
  color: #666;
  border: none;
  border-radius: 25px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.edit-btn:hover {
  background: #eee;
}

/* 用户内容区域 */
.user-content {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.content-tabs {
  display: flex;
  border-bottom: 1px solid #f0f0f0;
}

.tab-btn {
  flex: 1;
  padding: 16px;
  background: none;
  border: none;
  font-size: 16px;
  font-weight: 500;
  color: #999;
  cursor: pointer;
  position: relative;
  transition: color 0.3s;
}

.tab-btn.active {
  color: #667eea;
}

.tab-btn.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 40px;
  height: 3px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 3px;
}

.content-area {
  padding: 40px 20px;
}

.empty-content {
  text-align: center;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-content p {
  color: #999;
  font-size: 14px;
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

.modal-content {
  background: white;
  border-radius: 20px;
  width: 100%;
  max-width: 400px;
  max-height: 70vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.modal-header h3 {
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
  cursor: pointer;
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
}

.follow-info {
  flex: 1;
  min-width: 0;
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

.follow-back-btn {
  padding: 6px 16px;
  border-radius: 15px;
  font-size: 13px;
  border: none;
  cursor: pointer;
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a5a 100%);
  color: white;
}

.follow-back-btn.following {
  background: #f5f5f5;
  color: #666;
}

.empty-follow {
  text-align: center;
  padding: 60px 20px;
  color: #999;
}

/* 响应式 */
@media (max-width: 600px) {
  .user-header {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .user-meta {
    justify-content: center;
  }
  
  .follow-stats {
    justify-content: center;
  }
}
</style>
