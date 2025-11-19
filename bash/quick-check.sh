#!/bin/bash
# 保存为 quick-check.sh

echo "========================================="
echo "视频深伪检测系统 - 快速验证"
echo "========================================="

# 1. 检查项目结构
echo -e "\n1️⃣ 检查项目结构..."
required_dirs=("common-lib" "ai-client" "auth-service" "video-service" "worker-service" "detection-service" "websocket-service" "audit-service")
for dir in "${required_dirs[@]}"; do
    if [ -d "$dir" ]; then
        echo "   ✅ $dir"
    else
        echo "   ❌ $dir 缺失"
    fi
done

# 2. 检查Maven配置
echo -e "\n2️⃣ 检查Maven配置..."
if [ -f "pom.xml" ]; then
    echo "   ✅ 父POM存在"
    mvn validate > /dev/null 2>&1 && echo "   ✅ POM配置有效" || echo "   ❌ POM配置无效"
else
    echo "   ❌ 父POM缺失"
fi

# 3. 快速编译测试
echo -e "\n3️⃣ 快速编译测试..."
mvn clean compile -DskipTests -q
if [ $? -eq 0 ]; then
    echo "   ✅ 编译成功"
else
    echo "   ❌ 编译失败，请检查错误日志"
fi

# 4. 检查Docker环境
echo -e "\n4️⃣ 检查Docker环境..."
if command -v docker &> /dev/null; then
    echo "   ✅ Docker已安装"
    docker ps > /dev/null 2>&1 && echo "   ✅ Docker运行中" || echo "   ⚠️  Docker未运行"
else
    echo "   ❌ Docker未安装"
fi

# 5. 检查配置文件
echo -e "\n5️⃣ 检查配置文件..."
config_files=("scripts/init-database.sql" "docker-compose.yml")
for file in "${config_files[@]}"; do
    if [ -f "$file" ]; then
        echo "   ✅ $file"
    else
        echo "   ❌ $file 缺失"
    fi
done

echo -e "\n========================================="
echo "快速检查完成！"
echo "========================================="
