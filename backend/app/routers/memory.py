from fastapi import APIRouter, Request
from pydantic import BaseModel
from sqlmodel import SQLModel, Field, Session, create_engine, select
from app.models import Memory
from app.core.config import settings
from app.services import vector_store

router = APIRouter()

engine = create_engine(settings.DATABASE_URL, echo=False)
SQLModel.metadata.create_all(engine)

class IngestRequest(BaseModel):
    user_id: str
    content: str
    metadata: dict = {}


@router.post("/ingest")
async def ingest(req: IngestRequest, request: Request):
    """Ingest content into memory: compute embeddings via a provider and persist both a Memory row and a vector entry.
    """
    pm = getattr(request.app.state, "provider_manager", None)
    if pm is None:
        # Fallback: store without embedding
        mem = Memory(user_id=req.user_id, content_text=req.content, metadata=req.metadata)
        with Session(engine) as session:
            session.add(mem)
            session.commit()
            session.refresh(mem)
        return {"id": mem.id}

    # Compute embeddings using selected provider
    adapter = pm.select_for_task("embeddings")
    embeddings = await adapter.embeddings([req.content])
    embedding = embeddings[0] if embeddings else []

    # Persist memory record
    mem = Memory(user_id=req.user_id, content_text=req.content, metadata=req.metadata)
    with Session(engine) as session:
        session.add(mem)
        session.commit()
        session.refresh(mem)

    # Upsert vector into vector store (producer for semantic search)
    try:
        vector_store.upsert_vector(req.user_id, req.content, embedding, req.metadata)
    except Exception:
        # On error, continue (do not break ingestion); log in production
        pass

    return {"id": mem.id}


class QueryRequest(BaseModel):
    user_id: str
    query: str
    top_k: int = 5


@router.post("/query")
async def query(req: QueryRequest, request: Request):
    pm = getattr(request.app.state, "provider_manager", None)
    if pm is None:
        # Fallback: return latest memories
        with Session(engine) as session:
            results = session.exec(select(Memory).where(Memory.user_id == req.user_id).limit(req.top_k)).all()
        return {"results": [r.dict() for r in results]}

    adapter = pm.select_for_task("embeddings")
    embeddings = await adapter.embeddings([req.query])
    query_embedding = embeddings[0] if embeddings else []

    # Use vector store semantic search
    try:
        results = vector_store.semantic_search(req.user_id, query_embedding, top_k=req.top_k)
        return {"results": results}
    except Exception:
        # Fallback to simple DB query
        with Session(engine) as session:
            results = session.exec(select(Memory).where(Memory.user_id == req.user_id).limit(req.top_k)).all()
        return {"results": [r.dict() for r in results]}
