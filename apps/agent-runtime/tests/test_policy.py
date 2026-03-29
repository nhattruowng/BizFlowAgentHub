from app.agents.policy import PolicyAgent
from app.models import AgentInput, AgentState, WorkflowType


def test_policy_flags_threshold():
    agent = PolicyAgent()
    state = AgentState(inputs=AgentInput(
        tenant_id="t1",
        task_id="task-1",
        workflow=WorkflowType.INVOICE_APPROVAL,
        payload={"amount": 2000},
    ))
    output = agent.run(state)
    assert output.data["requires_approval"] is True
