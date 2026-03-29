# Local Development

## Prerequisites
- Java 21
- Maven 3.9+
- Python 3.11
- Node 18+
- Docker Desktop

## Start Infra
```bash
# Windows PowerShell
./infra/scripts/bootstrap.ps1

# or bash
./infra/scripts/bootstrap.sh
```

## Run Services
```bash
# Gateway API
mvn -pl apps/gateway-api spring-boot:run

# Workflow Engine
mvn -pl services/workflow-engine spring-boot:run

# Tool Hub
mvn -pl services/tool-hub spring-boot:run

# Approval Service
mvn -pl services/approval-service spring-boot:run

# Knowledge Service
mvn -pl services/knowledge-service spring-boot:run

# Audit Service
mvn -pl services/audit-service spring-boot:run
```

## Run Agent Runtime
```bash
cd apps/agent-runtime
python -m venv .venv
.venv/Scripts/activate
pip install -e .
uvicorn app.main:app --reload --port 8090
```

## Run Admin Console
```bash
cd apps/admin-console
npm install
npm run dev
```

## Seed Data
Database is seeded on container start from `infra/db/init`.
