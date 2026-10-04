# 020 — API hosting for physical-device access

Status: **blueprint, configurable Android client, provider routes, readiness diagnostics, and secret placeholders implemented; deploy/live validations remain blocked on external access and keys.**

## Objective

Define a public HTTPS host path for FastAPI so a phone can access APIs outside the development computer's Wi‑Fi, while preserving a LAN option for local development.

## Scope

- Render Blueprint for FastAPI health/readiness, static catalogs, Comic Vine, and Groq routes.
- Build from repository root so `shared/game_catalog.json` remains available.
- Exact Python 3.13.15 runtime, hash-locked dependencies, public health check.
- Free service plan for an initial prototype; provider credentials remain dashboard-only secrets.
- `/health` process liveness and `/ready` secret-presence booleans (never values).
- Record host/auth readiness and cold-start limitations.

## Out of scope

- Creating a new paid service or storing a provider credential in the repository, APK, logs, or build configuration.
- Publishing the service or validating a live device URL; these require platform access and keys.

## Acceptance

- [x] Blueprint builds from repository root and points at the actual FastAPI module.
- [x] Health check and runtime version are explicit.
- [x] No API keys or `.env` contents are included in configuration.
- [x] Provider secret names use `sync: false`; `/ready` reveals booleans only.
- [ ] Deploy a public URL and verify health/readiness/catalog/provider endpoints (requires connected platform account/repository and provider keys).
- [ ] Connect Android build configuration to a chosen host and verify on physical device.

## Runtime tradeoff

Render's free web service sleeps after 15 minutes of inactivity and takes about one minute to wake. This is suitable for a no-cost test endpoint, with cold-start delays. The API currently provides only health and static catalogs; Android stil