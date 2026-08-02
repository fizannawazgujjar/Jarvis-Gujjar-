fn main() {
    // Simple WASI-compatible plugin that reads stdin and echoes it back
    // Build with: rustup target add wasm32-wasi
    // cargo new --bin hello_wasm
    // (place this src/main.rs into the crate and build with `cargo build --release --target wasm32-wasi`)
    // The resulting .wasm can be used as a plugin that echoes stdin to stdout.

    // NOTE: This is a placeholder source file for developers to build a real wasm binary.
    println!("Hello from Hello Plugin (WASM source placeholder)");
}
