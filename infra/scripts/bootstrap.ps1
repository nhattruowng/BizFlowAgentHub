Write-Host "Starting BizFlow local infrastructure..."
docker compose -f "D:\BizFlowAgentHub\docker-compose.yml" up -d
Write-Host "Postgres and Redis should be available on localhost."
