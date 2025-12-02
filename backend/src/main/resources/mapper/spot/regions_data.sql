-- ============================================================
-- 文件: regions_data.sql
-- 模块: spot (景点模块)
-- 数据库: travel_db
-- 表名: regions (地区表)
-- 说明: 包含省份和城市数据
-- ============================================================

-- 重置序列（可选，仅在需要重新导入时使用）
-- SELECT setval('regions_id_seq', 1, false);

-- ==================== 省份/直辖市 ====================
INSERT INTO regions (id, name, parent_id, type) VALUES (1, '北京', NULL, 'province');
INSERT INTO regions (id, name, parent_id, type) VALUES (2, '上海', NULL, 'province');
INSERT INTO regions (id, name, parent_id, type) VALUES (3, '四川', NULL, 'province');
INSERT INTO regions (id, name, parent_id, type) VALUES (4, '浙江', NULL, 'province');
INSERT INTO regions (id, name, parent_id, type) VALUES (5, '广东', NULL, 'province');
INSERT INTO regions (id, name, parent_id, type) VALUES (6, '江苏', NULL, 'province');
INSERT INTO regions (id, name, parent_id, type) VALUES (7, '湖北', NULL, 'province');
INSERT INTO regions (id, name, parent_id, type) VALUES (8, '陕西', NULL, 'province');
INSERT INTO regions (id, name, parent_id, type) VALUES (9, '云南', NULL, 'province');
INSERT INTO regions (id, name, parent_id, type) VALUES (10, '海南', NULL, 'province');
INSERT INTO regions (id, name, parent_id, type) VALUES (11, '福建', NULL, 'province');
INSERT INTO regions (id, name, parent_id, type) VALUES (12, '山东', NULL, 'province');
INSERT INTO regions (id, name, parent_id, type) VALUES (13, '重庆', NULL, 'province');
INSERT INTO regions (id, name, parent_id, type) VALUES (14, '天津', NULL, 'province');

-- ==================== 四川省城市 ====================
INSERT INTO regions (id, name, parent_id, type) VALUES (15, '成都市', 3, 'city');
INSERT INTO regions (id, name, parent_id, type) VALUES (16, '绵阳市', 3, 'city');
INSERT INTO regions (id, name, parent_id, type) VALUES (17, '乐山市', 3, 'city');
INSERT INTO regions (id, name, parent_id, type) VALUES (18, '九寨沟县', 3, 'city');

-- ==================== 浙江省城市 ====================
INSERT INTO regions (id, name, parent_id, type) VALUES (19, '杭州市', 4, 'city');
INSERT INTO regions (id, name, parent_id, type) VALUES (20, '宁波市', 4, 'city');
INSERT INTO regions (id, name, parent_id, type) VALUES (21, '温州市', 4, 'city');
INSERT INTO regions (id, name, parent_id, type) VALUES (22, '绍兴市', 4, 'city');
INSERT INTO regions (id, name, parent_id, type) VALUES (23, '嘉兴市', 4, 'city');

-- ==================== 广东省城市 ====================
INSERT INTO regions (id, name, parent_id, type) VALUES (24, '广州市', 5, 'city');
INSERT INTO regions (id, name, parent_id, type) VALUES (25, '深圳市', 5, 'city');
INSERT INTO regions (id, name, parent_id, type) VALUES (26, '珠海市', 5, 'city');
INSERT INTO regions (id, name, parent_id, type) VALUES (27, '佛山市', 5, 'city');
INSERT INTO regions (id, name, parent_id, type) VALUES (28, '东莞市', 5, 'city');

-- ==================== 江苏省城市 ====================
INSERT INTO regions (id, name, parent_id, type) VALUES (29, '南京市', 6, 'city');
INSERT INTO regions (id, name, parent_id, type) VALUES (30, '苏州市', 6, 'city');
INSERT INTO regions (id, name, parent_id, type) VALUES (31, '无锡市', 6, 'city');
INSERT INTO regions (id, name, parent_id, type) VALUES (32, '扬州市', 6, 'city');
INSERT INTO regions (id, name, parent_id, type) VALUES (33, '常州市', 6, 'city');

-- ==================== 湖北省城市 ====================
INSERT INTO regions (id, name, parent_id, type) VALUES (34, '武汉市', 7, 'city');
INSERT INTO regions (id, name, parent_id, type) VALUES (35, '宜昌市', 7, 'city');
INSERT INTO regions (id, name, parent_id, type) VALUES (36, '襄阳市', 7, 'city');

-- ==================== 陕西省城市 ====================
INSERT INTO regions (id, name, parent_id, type) VALUES (37, '西安市', 8, 'city');
INSERT INTO regions (id, name, parent_id, type) VALUES (38, '延安市', 8, 'city');
INSERT INTO regions (id, name, parent_id, type) VALUES (39, '咸阳市', 8, 'city');

-- ==================== 云南省城市 ====================
INSERT INTO regions (id, name, parent_id, type) VALUES (40, '昆明市', 9, 'city');
INSERT INTO regions (id, name, parent_id, type) VALUES (41, '大理市', 9, 'city');
INSERT INTO regions (id, name, parent_id, type) VALUES (42, '丽江市', 9, 'city');
INSERT INTO regions (id, name, parent_id, type) VALUES (43, '西双版纳', 9, 'city');

-- ==================== 海南省城市 ====================
INSERT INTO regions (id, name, parent_id, type) VALUES (44, '海口市', 10, 'city');
INSERT INTO regions (id, name, parent_id, type) VALUES (45, '三亚市', 10, 'city');

-- ==================== 福建省城市 ====================
INSERT INTO regions (id, name, parent_id, type) VALUES (46, '福州市', 11, 'city');
INSERT INTO regions (id, name, parent_id, type) VALUES (47, '厦门市', 11, 'city');
INSERT INTO regions (id, name, parent_id, type) VALUES (48, '泉州市', 11, 'city');

-- ==================== 山东省城市 ====================
INSERT INTO regions (id, name, parent_id, type) VALUES (49, '济南市', 12, 'city');
INSERT INTO regions (id, name, parent_id, type) VALUES (50, '青岛市', 12, 'city');
INSERT INTO regions (id, name, parent_id, type) VALUES (51, '烟台市', 12, 'city');
INSERT INTO regions (id, name, parent_id, type) VALUES (52, '威海市', 12, 'city');

-- 更新序列值
SELECT setval('regions_id_seq', 52, true);
