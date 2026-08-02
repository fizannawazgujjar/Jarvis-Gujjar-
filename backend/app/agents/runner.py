"""
Agent runner: enqueue tasks and run workers.
Uses Redis as a simple queue for reliability. Production recommendation: use Celery or Temporal.
"""
import os
import json
import time
from typing import Dict, Any, Callable
import redis

REDIS_URL = os.getenv("REDIS_URL", "redis://localhost:6379/0")
redis_client = redis.Redis.from_url(REDIS_URL, decode_responses=True)

TASK_QUEUE = "jarvis:tasks"

def submit_task(agent_name: str, payload: Dict[str, Any]) -> str:
    task = {"agent": agent_name, "payload": payload, "created_at": time.time()}
    task_id = f"task:{int(time.time()*1000)}"
    redis_client.hset(task_id, mapping={"status":"queued", "payload": json.dumps(task)})
    redis_client.rpush(TASK_QUEUE, task_id)
    return task_id

def worker_loop(handler: Callable[[Dict[str, Any]], Dict[str, Any]], poll_interval: float = 0.5):
    print("Agent worker started")
    while True:
        item = redis_client.lpop(TASK_QUEUE)
        if not item:
            time.sleep(poll_interval)
            continue
        data = redis_client.hget(item, "payload")
        if not data:
            continue
        task = json.loads(data)
        redis_client.hset(item, "status", "running")
        try:
            result = handler(task)
            redis_client.hset(item, "status", "done")
            redis_client.hset(item, "result", json.dumps(result))
        except Exception as e:
            redis_client.hset(item, "status", "failed")
            redis_client.hset(item, "error", str(e))
