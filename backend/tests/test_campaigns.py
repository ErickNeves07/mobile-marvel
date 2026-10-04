from fastapi.testclient import TestClient

from app.main import app


client = TestClient(app)


def test_campaign_catalog_exposes_only_approved_summary_fields() -> None:
    response = client.get("/v1/campaigns")

    assert response.status_code == 200
    assert response.json() == {
        "items": [
            {
                "id": "x-men",
                "title": "X-Men",
                "faction_id": "x-men",
                "boss_id": "magneto",
            },
            {
                "id": "fantastic-four",
                "title": "Quarteto Fantástico",
                "faction_id": "fantastic-four",
            },
        ]
    }


def test_campaign_catalog_is_deterministic_and_does_not_mutate() -> None:
    first = client.get("/v1/campaigns")
    second = client.get("/v1/campaigns")

    assert first.content == second.content


def test_campaign_schema_rejects_unsupported_fields() -> None:
    response = client.get("/v1/campaigns")
    items = response.json()["items"]

    assert not {"objective", "reward", "difficulty", "roster"}.intersection(items[0])
    assert "boss_id" not in items[1]
