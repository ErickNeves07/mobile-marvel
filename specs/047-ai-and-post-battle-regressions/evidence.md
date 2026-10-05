# 047 - Evidence

Status: implemented and locally validated; deployment pending.

## Comparison

- The app and request schema allow up to 6,000 characters in `game_context`, but both Gemini and Groq adapters call `GroqNarrativeAdapter._validate_messages`, whose per-message bound is 4,000. The route prepends server facts/instructions to the context, so the app's full request is rejected before either provider is called. A minimal live test with empty context did not exercise this case.
- Before commit `05170f7`, the 422 compatibility retry preserved the caller's `context_id`; `05170f7` rebuilt the request with hard-coded `context_id="nexus"`. Restore the earlier behavior.
- Battle actions schedule a delayed `renderMagnetoBattle` after victory; collecting rewards before that delay expires lets the pending callback replace the story screen. Duplicate mission completion also skipped its scene in `873d55c`.
- The Nexus string `nexus_shortcut_action` includes `%1$s`. It is currently a content description, but remove the raw token so it cannot surface in the reported view/accessibility path.

## Fixes and validation

- Provider adapter message bound is now 8,000 characters. The backend regression sends the maximum 6,000-character client context through the route and actual adapter validation with a mocked Gemini transport; the full system message fits and does not fall back.
- Android's one-time HTTP 422 compatibility retry retains the original context id (`app`), matching the pre-`05170f7` behavior.
- Nexus content descriptions compose the plain word `Abrir` with the destination; the raw `%1$s` token has been removed from resources.
- Delayed battle callbacks redraw only if the same battle remains on screen and neither a story nor reward claim is active. The collect action shows a disabled progress label immediately and restores itself on storage failure.
- Every valid victory claim shows the authored post-battle scene, including replays; duplicate reward records remain idempotent.
- `powershell -ExecutionPolicy Bypass -File scripts/validate-local.ps1 -Target backend`: `pip check` clean; **60 pytest tests passed**.
- With Android Studio JBR and the existing local SDK/cache, `gradlew.bat --offline :app:testDebugUnitTest :app:assembleDebugAndroidTest :app:lintDebug :app:assembleDebug`: **BUILD SUCCESSFUL**. JVM tests passed, instrumentation tests compiled, lint passed, and debug APK assembled.
- Instrumentation was compiled but not run on a device. No provider call, app install, commit, push, or deploy was made.

## Handoff

Deploy the backend update to Render before testing Deadpool's full context in production. Then test a question about the current team and a general Marvel opinion. The Android Nexus and battle changes are in the local build.
