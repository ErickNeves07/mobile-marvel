"""Small backend-only Groq adapter. Callers own prompt and content validation."""

from __future__ import annotations

import json
import os
from dataclasses import dataclass
from typing import Callable
from urllib.error import HTTPError, URLError
from urllib.request import Request, urlopen

ENDPOINT = "https://api.groq.com/openai/v1/chat/completions"
DEFAULT_MODEL = "openai/gpt-oss-120b"
RETIRED_MODEL = "llama-3.3-70b-versatile"
MAX_OUTPUT_CHARS = 4_000
MAX_MESSAGES = 12
MAX_MESSAGE_CHARS = 4_000
TIMEOUT_SECONDS = 20


class GroqNarrativeError(RuntimeError):
    """Sanitized provider/configuration error with no request or response details."""


@dataclass(frozen=True)
class NarrativeRequest:
    messages: list[dict[str, str]]


def _configured_model() -> str:
    model = os.environ.get("GROQ_MODEL", "").strip()
    return DEFAULT_MODEL if not model or model == RETIRED_MODEL else model


class GroqNarrativeAdapter:
    def __init__(self, transport: Callable = urlopen) -> None:
        self._transport = transport

    def generate(self, request: NarrativeRequest) -> str:
        api_key = os.environ.get("GROQ_API_KEY", "").strip()
        if not api_key:
            raise GroqNarrativeError("Groq provider is not configured")
        try:
            messages = self._validate_messages(request.messages)
        except (TypeError, ValueError):
            raise GroqNarrativeError("Groq request is invalid") from None
        model = _configured_model()
        payload = {
            "model": model,
            "messages": messages,
            "temperature": 0.4,
            "max_tokens": 512,
            "stream": False,
        }
        if model.startswith("openai/gpt-oss-"):
            payload["reasoning_effort"] = "low"
        body = json.dumps(payload).encode("utf-8")
        http_request = Request(
            ENDPOINT,
            data=body,
            headers={
                "Authorization": f"Bearer {api_key}",
                "Content-Type": "application/json",
            },
            method="POST",
        )
        try:
            try:
                response_context = self._transport(http_request, timeout=TIMEOUT_SECONDS)
            except TypeError as exc:
                if "unexpected keyword argument 'timeout'" not in str(exc):
                    raise
                response_context = self._transport(http_request, TIMEOUT_SECONDS)
            with response_context as response:
                try:
                    raw_body = response.read(1_000_001)
                except TypeError as exc:
                    if "positional" not in str(exc) and "arguments" not in str(exc):
                        raise
                    raw_body = response.read()
                if len(raw_body) > 1_000_000:
                    raise GroqNarrativeError("Groq provider response exceeded the size limit")
                payload = json.loads(raw_body)
        except (HTTPError, URLError, TimeoutError, OSError, ValueError, TypeError) as exc:
            raise GroqNarrativeError("Groq provider request failed") from None
        try:
            content = payload["choices"][0]["message"]["content"]
        except (KeyError, IndexError, TypeError):
            raise GroqNarrativeError("Groq provider returned an invalid response") from None
        if not isinstance(content, str) or not content.strip() or len(content) > MAX_OUTPUT_CHARS:
            raise GroqNarrativeError("Groq provider returned invalid narrative content")
        return content.strip()
    @staticmethod
    def _validate_messages(messages: list[dict[str, str]]) -> list[dict[str, str]]:
        if not isinstance(messages, list) or not 1 <= len(messages) <= MAX_MESSAGES:
            raise ValueError("messages must contain between 1 and 12 entries")
        result = []
        for message in messages:
            if not isinstance(message, dict) or set(message) != {"role", "content"}:
                raise ValueError("each message must contain role and content")
            role, content = message["role"], message["content"]
            if role not in {"system", "user", "assistant"}:
                raise ValueError("unsupported message role")
            if not isinstance(content, str) or not content.strip() or len(content) > MAX_MESSAGE_CHARS:
                raise ValueError("message content is empty or exceeds the limit")
            result.append({"role": role, "content": content})
        return result
