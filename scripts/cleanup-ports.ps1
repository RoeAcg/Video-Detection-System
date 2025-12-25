# Force kill all Java processes and verify ports are free
# Run this before starting microservices

Write-Host "=== Microservice Port Cleanup Tool ===" -ForegroundColor Cyan
Write-Host ""

# Step 1: Kill all Java processes
Write-Host "[1/3] Terminating all Java processes..." -ForegroundColor Yellow
$killed = 0
Get-Process java -ErrorAction SilentlyContinue | ForEach-Object {
    Stop-Process -Id $_.Id -Force -ErrorAction SilentlyContinue
    $killed++
}

if ($killed -gt 0) {
    Write-Host "  Terminated $killed Java process(es)" -ForegroundColor Green
    Start-Sleep -Seconds 2
}
else {
    Write-Host "  No Java processes found" -ForegroundColor Green
}

# Step 2: Verify all processes are gone
Write-Host ""
Write-Host "[2/3] Verifying cleanup..." -ForegroundColor Yellow
$remaining = (Get-Process java -ErrorAction SilentlyContinue | Measure-Object).Count
if ($remaining -eq 0) {
    Write-Host "  All Java processes terminated successfully" -ForegroundColor Green
}
else {
    Write-Host "  WARNING: $remaining Java process(es) still running!" -ForegroundColor Red
    Get-Process java -ErrorAction SilentlyContinue | Format-Table Id, ProcessName, StartTime
}

# Step 3: Check port status
Write-Host ""
Write-Host "[3/3] Checking microservice ports..." -ForegroundColor Yellow
$ports = @(9001, 9002, 9003, 9004, 9005, 9006)
$occupied = @()

foreach ($port in $ports) {
    $result = netstat -ano | findstr ":$port " | findstr "LISTENING"
    if ($result) {
        $occupied += $port
    }
}

if ($occupied.Count -eq 0) {
    Write-Host "  All ports are FREE!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Ready to start services. Recommended order:" -ForegroundColor Cyan
    Write-Host "  1. auth-service (9001)" -ForegroundColor White
    Write-Host "  2. video-service (9002)" -ForegroundColor White
    Write-Host "  3. worker-service (9003)" -ForegroundColor White
    Write-Host "  4. detection-service (9004)" -ForegroundColor White
    Write-Host "  5. websocket-service (9005)" -ForegroundColor White
    Write-Host "  6. audit-service (9006)" -ForegroundColor White
    Write-Host ""
    Write-Host "IMPORTANT: Start services ONE AT A TIME, wait for each to fully start!" -ForegroundColor Yellow
}
else {
    Write-Host "  WARNING: Ports still occupied: $($occupied -join ', ')" -ForegroundColor Red
    Write-Host ""
    Write-Host "Detailed port status:" -ForegroundColor Yellow
    netstat -ano | findstr "LISTENING" | findstr ":900"
}
