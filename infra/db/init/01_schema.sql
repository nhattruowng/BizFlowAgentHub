-- BizFlow Agent Hub schema

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'task_status') THEN
    CREATE TYPE task_status AS ENUM ('CREATED','QUEUED','RUNNING','COMPLETED','FAILED');
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'workflow_status') THEN
    CREATE TYPE workflow_status AS ENUM (
      'CREATED','QUEUED','PLANNING','CONTEXT_LOADING','POLICY_CHECKING','WAITING_TOOL',
      'VALIDATING','WAITING_APPROVAL','APPROVED','REJECTED','COMPLETED','FAILED','ESCALATED'
    );
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'approval_status') THEN
    CREATE TYPE approval_status AS ENUM ('PENDING','APPROVED','REJECTED');
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'tool_side_effect') THEN
    CREATE TYPE tool_side_effect AS ENUM ('READ','WRITE','DESTRUCTIVE');
  END IF;
END$$;

CREATE TABLE IF NOT EXISTS tenants (
  id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  name text NOT NULL,
  status text NOT NULL DEFAULT 'ACTIVE',
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS users (
  id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  tenant_id uuid NOT NULL REFERENCES tenants(id),
  email text NOT NULL,
  name text NOT NULL,
  status text NOT NULL DEFAULT 'ACTIVE',
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS roles (
  id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  name text NOT NULL UNIQUE,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS user_roles (
  user_id uuid NOT NULL REFERENCES users(id),
  role_id uuid NOT NULL REFERENCES roles(id),
  created_at timestamptz NOT NULL DEFAULT now(),
  PRIMARY KEY (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS tasks (
  id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  tenant_id uuid NOT NULL REFERENCES tenants(id),
  source text NOT NULL,
  type text NOT NULL,
  status task_status NOT NULL,
  workflow_run_id text,
  correlation_id text,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS task_inputs (
  id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  task_id uuid NOT NULL REFERENCES tasks(id),
  payload_json text NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS workflows (
  id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  name text NOT NULL UNIQUE,
  description text,
  created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS workflow_runs (
  id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  workflow_id uuid NOT NULL REFERENCES workflows(id),
  task_id text,
  status workflow_status NOT NULL,
  correlation_id text,
  error_reason text,
  started_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS workflow_steps (
  id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  run_id uuid NOT NULL REFERENCES workflow_runs(id),
  step_name text NOT NULL,
  status workflow_status NOT NULL,
  attempt int NOT NULL DEFAULT 1,
  started_at timestamptz,
  ended_at timestamptz,
  error_reason text
);

CREATE TABLE IF NOT EXISTS agents (
  id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  name text NOT NULL UNIQUE,
  description text,
  version text NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS tools (
  id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  tool_name text NOT NULL UNIQUE,
  version text NOT NULL,
  description text NOT NULL,
  input_schema text NOT NULL,
  output_schema text NOT NULL,
  side_effect_level tool_side_effect NOT NULL,
  approval_required boolean NOT NULL DEFAULT false,
  created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS agent_tools (
  agent_id uuid NOT NULL REFERENCES agents(id),
  tool_id uuid NOT NULL REFERENCES tools(id),
  created_at timestamptz NOT NULL DEFAULT now(),
  PRIMARY KEY (agent_id, tool_id)
);

CREATE TABLE IF NOT EXISTS tool_calls (
  id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  tool_id uuid NOT NULL REFERENCES tools(id),
  workflow_run_id text,
  status text NOT NULL,
  request_payload text,
  response_payload text,
  created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS approvals (
  id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  workflow_run_id text NOT NULL,
  requested_by text NOT NULL,
  reason text NOT NULL,
  status approval_status NOT NULL,
  decided_by text,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS knowledge_docs (
  id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  title text NOT NULL,
  source text NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS knowledge_chunks (
  id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  doc_id uuid NOT NULL REFERENCES knowledge_docs(id),
  chunk_index int NOT NULL,
  content text NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS policies (
  id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  name text NOT NULL,
  description text,
  rule_json text NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS audit_logs (
  id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  workflow_run_id text NOT NULL,
  action text NOT NULL,
  payload text,
  source_event_id text,
  created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS events_outbox (
  id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  producer_service text,
  aggregate_type text NOT NULL,
  aggregate_id text NOT NULL,
  event_type text NOT NULL,
  payload text NOT NULL,
  status text NOT NULL DEFAULT 'NEW',
  created_at timestamptz NOT NULL DEFAULT now(),
  published_at timestamptz,
  last_error text
);

ALTER TABLE audit_logs
  ADD COLUMN IF NOT EXISTS source_event_id text;

ALTER TABLE events_outbox
  ADD COLUMN IF NOT EXISTS producer_service text;

ALTER TABLE events_outbox
  ADD COLUMN IF NOT EXISTS published_at timestamptz;

ALTER TABLE events_outbox
  ADD COLUMN IF NOT EXISTS last_error text;

CREATE INDEX IF NOT EXISTS idx_tasks_tenant ON tasks(tenant_id);
CREATE INDEX IF NOT EXISTS idx_workflow_runs_status ON workflow_runs(status);
CREATE INDEX IF NOT EXISTS idx_workflow_steps_run ON workflow_steps(run_id);
CREATE INDEX IF NOT EXISTS idx_approvals_status ON approvals(status);
CREATE INDEX IF NOT EXISTS idx_tool_calls_run ON tool_calls(workflow_run_id);
CREATE INDEX IF NOT EXISTS idx_events_outbox_status_created ON events_outbox(status, created_at);
CREATE INDEX IF NOT EXISTS idx_events_outbox_service_status_created ON events_outbox(producer_service, status, created_at);
CREATE UNIQUE INDEX IF NOT EXISTS idx_audit_logs_source_event_id ON audit_logs(source_event_id) WHERE source_event_id IS NOT NULL;
