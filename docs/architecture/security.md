# Security & Governance

## RBAC
- Roles: ADMIN, OPERATOR, SUPERVISOR.
- Role checks are enforced on approvals and sensitive tools.

## Tenant Isolation
- Every request carries tenant context.
- Data access is scoped by tenant ID in production.

## Approval Gate
- Tools marked `approval_required` are blocked until an approver acts.
- Approvals are auditable and replayable.

## Audit & Redaction
- All actions are logged in `audit_logs`.
- Sensitive payloads are redacted before log storage (MVP uses placeholders).

## Structured Validation
- All API inputs use JSON schema or Pydantic models.

## Threat Model (Basic)
- Unauthorized tool execution: mitigated by approval gate + RBAC.
- Cross-tenant data leakage: tenant context enforced across services.
- Non-repudiation: audit logs and outbox events.
