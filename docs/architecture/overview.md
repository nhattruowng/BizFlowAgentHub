# BizFlow Agent Hub - Architecture Overview

## Product Goals
BizFlow Agent Hub is a workflow-first automation platform that ingests tasks from multiple sources, orchestrates durable workflows, and routes work through specialized agents and governed tools. The system is designed for auditability, retries, approvals, and enterprise-grade observability.

## Scope
- Task intake from REST, email webhook, scheduler, and manual admin form.
- Durable workflow orchestration with retries and step history.
- Event-driven propagation through Kafka with transactional outbox relay.
- Agent runtime with structured I/O, tool invocation, policy checks, and handoff.
- Approval workflow for sensitive actions.
- Auditable logs, metrics, and tracing.

## Design Principles
- Workflow-first orchestration (agents are steps, not the orchestrator).
- Modular services with clear contracts.
- Production-oriented defaults (observability, security, governance).
- Easy extensibility for new workflows, agents, and tools.
- Replay-friendly logs and deterministic orchestration state.

## High-Level System Diagram
```mermaid
flowchart LR
  A[Task Intake] --> B[Workflow Orchestrator]
  B --> O[Events Outbox]
  O --> K[Kafka Topic events]
  B --> C[Agent Runtime]
  C --> D[Tool Hub]
  C --> E[Knowledge Service]
  C --> F[Approval Service]
  K --> G[Audit Service Worker]
  H[Admin Console] --> A
  H --> B
  H --> F
  H --> G
```

## Core Components
- **API Gateway / Task Intake**: Accepts tasks and normalizes input into workflow runs.
- **Workflow Orchestrator**: Durable state machine, retries, step history, replay logs, and outbox event production.
- **Agent Runtime**: Executes agent graph; routing, planning, context loading, policy checks.
- **Tool Hub**: Governs tool invocation, permissions, and audit logging.
- **Approval Service**: Approval queue and decision API.
- **Knowledge Service**: Document and chunk retrieval for context.
- **Audit / Observability**: Structured logs, tracing, metrics, and Kafka-driven audit ingestion.
- **Admin Console**: UI for tasks, runs, approvals, audit, tools, and agents.

## Data Flow
1. Task intake receives a request and creates a Task record.
2. Orchestrator creates a `WorkflowRun`, initial steps, and an outbox record in `events_outbox`.
3. Outbox relay publishes the event to Kafka topic `events`.
4. Agent runtime executes routing/planning/context/policy/execution.
5. Tool Hub executes or queues actions requiring approval.
6. Approval Service manages decisions.
7. Audit Service worker consumes Kafka events and stores immutable audit records.
8. Admin Console visualizes everything.
