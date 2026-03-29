from app.agents.base import Agent
from app.models import AgentState, AgentOutput, WorkflowType
from app.tools import call_tool


class ExecutorAgent(Agent):
    name = "executor"

    def run(self, state: AgentState) -> AgentOutput:
        workflow_run_id = state.inputs.payload.get("workflow_run_id") or state.inputs.task_id
        if state.inputs.workflow == WorkflowType.EMAIL_TICKET:
            ticket_call = call_tool(
                "create_ticket",
                workflow_run_id,
                {
                    "subject": state.inputs.payload.get("subject", "Support request"),
                    "body": state.inputs.payload.get("body", ""),
                },
            )
            email_call = call_tool(
                "send_email_draft",
                workflow_run_id,
                {"to": state.inputs.payload.get("from", "customer@example.com"), "draft": "We received your request."},
            )
            state.tool_calls.extend([ticket_call, email_call])
            return AgentOutput(status="COMPLETED", data={"ticket": ticket_call.output, "draft": email_call.output})

        if state.inputs.workflow == WorkflowType.INVOICE_APPROVAL:
            extract_call = call_tool(
                "extract_invoice_mock",
                workflow_run_id,
                {"invoice_id": state.inputs.payload.get("invoice_id", "INV-001")},
            )
            state.tool_calls.append(extract_call)
            if state.requires_approval:
                approval_call = call_tool(
                    "submit_approval_request",
                    workflow_run_id,
                    {"reason": "Invoice exceeds threshold", "amount": state.inputs.payload.get("amount")},
                )
                state.tool_calls.append(approval_call)
            return AgentOutput(status="COMPLETED", data={"extracted": extract_call.output})

        return AgentOutput(status="SKIPPED", data={})
