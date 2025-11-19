# auth-service 认证服务

## 功能特性

- ✅ 用户注册和登录
- ✅ JWT令牌生成和验证
- ✅ Spring Security安全认证
- ✅ RBAC角色权限控制
- ✅ 密码加密（BCrypt）
- ✅ 令牌刷新机制

## API接口

### 1. 用户注册
```http
POST /api/auth/register
Content-Type: application/json

{
"username": "john",
"email": "john@example.com",
"password": "Password123"
}
```

### 2. 用户登录

```http
POST /api/auth/login
Content-Type: application/json

{
"username": "john",
"password": "Password123"
}
```

### 3. 获取当前用户
```http
GET /api/auth/me
Authorization: Bearer <token>
```

### 4. 刷新令牌
```http
POST /api/auth/refresh
Authorization: Bearer <old-token>
```

### 5. 登出
```http
POST /api/auth/logout
Authorization: Bearer <token>
```

## 构建和运行

编译
mvn clean compile -pl auth-service

运行
mvn spring-boot:run -pl auth-service

打包
mvn clean package -pl auth-service

## 配置说明

### 数据库
- 数据库: PostgreSQL
- 默认端口: 5432
- 数据库名: video_detection

### JWT
- 密钥: 在 application.yml 中配置
- 有效期: 24小时

### 端口
- 服务端口: 8081

# 构建与测试

## 构建命令

```bash
# 1. 编译auth-service
mvn clean compile -pl auth-service

# 2. 安装到本地仓库
mvn install -pl auth-service -DskipTests

# 3. 运行服务
mvn spring-boot:run -pl auth-service
```

## 测试API

```bash
# 1. 注册用户
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "Test1234"
  }'

# 2. 登录
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test1234"
  }'

# 3. 获取当前用户（需要替换<token>）
curl -X GET http://localhost:8081/api/auth/me \
  -H "Authorization: Bearer <token>"
```