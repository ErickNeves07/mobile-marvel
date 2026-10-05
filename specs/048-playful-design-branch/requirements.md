# 048 - Playful visual redesign on a separate branch

Status: approved by Erick on 2026-10-05; Q-060 resolved.

## Objective

Create a comfortable, playful Android visual language based on the published Lovable opening, Nexus, and campaign pages, while keeping the current release on `main` and preserving Comic Vine character portraits.

## Requirements

- R-048-01: Work only on branch `feat/playful-design` based on `origin/main`; publish that branch separately. Do not merge it into `main` or install it on the phone in this task.
- R-048-02: Use the three published pages as the primary visual reference: rounded shapes, friendly typography, generous spacing, gem colors, soft dark surfaces, green/yellow/coral actions, and a clear map path.
- R-048-03: Preserve the existing Comic Vine portrait selection, credits, caching, and fallbacks for all characters and variants. Replace or restyle other decorative art where needed.
- R-048-04: Show actual local inventory, resources, campaign progress, and unlock state; prototype numbers and states are visual examples, not game data.
- R-048-05: Preserve gameplay, rewards, progression, persistence, AI contracts, routes, and accessibility behaviors while changing presentation.
- R-048-06: Keep a usable light mode and dark mode, with readable contrast, 48dp minimum targets, spoken labels, and reduced-motion-safe feedback.
- R-048-07: Apply the visual language across the entire app, including Forge, Collection, battles, daily challenge, Deadpool, and subflows; the opening, Nexus, and campaigns receive layout-level redesigns.

## Acceptance

- Opening shows a friendly central six-gem illustration, two-line title, clear invitation, and one primary action; skip and story continuation still work.
- Nexus shows a welcome hierarchy, real resource summary, six-stone progress, a next available mission panel, and large activity shortcuts; all open the correct destinations.
- Campaigns show nine connected chapter cards in difficulty order with real completed/available/locked states, boss portraits, and a clear tap target; mission details and battle entry still work.
- Shared colors, typography, cards, buttons, navigation, forms, and feedback match the new language on the remaining approved screens without changing game data.
- Existing Comic Vine images remain in use. Dark/light layouts, loading/error/empty states, screen sizes, and back navigation remain usable.
- Android unit tests, lint, debug build, instrumentation compile, and meaningful UI checks pass after the last edit. Screenshots of opening, Nexus, and campaigns are reviewed.

## Out of scope

- New game mechanics, rewards, characters, API providers, or backend changes.
- Copying mocked resources/progression from Lovable.
- Publishing or installing the branch as the production app.

## Scope decision

- Q-060 resolved: Erick chose the entire app, with the three named Lovable pages as reference.
