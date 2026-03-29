# ADR-001: Workflow-First Architecture

## Status
Accepted

## Context
We need predictable, auditable automation with retries and approvals.

## Decision
Adopt workflow-first architecture. Agents are steps within a durable workflow.

## Consequences
- Clear step history and replay support.
- Easier to integrate Temporal later.
