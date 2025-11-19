# websocket-service 实时通知服务

## 功能特性

- ✅ WebSocket连接管理
- ✅ 实时消息推送
- ✅ 检测完成通知
- ✅ 心跳机制
- ✅ 多连接支持（一个用户多个设备）

## WebSocket连接

### 连接地址
ws://localhost:8085/ws/notifications?userId=<用户ID>

text

### 消息格式

#### 1. 连接成功
{
"type": "CONNECTED",
"message": "连接成功",
"timestamp": 1700236800000
}

text

#### 2. 检测完成通知
{
"type": "DETECTION_COMPLETED",
"message": "检测已完成",
"data": {
"taskId": "uuid-123",
"detectionId": 456,
"result": "FAKE",
"confidence": 0.95
},
"timestamp": 1700236800000
}

text

#### 3. 心跳
客户端发送:
{
"type": "PING"
}

text

服务端响应:
{
"type": "PONG",
"timestamp": 1700236800000
}

text

## 客户端示例

### JavaScript
const ws = new WebSocket('ws://localhost:8085/ws/notifications?userId=123');

ws.onopen = () => {
console.log('WebSocket连接已建立');

// 启动心跳
setInterval(() => {
ws.send(JSON.stringify({ type: 'PING' }));
}, 30000);
};

ws.onmessage = (event) => {
const message = JSON.parse(event.data);
console.log('收到消息:', message);

if (message.type === 'DETECTION_COMPLETED') {
console.log('检测完成:', message.data);
// 更新UI...
}
};

ws.onerror = (error) => {
console.error('WebSocket错误:', error);
};

ws.onclose = () => {
console.log('WebSocket连接已关闭');
};

text

## 构建和运行

编译
mvn clean compile -pl websocket-service

运行
mvn spring-boot:run -pl websocket-service

打包
mvn clean package -pl websocket-service -DskipTests

text

## 依赖服务

- Kafka: 9092
- Redis: 6379（可选）

## 测试WebSocket

使用在线工具测试：
1. https://www.websocket.org/echo.html
2. 连接地址: ws://localhost:8085/ws/notifications?userId=1
3. 发送心跳: {"type":"PING"}
