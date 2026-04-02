# ADR-005: Audit + Outbox Pattern

## Status
Accepted

## Context
We need reliable event publishing and traceable logs without coupling workflow execution to synchronous downstream HTTP calls.

## Decision
Use `events_outbox` as the transactional handoff from workflow execution to Kafka, and let downstream services such as Audit consume domain events asynchronously.

Current MVP details:
- `workflow-engine` writes `workflow.run.created` into `events_outbox`.
- A relay worker publishes pending rows to Kafka topic `events`.
- `audit-service` consumes from Kafka and persists audit records idempotently by `source_event_id`.

## Consequences
- Strong observability and compliance.
- Reliable retry boundary between DB write and message publish.
- Requires background job to publish outbox events later.
- Requires consumers to handle duplicate delivery safely.
