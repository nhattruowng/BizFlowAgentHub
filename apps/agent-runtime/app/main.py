from fastapi import FastAPI

from app.engine import AgentRuntimeEngine
from app.models import AgentInput, AgentRuntimeResponse

app = FastAPI(title="BizFlow Agent Runtime", version="0.1.0")
engine = AgentRuntimeEngine()


@app.get("/health")
async def health():
    return {"status": "ok"}


@app.post("/api/agent-runtime/run", response_model=AgentRuntimeResponse)
async def run_agents(agent_input: AgentInput) -> AgentRuntimeResponse:
    return engine.run(agent_input)
