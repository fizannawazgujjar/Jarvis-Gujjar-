import pytest
from app.services.provider_interface import default_provider_manager


def test_default_provider_manager():
    pm = default_provider_manager()
    assert pm is not None
    # Ensure adapters expose metadata and expected capabilities
    ids = [a.metadata()["id"] for a in pm.adapters]
    assert "openai" in ids
    assert "local" in ids

    # Select provider by capability
    adapter = pm.select_for_task("embeddings")
    assert hasattr(adapter, "embeddings")
