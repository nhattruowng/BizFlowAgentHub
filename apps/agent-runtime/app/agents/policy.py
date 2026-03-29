from app.agents.base import Agent
from app.models import AgentState, AgentOutput, WorkflowType


class PolicyAgent(Agent):
    name = "policy"

    def run(self, state: AgentState) -> AgentOutput:
        if state.inputs.workflow != WorkflowType.INVOICE_APPROVAL:
            return AgentOutput(status="SKIPPED", data={})
        amount = float(state.inputs.payload.get("amount", 0))
        threshold = 1000.0
        needs_approval = amount > threshold
        state.requires_approval = needs_approval
        return AgentOutput(
            status="COMPLETED",
            data={"amount": amount, "threshold": threshold, "requires_approval": needs_approval},
        )
