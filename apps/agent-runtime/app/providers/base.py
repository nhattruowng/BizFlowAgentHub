from typing import Protocol


class ModelProvider(Protocol):
    def generate(self, prompt: str) -> str:
        ...


class MockProvider:
    def generate(self, prompt: str) -> str:
        return f"[mock-llm] {prompt[:120]}"
