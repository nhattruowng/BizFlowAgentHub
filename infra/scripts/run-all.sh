#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
LOG_DIR="$ROOT_DIR/.run-logs"
MAVEN_VERSION="3.9.6"
MAVEN_DIR="$ROOT_DIR/.tooling/apache-maven-$MAVEN_VERSION"

mkdir -p "$LOG_DIR"

info() { echo "[bizflow] $*"; }

command_exists() { command -v "$1" >/dev/null 2>&1; }

ensure_cmd() {
  local cmd="$1"
  if ! command_exists "$cmd"; then
    info "Missing required command: $cmd"
    return 1
  fi
}

download_file() {
  local url="$1"
  local out="$2"
  if command_exists curl; then
    curl -L "$url" -o "$out"
  elif command_exists wget; then
    wget -O "$out" "$url"
  else
    python - <<PY
import urllib.request
urllib.request.urlretrieve("$url", "$out")
PY
  fi
}

extract_zip() {
  local zip="$1"
  local dest="$2"
  if command_exists unzip; then
    unzip -q -o "$zip" -d "$dest"
  else
    python - <<PY
import zipfile
with zipfile.ZipFile("$zip") as z:
    z.extractall("$dest")
PY
  fi
}

ensure_maven() {
  if command_exists mvn; then
    return 0
  fi
  info "Maven not found. Installing to $MAVEN_DIR"
  mkdir -p "$ROOT_DIR/.tooling"
  local zip="$ROOT_DIR/.tooling/apache-maven-$MAVEN_VERSION-bin.zip"
  local url="https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/$MAVEN_VERSION/apache-maven-$MAVEN_VERSION-bin.zip"
  if [ ! -d "$MAVEN_DIR" ]; then
    download_file "$url" "$zip"
    extract_zip "$zip" "$ROOT_DIR/.tooling"
  fi
  export PATH="$MAVEN_DIR/bin:$PATH"
}

ensure_python() {
  if command_exists python; then
    return 0
  fi
  if command_exists python3; then
    return 0
  fi
  info "Python not found. Please install Python 3.11+ and re-run."
  return 1
}

ensure_node() {
  ensure_cmd node
  ensure_cmd npm
}

ensure_docker() {
  if command_exists docker; then
    return 0
  fi
  info "Docker not found. Please install Docker Desktop and re-run."
  return 1
}

wait_for_http() {
  local url="$1"
  local name="$2"
  local retries=60
  local delay=2
  info "Waiting for $name at $url"
  for _ in $(seq 1 $retries); do
    if command_exists curl; then
      if curl -s "$url" >/dev/null 2>&1; then
        info "$name is up"
        return 0
      fi
    else
      if python - <<PY
import urllib.request
try:
    urllib.request.urlopen("$url", timeout=1)
    print("ok")
except Exception:
    pass
PY
      then
        info "$name is up"
        return 0
      fi
    fi
    sleep $delay
  done
  info "Timeout waiting for $name"
  return 1
}

start_service() {
  local name="$1"
  local cmd="$2"
  local log="$LOG_DIR/$name.log"
  info "Starting $name..."
  nohup bash -c "$cmd" >"$log" 2>&1 &
  info "$name started (log: $log)"
}

info "Root: $ROOT_DIR"

ensure_docker
ensure_maven
ensure_python
ensure_node

info "Starting infra (Postgres/Redis/MinIO)"
if command_exists docker && command_exists docker-compose; then
  docker-compose -f "$ROOT_DIR/docker-compose.yml" up -d
else
  docker compose -f "$ROOT_DIR/docker-compose.yml" up -d
fi

info "Building Java services (skip tests)"
(cd "$ROOT_DIR" && mvn -q -pl apps/gateway-api,services/workflow-engine,services/tool-hub,services/approval-service,services/knowledge-service,services/audit-service -am -DskipTests package)

start_service "gateway-api" "cd '$ROOT_DIR' && mvn -q -pl apps/gateway-api spring-boot:run"
wait_for_http "http://localhost:8081/actuator/health" "gateway-api"

start_service "workflow-engine" "cd '$ROOT_DIR' && mvn -q -pl services/workflow-engine spring-boot:run"
wait_for_http "http://localhost:8082/actuator/health" "workflow-engine"

start_service "tool-hub" "cd '$ROOT_DIR' && mvn -q -pl services/tool-hub spring-boot:run"
wait_for_http "http://localhost:8083/actuator/health" "tool-hub"

start_service "approval-service" "cd '$ROOT_DIR' && mvn -q -pl services/approval-service spring-boot:run"
wait_for_http "http://localhost:8084/actuator/health" "approval-service"

start_service "knowledge-service" "cd '$ROOT_DIR' && mvn -q -pl services/knowledge-service spring-boot:run"
wait_for_http "http://localhost:8085/actuator/health" "knowledge-service"

start_service "audit-service" "cd '$ROOT_DIR' && mvn -q -pl services/audit-service spring-boot:run"
wait_for_http "http://localhost:8086/actuator/health" "audit-service"

info "Setting up agent runtime venv + deps"
VENV="$ROOT_DIR/apps/agent-runtime/.venv"
PY_BIN="python"
if command_exists python3; then
  PY_BIN="python3"
fi
if [ ! -d "$VENV" ]; then
  "$PY_BIN" -m venv "$VENV"
fi
"$VENV/Scripts/python.exe" -m pip install --upgrade pip >/dev/null
"$VENV/Scripts/python.exe" -m pip install -e "$ROOT_DIR/apps/agent-runtime" >/dev/null
start_service "agent-runtime" "cd '$ROOT_DIR/apps/agent-runtime' && '$VENV/Scripts/python.exe' -m uvicorn app.main:app --port 8090"
wait_for_http "http://localhost:8090/health" "agent-runtime"

info "Setting up admin console"
if [ ! -d "$ROOT_DIR/apps/admin-console/node_modules" ]; then
  (cd "$ROOT_DIR/apps/admin-console" && npm install)
fi
start_service "admin-console" "cd '$ROOT_DIR/apps/admin-console' && npm run dev -- --host 0.0.0.0 --port 5173"
wait_for_http "http://localhost:5173" "admin-console"

info "All services are up. Logs in $LOG_DIR"
