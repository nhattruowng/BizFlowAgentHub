from app.agents.base import Agent
from app.models import AgentState, AgentOutput


class ValidatorAgent(Agent):
    name = "validator"

    def run(self, state: AgentState) -> AgentOutput:
        issues = []
        if state.inputs.workflow.value.startswith("email") and not state.executor:
            issues.append("Missing executor output")
        if state.requires_approval:
            issues.append("Approval pending")
        status = "COMPLETED" if not issues else "WARNING"
        return AgentOutput(status=status, data={"issues": issues})
