<template>
  <div class="food-detail-page">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
    </div>
    
    <!-- 美食详情 -->
    <template v-else-if="food">
      <!-- 头部图片 -->
      <div class="food-header">
        <div class="food-image">
          <img v-if="food.imageUrl" :src="food.imageUrl" :alt="food.name" />
          <div v-else class="image-placeholder">🍜</div>
        </div>
        <div class="food-overlay">
          <button class="btn-back" @click="goBack">← 返回</button>
        </div>
      </div>
      
      <!-- 基本信息 -->
      <div class="food-info-card">
        <div class="food-title-row">
          <h1 class="food-name">{{ food.name }}</h1>
          <div class="food-rating">
            <span class="rating-star">⭐</span>
            <span class="rating-value">{{ food.rating || 4.5 }}</span>
          </div>
        </div>
        
        <div class="food-tags">
          <span class="tag category">{{ food.category || '特色美食' }}</span>
          <span v-for="tag in foodTags" :key="tag" class="tag">{{ tag }}</span>
        </div>
        
        <p class="food-description">{{ food.description || '这是一道地道的本地美食，值得品尝！' }}</p>
        
        <div class="food-meta">
          <div class="meta-item">
            <span class="meta-icon">💰</span>
            <span class="meta-label">人均消费</span>
            <span class="meta-value">¥{{ food.priceMin || 30 }} - ¥{{ food.priceMax || 80 }}</span>
          </div>
          <div class="meta-item">
            <span class="meta-icon">⏱️</span>
            <span class="meta-label">用餐时间</span>
            <span class="meta-value">约 {{ food.diningTime || 60 }} 分钟</span>
          </div>
          <div class="meta-item">
            <span class="meta-icon">🕐</span>
            <span class="meta-label">营业时间</span>
            <span class="meta-value">{{ food.openTime || '10:00 - 22:00' }}</span>
          </div>
          <div class="meta-item">
            <span class="meta-icon">📍</span>
            <span class="meta-label">地址</span>
            <span class="meta-value">{{ food.address || '暂无地址信息' }}</span>
          </div>
        </div>
        
        <div class="food-actions">
          <button class="btn-favorite" :class="{ active: isFavorite }" @click="toggleFavorite">
            {{ isFavorite ? '❤️ 已收藏' : '🤍 收藏' }}
          </button>
          <button class="btn-navigate" @click="openMap">
            🗺️ 导航前往
          </button>
        </div>
      </div>
      
      <!-- 评论区 -->
      <div class="comments-section">
        <div class="section-header">
          <h2>💬 用户评价 ({{ commentCount }})</h2>
          <div class="rating-summary" v-if="commentCount > 0">
            <span class="avg-rating">{{ averageRating }}</span>
            <span class="avg-star">⭐</span>
            <span class="rating-count">{{ commentCount }} 条评价</span>
          </div>
        </div>
        
        <!-- 评分分布 -->
        <div class="rating-distribution" v-if="comments.length > 0">
          <div v-for="star in [5, 4, 3, 2, 1]" :key="star" class="rating-bar">
            <span class="bar-label">{{ star }}星</span>
            <div class="bar-track">
              <div class="bar-fill" :style="{ width: getRatingPercent(star) + '%' }"></div>
            </div>
            <span class="bar-count">{{ getRatingCount(star) }}</span>
          </div>
        </div>
        
        <!-- 发表评论 -->
        <div class="comment-form" :class="{ 'reply-mode': replyingTo }">
          <div class="form-header">
            <span class="form-title" v-if="!replyingTo">✍️ 写评价</span>
            <span class="form-title reply-title" v-else>
              回复 @{{ replyingTo.userName || '游客' + replyingTo.userId }}
              <button class="cancel-reply" @click="cancelReply">✕ 取消</button>
            </span>
            <span class="form-tip" v-if="!authStore.isLoggedIn">登录后可发表评论</span>
          </div>
          <div class="rating-input" v-if="!replyingTo">
            <span class="rating-label">评分：</span>
            <div class="stars">
              <span 
                v-for="i in 5" 
                :key="i" 
                class="star" 
                :class="{ active: i <= newComment.rating }"
                @click="newComment.rating = i"
                @mouseenter="hoverRating = i"
                @mouseleave="hoverRating = 0"
              >
                {{ i <= (hoverRating || newComment.rating) ? '⭐' : '☆' }}
              </span>
            </div>
            <span class="rating-text">{{ ratingTexts[newComment.rating - 1] }}</span>
          </div>
          <div class="input-group">
            <textarea 
              v-model="newComment.content" 
              placeholder="说说你的用餐体验吧，菜品口味、服务态度、环境氛围..."
              rows="4"
              maxlength="500"
            ></textarea>
            <span class="char-count">{{ newComment.content.length }}/500</span>
          </div>
          <div class="form-footer">
            <div class="quick-tags">
              <span 
                v-for="tag in quickTags" 
                :key="tag" 
                class="quick-tag"
                @click="addQuickTag(tag)"
              >
                {{ tag }}
              </span>
            </div>
            <button class="btn-submit" @click="submitComment" :disabled="submitting || !newComment.content.trim()">
              {{ submitting ? '提交中...' : '发表评论' }}
            </button>
          </div>
        </div>
        
        <!-- 评论筛选和排序 -->
        <div class="comment-filter" v-if="comments.length > 0">
          <div class="filter-left">
            <span 
              class="filter-item" 
              :class="{ active: commentFilter === 'all' }"
              @click="commentFilter = 'all'"
            >
              全部
            </span>
            <span 
              class="filter-item" 
              :class="{ active: commentFilter === 'good' }"
              @click="commentFilter = 'good'"
            >
              好评 ({{ goodComments.length }})
            </span>
            <span 
              class="filter-item" 
              :class="{ active: commentFilter === 'bad' }"
              @click="commentFilter = 'bad'"
            >
              差评 ({{ badComments.length }})
            </span>
          </div>
          <div class="sort-tabs">
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
        
        <!-- 评论列表 -->
        <div v-if="filteredComments.length === 0" class="empty-comments">
          <div class="empty-icon">📝</div>
          <p>{{ comments.length === 0 ? '暂无评价，快来发表第一条评论吧！' : '暂无符合条件的评论' }}</p>
        </div>
        
        <div v-else class="comments-list">
          <div v-for="comment in filteredComments" :key="comment.id" class="comment-item">
            <div class="comment-header">
              <div class="comment-user">
                <div 
                  class="user-avatar clickable" 
                  :style="{ background: getAvatarColor(comment.userName) }"
                  @click="goToUserProfile(comment.userId)"
                  title="查看用户主页"
                >
                  {{ comment.userName?.charAt(0) || '游' }}
                </div>
                <div class="user-info">
                  <span class="user-name clickable" @click="goToUserProfile(comment.userId)">{{ comment.userName || '匿名用户' }}</span>
                  <span class="comment-time">{{ formatDate(comment.createdAt) }}</span>
                </div>
              </div>
              <div class="comment-rating">
                <span class="rating-stars">
                  <span v-for="i in 5" :key="i" class="mini-star">{{ i <= comment.rating ? '⭐' : '☆' }}</span>
                </span>
                <span class="rating-tag" :class="getRatingClass(comment.rating)">
                  {{ getRatingTag(comment.rating) }}
                </span>
              </div>
            </div>
            <p class="comment-content">{{ comment.content }}</p>
            <div class="comment-footer">
              <div class="comment-actions">
                <button 
                  class="btn-like" 
                  :class="{ liked: comment.isLiked }"
                  @click="likeComment(comment)"
                >
                  {{ comment.isLiked ? '👍' : '👍🏻' }} {{ comment.likes || 0 }}
                </button>
                <button class="btn-reply" @click="replyTo(comment)">
                  💬 回复
                </button>
              </div>
            </div>
            <!-- 回复列表 -->
            <div v-if="comment.replies && comment.replies.length > 0" class="replies-section">
              <button 
                class="toggle-replies-btn" 
                @click="toggleReplies(comment.id)"
              >
                <span class="toggle-icon">{{ expandedReplies.has(comment.id) ? '▼' : '▶' }}</span>
                {{ expandedReplies.has(comment.id) ? '收起' : '展开' }} {{ comment.replies.length }} 条回复
              </button>
              <div v-show="expandedReplies.has(comment.id)" class="replies-list">
                <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
                  <div 
                    class="reply-avatar clickable" 
                    :style="{ background: getAvatarColor(reply.userName) }"
                    @click="goToUserProfile(reply.userId)"
                    title="查看用户主页"
                  >
                    {{ reply.userName?.charAt(0) || '游' }}
                  </div>
                  <div class="reply-body">
                    <div class="reply-header">
                      <span class="reply-user clickable" @click="goToUserProfile(reply.userId)">{{ reply.userName || '匿名用户' }}</span>
                      <span class="reply-time">{{ formatDate(reply.createdAt) }}</span>
                    </div>
                    <p class="reply-text">
                      <span class="reply-to" v-if="reply.replyToUser">@{{ reply.replyToUser }} </span>
                      {{ reply.content }}
                    </p>
                    <div class="reply-actions">
                      <button 
                        class="btn-like small" 
                        :class="{ liked: reply.isLiked }"
                        @click="likeComment(reply)"
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
    </template>
    
    <!-- 错误状态 -->
    <div v-else class="error-state">
      <div class="error-icon">😕</div>
      <p>未找到美食信息</p>
      <button class="btn-back-home" @click="goBack">返回</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getFoodById } from '@/api/food';
import { getFoodComments, addFoodComment, replyFoodComment } from '@/api/foodComment';
import { useAuthStore } from '@/modules/auth/store';
import { ElMessage } from 'element-plus';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const food = ref<any>(null);
const loading = ref(true);
const isFavorite = ref(false);
const comments = ref<any[]>([]);
const submitting = ref(false);
const hoverRating = ref(0);
const commentFilter = ref<'all' | 'good' | 'bad'>('all');
const commentSort = ref<'hot' | 'new'>('hot');
const replyingTo = ref<any>(null);
const expandedReplies = ref<Set<number>>(new Set()); // 展开的回复列表

const newComment = ref({
  rating: 5,
  content: ''
});

// 评分文字
const ratingTexts = ['很差', '较差', '一般', '推荐', '强烈推荐'];

// 快捷标签
const quickTags = ['味道正宗', '环境优雅', '服务热情', '性价比高', '分量足', '上菜快', '值得再来'];

// 解析标签
const foodTags = computed(() => {
  if (!food.value?.tags) return [];
  return food.value.tags.split(',').filter((t: string) => t.trim());
});

// 平均评分（优先使用后端计算的评分）
const averageRating = computed(() => {
  // 如果后端返回了评分，直接使用
  if (food.value?.rating && food.value.rating > 0) {
    return food.value.rating.toFixed(1);
  }
  // 否则从评论列表计算
  if (comments.value.length === 0) return '0.0';
  const sum = comments.value.reduce((acc, c) => acc + (c.rating || 0), 0);
  return (sum / comments.value.length).toFixed(1);
});

// 评论数量（优先使用后端返回的数量）
const commentCount = computed(() => {
  if (food.value?.commentCount !== undefined && food.value.commentCount !== null) {
    return food.value.commentCount;
  }
  return comments.value.length;
});

// 好评（4-5星）
const goodComments = computed(() => {
  return comments.value.filter(c => c.rating >= 4);
});

// 差评（1-2星）
const badComments = computed(() => {
  return comments.value.filter(c => c.rating <= 2);
});

// 筛选后的评论
const filteredComments = computed(() => {
  let list = [...comments.value];
  
  // 筛选
  if (commentFilter.value === 'good') {
    list = list.filter(c => c.rating >= 4);
  } else if (commentFilter.value === 'bad') {
    list = list.filter(c => c.rating <= 2);
  }
  
  // 排序
  if (commentSort.value === 'hot') {
    list.sort((a, b) => (b.likes || 0) - (a.likes || 0));
  } else {
    list.sort((a, b) => {
      const timeA = new Date(a.createdAt || 0).getTime();
      const timeB = new Date(b.createdAt || 0).getTime();
      return timeB - timeA;
    });
  }
  
  return list;
});

// 获取某星级的评论数量
function getRatingCount(star: number): number {
  return comments.value.filter(c => c.rating === star).length;
}

// 获取某星级的百分比
function getRatingPercent(star: number): number {
  if (comments.value.length === 0) return 0;
  return (getRatingCount(star) / comments.value.length) * 100;
}

// 获取评分标签
function getRatingTag(rating: number): string {
  if (rating >= 5) return '超赞';
  if (rating >= 4) return '推荐';
  if (rating >= 3) return '一般';
  return '较差';
}

// 获取评分样式类
function getRatingClass(rating: number): string {
  if (rating >= 4) return 'good';
  if (rating >= 3) return 'normal';
  return 'bad';
}

// 获取头像颜色
function getAvatarColor(name?: string): string {
  const colors = [
    'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
    'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
    'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)'
  ];
  const index = (name?.charCodeAt(0) || 0) % colors.length;
  return colors[index];
}

// 跳转到用户主页
function goToUserProfile(userId: number) {
  if (userId) {
    router.push(`/user/${userId}`);
  }
}

// 添加快捷标签
function addQuickTag(tag: string) {
  if (!newComment.value.content.includes(tag)) {
    newComment.value.content += (newComment.value.content ? '，' : '') + tag;
  }
}

// 回复评论
function replyTo(comment: any) {
  if (!authStore.isLoggedIn) {
    ElMessage.warning('请先登录');
    return;
  }
  replyingTo.value = comment;
  // 滚动到评论框
  const form = document.querySelector('.comment-form');
  form?.scrollIntoView({ behavior: 'smooth', block: 'center' });
}

// 取消回复
function cancelReply() {
  replyingTo.value = null;
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
  
  // 切换点赞状态
  if (comment.isLiked) {
    comment.likes = (comment.likes || 1) - 1;
    comment.isLiked = false;
  } else {
    comment.likes = (comment.likes || 0) + 1;
    comment.isLiked = true;
  }
  
  // 保存到本地存储（按用户区分）
  const userId = authStore.user?.id;
  if (userId) {
    const key = `food_comment_likes_${userId}_${comment.id}`;
    if (comment.isLiked) {
      localStorage.setItem(key, 'true');
    } else {
      localStorage.removeItem(key);
    }
  }
}

// 加载点赞状态（按用户区分）
function loadLikeStatus() {
  const userId = authStore.user?.id;
  if (!userId) return;
  
  comments.value.forEach(c => {
    c.isLiked = localStorage.getItem(`food_comment_likes_${userId}_${c.id}`) === 'true';
    // 加载回复的点赞状态
    if (c.replies) {
      c.replies.forEach((r: any) => {
        r.isLiked = localStorage.getItem(`food_comment_likes_${userId}_reply_${r.id}`) === 'true';
      });
    }
  });
}

// 加载美食详情
async function loadFood() {
  const id = Number(route.params.id);
  if (!id) {
    loading.value = false;
    return;
  }
  
  try {
    const res = await getFoodById(id);
    food.value = res.data;
    
    // 加载评论
    await loadComments();
  } catch (e) {
    console.error('加载美食详情失败', e);
  } finally {
    loading.value = false;
  }
}

// 加载评论
async function loadComments() {
  const id = Number(route.params.id);
  try {
    const res = await getFoodComments(id);
    comments.value = res.data || [];
  } catch (e) {
    console.error('加载评论失败', e);
    // 使用模拟数据
    comments.value = [
      { id: 1, userName: '美食家', rating: 5, content: '味道非常正宗，强烈推荐！', createdAt: new Date().toISOString(), likes: 12 },
      { id: 2, userName: '旅行者', rating: 4, content: '环境不错，服务也很好，下次还会来。', createdAt: new Date().toISOString(), likes: 8 },
    ];
  }
  // 加载点赞状态
  loadLikeStatus();
}

// 提交评论
async function submitComment() {
  if (!newComment.value.content.trim()) {
    ElMessage.warning('请输入评论内容');
    return;
  }
  
  if (!authStore.isLoggedIn || !authStore.user?.id) {
    ElMessage.warning('请先登录');
    router.push('/login');
    return;
  }
  
  submitting.value = true;
  
  try {
    const foodId = Number(route.params.id);
    const userId = authStore.user.id;
    const userName = authStore.user.nickname || authStore.user.username || '游客';
    
    if (replyingTo.value) {
      // 发表回复 - 调用后端 API
      await replyFoodComment(replyingTo.value.id, {
        userId: userId,
        userName: userName,
        content: newComment.value.content
      });
      
      // 前端立即显示回复
      const parentComment = comments.value.find(c => c.id === replyingTo.value.id);
      if (parentComment) {
        if (!parentComment.replies) parentComment.replies = [];
        parentComment.replies.push({
          id: Date.now(),
          userId: userId,
          userName: userName,
          content: newComment.value.content,
          createdAt: new Date().toISOString(),
          replyToUser: replyingTo.value.userName || '游客' + replyingTo.value.userId,
          likes: 0
        });
        // 自动展开回复列表
        expandedReplies.value.add(replyingTo.value.id);
        expandedReplies.value = new Set(expandedReplies.value);
      }
      
      ElMessage.success('回复成功');
      cancelReply();
    } else {
      // 发表新评论
      await addFoodComment(foodId, {
        userId: userId,
        userName: userName,
        rating: newComment.value.rating,
        content: newComment.value.content
      });
      
      // 添加到列表
      comments.value.unshift({
        id: Date.now(),
        userName: userName,
        rating: newComment.value.rating,
        content: newComment.value.content,
        createdAt: new Date().toISOString(),
        likes: 0
      });
      
      ElMessage.success('评论发表成功');
    }
    
    // 清空表单
    newComment.value = { rating: 5, content: '' };
  } catch (e) {
    console.error('发表评论失败', e);
    ElMessage.error('发表评论失败');
  } finally {
    submitting.value = false;
  }
}

// 收藏
function toggleFavorite() {
  isFavorite.value = !isFavorite.value;
  ElMessage.success(isFavorite.value ? '已收藏' : '已取消收藏');
}

// 打开地图导航
function openMap() {
  if (food.value?.latitude && food.value?.longitude) {
    const url = `https://uri.amap.com/marker?position=${food.value.longitude},${food.value.latitude}&name=${encodeURIComponent(food.value.name)}`;
    window.open(url, '_blank');
  } else {
    ElMessage.info('暂无位置信息');
  }
}

// 返回
function goBack() {
  router.back();
}

// 格式化日期
function formatDate(date: string) {
  if (!date) return '刚刚';
  const d = new Date(date);
  const now = new Date();
  const diff = now.getTime() - d.getTime();
  
  if (diff < 60000) return '刚刚';
  if (diff < 3600000) return `${Math.floor(diff / 60000)} 分钟前`;
  if (diff < 86400000) return `${Math.floor(diff / 3600000)} 小时前`;
  if (diff < 604800000) return `${Math.floor(diff / 86400000)} 天前`;
  
  return d.toLocaleDateString('zh-CN');
}

onMounted(() => {
  loadFood();
});
</script>

<style scoped>
.food-detail-page {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

/* 加载状态 */
.loading-state {
  text-align: center;
  padding: 100px 0;
  color: #999;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #f0f0f0;
  border-top-color: #667eea;
  border-radius: 50%;
  margin: 0 auto 16px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 头部图片 */
.food-header {
  position: relative;
  height: 300px;
  border-radius: 24px;
  overflow: hidden;
  margin-bottom: 24px;
}

.food-image {
  width: 100%;
  height: 100%;
}

.food-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #ff6b6b 0%, #ffa500 100%);
  font-size: 80px;
}

.food-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  padding: 20px;
  background: linear-gradient(to bottom, rgba(0,0,0,0.4), transparent);
}

.btn-back {
  padding: 10px 20px;
  background: rgba(255,255,255,0.9);
  border: none;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-back:hover {
  background: white;
  transform: translateX(-4px);
}

/* 基本信息卡片 */
.food-info-card {
  background: white;
  border-radius: 24px;
  padding: 32px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.05);
}

.food-title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.food-name {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a2e;
}

.food-rating {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 16px;
  background: #fff8e1;
  border-radius: 20px;
}

.rating-star {
  font-size: 18px;
}

.rating-value {
  font-size: 18px;
  font-weight: 700;
  color: #f5a623;
}

.food-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

.tag {
  padding: 6px 14px;
  background: #f0f0f0;
  border-radius: 16px;
  font-size: 13px;
  color: #666;
}

.tag.category {
  background: linear-gradient(135deg, #ff6b6b 0%, #ffa500 100%);
  color: white;
}

.food-description {
  font-size: 15px;
  color: #666;
  line-height: 1.8;
  margin-bottom: 24px;
}

.food-meta {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 12px;
}

.meta-icon {
  font-size: 20px;
}

.meta-label {
  font-size: 13px;
  color: #999;
}

.meta-value {
  margin-left: auto;
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.food-actions {
  display: flex;
  gap: 16px;
}

.btn-favorite,
.btn-navigate {
  flex: 1;
  padding: 16px;
  border: none;
  border-radius: 16px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-favorite {
  background: #f8f9fa;
  color: #666;
}

.btn-favorite.active {
  background: #fff0f0;
  color: #ff6b6b;
}

.btn-navigate {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-navigate:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

/* 评论区 */
.comments-section {
  background: white;
  border-radius: 24px;
  padding: 32px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.05);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: #1a1a2e;
  margin: 0;
}

.rating-summary {
  display: flex;
  align-items: center;
  gap: 4px;
}

.avg-rating {
  font-size: 28px;
  font-weight: 700;
  color: #f5a623;
}

.avg-star {
  font-size: 20px;
}

.rating-count {
  font-size: 13px;
  color: #999;
  margin-left: 8px;
}

/* 评分分布 */
.rating-distribution {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 24px;
}

.rating-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.rating-bar:last-child {
  margin-bottom: 0;
}

.bar-label {
  font-size: 13px;
  color: #666;
  width: 36px;
}

.bar-track {
  flex: 1;
  height: 8px;
  background: #e0e0e0;
  border-radius: 4px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  background: linear-gradient(135deg, #f5a623 0%, #ff6b6b 100%);
  border-radius: 4px;
  transition: width 0.3s;
}

.bar-count {
  font-size: 13px;
  color: #999;
  width: 24px;
  text-align: right;
}

/* 评论表单 */
.comment-form {
  background: #f8f9fa;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 24px;
}

.form-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.form-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.form-tip {
  font-size: 13px;
  color: #999;
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
  font-size: 28px;
  cursor: pointer;
  transition: transform 0.2s;
}

.star:hover {
  transform: scale(1.2);
}

.rating-text {
  font-size: 14px;
  color: #f5a623;
  font-weight: 500;
}

.input-group {
  position: relative;
  margin-bottom: 16px;
}

.comment-form textarea {
  width: 100%;
  padding: 16px;
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  font-size: 15px;
  resize: none;
  transition: border-color 0.3s;
  box-sizing: border-box;
}

.comment-form textarea:focus {
  outline: none;
  border-color: #667eea;
}

.char-count {
  position: absolute;
  bottom: 12px;
  right: 16px;
  font-size: 12px;
  color: #999;
}

.form-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.quick-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.quick-tag {
  padding: 6px 12px;
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 16px;
  font-size: 12px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
}

.quick-tag:hover {
  background: #667eea;
  color: white;
  border-color: #667eea;
}

.btn-submit {
  padding: 12px 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-submit:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 评论筛选 */
.comment-filter {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.filter-left {
  display: flex;
  gap: 8px;
}

.filter-item {
  font-size: 14px;
  color: #666;
  cursor: pointer;
  padding: 6px 16px;
  border-radius: 16px;
  transition: all 0.3s;
}

.filter-item:hover {
  background: #f0f0f0;
}

.filter-item.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

/* 排序选项 */
.sort-tabs {
  display: flex;
  gap: 8px;
}

.sort-tab {
  padding: 6px 14px;
  border-radius: 16px;
  font-size: 13px;
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

/* 空评论 */
.empty-comments {
  text-align: center;
  padding: 60px 40px;
  color: #999;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 12px;
}

/* 评论列表 */
.comments-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.comment-item {
  padding: 20px;
  background: #f8f9fa;
  border-radius: 16px;
  transition: all 0.3s;
}

.comment-item:hover {
  background: #f0f0f5;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.comment-user {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 600;
  font-size: 18px;
}

.user-avatar.clickable,
.reply-avatar.clickable {
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.user-avatar.clickable:hover,
.reply-avatar.clickable:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.user-name.clickable,
.reply-user.clickable {
  cursor: pointer;
  transition: color 0.2s;
}

.user-name.clickable:hover,
.reply-user.clickable:hover {
  color: #667eea;
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.user-name {
  font-size: 15px;
  font-weight: 600;
  color: #333;
}

.comment-time {
  font-size: 12px;
  color: #999;
}

.comment-rating {
  display: flex;
  align-items: center;
  gap: 8px;
}

.rating-stars {
  font-size: 12px;
}

.mini-star {
  font-size: 12px;
}

.rating-tag {
  padding: 3px 10px;
  border-radius: 10px;
  font-size: 11px;
  font-weight: 500;
}

.rating-tag.good {
  background: #e8f5e9;
  color: #4caf50;
}

.rating-tag.normal {
  background: #fff8e1;
  color: #ff9800;
}

.rating-tag.bad {
  background: #ffebee;
  color: #f44336;
}

.comment-content {
  font-size: 15px;
  color: #444;
  line-height: 1.7;
  margin-bottom: 16px;
}

.comment-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.comment-actions {
  display: flex;
  gap: 12px;
}

.btn-like,
.btn-reply {
  padding: 6px 14px;
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 16px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-like:hover,
.btn-reply:hover {
  background: #f0f0f0;
  border-color: #ccc;
}

.btn-like.liked {
  background: #fff0f0;
  border-color: #ffcdd2;
  color: #f44336;
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

.reply-user {
  font-size: 13px;
  font-weight: 600;
  color: #333;
}

.reply-time {
  font-size: 12px;
  color: #999;
}

.reply-text {
  font-size: 13px;
  color: #555;
  line-height: 1.5;
  margin: 0 0 8px 0;
}

.reply-to {
  color: #667eea;
  font-weight: 500;
}

.reply-actions {
  display: flex;
  gap: 8px;
}

.btn-like.small {
  padding: 4px 10px;
  font-size: 12px;
}

/* 错误状态 */
.error-state {
  text-align: center;
  padding: 100px 0;
}

.error-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.btn-back-home {
  margin-top: 20px;
  padding: 12px 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 15px;
  cursor: pointer;
}

/* 响应式 */
@media (max-width: 768px) {
  .food-header {
    height: 200px;
    border-radius: 0;
    margin: -20px -20px 24px;
  }
  
  .food-info-card,
  .comments-section {
    border-radius: 16px;
    padding: 20px;
  }
  
  .food-name {
    font-size: 22px;
  }
  
  .food-actions {
    flex-direction: column;
  }
}
</style>
