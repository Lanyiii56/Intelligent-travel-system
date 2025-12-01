# 🗺️ 智能旅游推荐平台

基于 Vue 3 + Spring Boot 的地域特色旅游推荐系统，支持根据年龄、时间、人数智能推荐景点和规划路线。

## ✨ 核心功能

- **地区选择**: 支持省市区三级地区选择
- **年龄适配**: 根据用户年龄推荐适合的景点和活动
- **时间规划**: 根据可用游玩时间自动规划最优路线
- **人数计算**: 支持多人出行，自动计算总费用和人均费用
- **费用预估**: 包含门票、交通、餐饮等费用预估
- **地图展示**: 集成高德地图，可视化展示游玩路线

## 🛠️ 技术栈

### 前端
- Vue 3 + TypeScript + Vite
- Element Plus (UI 组件库)
- Pinia (状态管理)
- Vue Router
- 高德地图 JS API
- ECharts (数据可视化)

### 后端
- Spring Boot 3.2
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL
- Redis (缓存)

## 🚀 快速开始

### 1. 环境要求

- Node.js >= 20.x
- Java 17+
- Docker Desktop
- Git

### 2. 一键启动

```bash
# 1. 启动数据库 (Docker)
docker-compose up -d

# 2. 启动后端
cd backend && ./mvnw spring-boot:run

# 3. 启动前端 (新终端)
cd frontend && npm install && npm run dev
```

### 3. 访问

- 前端: http://localhost:5173
- 后端: http://localhost:8080

> 详细步骤请参考 [docs/快速启动.md](./docs/快速启动.md)

### 4. 配置高德地图

1. 访问 [高德开放平台](https://lbs.amap.com/) 注册账号
2. 创建应用，获取 Web JS API Key
3. 修改 `frontend/src/config/amap.ts` 中的 `key` 值

## 📁 项目结构 (模块化)

```
Intelligent-travel-system/
├── backend/                          # 后端代码
│   └── src/main/java/.../backend/
│       ├── common/                   # 🔒 公共模块
│       │   └── util/
│       └── modules/                  # 📦 业务模块
│           ├── auth/                 # 👤 成员1: 用户认证
│           ├── spot/                 # 🏞️ 成员2: 景点管理
│           ├── recommend/            # 🎯 成员3: 智能推荐
│           └── order/                # 📦 成员4: 订单社交
│
├── frontend/                         # 前端代码
│   └── src/
│       ├── api/                      # 🔒 公共API配置
│       ├── router/                   # 🔒 路由入口
│       └── modules/                  # 📦 业务模块
│           ├── auth/                 # 👤 成员1: 用户认证
│           ├── spot/                 # 🏞️ 成员2: 景点管理
│           ├── recommend/            # 🎯 成员3: 智能推荐
│           └── order/                # 📦 成员4: 订单社交
│
├── docs/                             # 📚 项目文档
│   ├── 环境搭建指南.md
│   ├── 快速启动.md
│   ├── 项目分工说明.md
│   └── 项目开发步骤.md
│
└── docker-compose.yml                # 🐳 Docker配置
```

## 👥 团队分工

| 成员 | 模块 | 职责 |
|------|------|------|
| 成员1 | auth | 用户认证、登录注册、个人中心 |
| 成员2 | spot | 景点管理、地区选择、首页 |
| 成员3 | recommend | 智能推荐、路线规划、地图展示 |
| 成员4 | order | 订单管理、评论、收藏 |

> 详细分工请参考 [docs/项目分工说明.md](./docs/项目分工说明.md)

## 🔌 API 接口

### 智能推荐

```
POST /api/recommend/smart
```

请求参数:
```json
{
  "regionId": 1,        // 地区ID
  "age": 25,            // 年龄
  "playTime": 480,      // 游玩时间(分钟)
  "peopleCount": 2,     // 人数
  "budget": 1000,       // 预算(可选)
  "preference": "观光"   // 偏好(可选)
}
```

响应:
```json
{
  "spots": [...],       // 推荐景点列表
  "route": {...},       // 路线信息
  "cost": {...},        // 费用预估
  "summary": "..."      // 推荐总结
}
```

## 📝 开发计划

- [x] 基础架构搭建
- [x] 模块化重构
- [x] 用户认证系统
- [x] 景点管理
- [x] 智能推荐算法
- [x] 路线规划
- [x] 费用预估
- [x] 地图展示
- [x] 订单管理
- [x] 评论收藏
- [ ] 支付集成
- [ ] 社交分享

## 📚 文档

| 文档 | 说明 |
|------|------|
| [环境搭建指南](./docs/环境搭建指南.md) | 详细的环境配置步骤 |
| [快速启动](./docs/快速启动.md) | 3分钟快速启动项目 |
| [项目分工说明](./docs/项目分工说明.md) | 详细的模块分工和文件列表 |
| [项目开发步骤](./docs/项目开发步骤.md) | 开发流程和计划 |
| [版本依赖说明](./docs/版本依赖说明.md) | ⚠️ **重要** 所有依赖版本 |

## 📄 License

MIT
