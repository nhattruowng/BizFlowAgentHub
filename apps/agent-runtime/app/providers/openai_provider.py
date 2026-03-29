import os
from typing import Optional

from .base import ModelProvider


class OpenAIProvider(ModelProvider):
    def __init__(self, api_key: Optional[str] = None, model: str = "gpt-4o-mini"):
        self.api_key = api_key or os.getenv("OPENAI_API_KEY")
        self.model = model

    def generate(self, prompt: str) -> str:
        if not self.api_key:
            return "[openai-provider-disabled] No API key configured."
        # Placeholder: real integration should call OpenAI SDK.
        return f"[openai-provider-mock:{self.model}] {prompt[:120]}"
