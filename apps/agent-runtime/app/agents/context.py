from app.agents.base import Agent
from app.models import AgentState, AgentOutput


class ContextAgent(Agent):
    name = "context"

    def run(self, state: AgentState) -> AgentOutput:
        context = {
            "tenant": state.inputs.tenant_id,
            "task": state.inputs.task_id,
            "summary": "Loaded tenant context and recent activity (mock).",
        }
        return AgentOutput(status="COMPLETED", data=context)
