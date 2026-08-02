from fastapi import APIRouter, Request
from pydantic import BaseModel
from app.agents.commander import commander

router = APIRouter()

class CommanderRequest(BaseModel):
    user_id: str
    prompt: str
    agents: list[str] | None = None
    timeout: float | None = None


@router.post("/ask")
async def ask(req: CommanderRequest, request: Request):
    # Delegate to the Commander instance
    res = await commander.handle_request(req.user_id, req.prompt, agents=req.agents, timeout=req.timeout or 15.0)
    return res
