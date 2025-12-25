# Video Detection System

基于深度学习的视频深伪检测系统，支持Deepfake和AIGC内容检测。

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18.3.1-blue.svg)](https://reactjs.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 📋 项目简介

Video Detection System 是一个企业级的视频深伪检测平台，采用微服务架构，集成了先进的深度学习模型（Effort、DRCT），提供完整的视频上传、检测、结果分析和审计功能。

### 主要特性

- 🎯 **双模式检测**：支持人脸伪造检测（Deepfake）和通用生成内容检测（AIGC）
- 🏗️ **微服务架构**：6个独立微服务，易于扩展和维护
- 🔐 **安全认证**：基于JWT的用户认证和授权机制
- 📊 **实时通知**：WebSocket实时推送检测进度和结果
- 📝 **完整审计**：详细的操作日志和审计追踪
- 🎨 **现代前端**：React + Vite构建的响应式用户界面
- 🐳 **容器化部署**：完整的Docker Compose配置
 
 ## 🖼️ 系统演示
 
 ### 1. 仪表盘
 ![Dashboard](docs/images/dashboard.png)
 
 ### 2. 视频检测
 ![Video Detection](docs/images/video_detection.png)
 
 ### 3. 图片检测
 ![Image Detection](docs/images/image_detection.png)
 
 ### 4. 历史记录
 ![History](docs/images/history.png)
 
 ### 5. 审计日志
 ![Audit Log](docs/images/audit_log.png)

## 🏛️ 系统架构

```
┌─────────────┐
│   Frontend  │ (React + Vite)
└──────┬──────┘
       │
┌──────▼──────────────────────────────────────────┐
│              API Gateway (Nginx)                 │
└──────┬──────────────────────────────────────────┘
       │
┌──────▼──────────────────────────────────────────┐
│                 Microservices                    │
├──────────────┬──────────────┬────────────────────┤
│ Auth Service │Video Service │Detection Service   │
│   (9001)     │   (9002)     │     (9004)         │
├──────────────┼──────────────┼────────────────────┤
│Worker Service│WebSocket Svc │ Audit Service      │
│   (9003)     │   (9005)     │     (9006)         │
└──────────────┴──────────────┴────────────────────┘
       │              │              │
┌──────▼──────┐ ┌────▼────┐  ┌──────▼──────┐
│  PostgreSQL │ │  Kafka  │  │    Redis    │
│   (5432)    │ │ (19092) │  │   (6379)    │
└─────────────┘ └─────────┘  └─────────────┘
       │
┌──────▼──────────┐
│  AI Detection   │ (Python Flask)
│   Service       │
│    (5000)       │
└─────────────────┘
```

## 🚀 快速开始

### 前置要求

- **Java 17+**
- **Maven 3.8+**
- **Node.js 18+**
- **Docker & Docker Compose**
- **PostgreSQL 15+**
- **Python 3.8+** (用于AI服务)

### 安装步骤

1. **克隆项目**
   ```bash
   git clone https://github.com/yourusername/video-detection-system.git
   cd video-detection-system
   ```

2. **启动基础设施**
   ```bash
   # Windows
   .\start-infra.ps1
   
   # Linux/Mac
   docker-compose -f docker-compose-infra.yml up -d
   ```

3. **初始化数据库**
   ```bash
   # Windows
   .\scripts\init-database.ps1
   
   # Linux/Mac
   bash scripts/init-database.sh
   ```

4. **编译后端服务**
   ```bash
   mvn clean package -DskipTests
   ```

5. **启动微服务**
   ```bash
   # 按顺序启动各服务
   # 1. auth-service
   # 2. video-service
   # 3. worker-service
   # 4. detection-service
   # 5. websocket-service
   # 6. audit-service
   ```

6. **启动前端**
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

7. **访问系统**
   - 前端: http://localhost:5173
   - 测试账户: admin / 123456

## 📚 文档

- [API接口文档](docs/API接口文档.md) - 完整的REST API文档
- [AI客户端API](docs/AI_Client_API_Documentation.md) - AI检测服务接口
- [脚本使用说明](docs/SCRIPTS.md) - 工具脚本使用指南
- [端口迁移说明](docs/PORT_MIGRATION.md) - 端口配置变更记录

## 🛠️ 技术栈

### 后端
- **框架**: Spring Boot 3.2.0, Spring Cloud
- **数据库**: PostgreSQL 15
- **消息队列**: Apache Kafka 7.5.0
- **缓存**: Redis 7.0
- **认证**: JWT (JSON Web Token)
- **API文档**: OpenAPI 3.0

### 前端
- **框架**: React 18.3.1
- **构建工具**: Vite 6.0
- **路由**: React Router 7.1
- **HTTP客户端**: Axios 1.7
- **样式**: CSS Modules

### AI服务
- **框架**: Flask (Python)
- **模型**: Effort (Deepfake检测), DRCT (AIGC检测)

- **深度学习**: PyTorch, OpenCV
> **注意**: 本开源版本不包含完整的 AI 检测服务核心代码（闭源）。项目提供了一个 `scripts/mock-ai-service.py` 脚本用于演示系统功能流转。该脚本会模拟检测过程并返回随机结果。

### DevOps
- **容器化**: Docker, Docker Compose
- **构建工具**: Maven
- **版本控制**: Git

## 📦 项目结构

```
video-detection-system/
├── auth-service/          # 认证服务
├── video-service/         # 视频管理服务
├── worker-service/        # 任务处理服务
├── detection-service/     # 检测服务
├── websocket-service/     # WebSocket通知服务
├── audit-service/         # 审计日志服务
├── common-lib/            # 公共库
├── ai-client/             # AI服务客户端
├── frontend/              # React前端
├── scripts/               # 工具脚本
├── docs/                  # 文档
├── docker-compose.yml     # Docker编排配置
└── pom.xml                # Maven父POM
```

## 🔧 配置说明

### 环境变量

关键配置项（需要根据实际环境修改）：

```yaml
# 数据库配置
POSTGRES_USER: admin
POSTGRES_PASSWORD: your-password  # 对应后端配置中的 POSTGRES_PASSWORD
POSTGRES_DB: video_detection

# JWT密钥
JWT_SECRET: your-secret-key       # 对应后端配置中的 JWT_SECRET

# AI服务地址
AI_SERVICE_URL: http://localhost:5000
```

### 端口配置

| 服务 | 端口 | 说明 |
|------|------|------|
| auth-service | 9001 | 认证服务 |
| video-service | 9002 | 视频服务 |
| worker-service | 9003 | 任务处理 |
| detection-service | 9004 | 检测服务 |
| websocket-service | 9005 | WebSocket |
| audit-service | 9006 | 审计服务 |
| PostgreSQL | 5432 | 数据库 |
| Kafka | 19092 | 消息队列 |
| Redis | 6379 | 缓存 |
| AI Service | 5000 | AI检测 |

## 🧪 测试

```bash
# 运行单元测试
mvn test

# 运行集成测试
mvn verify

# 前端测试
cd frontend
npm test
```

## 📊 性能指标

- **检测速度**: 平均1-3秒/视频（取决于视频长度和硬件）
- **并发处理**: 支持多任务并发检测
- **准确率**: Deepfake检测准确率 >90%（基于Effort模型）

## 🤝 贡献指南

欢迎贡献代码！请遵循以下步骤：

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📝 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

## 👥 作者

- **Your Name** - *Initial work*

## 🙏 致谢

- [DeepfakeBench](https://github.com/SCLBD/DeepfakeBench) - 深度学习模型
- Spring Boot 社区
- React 社区

## 📮 联系方式

- 项目主页: https://github.com/yourusername/video-detection-system
- 问题反馈: https://github.com/yourusername/video-detection-system/issues

---

**注意**: 本项目仅供学习和研究使用，请勿用于非法用途。
