# ADR-002: Hybrid Agent Runtime

## Status
Accepted

## Context
Agents need to run locally without vendor lock-in.

## Decision
Implement a Python agent runtime with provider abstraction and mock LLM.

## Consequences
- Local demos without API keys.
- Vendor-specific providers can be added later.
