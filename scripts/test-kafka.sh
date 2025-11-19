#!/bin/bash

echo "========================================="
echo "Kafka 测试脚本"
echo "========================================="

# 1. 检查 Kafka 是否运行
echo -e "\n1️⃣ 检查 Kafka 状态..."
docker exec kafka-broker kafka-broker-api-versions --bootstrap-server localhost:9092 > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo "✅ Kafka 运行正常"
else
    echo "❌ Kafka 未运行"
    exit 1
fi

# 2. 创建测试主题
echo -e "\n2️⃣ 创建测试主题..."
docker exec kafka-broker kafka-topics --create \
  --bootstrap-server localhost:9092 \
  --replication-factor 1 \
  --partitions 3 \
  --topic test-topic \
  --if-not-exists

# 3. 列出所有主题
echo -e "\n3️⃣ 列出所有主题..."
docker exec kafka-broker kafka-topics --list --bootstrap-server localhost:9092

# 4. 发送测试消息
echo -e "\n4️⃣ 发送测试消息..."
echo "Hello Kafka - $(date)" | docker exec -i kafka-broker kafka-console-producer \
  --bootstrap-server localhost:9092 \
  --topic test-topic

echo "✅ 消息已发送"

# 5. 消费消息
echo -e "\n5️⃣ 消费消息（按 Ctrl+C 停止）..."
docker exec kafka-broker kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic test-topic \
  --from-beginning \
  --max-messages 1

echo -e "\n========================================="
echo "✅ Kafka 测试完成！"
echo "========================================="
