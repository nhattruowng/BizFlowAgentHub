from __future__ import annotations

from enum import Enum
from typing import Any, Dict, List, Optional

from pydantic import BaseModel, Field


class WorkflowType(str, Enum):
    EMAIL_TICKET = "email-ticket-workflow"
    INVOICE_APPROVAL = "invoice-approval-workflow"
    POLICY_LOOKUP = "policy-lookup-workflow"


class AgentInput(BaseModel):
    tenant_id: str
    task_id: str
    workflow: WorkflowType
    payload: Dict[str, Any]
    correlation_id: Optional[str] = None


class AgentOutput(BaseModel):
    status: str
    data: Dict[str, Any] = Field(default_factory=dict)
    notes: Optional[str] = None


class ToolCall(BaseModel):
    tool_name: str
    input: Dict[str, Any]
    output: Optional[Dict[str, Any]] = None
    status: str = "PENDING"


class AgentState(BaseModel):
    inputs: AgentInput
    router: Optional[AgentOutput] = None
    planner: Optional[AgentOutput] = None
    context: Optional[AgentOutput] = None
    knowledge: Optional[AgentOutput] = None
    policy: Optional[AgentOutput] = None
    executor: Optional[AgentOutput] = None
    validator: Optional[AgentOutput] = None
    handoff: Optional[AgentOutput] = None
    monitoring: Optional[AgentOutput] = None
    tool_calls: List[ToolCall] = Field(default_factory=list)
    requires_approval: bool = False
    escalated: bool = False


class AgentRuntimeResponse(BaseModel):
    workflow: WorkflowType
    task_id: str
    correlation_id: Optional[str]
    status: str
    outputs: Dict[str, AgentOutput]
    tool_calls: List[ToolCall]
