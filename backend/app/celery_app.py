"""
Celery app factory and tasks.

This file defines a Celery application and a lightweight task to run an agent.
The task will use a provider adapter (default manager) to produce a response for simple agents.
"""
from celery import Celery
import os
from typing import Dict, Any
from app.services.provider_interface import default_provider_manager

CELERY_BROKER = os.getenv("CELERY_BROKER_URL", os.getenv("REDIS_URL", "redis://localhost:6379/0"))
celery_app = Celery("jarvis", broker=CELERY_BROKER)
celery_app.conf.task_soft_time_limit = 30

@celery_app.task(name="app.celery_app.run_agent_task")
def run_agent_task(agent_name: str, payload: Dict[str, Any]) -> Dict[str, Any]:
    """A simple agent runner used by Celery workers. For complex agents implement separate services.
    This function selects a provider by capability and asks it to produce a chat response.
    """
    pm = default_provider_manager()
    # Map agent_name to capability (simple mapping)
    capability_map = {
        "coding": "chat",
        "research": "chat",
        "vision": "vision",
        "default": "chat",
    }
    cap = capability_map.get(agent_name, "chat")
    adapter = pm.select_for_task(cap)
    messages = [
        {"role": "system", "content": f"You are the {agent_name} agent. Respond concisely."},
        {"role": "user", "content": payload.get("input", {}).get("prompt", "")}
    ]
    try:
        res = adapter.chat_stream(messages)
        # If adapter.chat_stream is async return placeholder structure
        if hasattr(res, "__await__"):
            # run sync since Celery task is sync
            import asyncio
            res = asyncio.get_event_loop().run_until_complete(res)
        # Normalize response
        if isinstance(res, dict):
            # Try to extract usual OpenAI-like structure
            choices = res.get("choices")
            if choices and isinstance(choices, list):
                text = choices[0].get("message", {}).get("content") or choices[0].get("text")
                return {"result": text}
            # fallback entire payload
            return {"result": str(res)}
        return {"result": str(res)}
    except Exception as e:
        return {"error": str(e)}
