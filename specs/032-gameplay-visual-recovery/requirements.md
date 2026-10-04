# 032 — Gameplay and visual recovery: Requirements

Status: Erick explicitly requested the corrections on 2026-10-04. The independent items below are approved; character acquisition and campaign expansion await answers Q-045/Q-046.

## Goal

Make the playable Android app match the published Lovable layout more closely while making every visible battle, merge, portrait, variant action and Deadpool control functional with persistent game state.

## Confirmed requirements

- Battles show Comic Vine portraits for the selected team and a relevant enemy, a readable scene, meaningful tactical choices, clear hit feedback and spaced controls. Every selectable mission opens its own battle instead of resolving automatically. The saved team affects results and may lose/retry without rewards.
- A battle team can use three distinct owned characters from the full collection, regardless of campaign faction. It uses each character's equipped, owned variant. The starter roster is Homem-Aranha, Wolverine and Tocha Humana.
- The Collection root shows 21 general characters, each with its currently equipped variant; a character detail allows viewing/changing owned variants and shows game power attributes separately from Comic Vine editorial name, summary, powers, teams and source link.
- Portraits are centered consistently and transient network failures offer a retry. The Homem de Ferro image must load on a connected phone. Reuse the 21 editorial images across variants as previously approved; do not package Comic Vine binaries into the APK.
- The Forge exposes 2:1 merge for two matching items, including Fragmento → Núcleo Instável, with inventory refresh and actionable errors. A complete/activated Manopla allows progression to the next valid variant where the approved requirements are met.
- Nexus shows Reed Richards and Doutor Estranho portraits and a polished, stateful Manopla illustration. Daily Challenge has a dedicated, readable screen matching the Lovable hierarchy while preserving its six guesses and one Fragmento reward.
- Deadpool controls must call the existing backend contexts or navigate to the corresponding local feature. Show loading, generated text, scripted fallback and actionable connection error clearly. AI cannot determine game facts or rewards.
- Blue actions have breathing room from adjacent text. Remove stale or misleading mock presentation.

## Open critical decisions

- **Q-045:** how the other 18 general characters are acquired after the starter trio. No new acquisition/reward rule or migration will be invented until Erick answers.
- **Q-046:** whether the two campaigns/six approved reward missions become the nine Lovable chapters. No extra reward package or chapter gate will be invented until Erick answers.

## Acceptance

- [ ] On a physical phone, the starter trio can be selected in any available battle and a locked character cannot be selected; equipped variant and attributes affect combat according to the approved authored data.
- [ ] Each implemented mission launches a distinct interactive scene, with its own enemy image and reward only after first victory.
- [ ] Coleção root has 21 cards; detail has five tiers, authored stats and real editorial facts/credit, with unlock/equip state updating immediately.
- [ ] Homem de Ferro and sampled portraits load on phone with correct centering, error/retry and credit.
- [ ] 2 Fragmentos merge into 1 Núcleo Instável; active Manopla permits a valid next-tier unlock and equip.
- [ ] Nexus, Daily and Deadpool match the observed mobile reference in hierarchy and maintain real data/actions.
- [ ] Relevant JVM, backend, Android instrumentation, lint and direct debug/release builds pass after the last edit; inspect phone screenshots/flows and record remaining differences.

## Out of scope

- Comic Vine images as bundled/static assets; Comic Vine or AI derived combat values.
- PvP, account login, paid unlocks, retroactive rewards and unapproved reward changes.
