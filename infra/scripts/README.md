# Run All Services (Sequential)

This script boots the entire BizFlow Agent Hub stack in sequence. It checks for required tools, installs Maven locally if missing, ensures Python/Node deps are present, and starts each service with health checks.

```bash
bash infra/scripts/run-all.sh
```

Logs are written to `.run-logs/` in the repo root.
