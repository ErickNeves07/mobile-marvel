# 048 - Design

Status: approved scope; implementation design ready.

## Reference audit (published Lovable, 2026-10-05)

| Surface | Observable treatment | Android translation |
| --- | --- | --- |
| Opening `/` | Navy field, soft coral/mint glows, stacked rounded portal, six orbiting gems, rocket, friendly title, mint full-width CTA | Code-drawn central illustration, rounded skip chip, spacious centered copy, mint action; retain first-run story semantics |
| Nexus `/nexus` | Small greeting, large friendly question, three compact stats, six-gem progress card, highlighted next mission, three large activity tiles | Real credits/XP and completed stones, next unlocked mission from local repository, direct destination/forged challenge links |
| Campaigns `/campanhas` | “Mapa de aventuras” header, warm info card, central path, alternating rounded chapter cards, large state icon, slim progress line | Nine missions in authored order, actual completion/unlock state, Comic Vine boss art embedded in each card, semantic status label |

The public CSS uses `#20233D` navy, `#343855` soft panel, `#F8F7FF` ink, `#7BD389` mint, `#FFCB77` sun, and `#EF767A` coral. It uses Outfit headings and Figtree body. The Android app already bundles Sora and Barlow; prefer bundled Sora for rounded headings/body to avoid a new font dependency, then assess the rendered result.

## Android structure

- Add `play_*` color tokens for day/night, and use them to remap existing semantic colors. Keep six Infinity Stone hues distinct.
- Increase shared card/button/nav radii, padding, and touch areas. Build rounded drawable helpers with restrained shadow/elevation and clear selected/disabled states.
- Give opening, Nexus, and campaigns dedicated layout composition in `MainActivity`; small reusable views can be extracted where it reduces duplication.
- Keep existing `PortraitLoader` calls and editorial attribution. For campaign cards, reflow opponent portraits rather than removing them.
- Replace mock-compatible Lovable concepts (level, energy, fixed mission) with local XP, credits, stone inventory, and first available mission. Read repository on background executor and update attached views on the UI thread.
- Preserve explicit theme preference. Dark follows the observed navy palette; light uses a warm pale background with dark ink and matching accent colors.
- Respect `ValueAnimator.areAnimatorsEnabled()` and Android accessibility settings; decorative visuals have no spoken content, actionable cards have labels and status.

## Risks and tests

- `MainActivity` is a large programmatic View builder. Keep gameplay handlers/data calculations unchanged and isolate visual helpers so rerenders do not interfere with rewards or battle state.
- Test mission ordering and selection, real inventory/resource binding, button destinations, theme contrast, and existing collection/battle flow. Compile instrumentation tests and capture screenshots on an emulator if available without overwriting the installed physical app.
- Do not ship secrets or raw public-site bundles; screenshots are research evidence only.
