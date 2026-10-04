# 022 — Loop jogável — Requirements

Status: **implementation active; rules delegated by the product owner where specified below.**

## Goal and scope

Turn the existing offline roster, campaign placeholders and Forge into a playable persistent loop: daily deduction challenge, team selection, sequential campaign missions, deterministic battles and idempotent rewards.

## Approved/delegated rules

- Daily challenge: one puzzle per device-local date, target selected deterministically from the 21 game-roster IDs; six guesses, guesses come from that roster; feedback reports matching faction and alphabetic direction only. A correct solution grants exactly 3 Shards for that date's deterministic Stone; a failed challenge grants none. One reward per date event ID.
- Campaigns: X-Men vs Magneto and Fantastic Four vs Doctor Doom (the latter is in the approved initial boss roster; campaign rules were delegated). Three sequential missions per campaign.
- Teams contain three playable characters from the campaign faction.
- Combat uses authored game stats/actions and deterministic rules, never editorial Comic Vine data or AI output. Wins unlock the next mission and grant exactly 3 Shards for the authored mission Stone. Losses are retryable and consume no Forge inventory.
- Challenge, team, battle and completion state persist locally. Grant event IDs are idempotent. Daily date uses device local date; local-only challenge is not server anti-cheat.

## Acceptance

- [x] Challenge accepts only known roster entries, displays six tries/feedback and restores state after process restart/date rollover.
- [x] Challenge win grants exactly three Shards; loss grants none; duplicate event IDs never double-grant.
- [x] Both campaigns contain three missions with team-of-three validation, deterministic battles and sequential unlock.
- [x] Battle victory rewards/unlocks persist; defeat is retryable and does not change inventory.
- [x] A fresh install can complete the challenge and use Forge; progress persists locally.
- [ ] Accessible UI states and all acceptance claims require Android build/instrumentation/AVD verification. Implementation is written but not runtime-validated.
