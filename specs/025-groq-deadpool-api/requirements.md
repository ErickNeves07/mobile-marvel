# 025 — Groq/Deadpool — Requirements

Status: implementation active; mocked adapter tests pass, route/provider runtime verification remains.

- Provide bounded `POST /v1/ai/deadpool-line` with an allowlisted context ID and prompt <=300 chars.
- Backend selects approved facts/Portuguese fallback; no canon/reward/difficulty/game state is delegated to AI.
- Read `GROQ_API_KEY` only in backend process; sanitize output and provider failures.
- Android Deadpool view shows loading, response, fallback and error states.

## Acceptance

- [x] Route and Android interaction are implemented in source.
- [x] Seven Groq adapter mocked tests pass.
- [ ] Route suite runs with compatible FastAPI/Pydantic native runtime.
- [ ] Live provider call only after backend environment/network configuration; none performed here.
