from app.agents.base import Agent
from app.models import AgentState, AgentOutput


class HandoffAgent(Agent):
    name = "handoff"

    def run(self, state: AgentState) -> AgentOutput:
        if state.requires_approval:
            state.escalated = True
            return AgentOutput(status="WAITING_APPROVAL", data={"message": "Approval required."})
        return AgentOutput(status="SKIPPED", data={})
