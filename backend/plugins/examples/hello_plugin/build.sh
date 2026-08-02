#!/usr/bin/env bash
set -euo pipefail
# Build script for the example hello plugin (Rust -> wasm32-wasi)
# Requires Rust toolchain installed and wasm32-wasi target added.
# Usage: ./build.sh

CRATE_DIR=$(cd "$(dirname "$0")" && pwd)
cd "$CRATE_DIR"

if ! command -v rustc >/dev/null 2>&1; then
  echo "Rust is not installed; please install Rust (https://rustup.rs)"
  exit 1
fi

rustup target add wasm32-wasi || true
cargo build --release --target wasm32-wasi
WASM_PATH="target/wasm32-wasi/release/hello_plugin.wasm"
if [ -f "$WASM_PATH" ]; then
  base64 "$WASM_PATH" > plugin.wasm.base64
  echo "Built plugin.wasm.base64"
else
  echo "Build succeeded but expected wasm not found at $WASM_PATH. Adjust crate name or path."
  exit 1
fi
