# 042 - Design

## Approved decisions

Q-059 / DEC-093 approved free switching, active-only boss damage, persistent reserve HP, HP normalized by `VariantStats.Vida / 9`, boss strength calibrated against the trio, shared super and no action cap.

## Components and behavior

- `LovableBattle` owns three `Fighter` objects, each with immutable `FighterSpec`, individual HP and a KO state. The battle owns the active hero, boss HP, global charge, action counter and recap totals.
- Game-authored mission tells/counter choices and `VariantStats` remain the only combat inputs. Comic Vine and AI never determine stats or outcomes.
- Boss damage applies to active hero only. Selecting a living hero is free; after KO, the next living hero must be selected.
- Super remains available at 60% charge and resets the global meter after use. Special damage uses the active hero's stats and authored move name.
- Boss HP scales with mission difficulty and the team's authored attack profile.
- Team picker shows three ordered slots. Battle cards show active/KO/reserve state, comic cover, HP and special name. Attack lunges toward boss; defend displays cyan shield; destabilize pulses the hero card.
- Recap includes actions, damage dealt/received, action counts, super users, KOs and survivors. Existing reward ledger is unchanged.
- Deadpool request facts are built locally from owned roster/variants, authored stats, current battle, campaign progress, resources, forge inventory and an existing daily challenge. Read-only daily lookup must not create a challenge.
- Backend accepts the facts payload up to 6,000 characters and instructs the model to use authored stats for advice; general humor/opinions are allowed. AI remains non-authoritative.

## Contracts and privacy

Request remains `{context_id, prompt, game_context}`. No PII, API key, Comic Vine payload/image URL, or user-supplied names are added to `game_context`. No prompt is persisted.

## Persistence and rollback

Battle and recap remain in-memory only. No SQLite migration or reward change. If a battle is abandoned, it is discarded. The legacy Deadpool backend fallback may only receive the bounded prompt until Render is updated.

## Verification plan

- Android domain tests: free switching, HP persistence, active-only damage, required replacement, shared super, uncapped actions, victory/defeat and recap.
- Backend checks: request schema accepts 6,000 chars; prompt preserves factual-only and non-authoritative behavior.
- Build and UI review of battle/recap. Render publication and live AI call remain separate operations.
