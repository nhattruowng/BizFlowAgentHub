from app.agents.base import Agent
from app.models import AgentState, AgentOutput, WorkflowType
from app.tools import search_knowledge


class KnowledgeAgent(Agent):
    name = "knowledge"

    def run(self, state: AgentState) -> AgentOutput:
        if state.inputs.workflow != WorkflowType.POLICY_LOOKUP:
            return AgentOutput(status="SKIPPED", data={})
        query = state.inputs.payload.get("query", "policy")
        results = search_knowledge(query)
        return AgentOutput(status="COMPLETED", data={"results": results})
