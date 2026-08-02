import base64
from tools.plugin_cli import sign


def test_sign_and_verify():
    sk, pk = sign.generate_keypair()
    pk_b64 = base64.b64encode(bytes(pk)).decode('utf-8')
    m = {"id": "example", "name": "test"}
    sig = sign.sign_manifest(m, sk)
    assert sign.verify_manifest(m, sig, pk_b64)
