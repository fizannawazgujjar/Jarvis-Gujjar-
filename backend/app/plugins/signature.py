"""
Signature verification utilities for plugin manifests using Ed25519.

Uses PyNaCl (nacl) for signature verification.
"""
from nacl.signing import VerifyKey
from nacl.exceptions import BadSignatureError
import base64


def verify_manifest_signature(manifest_bytes: bytes, signature_b64: str, public_key_b64: str) -> bool:
    """Verify an Ed25519 signature for the manifest_bytes.
    Returns True if valid, raises on invalid.
    """
    signature = base64.b64decode(signature_b64)
    public_key = base64.b64decode(public_key_b64)
    vk = VerifyKey(public_key)
    try:
        vk.verify(manifest_bytes, signature)
        return True
    except BadSignatureError as e:
        raise e
