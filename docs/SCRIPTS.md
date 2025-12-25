# 脚本使用说明

本文档说明项目中保留的所有脚本文件的用途和使用方法。

## PowerShell 脚本 (.ps1)

### 1. start-infra.ps1

**位置**: `video-detection-system/start-infra.ps1`

**用途**: 启动基础设施服务（PostgreSQL, Redis, ZooKeeper, Kafka）

**使用方法**:
```powershell
.\start-infra.ps1
```

**说明**:
- 使用Docker Compose启动基础设施容器
- 等待10秒确保Kafka就绪
- 适合在启动微服务之前运行

---

### 2. set-jdk17.ps1

**位置**: `video-detection-system/scripts/set-jdk17.ps1`

**用途**: 设置当前PowerShell会话的JDK 17环境变量

**使用方法**:
```powershell
.\scripts\set-jdk17.ps1
```

**说明**:
- 设置JAVA_HOME为JDK 17路径
- 更新PATH环境变量
- 仅影响当前PowerShell会话
- 需要根据实际JDK安装路径修改脚本中的路径

---

### 3. cleanup-ports.ps1

**位置**: `video-detection-system/scripts/cleanup-ports.ps1`

**用途**: 清理微服务端口占用，终止所有Java进程

**使用方法**:
```powershell
.\scripts\cleanup-ports.ps1
```

**功能**:
1. 终止所有Java进程
2. 验证清理结果
3. 检查端口状态（9001-9006）
4. 显示服务启动顺序建议

**使用场景**:
- 微服务启动前清理端口
- 解决端口占用问题
- 重启所有服务前的准备工作

---

## Shell 脚本 (.sh)

### 1. start-kafka.sh

**位置**: `video-detection-system/scripts/start-kafka.sh`

**用途**: 使用Docker启动Kafka和ZooKeeper

**使用方法**:
```bash
bash scripts/start-kafka.sh
```

**功能**:
1. 检查Docker环境
2. 清理旧容器
3. 启动ZooKeeper容器（端口2181）
4. 启动Kafka容器（端口9092）
5. 验证服务状态

**注意**:
- 脚本配置的Kafka端口为9092
- 项目实际使用的Kafka端口为19092（在docker-compose.yml中配置）
- 建议使用docker-compose启动基础设施

---

### 2. create-topics.sh

**位置**: `video-detection-system/scripts/create-topics.sh`

**用途**: 创建Kafka主题

**使用方法**:
```bash
bash scripts/create-topics.sh
```

**说明**:
- 创建项目所需的Kafka主题
- 需要在Kafka启动后运行

---

### 3. init-database.sh

**位置**: `video-detection-system/scripts/init-database.sh`

**用途**: 使用data.sql初始化数据库

**使用方法**:
```bash
bash scripts/init-database.sh
```

**功能**:
1. 检查PostgreSQL容器是否运行
2. 验证data.sql文件存在
3. 导入数据库结构和示例数据

**使用场景**:
- 首次部署系统
- 重置数据库到初始状态
- 恢复数据库结构

**注意**:
- 需要先启动PostgreSQL容器
- 会导入示例用户和测试数据
- 测试账户: admin/123456, testuser/123456

---

### 4. init-database.ps1

**位置**: `video-detection-system/scripts/init-database.ps1`

**用途**: 使用data.sql初始化数据库（Windows版本）

**使用方法**:
```powershell
.\scripts\init-database.ps1
```

**功能**: 与init-database.sh相同，适用于Windows环境

---

### 5. backup-database.sh

**位置**: `video-detection-system/scripts/backup-database.sh`

**用途**: 备份PostgreSQL数据库

**使用方法**:
```bash
bash scripts/backup-database.sh
```

**功能**:
- 使用pg_dump导出数据库
- 生成带时间戳的备份文件
- 适合定期备份数据

---

### 6. recovery-database.sh

**位置**: `video-detection-system/scripts/recovery-database.sh`

**用途**: 从备份恢复PostgreSQL数据库

**使用方法**:
```bash
bash scripts/recovery-database.sh [backup_file]
```

**功能**:
- 从备份文件恢复数据库
- 支持指定备份文件路径
- 恢复前会提示确认

---

## 推荐工作流程

### 启动开发环境

1. **启动基础设施**:
   ```powershell
   .\start-infra.ps1
   ```

2. **初始化数据库**（首次部署）:
   ```powershell
   .\scripts\init-database.ps1
   ```
   或
   ```bash
   bash scripts/init-database.sh
   ```

3. **清理端口**（如果需要）:
   ```powershell
   .\scripts\cleanup-ports.ps1
   ```

4. **启动微服务**（按顺序）:
   - auth-service (9001)
   - video-service (9002)
   - worker-service (9003)
   - detection-service (9004)
   - websocket-service (9005)
   - audit-service (9006)

### 数据库维护

**备份数据库**:
```bash
bash scripts/backup-database.sh
```

**恢复数据库**:
```bash
bash scripts/recovery-database.sh backup_20251225.sql
```

---

## 脚本清单

### PowerShell脚本 (3个)
- ✅ `start-infra.ps1` - 启动基础设施
- ✅ `bash/set-jdk17.ps1` - 设置JDK环境
- ✅ `scripts/cleanup-ports.ps1` - 清理端口

### Shell脚本 (6个)
- ✅ `scripts/start-kafka.sh` - 启动Kafka
- ✅ `scripts/init-database.sh` - 初始化数据库
- ✅ `scripts/create-topics.sh` - 创建Kafka主题
- ✅ `scripts/backup-database.sh` - 备份数据库
- ✅ `scripts/recovery-database.sh` - 恢复数据库

---

**文档更新时间**: 2025-12-25
