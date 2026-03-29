# ADR-003: Tool Hub Mandatory

## Status
Accepted

## Context
Agents must not directly access external systems.

## Decision
All tools are invoked via Tool Hub with audit logging and approvals.

## Consequences
- Centralized governance.
- Slower direct access but safer.
