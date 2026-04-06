# Local Development

## Prerequisites
- Java 21
- Maven 3.9+
- Python 3.11
- Node 18+
- Docker Desktop

## Java Setup Note
- Ensure `JAVA_HOME` points to JDK 21 before running Maven.
- On Windows PowerShell:
```powershell
$env:JAVA_HOME='C:\Program Files\Java\jdk-21'
$env:Path='C:\Program Files\Java\jdk-21\bin;' + $env:Path
```
- On Git Bash:
```bash
export JAVA_HOME="/c/Program Files/Java/jdk-21"
export PATH="$JAVA_HOME/bin:$PATH"
```

## Start Infra
```bash
# Windows PowerShell
./infra/scripts/bootstrap.ps1

# or bash
./infra/scripts/bootstrap.sh
```

Infra local includes PostgreSQL, Redis, MinIO, and Kafka. Kafka listens on `localhost:9092` and the workflow relay publishes to topic `events`.

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

## Kafka Event Flow Check
1. Start `workflow-engine` and `audit-service`.
2. Call `POST /api/workflows/run`.
3. Confirm one row appears in `events_outbox` and is later marked `PUBLISHED`.
4. Query `GET /api/audit/{runId}` or `GET /api/audit?workflowRunId=<runId>` to verify the consumed event was written as an audit log.
