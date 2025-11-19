# detection-service 检测结果查询服务

## 功能特性

- ✅ 检测结果查询
- ✅ 任务状态查询
- ✅ 检测历史记录
- ✅ 统计数据分析
- ✅ Redis缓存加速

## API接口

### 1. 查询检测结果（按任务ID）
GET /api/detections/task/{taskId}
Authorization: Bearer <token>

text

### 2. 查询检测结果（按视频ID）
GET /api/detections/video/{videoId}
Authorization: Bearer <token>

text

### 3. 查询检测历史
GET /api/detections/history?page=0&size=10&result=FAKE
Authorization: Bearer <token>

text

### 4. 查询统计数据
GET /api/detections/statistics
Authorization: Bearer <token>

text

### 5. 查询任务状态
GET /api/tasks/{taskId}/status
Authorization: Bearer <token>

text

### 6. 查询任务列表
GET /api/tasks/my?page=0&size=10&status=COMPLETED
Authorization: Bearer <token>

text

### 7. 取消任务
POST /api/tasks/{taskId}/cancel
Authorization: Bearer <token>

text

## 构建和运行

编译
mvn clean compile -pl detection-service

运行
mvn spring-boot:run -pl detection-service

打包
mvn clean package -pl detection-service -DskipTests

text

## 依赖服务

- PostgreSQL: 5432
- Redis: 6379