# 046 - Design

## Nexus and Chamber

- Reuse `EditorialPortraitLoader` for existing Comic Vine IDs `senhor-fantastico` and `doutor-estranho` inside the Nexus hero panel. Keep the current panel label and show compact role captions.
- Keep the current briefing location in `showCharacterVariants`, when the Gauntlet is active. Replace the old preference key with a stable v2 key so the 0.12.0 preference cannot suppress the first display. Mark it seen before opening to make Skip/Back/re-entry idempotent.
- Remove the replay button and callback.

## Battle scene and reward receipt

- In the claim worker, check `hasCompletedMission`/the idempotent `completeMission` result. First claim plays the scene and then opens a newly-awarded reward page. A duplicate claim skips the scene and opens the previous receipt page directly.
- Extend reward page rendering with an `awardedNow` flag. Duplicate receipts show the prior fragment record and state that credits/XP were not granted again; do not show positive reward rows.
- Set `battleClaiming=false` when transitioning to either reward page.

## Battle active fighter and Super

- Give the active team card a cyan/gold treatment, elevation, compact "ATIVO" and "SUA VEZ" labels, and an accessibility description. Keep the other living cards tappable for a free switch.
- Style the charged Super as a distinct dark, gold-edged Infinity control. Keep the active hero's move in the title and explicitly label its charge as shared. Preserve the current click guard, global charge consumption, and combat animation.

## Theme-aware action buttons

- Central `action()` keeps the current cyan/quartet-filled style in light mode. In dark mode it uses elevated/primary surfaces, gold label and border, and tighter letter spacing; no gameplay action or eligibility changes.
- Fragment shop uses the same dark/gold treatment in dark mode; light mode delegates to the existing primary button style. Disabled offers keep their enabled-state guard and reduced opacity.

## Deadpool diagnostics

- Preserve the current client contract/retry behavior and local fallback.
- Provider adapters attach sanitized failure codes (e.g. `http_401`, `http_429`, `transport_error`, `invalid_response`) to typed exceptions. Never include provider response bodies or headers.
- The route logs provider name and code when Gemini fails and when Groq fallback also fails. Do not log prompt, `game_context`, key, or generated text.
- Keep success response schema stable. Diagnostics are server logs only.

## Verification

- JVM tests for a stable preference key and reward decision/receipt policy.
- Android UI tests for Nexus portraits, first-use one-shot Chamber flow and no replay button, duplicate reward receipt destination, active fighter selection marker, charged Super control, and shared action palette in both themes.
- Backend tests assert both fallback providers log safe category metadata and no secret/prompt leakage.
- No live provider generation call without the user's explicit authorization; `/ready` and `/openapi.json` are read-only checks and do not prove inference.
