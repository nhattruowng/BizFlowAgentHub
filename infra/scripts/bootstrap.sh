#!/usr/bin/env bash
set -euo pipefail

echo "Starting BizFlow local infrastructure..."
docker compose -f "$(pwd)/docker-compose.yml" up -d
echo "Postgres and Redis should be available on localhost."
