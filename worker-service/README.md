# worker-service 异步检测工作服务

## 功能特性

- ✅ Kafka消息消费
- ✅ 调用AI服务进行检测
- ✅ 任务状态管理
- ✅ 检测结果保存
- ✅ 失败重试机制
- ✅ 完成通知发送

## 工作流程

Kafka消费者监听 video-detection-tasks 主题

接收到检测任务消息

更新任务状态为 PROCESSING

调用 AI 服务 (通过 ai-client)

保存检测结果到数据库

更新任务状态为 COMPLETED

发送完成通知到 detection-notifications 主题

text

## 重试机制

- 最大重试次数: 3次
- 失败后自动重试
- 超过重试次数标记为 FAILED

## 并发配置

- 消费者并发数: 3
- 单次拉取记录数: 10
- 手动ACK模式

## 构建和运行

编译
mvn clean compile -pl worker-service

运行
mvn spring-boot:run -pl worker-service

打包
mvn clean package -pl worker-service -DskipTests

text

## 依赖服务

- PostgreSQL: 5432
- Kafka: 9092
- AI服务: 5000

## 监控

查看日志：
tail -f logs/worker-service.log

text

## 测试

手动发送测试消息到Kafka：
kafka-console-producer --bootstrap-server localhost:9092
--topic video-detection-tasks

text
undefined