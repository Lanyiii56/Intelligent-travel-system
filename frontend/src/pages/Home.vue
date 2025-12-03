<template>
  <div class="home-page">
    <!-- Hero 欢迎区域 -->
    <section class="hero">
      <div class="hero-bg">
        <div class="bg-circle circle-1"></div>
        <div class="bg-circle circle-2"></div>
        <div class="bg-circle circle-3"></div>
      </div>
      <div class="hero-content">
        <div class="hero-badge">🌟 智游天下</div>
        <h1 class="hero-title">欢迎来到智游天下</h1>
        <p class="hero-subtitle">您的智能旅行规划助手</p>
        <p class="hero-desc">
          智游天下是一款基于人工智能的旅行规划平台，为您提供个性化的行程推荐、
          景点探索、美食发现和酒店预订服务，让每一次旅行都成为难忘的回忆。
        </p>
        <div class="hero-actions">
          <router-link to="/recommend" class="btn-start">
            <span>🚀</span> 开始探索
          </router-link>
        </div>
      </div>
      <div class="hero-illustration">
        <div class="illustration-item item-1">🌍</div>
        <div class="illustration-item item-2">✈️</div>
        <div class="illustration-item item-3">🏔️</div>
        <div class="illustration-item item-4">🌴</div>
        <div class="illustration-item item-5">🏯</div>
      </div>
    </section>

    <!-- 平台介绍 -->
    <section class="intro-section">
      <h2 class="section-title">关于我们</h2>
      <div class="intro-content">
        <div class="intro-text">
          <p>
            <strong>智游天下</strong>致力于为每一位旅行者提供最贴心的旅行服务。
            我们运用先进的人工智能技术，根据您的年龄、预算、时间和个人偏好，
            为您量身定制最适合的旅行方案。
          </p>
          <p>
            无论您是想要一场说走就走的周末短途游，还是精心策划的家庭亲子行，
            智游天下都能帮您轻松搞定。我们覆盖全国各大热门旅游城市，
            精选优质景点、地道美食和舒适酒店，让您的旅途无忧无虑。
          </p>
        </div>
        <div class="intro-stats">
          <div class="stat-card">
            <span class="stat-icon">🏞️</span>
            <span class="stat-number">140+</span>
            <span class="stat-label">精选景点</span>
          </div>
          <div class="stat-card">
            <span class="stat-icon">🍜</span>
            <span class="stat-number">129+</span>
            <span class="stat-label">特色美食</span>
          </div>
          <div class="stat-card">
            <span class="stat-icon">🏨</span>
            <span class="stat-number">18+</span>
            <span class="stat-label">优质酒店</span>
          </div>
          <div class="stat-card">
            <span class="stat-icon">🗺️</span>
            <span class="stat-number">53+</span>
            <span class="stat-label">覆盖城市</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 功能介绍 -->
    <section class="features-section">
      <h2 class="section-title">平台功能</h2>
      <p class="section-subtitle">点击下方功能卡片，了解如何使用</p>
      <div class="features-grid">
        <div class="feature-card" @click="showFeatureGuide('recommend')">
          <div class="feature-icon">🎯</div>
          <h3>智能推荐</h3>
          <p>AI 智能匹配最适合您的景点和路线</p>
          <span class="feature-link">了解详情 →</span>
        </div>
        <div class="feature-card" @click="showFeatureGuide('spots')">
          <div class="feature-icon">🏞️</div>
          <h3>景点探索</h3>
          <p>浏览全国各地精选景点，发现美好风景</p>
          <span class="feature-link">了解详情 →</span>
        </div>
        <div class="feature-card" @click="showFeatureGuide('food')">
          <div class="feature-icon">🍜</div>
          <h3>美食发现</h3>
          <p>探索地道美食，品味当地特色风味</p>
          <span class="feature-link">了解详情 →</span>
        </div>
        <div class="feature-card" @click="showFeatureGuide('hotel')">
          <div class="feature-icon">🏨</div>
          <h3>酒店预订</h3>
          <p>精选优质酒店，享受舒适住宿体验</p>
          <span class="feature-link">了解详情 →</span>
        </div>
        <div class="feature-card" @click="showFeatureGuide('social')">
          <div class="feature-icon">👥</div>
          <h3>社交互动</h3>
          <p>关注好友、分享行程、交流旅行心得</p>
          <span class="feature-link">了解详情 →</span>
        </div>
        <div class="feature-card" @click="showFeatureGuide('profile')">
          <div class="feature-icon">👤</div>
          <h3>个人中心</h3>
          <p>管理收藏、订单、行程，个性化设置</p>
          <span class="feature-link">了解详情 →</span>
        </div>
      </div>
    </section>

    <!-- 使用流程 -->
    <section class="process-section">
      <h2 class="section-title">使用流程</h2>
      <div class="process-steps">
        <div class="process-step">
          <div class="step-number">1</div>
          <div class="step-icon">📝</div>
          <h3>注册登录</h3>
          <p>创建您的账号，开启旅行之旅</p>
        </div>
        <div class="process-arrow">→</div>
        <div class="process-step">
          <div class="step-number">2</div>
          <div class="step-icon">🎯</div>
          <h3>选择偏好</h3>
          <p>告诉我们您的旅行需求和偏好</p>
        </div>
        <div class="process-arrow">→</div>
        <div class="process-step">
          <div class="step-number">3</div>
          <div class="step-icon">🗺️</div>
          <h3>获取推荐</h3>
          <p>AI 为您生成个性化行程方案</p>
        </div>
        <div class="process-arrow">→</div>
        <div class="process-step">
          <div class="step-number">4</div>
          <div class="step-icon">🎉</div>
          <h3>开始旅行</h3>
          <p>按照规划出发，享受美好旅程</p>
        </div>
      </div>
    </section>

    <!-- 底部 CTA -->
    <section class="cta-section">
      <div class="cta-content">
        <h2>准备好开始您的旅程了吗？</h2>
        <p>立即体验智游天下，让 AI 为您规划完美行程</p>
        <div class="cta-buttons">
          <router-link to="/recommend" class="btn-cta-primary">
            🚀 立即开始
          </router-link>
          <router-link to="/spots" class="btn-cta-secondary">
            🏞️ 浏览景点
          </router-link>
        </div>
      </div>
    </section>

    <!-- 功能使用指南弹窗 -->
    <el-dialog 
      v-model="showGuideDialog" 
      :title="currentGuide.title" 
      width="600px"
      class="guide-dialog"
    >
      <div class="guide-content">
        <div class="guide-icon">{{ currentGuide.icon }}</div>
        <div class="guide-steps">
          <div 
            v-for="(step, index) in currentGuide.steps" 
            :key="index" 
            class="guide-step"
          >
            <div class="guide-step-number">{{ index + 1 }}</div>
            <div class="guide-step-content">
              <h4>{{ step.title }}</h4>
              <p>{{ step.desc }}</p>
            </div>
          </div>
        </div>
        <div class="guide-tips" v-if="currentGuide.tips">
          <h4>💡 小贴士</h4>
          <p>{{ currentGuide.tips }}</p>
        </div>
      </div>
      <template #footer>
        <el-button @click="showGuideDialog = false">关闭</el-button>
        <el-button type="primary" @click="goToFeature">
          前往体验 →
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

// 功能指南弹窗
const showGuideDialog = ref(false);
const currentFeature = ref('');

// 功能指南数据
const featureGuides: Record<string, {
  title: string;
  icon: string;
  route: string;
  steps: { title: string; desc: string }[];
  tips?: string;
}> = {
  recommend: {
    title: '智能推荐使用指南',
    icon: '🎯',
    route: '/recommend',
    steps: [
      { title: '选择目的地', desc: '在页面顶部选择您想要前往的城市或地区' },
      { title: '设置偏好', desc: '填写您的年龄、出行人数、游玩时长和预算范围' },
      { title: '选择旅行风格', desc: '选择您喜欢的旅行类型，如自然风光、历史文化、美食探店等' },
      { title: '获取推荐', desc: '点击"开始推荐"按钮，AI 将为您生成个性化行程方案' },
      { title: '保存行程', desc: '满意的行程可以保存到个人中心，随时查看' },
    ],
    tips: '建议提前规划好出行时间和预算，这样推荐结果会更加精准哦！'
  },
  spots: {
    title: '景点探索使用指南',
    icon: '🏞️',
    route: '/spots',
    steps: [
      { title: '浏览景点列表', desc: '进入景点页面，可以看到全国各地的精选景点' },
      { title: '筛选景点', desc: '使用地区筛选器，快速找到特定城市的景点' },
      { title: '搜索景点', desc: '在搜索框输入景点名称或关键词进行搜索' },
      { title: '查看详情', desc: '点击景点卡片，查看详细介绍、门票价格、游玩时长等信息' },
      { title: '收藏景点', desc: '喜欢的景点可以点击收藏按钮，保存到个人收藏' },
    ],
    tips: '可以在景点详情页发表评论，分享您的游玩体验！'
  },
  food: {
    title: '美食发现使用指南',
    icon: '🍜',
    route: '/spots',
    steps: [
      { title: '进入美食页面', desc: '在智能推荐结果中会展示当地特色美食' },
      { title: '查看美食详情', desc: '点击美食卡片，查看详细介绍、人均消费、地址等' },
      { title: '查看评论', desc: '浏览其他用户的评价，了解真实口碑' },
      { title: '发表评论', desc: '品尝后可以发表您的评价，帮助其他用户' },
    ],
    tips: '美食推荐会根据您的行程自动匹配，让您在游玩途中不错过任何美味！'
  },
  hotel: {
    title: '酒店预订使用指南',
    icon: '🏨',
    route: '/hotels',
    steps: [
      { title: '选择城市', desc: '在酒店页面顶部选择您要入住的城市' },
      { title: '筛选酒店', desc: '根据星级（经济型/舒适型/高档型/豪华型）筛选酒店' },
      { title: '排序查看', desc: '可以按价格或评分排序，找到最合适的酒店' },
      { title: '查看详情', desc: '点击酒店卡片，查看房型、设施、位置等详细信息' },
      { title: '预订房间', desc: '选择心仪的房型，填写入住信息完成预订' },
    ],
    tips: '建议提前预订热门酒店，特别是节假日期间！'
  },
  social: {
    title: '社交互动使用指南',
    icon: '👥',
    route: '/profile',
    steps: [
      { title: '查看用户主页', desc: '点击其他用户的头像，可以查看他们的主页' },
      { title: '关注用户', desc: '点击"关注"按钮，关注感兴趣的旅行达人' },
      { title: '发送私信', desc: '点击"私信"按钮，与其他用户交流旅行心得' },
      { title: '查看粉丝', desc: '在个人中心可以查看您的粉丝和关注列表' },
    ],
    tips: '多与其他旅行者交流，可以获得更多旅行灵感！'
  },
  profile: {
    title: '个人中心使用指南',
    icon: '👤',
    route: '/profile',
    steps: [
      { title: '编辑资料', desc: '点击"编辑资料"按钮，完善您的个人信息' },
      { title: '更换头像', desc: '点击头像，可以上传新头像或选择预设头像' },
      { title: '查看收藏', desc: '在收藏列表中查看您收藏的景点和行程' },
      { title: '管理订单', desc: '查看您的订单记录和订单状态' },
      { title: '设置', desc: '点击设置按钮，可以修改隐私、通知等偏好设置' },
    ],
    tips: '完善个人资料可以获得更精准的推荐结果！'
  }
};

// 当前显示的指南
const currentGuide = reactive({
  title: '',
  icon: '',
  route: '',
  steps: [] as { title: string; desc: string }[],
  tips: ''
});

// 显示功能指南
function showFeatureGuide(feature: string) {
  currentFeature.value = feature;
  const guide = featureGuides[feature];
  if (guide) {
    currentGuide.title = guide.title;
    currentGuide.icon = guide.icon;
    currentGuide.route = guide.route;
    currentGuide.steps = guide.steps;
    currentGuide.tips = guide.tips || '';
    showGuideDialog.value = true;
  }
}

// 前往功能页面
function goToFeature() {
  showGuideDialog.value = false;
  router.push(currentGuide.route);
}
</script>

<style scoped>
.home-page {
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

/* ==================== Hero 欢迎区域 ==================== */
.hero {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 32px;
  padding: 80px 60px;
  margin-bottom: 32px;
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  min-height: 500px;
}

.hero-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
}

.bg-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
}

.circle-1 {
  width: 400px;
  height: 400px;
  top: -100px;
  right: -100px;
}

.circle-2 {
  width: 300px;
  height: 300px;
  bottom: -50px;
  left: -50px;
}

.circle-3 {
  width: 200px;
  height: 200px;
  top: 50%;
  left: 30%;
}

.hero-content {
  position: relative;
  z-index: 2;
  max-width: 600px;
  color: white;
}

.hero-badge {
  display: inline-block;
  padding: 10px 20px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border-radius: 25px;
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 24px;
}

.hero-title {
  font-size: 56px;
  font-weight: 800;
  margin-bottom: 16px;
  line-height: 1.2;
}

.hero-subtitle {
  font-size: 24px;
  opacity: 0.9;
  margin-bottom: 20px;
}

.hero-desc {
  font-size: 16px;
  line-height: 1.8;
  opacity: 0.85;
  margin-bottom: 32px;
}

.hero-actions {
  display: flex;
  gap: 16px;
}

.btn-start {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 18px 40px;
  background: white;
  color: #667eea;
  border-radius: 30px;
  font-size: 18px;
  font-weight: 700;
  transition: all 0.3s;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.btn-start:hover {
  transform: translateY(-4px);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.3);
  color: #667eea;
}

.hero-illustration {
  position: absolute;
  right: 60px;
  top: 50%;
  transform: translateY(-50%);
  width: 300px;
  height: 300px;
}

.illustration-item {
  position: absolute;
  font-size: 48px;
  animation: float 3s ease-in-out infinite;
}

.item-1 { left: 50%; top: 50%; transform: translate(-50%, -50%); font-size: 80px; }
.item-2 { left: 0; top: 20%; animation-delay: 0.5s; }
.item-3 { right: 0; top: 10%; animation-delay: 1s; }
.item-4 { left: 10%; bottom: 10%; animation-delay: 1.5s; }
.item-5 { right: 10%; bottom: 20%; animation-delay: 2s; }

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-15px); }
}

/* ==================== 平台介绍 ==================== */
.intro-section {
  background: white;
  border-radius: 24px;
  padding: 60px;
  margin-bottom: 32px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.section-title {
  font-size: 32px;
  font-weight: 700;
  color: #1a1a2e;
  text-align: center;
  margin-bottom: 40px;
}

.intro-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 60px;
  align-items: center;
}

.intro-text p {
  font-size: 16px;
  line-height: 2;
  color: #555;
  margin-bottom: 20px;
}

.intro-text strong {
  color: #667eea;
}

.intro-stats {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.stat-card {
  background: linear-gradient(135deg, #f5f3ff 0%, #fdf2f8 100%);
  border-radius: 20px;
  padding: 30px;
  text-align: center;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.15);
}

.stat-icon {
  font-size: 40px;
  display: block;
  margin-bottom: 12px;
}

.stat-number {
  font-size: 36px;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  display: block;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-top: 4px;
}

/* ==================== 功能介绍 ==================== */
.features-section {
  background: white;
  border-radius: 24px;
  padding: 60px;
  margin-bottom: 32px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.section-subtitle {
  text-align: center;
  color: #888;
  font-size: 16px;
  margin-top: -24px;
  margin-bottom: 40px;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.feature-card {
  background: #f8f9fa;
  border-radius: 20px;
  padding: 40px 30px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid transparent;
}

.feature-card:hover {
  background: white;
  border-color: #667eea;
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(102, 126, 234, 0.15);
}

.feature-icon {
  font-size: 56px;
  margin-bottom: 20px;
}

.feature-card h3 {
  font-size: 20px;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 12px;
}

.feature-card p {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 16px;
}

.feature-link {
  color: #667eea;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s;
}

.feature-card:hover .feature-link {
  color: #764ba2;
}

/* ==================== 使用流程 ==================== */
.process-section {
  background: linear-gradient(135deg, #f5f3ff 0%, #fdf2f8 100%);
  border-radius: 24px;
  padding: 60px;
  margin-bottom: 32px;
}

.process-steps {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
}

.process-step {
  background: white;
  border-radius: 20px;
  padding: 30px;
  text-align: center;
  width: 200px;
  position: relative;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.step-number {
  position: absolute;
  top: -15px;
  left: 50%;
  transform: translateX(-50%);
  width: 30px;
  height: 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 14px;
}

.step-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.process-step h3 {
  font-size: 18px;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 8px;
}

.process-step p {
  font-size: 13px;
  color: #666;
}

.process-arrow {
  font-size: 24px;
  color: #667eea;
  font-weight: 700;
}

/* ==================== 底部 CTA ==================== */
.cta-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 24px;
  padding: 80px 60px;
  text-align: center;
  color: white;
}

.cta-content h2 {
  font-size: 36px;
  font-weight: 800;
  margin-bottom: 16px;
}

.cta-content p {
  font-size: 18px;
  opacity: 0.9;
  margin-bottom: 32px;
}

.cta-buttons {
  display: flex;
  gap: 20px;
  justify-content: center;
}

.btn-cta-primary {
  padding: 18px 40px;
  background: white;
  color: #667eea;
  border-radius: 30px;
  font-size: 18px;
  font-weight: 700;
  transition: all 0.3s;
}

.btn-cta-primary:hover {
  transform: translateY(-4px);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.2);
  color: #667eea;
}

.btn-cta-secondary {
  padding: 18px 40px;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border-radius: 30px;
  font-size: 18px;
  font-weight: 700;
  transition: all 0.3s;
}

.btn-cta-secondary:hover {
  background: rgba(255, 255, 255, 0.3);
  color: white;
}

/* ==================== 功能指南弹窗 ==================== */
.guide-content {
  padding: 20px 0;
}

.guide-icon {
  font-size: 64px;
  text-align: center;
  margin-bottom: 30px;
}

.guide-steps {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.guide-step {
  display: flex;
  gap: 16px;
  align-items: flex-start;
}

.guide-step-number {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  flex-shrink: 0;
}

.guide-step-content h4 {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 4px;
}

.guide-step-content p {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
}

.guide-tips {
  margin-top: 30px;
  padding: 20px;
  background: linear-gradient(135deg, #f5f3ff 0%, #fdf2f8 100%);
  border-radius: 12px;
}

.guide-tips h4 {
  font-size: 15px;
  font-weight: 600;
  color: #667eea;
  margin-bottom: 8px;
}

.guide-tips p {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}

/* ==================== 响应式 ==================== */
@media (max-width: 1024px) {
  .features-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .process-steps {
    flex-wrap: wrap;
  }
  
  .process-arrow {
    display: none;
  }
}

@media (max-width: 768px) {
  .hero {
    padding: 60px 30px;
    min-height: auto;
  }
  
  .hero-title {
    font-size: 36px;
  }
  
  .hero-subtitle {
    font-size: 18px;
  }
  
  .hero-illustration {
    display: none;
  }
  
  .intro-content {
    grid-template-columns: 1fr;
    gap: 40px;
  }
  
  .features-grid {
    grid-template-columns: 1fr;
  }
  
  .process-step {
    width: 100%;
    max-width: 280px;
  }
  
  .cta-buttons {
    flex-direction: column;
    align-items: center;
  }
  
  .section-title {
    font-size: 24px;
  }
}
</style>
