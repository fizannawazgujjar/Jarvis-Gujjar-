from fastapi import APIRouter, Request

router = APIRouter()


@router.get("/")
async def list_providers(request: Request):
    """Return configured provider adapters and metadata from the ProviderManager attached to app state.
    """
    pm = getattr(request.app.state, "provider_manager", None)
    if pm is None:
        return {"providers": []}
    providers = [a.metadata() for a in pm.adapters]
    return {"providers": providers}
