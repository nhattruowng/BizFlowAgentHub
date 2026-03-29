from abc import ABC, abstractmethod

from app.models import AgentState, AgentOutput


class Agent(ABC):
    name: str

    @abstractmethod
    def run(self, state: AgentState) -> AgentOutput:
        raise NotImplementedError
