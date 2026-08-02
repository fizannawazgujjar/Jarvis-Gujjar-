"""
Plugin manifest validation utilities.
"""
import json
import os
from jsonschema import validate, ValidationError

SCHEMA_PATH = os.path.join(os.path.dirname(__file__), "../../pluginsdk/manifest_schema.json")


def load_schema():
    with open(SCHEMA_PATH, "r", encoding="utf-8") as f:
        return json.load(f)


def validate_manifest(manifest: dict) -> None:
    schema = load_schema()
    try:
        validate(instance=manifest, schema=schema)
    except ValidationError as e:
        raise e
