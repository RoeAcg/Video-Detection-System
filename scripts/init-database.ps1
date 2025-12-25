# 数据库初始化脚本

Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "  数据库初始化脚本" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

# 检查Docker容器是否运行
Write-Host "[1/3] 检查PostgreSQL容器..." -ForegroundColor Yellow
$containerRunning = docker ps --filter "name=video-detection-db" --format "{{.Names}}"
if (-not $containerRunning) {
    Write-Host "❌ PostgreSQL容器未运行" -ForegroundColor Red
    Write-Host "请先启动容器: docker-compose up -d postgres" -ForegroundColor Yellow
    exit 1
}
Write-Host "✅ 容器运行中" -ForegroundColor Green

# 检查SQL文件是否存在
Write-Host ""
Write-Host "[2/3] 检查SQL文件..." -ForegroundColor Yellow
if (-not (Test-Path "scripts\data.sql")) {
    Write-Host "❌ 找不到 scripts\data.sql" -ForegroundColor Red
    exit 1
}
Write-Host "✅ SQL文件存在" -ForegroundColor Green

# 导入数据库结构和数据
Write-Host ""
Write-Host "[3/3] 导入数据库..." -ForegroundColor Yellow
Get-Content scripts\data.sql | docker exec -i video-detection-db psql -U admin video_detection

if ($?) {
    Write-Host ""
    Write-Host "=========================================" -ForegroundColor Cyan
    Write-Host "✅ 数据库初始化成功" -ForegroundColor Green
    Write-Host "=========================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "测试账户：" -ForegroundColor Yellow
    Write-Host "  管理员: admin / 123456" -ForegroundColor White
    Write-Host "  用户:   testuser / 123456" -ForegroundColor White
} else {
    Write-Host ""
    Write-Host "=========================================" -ForegroundColor Cyan
    Write-Host "❌ 数据库初始化失败" -ForegroundColor Red
    Write-Host "=========================================" -ForegroundColor Cyan
    exit 1
}
