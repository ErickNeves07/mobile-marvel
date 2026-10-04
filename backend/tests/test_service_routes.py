from fastapi.testclient import TestClient

import app.main as main
from app.services.comic_vine import ComicVineError
from app.services.groq_narrative import GroqNarrativeError


client = TestClient(main.app)


def test_editorial_route_reports_missing_comic_vine_key_without_calling_upstream(monkeypatch):
    monkeypatch.setattr(main.comic_vine, "search_characters",
                        lambda *_args: (_ for _ in ()).throw(ComicVineError("not_configured", "not configured")))
    response = client.get("/v1/editorial/characters", params={"q": "Iron Man"})
    assert response.status_code == 503
    assert response.json()["detail"]["code"] == "not_configured"


def test_editorial_detail_404s_non_marvel_character(monkeypatch):
    monkeypatch.setattr(main.comic_vine, "get_character",
                        lambda _id: (_ for _ in ()).throw(ComicVineError("not_marvel", "outside Marvel")))
    response = client.get("/v1/editorial/characters/42")
    assert response.status_code == 404


def test_editorial_search_marvel_page_contract(monkeypatch):
    monkeypatch.setattr(main.comic_vine, "search_characters", lambda *_args: {
        "items": ({"id": 42, "name": "Iron Man", "deck": "Hero", "description": None,
                   "image_url": None, "site_url": "https://comicvine.gamespot.com/characters/42/",
                   "real_name": "Tony Stark", "publisher_id": 31, "publisher_name": "Marvel",
                   "powers": ("Armor",), "teams": ("Avengers",)},),
        "total": 1, "limit": 5, "offset": 0,
    })
    response = client.get("/v1/editorial/characters", params={"q": "Iron Man"})
    assert response.status_code == 200
    assert response.json()["items"][0]["publisher_id"] == 31
    assert response.json()["source_name"] == "Comic Vine"


def test_deadpool_route_uses_allowlisted_context_and_fallback(monkeypatch):
    captured = {}

    def fail(request):
        captured["messages"] = request.messages
        raise GroqNarrativeError("provider unavailable")

    monkeypatch.setattr(main.groq, "generate", fail)
    response = client.post("/v1/ai/deadpool-line", json={"context_id": "xmen", "prompt": "Say something"})
    assert response.status_code == 200
    assert response.json()["fallback"] is True
    assert response.json()["text"]
    assert "Magneto" in captured["messages"][0]["content"]
    assert "Say something" == captured["messages"][1]["content"]


def test_deadpool_route_rejects_unknown_context_and_extra_fields():
    unknown = client.post("/v1/ai/deadpool-line", json={"context_id": "invent-canon"})
    extra = client.post("/v1/ai/deadpool-line", json={"context_id": "nexus", "fact": "Thanos is free"})
    assert unknown.status_code == 422
    assert extra.status_code == 422


def test_deadpool_prompt_size_is_bounded():
    response = client.post("/v1/ai/deadpool-line", json={"context_id": "nexus", "prompt": "x" * 301})
    assert response.status_code == 422


def test_deadpool_route_sanitizes_provider_text(monkeypatch):
    monkeypatch.setattr(main.groq, "generate", lambda _request: "Hello\n\u0000  multiverse")
    response = client.post("/v1/ai/deadpool-line", json={"context_id": "nexus"})
    assert response.status_code == 200
    assert response.json() == {"text": "Hello multiverse", "fallback": False}
