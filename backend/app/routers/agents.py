from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from app.agents.runner import submit_task, redis_client

router = APIRouter()

class AgentTask(BaseModel):
    agent_name: str
    user_id: str
    input: dict

@router.post("/submit")
async def submit(task: AgentTask):
    task_id = submit_task(task.agent_name, {"user_id": task.user_id, "input": task.input})
    return {"task_id": task_id}

@router.get("/status/{task_id}")
async def task_status(task_id: str):
    # Return all stored metadata for a task
    data = redis_client.hgetall(task_id)
    if not data:
        raise HTTPException(status_code=404, detail="task not found")
    # Attempt to parse JSON fields
    for k in ["payload", "result", "error"]:
        if k in data:
            try:
                data[k] = json.loads(data[k])
            except Exception:
                pass
    return data
