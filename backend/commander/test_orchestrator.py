import asyncio

import pytest
from backend.commander.orchestrator import orchestrate

@pytest.mark.asyncio
async def test_orchestrator_success(monkeypatch):
    # Make run_subtask deterministic by patching random.random and sleep to be fast
    monkeypatch.setattr('random.random', lambda: 0.5)
    monkeypatch.setattr('asyncio.sleep', lambda *_: asyncio.sleep(0))

    subtasks = [
        {"name": "t1", "payload": {"input": "x"}},
        {"name": "t2", "payload": {"input": "y"}},
    ]
    out = await orchestrate('task-1', subtasks)
    assert out['task_id'] == 'task-1'
    assert len(out['results']) == 2
    assert all('success' in r for r in out['results'])

@pytest.mark.asyncio
async def test_orchestrator_retries(monkeypatch):
    # Force a failure on first attempt then success
    calls = {'n': 0}

    async def fake_sleep(sec):
        return

    def fake_random():
        calls['n'] += 1
        # first call -> cause provider error (<0.1), next calls ok
        return 0.05 if calls['n'] == 1 else 0.5

    monkeypatch.setattr('random.random', fake_random)
    monkeypatch.setattr('asyncio.sleep', fake_sleep)

    subtasks = [{"name": "t1", "payload": {"input": "z"}, "timeout": 2.0}]
    out = await orchestrate('task-2', subtasks)
    assert out['task_id'] == 'task-2'
    assert len(out['results']) == 1
    # eventual success expected after retry
    assert any(r.get('success') for r in out['results'])
