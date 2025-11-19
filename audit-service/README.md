# audit-service 审计日志服务

## 功能特性

- ✅ AOP自动记录审计日志
- ✅ 审计日志查询
- ✅ 用户行为追踪
- ✅ 统计分析
- ✅ 过期日志清理
- ✅ 异步保存日志

## 使用方法

### 1. 在需要审计的方法上添加注解

@Audited(action = "USER_LOGIN", resourceType = "USER", description = "用户登录")
public AuthResponse login(LoginRequest request) {
// 业务逻辑
}

text

### 2. 自动记录的信息

- 操作名称
- 资源类型和ID
- 用户ID
- IP地址
- User-Agent
- 请求方法和URI
- 请求参数
- 操作时间
- 状态码

## API接口

### 1. 查询审计日志列表
GET /api/audit/logs?page=0&size=20&userId=1&action=USER_LOGIN
Authorization: Bearer <admin-token>

text

### 2. 查询用户操作日志
GET /api/audit/logs/user/{userId}?page=0&size=20
Authorization: Bearer <admin-token>

text

### 3. 查询审计日志详情
GET /api/audit/logs/{logId}
Authorization: Bearer <admin-token>

text

### 4. 获取统计数据
GET /api/audit/statistics?startTime=2025-01-01T00:00:00&endTime=2025-12-31T23:59:59
Authorization: Bearer <admin-token>

text

### 5. 清理过期日志
DELETE /api/audit/logs/cleanup?daysToKeep=90
Authorization: Bearer <admin-token>

text

## 审计日志字段

| 字段 | 说明 |
|------|------|
| id | 日志ID |
| userId | 用户ID |
| action | 操作名称 |
| resourceType | 资源类型 |
| resourceId | 资源ID |
| oldValue | 旧值 |
| newValue | 新值 |
| ipAddress | IP地址 |
| userAgent | User-Agent |
| requestMethod | 请求方法 |
| requestUri | 请求URI |
| statusCode | 状态码 |
| createdAt | 创建时间 |

## 构建和运行

编译
mvn clean compile -pl audit-service

运行
mvn spring-boot:run -pl audit-service

打包
mvn clean package -pl audit-service -DskipTests

text

## 依赖服务

- PostgreSQL: 5432

## 定时清理

可以配置定时任务定期清理过期日志：
audit:
retention-days: 90 # 保留90天

text
undefined
11. 构建与测试
    构建命令
    bash
# 1. 编译
mvn clean compile -pl audit-service

# 2. 安装
mvn install -pl audit-service -DskipTests

# 3. 运行
mvn spring-boot:run -pl audit-service
测试审计功能
java
// 在任何Controller方法上添加@Audited注解
@PostMapping("/test")
@Audited(action = "TEST_ACTION", resourceType = "TEST", description = "测试操作")
public ResponseEntity<String> testAudit() {
return ResponseEntity.ok("审计测试");
}

// 访问该接口后，会自动记录审计日志