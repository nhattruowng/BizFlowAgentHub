import os

WORKFLOW_ENGINE_URL = os.getenv("WORKFLOW_ENGINE_URL", "http://localhost:8082")
TOOL_HUB_URL = os.getenv("TOOL_HUB_URL", "http://localhost:8083")
APPROVAL_SERVICE_URL = os.getenv("APPROVAL_SERVICE_URL", "http://localhost:8084")
KNOWLEDGE_SERVICE_URL = os.getenv("KNOWLEDGE_SERVICE_URL", "http://localhost:8085")
