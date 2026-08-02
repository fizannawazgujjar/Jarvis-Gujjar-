"""
Commander agent orchestrator.

It accepts a high-level request, decides which agents to run, dispatches subtasks, waits for results,
and aggregates them into a final response. This implementation supports two modes:
 - Celery-driven tasks (if CELERY_BROKER_URL is configured)
 - Lightweight Redis-based queue (fallback)

This is an initial, production-oriented implementation with timeouts and simple ranking.
"""
from __future__ import annotations
import os
import time
import json
import asyncio
from typing import List, Dict, Any, Optional
from .runner import submit_task, redis_client
from app.services.provider_interface import default_provider_manager

CELERY_BROKER = os.getenv("CELERY_BROKER_URL")
USE_CELERY = bool(CELERY_BROKER)

DEFAULT_TIMEOUT = 15.0


class Commander:
    def __init__(self, provider_manager=None):
        self.pm = provider_manager or default_provider_manager()

    async def handle_request(self, user_id: str, prompt: str, agents: Optional[List[str]] = None, timeout: float = DEFAULT_TIMEOUT) -> Dict[str, Any]:
        """High-level entry point: orchestrate across agents and return aggregated response.
        agents: list of agent names e.g. ["coding", "research", "vision"]. If omitted, choose defaults.
        """
        agents = agents or ["research", "coding"]
        task_ids = {}

        # Dispatch tasks
        for a in agents:
            payload = {"user_id": user_id, "input": {"prompt": prompt}}
            if USE_CELERY:
                # Celery tasks should be defined in celery_app.py as `run_agent_task`
                try:
                    from app.celery_app import celery_app
                    res = celery_app.send_task("app.celery_app.run_agent_task", args=(a, payload))
                    task_ids[a] = {"type": "celery", "id": res.id}
                except Exception:
                    # fallback to Redis queue
                    tid = submit_task(a, payload)
                    task_ids[a] = {"type": "redis", "id": tid}
            else:
                tid = submit_task(a, payload)
                task_ids[a] = {"type": "redis", "id": tid}

        # Await results with simple polling (for Redis tasks)
        deadline = time.time() + timeout
        results = {}
        while time.time() < deadline and len(results) < len(task_ids):
            for a, info in list(task_ids.items()):
                if a in results:
                    continue
                if info["type"] == "redis":
                    status = redis_client.hget(info["id"], "status")
                    if status == "done":
                        res = redis_client.hget(info["id"], "result")
                        try:
                            results[a] = json.loads(res) if res else {"result": None}
                        except Exception:
                            results[a] = {"result": res}
                    elif status == "failed":
                        err = redis_client.hget(info["id"], "error")
                        results[a] = {"error": err}
                else:
                    # For Celery we cannot poll here easily; rely on backend task status endpoints in production
                    # Mark as pending for now
                    results[a] = {"status": "submitted", "task_id": info["id"]}
            await asyncio.sleep(0.5)

        # Any missing results are considered timed out
        for a in agents:
            if a not in results:
                results[a] = {"error": "timeout"}

        # Aggregate: simple concatenation and confidence scoring by presence
        aggregated_texts = []
        for a in agents:
            r = results.get(a, {})
            if r.get("result"):
                aggregated_texts.append(f"[{a}] {r['result']}" if isinstance(r['result'], str) else f"[{a}] {json.dumps(r['result'])}")
            elif r.get("error"):
                aggregated_texts.append(f"[{a}] ERROR: {r.get('error')}" )
            else:
                aggregated_texts.append(f"[{a}] NO_RESULT")

        final = "\n\n".join(aggregated_texts)
        return {"aggregated": final, "parts": results}


commander = Commander()
