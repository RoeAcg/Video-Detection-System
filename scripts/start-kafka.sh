#!/bin/bash
# -*- coding: utf-8 -*-
export LANG=zh_CN.UTF-8

echo "========================================="
echo "启动 Kafka (Docker)"
echo "========================================="

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

# 检查 Docker
echo -e "\n${YELLOW}[1] 检查 Docker...${NC}"
if ! command -v docker &> /dev/null; then
    echo -e "${RED}❌ Docker 未安装，请先安装 Docker${NC}"
    echo -e "${CYAN}下载地址: https://www.docker.com/products/docker-desktop${NC}"
    exit 1
fi

if ! docker ps &> /dev/null; then
    echo -e "${RED}❌ Docker 未运行，请启动 Docker${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Docker 已就绪${NC}"

# 停止并删除旧容器
echo -e "\n${YELLOW}[2] 清理旧容器...${NC}"
docker stop kafka zookeeper 2>/dev/null || true
docker rm kafka zookeeper 2>/dev/null || true

# 启动 Zookeeper
echo -e "\n${YELLOW}[3] 启动 Zookeeper...${NC}"
docker run -d \
    --name zookeeper \
    -p 2181:2181 \
    -e ZOOKEEPER_CLIENT_PORT=2181 \
    -e ZOOKEEPER_TICK_TIME=2000 \
    confluentinc/cp-zookeeper:7.5.0

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Zookeeper 启动成功${NC}"
else
    echo -e "${RED}❌ Zookeeper 启动失败${NC}"
    exit 1
fi

# 等待 Zookeeper 就绪
echo -e "\n${YELLOW}[4] 等待 Zookeeper 就绪...${NC}"
sleep 10

# 启动 Kafka
echo -e "\n${YELLOW}[5] 启动 Kafka...${NC}"
docker run -d \
    --name kafka \
    --link zookeeper \
    -p 9092:9092 \
    -e KAFKA_BROKER_ID=1 \
    -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 \
    -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
    -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
    -e KAFKA_TRANSACTION_STATE_LOG_MIN_ISR=1 \
    -e KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR=1 \
    confluentinc/cp-kafka:7.5.0

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Kafka 启动成功${NC}"
else
    echo -e "${RED}❌ Kafka 启动失败${NC}"
    exit 1
fi

# 等待 Kafka 就绪
echo -e "\n${YELLOW}[6] 等待 Kafka 就绪 (30秒)...${NC}"
sleep 30

# 验证服务
echo -e "\n${YELLOW}[7] 验证服务状态...${NC}"
docker ps --filter "name=kafka" --filter "name=zookeeper" --format "table {{.Names}}\t{{.Status}}"

# 测试连接
echo -e "\n${YELLOW}[8] 测试 Kafka 连接...${NC}"
docker exec kafka kafka-broker-api-versions --bootstrap-server localhost:9092 &> /dev/null
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Kafka 连接正常${NC}"
else
    echo -e "${YELLOW}⚠️  Kafka 可能还在启动中，请稍等${NC}"
fi

echo -e "\n========================================="
echo -e "${GREEN}Kafka 启动完成！${NC}"
echo -e "========================================="
echo -e "${CYAN}Zookeeper: localhost:2181${NC}"
echo -e "${CYAN}Kafka:     localhost:9092${NC}"
echo -e "\n${YELLOW}停止命令: docker stop kafka zookeeper${NC}"
echo -e "${YELLOW}删除命令: docker rm kafka zookeeper${NC}"
