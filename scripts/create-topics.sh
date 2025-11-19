#!/bin/bash

echo "创建项目所需的 Kafka Topics..."

# video-detection-tasks (视频检测任务)
docker exec kafka-broker kafka-topics --create \
  --bootstrap-server localhost:9092 \
  --replication-factor 1 \
  --partitions 3 \
  --topic video-detection-tasks \
  --if-not-exists

# detection-notifications (检测完成通知)
docker exec kafka-broker kafka-topics --create \
  --bootstrap-server localhost:9092 \
  --replication-factor 1 \
  --partitions 2 \
  --topic detection-notifications \
  --if-not-exists

echo "✅ Topics 创建完成"

# 查看所有 Topics
echo -e "\n当前所有 Topics:"
docker exec kafka-broker kafka-topics --list --bootstrap-server localhost:9092
