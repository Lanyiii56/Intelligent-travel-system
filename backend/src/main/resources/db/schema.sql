-- ============================================
-- 智游天下 - 数据库初始化脚本
-- 数据库: PostgreSQL
-- ============================================

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(100),
    age INTEGER,
    gender VARCHAR(20),
    avatar_url VARCHAR(500),
    motto VARCHAR(255),
    points INTEGER DEFAULT 0,
    level VARCHAR(50) DEFAULT '萌新旅者',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 用户成就表
CREATE TABLE IF NOT EXISTS user_achievements (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    achievement_id VARCHAR(50) NOT NULL,
    achievement_name VARCHAR(100) NOT NULL,
    achievement_icon VARCHAR(50),
    unlocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, achievement_id)
);

-- 地区表
CREATE TABLE IF NOT EXISTS regions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    image_url VARCHAR(500),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 景点表
CREATE TABLE IF NOT EXISTS spots (
    id BIGSERIAL PRIMARY KEY,
    region_id BIGINT REFERENCES regions(id),
    name VARCHAR(200) NOT NULL,
    description TEXT,
    address VARCHAR(500),
    latitude DECIMAL(10, 7),
    longitude DECIMAL(10, 7),
    image_url VARCHAR(500),
    ticket_price DECIMAL(10, 2) DEFAULT 0,
    open_time VARCHAR(100),
    close_time VARCHAR(100),
    recommended_duration INTEGER DEFAULT 60,
    rating DECIMAL(3, 2) DEFAULT 0,
    tags VARCHAR(500),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 收藏表
CREATE TABLE IF NOT EXISTS favorites (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    spot_id BIGINT NOT NULL REFERENCES spots(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, spot_id)
);

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    spot_id BIGINT NOT NULL REFERENCES spots(id),
    amount DECIMAL(10, 2) NOT NULL,
    order_status VARCHAR(50) DEFAULT 'PENDING',
    visit_date DATE,
    visitor_count INTEGER DEFAULT 1,
    contact_name VARCHAR(100),
    contact_phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 行程表
CREATE TABLE IF NOT EXISTS itineraries (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(200) NOT NULL,
    itinerary_data TEXT,
    total_cost DECIMAL(10, 2) DEFAULT 0,
    total_distance DECIMAL(10, 2) DEFAULT 0,
    total_duration INTEGER DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 美食表
CREATE TABLE IF NOT EXISTS foods (
    id BIGSERIAL PRIMARY KEY,
    region_id BIGINT REFERENCES regions(id),
    name VARCHAR(200) NOT NULL,
    description TEXT,
    address VARCHAR(500),
    latitude DECIMAL(10, 7),
    longitude DECIMAL(10, 7),
    image_url VARCHAR(500),
    price_range VARCHAR(50),
    rating DECIMAL(3, 2) DEFAULT 0,
    cuisine_type VARCHAR(100),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 景点评论表
CREATE TABLE IF NOT EXISTS comments (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    user_name VARCHAR(100),
    spot_id BIGINT REFERENCES spots(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    rating INTEGER CHECK (rating >= 1 AND rating <= 5),
    likes INTEGER DEFAULT 0,
    parent_id BIGINT REFERENCES comments(id) ON DELETE CASCADE,
    reply_to_user_id BIGINT REFERENCES users(id),
    reply_to_user_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 美食评论表
CREATE TABLE IF NOT EXISTS food_comments (
    id BIGSERIAL PRIMARY KEY,
    food_id BIGINT NOT NULL REFERENCES foods(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    user_name VARCHAR(100),
    rating INTEGER CHECK (rating >= 0 AND rating <= 5),
    content TEXT,
    likes INTEGER DEFAULT 0,
    parent_id BIGINT REFERENCES food_comments(id) ON DELETE CASCADE,
    reply_to_user_id BIGINT REFERENCES users(id),
    reply_to_user VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 浏览历史表
CREATE TABLE IF NOT EXISTS browse_history (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    spot_id BIGINT NOT NULL REFERENCES spots(id) ON DELETE CASCADE,
    viewed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_favorites_user_id ON favorites(user_id);
CREATE INDEX IF NOT EXISTS idx_orders_user_id ON orders(user_id);
CREATE INDEX IF NOT EXISTS idx_itineraries_user_id ON itineraries(user_id);
CREATE INDEX IF NOT EXISTS idx_spots_region_id ON spots(region_id);
CREATE INDEX IF NOT EXISTS idx_comments_spot_id ON comments(spot_id);
CREATE INDEX IF NOT EXISTS idx_browse_history_user_id ON browse_history(user_id);
CREATE INDEX IF NOT EXISTS idx_food_comments_food_id ON food_comments(food_id);

-- ============================================
-- 注意: 由于使用了 JPA 的 ddl-auto=update，
-- 大部分表会自动创建。此脚本用于手动初始化或补充。
-- ============================================
