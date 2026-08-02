import json
import base64
from fastapi.testclient import TestClient
from app.main import app
from nacl.signing import SigningKey

client = TestClient(app)


def test_plugin_install_endpoint(tmp_path):
    # Prepare manifest
    manifest = {
        "id": "example.test",
        "name": "Example Test Plugin",
        "version": "0.0.1",
        "author": "Test",
        "description": "Test plugin",
        "entrypoint": "plugin.wasm",
        "permissions": ["execute"],
        "compatibility": {"app_version": ">=0.1.0"}
    }

    # Canonicalize manifest bytes as the server does
    manifest_bytes = json.dumps(manifest, separators=(",", ":")).encode("utf-8")

    # Generate a key pair and sign
    sk = SigningKey.generate()
    pk = sk.verify_key
    sig = sk.sign(manifest_bytes).signature
    sig_b64 = base64.b64encode(sig).decode('utf-8')
    pk_b64 = base64.b64encode(bytes(pk)).decode('utf-8')

    # Use the placeholder wasm base64 included in repo
    with open('backend/plugins/examples/hello_plugin/plugin.wasm.base64', 'r', encoding='utf-8') as f:
        wasm_b64 = f.read().strip()

    payload = {
        "manifest": manifest,
        "signature": sig_b64,
        "publisher_key": pk_b64,
        "wasm_base64": wasm_b64
    }

    resp = client.post('/api/v1/plugins/install', json=payload)
    assert resp.status_code == 200
    data = resp.json()
    assert data.get('status') == 'installed'
    assert data.get('id') == manifest['id']
