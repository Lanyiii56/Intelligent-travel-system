# 数据库初始化脚本说明

## 目录结构

```
mapper/
├── README.md                    # 本说明文件
├── spot/                        # 景点模块 (spot)
│   ├── regions_data.sql         # 地区表数据 (regions)
│   └── spots_data.sql           # 景点表数据 (spots)
└── food/                        # 美食模块 (food)
    └── foods_data.sql           # 美食表数据 (foods)
```

## 文件说明

| 文件 | 模块 | 数据库表 | 数据量 | 说明 |
|------|------|----------|--------|------|
| `spot/regions_data.sql` | spot | regions | 52 条 | 省份和城市数据 |
| `spot/spots_data.sql` | spot | spots | 126 条 | 景点数据 |
| `food/foods_data.sql` | food | foods | 115 条 | 美食数据 |

## 数据库信息

- **数据库名**: travel_db
- **数据库类型**: PostgreSQL
- **连接信息**: 见 `application.properties`

## 导入顺序

**必须按以下顺序导入**（因为存在外键依赖）：

1. `spot/regions_data.sql` - 地区数据（被 spots 和 foods 依赖）
2. `spot/spots_data.sql` - 景点数据
3. `food/foods_data.sql` - 美食数据

## 使用方法

### 方法一：Docker 环境（推荐）

```bash
# 进入项目根目录
cd Intelligent-travel-system

# 按顺序导入数据
docker exec -i travel-postgres psql -U postgres -d travel_db < backend/src/main/resources/mapper/spot/regions_data.sql
docker exec -i travel-postgres psql -U postgres -d travel_db < backend/src/main/resources/mapper/spot/spots_data.sql
docker exec -i travel-postgres psql -U postgres -d travel_db < backend/src/main/resources/mapper/food/foods_data.sql
```

### 方法二：一键导入脚本

```bash
# 进入项目根目录
cd Intelligent-travel-system

# 一键导入所有数据
for sql in backend/src/main/resources/mapper/spot/regions_data.sql \
           backend/src/main/resources/mapper/spot/spots_data.sql \
           backend/src/main/resources/mapper/food/foods_data.sql; do
    docker exec -i travel-postgres psql -U postgres -d travel_db < "$sql"
done
```

### 方法三：使用数据库管理工具

1. 打开 DBeaver、Navicat 或其他数据库管理工具
2. 连接到 PostgreSQL 数据库 (travel_db)
3. 按顺序执行以下文件：
   - `backend/src/main/resources/mapper/spot/regions_data.sql`
   - `backend/src/main/resources/mapper/spot/spots_data.sql`
   - `backend/src/main/resources/mapper/food/foods_data.sql`

## 清空数据重新导入

```bash
# 清空表数据（注意顺序，先删除有外键依赖的表）
docker exec -i travel-postgres psql -U postgres -d travel_db -c "
TRUNCATE TABLE foods, spots, regions RESTART IDENTITY CASCADE;
"

# 重新导入
for sql in backend/src/main/resources/mapper/spot/regions_data.sql \
           backend/src/main/resources/mapper/spot/spots_data.sql \
           backend/src/main/resources/mapper/food/foods_data.sql; do
    docker exec -i travel-postgres psql -U postgres -d travel_db < "$sql"
done
```

## 导出最新数据

如果修改了数据库数据，需要重新导出：

```bash
# 导出地区数据
docker exec -i travel-postgres pg_dump -U postgres -d travel_db \
  --data-only --inserts --column-inserts -t regions \
  > backend/src/main/resources/mapper/spot/regions_data.sql

# 导出景点数据
docker exec -i travel-postgres pg_dump -U postgres -d travel_db \
  --data-only --inserts --column-inserts -t spots \
  > backend/src/main/resources/mapper/spot/spots_data.sql

# 导出美食数据
docker exec -i travel-postgres pg_dump -U postgres -d travel_db \
  --data-only --inserts --column-inserts -t foods \
  > backend/src/main/resources/mapper/food/foods_data.sql
```

## 注意事项

1. **执行顺序**: 先启动后端服务让 JPA 自动创建表结构，再导入数据
2. **外键依赖**: regions 表必须先导入，因为 spots 和 foods 表依赖它
3. **重复执行**: 如果数据已存在，可能会报主键冲突错误，需要先清空表
