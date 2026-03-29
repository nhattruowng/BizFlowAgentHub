# ADR-005: Audit + Outbox Pattern

## Status
Accepted

## Context
We need reliable event publishing and traceable logs.

## Decision
Use audit logs and events_outbox table as the integration backbone.

## Consequences
- Strong observability and compliance.
- Requires background job to publish outbox events later.
