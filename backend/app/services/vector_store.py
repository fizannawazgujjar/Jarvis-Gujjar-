"""
Vector store adapter using Postgres + pgvector (recommended for production).
This module provides a simple abstraction for storing/retrieving embeddings.
"""
from typing import List, Dict, Any, Optional
from sqlalchemy import create_engine, Column, Integer, String, JSON
from sqlalchemy.orm import sessionmaker, declarative_base
import os

DATABASE_URL = os.getenv("DATABASE_URL", "postgresql+psycopg2://user:pass@localhost/jarvis")
Base = declarative_base()

class VectorItem(Base):
    __tablename__ = "memory_vectors"
    id = Column(Integer, primary_key=True, autoincrement=True)
    user_id = Column(String, index=True, nullable=False)
    content = Column(String, nullable=False)
    metadata = Column(JSON, nullable=True)
    embedding = Column(JSON, nullable=False)  # store as JSON array in a portable way

def init_db(url: Optional[str] = None):
    url = url or DATABASE_URL
    engine = create_engine(url, future=True)
    Base.metadata.create_all(engine)
    return sessionmaker(bind=engine)

SessionLocal = init_db()

def upsert_vector(user_id: str, content: str, embedding: List[float], metadata: Dict[str, Any] = None):
    session = SessionLocal()
    item = VectorItem(user_id=user_id, content=content, embedding=embedding, metadata=metadata or {})
    session.add(item)
    session.commit()
    session.close()
    return item.id

def semantic_search(user_id: str, query_embedding: List[float], top_k: int = 5) -> List[Dict[str, Any]]:
    """
    For pgvector use-case we would use SQL-level vector similarity (pgvector extension).
    Here we provide a portable fallback (cosine on JSON embeddings) suitable for dev.
    In production replace this with SQL using `embedding <-> query_embedding` and ORDER BY.
    """
    import math
    def cosine(a, b):
        dot = sum(x*y for x,y in zip(a,b))
        na = math.sqrt(sum(x*x for x in a))
        nb = math.sqrt(sum(x*x for x in b))
        return dot / (na*nb + 1e-8)

    session = SessionLocal()
    rows = session.query(VectorItem).filter(VectorItem.user_id == user_id).all()
    scored = []
    for r in rows:
        score = cosine(r.embedding, query_embedding)
        scored.append((score, r))
    scored.sort(key=lambda x: x[0], reverse=True)
    results = []
    for score, r in scored[:top_k]:
        results.append({"id": r.id, "content": r.content, "score": float(score), "metadata": r.metadata})
    session.close()
    return results
