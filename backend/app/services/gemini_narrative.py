"""Backend-only Gemini adapter for short, bounded Deadpool narration."""

from __future__ import annotations

import json
import os
from typing import Callable
from urllib.error import HTTPError, URLError
from urllib.request import Request, urlopen

from app.services.groq_narrative import GroqNarrativeAdapter, NarrativeRequest

ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash-lite:generateContent"
TIMEOUT_SECONDS = 20
MAX_RESPONSE_BYTES = 1_000_000
MAX_OUTPUT_CHARS = 4_000


class GeminiNarrativeError(RuntimeError):
    """Sanitized configuration, transport or response failure."""


class GeminiNarrativeAdapter:
    def __init__(self, transport: Callable = urlopen) -> None:
        self._transport = transport

    def generate(self, request: NarrativeRequest) -> str:
        key = os.environ.get("GEMINI_API_KEY", "").strip()
        if not key:
            raise GeminiNarrativeError("Gemini provider is not configured")
        try:
            messages = GroqNarrativeAdapter._validate_messages(request.messages)
        except (TypeError, ValueError):
            raise GeminiNarrativeError("Gemini request is invalid") from None
        system = "\n".join(message["content"] for message in messages if message["role"] == "system")
        contents = [
            {"role": "model" if message["role"] == "assistant" else "user",
             "parts": [{"text": message["content"]}]}
            for message in messages if message["role"] != "system"
        ]
        if not contents:
            raise GeminiNarrativeError("Gemini request is invalid")
        payload = {
            "contents": contents,
            "generationConfig": {"temperature": 0.4, "maxOutputTokens": 512},
        }
        if system:
            payload["systemInstruction"] = {"parts": [{"text": system}]}
        http_request = Request(
            ENDPOINT,
            data=json.dumps(payload).encode("utf-8"),
            headers={"x-goog-api-key": key, "Content-Type": "application/json"},
            method="POST",
        )
        try:
            response_context = self._transport(http_request, timeout=TIMEOUT_SECONDS)
            with response_context as response:
                raw = response.read(MAX_RESPONSE_BYTES + 1)
            if len(raw) > MAX_RESPONSE_BYTES:
                raise GeminiNarrativeError("Gemini response exceeded the size limit")
            result = json.loads(raw)
        except GeminiNarrativeError:
            raise
        except (HTTPError, URLError, TimeoutError, OSError, ValueError, TypeError):
            raise GeminiNarrativeError("Gemini provider request failed") from None
        try:
            parts = result["candidates"][0]["content"]["parts"]
            text = " ".join(part["text"] for part in parts if isinstance(part, dict) and isinstance(part.get("text"), str))
        except (KeyError, IndexError, TypeError):
            raise GeminiNarrativeError("Gemini provider returned an invalid response") from None
        if not text.strip() or len(text) > MAX_OUTPUT_CHARS:
            raise GeminiNarrativeError("Gemini provider returned invalid narrative content")
        return text.strip()
