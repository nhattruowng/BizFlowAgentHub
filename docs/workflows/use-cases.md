# Workflow Use Cases

## Use Case 1: Email / Ticket Automation
1. Task intake receives support email.
2. Router Agent classifies as support request.
3. Planner Agent creates plan.
4. Executor Agent calls `create_ticket` and `send_email_draft` tools.
5. Validator Agent checks outputs.
6. Audit records are created.

Expected Output:
- Ticket ID
- Draft email response
- Audit log entries

## Use Case 2: Invoice OCR Approval
1. Task intake receives invoice payload.
2. Router Agent routes to invoice approval workflow.
3. Executor Agent calls `extract_invoice_mock`.
4. Policy Agent checks amount threshold.
5. If over threshold, Approval Service is triggered.
6. Workflow waits in `WAITING_APPROVAL`.

Expected Output:
- Extracted invoice data
- Approval request created

## Use Case 3: Policy Lookup Assistant
1. User submits internal query.
2. Router Agent selects policy lookup workflow.
3. Knowledge Agent searches Knowledge Service.
4. Response includes citations from knowledge chunks.
5. Audit logs and trace info stored.
