from fastapi.testclient import TestClient
from app.main import app

client = TestClient(app)


def test_commander_endpoint():
    payload = {"user_id": "test-user", "prompt": "Hello Jarvis", "agents": ["research"], "timeout": 2}
    resp = client.post("/api/v1/commander/ask", json=payload)
    assert resp.status_code == 200
    data = resp.json()
    assert "aggregated" in data
    assert "parts" in data
