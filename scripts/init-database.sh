#!/bin/bash

echo "========================================="
echo "  数据库初始化脚本"
echo "========================================="
echo ""

# 检查Docker容器是否运行
echo "[1/3] 检查PostgreSQL容器..."
if ! docker ps | grep -q video-detection-db; then
    echo "❌ PostgreSQL容器未运行"
    echo "请先启动容器: docker-compose up -d postgres"
    exit 1
fi
echo "✅ 容器运行中"

# 检查SQL文件是否存在
echo ""
echo "[2/3] 检查SQL文件..."
if [ ! -f "scripts/data.sql" ]; then
    echo "❌ 找不到 scripts/data.sql"
    exit 1
fi
echo "✅ SQL文件存在"

# 导入数据库结构和数据
echo ""
echo "[3/3] 导入数据库..."
docker exec -i video-detection-db psql -U admin video_detection < scripts/data.sql

if [ $? -eq 0 ]; then
    echo ""
    echo "========================================="
    echo "✅ 数据库初始化成功"
    echo "========================================="
    echo ""
    echo "测试账户："
    echo "  管理员: admin / 123456"
    echo "  用户:   testuser / 123456"
else
    echo ""
    echo "========================================="
    echo "❌ 数据库初始化失败"
    echo "========================================="
    exit 1
fi
