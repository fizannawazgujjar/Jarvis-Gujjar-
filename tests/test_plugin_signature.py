from nacl.signing import SigningKey
from app.plugins.signature import verify_manifest_signature
import base64


def test_signature_roundtrip():
    sk = SigningKey.generate()
    pk = sk.verify_key
    manifest = b'{"id":"test","name":"Test"}'
    sig = sk.sign(manifest).signature
    sig_b64 = base64.b64encode(sig).decode('utf-8')
    pk_b64 = base64.b64encode(bytes(pk)).decode('utf-8')
    assert verify_manifest_signature(manifest, sig_b64, pk_b64)
