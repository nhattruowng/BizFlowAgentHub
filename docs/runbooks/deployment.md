# Deployment (Local/Dev)

## Docker Compose
Use the root `docker-compose.yml` for Postgres/Redis/MinIO. Services are run from source in dev mode.

## Cloud Readiness Notes
- Replace local Postgres with managed Postgres.
- Replace Redis with managed Redis.
- Deploy Java services as containers.
- Add centralized log aggregation (ELK/Grafana/Loki).
- Configure OpenTelemetry collector + tracing backend.
