from fastapi.testclient import TestClient

from app.main import app


client = TestClient(app)


def test_campaign_catalog_exposes_nine_chapters_in_story_order() -> None:
    response = client.get("/v1/campaigns")

    assert response.status_code == 200
    items = response.json()["items"]
    assert len(items) == 9
    assert [item["chapter"] for item in items] == list(range(1, 10))
    assert [item["boss_id"] for item in items] == [
        "rei-do-crime", "ultron", "wakanda-tech", "dormammu", "ronan",
        "magneto", "annihilus", "doutor-destino", "thanos",
    ]
    assert all(item["faction_id"] == "all" for item in items)


def test_campaign_catalog_is_deterministic_and_does_not_mutate() -> None:
    first = client.get("/v1/campaigns")
    second = client.get("/v1/campaigns")

    assert first.content == second.content


def test_campaign_schema_rejects_unsupported_fields() -> None:
    response = client.get("/v1/campaigns")
    items = response.json()["items"]

    assert not {"objective", "reward", "difficulty", "roster"}.intersection(items[0])
    assert items[0]["title"] == "Nova York em Ruptura"
    assert items[-1]["title"] == "Titã em Colapso"
