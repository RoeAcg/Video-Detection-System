#!/bin/bash

DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="backup_video_detection_$DATE.sql"

echo "开始备份数据库..."
docker exec video-detection-db pg_dump -U admin video_detection > $BACKUP_FILE

if [ $? -eq 0 ]; then
    echo "✅ 备份成功: $BACKUP_FILE"
    gzip $BACKUP_FILE
    echo "✅ 压缩完成: $BACKUP_FILE.gz"
else
    echo "❌ 备份失败"
fi
