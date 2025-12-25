# Startup Script for Video Detection System Infrastructure
Write-Host "Starting Infrastructure (Postgres, Redis, ZooKeeper, Kafka)..."
docker-compose up -d postgres zookeeper kafka

Write-Host "Waiting for Kafka to be ready..."
Start-Sleep -Seconds 10

Write-Host "Infrastructure is ready!"
Write-Host "Kafka is listening on localhost:19092"
Write-Host "Postgres is listening on localhost:5432"
