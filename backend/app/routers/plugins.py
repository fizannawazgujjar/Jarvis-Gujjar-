from fastapi import APIRouter, HTTPException, Request
from pydantic import BaseModel
from app.plugins.manifest import validate_manifest
from app.plugins.signature import verify_manifest_signature
from app.plugins.runner_wasm import run_wasm
import os
import json
import base64

router = APIRouter()

PLUGINS_DIR = os.getenv("JARVIS_PLUGINS_DIR", "./backend/plugins")
os.makedirs(PLUGINS_DIR, exist_ok=True)

class InstallRequest(BaseModel):
    manifest: dict
    signature: str | None = None
    publisher_key: str | None = None
    wasm_base64: str | None = None

@router.post("/install")
async def install(req: InstallRequest):
    # Validate manifest schema
    try:
        validate_manifest(req.manifest)
    except Exception as e:
        raise HTTPException(status_code=400, detail=f"invalid manifest: {e}")

    plugin_id = req.manifest.get("id")
    dirpath = os.path.join(PLUGINS_DIR, plugin_id)
    os.makedirs(dirpath, exist_ok=True)

    manifest_path = os.path.join(dirpath, "manifest.json")
    with open(manifest_path, "w", encoding="utf-8") as f:
        json.dump(req.manifest, f, indent=2)

    # If signature provided verify
    if req.signature and req.publisher_key:
        try:
            verify_manifest_signature(json.dumps(req.manifest, separators=(",", ":")).encode("utf-8"), req.signature, req.publisher_key)
        except Exception as e:
            raise HTTPException(status_code=400, detail=f"signature verification failed: {e}")

    # If wasm provided store artifact
    if req.wasm_base64:
        try:
            wasm_bytes = base64.b64decode(req.wasm_base64)
            with open(os.path.join(dirpath, "plugin.wasm"), "wb") as wf:
                wf.write(wasm_bytes)
        except Exception as e:
            raise HTTPException(status_code=400, detail=f"invalid wasm artifact: {e}")

    return {"status": "installed", "id": plugin_id}

@router.get("/")
async def list_plugins():
    items = []
    for name in os.listdir(PLUGINS_DIR):
        mpath = os.path.join(PLUGINS_DIR, name, "manifest.json")
        if os.path.exists(mpath):
            with open(mpath, "r", encoding="utf-8") as f:
                try:
                    m = json.load(f)
                except Exception:
                    m = {"id": name}
            items.append(m)
    return {"plugins": items}

class RunRequest(BaseModel):
    input: dict

@router.post("/{plugin_id}/run")
async def run_plugin(plugin_id: str, req: RunRequest):
    dirpath = os.path.join(PLUGINS_DIR, plugin_id)
    wasm_path = os.path.join(dirpath, "plugin.wasm")
    if not os.path.exists(wasm_path):
        raise HTTPException(status_code=404, detail="wasm artifact not found")
    with open(wasm_path, "rb") as f:
        wasm_bytes = f.read()

    ret, out, err = run_wasm(wasm_bytes, json.dumps(req.input), timeout=10)
    if ret == -1:
        raise HTTPException(status_code=504, detail="plugin timeout")
    if ret == -2:
        raise HTTPException(status_code=500, detail="wasmtime not installed on server")
    if err:
        # Return stderr as part of response for debugging
        return {"returncode": ret, "stderr": err, "stdout": out}
    return {"returncode": ret, "stdout": out}
