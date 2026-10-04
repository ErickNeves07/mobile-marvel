import json
from collections import Counter
from pathlib import Path

from fastapi.testclient import TestClient
from pydantic import ValidationError

from app.main import app
from app.schemas.characters import GameCharacterCatalogResponse


client = TestClient(app)
CATALOG_PATH = Path(__file__).resolve().parents[2] / "shared" / "game_catalog.json"
EXPECTED_TIERS = ["origin", "ascension", "legendary", "multiversal", "infinity"]
EXPECTED_GROUP_COUNTS = {
    "avengers-allies": 8,
    "x-men": 4,
    "fantastic-four": 4,
    "cosmic-specials": 5,
}


def test_game_catalog_matches_the_shared_offline_source() -> None:
    source = json.loads(CATALOG_PATH.read_text(encoding="utf-8"))
    response = client.get("/v1/game/characters")

    assert response.status_code == 200
    assert response.json() == source


def test_game_catalog_has_21_unique_characters_and_four_groups() -> None:
    items = client.get("/v1/game/characters").json()["items"]

    assert len(items) == 21
    assert len({item["id"] for item in items}) == 21
    assert Counter(item["group_id"] for item in items) == EXPECTED_GROUP_COUNTS
    assert all(set(item) == {"id", "name", "group_id", "variants"} for item in items)


def test_every_character_has_five_ordered_variants_with_unique_game_ids() -> None:
    items = client.get("/v1/game/characters").json()["items"]
    variant_ids = []

    for character in items:
        variants = character["variants"]
        assert len(variants) == 5
        assert [variant["tier_id"] for variant in variants] == EXPECTED_TIERS
        assert all(set(variant) == {"id", "tier_id", "name"} for variant in variants)
        variant_ids.extend(variant["id"] for variant in variants)

    assert len(variant_ids) == 105
    assert len(set(variant_ids)) == 105


def test_game_catalog_stays_editorial_free_and_keeps_bosses_out_of_roster() -> None:
    items = client.get("/v1/game/characters").json()["items"]
    serialized = json.dumps(items, ensure_ascii=False).lower()
    xavier = next(item for item in items if item["id"] == "professor-xavier")

    assert "magneto" not in {item["id"] for item in items}
    assert xavier["group_id"] == "x-men"
    assert "comicvine" not in serialized
    assert "publisher" not in serialized
    assert "image" not in serialized


def test_catalog_schema_rejects_blank_required_strings() -> None:
    source = json.loads(CATALOG_PATH.read_text(encoding="utf-8"))
    source["items"][0]["name"] = "  "

    try:
        GameCharacterCatalogResponse.model_validate(source)
    except ValidationError:
        pass
    else:
        raise AssertionError("blank required character name should be rejected")


def test_catalog_schema_rejects_duplicate_character_ids() -> None:
    source = json.loads(CATALOG_PATH.read_text(encoding="utf-8"))
    source["items"][1]["id"] = source["items"][0]["id"]

    try:
        GameCharacterCatalogResponse.model_validate(source)
    except ValidationError:
        pass
    else:
        raise AssertionError("duplicate character ID should be rejected")


def test_catalog_schema_rejects_duplicate_variant_ids_across_characters() -> None:
    source = json.loads(CATALOG_PATH.read_text(encoding="utf-8"))
    source["items"][1]["variants"][0]["id"] = source["items"][0]["variants"][0]["id"]

    try:
        GameCharacterCatalogResponse.model_validate(source)
    except ValidationError:
        pass
    else:
        raise AssertionError("duplicate variant ID should be rejected")
