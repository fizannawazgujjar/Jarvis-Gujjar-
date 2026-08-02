from __future__ import annotations
import os
import time
import json
import asyncio
from typing import List, Dict, Any, Optional
from .runner import submit_task, redis_client
from app.services.provider_interface import default_provider_manager
from app.agents.ranker import rank_results

CELERY_BROKER = os.getenv("CELERY_BROKER_URL")
USE_CELERY = bool(CELERY_BROKER)

DEFAULT_TIMEOUT = 15.0


class Commander:
    def __init__(self, provider_manager=None):
        self.pm = provider_manager or default_provider_manager()

    async def _poll_redis_task(self, task_id: str, timeout: float) -> Dict[str, Any]:
        """Poll a Redis-backed task until completion, failure, or timeout."""
        loop = asyncio.get_running_loop()
        deadline = time.time() + timeout
        while time.time() < deadline:
            # Use executor for blocking Redis calls
            status = await loop.run_in_executor(None, lambda: redis_client.hget(task_id, "status"))
            if status == "done":
                res = await loop.run_in_executor(None, lambda: redis_client.hget(task_id, "result"))
                try:
                    return {"result": json.loads(res) if res else None}
                except Exception:
                    return {"result": res}
            if status == "failed":
                err = await loop.run_in_executor(None, lambda: redis_client.hget(task_id, "error"))
                return {"error": err}
            await asyncio.sleep(0.3)
        return {"error": "timeout"}

    async def handle_request(self, user_id: str, prompt: str, agents: Optional[List[str]] = None, timeout: float = DEFAULT_TIMEOUT) -> Dict[str, Any]:
        """High-level entry point: orchestrate across agents and return aggregated response.

        agents: list of agent names e.g. ["coding", "research", "vision"]. If omitted, choose defaults.
        """
        agents = agents or ["research", "coding"]
        task_infos: Dict[str, Dict[str, Any]] = {}

        # Dispatch tasks
        for a in agents:
            payload = {"user_id": user_id, "input": {"prompt": prompt}}
            if USE_CELERY:
                try:
                    from app.celery_app import celery_app
                    res = celery_app.send_task("app.celery_app.run_agent_task", args=(a, payload))
                    task_infos[a] = {"type": "celery", "id": res.id}
                except Exception:
                    tid = submit_task(a, payload)
                    task_infos[a] = {"type": "redis", "id": tid}
            else:
                tid = submit_task(a, payload)
                task_infos[a] = {"type": "redis", "id": tid}

        # Create polling coroutines for redis-backed tasks
        coros = {}
        for a, info in task_infos.items():
            if info["type"] == "redis":
                coros[a] = self._poll_redis_task(info["id"], timeout)
            else:
                # For Celery tasks we don't poll here; mark as submitted and include task id
                coros[a] = asyncio.sleep(0, result={"status": "submitted", "task_id": info["id"]})

        # Run all polling coroutines concurrently with overall timeout
        gathered = await asyncio.gather(*coros.values(), return_exceptions=True)
        results: Dict[str, Any] = {}
        for name, res in zip(coros.keys(), gathered):
            if isinstance(res, Exception):
                results[name] = {"error": str(res)}
            else:
                results[name] = res

        # Any missing results are considered timed out (unlikely due to polling)
        for a in agents:
            if a not in results:
                results[a] = {"error": "timeout"}

        # Aggregate & rank parts
        ranked = rank_results(results)
        aggregated_texts = []
        for name, part, score in ranked:
            # Normalize part content
            if part.get("result"):
                content = part["result"]
                if isinstance(content, dict) and "result" in content:
                    content = content["result"]
                aggregated_texts.append(f"[{name}] {content}")
            elif part.get("stdout"):
                aggregated_texts.append(f"[{name}] {part.get('stdout')}" )
            elif part.get("error"):
                aggregated_texts.append(f"[{name}] ERROR: {part.get('error')}" )
            else:
                aggregated_texts.append(f"[{name}] NO_RESULT")

        final = "\n\n".join(aggregated_texts)
        return {"aggregated": final, "parts": results, "ranked": [{"name": n, "score": s} for n,_,s in ranked]}


commander = Commander()
