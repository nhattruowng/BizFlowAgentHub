from typing import Dict

from app.agents.context import ContextAgent
from app.agents.executor import ExecutorAgent
from app.agents.handoff import HandoffAgent
from app.agents.knowledge import KnowledgeAgent
from app.agents.monitoring import MonitoringAgent
from app.agents.planner import PlannerAgent
from app.agents.policy import PolicyAgent
from app.agents.router import RouterAgent
from app.agents.validator import ValidatorAgent
from app.models import AgentInput, AgentRuntimeResponse, AgentState


class AgentRuntimeEngine:
    def __init__(self) -> None:
        self.router = RouterAgent()
        self.planner = PlannerAgent()
        self.context = ContextAgent()
        self.knowledge = KnowledgeAgent()
        self.policy = PolicyAgent()
        self.executor = ExecutorAgent()
        self.validator = ValidatorAgent()
        self.handoff = HandoffAgent()
        self.monitoring = MonitoringAgent()

    def run(self, agent_input: AgentInput) -> AgentRuntimeResponse:
        state = AgentState(inputs=agent_input)

        state.router = self.router.run(state)
        state.planner = self.planner.run(state)
        state.context = self.context.run(state)
        state.knowledge = self.knowledge.run(state)
        state.policy = self.policy.run(state)
        state.executor = self.executor.run(state)
        state.validator = self.validator.run(state)
        state.handoff = self.handoff.run(state)
        state.monitoring = self.monitoring.run(state)

        status = "COMPLETED"
        if state.requires_approval:
            status = "WAITING_APPROVAL"
        if state.escalated:
            status = "ESCALATED"

        outputs: Dict[str, object] = {
            "router": state.router,
            "planner": state.planner,
            "context": state.context,
            "knowledge": state.knowledge,
            "policy": state.policy,
            "executor": state.executor,
            "validator": state.validator,
            "handoff": state.handoff,
            "monitoring": state.monitoring,
        }

        return AgentRuntimeResponse(
            workflow=agent_input.workflow,
            task_id=agent_input.task_id,
            correlation_id=agent_input.correlation_id,
            status=status,
            outputs=outputs,
            tool_calls=state.tool_calls,
        )
