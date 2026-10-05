# 042 - Evidence

Status: implementation complete; full Android/backend suites blocked by this environment.

## Implemented

- `LovableBattle` tracks normalized HP (`Vida/9`) per hero, active hero, persistent reserve HP, global super and uncapped actions. Boss damages only the active hero; a KO requires selecting a living reserve.
- Team picker uses three ordered slots. Battle cards show active/reserve/KO, HP and character-specific special. Switching is free. Attack lunges toward the boss, defense displays a cyan shield, destabilize pulses the active card.
- Defeat and reward screens show action, damage, action-type, super-use, KO and survivor summaries. Existing rewards and ledger remain unchanged.
- Deadpool facts include owned roster/variants, authored stats, current team/battle, campaign wins, resources, forge inventory and existing daily challenge state. Read-only challenge lookup avoids creating a run. Comic Vine editorial facts remain separate.
- Local backend accepts `game_context` up to 6,000 characters and keeps AI non-authoritative. Render deployment/contract remains pending (Q-057).

## Checks

- Java domain classes compiled with `javac` using a temporary Android `R` stub; a domain smoke run passed free switching, HP persistence, active-only damage, switching before spending shared super, KO/replacement/defeat, counter-based victory and an 8-action fight.
- Python syntax compilation passed for edited backend and route-test files.
- Full Gradle suite could not start: wrapper distribution download was blocked by network policy; the local validation script could not access the Android SDK.
- Backend pytest could not start: the local validation script could not access the venv base Python; system Python has no pytest module.
- No full Android build, screenshots, device install, Render call or remote action was completed.
