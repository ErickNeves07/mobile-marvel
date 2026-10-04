# 022 — Playable game loop — Design

Roster identity remains sourced from `shared/game_catalog.json`; editorial Comic Vine fields never enter gameplay. Daily puzzle date/target/guesses/status, campaign team/unlocks/completions and Forge inventory/reward ledger share `ForgeRepository` SQLite, so each reward and completion are atomic.

Daily target and Stone derive from SHA-256 of device-local ISO date. Six known roster IDs maximum; feedback reveals faction equality and alphabetical direction. Correct guess applies stable `daily:<date>` reward, exactly three Shards; loss grants none.

Campaigns use X-Men/Magneto and Fantastic Four/Doctor Doom, each with three sequential authored mission labels and fixed enemy strength. Three unique faction members are selected and saved. Deterministic turn resolution uses authored game power values, not Comic Vine or AI. Victory grants three shards with stable mission event ID and unlocks next mission. Defeat leaves mission unlocked for retry.

All writes and network calls run on a single background executor. Backend calls use the optional HTTPS BuildConfig URL; absent URL preserves fully playable offline features. UI inputs label states textually; color does not convey outcomes alone. Instrumentation covers deterministic daily win/loss, campaign mission ordering, team persistence, reward idempotency, and state restoration across repository recreation.
