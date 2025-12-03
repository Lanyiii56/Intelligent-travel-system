<template>
  <div class="chat-page">
    <!-- 聊天头部 -->
    <div class="chat-header">
      <button class="back-btn" @click="goBack">
        <span>←</span>
      </button>
      <div class="user-info" @click="viewProfile">
        <img :src="otherUser.avatar || defaultAvatar" class="user-avatar" />
        <div class="user-details">
          <span class="user-name">{{ otherUser.nickname || '用户' }}</span>
          <span class="user-status" :class="{ online: isOnline }">
            {{ isOnline ? '在线' : '离线' }}
          </span>
        </div>
      </div>
      <div class="header-actions">
        <button class="action-btn" @click="toggleFollow" v-if="!isCurrentUser">
          <span>{{ isFollowing ? '已关注' : '关注' }}</span>
        </button>
      </div>
    </div>

    <!-- 消息列表 -->
    <div class="messages-container" ref="messagesContainer">
      <div class="loading-state" v-if="loading">
        <div class="loading-spinner"></div>
      </div>
      
      <div class="empty-chat" v-else-if="messages.length === 0">
        <div class="empty-icon">💬</div>
        <p>开始和 {{ otherUser.nickname || '对方' }} 聊天吧</p>
      </div>

      <div class="messages-list" v-else>
        <div 
          v-for="(msg, index) in messages" 
          :key="msg.id"
          class="message-wrapper"
          :class="{ 'is-mine': msg.isMine }"
        >
          <!-- 时间分割线 -->
          <div class="time-divider" v-if="shouldShowTime(index)">
            {{ formatFullTime(msg.createTime) }}
          </div>
          
          <!-- 消息气泡 -->
          <div class="message-item">
            <img 
              v-if="!msg.isMine"
              :src="otherUser.avatar || defaultAvatar" 
              class="msg-avatar"
            />
            <div class="message-bubble" :class="{ mine: msg.isMine }">
              <p class="message-text">{{ msg.content }}</p>
            </div>
            <img 
              v-if="msg.isMine"
              :src="currentUserAvatar || defaultAvatar" 
              class="msg-avatar"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- 输入区域 -->
    <div class="input-area">
      <div class="input-wrapper">
        <button class="emoji-btn">😊</button>
        <input 
          type="text" 
          v-model="inputMessage"
          placeholder="输入消息..."
          @keyup.enter="sendMessage"
          :disabled="sending"
        />
        <button class="image-btn">📷</button>
      </div>
      <button 
        class="send-btn" 
        @click="sendMessage"
        :disabled="!inputMessage.trim() || sending"
        :class="{ active: inputMessage.trim() }"
      >
        发送
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, nextTick, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '@/modules/auth/store';
import * as messageApi from '@/modules/message/api';
import type { Message } from '@/modules/message/api';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

// 状态
const loading = ref(false);
const sending = ref(false);
const messages = ref<Message[]>([]);
const inputMessage = ref('');
const isFollowing = ref(false);
const messagesContainer = ref<HTMLElement | null>(null);

// 对方用户信息
const otherUser = ref({
  id: 0,
  nickname: '',
  avatar: ''
});

// 默认头像
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';

// 计算属性
const userId = computed(() => authStore.user?.id);
const currentUserAvatar = computed(() => authStore.user?.avatar || authStore.avatar);
const otherUserId = computed(() => Number(route.params.userId));
const isCurrentUser = computed(() => userId.value === otherUserId.value);
const isOnline = computed(() => Math.random() > 0.5); // 模拟在线状态

// 监听路由变化
watch(() => route.params.userId, (newId) => {
  if (newId) {
    loadData();
  }
});

// 生命周期
onMounted(() => {
  loadData();
});

// 方法
async function loadData() {
  if (!userId.value || !otherUserId.value) return;
  
  loading.value = true;
  try {
    await Promise.all([
      loadMessages(),
      loadOtherUserInfo(),
      checkFollowStatus()
    ]);
  } finally {
    loading.value = false;
    scrollToBottom();
  }
}

async function loadMessages() {
  if (!userId.value) return;
  try {
    const res = await messageApi.getConversation(userId.value, otherUserId.value);
    messages.value = res.data || [];
  } catch (error) {
    console.error('加载消息失败:', error);
  }
}

async function loadOtherUserInfo() {
  // 从会话列表获取用户信息，或者调用用户API
  // 这里简化处理，实际应该调用用户信息API
  try {
    if (userId.value) {
      const res = await messageApi.getConversationList(userId.value);
      const conv = (res.data || []).find((c: { userId: number }) => c.userId === otherUserId.value);
      if (conv) {
        otherUser.value = {
          id: conv.userId,
          nickname: conv.nickname,
          avatar: conv.avatar
        };
      } else {
        // 如果没有会话记录，设置默认值
        otherUser.value = {
          id: otherUserId.value,
          nickname: `用户${otherUserId.value}`,
          avatar: ''
        };
      }
    }
  } catch (error) {
    console.error('加载用户信息失败:', error);
  }
}

async function checkFollowStatus() {
  if (!userId.value) return;
  try {
    const res = await messageApi.checkFollow(userId.value, otherUserId.value);
    isFollowing.value = res.data;
  } catch (error) {
    console.error('检查关注状态失败:', error);
  }
}

async function sendMessage() {
  if (!inputMessage.value.trim() || !userId.value || sending.value) return;
  
  sending.value = true;
  const content = inputMessage.value.trim();
  inputMessage.value = '';
  
  try {
    await messageApi.sendMessage(userId.value, otherUserId.value, content);
    
    // 添加到本地消息列表
    messages.value.push({
      id: Date.now(),
      senderId: userId.value,
      receiverId: otherUserId.value,
      content,
      type: 'text',
      createTime: new Date().toISOString(),
      isMine: true
    });
    
    await nextTick();
    scrollToBottom();
  } catch (error) {
    console.error('发送消息失败:', error);
    inputMessage.value = content; // 恢复输入
  } finally {
    sending.value = false;
  }
}

async function toggleFollow() {
  if (!userId.value) return;
  try {
    const res = await messageApi.toggleFollow(userId.value, otherUserId.value);
    isFollowing.value = res.data.isFollowing;
  } catch (error) {
    console.error('关注操作失败:', error);
  }
}

function goBack() {
  router.back();
}

function viewProfile() {
  // 跳转到用户主页
  // router.push(`/user/${otherUserId.value}`);
}

function scrollToBottom() {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
  }
}

function shouldShowTime(index: number): boolean {
  if (index === 0) return true;
  
  const current = new Date(messages.value[index].createTime);
  const previous = new Date(messages.value[index - 1].createTime);
  const diff = current.getTime() - previous.getTime();
  
  // 超过5分钟显示时间
  return diff > 5 * 60 * 1000;
}

function formatFullTime(timeStr: string): string {
  if (!timeStr) return '';
  
  const date = new Date(timeStr);
  const now = new Date();
  const isToday = date.toDateString() === now.toDateString();
  
  const hours = date.getHours().toString().padStart(2, '0');
  const minutes = date.getMinutes().toString().padStart(2, '0');
  const time = `${hours}:${minutes}`;
  
  if (isToday) {
    return time;
  }
  
  const month = (date.getMonth() + 1).toString().padStart(2, '0');
  const day = date.getDate().toString().padStart(2, '0');
  return `${month}/${day} ${time}`;
}
</script>

<style scoped>
.chat-page {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 180px);
  max-width: 800px;
  margin: 0 auto;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

/* 聊天头部 */
.chat-header {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  background: white;
  border-bottom: 1px solid #f0f0f0;
}

.back-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #f5f5f5;
  border: none;
  cursor: pointer;
  font-size: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  transition: all 0.2s;
}

.back-btn:hover {
  background: #eee;
}

.user-info {
  display: flex;
  align-items: center;
  flex: 1;
  cursor: pointer;
}

.user-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 12px;
}

.user-details {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.user-status {
  font-size: 12px;
  color: #999;
}

.user-status.online {
  color: #4cd964;
}

.header-actions .action-btn {
  padding: 8px 16px;
  border-radius: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.header-actions .action-btn:hover {
  transform: scale(1.02);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

/* 消息容器 */
.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f8f9fa;
}

.loading-state {
  display: flex;
  justify-content: center;
  padding: 40px;
}

.loading-spinner {
  width: 32px;
  height: 32px;
  border: 3px solid #f0f0f0;
  border-top-color: #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.empty-chat {
  text-align: center;
  padding: 60px 20px;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-chat p {
  font-size: 14px;
  color: #999;
}

/* 消息列表 */
.messages-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.message-wrapper {
  display: flex;
  flex-direction: column;
}

.message-wrapper.is-mine {
  align-items: flex-end;
}

.time-divider {
  text-align: center;
  font-size: 12px;
  color: #999;
  padding: 12px 0;
}

.message-item {
  display: flex;
  align-items: flex-end;
  gap: 8px;
  max-width: 75%;
}

.message-wrapper.is-mine .message-item {
  flex-direction: row-reverse;
}

.msg-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}

.message-bubble {
  padding: 12px 16px;
  border-radius: 18px;
  background: white;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.message-bubble.mine {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.message-text {
  font-size: 15px;
  line-height: 1.5;
  word-break: break-word;
  margin: 0;
}

/* 输入区域 */
.input-area {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  background: white;
  border-top: 1px solid #f0f0f0;
}

.input-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  background: #f5f5f5;
  border-radius: 24px;
  padding: 4px 8px;
}

.emoji-btn,
.image-btn {
  width: 36px;
  height: 36px;
  border: none;
  background: none;
  font-size: 20px;
  cursor: pointer;
  border-radius: 50%;
  transition: background 0.2s;
}

.emoji-btn:hover,
.image-btn:hover {
  background: #eee;
}

.input-wrapper input {
  flex: 1;
  border: none;
  background: none;
  padding: 10px 12px;
  font-size: 15px;
  outline: none;
}

.input-wrapper input::placeholder {
  color: #999;
}

.send-btn {
  padding: 10px 24px;
  border-radius: 24px;
  background: #f0f0f0;
  color: #999;
  border: none;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.send-btn.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.send-btn.active:hover {
  transform: scale(1.02);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.send-btn:disabled {
  cursor: not-allowed;
}

/* 响应式 */
@media (max-width: 600px) {
  .chat-page {
    height: calc(100vh - 140px);
    border-radius: 0;
  }
  
  .chat-header {
    padding: 12px 16px;
  }
  
  .messages-container {
    padding: 16px;
  }
  
  .message-item {
    max-width: 85%;
  }
  
  .input-area {
    padding: 12px 16px;
  }
}
</style>
