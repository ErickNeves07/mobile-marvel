import json

import pytest

from app.services.groq_narrative import (
    DEFAULT_MODEL,
    ENDPOINT,
    GroqNarrativeAdapter,
    GroqNarrativeError,
    NarrativeRequest,
)


class FakeResponse:
    def __init__(self, payload):
        self.payload = json.dumps(payload).encode()

    def __enter__(self):
        return self

    def __exit__(self, *_args):
        return False

    def read(self):
        return self.payload


def completion(content="Ready, bub."):
    return {"choices": [{"message": {"content": content}}]}


def test_missing_api_key_fails_before_transport(monkeypatch):
    monkeypatch.delenv("GROQ_API_KEY", raising=False)
    called = False

    def transport(*_args, **_kwargs):
        nonlocal called
        called = True

    with pytest.raises(GroqNarrativeError, match="not configured"):
        GroqNarrativeAdapter(transport).generate(NarrativeRequest([{"role": "user", "content": "Hi"}]))
    assert not called


def test_request_contract_and_validated_response(monkeypatch):
    secret = "test-key-never-print"
    monkeypatch.setenv("GROQ_API_KEY", secret)
    monkeypatch.delenv("GROQ_MODEL", raising=False)
    captured = {}

    def transport(request, timeout):
        captured["url"] = request.full_url
        captured["authorization"] = request.get_header("Authorization")
        captured["body"] = json.loads(request.data)
        captured["timeout"] = timeout
        return FakeResponse(completion())

    result = GroqNarrativeAdapter(transport).generate(
        NarrativeRequest([{"role": "system", "content": "Use only supplied context."},
                          {"role": "user", "content": "Write a hint."}])
    )
    assert result == "Ready, bub."
    assert captured["url"] == ENDPOINT
    assert captured["authorization"] == f"Bearer {secret}"
    assert secret not in json.dumps(captured["body"])
    assert captured["body"]["model"] == DEFAULT_MODEL
    assert DEFAULT_MODEL == "openai/gpt-oss-120b"
    assert captured["body"]["max_tokens"] == 512
    assert captured["body"]["reasoning_effort"] == "low"
    assert captured["body"]["stream"] is False
    assert captured["timeout"] == 20


def test_retired_model_override_uses_current_default(monkeypatch):
    monkeypatch.setenv("GROQ_API_KEY", "test-key")
    monkeypatch.setenv("GROQ_MODEL", "llama-3.3-70b-versatile")
    captured = {}

    def transport(request, timeout):
        captured.update(json.loads(request.data))
        return FakeResponse(completion())

    GroqNarrativeAdapter(transport).generate(
        NarrativeRequest([{"role": "user", "content": "Hi"}])
    )
    assert captured["model"] == DEFAULT_MODEL


@pytest.mark.parametrize("payload", [{}, {"choices": []}, {"choices": [{"message": {}}]}])
def test_malformed_provider_response_is_sanitized(monkeypatch, payload):
    monkeypatch.setenv("GROQ_API_KEY", "test-key")
    with pytest.raises(GroqNarrativeError, match="invalid response"):
        GroqNarrativeAdapter(lambda *_args, **_kwargs: FakeResponse(payload)).generate(
            NarrativeRequest([{"role": "user", "content": "Hi"}])
        )


def test_oversized_content_is_rejected(monkeypatch):
    monkeypatch.setenv("GROQ_API_KEY", "test-key")
    with pytest.raises(GroqNarrativeError, match="invalid narrative"):
        GroqNarrativeAdapter(lambda *_args, **_kwargs: FakeResponse(completion("x" * 4001))).generate(
            NarrativeRequest([{"role": "user", "content": "Hi"}])
        )


def test_oversized_provider_body_is_rejected(monkeypatch):
    monkeypatch.setenv("GROQ_API_KEY", "test-key")

    class LargeResponse(FakeResponse):
        def read(self, size=-1):
            return b"{" + (b" " * 1_000_001)

    with pytest.raises(GroqNarrativeError, match="size limit"):
        GroqNarrativeAdapter(lambda *_args, **_kwargs: LargeResponse({})).generate(
            NarrativeRequest([{"role": "user", "content": "Hi"}])
        )


def test_http_failure_does_not_leak_provider_details(monkeypatch):
    monkeypatch.setenv("GROQ_API_KEY", "test-secret")

    def failing_transport(*_args, **_kwargs):
        raise OSError("test-secret provider response payload")

    with pytest.raises(GroqNarrativeError, match="request failed") as raised:
        GroqNarrativeAdapter(failing_transport).generate(
            NarrativeRequest([{"role": "user", "content": "Hi"}])
        )
    assert "test-secret" not in str(raised.value)
