<template>
  <div class="detail-page" v-if="spot">
    <!-- 返回按钮 -->
    <button class="back-btn" @click="goBack">
      <span>←</span> 返回
    </button>

    <!-- 主要内容区 -->
    <div class="detail-container">
      <!-- 左侧：图片和信息 -->
      <div class="detail-main">
        <div class="spot-hero">
          <!-- 图片轮播 -->
          <div class="image-carousel">
            <div class="carousel-main">
              <img 
                v-if="currentImage" 
                :src="currentImage" 
                :alt="spot.name" 
                class="spot-image"
                @error="handleImageError"
              />
              <div v-else class="spot-placeholder">🏞️</div>
              
              <!-- 轮播控制 -->
              <button 
                v-if="allImages.length > 1" 
                class="carousel-btn prev" 
                @click="prevImage"
              >‹</button>
              <button 
                v-if="allImages.length > 1" 
                class="carousel-btn next" 
                @click="nextImage"
              >›</button>
              
              <!-- 图片计数 -->
              <div class="image-counter" v-if="allImages.length > 1">
                {{ currentImageIndex + 1 }} / {{ allImages.length }}
              </div>
            </div>
            
            <!-- 缩略图 -->
            <div class="carousel-thumbs" v-if="allImages.length > 1">
              <div 
                v-for="(img, idx) in allImages" 
                :key="idx"
                class="thumb-item"
                :class="{ active: idx === currentImageIndex }"
                @click="currentImageIndex = idx"
              >
                <img :src="img" :alt="`图片${idx + 1}`" />
              </div>
            </div>
          </div>
          
          <div class="spot-hero-content">
            <div class="spot-title-row">
              <h1 class="spot-title">{{ spot.name }}</h1>
              <div class="spot-rating-badge" v-if="avgRating > 0">
                <span class="rating-star">⭐</span>
                <span class="rating-value">{{ avgRating.toFixed(1) }}</span>
                <span class="rating-count">({{ commentCount }}条评价)</span>
              </div>
            </div>
            <div class="spot-meta">
              <span class="meta-item">⏱️ {{ spot.playTime }} 分钟</span>
              <span class="meta-item">👥 {{ spot.ageMin }}-{{ spot.ageMax }}岁</span>
              <span class="meta-item" v-if="spot.openTime">🕐 {{ spot.openTime }}</span>
            </div>
          </div>
        </div>

        <!-- 价格和操作 -->
        <div class="action-card">
          <div class="price-section">
            <span class="price-label">门票价格</span>
            <div class="price-range">
              <span class="price-value">¥{{ spot.priceMin }}</span>
              <span class="price-separator">~</span>
              <span class="price-value">¥{{ spot.priceMax }}</span>
            </div>
          </div>
          <div class="action-buttons">
            <button @click="favorite" class="btn-favorite" :class="{ 'is-favorited': isFavorited }">
              <span>{{ isFavorited ? '❤️' : '🤍' }}</span>
              {{ isFavorited ? '已收藏' : '收藏' }}
            </button>
            <button @click="toReserve" class="btn-reserve">
              <span>🎫</span> 立即预订
            </button>
          </div>
        </div>

        <!-- 景点介绍 -->
        <div class="info-card">
          <h2 class="card-title">📖 景点介绍</h2>
          <div class="description" v-html="spot.description"></div>
        </div>

        <!-- 评论区 -->
        <div class="comments-card">
          <div class="comments-header">
            <div class="header-left">
              <h2 class="card-title">💬 游客评论 ({{ comments.length }})</h2>
              <div class="rating-summary" v-if="comments.length > 0">
                <span class="avg-rating">{{ avgRating.toFixed(1) }}</span>
                <span class="avg-star">⭐</span>
              </div>
            </div>
            <!-- 排序选项 -->
            <div class="sort-tabs" v-if="comments.length > 0">
              <span 
                class="sort-tab" 
                :class="{ active: commentSort === 'hot' }"
                @click="commentSort = 'hot'"
              >🔥 最热</span>
              <span 
                class="sort-tab" 
                :class="{ active: commentSort === 'new' }"
                @click="commentSort = 'new'"
              >🕐 最新</span>
            </div>
          </div>
          
          <!-- 发表评论 -->
          <div class="comment-form" :class="{ 'reply-mode': replyingTo }">
            <div class="reply-hint" v-if="replyingTo">
              <span>回复 @{{ replyingTo.userName || '游客' + replyingTo.userId }}</span>
              <button class="cancel-reply" @click="cancelReply">✕ 取消</button>
            </div>
            <div class="rating-input" v-if="!replyingTo">
              <span class="rating-label">评分：</span>
              <div class="stars">
                <span 
                  v-for="i in 5" 
                  :key="i" 
                  class="star" 
                  :class="{ active: i <= newRating }"
                  @click="newRating = i"
                  @mouseenter="hoverRating = i"
                  @mouseleave="hoverRating = 0"
                >
                  {{ i <= (hoverRating || newRating) ? '⭐' : '☆' }}
                </span>
              </div>
            </div>
            <textarea 
              ref="commentInput"
              v-model="newComment" 
              :placeholder="replyingTo ? '写下你的回复...' : '分享您的旅行体验...'"
              class="comment-input"
              rows="3"
            ></textarea>
            <button @click="postComment" class="btn-submit" :disabled="!newComment.trim()">
              {{ replyingTo ? '发表回复' : '发表评论' }}
            </button>
          </div>

          <!-- 评论列表 -->
          <div v-if="comments.length === 0" class="empty-comments">
            <span class="empty-icon">💭</span>
            <p>暂无评论，来做第一个评论的人吧！</p>
          </div>
          <div v-else class="comments-list">
            <div v-for="c in sortedComments" :key="c.id" class="comment-item">
              <div 
                class="comment-avatar clickable" 
                :style="{ background: getAvatarColor(c.userName || c.userId) }"
                @click="goToUserProfile(c.userId)"
                title="查看用户主页"
              >
                {{ getAvatarText(c.userName) }}
              </div>
              <div class="comment-body">
                <div class="comment-header">
                  <div class="comment-user-info">
                    <span class="comment-author clickable" @click="goToUserProfile(c.userId)">{{ c.userName || '游客' + c.userId }}</span>
                    <span class="comment-rating" v-if="c.rating">
                      <span v-for="i in 5" :key="i" class="mini-star">
                        {{ i <= c.rating ? '⭐' : '☆' }}
                      </span>
                    </span>
                  </div>
                  <span class="comment-time">{{ formatTime(c.createdAt) }}</span>
                </div>
                <p class="comment-content">{{ c.content }}</p>
                
                <!-- 评论操作 -->
                <div class="comment-actions">
                  <button 
                    class="action-btn like-btn" 
                    :class="{ liked: c.isLiked }"
                    @click="likeComment(c)"
                  >
                    {{ c.isLiked ? '👍' : '👍🏻' }} {{ c.likes || 0 }}
                  </button>
                  <button class="action-btn reply-btn" @click="replyTo(c)">
                    💬 回复
                  </button>
                </div>
                
                <!-- 回复列表 -->
                <div v-if="c.replies && c.replies.length > 0" class="replies-section">
                  <button 
                    class="toggle-replies-btn" 
                    @click="toggleReplies(c.id)"
                  >
                    <span class="toggle-icon">{{ expandedReplies.has(c.id) ? '▼' : '▶' }}</span>
                    {{ expandedReplies.has(c.id) ? '收起' : '展开' }} {{ c.replies.length }} 条回复
                  </button>
                  <div v-show="expandedReplies.has(c.id)" class="replies-list">
                    <div v-for="reply in c.replies" :key="reply.id" class="reply-item">
                      <div 
                        class="reply-avatar clickable" 
                        :style="{ background: getAvatarColor(reply.userName || reply.userId) }"
                        @click="goToUserProfile(reply.userId)"
                        title="查看用户主页"
                      >
                        {{ getAvatarText(reply.userName) }}
                      </div>
                      <div class="reply-body">
                        <div class="reply-header">
                          <span class="reply-author clickable" @click="goToUserProfile(reply.userId)">{{ reply.userName || '游客' + reply.userId }}</span>
                          <span class="reply-time">{{ formatTime(reply.createdAt) }}</span>
                        </div>
                        <p class="reply-content">
                          <span class="reply-to" v-if="reply.replyToUserId || reply.replyToUserName">@{{ reply.replyToUserName || '游客' + reply.replyToUserId }} </span>
                          {{ reply.content }}
                        </p>
                        <div class="reply-actions">
                          <button 
                            class="action-btn like-btn small" 
                            :class="{ liked: reply.isLiked }"
                            @click="likeReply(reply)"
                          >
                            {{ reply.isLiked ? '👍' : '👍🏻' }} {{ reply.likes || 0 }}
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：快捷信息 -->
      <div class="detail-sidebar">
        <div class="sidebar-card">
          <h3 class="sidebar-title">📍 快捷信息</h3>
          <div class="info-list">
            <div class="info-item">
              <span class="info-icon">⏰</span>
              <div class="info-content">
                <span class="info-label">游玩时长</span>
                <span class="info-value">{{ spot.playTime }} 分钟</span>
              </div>
            </div>
            <div class="info-item">
              <span class="info-icon">👨‍👩‍👧‍👦</span>
              <div class="info-content">
                <span class="info-label">适合年龄</span>
                <span class="info-value">{{ spot.ageMin }}-{{ spot.ageMax }}岁</span>
              </div>
            </div>
            <div class="info-item">
              <span class="info-icon">💰</span>
              <div class="info-content">
                <span class="info-label">票价范围</span>
                <span class="info-value">¥{{ spot.priceMin }}-{{ spot.priceMax }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="sidebar-card tips-card">
          <h3 class="sidebar-title">💡 温馨提示</h3>
          <ul class="tips-list">
            <li>建议提前预订门票</li>
            <li>请携带有效身份证件</li>
            <li>注意天气变化，做好防护</li>
          </ul>
        </div>
      </div>
    </div>
  </div>

  <!-- 加载状态 -->
  <div v-else class="loading-container">
    <div class="loading-card">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getSpot } from '@/api/spots';
import { listComments, addComment, toggleFavorite, replyComment } from '@/api/social';
import { useAuthStore } from '@/modules/auth/store';
import { ElMessage } from 'element-plus';

// 组件名称
defineOptions({
  name: 'SpotDetail'
});

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const id = Number(route.params.id);
const spot = ref<any | null>(null);
const comments = ref<any[]>([]);
const newComment = ref('');
const newRating = ref(5);
const hoverRating = ref(0);
const isFavorited = ref(false);
const commentInput = ref<HTMLTextAreaElement | null>(null);

// 评论排序和回复
const commentSort = ref<'hot' | 'new'>('hot');
const replyingTo = ref<any>(null);
const expandedReplies = ref<Set<number>>(new Set()); // 展开的回复列表

// 图片轮播
const currentImageIndex = ref(0);

// 所有图片（主图 + 额外图片）
const allImages = computed(() => {
  if (!spot.value) return [];
  const images: string[] = [];
  if (spot.value.imageUrl) images.push(spot.value.imageUrl);
  if (spot.value.images && Array.isArray(spot.value.images)) {
    images.push(...spot.value.images);
  }
  return images;
});

// 当前显示的图片
const currentImage = computed(() => {
  return allImages.value[currentImageIndex.value] || spot.value?.imageUrl;
});

// 平均评分（优先使用后端计算的评分）
const avgRating = computed(() => {
  // 如果后端返回了评分，直接使用
  if (spot.value?.rating && spot.value.rating > 0) {
    return spot.value.rating;
  }
  // 否则从评论列表计算
  if (comments.value.length === 0) return 0;
  const sum = comments.value.reduce((acc, c) => acc + (c.rating || 5), 0);
  return sum / comments.value.length;
});

// 评论数量（优先使用后端返回的数量）
const commentCount = computed(() => {
  if (spot.value?.commentCount !== undefined && spot.value.commentCount !== null) {
    return spot.value.commentCount;
  }
  return comments.value.length;
});

// 排序后的评论
const sortedComments = computed(() => {
  const list = [...comments.value];
  if (commentSort.value === 'hot') {
    // 按点赞数排序
    return list.sort((a, b) => (b.likes || 0) - (a.likes || 0));
  } else {
    // 按时间排序（最新）
    return list.sort((a, b) => {
      const timeA = new Date(a.createdAt || 0).getTime();
      const timeB = new Date(b.createdAt || 0).getTime();
      return timeB - timeA;
    });
  }
});

// 切换图片
function prevImage() {
  if (currentImageIndex.value > 0) {
    currentImageIndex.value--;
  } else {
    currentImageIndex.value = allImages.value.length - 1;
  }
}

function nextImage() {
  if (currentImageIndex.value < allImages.value.length - 1) {
    currentImageIndex.value++;
  } else {
    currentImageIndex.value = 0;
  }
}

// 图片加载错误处理
function handleImageError(e: Event) {
  const img = e.target as HTMLImageElement;
  img.style.display = 'none';
}

// 返回上一页
function goBack() {
  router.back();
}

// 头像颜色
function getAvatarColor(nameOrId: string | number | undefined): string {
  const colors = [
    'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
    'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
  ];
  let index = 0;
  if (typeof nameOrId === 'string' && nameOrId) {
    index = nameOrId.charCodeAt(0);
  } else if (typeof nameOrId === 'number') {
    index = nameOrId;
  }
  return colors[index % colors.length];
}

// 头像文字
function getAvatarText(name: string | number | undefined): string {
  if (typeof name === 'string' && name) {
    return name.charAt(0);
  }
  return '游';
}

// 回复评论
function replyTo(comment: any) {
  replyingTo.value = comment;
  newComment.value = '';
  // 滚动到输入框并聚焦
  setTimeout(() => {
    commentInput.value?.focus();
    commentInput.value?.scrollIntoView({ behavior: 'smooth', block: 'center' });
  }, 100);
}

// 取消回复
function cancelReply() {
  replyingTo.value = null;
  newComment.value = '';
}

// 切换回复列表展开/折叠
function toggleReplies(commentId: number) {
  if (expandedReplies.value.has(commentId)) {
    expandedReplies.value.delete(commentId);
  } else {
    expandedReplies.value.add(commentId);
  }
  // 触发响应式更新
  expandedReplies.value = new Set(expandedReplies.value);
}

// 点赞评论
function likeComment(comment: any) {
  if (!authStore.isLoggedIn) {
    ElMessage.warning('请先登录');
    return;
  }
  
  // 切换点赞状态（前端模拟）
  if (comment.isLiked) {
    comment.likes = (comment.likes || 1) - 1;
    comment.isLiked = false;
  } else {
    comment.likes = (comment.likes || 0) + 1;
    comment.isLiked = true;
  }
  
  // 保存到本地存储
  saveLikeStatus(comment.id, comment.isLiked);
}

// 点赞回复
function likeReply(reply: any) {
  if (!authStore.isLoggedIn) {
    ElMessage.warning('请先登录');
    return;
  }
  
  if (reply.isLiked) {
    reply.likes = (reply.likes || 1) - 1;
    reply.isLiked = false;
  } else {
    reply.likes = (reply.likes || 0) + 1;
    reply.isLiked = true;
  }
  
  saveLikeStatus(`reply_${reply.id}`, reply.isLiked);
}

// 保存点赞状态到本地存储（按用户区分）
function saveLikeStatus(id: string | number, isLiked: boolean) {
  const userId = authStore.user?.id;
  if (!userId) return;
  
  const key = `comment_likes_${userId}_${id}`;
  if (isLiked) {
    localStorage.setItem(key, 'true');
  } else {
    localStorage.removeItem(key);
  }
}

// 加载点赞状态（按用户区分）
function loadLikeStatus() {
  const userId = authStore.user?.id;
  if (!userId) return;
  
  comments.value.forEach(c => {
    c.isLiked = localStorage.getItem(`comment_likes_${userId}_${c.id}`) === 'true';
    if (c.replies) {
      c.replies.forEach((r: any) => {
        r.isLiked = localStorage.getItem(`comment_likes_${userId}_reply_${r.id}`) === 'true';
      });
    }
  });
}

async function load() {
  const res = await getSpot(id);
  spot.value = res.data;
  const c = await listComments(id);
  comments.value = c.data || [];
  // 加载点赞状态
  loadLikeStatus();
}

async function favorite() {
  if (!authStore.isLoggedIn) {
    ElMessage.warning('请先登录');
    router.push('/login');
    return;
  }
  const userId = authStore.user?.id;
  if (!userId) return;
  
  await toggleFavorite(userId, id);
  isFavorited.value = !isFavorited.value;
  ElMessage.success(isFavorited.value ? '已收藏' : '已取消收藏');
}

function toReserve() {
  router.push({ path: '/reservation', query: { spotId: id } });
}

async function postComment() {
  if (!newComment.value.trim()) return;
  if (!authStore.isLoggedIn) {
    ElMessage.warning('请先登录');
    router.push('/login');
    return;
  }
  const userId = authStore.user?.id;
  if (!userId) return;
  
  const userName = authStore.user?.nickname || authStore.user?.username || '游客';
  
  if (replyingTo.value) {
    // 发表回复 - 调用后端 API
    try {
      await replyComment(replyingTo.value.id, userId, id, newComment.value, userName);
      
      // 前端立即显示回复
      const parentComment = comments.value.find(c => c.id === replyingTo.value.id);
      if (parentComment) {
        if (!parentComment.replies) parentComment.replies = [];
        parentComment.replies.push({
          id: Date.now(),
          userId: userId,
          userName: userName,
          content: newComment.value,
          createdAt: new Date().toISOString(),
          replyToUserId: replyingTo.value.userId,
          replyToUserName: replyingTo.value.userName || '游客' + replyingTo.value.userId,
          likes: 0
        });
        // 自动展开回复列表
        expandedReplies.value.add(replyingTo.value.id);
        expandedReplies.value = new Set(expandedReplies.value);
      }
      ElMessage.success('回复成功');
      cancelReply();
    } catch (e) {
      console.error('回复失败', e);
      ElMessage.error('回复失败');
    }
  } else {
    // 发表新评论
    await addComment(userId, id, newComment.value, newRating.value, userName);
    newComment.value = '';
    newRating.value = 5;
    ElMessage.success('评论成功');
    await load();
  }
}

function formatTime(time: string) {
  if (!time) return '刚刚';
  const d = new Date(time);
  const now = new Date();
  const diff = now.getTime() - d.getTime();
  
  if (diff < 60000) return '刚刚';
  if (diff < 3600000) return `${Math.floor(diff / 60000)} 分钟前`;
  if (diff < 86400000) return `${Math.floor(diff / 3600000)} 小时前`;
  if (diff < 604800000) return `${Math.floor(diff / 86400000)} 天前`;
  
  return d.toLocaleDateString('zh-CN');
}

// 跳转到用户主页
function goToUserProfile(userId: number) {
  if (userId) {
    router.push(`/user/${userId}`);
  }
}

onMounted(load);
</script>

<style scoped>
.detail-page {
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

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  color: #667eea;
  font-weight: 500;
  margin-bottom: 24px;
  transition: all 0.3s;
}

.back-btn:hover {
  background: white;
  transform: translateX(-4px);
}

.detail-container {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 32px;
}

/* 主要内容区 */
.detail-main {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.spot-hero {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
}

/* 图片轮播 */
.image-carousel {
  position: relative;
}

.carousel-main {
  position: relative;
  height: 400px;
  overflow: hidden;
}

.spot-image {
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
  font-size: 80px;
}

.carousel-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 48px;
  height: 48px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  font-size: 24px;
  color: #333;
  cursor: pointer;
  transition: all 0.3s;
  z-index: 10;
}

.carousel-btn:hover {
  background: white;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.carousel-btn.prev {
  left: 16px;
}

.carousel-btn.next {
  right: 16px;
}

.image-counter {
  position: absolute;
  bottom: 16px;
  right: 16px;
  padding: 6px 14px;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  border-radius: 20px;
  font-size: 13px;
}

.carousel-thumbs {
  display: flex;
  gap: 8px;
  padding: 12px;
  background: #f8f9fa;
  overflow-x: auto;
}

.thumb-item {
  width: 80px;
  height: 60px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  opacity: 0.6;
  transition: all 0.3s;
  flex-shrink: 0;
  border: 2px solid transparent;
}

.thumb-item:hover,
.thumb-item.active {
  opacity: 1;
  border-color: #667eea;
}

.thumb-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.spot-hero-content {
  padding: 28px;
}

.spot-title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.spot-title {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a2e;
}

.spot-rating-badge {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 16px;
  background: #fff8e1;
  border-radius: 20px;
}

.spot-rating-badge .rating-star {
  font-size: 18px;
}

.spot-rating-badge .rating-value {
  font-size: 20px;
  font-weight: 700;
  color: #f5a623;
}

.spot-rating-badge .rating-count {
  font-size: 12px;
  color: #999;
  margin-left: 4px;
}

.spot-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.meta-item {
  font-size: 15px;
  color: #666;
}

/* 操作卡片 */
.action-card {
  background: white;
  border-radius: 20px;
  padding: 28px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
}

.price-label {
  font-size: 14px;
  color: #888;
  display: block;
  margin-bottom: 8px;
}

.price-range {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.price-value {
  font-size: 28px;
  font-weight: 700;
  color: #667eea;
}

.price-separator {
  color: #ccc;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

.btn-favorite {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 24px;
  background: #f5f5f5;
  color: #666;
  border-radius: 12px;
  font-weight: 600;
  transition: all 0.3s;
}

.btn-favorite:hover {
  background: #eee;
}

.btn-favorite.is-favorited {
  background: #fef2f2;
  color: #ef4444;
}

.btn-reserve {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px;
  font-weight: 600;
  transition: all 0.3s;
}

.btn-reserve:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

/* 信息卡片 */
.info-card, .comments-card {
  background: white;
  border-radius: 20px;
  padding: 28px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
}

.card-title {
  font-size: 20px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 20px;
}

.description {
  font-size: 15px;
  line-height: 1.8;
  color: #555;
}

/* 评论区 */
.comments-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 16px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.comments-header .card-title {
  margin-bottom: 0;
}

.rating-summary {
  display: flex;
  align-items: center;
  gap: 4px;
}

.avg-rating {
  font-size: 24px;
  font-weight: 700;
  color: #f5a623;
}

.avg-star {
  font-size: 18px;
}

/* 排序选项 */
.sort-tabs {
  display: flex;
  gap: 8px;
}

.sort-tab {
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
  background: #f5f5f5;
}

.sort-tab:hover {
  background: #eee;
}

.sort-tab.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.comment-form {
  margin-bottom: 28px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
  transition: all 0.3s;
}

.comment-form.reply-mode {
  background: #e8f4fd;
  border: 2px solid #667eea;
}

.reply-hint {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(102, 126, 234, 0.2);
}

.reply-hint span {
  color: #667eea;
  font-weight: 500;
}

.cancel-reply {
  padding: 4px 12px;
  background: transparent;
  color: #999;
  font-size: 13px;
  cursor: pointer;
  border-radius: 4px;
}

.cancel-reply:hover {
  background: rgba(0, 0, 0, 0.05);
  color: #666;
}

.rating-input {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.rating-label {
  font-size: 14px;
  color: #666;
}

.stars {
  display: flex;
  gap: 4px;
}

.star {
  font-size: 24px;
  cursor: pointer;
  transition: transform 0.2s;
}

.star:hover {
  transform: scale(1.2);
}

.comment-input {
  resize: none;
  margin-bottom: 12px;
  width: 100%;
  padding: 16px;
  border: 1px solid #e0e0e0;
  border-radius: 10px;
  font-size: 15px;
  transition: border-color 0.3s;
}

.comment-input:focus {
  outline: none;
  border-color: #667eea;
}

.btn-submit {
  padding: 12px 28px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 10px;
  font-weight: 600;
  transition: all 0.3s;
}

.btn-submit:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.btn-submit:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.empty-comments {
  text-align: center;
  padding: 40px;
  color: #999;
}

.empty-icon {
  font-size: 48px;
  display: block;
  margin-bottom: 12px;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comment-item {
  display: flex;
  gap: 16px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
}

.comment-avatar {
  width: 44px;
  height: 44px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.comment-avatar.clickable,
.reply-avatar.clickable {
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.comment-avatar.clickable:hover,
.reply-avatar.clickable:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.comment-author.clickable,
.reply-author.clickable {
  cursor: pointer;
  transition: color 0.2s;
}

.comment-author.clickable:hover,
.reply-author.clickable:hover {
  color: #667eea;
}

.comment-body {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.comment-user-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.comment-author {
  font-weight: 600;
  color: #1a1a2e;
}

.comment-rating {
  display: flex;
  gap: 2px;
}

.mini-star {
  font-size: 12px;
}

.comment-time {
  font-size: 13px;
  color: #999;
}

.comment-content {
  font-size: 14px;
  color: #555;
  line-height: 1.6;
  margin-bottom: 12px;
}

/* 评论操作 */
.comment-actions {
  display: flex;
  gap: 16px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  background: transparent;
  border-radius: 6px;
  font-size: 13px;
  color: #888;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn:hover {
  background: rgba(0, 0, 0, 0.05);
  color: #666;
}

.action-btn.like-btn.liked {
  color: #667eea;
}

.action-btn.small {
  padding: 4px 8px;
  font-size: 12px;
}

/* 回复列表 */
.replies-section {
  margin-top: 12px;
}

.toggle-replies-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  background: #f0f2f5;
  border: none;
  border-radius: 8px;
  font-size: 13px;
  color: #667eea;
  cursor: pointer;
  transition: all 0.2s;
}

.toggle-replies-btn:hover {
  background: #e8eaf0;
}

.toggle-icon {
  font-size: 10px;
  transition: transform 0.2s;
}

.replies-list {
  margin-top: 12px;
  padding-left: 16px;
  border-left: 2px solid #e0e0e0;
  animation: slideDown 0.2s ease;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.reply-item {
  display: flex;
  gap: 12px;
  padding: 12px 0;
}

.reply-item:not(:last-child) {
  border-bottom: 1px solid #eee;
}

.reply-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: white;
  flex-shrink: 0;
}

.reply-body {
  flex: 1;
}

.reply-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.reply-author {
  font-size: 13px;
  font-weight: 600;
  color: #1a1a2e;
}

.reply-time {
  font-size: 12px;
  color: #999;
}

.reply-content {
  font-size: 13px;
  color: #555;
  line-height: 1.5;
}

.reply-to {
  color: #667eea;
  font-weight: 500;
}

.reply-actions {
  margin-top: 8px;
}

/* 侧边栏 */
.detail-sidebar {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.sidebar-card {
  background: white;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
}

.sidebar-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 20px;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 14px;
}

.info-icon {
  width: 44px;
  height: 44px;
  background: rgba(102, 126, 234, 0.1);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.info-content {
  display: flex;
  flex-direction: column;
}

.info-label {
  font-size: 13px;
  color: #888;
}

.info-value {
  font-size: 15px;
  font-weight: 600;
  color: #1a1a2e;
}

.tips-list {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.tips-list li {
  font-size: 14px;
  color: #666;
  padding-left: 20px;
  position: relative;
}

.tips-list li::before {
  content: '•';
  position: absolute;
  left: 0;
  color: #667eea;
}

/* 加载状态 */
.loading-container {
  display: flex;
  justify-content: center;
  padding: 80px 20px;
}

.loading-card {
  background: white;
  border-radius: 20px;
  padding: 60px 80px;
  text-align: center;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
}

.loading-spinner {
  width: 48px;
  height: 48px;
  border: 4px solid #e0e0e0;
  border-top-color: #667eea;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

@media (max-width: 1024px) {
  .detail-container {
    grid-template-columns: 1fr;
  }
  
  .detail-sidebar {
    flex-direction: row;
    flex-wrap: wrap;
  }
  
  .sidebar-card {
    flex: 1;
    min-width: 280px;
  }
}

@media (max-width: 640px) {
  .action-card {
    flex-direction: column;
    gap: 20px;
    align-items: stretch;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .btn-favorite, .btn-reserve {
    justify-content: center;
  }
}
</style>
