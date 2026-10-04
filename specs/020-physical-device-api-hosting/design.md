# 020 — API hosting — Design

Render is selected over Vercel for the first prototype because the existing backend is a conventional long-running FastAPI/Uvicorn app and Render documents a direct Web Service deployment path. The Blueprint keeps repository root as the service root so `backend/app/content/characters.py` can load `shared/game_catalog.json` without duplicating data.

The free plan avoids introducing a recurring cost. It is expected to sleep after 15 minutes idle and wake on request with roughly one minute of delay. Do not store player progress on this service: Android Forge progress remains local SQLite. Comic Vine and Groq key names are declared as `sync: false`, so their values are entered only through the host dashboard. `/ready` reports configured/unconfigured booleans and never returns key values.

The phone uses an HTTPS public URL only after Android receives a configurable base URL in a separate network integration spec. Same-Wi‑Fi local testing may instead use the computer's LAN address, but that address is not checked into production configuration.

Deployment requires Render account access and a connected Git repository or an authenticated deployment flow. Neither CLI/account session nor configured repository remote is present in the current execution environment. No deployment or push was attempted. Android's HTTPS client and Groq/Comic Vine backend routes are implemented in their respective specs, but production URL/live checks remain pending.
