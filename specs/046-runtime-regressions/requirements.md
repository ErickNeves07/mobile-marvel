# 046 - Requirements

Status: approved by Erick's explicit bug report and follow-up direction.

## Objective

Repair regressions in the Nexus Chamber presentation, first-use Chamber briefing, Deadpool provider diagnosis, post-battle story/reward transition, and battle action clarity.

## Scope

- Restore Reed Richards and Doctor Strange portraits inside the Nexus hero panel labeled "Operacao Ativa / Camara de Variantes".
- Keep the explanatory Chamber scene at the existing first gauntlet/variant use flow. Show it once, even for an install whose old preference had already been set. Remove the "rever" action; do not add another Nexus-entry scene.
- Prevent an after-battle story from returning to a completed victory screen when its reward receipt already exists. Keep reward credit/XP and fragment receipts idempotent.
- Add safe provider failure diagnostics so Render logs distinguish Gemini and Groq status failures without recording prompts, API keys, response bodies, or player data.
- Preserve the local Deadpool response only as fallback; the app must still show actual backend text whenever `fallback=false`.
- Make the active fighter immediately identifiable in the three-character battle row and make the charged Super control visually distinct while preserving its global charge and active-character-specific attack.
- Restyle fragment shop purchase controls to use the app's dark surface and gold/stone accents instead of the generic bright-blue action button; purchase eligibility and prices stay unchanged.
- Replace the generic bright-blue primary action treatment across app screens in dark mode with a coordinated dark-surface/gold-outline style. Keep the existing light-mode button palette unchanged.

## Acceptance

- Nexus always renders both editorial portraits with names and roles.
- First gauntlet use displays the Chamber scene; completing or skipping it returns to the variant flow. It does not replay on later visits and no replay button is present.
- A newly completed battle plays its scene, then opens the reward screen. An already-rewarded battle opens its existing receipt directly and never returns to the victory panel after its story.
- Duplicate reward collection does not add resources twice.
- The active fighter has a high-contrast selected treatment, a clear "ATIVO / SUA VEZ" label, and an accessibility description identifying the active turn. Other living fighters remain visibly switchable.
- When charged, Super has its own Infinity-themed control, clearly names the active character's move, and states that charge is shared. Activating it retains the existing combat effect and global meter behavior.
- Fragment shop controls use a dark/gold outlined treatment in both enabled and disabled states, with disabled offers still visibly unavailable and purchase rules unchanged.
- Shared primary actions are dark/gold in dark mode and retain their existing light-mode appearance.
- Provider failures are logged as provider + sanitized HTTP/status category only; no secrets, prompt, game context, or raw provider response appears in logs.
- Automated checks cover these paths; Android unit/lint/build and backend tests pass.

## Edge cases

- App update retains SharedPreferences from 0.12.0: the one-time briefing still appears once using a new stable preference key.
- Story skip and Android Back complete the continuation once.
- Replaying a completed encounter does not replay the first-clear story or grant duplicate rewards.
- Provider key readiness only proves a non-empty secret exists; it is not treated as proof that generation succeeded.
