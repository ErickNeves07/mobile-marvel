# 024 — Integração Android/backend — Requirements

Status: **implementation active; runtime tests and production host pending.**

## Scope

- Java client calls FastAPI off the main thread through a configurable base URL.
- HTTPS only; cleartext remains disabled.
- Fetch Comic Vine editorial characters and optional narrative; keep game catalog available offline with source/error state.
- Bounded timeouts/response sizes and loading/content/empty/error states.
- No Comic Vine/Groq credentials in APK.

## Acceptance

- [x] Configurable HTTPS base URL without a production address hard-coded.
- [ ] Local FastAPI and AVD integration tests pass.
- [x] Offline/error preserves cache/local game catalog.
- [ ] Physical handset verifies the configured LAN/HTTPS host.
- [ ] Release permission/security configuration is audited.
