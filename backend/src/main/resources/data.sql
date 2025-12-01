-- 地区数据
INSERT INTO regions (name, parent_id, type) VALUES ('北京市', NULL, 'province') ON CONFLICT DO NOTHING;
INSERT INTO regions (name, parent_id, type) VALUES ('东城区', 1, 'city') ON CONFLICT DO NOTHING;
INSERT INTO regions (name, parent_id, type) VALUES ('海淀区', 1, 'city') ON CONFLICT DO NOTHING;
INSERT INTO regions (name, parent_id, type) VALUES ('朝阳区', 1, 'city') ON CONFLICT DO NOTHING;
INSERT INTO regions (name, parent_id, type) VALUES ('上海市', NULL, 'province') ON CONFLICT DO NOTHING;
INSERT INTO regions (name, parent_id, type) VALUES ('黄浦区', 5, 'city') ON CONFLICT DO NOTHING;
INSERT INTO regions (name, parent_id, type) VALUES ('浦东新区', 5, 'city') ON CONFLICT DO NOTHING;
INSERT INTO regions (name, parent_id, type) VALUES ('四川省', NULL, 'province') ON CONFLICT DO NOTHING;
INSERT INTO regions (name, parent_id, type) VALUES ('成都市', 8, 'city') ON CONFLICT DO NOTHING;
INSERT INTO regions (name, parent_id, type) VALUES ('浙江省', NULL, 'province') ON CONFLICT DO NOTHING;
INSERT INTO regions (name, parent_id, type) VALUES ('杭州市', 10, 'city') ON CONFLICT DO NOTHING;

-- 北京景点
INSERT INTO spots (name, description, region_id, price_min, price_max, open_time, play_time, age_min, age_max, latitude, longitude, image_url)
VALUES ('故宫博物院', '中国明清两代的皇家宫殿，世界上现存规模最大、保存最为完整的木质结构古建筑之一。', 1, 60, 60, '08:30-17:00', 180, 6, 80, 39.9163, 116.3972, 'https://images.unsplash.com/photo-1584450150050-4b9bdbd51f68?w=400')
ON CONFLICT DO NOTHING;

INSERT INTO spots (name, description, region_id, price_min, price_max, open_time, play_time, age_min, age_max, latitude, longitude, image_url)
VALUES ('天安门广场', '世界上最大的城市广场之一，是中华人民共和国的象征。', 1, 0, 0, '全天开放', 60, 1, 100, 39.9055, 116.3976, 'https://images.unsplash.com/photo-1508804185872-d7badad00f7d?w=400')
ON CONFLICT DO NOTHING;

INSERT INTO spots (name, description, region_id, price_min, price_max, open_time, play_time, age_min, age_max, latitude, longitude, image_url)
VALUES ('颐和园', '中国清朝时期皇家园林，前身为清漪园，是保存最完整的一座皇家行宫御苑。', 1, 30, 30, '06:30-18:00', 240, 5, 85, 39.9999, 116.2755, 'https://images.unsplash.com/photo-1547981609-4b6bfe67ca0b?w=400')
ON CONFLICT DO NOTHING;

INSERT INTO spots (name, description, region_id, price_min, price_max, open_time, play_time, age_min, age_max, latitude, longitude, image_url)
VALUES ('八达岭长城', '中国古代伟大的防御工程万里长城的重要组成部分，是明长城的一个隘口。', 1, 40, 40, '06:30-19:00', 300, 10, 65, 40.3598, 116.0201, 'https://images.unsplash.com/photo-1508804052814-cd3ba865a116?w=400')
ON CONFLICT DO NOTHING;

INSERT INTO spots (name, description, region_id, price_min, price_max, open_time, play_time, age_min, age_max, latitude, longitude, image_url)
VALUES ('北京环球度假区', '全球第五座环球影城主题乐园，拥有七大主题景区。', 1, 528, 748, '09:00-21:00', 480, 4, 60, 39.8433, 116.6781, 'https://images.unsplash.com/photo-1536086845234-f8f5f5f5f5f5?w=400')
ON CONFLICT DO NOTHING;

INSERT INTO spots (name, description, region_id, price_min, price_max, open_time, play_time, age_min, age_max, latitude, longitude, image_url)
VALUES ('北京动物园', '中国开放最早、动物种类最多的动物园之一，有大熊猫、金丝猴等珍稀动物。', 1, 15, 15, '07:30-18:00', 180, 3, 70, 39.9399, 116.3383, 'https://images.unsplash.com/photo-1474511320723-9a56873571b7?w=400')
ON CONFLICT DO NOTHING;

-- 上海景点
INSERT INTO spots (name, description, region_id, price_min, price_max, open_time, play_time, age_min, age_max, latitude, longitude, image_url)
VALUES ('外滩', '上海的标志性景观，矗立着52幢风格迥异的古典复兴大楼，素有外滩万国建筑博览群之称。', 5, 0, 0, '全天开放', 90, 1, 100, 31.2400, 121.4900, 'https://images.unsplash.com/photo-1545893835-abaa50cbe628?w=400')
ON CONFLICT DO NOTHING;

INSERT INTO spots (name, description, region_id, price_min, price_max, open_time, play_time, age_min, age_max, latitude, longitude, image_url)
VALUES ('东方明珠', '上海的标志性文化景观之一，塔高约468米，是亚洲第四高塔。', 5, 180, 220, '08:00-21:30', 120, 4, 75, 31.2397, 121.4998, 'https://images.unsplash.com/photo-1548919973-5cef591cdbc9?w=400')
ON CONFLICT DO NOTHING;

INSERT INTO spots (name, description, region_id, price_min, price_max, open_time, play_time, age_min, age_max, latitude, longitude, image_url)
VALUES ('上海迪士尼乐园', '中国内地首座迪士尼主题乐园，拥有七大主题园区。', 5, 435, 769, '08:30-20:30', 600, 3, 65, 31.1434, 121.6580, 'https://images.unsplash.com/photo-1597466599360-3b9775841aec?w=400')
ON CONFLICT DO NOTHING;

INSERT INTO spots (name, description, region_id, price_min, price_max, open_time, play_time, age_min, age_max, latitude, longitude, image_url)
VALUES ('豫园', '上海著名的古典园林，始建于明代，是江南古典园林的代表。', 5, 40, 40, '08:30-17:00', 120, 6, 85, 31.2273, 121.4924, 'https://images.unsplash.com/photo-1577086664693-894d8c895ba2?w=400')
ON CONFLICT DO NOTHING;

-- 成都景点
INSERT INTO spots (name, description, region_id, price_min, price_max, open_time, play_time, age_min, age_max, latitude, longitude, image_url)
VALUES ('成都大熊猫繁育研究基地', '世界著名的大熊猫迁地保护基地，可近距离观看大熊猫。', 8, 55, 55, '07:30-18:00', 240, 3, 80, 30.7328, 104.1467, 'https://images.unsplash.com/photo-1564349683136-77e08dba1ef7?w=400')
ON CONFLICT DO NOTHING;

INSERT INTO spots (name, description, region_id, price_min, price_max, open_time, play_time, age_min, age_max, latitude, longitude, image_url)
VALUES ('宽窄巷子', '成都遗留下来的较成规模的清朝古街道，由宽巷子、窄巷子和井巷子组成。', 8, 0, 0, '全天开放', 150, 1, 100, 30.6696, 104.0554, 'https://images.unsplash.com/photo-1590559899731-a382839e5549?w=400')
ON CONFLICT DO NOTHING;

INSERT INTO spots (name, description, region_id, price_min, price_max, open_time, play_time, age_min, age_max, latitude, longitude, image_url)
VALUES ('锦里古街', '成都武侯祠博物馆的一部分，传说中离浪漫最近的地方。', 8, 0, 0, '全天开放', 120, 1, 100, 30.6455, 104.0489, 'https://images.unsplash.com/photo-1590559899731-a382839e5549?w=400')
ON CONFLICT DO NOTHING;

INSERT INTO spots (name, description, region_id, price_min, price_max, open_time, play_time, age_min, age_max, latitude, longitude, image_url)
VALUES ('都江堰', '世界文化遗产，世界自然遗产，是全世界迄今为止年代最久的大型水利工程。', 8, 80, 80, '08:00-18:00', 240, 8, 75, 31.0049, 103.6100, 'https://images.unsplash.com/photo-1569839333583-7375336cde4b?w=400')
ON CONFLICT DO NOTHING;

-- 杭州景点
INSERT INTO spots (name, description, region_id, price_min, price_max, open_time, play_time, age_min, age_max, latitude, longitude, image_url)
VALUES ('西湖', '中国大陆首批国家重点风景名胜区和中国十大风景名胜之一。', 10, 0, 0, '全天开放', 300, 1, 100, 30.2590, 120.1388, 'https://images.unsplash.com/photo-1599571234909-29ed5d1321d6?w=400')
ON CONFLICT DO NOTHING;

INSERT INTO spots (name, description, region_id, price_min, price_max, open_time, play_time, age_min, age_max, latitude, longitude, image_url)
VALUES ('灵隐寺', '中国佛教古寺，又名云林寺，始建于东晋咸和元年。', 10, 75, 75, '07:00-18:00', 150, 6, 85, 30.2408, 120.0993, 'https://images.unsplash.com/photo-1598887142487-3c854d51eabb?w=400')
ON CONFLICT DO NOTHING;

INSERT INTO spots (name, description, region_id, price_min, price_max, open_time, play_time, age_min, age_max, latitude, longitude, image_url)
VALUES ('宋城', '大型宋代文化主题乐园，以《宋城千古情》演出闻名。', 10, 310, 310, '10:00-21:00', 300, 5, 70, 30.1833, 120.1167, 'https://images.unsplash.com/photo-1583417319070-4a69db38a482?w=400')
ON CONFLICT DO NOTHING;

INSERT INTO spots (name, description, region_id, price_min, price_max, open_time, play_time, age_min, age_max, latitude, longitude, image_url)
VALUES ('千岛湖', '国家5A级旅游景区，因湖内拥有1078座翠岛而得名。', 10, 150, 150, '08:00-17:00', 480, 6, 75, 29.6044, 118.9578, 'https://images.unsplash.com/photo-1501785888041-af3ef285b470?w=400')
ON CONFLICT DO NOTHING;
