from app.agents.base import Agent
from app.models import AgentState, AgentOutput


class RouterAgent(Agent):
    name = "router"

    def run(self, state: AgentState) -> AgentOutput:
        payload = state.inputs.payload
        classification = payload.get("type") or state.inputs.workflow.value
        return AgentOutput(status="COMPLETED", data={"classification": classification})
