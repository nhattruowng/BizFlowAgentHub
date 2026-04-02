# API Endpoints

## Task Intake
### POST `/api/tasks`
Request:
```json
{
  "tenantId": "demo-tenant",
  "source": "email",
  "type": "email_support",
  "payload": {
    "subject": "Cannot login",
    "body": "User locked out",
    "from": "customer@example.com"
  },
  "correlationId": "corr-123"
}
```
Response:
```json
{
  "taskId": "<uuid>",
  "status": "QUEUED",
  "workflowRunId": "<uuid>",
  "createdAt": "2026-03-30T00:00:00Z"
}
```

### GET `/api/tasks/{id}`
Returns task status and workflow run id.

## Workflow Orchestrator
### POST `/api/workflows/run`
```json
{
  "workflowName": "email-ticket-workflow",
  "tenantId": "demo-tenant",
  "taskId": "<uuid>",
  "correlationId": "corr-123",
  "input": {}
}
```

Side effect:
- Creates a workflow run, persists steps, and appends a Kafka outbox event that will be published to topic `events`.

### GET `/api/workflows/runs/{id}`
Returns run status and steps.

## Approval Service
### POST `/api/approvals/{id}/approve`
### POST `/api/approvals/{id}/reject`

## Audit Service
### GET `/api/audit/{runId}`
### GET `/api/audit?workflowRunId=<runId>&action=<action>`

Notes:
- Audit records may be created by direct API append or by Kafka consumer worker after processing events from topic `events`.

## Tool Hub
### GET `/api/tools`
### POST `/api/tools/{toolName}/invoke`

## Knowledge Service
### POST `/api/knowledge/search`
```json
{
  "query": "invoice",
  "limit": 3
}
```

## Sample cURL
```bash
curl -X POST http://localhost:8081/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"tenantId":"demo-tenant","source":"email","type":"email_support","payload":{"subject":"Login issue"}}'
```
