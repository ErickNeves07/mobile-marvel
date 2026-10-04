# 019 — Groq narrative adapter — Evidence

Status: **adapter and local mocked tests complete; no route or live call.**

Official references checked 2026-09-28:

- [Groq API reference](https://console.groq.com/docs/api-reference) — Chat Completions endpoint and request/response shape.
- [Groq supported models](https://console.groq.com/docs/models) — production model identifiers.

No authenticated or external API call has been made.

Local verification 2026-09-28: `scripts/validate-local.ps1 -Target all` passed (`pip check`, 18 pytest; Android unit tests, Android test APK assembly, lint and debug assemble). Groq tests mock transport, including missing key and sanitized failures. `GROQ_API_KEY` was not printed or used for a network request.
