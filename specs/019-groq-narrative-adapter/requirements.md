# 019 — Groq narrative adapter

Status: **backend adapter implemented and mock-verified; route and real provider call intentionally remain out of scope.**

## Objective

Provide a backend-only adapter for optional narrative text generation through Groq, without allowing generated text to determine canonical facts, rewards, gameplay balance, or progression.

## Scope

- Read `GROQ_API_KEY` from process environment at request time; never return/log it.
- Use Groq chat completions via HTTPS and an exact production model ID.
- Bound input, output tokens and timeout; return a validated plain-text result or a sanitized provider error.
- Unit-test request/response handling with mocked transport and no external calls.

## Out of scope

- Android direct access, automatic retries, tools/web search, persistence, prompt content/persona policy, public API route, real API call, credentials setup.
- Groq deciding facts about Marvel canon or deterministic game state.

## Acceptance

- [x] Missing key produces a configuration error without network access.
- [x] Request uses the documented chat-completions contract and chosen stable model.
- [x] Response structure/content limits are validated; malformed/provider failure is sanitized.
- [x] Tests mock transport, assert key is sent only as authorization header, and do not expose it.
- [x] No external calls in local verification.
