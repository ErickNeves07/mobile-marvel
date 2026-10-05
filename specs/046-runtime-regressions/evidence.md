# 046 - Evidence

Status: pushed as `873d55c` and installed as Android 0.12.0 on phone `C6OFVWYD4DZTBA5H`; Render deployment remains pending.

## Implementation evidence

- Nexus hero now renders Reed Richards and Doctor Strange portraits with role labels through the Comic Vine portrait loader.
- The Chamber briefing uses `chamber_briefing_seen_v2`, appears once from the first-use variant flow, and has no replay control.
- First-time battle completion shows the authored story and then the reward screen. Duplicate completion skips story and shows the stored fragment receipt without regranting credits/XP or reconstructing the victory battle.
- Deadpool returns actual backend text whenever `fallback=false`. Local replies distinguish provider fallback from inability to reach the backend. Gemini/Groq failures are logged with provider and sanitized code only.
- Active fighter card has a bright selected treatment and explicit active-turn label. Super has a dedicated dark/gold Infinity control naming the active hero's move and shared charge.
- Shared action buttons are dark/gold in dark mode while the existing cyan style remains in light mode. Shop buy controls follow the theme without changing eligibility or prices.

## Verification

- `powershell -ExecutionPolicy Bypass -File scripts\validate-local.ps1 -Target android` — passed after final UI changes: `testDebugUnitTest`, Android instrumentation-test compilation, `lintDebug`, and `assembleDebug`. Instrumentation APK compiled; tests were not run on a device/AVD in this task.
- `powershell -ExecutionPolicy Bypass -File scripts\validate-local.ps1 -Target backend` — passed: `pip check`; pytest **59 passed**.
- A single minimal provider generation request was made earlier in this task after Erick explicitly authorized one call. It returned `fallback=false`, confirming the deployed route can generate at least that test response. No follow-up provider requests were made.
- Automated backend test verifies Gemini/Groq failure logs contain only provider and safe HTTP category, never prompt, key, or response contents.

## Limitations / next step

- App install and launch were confirmed on the phone. Manually verify both themes and the Chamber/repeated-reward flows.
- Deploy the backend diagnostics changes before using Render logs to distinguish provider failures. Render deployment was not performed.
