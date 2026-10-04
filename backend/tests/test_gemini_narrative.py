import json

import pytest

from app.services.gemini_narrative import (
    ENDPOINT,
    GeminiNarrativeAdapter,
    GeminiNarrativeError,
)
from app.services.groq_narrative import NarrativeRequest


class FakeResponse:
    def __init__(self, payload):
        self.payload = json.dumps(payload).encode()

    def __enter__(self):
        return self

    def __exit__(self, *_args):
        return False

    def read(self, _limit):
        return self.payload


def test_gemini_contract_and_response(monkeypatch):
    monkeypatch.setenv("GEMINI_API_KEY", "secret-never-print")
    captured = {}

    def transport(request, timeout):
        captured["url"] = request.full_url
        captured["header"] = request.get_header("X-goog-api-key")
        captured["body"] = json.loads(request.data)
        captured["timeout"] = timeout
        return FakeResponse({"candidates": [{"content": {"parts": [{"text": "Uma frase."}]}}]})

    result = GeminiNarrativeAdapter(transport).generate(NarrativeRequest([
        {"role": "system", "content": "Use somente o fato fornecido."},
        {"role": "user", "content": "Diga uma frase."},
    ]))
    assert result == "Uma frase."
    assert captured["url"] == ENDPOINT
    assert captured["header"] == "secret-never-print"
    assert captured["body"]["systemInstruction"]["parts"][0]["text"] == "Use somente o fato fornecido."
    assert captured["body"]["contents"][0]["role"] == "user"
    assert "secret-never-print" not in json.dumps(captured["body"])
    assert captured["timeout"] == 20


def test_gemini_missing_key_and_malformed_response(monkeypatch):
    monkeypatch.delenv("GEMINI_API_KEY", raising=False)
    with pytest.raises(GeminiNarrativeError, match="not configured"):
        GeminiNarrativeAdapter(lambda *_args, **_kwargs: None).generate(
            NarrativeRequest([{"role": "user", "content": "Olá"}]))
    monkeypatch.setenv("GEMINI_API_KEY", "test-key")
    with pytest.raises(GeminiNarrativeError, match="invalid response"):
        GeminiNarrativeAdapter(lambda *_args, **_kwargs: FakeResponse({})).generate(
            NarrativeRequest([{"role": "user", "content": "Olá"}]))


def test_gemini_transport_error_hides_secret(monkeypatch):
    monkeypatch.setenv("GEMINI_API_KEY", "secret-never-print")

    def fail(*_args, **_kwargs):
        raise OSError("secret-never-print upstream error")

    with pytest.raises(GeminiNarrativeError) as raised:
        GeminiNarrativeAdapter(fail).generate(NarrativeRequest([{"role": "user", "content": "Olá"}]))
    assert "secret-never-print" not in str(raised.value)
