# Backend README

This folder contains the core backend for JARVIS V3 implemented with FastAPI.

Run locally (dev):

1. python -m venv .venv
2. source .venv/bin/activate
3. pip install -r backend/requirements.txt
4. uvicorn app.main:app --reload --reload-dir backend/app --port 8000

Notes:
- Development uses SQLite by default. Configure DATABASE_URL in backend/.env to use Postgres with pgvector in production.
- The memory service currently stores plain text and a placeholder embedding.

Running workers (Celery)

- To run a Celery worker locally via docker-compose (recommended for integration):
  docker-compose -f infra/docker-compose.yml up --build worker

- Or run a local worker in your virtualenv:
  CELERY_BROKER_URL=redis://localhost:6379/0 celery -A app.celery_app.celery_app worker --loglevel=info

Plugin example (hello plugin)

- A minimal placeholder WASM artifact (base64) is included at backend/plugins/examples/hello_plugin/plugin.wasm.base64. This is a tiny placeholder module; build real plugins using Rust/wasm32-wasi or other toolchains.
- To sign and install a plugin locally:
  python backend/plugins/cli_sign.py gen-keys --out keys.json
  python backend/plugins/cli_sign.py sign --manifest backend/plugins/examples/hello_plugin/manifest.json --key keys.json --out sig.txt
  WASM_BASE64=$(cat backend/plugins/examples/hello_plugin/plugin.wasm.base64)
  curl -X POST http://localhost:8000/api/v1/plugins/install -H "Content-Type: application/json" -d '{"manifest": $(cat backend/plugins/examples/hello_plugin/manifest.json), "signature": "'$(cat sig.txt)'", "publisher_key": "'$(jq -r .public_key keys.json)'", "wasm_base64": "'