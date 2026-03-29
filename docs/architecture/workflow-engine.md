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

## Retry Strategy
- Each step is recorded in `workflow_steps` with `attempt`.
- Retries are idempotent at the step level.
- Errors are captured in `error_reason` and propagated to the audit log.

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
