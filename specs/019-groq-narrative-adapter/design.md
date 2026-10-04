# 019 — Groq narrative adapter — Design

## Provider contract

Use `POST https://api.groq.com/openai/v1/chat/completions`, bearer authentication from `GROQ_API_KEY`, and model `llama-3.3-70b-versatile` (Groq production model list, checked 2026-09-28). Set temperature conservatively, `max_tokens=256`, and 20 second socket timeout. No retries in this increment.

Use Python stdlib `urllib` to avoid a runtime dependency change. JSON body contains supplied messages only plus bounded generation parameters. Transport errors, HTTP status, invalid JSON, absent choices/content, and oversized output map to sanitized adapter errors; response bodies and credentials are never included in exceptions.

## Trust boundaries

This adapter only formats narrative from caller-provided context. Higher-level feature specs must define persona/safety/prompt content and validate outputs before a public route. Generated output cannot set game state or facts. No endpoint is wired here.

## Verification

Inject a transport callable for tests; mock HTTP response and errors. Assert env key is read at invocation and only appears in authorization header. Tests must not call Groq.
