# 设置 JDK17 路径
$jdk17 = "C:\Users\ROe\.jdks\ms-17.0.17"

Write-Host "Setting JAVA_HOME to $jdk17" -ForegroundColor Green

# 设置当前会话环境变量
$env:JAVA_HOME = $jdk17
$env:Path = "$jdk17\bin;" + $env:Path

# 确认输出
Write-Host "JAVA_HOME  = $env:JAVA_HOME" -ForegroundColor Yellow
Write-Host "Java version:" -ForegroundColor Cyan

java -version
