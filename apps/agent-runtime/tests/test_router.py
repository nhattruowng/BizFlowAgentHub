from app.agents.router import RouterAgent
from app.models import AgentInput, AgentState, WorkflowType


def test_router_classifies():
    agent = RouterAgent()
    state = AgentState(inputs=AgentInput(
        tenant_id="t1",
        task_id="task-1",
        workflow=WorkflowType.EMAIL_TICKET,
        payload={"type": "support_request"},
    ))
    output = agent.run(state)
    assert output.data["classification"] == "support_request"
