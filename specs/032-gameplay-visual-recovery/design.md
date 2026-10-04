# 032 — Gameplay and visual recovery: Design

## Reference and current defects

Captured public Lovable routes at 390 × 844 on 2026-10-04: Nexus, Campanhas, Forja, Coleção, Desafio, Deadpool and Batalha. The published map has nine chapter cards. Current Android has two campaign expanders with six missions, but only X-Men mission 1 has an interactive battle; the other five call an automatic `GameRules.battle` and show a toast. The root Collection eagerly renders 105 variant cards and requests 21 portraits in two worker threads; portrait fetch failures are cached as unavailable for the Activity lifetime. Forge merge actions are hidden inside collapsed details. Deadpool has two inert cards and its primary action always uses `context_id=nexus`. Nexus companion cards have abstract placeholders. Daily Challenge is embedded in a plain Nexus card.

## Data boundaries

- Keep `shared/game_catalog.json` and authored `VariantStats`/battle rules as the game source. Display stats with a visible “Dados do jogo” label.
- Obtain editorial portraits/details from the FastAPI Comic Vine proxy. Character detail may fetch metadata by game ID then `/v1/editorial/characters/{id}` for deck/real name/powers/teams. Display Comic Vine attribution and source link; do not claim the portrait represents a specific variant.
- Store owned general characters and equipped variants in the existing SQLite repository with a forward-only migration. Seed the approved starter trio once, preserving existing rewards/stone counts/campaign progress. The acquisition trigger for other characters is Q-045 and stays unimplemented until answered.
- The battle team chooser reads persisted ownership and variant equipment, never the catalog alone. Existing campaign IDs and reward ledger remain stable unless Q-046 changes the campaign structure.

## UI approach

- Collection root uses 21 portrait cards. Tapping opens a character page with a hero portrait, five tier options, current equip status, authored power bars, editorial facts and a prominent unlock/equip action with exact requirement or block reason.
- Battle selection opens a separate roster panel of owned cards with portraits, role/power, equipped tier and three selected slots. Mission scene places enemy and team portraits inside dark angular panels, followed by intent/feedback and spaced tactical actions.
- Forge shows a visible recipe action for every stone/stage with at least two items; the 2:1 confirmation names both input/output. The Manopla activation state is explicit on the detail page and variant page.
- Nexus reuses the editorial portrait loader for Reed/Strange and the code-native Gauntlet view for its miniature. Daily Challenge gets its own transient page; Nexus has a single entry summary.
- Deadpool cards choose context `nexus`, `xmen`/`fantastic_four`, `daily_challenge` and `forge` where applicable; disabled-looking controls must either work or be removed. The server scripted fallback remains visibly labeled.
- All image targets use a consistent fit/center treatment, a bounded loading state and a retry control. Retry clears only transient failure state, not the trusted metadata cache. Do not log provider keys or full upstream errors.

## Tests and rollout

Add repository tests for starter ownership/migration and variant progression, battle validation/results, and merge 2:1. Add UI tests for 21-card root, team chooser, six distinct battle entry points, reward gates and portrait retry. Backend tests cover any new editorial mapping/endpoint. Verify direct Android Studio build and installed debug update on the connected phone using the matching debug signature. Do not run destructive full instrumentation against the user's live data; use an isolated test package or AVD for state-changing tests.
