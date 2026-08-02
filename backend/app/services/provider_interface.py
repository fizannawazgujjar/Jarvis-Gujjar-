"""
Provider adapter interface and manager.

Each provider adapter implements:
- chat_stream(messages, model, **options)
- embeddings(texts)
- metadata()

ProviderManager selects provider by capability and fallback policy.
"""
from __future__ import annotations
from typing import List, Dict, Any, Protocol, Optional
import os
import httpx
import asyncio

class ProviderAdapter(Protocol):
    async def chat_stream(self, messages: List[Dict[str, Any]], model: str, **options) -> Any: ...
    async def embeddings(self, texts: List[str]) -> List[List[float]]: ...
    def metadata(self) -> Dict[str, Any]: ...

class OpenAIAdapter:
    def __init__(self, api_key: Optional[str] = None, base_url: Optional[str] = None):
        self.api_key = api_key or os.getenv("OPENAI_API_KEY")
        self.base_url = base_url or "https://api.openai.com/v1"

    async def chat_stream(self, messages, model="gpt-4o-mini", **options):
        # streaming via SSE or chunked responses would be implemented here.
        headers = {"Authorization": f"Bearer {self.api_key}"}
        async with httpx.AsyncClient(timeout=60) as client:
            payload = {"model": model, "messages": messages}
            r = await client.post(f"{self.base_url}/chat/completions", json=payload, headers=headers)
            r.raise_for_status()
            return r.json()

    async def embeddings(self, texts: List[str]) -> List[List[float]]:
        headers = {"Authorization": f"Bearer {self.api_key}"}
        async with httpx.AsyncClient(timeout=60) as client:
            payload = {"model": "text-embedding-3-small", "input": texts}
            r = await client.post(f"{self.base_url}/embeddings", json=payload, headers=headers)
            r.raise_for_status()
            return [item["embedding"] for item in r.json().get("data", [])]

    def metadata(self):
        return {"id": "openai", "name": "OpenAI", "capabilities": ["chat", "embeddings", "vision"]}

class LocalLLMAdapter:
    def __init__(self, endpoint: str = "http://localhost:11434"):
        # Expect a local adapter (Ollama/llama-bridge) exposing a simple REST API
        self.endpoint = endpoint

    async def chat_stream(self, messages, model="local", **options):
        async with httpx.AsyncClient(timeout=60) as client:
            payload = {"model": model, "messages": messages}
            r = await client.post(f"{self.endpoint}/v1/chat", json=payload)
            r.raise_for_status()
            return r.json()

    async def embeddings(self, texts: List[str]) -> List[List[float]]:
        async with httpx.AsyncClient(timeout=60) as client:
            payload = {"texts": texts}
            r = await client.post(f"{self.endpoint}/v1/embeddings", json=payload)
            r.raise_for_status()
            return r.json().get("embeddings", [])

    def metadata(self):
        return {"id": "local", "name": "LocalLLM", "capabilities": ["chat", "embeddings"]}

class ProviderManager:
    def __init__(self, adapters: Optional[List[ProviderAdapter]] = None):
        self.adapters = adapters or []
        self._index = {a.metadata()["id"]: a for a in self.adapters}

    def get(self, provider_id: str) -> ProviderAdapter:
        return self._index[provider_id]

    def select_for_task(self, capability: str, prefer: Optional[str] = None) -> ProviderAdapter:
        # Simple selection: prefer explicit provider, else choose first that has capability
        if prefer and prefer in self._index:
            return self._index[prefer]
        for a in self.adapters:
            if capability in a.metadata().get("capabilities", []):
                return a
        raise RuntimeError("No provider available for capability: " + capability)

# Factory to create a default manager used by the app
def default_provider_manager() -> ProviderManager:
    adapters = [
        OpenAIAdapter(),
        LocalLLMAdapter()
    ]
    return ProviderManager(adapters)
