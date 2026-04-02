Write-Host "Starting BizFlow local infrastructure..."
docker compose -f "D:\BizFlowAgentHub\docker-compose.yml" up -d
Write-Host "Postgres, Redis, MinIO, and Kafka should be available on localhost."
