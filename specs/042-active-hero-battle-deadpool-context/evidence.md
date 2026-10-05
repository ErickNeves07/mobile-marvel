# 042 - Evidence

Status: implementation complete; Android build and physical installation verified. Backend pytest and Render deployment remain pending.

## Implemented

- `LovableBattle` tracks normalized HP (`Vida/9`) per hero, active hero, persistent reserve HP, global super and uncapped actions. Boss damages only the active hero; a KO requires selecting a living reserve.
- Team picker uses three ordered slots. Battle cards show active/reserve/KO, HP and character-specific special. Switching is free. Attack lunges toward the boss, defense displays a cyan shield, destabilize pulses the active card.
- Defeat and reward screens show action, damage, action-type, super-use, KO and survivor summaries. Existing rewards and ledger remain unchanged.
- Deadpool facts include owned roster/variants, authored stats, current team/battle, campaign wins, resources, forge inventory and existing daily challenge state. Read-only challenge lookup avoids creating a run. Comic Vine editorial facts remain separate.
- Local backend accepts `game_context` up to 6,000 characters and keeps AI non-authoritative. Render deployment/contract remains pending (Q-057).

## Checks

- Java domain classes compiled with `javac` using a temporary Android `R` stub; a domain smoke run passed free switching, HP persistence, active-only damage, switching before spending shared super, KO/replacement/defeat, counter-based victory and an 8-action fight.
- Python syntax compilation passed for edited backend and route-test files.
- First Android build exposed a reference to undefined `R.dimen.space_5`; defining it as 20dp fixed compilation.
- `powershell -ExecutionPolicy Bypass -File scripts/validate-local.ps1 -Target android` passed `testDebugUnitTest`, `lintDebug`, and `assembleDebug`.
- APK package metadata reports `versionCode=11`, `versionName=0.11.0`. `adb -s C6OFVWYD4DZTBA5H install -r ...app-debug.apk` succeeded, the app launched to `MainActivity`, and recent logcat showed no app crash.
- Backend pytest was not run because its configured Python venv/base interpreter is inaccessible. No Render request/deploy was performed.
