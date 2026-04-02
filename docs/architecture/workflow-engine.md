# Workflow Engine

## State Machine
The orchestrator runs workflows as deterministic state machines with explicit states:

- CREATED
- QUEUED
- PLANNING
- CONTEXT_LOADING
- POLICY_CHECKING
- WAITING_TOOL
- VALIDATING
- WAITING_APPROVAL
- APPROVED
- REJECTED
- COMPLETED
- FAILED
- ESCALATED

## Event Publishing
- Every workflow start writes a domain event into `events_outbox` in the same service flow.
- A scheduled outbox relay publishes pending rows to Kafka topic `events`.
- Published rows are marked `PUBLISHED`; relay failures are marked `FAILED` with `last_error` for retry.
- The current event emitted by the MVP is `workflow.run.created`.

## Retry Strategy
- Each step is recorded in `workflow_steps` with `attempt`.
- Retries are idempotent at the step level.
- Errors are captured in `error_reason` and propagated to the audit log.
- Outbox relay retries failed publishes by polling `NEW` and `FAILED` rows.

## Approval Handling
- Steps that require approvals transition to `WAITING_APPROVAL`.
- The Approval Service issues the final decision.
- On approval: resume remaining steps.
- On rejection: mark run as `REJECTED` and finalize.

## Escalation Flow
- Policy or Validator agents can raise escalation.
- Escalation switches run status to `ESCALATED`.
- Admin Console surfaces escalation to human operators.

## Temporal Readiness
- Current MVP uses a simple state machine with persisted steps.
- Workflow API and models are designed to map 1:1 to Temporal workflows later.
- The outbox + Kafka boundary keeps downstream integrations decoupled from the workflow runtime.
