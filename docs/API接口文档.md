# API接口文档

## 基础信息

- **小程序后端地址**: `http://localhost:8080/api`
- **后台管理地址**: `http://localhost:8081`
- **请求格式**: JSON
- **响应格式**: JSON

---

## 一、用户认证模块 (/api/auth)

### 1.1 用户注册
```
POST /api/auth/register
```

**请求参数**:
```json
{
  "username": "用户名",
  "password": "密码",
  "email": "邮箱(可选)",
  "phone": "手机号(可选)"
}
```

**响应**:
```json
{
  "id": 1,
  "username": "用户名",
  "email": "邮箱",
  "createTime": "2025-12-04T12:00:00"
}
```

### 1.2 用户登录
```
POST /api/auth/login
```

**请求参数**:
```json
{
  "username": "用户名",
  "password": "密码"
}
```

### 1.3 获取用户信息
```
GET /api/auth/user/{userId}
```

### 1.4 更新用户信息
```
PUT /api/auth/user/{userId}
```

---

## 二、景点模块 (/api/spots)

### 2.1 获取景点列表
```
GET /api/spots
```

**查询参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| regionId | Long | 地区ID |
| keyword | String | 搜索关键词 |
| page | Integer | 页码 |
| size | Integer | 每页数量 |

### 2.2 获取景点详情
```
GET /api/spots/{id}
```

### 2.3 获取热门景点
```
GET /api/spots/hot
```

### 2.4 按地区获取景点
```
GET /api/spots/region/{regionId}
```

---

## 三、酒店模块 (/api/hotels)

### 3.1 获取酒店列表
```
GET /api/hotels
```

### 3.2 获取酒店详情
```
GET /api/hotels/{id}
```

### 3.3 按地区获取酒店
```
GET /api/hotels/region/{regionId}
```

### 3.4 获取酒店房型
```
GET /api/hotels/{hotelId}/rooms
```

### 3.5 创建酒店订单
```
POST /api/hotels/orders
```

**请求参数**:
```json
{
  "userId": 1,
  "hotelId": 1,
  "roomId": 1,
  "checkInDate": "2025-12-10",
  "checkOutDate": "2025-12-12",
  "guestName": "张三",
  "guestPhone": "13800138000"
}
```

---

## 四、美食模块 (/api/foods)

### 4.1 获取美食列表
```
GET /api/foods
```

### 4.2 获取美食详情
```
GET /api/foods/{id}
```

### 4.3 按地区获取美食
```
GET /api/foods/region/{regionId}
```

### 4.4 添加美食评论
```
POST /api/foods/{foodId}/comments
```

---

## 五、订单模块 (/api/order)

### 5.1 创建订单
```
POST /api/order/create
```

**请求参数**:
```json
{
  "userId": 1,
  "spotId": 1,
  "amount": 100.00,
  "visitDate": "2025-12-10",
  "visitorCount": 2,
  "contactName": "张三",
  "contactPhone": "13800138000"
}
```

### 5.2 支付订单
```
POST /api/order/pay/{orderId}
```

### 5.3 取消订单
```
POST /api/order/cancel/{orderId}
```

### 5.4 获取用户订单列表
```
GET /api/order/list/{userId}
```

---

## 六、社交模块 (/api/social)

### 6.1 收藏/取消收藏
```
POST /api/social/favorite?userId={userId}&spotId={spotId}
```

### 6.2 获取收藏列表
```
GET /api/social/favorite/{userId}
```

### 6.3 发表评论
```
POST /api/social/comment?userId={userId}&spotId={spotId}&content={content}&rating={rating}
```

### 6.4 获取评论列表
```
GET /api/social/comment/{spotId}
```

### 6.5 点赞评论
```
POST /api/social/comment/{commentId}/like
```

### 6.6 回复评论
```
POST /api/social/comment/{parentId}/reply?userId={userId}&spotId={spotId}&content={content}
```

---

## 七、推荐模块 (/api/recommend)

### 7.1 智能推荐行程
```
POST /api/recommend/smart
```

**请求参数**:
```json
{
  "userId": 1,
  "regionId": 1,
  "days": 3,
  "preferences": ["自然风光", "历史文化"],
  "budget": 5000
}
```

### 7.2 保存行程
```
POST /api/recommend/itinerary/save
```

### 7.3 获取用户行程
```
GET /api/recommend/itinerary/{userId}
```

---

## 八、消息模块 (/api/message)

### 8.1 发送私信
```
POST /api/message/send
```

### 8.2 获取对话列表
```
GET /api/message/conversations/{userId}
```

### 8.3 获取对话消息
```
GET /api/message/chat/{userId}/{targetUserId}
```

### 8.4 关注用户
```
POST /api/message/follow?followerId={followerId}&followingId={followingId}
```

### 8.5 获取关注列表
```
GET /api/message/following/{userId}
```

### 8.6 获取粉丝列表
```
GET /api/message/followers/{userId}
```

---

## 九、地区模块 (/api/regions)

### 9.1 获取所有地区
```
GET /api/regions
```

### 9.2 获取地区详情
```
GET /api/regions/{id}
```

### 9.3 获取热门地区
```
GET /api/regions/hot
```

---

## 十、后台管理接口

后台管理系统使用若依框架，接口前缀为 `/travel/`

### 10.1 数据统计
```
GET /travel/statistics/dashboard
```

### 10.2 景点管理
```
GET    /travel/spot/list          # 列表
GET    /travel/spot/{id}          # 详情
POST   /travel/spot               # 新增
PUT    /travel/spot               # 修改
DELETE /travel/spot/{ids}         # 删除
```

### 10.3 其他管理接口
- `/travel/hotel/*` - 酒店管理
- `/travel/food/*` - 美食管理
- `/travel/region/*` - 地区管理
- `/travel/order/*` - 订单管理
- `/travel/comment/*` - 评论管理
- `/travel/user/*` - 用户管理
- `/travel/message/*` - 消息管理
- `/travel/follow/*` - 关注管理

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |
