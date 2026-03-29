from app.agents.base import Agent
from app.models import AgentState, AgentOutput, WorkflowType


class PlannerAgent(Agent):
    name = "planner"

    def run(self, state: AgentState) -> AgentOutput:
        workflow = state.inputs.workflow
        if workflow == WorkflowType.INVOICE_APPROVAL:
            plan = ["Extract invoice data", "Check policy", "Request approval", "Finalize"]
        elif workflow == WorkflowType.POLICY_LOOKUP:
            plan = ["Search knowledge base", "Summarize answer", "Provide citations"]
        else:
            plan = ["Create ticket", "Draft response", "Log audit"]
        return AgentOutput(status="COMPLETED", data={"plan": plan})
