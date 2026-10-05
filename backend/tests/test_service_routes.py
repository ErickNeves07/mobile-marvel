from fastapi.testclient import TestClient

import app.main as main
from app.services.comic_vine import ComicVineError
from app.services.groq_narrative import GroqNarrativeError
from app.services.gemini_narrative import GeminiNarrativeError
from app.services.groq_narrative import GroqNarrativeAdapter, MAX_MESSAGE_CHARS


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


def test_deadpool_uses_bounded_client_game_state_as_allowlisted_context(monkeypatch):
    captured = {}
    def capture(request):
        captured["system"] = request.messages[0]["content"]
        return "Linha contextualizada"
    monkeypatch.setattr(main.groq, "generate", capture)
    response = client.post("/v1/ai/deadpool-line", json={
        "context_id": "app", "game_context": "Wolverine desbloqueado; campanha Ultron em andamento"
    })
    assert response.status_code == 200
    assert "Wolverine desbloqueado" in captured["system"]
    assert "Varie as piadas" in captured["system"]


def test_deadpool_can_comment_on_thanos_and_current_team_without_inventing_game_rules(monkeypatch):
    captured = {}
    def capture(request):
        captured["system"] = request.messages[0]["content"]
        captured["user"] = request.messages[1]["content"]
        return "Titã parece um passeio tranquilo... se você ignorar Thanos."
    monkeypatch.setattr(main.groq, "generate", capture)
    response = client.post("/v1/ai/deadpool-line", json={
        "context_id": "app", "prompt": "O que acha do Thanos e do meu time?",
        "game_context": "Equipe salva: Homem-Aranha (Origem), Wolverine (Origem), Tocha Humana (Origem). "
                        "Missão selecionada: Titã em Colapso contra Thanos"
    })
    assert response.status_code == 200
    assert response.json()["fallback"] is False
    assert "Thanos em Titã" in captured["system"]
    assert "Homem-Aranha" in captured["system"]
    assert "não sugira/decida regras" in captured["system"]
    assert captured["user"] == "O que acha do Thanos e do meu time?"


def test_deadpool_route_rejects_unknown_context_and_extra_fields():
    unknown = client.post("/v1/ai/deadpool-line", json={"context_id": "invent-canon"})
    extra = client.post("/v1/ai/deadpool-line", json={"context_id": "nexus", "fact": "Thanos is free"})
    assert unknown.status_code == 422
    assert extra.status_code == 422


def test_deadpool_prompt_size_is_bounded(monkeypatch):
    monkeypatch.setattr(main.groq, "generate", lambda _request: "bounded")
    monkeypatch.setattr(main.gemini, "generate", lambda _request: "bounded")
    response = client.post("/v1/ai/deadpool-line", json={"context_id": "nexus", "prompt": "x" * 301})
    assert response.status_code == 422
    accepted_context = client.post("/v1/ai/deadpool-line", json={
        "context_id": "app", "game_context": "x" * 6_000
    })
    assert accepted_context.status_code == 200
    context = client.post("/v1/ai/deadpool-line", json={"context_id": "app", "game_context": "x" * 6_001})
    assert context.status_code == 422


def test_deadpool_route_sanitizes_provider_text(monkeypatch):
    monkeypatch.setattr(main.groq, "generate", lambda _request: "Hello\n\u0000  multiverse")
    response = client.post("/v1/ai/deadpool-line", json={"context_id": "nexus"})
    assert response.status_code == 200
    assert response.json() == {"text": "Hello multiverse", "fallback": False}


def test_deadpool_prefers_gemini_when_configured(monkeypatch):
    monkeypatch.setenv("GEMINI_API_KEY", "test-key")
    monkeypatch.setattr(main.gemini, "generate", lambda _request: "Gemini respondeu")
    monkeypatch.setattr(main.groq, "generate", lambda _request: (_ for _ in ()).throw(AssertionError("Groq should not run")))
    response = client.post("/v1/ai/deadpool-line", json={"context_id": "nexus"})
    assert response.json() == {"text": "Gemini respondeu", "fallback": False}


def test_deadpool_tries_groq_after_gemini_error(monkeypatch):
    monkeypatch.setenv("GEMINI_API_KEY", "test-key")
    monkeypatch.setattr(main.gemini, "generate", lambda _request: (_ for _ in ()).throw(GeminiNarrativeError("unavailable")))
    monkeypatch.setattr(main.groq, "generate", lambda _request: "Groq respondeu")
    response = client.post("/v1/ai/deadpool-line", json={"context_id": "forge"})
    assert response.json() == {"text": "Groq respondeu", "fallback": False}


def test_deadpool_provider_failure_logs_only_safe_codes(monkeypatch, caplog):
    monkeypatch.setenv("GEMINI_API_KEY", "never-log-this-key")
    monkeypatch.setattr(main.gemini, "generate", lambda _request: (_ for _ in ()).throw(
        GeminiNarrativeError("private prompt never-log-this-prompt", "http_429")))
    monkeypatch.setattr(main.groq, "generate", lambda _request: (_ for _ in ()).throw(
        GroqNarrativeError("private response never-log-this-response", "http_401")))
    response = client.post("/v1/ai/deadpool-line", json={"context_id": "app",
                           "prompt": "never-log-this-prompt"})
    assert response.status_code == 200
    assert response.json()["fallback"] is True
    logs = caplog.text
    assert "provider=gemini code=http_429" in logs
    assert "provider=groq code=http_401" in logs
    for private_value in ("never-log-this-key", "never-log-this-prompt", "never-log-this-response"):
        assert private_value not in logs


def test_deadpool_accepts_full_client_context_through_provider_validation(monkeypatch):
    monkeypatch.setenv("GEMINI_API_KEY", "test-key")
    captured = {}

    def validate_and_generate(request):
        messages = GroqNarrativeAdapter._validate_messages(request.messages)
        captured["system_chars"] = len(messages[0]["content"])
        return "Resposta gerada com o contexto completo."

    monkeypatch.setattr(main.gemini, "generate", validate_and_generate)
    monkeypatch.setattr(main.groq, "generate", lambda _request: (_ for _ in ()).throw(
        AssertionError("Groq should not run after a valid Gemini response")))
    response = client.post("/v1/ai/deadpool-line", json={"context_id": "app",
                           "prompt": "Quem está no meu time?", "game_context": "x" * 6_000})
    assert response.status_code == 200
    assert response.json() == {"text": "Resposta gerada com o contexto completo.", "fallback": False}
    assert captured["system_chars"] > 4_000
    assert captured["system_chars"] < MAX_MESSAGE_CHARS
