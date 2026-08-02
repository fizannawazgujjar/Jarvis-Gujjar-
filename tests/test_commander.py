import json
from app.agents.commander import Commander


def test_commander_basic(monkeypatch):
    # Provide a fake provider manager that returns a dummy adapter
    class DummyAdapter:
        def metadata(self):
            return {"id": "dummy", "capabilities": ["chat", "embeddings"]}
        async def embeddings(self, texts):
            return [[0.1]*4 for _ in texts]
        async def chat_stream(self, messages, model="", **options):
            return {"choices": [{"message": {"content": "response from dummy"}}]}

    class DummyPM:
        def __init__(self):
            self.adapters = [DummyAdapter()]
        def select_for_task(self, cap, prefer=None):
            return DummyAdapter()

    cmd = Commander(provider_manager=DummyPM())
    res = __import__('asyncio').get_event_loop().run_until_complete(cmd.handle_request('u1', 'hello', agents=['research']))
    assert 'aggregated' in res
    assert 'research' in res['parts']

