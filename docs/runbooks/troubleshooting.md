# Troubleshooting

## Common Issues
- **Postgres connection refused**: ensure `docker compose up -d` is running.
- **Port conflicts**: check ports 8081-8086, 8090, 5173.
- **Missing seed data**: remove volume and restart Postgres.

## Reset Seed Data
```bash
docker compose down -v
docker compose up -d
```
