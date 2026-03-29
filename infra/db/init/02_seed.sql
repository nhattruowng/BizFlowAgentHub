-- Seed data for BizFlow Agent Hub

INSERT INTO tenants (id, name, status) VALUES
  ('11111111-1111-1111-1111-111111111111', 'Demo Tenant', 'ACTIVE')
ON CONFLICT DO NOTHING;

INSERT INTO roles (id, name) VALUES
  ('22222222-2222-2222-2222-222222222221', 'ADMIN'),
  ('22222222-2222-2222-2222-222222222222', 'OPERATOR'),
  ('22222222-2222-2222-2222-222222222223', 'SUPERVISOR')
ON CONFLICT DO NOTHING;

INSERT INTO users (id, tenant_id, email, name, status) VALUES
  ('33333333-3333-3333-3333-333333333331', '11111111-1111-1111-1111-111111111111', 'admin@demo.local', 'Demo Admin', 'ACTIVE'),
  ('33333333-3333-3333-3333-333333333332', '11111111-1111-1111-1111-111111111111', 'ops@demo.local', 'Ops User', 'ACTIVE')
ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_id, role_id) VALUES
  ('33333333-3333-3333-3333-333333333331', '22222222-2222-2222-2222-222222222221'),
  ('33333333-3333-3333-3333-333333333332', '22222222-2222-2222-2222-222222222222')
ON CONFLICT DO NOTHING;

INSERT INTO workflows (id, name, description) VALUES
  ('44444444-4444-4444-4444-444444444441', 'email-ticket-workflow', 'Email / ticket automation'),
  ('44444444-4444-4444-4444-444444444442', 'invoice-approval-workflow', 'Invoice OCR and approval'),
  ('44444444-4444-4444-4444-444444444443', 'policy-lookup-workflow', 'Policy lookup assistant')
ON CONFLICT DO NOTHING;

INSERT INTO agents (id, name, description, version) VALUES
  ('55555555-5555-5555-5555-555555555551', 'Router Agent', 'Classifies incoming tasks', 'v1'),
  ('55555555-5555-5555-5555-555555555552', 'Planner Agent', 'Builds execution plan', 'v1'),
  ('55555555-5555-5555-5555-555555555553', 'Context Agent', 'Loads context', 'v1'),
  ('55555555-5555-5555-5555-555555555554', 'Knowledge Agent', 'Retrieves knowledge', 'v1'),
  ('55555555-5555-5555-5555-555555555555', 'Policy Agent', 'Checks policies', 'v1'),
  ('55555555-5555-5555-5555-555555555556', 'Executor Agent', 'Executes tools', 'v1'),
  ('55555555-5555-5555-5555-555555555557', 'Validator Agent', 'Validates outputs', 'v1'),
  ('55555555-5555-5555-5555-555555555558', 'Handoff Agent', 'Escalates approvals', 'v1'),
  ('55555555-5555-5555-5555-555555555559', 'Monitoring Agent', 'Observability summary', 'v1')
ON CONFLICT DO NOTHING;

INSERT INTO tools (id, tool_name, version, description, input_schema, output_schema, side_effect_level, approval_required) VALUES
  ('66666666-6666-6666-6666-666666666661', 'send_email_draft', 'v1', 'Draft email response', '{"type":"object"}', '{"type":"object"}', 'WRITE', false),
  ('66666666-6666-6666-6666-666666666662', 'create_ticket', 'v1', 'Create support ticket', '{"type":"object"}', '{"type":"object"}', 'WRITE', false),
  ('66666666-6666-6666-6666-666666666663', 'read_policy_docs', 'v1', 'Read policy docs', '{"type":"object"}', '{"type":"object"}', 'READ', false),
  ('66666666-6666-6666-6666-666666666664', 'db_query_readonly', 'v1', 'Readonly DB query', '{"type":"object"}', '{"type":"object"}', 'READ', false),
  ('66666666-6666-6666-6666-666666666665', 'extract_invoice_mock', 'v1', 'Extract invoice data', '{"type":"object"}', '{"type":"object"}', 'READ', false),
  ('66666666-6666-6666-6666-666666666666', 'submit_approval_request', 'v1', 'Submit approval', '{"type":"object"}', '{"type":"object"}', 'WRITE', true),
  ('66666666-6666-6666-6666-666666666667', 'notify_user', 'v1', 'Notify user', '{"type":"object"}', '{"type":"object"}', 'WRITE', false)
ON CONFLICT DO NOTHING;

INSERT INTO knowledge_docs (id, title, source) VALUES
  ('77777777-7777-7777-7777-777777777771', 'Invoice Policy', 'internal/policies/invoice.md'),
  ('77777777-7777-7777-7777-777777777772', 'Support SLA', 'internal/policies/support-sla.md')
ON CONFLICT DO NOTHING;

INSERT INTO knowledge_chunks (id, doc_id, chunk_index, content) VALUES
  ('88888888-8888-8888-8888-888888888881', '77777777-7777-7777-7777-777777777771', 0, 'Invoices above 1000 require supervisor approval.'),
  ('88888888-8888-8888-8888-888888888882', '77777777-7777-7777-7777-777777777772', 0, 'Support SLA: initial response within 4 business hours.')
ON CONFLICT DO NOTHING;

INSERT INTO policies (id, name, description, rule_json) VALUES
  ('99999999-9999-9999-9999-999999999991', 'Invoice Threshold', 'Auto approval threshold', '{"max_amount":1000}')
ON CONFLICT DO NOTHING;

INSERT INTO approvals (id, workflow_run_id, requested_by, reason, status) VALUES
  ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'WR-7781', 'demo-user', 'Invoice > 1000', 'PENDING')
ON CONFLICT DO NOTHING;
