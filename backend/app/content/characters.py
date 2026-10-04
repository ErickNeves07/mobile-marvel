"""Shared static game catalog used by the API and Android asset bundle."""

from pathlib import Path

from app.schemas.characters import GameCharacterCatalogResponse


_CATALOG_PATH = Path(__file__).resolve().parents[3] / "shared" / "game_catalog.json"
GAME_CHARACTERS = GameCharacterCatalogResponse.model_validate_json(
    _CATALOG_PATH.read_text(encoding="utf-8")
)
