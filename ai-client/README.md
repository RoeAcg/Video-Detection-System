# ai-client 模块

## 模块说明

`ai-client` 是AI推理服务的客户端模块，封装了与AI服务的HTTP通信。

## 功能特性

- ✅ OpenFeign声明式HTTP客户端
- ✅ 自动重试机制（最多3次）
- ✅ 降级处理（服务不可用时）
- ✅ 超时配置（连接10秒，读取120秒）
- ✅ 自定义错误处理
- ✅ 详细的日志记录

## 目录结构
```text
ai-client/
├── pom.xml
└── src/main/java/com/zyn/aiclient/
├── dto/ [3个DTO]
├── feign/ [3个Feign相关类]
├── config/ [2个配置类]
└── exception/ [1个异常类]
```

## 使用方法

1. 添加依赖

在需要调用AI服务的模块中添加：


```xml
<dependency> <groupId>com.zyn</groupId> <artifactId>ai-client</artifactId> </dependency>
```
2. 启用Feign客户端
在启动类上添加注解：
```java
@SpringBootApplication
@EnableFeignClients(basePackages = "com.zyn.aiclient.feign")
public class WorkerServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(WorkerServiceApplication.class, args);
    }
}
```
3. 注入并使用客户端
```java
@Service
@RequiredArgsConstructor
public class DetectionService {
    
    private final AiServiceClient aiServiceClient;
    
    public void processVideo(String videoPath, String taskId) {
        // 构建请求
        AiDetectionRequest request = AiDetectionRequest.builder()
            .videoPath(videoPath)
            .taskId(taskId)
            .mode("standard")
            .frameRate(5)
            .build();
        
        // 调用AI服务
        AiDetectionResponse response = aiServiceClient.detect(request);
        
        // 处理响应
        if (response.getSuccess()) {
            System.out.println("检测结果: " + response.getResult());
            System.out.println("置信度: " + response.getConfidence());
        } else {
            System.err.println("检测失败: " + response.getErrorMessage());
        }
    }
}


```
4. 配置AI服务地址
   
   在 application.yml 中配置：
```yaml
ai:
  service:
    url: http://your-ai-service:5000
  client:
    connect-timeout: 10
    read-timeout: 120
```
# **API说明**

AiServiceClient 接口

| 方法           | 路径                   | 说明             |
| :------------- | :--------------------- | :--------------- |
| detect()       | POST /api/v1/detect    | 提交视频检测任务 |
| getModelInfo() | GET /api/v1/model/info | 获取模型信息     |
| healthCheck()  | GET /api/v1/health     | 健康检查         |

## 请求参数

**AiDetectionRequest**:

- `videoPath`: 视频文件路径（必需）
- `taskId`: 任务ID（必需）
- `mode`: 检测模式（fast/standard/thorough）
- `frameRate`: 采样帧率（默认5）
- `maxFrames`: 最大处理帧数（默认300）

## 响应结果

**AiDetectionResponse**:

- `result`: AUTHENTIC(真实) / FAKE(伪造) / UNCERTAIN(不确定)
- `confidence`: 置信度 (0.0-1.0)
- `processingTimeMs`: 处理时间
- `framesAnalyzed`: 分析的帧数
- `artifacts`: 检测到的伪造迹象

## 错误处理

1. **超时**: 自动重试3次，间隔1-3秒
2. **服务不可用**: 返回降级响应（result=UNCERTAIN）
3. **网络错误**: 抛出 `AiServiceException`

## 性能优化

- 连接池复用
- 请求压缩
- 响应缓存（可选）
- 并发限制（最多10个请求）

## 测试

```bash
# 编译

mvn clean compile -pl ai-client

# 运行测试

mvn test -pl ai-client

# 安装到本地仓库

mvn install -pl ai-client -DskipTests
```

依赖说明

- spring-cloud-starter-openfeign: Feign客户端
- spring-retry: 重试支持

- common-lib: 公共库（实体、枚举等）


# 构建与使用

## 构建步骤

```bash
# 1. 进入项目根目录
cd video-detection-system

# 2. 编译ai-client模块
mvn clean compile -pl ai-client

# 3. 安装到本地仓库
mvn install -pl ai-client -DskipTests

# 4. 验证
ls -lh ai-client/target/ai-client-0.0.1-SNAPSHOT.jar
```

## 在worker-service中使用

**worker-service/pom.xml**:

```xml
<dependency>
    <groupId>com.zyn</groupId>
    <artifactId>ai-client</artifactId>
</dependency>
```

**WorkerServiceApplication.java**:

```java
@SpringBootApplication
@EnableFeignClients(basePackages = "com.zyn.aiclient.feign")
public class WorkerServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(WorkerServiceApplication.class, args);
    }
}

```

**DetectionWorker.java**:

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class DetectionWorker {
    
    private final AiServiceClient aiServiceClient;
    
    @KafkaListener(topics = "video-detection-tasks")
    public void processTask(DetectionTaskEvent event) {
        log.info("开始处理任务: {}", event.getTaskId());
        
        // 调用AI服务
        AiDetectionRequest request = AiDetectionRequest.builder()
            .videoPath(event.getVideoPath())
            .taskId(event.getTaskId())
            .fileHash(event.getFileHash())
            .mode("standard")
            .build();
        
        try {
            AiDetectionResponse response = aiServiceClient.detect(request);
            
            if (response.getSuccess()) {
                log.info("检测完成 - 任务: {}, 结果: {}, 置信度: {}", 
                    event.getTaskId(), response.getResult(), response.getConfidence());
                // 保存结果到数据库...
            } else {
                log.error("检测失败 - 任务: {}, 错误: {}", 
                    event.getTaskId(), response.getErrorMessage());
            }
        } catch (AiServiceException e) {
            log.error("AI服务调用异常 - 任务: {}, 错误: {}", 
                event.getTaskId(), e.getMessage());
        }
    }
}
```

# 配置说明

## 核心配置项

| 配置项                     | 默认值                | 说明           |
| :------------------------- | :-------------------- | :------------- |
| ai.service.url             | http://localhost:5000 | AI服务地址     |
| ai.client.connect-timeout  | 10                    | 连接超时（秒） |
| ai.client.read-timeout     | 120                   | 读取超时（秒） |
| ai.client.max-retries      | 3                     | 最大重试次数   |
| ai.client.fallback-enabled | true                  | 是否启用降级   |