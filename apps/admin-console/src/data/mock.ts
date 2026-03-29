export const stats = [
  { label: "Active Runs", value: "12" },
  { label: "Waiting Approval", value: "3" },
  { label: "Tools Registered", value: "7" },
  { label: "Agents Online", value: "9" },
];

export const tasks = [
  { id: "TSK-1001", type: "email_support", status: "QUEUED", owner: "demo-user" },
  { id: "TSK-1002", type: "invoice_approval", status: "WAITING_APPROVAL", owner: "finance" },
  { id: "TSK-1003", type: "policy_lookup", status: "COMPLETED", owner: "ops" },
];

export const approvals = [
  { id: "APR-2001", run: "WR-7781", reason: "Invoice > 1000", status: "PENDING" },
  { id: "APR-2002", run: "WR-7782", reason: "Sensitive tool", status: "PENDING" },
];

export const auditLogs = [
  { id: "AUD-01", run: "WR-7781", action: "TOOL_CALL", message: "create_ticket" },
  { id: "AUD-02", run: "WR-7782", action: "APPROVAL_REQUESTED", message: "invoice" },
];

export const tools = [
  { name: "create_ticket", level: "WRITE", approval: "NO" },
  { name: "send_email_draft", level: "WRITE", approval: "NO" },
  { name: "submit_approval_request", level: "WRITE", approval: "YES" },
];

export const agents = [
  { name: "Router Agent", status: "ONLINE" },
  { name: "Planner Agent", status: "ONLINE" },
  { name: "Policy Agent", status: "ONLINE" },
  { name: "Executor Agent", status: "ONLINE" },
];
