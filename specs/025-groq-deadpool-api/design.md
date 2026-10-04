# 025 — Groq/Deadpool — Design

Reuse the stdlib Groq adapter. `/v1/ai/deadpool-line` accepts one allowlisted context and a short user prompt; server constructs a system instruction from fixed lore facts, treats user text as untrusted and bounds/sanitizes the output. Missing key, transport error or invalid output returns an authored Portuguese line with `fallback=true`. No generated output enters the game state. Android calls only the FastAPI endpoint and displays the returned fallback marker.
