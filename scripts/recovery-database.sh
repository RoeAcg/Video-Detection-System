#!/bin/bash

if [ -z "$1" ]; then
    echo "用法: ./restore-database.sh backup_file.sql.gz"
    exit 1
fi

BACKUP_FILE=$1

echo "开始恢复数据库..."
gunzip -c $BACKUP_FILE | docker exec -i vds-postgres psql -U postgres video_detection

if [ $? -eq 0 ]; then
    echo "✅ 恢复成功"
else
    echo "❌ 恢复失败"
fi
