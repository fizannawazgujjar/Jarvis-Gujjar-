from fastapi import FastAPI
from app.routers import auth, providers, memory, agents, commander as commander_router
from app.core.config import settings
from app.services.provider_interface import default_provider_manager

app = FastAPI(title="JARVIS V3 - Core Backend", version="0.1.0")


@app.on_event("startup")
async def startup_event():
    # Initialize provider manager and attach to app state for global access
    provider_manager = default_provider_manager()
    app.state.provider_manager = provider_manager


@app.on_event("shutdown")
async def shutdown_event():
    # Placeholder for cleanup (close provider connections, flush queues, etc.)
    pass

app.include_router(auth.router, prefix="/api/v1/auth", tags=["auth"])
app.include_router(providers.router, prefix="/api/v1/providers", tags=["providers"])
app.include_router(memory.router, prefix="/api/v1/memory", tags=["memory"])
app.include_router(agents.router, prefix="/api/v1/agents", tags=["agents"])
app.include_router(commander_router.router, prefix="/api/v1/commander", tags=["commander"])


@app.get("/")
async def root():
    return {"status": "ok", "service": "jarvis-core"}
