# video-service 视频管理服务

## 功能特性

- ✅ 视频文件上传（小文件直接上传）
- ✅ 大文件分块上传
- ✅ SHA-256哈希去重
- ✅ 文件存储管理
- ✅ 视频元数据管理
- ✅ 与Kafka集成（发送检测任务）

## API接口

### 1. 上传视频（小文件）
```html
POST /api/videos/upload
Content-Type: multipart/form-data
Authorization: Bearer <token>

file: <video-file>
description: "视频描述"
```

### 2. 分块上传 - 初始化
```html
POST /api/videos/upload/init
Authorization: Bearer <token>

fileName=video.mp4&fileSize=1073741824&totalChunks=200
```

### 3. 分块上传 - 上传分块
```html
POST /api/videos/upload/chunk
Content-Type: multipart/form-data
Authorization: Bearer <token>

fileId=<uuid>&chunkIndex=0&file=<chunk-data>
```

### 4. 分块上传 - 完成

```html
POST /api/videos/upload/complete
Authorization: Bearer <token>

fileId=<uuid>&description=描述
```

### 5. 获取视频详情
```html
GET /api/videos/{videoId}
Authorization: Bearer <token>
```

### 6. 获取我的视频列表
```html
GET /api/videos/my?page=0&size=10
Authorization: Bearer <token>
```

### 7. 删除视频
```html
DELETE /api/videos/{videoId}
Authorization: Bearer <token>
```

## 构建和运行

编译
`mvn clean compile -pl video-service`

运行
`mvn spring-boot:run -pl video-service`

打包
`mvn clean package -pl video-service -DskipTests`

## 文件存储

- 上传目录: `./uploads`
- 分块目录: `./chunks`
- 最大文件: 2GB

## 依赖服务

- PostgreSQL: 5432
- Kafka: 9092

## 构建与测试

### 构建命令

```bash
# 1. 编译
mvn clean compile -pl video-service

# 2. 安装
mvn install -pl video-service -DskipTests

# 3. 运行
mvn spring-boot:run -pl video-service
```

### 测试上传

```bash
# 上传视频
curl -X POST http://localhost:8082/api/videos/upload \
  -H "Authorization: Bearer <token>" \
  -F "file=@test.mp4" \
  -F "description=测试视频"
```
