# User Guide

## Login Demo
The MVP uses header-based demo identity. Use:
- `X-User-Id: demo-user`
- `X-User-Role: ADMIN`

## Create a Task
```bash
curl -X POST http://localhost:8081/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"tenantId":"demo-tenant","source":"email","type":"email_support","payload":{"subject":"Login issue","body":"User locked out"}}'
```

## View Workflow Run
```bash
curl http://localhost:8082/api/workflows/runs/<runId>
```

## Approve a Request
```bash
curl -X POST http://localhost:8084/api/approvals/<approvalId>/approve
```

## View Audit Log
```bash
curl http://localhost:8086/api/audit/<runId>
```

## Knowledge Lookup
```bash
curl -X POST http://localhost:8085/api/knowledge/search \
  -H "Content-Type: application/json" \
  -d '{"query":"invoice", "limit":2}'
```

## Workflow Status Reference
- `WAITING_APPROVAL` requires human approval.
- `COMPLETED` indicates successful run.
- `FAILED` indicates terminal error.

## Demo Use Cases
1. **Email/Ticket**: create task `email_support`.
2. **Invoice/OCR**: create task `invoice_approval` with `amount` > 1000.
3. **Policy Lookup**: create task `policy_lookup` with `query`.

## Reset Seed Data
See `docs/runbooks/troubleshooting.md`.
