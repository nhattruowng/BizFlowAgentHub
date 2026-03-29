from typing import Any, Dict, List

import httpx

from app.config import TOOL_HUB_URL, APPROVAL_SERVICE_URL, KNOWLEDGE_SERVICE_URL
from app.models import ToolCall


def call_tool(tool_name: str, workflow_run_id: str, payload: Dict[str, Any]) -> ToolCall:
    request = {"workflowRunId": workflow_run_id, "input": payload}
    try:
        response = httpx.post(f"{TOOL_HUB_URL}/api/tools/{tool_name}/invoke", json=request, timeout=5.0)
        response.raise_for_status()
        data = response.json()
        return ToolCall(tool_name=tool_name, input=payload, output=data.get("output"), status=data.get("status", "UNKNOWN"))
    except Exception as exc:
        return ToolCall(tool_name=tool_name, input=payload, output={"error": str(exc)}, status="FAILED")


def submit_approval(workflow_run_id: str, reason: str, requested_by: str) -> Dict[str, Any]:
    payload = {"workflowRunId": workflow_run_id, "reason": reason, "requestedBy": requested_by}
    try:
        response = httpx.post(f"{APPROVAL_SERVICE_URL}/api/approvals", json=payload, timeout=5.0)
        response.raise_for_status()
        return response.json()
    except Exception as exc:
        return {"error": str(exc)}


def search_knowledge(query: str, limit: int = 3) -> List[Dict[str, Any]]:
    payload = {"query": query, "limit": limit}
    try:
        response = httpx.post(f"{KNOWLEDGE_SERVICE_URL}/api/knowledge/search", json=payload, timeout=5.0)
        response.raise_for_status()
        return response.json()
    except Exception as exc:
        return [{"error": str(exc)}]
