"""
WASM runner prototype.

This runner executes a WASM module using the wasmtime CLI if available. It runs the wasm with a JSON payload
(provided on stdin) and returns the stdout output. Timeouts and memory limits are applied at the subprocess level.

Production note: use wasmtime .NET/Python embedding or a dedicated WASM runtime for tighter control.
"""
import subprocess
import tempfile
import os
from typing import Tuple

WASMTIME_CMD = os.getenv("WASMTIME_CMD", "wasmtime")


def run_wasm(wasm_bytes: bytes, input_json: str, timeout: int = 5) -> Tuple[int, str, str]:
    """Write wasm_bytes to a temp file and run with wasmtime, passing input_json on stdin.
    Returns (returncode, stdout, stderr)
    """
    with tempfile.TemporaryDirectory() as td:
        wasm_path = os.path.join(td, "plugin.wasm")
        with open(wasm_path, "wb") as f:
            f.write(wasm_bytes)
        try:
            proc = subprocess.run([WASMTIME_CMD, wasm_path, "--invoke", "handle"], input=input_json.encode("utf-8"), capture_output=True, timeout=timeout)
            return proc.returncode, proc.stdout.decode("utf-8", errors="ignore"), proc.stderr.decode("utf-8", errors="ignore")
        except subprocess.TimeoutExpired as e:
            return -1, "", "timeout"
        except FileNotFoundError:
            return -2, "", "wasmtime not installed"
