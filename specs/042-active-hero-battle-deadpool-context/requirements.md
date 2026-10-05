# 042 - Active-hero combat and full Deadpool context

Status: approved by Erick on 2026-10-05; local implementation in validation.

## Goal

Make each battle a tactical fight with three selectable heroes, individual HP, active-hero actions, visual feedback and a result recap. Give Deadpool enough factual app context to discuss the roster and suggest teams.

## Scope

- Show three numbered slots for distinct owned characters and their equipped variants.
- Keep attack, defend, destabilize, a character-specific super and free switching.
- Keep the super meter global and shared between heroes.
- Persist each hero's HP while in reserve; a KO'd hero cannot return during the battle.
- No action cap; end only when the boss or all three heroes fall.
- Show battle recap in defeat and reward screens without changing rewards.
- Provide Deadpool allowlisted authored stats, ownership, variants, team, campaign, resources, forge, challenge and current battle. Comic Vine facts remain editorial only.

## Out of scope

- Multiplayer, economy/reward changes, AI deciding combat/results, new campaigns or canonical Marvel facts.
- Persisting a battle after closing the Activity.

## Approved mechanics (Q-059 / DEC-093)

- Switching is free and does not use an action.
- Boss attacks only the active hero. Reserves retain their HP.
- Hero max HP is `max(60, round(VariantStats.Vida / 9))`. Bosses are tuned against the trio.
- Global super retains existing charge behavior: actions add charge; use at 60% or more; reset to zero on use. Active hero determines name and power.
- A fallen active hero requires the player to select a living reserve before continuing.

## Main flow and edge cases

1. Choose three owned heroes in ordered slots.
2. Choose an active hero, read boss intent, then attack, defend or destabilize. Switching does not advance the action.
3. The action updates boss HP, active HP and global charge. If the active hero falls, require a living replacement.
4. Continue until boss HP or all hero HP reaches zero.
5. Show a result recap; victory still uses the current idempotent reward flow; defeat permits a retry.
6. Deadpool uses provided facts for team advice and general humorous conversation; AI output cannot update game state.

## Acceptance criteria

- [x] AC-01: Three ordered slots show three distinct owned heroes and equipped variants.
- [x] AC-02: Exactly one active hero acts; changing the active hero is free.
- [x] AC-03: HP is individual and persists in reserve; boss damages only the active hero.
- [x] AC-04: Super charge is global; special name and power depend on active hero; meter resets after use.
- [x] AC-05: No action limit; victory/defeat only when boss/team falls.
- [x] AC-06: Attack, defense and destabilize show distinct visual feedback.
- [x] AC-07: Defeat and reward screens show a recap; rewards remain unchanged/idempotent.
- [x] AC-08: Deadpool payload includes game state and authored stats for team/strength advice.
- [x] AC-09: Comic Vine editorial data is kept separate from authored combat stats.
- [x] AC-10: AI does not decide result, damage, difficulty, rewards or persistent state.

## Dependencies and limitations

- Android data sources: `VariantStats`, `GameCatalog`, `ForgeRepository`, `CampaignState`, `ChallengeState`, `BattleMission`, `LovableBattle`.
- Local backend accepts `game_context` up to 6,000 chars. Published Render schema still needs deployment (Q-057).
