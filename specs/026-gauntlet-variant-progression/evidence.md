# 026 — Evidence

Implementation and verification completed 2026-09-28:

- `scripts/validate-local.ps1 -Target android` passed: `testDebugUnitTest`, `assembleDebugAndroidTest`, `lintDebug`, and `assembleDebug`.
- `scripts/run-android-instrumentation.ps1` passed on `emulator-5554`: **10 tests**, including v3→v4 migration preserving inventory/campaign, all-six-Stone Gauntlet gate without consumption, sequential/idempotent tier unlock, Stone non-consumption, and persistence after repository recreation.
- Visual AVD captures: `forge-avd.png` shows the six-Stone gate; `variants-locked-avd.png` shows all five tiers locked before Gauntlet activation. `gauntlet-avd.png` shows the locked-state action in Forge.
- Release build was also rebuilt and signed after these changes; see root `RELEASE_NOTES.md`.

No physical handset was connected. Battle stat effects remain deliberately outside this increment.
