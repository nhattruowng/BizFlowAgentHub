from app.agents.base import Agent
from app.models import AgentState, AgentOutput


class MonitoringAgent(Agent):
    name = "monitoring"

    def run(self, state: AgentState) -> AgentOutput:
        summary = {
            "tool_calls": len(state.tool_calls),
            "requires_approval": state.requires_approval,
            "escalated": state.escalated,
        }
        return AgentOutput(status="COMPLETED", data=summary)
