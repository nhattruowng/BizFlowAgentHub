from app.agents.context import ContextAgent
from app.agents.executor import ExecutorAgent
from app.agents.handoff import HandoffAgent
from app.agents.knowledge import KnowledgeAgent
from app.agents.monitoring import MonitoringAgent
from app.agents.planner import PlannerAgent
from app.agents.policy import PolicyAgent
from app.agents.router import RouterAgent
from app.agents.validator import ValidatorAgent
from app.models import AgentInput, AgentState, WorkflowType


def build_state(workflow: WorkflowType, payload=None):
    return AgentState(inputs=AgentInput(
        tenant_id="t1",
        task_id="task-1",
        workflow=workflow,
        payload=payload or {},
    ))


def test_agents_smoke():
    state = build_state(WorkflowType.EMAIL_TICKET, {"subject": "Help"})
    assert RouterAgent().run(state).status == "COMPLETED"
    assert PlannerAgent().run(state).status == "COMPLETED"
    assert ContextAgent().run(state).status == "COMPLETED"
    assert KnowledgeAgent().run(state).status in {"SKIPPED", "COMPLETED"}
    assert PolicyAgent().run(state).status in {"SKIPPED", "COMPLETED"}
    assert ExecutorAgent().run(state).status in {"COMPLETED", "SKIPPED"}
    assert ValidatorAgent().run(state).status in {"COMPLETED", "WARNING"}
    assert HandoffAgent().run(state).status in {"SKIPPED", "WAITING_APPROVAL"}
    assert MonitoringAgent().run(state).status == "COMPLETED"
