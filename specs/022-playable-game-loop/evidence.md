# 022 — Evidence

Requirements derive from the product vision, requirements, domain UX catalog, and the product owner's delegated rules. Implementation adds daily puzzle persistence, campaign teams, deterministic battles, and idempotent Forge rewards.

- `scripts/validate-local.ps1 -Target android` passed JVM unit tests, Android test APK compilation, lint, and debug APK assemble.
- `scripts/run-android-instrumentation.ps1` passed 13/13 tests on the preserved `emulator-5554`: 10 Forge repository tests and 3 game-loop repository tests.
- Build initially exposed duplicate Java switch cases for Thing/Mister Fantastic; redundant unreachable cases were removed, preserving their existing authored power values.
- Signed release was rebuilt; details and limits are recorded in root `RELEASE_NOTES.md`.

Manual end-to-end challenge/campaign playthrough was not exercised in this validation; the instrumented tests cover Forge persistence and merge/progression boundaries.

`GameLoopRepositoryTest` verifies the daily win grants one reward and survives repository recreation; six wrong guesses persist a loss without reward; X-Men missions reject out-of-order completion, preserve a selected three-character team, and issue one reward per mission. The reward's current stage and amount are verified in spec 028: one Fragment per daily victory and authored campaign packages. No app reinstall or user-data deletion was performed. Manual end-to-end screen interaction remains unverified.
