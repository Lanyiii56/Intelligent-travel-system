-- ============================================
-- 评论功能增强 - 添加点赞和回复字段
-- ============================================

-- 景点评论表添加新字段
ALTER TABLE comments ADD COLUMN IF NOT EXISTS user_name VARCHAR(100);
ALTER TABLE comments ADD COLUMN IF NOT EXISTS likes INTEGER DEFAULT 0;
ALTER TABLE comments ADD COLUMN IF NOT EXISTS parent_id BIGINT REFERENCES comments(id) ON DELETE CASCADE;
ALTER TABLE comments ADD COLUMN IF NOT EXISTS reply_to_user_id BIGINT;
ALTER TABLE comments ADD COLUMN IF NOT EXISTS reply_to_user_name VARCHAR(100);

-- 美食评论表添加新字段
ALTER TABLE food_comments ADD COLUMN IF NOT EXISTS parent_id BIGINT REFERENCES food_comments(id) ON DELETE CASCADE;
ALTER TABLE food_comments ADD COLUMN IF NOT EXISTS reply_to_user_id BIGINT;
ALTER TABLE food_comments ADD COLUMN IF NOT EXISTS reply_to_user VARCHAR(100);

-- 创建索引以提高查询性能
CREATE INDEX IF NOT EXISTS idx_comments_parent_id ON comments(parent_id);
CREATE INDEX IF NOT EXISTS idx_food_comments_parent_id ON food_comments(parent_id);
