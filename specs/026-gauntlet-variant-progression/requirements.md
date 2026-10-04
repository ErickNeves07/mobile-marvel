# 026 — Gauntlet and variant progression — Requirements

Status: implementation active. Rules below derive from the approved product vision and reuse the existing five variant tiers and six complete-Stone slots.

## Goal and scope

Connect recovered complete Infinity Stones to the Containment Gauntlet, unlock the Variant Chamber, and persist character variant ownership/equipment locally.

## Product rules derived from existing requirements

- The Gauntlet requires one complete copy of each of the six Infinity Stones in the Forge.
- Activating it consumes no Stone; it unlocks the Variant Chamber persistently.
- Variant tiers are ordered `origin`, `ascension`, `legendary`, `multiversal`, `infinity`, matching the five names in the shared catalog.
- For each playable character, owned progression begins at `origin`. Each next tier requires the complete Stone with the matching tier (`origin`→Space, `ascension`→Mind, `legendary`→Reality, `multiversal`→Power, `infinity`→Time). The Soul Stone is the initial Gauntlet activation requirement.
- Unlocking a tier does not consume a Stone. This keeps the six complete-Stone collection requirement and avoids a new cost rule; the same completed Stone can unlock that tier across characters.
- A character has at most one equipped owned tier; `origin` is the initial equipped tier. Writes persist locally. Duplicate unlock operations are idempotent.
- These unlocks are collection progression only; they do not affect combat stats in this increment.

## Acceptance

- [x] Gauntlet activation remains locked until each Stone has at least one complete item; activation is saved and does not change Forge counts.
- [x] Variant Chamber shows owned/locked tiers and the named variant per catalog character.
- [x] Tier unlock validates Gauntlet, character, expected next tier and required Stone; repeat requests do not duplicate ownership.
- [x] Equipped tier must be owned; state persists and one equipped tier is stored per character.
- [x] Failed operations leave Forge inventory and progression unchanged through SQLite transaction rollback.
- [x] Tier unlocks do not alter battle balance; documented explicitly.
