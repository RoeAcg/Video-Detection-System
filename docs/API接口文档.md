# 视频检测系统 API 接口文档

## 目录
- [系统概述](#系统概述)
- [认证说明](#认证说明)
- [服务地址](#服务地址)
- [错误处理](#错误处理)
- [认证服务 API](#认证服务-api)
- [视频服务 API](#视频服务-api)
- [检测任务 API](#检测任务-api)
- [检测结果 API](#检测结果-api)
- [审计日志 API](#审计日志-api)
- [WebSocket API](#websocket-api)
- [接口索引](#接口索引)

---

## 系统概述

视频检测系统是一个基于微服务架构的视频内容真伪检测平台，采用Spring Boot + JWT认证 + Kafka消息队列技术栈。

### 核心功能
- 用户认证授权（注册、登录、JWT令牌管理）
- 视频文件上传（支持分块上传，最大2GB）
- 视频内容AI检测（异步处理）
- 检测结果查询与统计
- 审计日志记录
- 实时通知推送（WebSocket）

### 技术特性
- **认证方式**：JWT Bearer Token（24小时有效期，7天刷新期）
- **文件上传**：支持分块上传，文件类型限制（mp4, avi, mov, mkv, webm）
- **异步处理**：Kafka消息队列解耦
- **实时通信**：WebSocket推送通知
- **分页查询**：统一分页格式（page, size）
- **权限控制**：基于角色的访问控制（USER, ADMIN）

---

## 认证说明

### JWT Token 获取流程
1. 调用 `POST /api/auth/login` 获取accessToken
2. 在后续请求Header中携带：`Authorization: Bearer <token>`
3. Token过期后调用 `POST /api/auth/refresh` 刷新

### 权限角色
- **ROLE_USER**：普通用户，可访问视频、检测相关接口
- **ROLE_ADMIN**：管理员，可额外访问审计日志接口

### 公开接口（无需认证）
- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/refresh`

---

## 服务地址

| 服务 | 端口 | 地址 | 用途 |
|------|------|------|------|
| 认证服务 | 9001 | http://localhost:9001 | 用户认证 |
| 视频服务 | 9002 | http://localhost:9002 | 视频上传管理 |
| 检测服务 | 9004 | http://localhost:9004 | 检测任务查询 |
| 审计服务 | 9006 | http://localhost:9006 | 审计日志 |
| WebSocket | 9005 | ws://localhost:9005/ws | 实时通知 |

---

## 错误处理

### 标准错误响应格式
```json
{
  "timestamp": "2025-11-23T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "参数验证失败",
  "path": "/api/videos/upload"
}
```

### 常见错误码
| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 201 | 创建成功 |
| 400 | 请求参数错误 |
| 401 | 未认证或Token无效 |
| 403 | 无权限访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 认证服务 API

### 基础路径：`http://localhost:9001/api/auth`

#### 1. 用户注册
**接口地址：** `POST /register`

**功能描述：** 创建新用户账户

**认证要求：** 否

**请求参数（Body）：**
| 参数名 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| username | string | 是 | 用户名（3-50字符，支持字母数字下划线） | "user123" |
| email | string | 是 | 邮箱地址 | "user@example.com" |
| password | string | 是 | 密码（8-64字符） | "password123" |

**请求示例：**
```json
{
  "username": "user123",
  "email": "user@example.com",
  "password": "password123"
}
```

**响应数据（200）：**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "userId": 123,
  "username": "user123",
  "email": "user@example.com",
  "roles": ["ROLE_USER"]
}
```

**前端调用示例：**
```javascript
const response = await axios.post('http://localhost:9001/api/auth/register', {
  username: 'user123',
  email: 'user@example.com',
  password: 'password123'
});
```

#### 2. 用户登录
**接口地址：** `POST /login`

**功能描述：** 用户登录获取JWT令牌

**认证要求：** 否

**请求参数（Body）：**
| 参数名 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| username | string | 是 | 用户名或邮箱 | "user123" |
| password | string | 是 | 密码 | "password123" |

**请求示例：**
```json
{
  "username": "user123",
  "password": "password123"
}
```

**响应数据（200）：**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "userId": 123,
  "username": "user123",
  "email": "user@example.com",
  "roles": ["ROLE_USER"]
}
```

#### 3. 刷新令牌
**接口地址：** `POST /refresh`

**功能描述：** 使用refresh token获取新的access token

**认证要求：** 否

**请求参数（Headers）：**
| 参数名 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| Authorization | string | 是 | Bearer刷新令牌 | "Bearer eyJhbGciOiJIUzI1NiJ9..." |

**响应数据（200）：**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "userId": 123,
  "username": "user123",
  "email": "user@example.com",
  "roles": ["ROLE_USER"]
}
```

#### 4. 用户登出
**接口地址：** `POST /logout`

**功能描述：** 使当前令牌失效

**认证要求：** 是

**请求参数（Headers）：**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| Authorization | string | 是 | Bearer访问令牌 |

**响应数据（200）：**
```json
"登出成功"
```

#### 5. 获取当前用户信息
**接口地址：** `GET /me`

**功能描述：** 获取当前登录用户的详细信息

**认证要求：** 是

**响应数据（200）：**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "userId": 123,
  "username": "user123",
  "email": "user@example.com",
  "roles": ["ROLE_USER"]
}
```

---

## 视频服务 API

### 基础路径：`http://localhost:9002/api/videos`

#### 1. 上传视频（小文件）
**接口地址：** `POST /upload`

**功能描述：** 直接上传小于2GB的视频文件（推荐用于小于100MB的文件）

**认证要求：** 是

**请求参数（FormData）：**
| 参数名 | 位置 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|------|
| file | body | MultipartFile | 是 | 视频文件 | 文件对象 |
| description | body | string | 否 | 视频描述 | "测试视频" |

**请求示例：**
```javascript
const formData = new FormData();
formData.append('file', videoFile);
formData.append('description', '测试视频');

const response = await axios.post(
  'http://localhost:9002/api/videos/upload',
  formData,
  {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'multipart/form-data'
    }
  }
);
```

**响应数据（201）：**
```json
{
  "taskId": "550e8400-e29b-41d4-a716-446655440000",
  "uploadStatus": "UPLOADED",
  "uploadProgress": 100,
  "estimatedTime": "10s",
  "createdAt": "2025-11-23T10:30:00"
}
```

#### 2. 分块上传 - 初始化
**接口地址：** `POST /upload/init`

**功能描述：** 初始化大文件分块上传，返回fileId

**认证要求：** 是

**请求参数（Query）：**
| 参数名 | 位置 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|------|
| fileName | query | string | 是 | 文件名 | "video.mp4" |
| fileSize | query | number | 是 | 文件大小（字节） | 2147483648 |
| totalChunks | query | number | 是 | 分块总数 | 100 |

**响应数据（200）：**
```
"550e8400-e29b-41d4-a716-446655440000"
```

#### 3. 分块上传 - 上传分块
**接口地址：** `POST /upload/chunk`

**功能描述：** 上传单个分块

**认证要求：** 是

**请求参数（FormData）：**
| 参数名 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| fileId | string | 是 | 初始化返回的文件ID | "550e8400..." |
| chunkIndex | number | 是 | 分块索引（从0开始） | 0 |
| file | MultipartFile | 是 | 分块文件数据 | 分块数据 |

**响应数据（200）：**
```json
"分块上传成功"
```

#### 4. 分块上传 - 完成上传
**接口地址：** `POST /upload/complete`

**功能描述：** 合并所有分块，完成上传

**认证要求：** 是

**请求参数（FormData）：**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| fileId | string | 是 | 初始化返回的文件ID |
| description | string | 否 | 视频描述 |

**响应数据（200）：**
```json
{
  "taskId": "550e8400-e29b-41d4-a716-446655440000",
  "uploadStatus": "COMPLETED",
  "uploadProgress": 100,
  "estimatedTime": "10s",
  "createdAt": "2025-11-23T10:30:00"
}
```

#### 5. 获取视频详情
**接口地址：** `GET /{videoId}`

**功能描述：** 根据视频ID获取视频详细信息

**认证要求：** 是

**路径参数：**
| 参数名 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| videoId | number | 是 | 视频ID | 123 |

**响应数据（200）：**
```json
{
  "id": 123,
  "userId": 456,
  "fileName": "test.mp4",
  "fileHash": "a665a45920422f9d417e4867efdc4fb8...",
  "filePath": "/uploads/123/test.mp4",
  "fileSize": 10485760,
  "mimeType": "video/mp4",
  "durationSeconds": 120,
  "description": "测试视频",
  "createdAt": "2025-11-23T10:30:00",
  "updatedAt": "2025-11-23T10:30:00"
}
```

#### 6. 获取用户视频列表
**接口地址：** `GET /my`

**功能描述：** 获取当前用户的视频列表（分页）

**认证要求：** 是

**查询参数：**
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| page | number | 否 | 0 | 页码（从0开始） |
| size | number | 否 | 10 | 每页大小 |

**响应数据（200）：**
```json
{
  "content": [
    {
      "id": 123,
      "fileName": "test.mp4",
      "fileSize": 10485760,
      "durationSeconds": 120,
      "description": "测试视频",
      "createdAt": "2025-11-23T10:30:00"
    }
  ],
  "page": 0,
  "size": 10,
  "totalPages": 5,
  "totalElements": 50,
  "first": true,
  "last": false
}
```

#### 7. 删除视频
**接口地址：** `DELETE /{videoId}`

**功能描述：** 删除指定视频（仅视频所有者）

**认证要求：** 是

**路径参数：**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| videoId | number | 是 | 视频ID |

**响应数据（200）：**
```json
"视频删除成功"
```

---

## 检测任务 API

### 基础路径：`http://localhost:9004/api/tasks`

#### 1. 获取任务状态
**接口地址：** `GET /{taskId}/status`

**功能描述：** 查询检测任务的实时状态

**认证要求：** 是

**路径参数：**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| taskId | string | 是 | 任务ID（UUID） |

**响应数据（200）：**
```json
{
  "taskId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "PROCESSING",
  "progress": 45,
  "message": "正在分析视频...",
  "estimatedTimeRemaining": 30,
  "result": null
}
```

**状态说明：**
- `PENDING`：待处理
- `PROCESSING`：处理中
- `COMPLETED`：已完成
- `FAILED`：失败

#### 2. 获取任务详情
**接口地址：** `GET /{taskId}`

**功能描述：** 获取检测任务的完整信息

**认证要求：** 是

**路径参数：**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| taskId | string | 是 | 任务ID |

**响应数据（200）：**
```json
{
  "id": 1,
  "taskId": "550e8400-e29b-41d4-a716-446655440000",
  "videoId": 123,
  "userId": 456,
  "status": "COMPLETED",
  "progress": 100,
  "estimatedTimeSeconds": 60,
  "startedAt": "2025-11-23T10:30:00",
  "completedAt": "2025-11-23T10:31:00",
  "errorMessage": null,
  "retryCount": 0,
  "createdAt": "2025-11-23T10:30:00",
  "updatedAt": "2025-11-23T10:31:00"
}
```

#### 3. 获取用户任务列表
**接口地址：** `GET /my`

**功能描述：** 获取当前用户的检测任务列表（分页）

**认证要求：** 是

**查询参数：**
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| page | number | 否 | 0 | 页码 |
| size | number | 否 | 10 | 每页大小 |
| status | string | 否 | - | 按状态过滤（PENDING/PROCESSING/COMPLETED/FAILED） |

**响应数据（200）：**
```json
{
  "content": [
    {
      "id": 1,
      "taskId": "550e8400-e29b-41d4-a716-446655440000",
      "videoId": 123,
      "status": "COMPLETED",
      "progress": 100,
      "startedAt": "2025-11-23T10:30:00",
      "completedAt": "2025-11-23T10:31:00"
    }
  ],
  "page": 0,
  "size": 10,
  "totalPages": 5,
  "totalElements": 50,
  "first": true,
  "last": false
}
```

#### 4. 取消任务
**接口地址：** `POST /{taskId}/cancel`

**功能描述：** 取消正在处理的任务

**认证要求：** 是

**路径参数：**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| taskId | string | 是 | 任务ID |

**响应数据（200）：**
```json
"任务已取消"
```

---

## 检测结果 API

### 基础路径：`http://localhost:9004/api/detections`

#### 1. 根据任务ID获取检测结果
**接口地址：** `GET /task/{taskId}`

**功能描述：** 根据任务ID获取完整的检测结果

**认证要求：** 是

**路径参数：**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| taskId | string | 是 | 任务ID |

**响应数据（200）：**
```json
{
  "detectionId": 789,
  "videoId": 123,
  "fileName": "test.mp4",
  "result": "FAKE",
  "confidence": 0.95,
  "modelVersion": "v2.1.0",
  "processingTimeMs": 45000,
  "framesAnalyzed": 1800,
  "artifacts": [
    "temporal_inconsistency",
    " unnatural_blinking"
  ],
  "features": {
    "face_quality": 0.85,
    "pose_stability": 0.92
  },
  "createdAt": "2025-11-23T10:31:00"
}
```

**检测结果说明：**
- `AUTHENTIC`：真实视频
- `FAKE`：伪造视频
- `UNCERTAIN`：不确定

#### 2. 根据视频ID获取检测结果
**接口地址：** `GET /video/{videoId}`

**功能描述：** 根据视频ID获取最新的检测结果

**认证要求：** 是

**路径参数：**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| videoId | number | 是 | 视频ID |

**响应数据（200）：**
```json
{
  "detectionId": 789,
  "videoId": 123,
  "fileName": "test.mp4",
  "result": "FAKE",
  "confidence": 0.95,
  "modelVersion": "v2.1.0",
  "processingTimeMs": 45000,
  "framesAnalyzed": 1800,
  "artifacts": [
    "temporal_inconsistency",
    "unnatural_blinking"
  ],
  "features": {
    "face_quality": 0.85,
    "pose_stability": 0.92
  },
  "createdAt": "2025-11-23T10:31:00"
}
```

#### 3. 获取检测结果详情
**接口地址：** `GET /{detectionId}`

**功能描述：** 根据检测ID获取详细的检测信息

**认证要求：** 是

**路径参数：**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| detectionId | number | 是 | 检测记录ID |

**响应数据（200）：**
```json
{
  "id": 789,
  "taskId": "550e8400-e29b-41d4-a716-446655440000",
  "videoId": 123,
  "userId": 456,
  "prediction": "FAKE",
  "confidence": 0.95,
  "modelVersion": "v2.1.0",
  "processingTimeMs": 45000,
  "framesAnalyzed": 1800,
  "features": {
    "face_quality": 0.85,
    "pose_stability": 0.92
  },
  "artifactsDetected": "temporal_inconsistency,unnatural_blinking",
  "createdAt": "2025-11-23T10:31:00",
  "updatedAt": "2025-11-23T10:31:00"
}
```

#### 4. 获取检测历史
**接口地址：** `GET /history`

**功能描述：** 获取当前用户的检测历史记录（分页）

**认证要求：** 是

**查询参数：**
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| page | number | 否 | 0 | 页码 |
| size | number | 否 | 10 | 每页大小 |
| result | string | 否 | - | 按结果过滤（AUTHENTIC/FAKE/UNCERTAIN） |

**响应数据（200）：**
```json
{
  "content": [
    {
      "id": 789,
      "videoId": 123,
      "prediction": "FAKE",
      "confidence": 0.95,
      "modelVersion": "v2.1.0",
      "processingTimeMs": 45000,
      "framesAnalyzed": 1800,
      "createdAt": "2025-11-23T10:31:00"
    }
  ],
  "page": 0,
  "size": 10,
  "totalPages": 5,
  "totalElements": 50,
  "first": true,
  "last": false
}
```

#### 5. 获取统计数据
**接口地址：** `GET /statistics`

**功能描述：** 获取当前用户的检测统计信息

**认证要求：** 是

**响应数据（200）：**
```json
{
  "totalDetections": 100,
  "authenticCount": 60,
  "fakeCount": 30,
  "uncertainCount": 10,
  "successRate": 0.90,
  "avgProcessingTime": 45000,
  "thisWeekCount": 15,
  "thisMonthCount": 60
}
```

---

## 审计日志 API

### 基础路径：`http://localhost:9006/api/audit`

**[安全提示]** 此模块需要管理员权限（ROLE_ADMIN）

#### 1. 查询审计日志列表
**接口地址：** `GET /logs`

**功能描述：** 查询系统审计日志（管理员权限）

**认证要求：** 是（需ADMIN角色）

**查询参数：**
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| page | number | 否 | 0 | 页码 |
| size | number | 否 | 20 | 每页大小 |
| userId | number | 否 | - | 按用户ID过滤 |
| action | string | 否 | - | 按操作类型过滤 |
| resourceType | string | 否 | - | 按资源类型过滤 |
| startTime | datetime | 否 | - | 开始时间（ISO 8601格式） |
| endTime | datetime | 否 | - | 结束时间（ISO 8601格式） |

**响应数据（200）：**
```json
{
  "content": [
    {
      "id": 1,
      "userId": 456,
      "action": "VIDEO_UPLOAD",
      "resourceType": "VIDEO",
      "resourceId": 123,
      "oldValue": null,
      "newValue": "Uploaded video: test.mp4",
      "ipAddress": "192.168.1.100",
      "userAgent": "Mozilla/5.0...",
      "requestMethod": "POST",
      "requestUri": "/api/videos/upload",
      "statusCode": 201,
      "createdAt": "2025-11-23T10:30:00"
    }
  ],
  "page": 0,
  "size": 20,
  "totalPages": 100,
  "totalElements": 2000,
  "first": true,
  "last": false
}
```

#### 2. 查询我的审计日志
**接口地址：** `GET /my-logs`

**功能描述：** 普通用户查询自己的操作日志

**认证要求：** 是

**查询参数：**
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| page | number | 否 | 0 | 页码 |
| size | number | 否 | 20 | 每页大小 |

**响应数据（200）：**
（同上格式，仅返回当前用户的日志）

#### 3. 查询用户操作日志
**接口地址：** `GET /logs/user/{userId}`

**功能描述：** 管理员查询指定用户的操作日志

**认证要求：** 是（需ADMIN角色）

**路径参数：**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | number | 是 | 用户ID |

**查询参数：**
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| page | number | 否 | 0 | 页码 |
| size | number | 否 | 20 | 每页大小 |

#### 4. 查询审计日志详情
**接口地址：** `GET /logs/{logId}`

**功能描述：** 根据日志ID获取审计日志详情

**认证要求：** 是（需ADMIN角色）

**路径参数：**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| logId | number | 是 | 日志ID |

#### 5. 获取审计统计数据
**接口地址：** `GET /statistics`

**功能描述：** 获取审计日志统计信息

**认证要求：** 是（需ADMIN角色）

**查询参数：**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| startTime | datetime | 否 | 开始时间 |
| endTime | datetime | 否 | 结束时间 |

#### 6. 清理过期审计日志
**接口地址：** `DELETE /logs/cleanup`

**功能描述：** 删除指定天数之前的审计日志

**认证要求：** 是（需ADMIN角色）

**查询参数：**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| daysToKeep | number | 是 | 保留天数（删除早于此天数的日志） |

**响应数据（200）：**
```json
"已删除 150 条过期审计日志"
```

---

## WebSocket API

### 连接地址：`ws://localhost:9005/ws`

### 功能描述
实时接收检测任务状态更新和通知消息

### 连接参数
| 参数名 | 位置 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| token | query | 是 | JWT令牌 | token=eyJhbGciOiJIUzI1NiJ9... |

**完整连接地址示例：**
```
ws://localhost:9005/ws?token=eyJhbGciOiJIUzI1NiJ9...
```

### 连接示例
```javascript
const token = 'your-jwt-token';
const ws = new WebSocket(`ws://localhost:9005/ws?token=${token}`);

ws.onopen = function(event) {
  console.log('WebSocket连接已建立');
};

ws.onmessage = function(event) {
  const notification = JSON.parse(event.data);
  console.log('收到通知:', notification);
};

ws.onerror = function(error) {
  console.error('WebSocket错误:', error);
};

ws.onclose = function(event) {
  console.log('WebSocket连接已关闭');
};
```

### 消息格式

#### 1. 任务状态更新
```json
{
  "type": "TASK_STATUS",
  "taskId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "PROCESSING",
  "progress": 45,
  "message": "正在分析视频...",
  "timestamp": "2025-11-23T10:30:00"
}
```

#### 2. 任务完成通知
```json
{
  "type": "TASK_COMPLETED",
  "taskId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "COMPLETED",
  "result": {
    "detectionId": 789,
    "videoId": 123,
    "prediction": "FAKE",
    "confidence": 0.95
  },
  "timestamp": "2025-11-23T10:31:00"
}
```

#### 3. 错误通知
```json
{
  "type": "ERROR",
  "taskId": "550e8400-e29b-41d4-a716-446655440000",
  "error": "检测失败",
  "message": "AI服务不可用",
  "timestamp": "2025-11-23T10:30:00"
}
```

### 消息类型说明
| 类型 | 说明 |
|------|------|
| TASK_STATUS | 任务状态更新 |
| TASK_COMPLETED | 任务完成 |
| TASK_FAILED | 任务失败 |
| ERROR | 系统错误通知 |
| PING | 心跳检测 |
| PONG | 心跳响应 |

### 心跳机制
客户端每30秒发送一次PING消息：
```json
{
  "type": "PING",
  "timestamp": "2025-11-23T10:30:00"
}
```

服务器响应PONG：
```json
{
  "type": "PONG",
  "timestamp": "2025-11-23T10:30:00"
}
```

---

## 接口索引

### 认证相关
- [用户注册](#1-用户注册) - `POST /api/auth/register`
- [用户登录](#2-用户登录) - `POST /api/auth/login`
- [刷新令牌](#3-刷新令牌) - `POST /api/auth/refresh`
- [用户登出](#4-用户登出) - `POST /api/auth/logout`
- [获取用户信息](#5-获取当前用户信息) - `GET /api/auth/me`

### 视频管理
- [小文件上传](#1-上传视频小文件) - `POST /api/videos/upload`
- [分块上传初始化](#2-分块上传---初始化) - `POST /api/videos/upload/init`
- [上传分块](#3-分块上传---上传分块) - `POST /api/videos/upload/chunk`
- [完成分块上传](#4-分块上传---完成上传) - `POST /api/videos/upload/complete`
- [获取视频详情](#5-获取视频详情) - `GET /api/videos/{videoId}`
- [获取视频列表](#6-获取用户视频列表) - `GET /api/videos/my`
- [删除视频](#7-删除视频) - `DELETE /api/videos/{videoId}`

### 检测任务
- [获取任务状态](#1-获取任务状态) - `GET /api/tasks/{taskId}/status`
- [获取任务详情](#2-获取任务详情) - `GET /api/tasks/{taskId}`
- [获取任务列表](#3-获取用户任务列表) - `GET /api/tasks/my`
- [取消任务](#4-取消任务) - `POST /api/tasks/{taskId}/cancel`

### 检测结果
- [按任务查询结果](#1-根据任务id获取检测结果) - `GET /api/detections/task/{taskId}`
- [按视频查询结果](#2-根据视频id获取检测结果) - `GET /api/detections/video/{videoId}`
- [获取结果详情](#3-获取检测结果详情) - `GET /api/detections/{detectionId}`
- [获取检测历史](#4-获取检测历史) - `GET /api/detections/history`
- [获取统计数据](#5-获取统计数据) - `GET /api/detections/statistics`

### 审计日志（需ADMIN权限）
- [查询审计日志](#1-查询审计日志列表) - `GET /api/audit/logs`
- [查询我的日志](#2-查询我的审计日志) - `GET /api/audit/my-logs`
- [查询用户日志](#3-查询用户操作日志) - `GET /api/audit/logs/user/{userId}`
- [查询日志详情](#4-查询审计日志详情) - `GET /api/audit/logs/{logId}`
- [审计统计](#5-获取审计统计数据) - `GET /api/audit/statistics`
- [清理日志](#6-清理过期审计日志) - `DELETE /api/audit/logs/cleanup`

### 实时通信
- [WebSocket连接](#websocket-api) - `ws://localhost:9005/ws`

---

## 附录

### 状态码枚举

#### TaskStatus（任务状态）
- `PENDING`：待处理
- `PROCESSING`：处理中
- `COMPLETED`：已完成
- `FAILED`：失败

#### DetectionResultEnum（检测结果）
- `AUTHENTIC`：真实视频
- `FAKE`：伪造视频
- `UNCERTAIN`：不确定

### 分页参数说明
- **page**：页码，从0开始计数
- **size**：每页大小，建议不超过100
- **响应包含**：content（数据列表）、page（当前页）、size（页大小）、totalPages（总页数）、totalElements（总记录数）、first（是否第一页）、last（是否最后一页）

### 常见伪造特征（artifacts）
- `temporal_inconsistency`：时间不一致
- `unnatural_blinking`：不自然的眨眼
- `lip_sync_error`：唇同步错误
- `facial_distortion`：面部扭曲
- `pose_inconsistency`：姿态不一致

---

**文档版本：** v1.0
**更新时间：** 2025-11-23
**系统版本：** video-detection-system v1.0.0
本文档最后更新时间：2025-12-25
