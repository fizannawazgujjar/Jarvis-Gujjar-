from fastapi import APIRouter
from pydantic import BaseModel
from app.agents.runner import submit_task

router = APIRouter()

class AgentTask(BaseModel):
    agent_name: str
    user_id: str
    input: dict

@router.post("/submit")
async def submit(task: AgentTask):
    task_id = submit_task(task.agent_name, {"user_id": task.user_id, "input": task.input})
    return {"task_id": task_id}
