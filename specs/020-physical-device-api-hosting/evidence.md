# 020 — API hosting — Evidence

Official references checked 2026-09-28:

- [Render FastAPI deployment](https://render.com/docs/deploy-fastapi) — Web Service build/start commands and public `onrender.com` endpoint.
- [Render free services](https://render.com/docs/free) — idle 15-minute spin-down, about one-minute wake, ephemeral filesystem, free-instance limits.
- [Render Python versions](https://render.com/docs/python-version) — exact `PYTHON_VERSION` configuration.
- [Render environment variables](https://render.com/docs/configure-environment-variables) — secrets are set out of band; Blueprint supports `sync: false` for secret placeholders.
- [Vercel FastAPI](https://vercel.com/docs/frameworks/backend/fastapi) — FastAPI is packaged as a Function; cited for comparison.

Local checks: `render`, `vercel`, `fly`, `gcloud`, `git`, and `gh` commands are absent from PATH; no Render/Vercel/Fly token is present in process environment; no platform session/browser is available. The repository has no configured remote visible to this execution. No credentials were printed. Public deployment and device-to-host call are therefore not validated.

Android assessment: the app can run Forge/gameplay offline, while its HTTPS client can call Comic Vine and Deadpool routes when `-PriApiBaseUrl` is configured. The APK has INTERNET permission and cleartext remains disabled. No public URL is currently configured, so the release continues to use local fallback and has not been tested against a hosted service.

Local release candidate: `artifacts/Marvel-Ruptura-Infinita-debug.apk` (debuggable build; includes a debug-only shard grant action for testing). Build and local backend tests are evidenced by `scripts/validate-local.ps1 -Target all`; AVD manual and instrumentation evidence is in spec 005. No physical handset was connected.

Follow-up 2026-09-28: `/health` remains liveness for the Render health check; `/ready` reports Comic Vine/Groq configuration booleans without exposing values. Blueprint declares both secret keys with `sync: false`. Full backend route suite passes **39 tests** under the project Python 3.13 venv, including readiness redaction. Groq live smoke attempted with the local `.env` key and returned a sanitized adapter error; connectivity/provider acceptance remains unknown. No Render token, Comic Vine key, or host URL is present, so no deployment was attempted.

Follow-up 2026-10-04: Erick created the Web Service manually from `ErickNeves07/mobile-marvel` using the settings documented in `docs/11-RENDER-DEPLOY.md`. `https://mobile-marvel-8qex.onrender.com/health` returned `ok`; `/ready` returned `ok` with Comic Vine and Groq presence flags true. Hosted catalogs returned 21 characters/two campaigns, and 21/21 game-character editorial routes returned correct IDs, Comic Vine source and image host. A signed Android APK was rebuilt with this URL; AVD loaded and captured real Comic Vine portraits in Collection and Comparison; online instrumentation passed 22/22. APK size/hash and screenshots are in spec 030. Groq provider response and physical phone remain unverified; older statements above describe the historical state before deployment.
