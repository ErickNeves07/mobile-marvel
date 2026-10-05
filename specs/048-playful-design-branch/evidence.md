# 048 - Evidence

Status: complete; branch published separately from `main`.

## Prototype research

- Public routes inspected: `https://marvel-ruptura-infinita.lovable.app/`, `/nexus`, `/campanhas` on 2026-10-05.
- `web.run` could not render the SPA. A direct public HTML request returned 200 and route bundles. Screenshots were captured using a fresh temporary headless Chrome profile, without reading the user's open personal Chrome windows.
- Automatic approval rejected inspecting the existing Chrome window because it contained WhatsApp. No personal window content was read.
- Captures in `%TEMP%\MarvelLovableIsolated`: `intro2.png`, `nexus-tall.png`, `campanhas-tall.png` (research only, not app assets).
- Palette, typography, screen structure, and prototype data boundary recorded in `design.md`.

## Build and visual validation

- `./gradlew.bat --offline :app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:connectedDebugAndroidTest` passed after the final UI code change: **42/42 emulator instrumentation tests** on Medium Phone API 36.1, plus JVM tests, lint and APK build.
- After giving the experimental APK its own `versionCode=13` and `versionName=0.13.0-playful`, `testDebugUnitTest`, `lintDebug` and `assembleDebug` passed again. The version metadata edit did not change app behavior.
- Existing UI tests were adapted to the new action colors, labels, card structure and real variable rewards. The tests still cover the intro, Nexus, daily challenge, Forge, Collection, team chooser, battle, story, receipt, shop and gauntlet flows.
- Reviewed emulator captures: `reports/playful-design/intro.png`, `nexus-dark.png`, `campaign-dark.png`, `campaign-light.png`. A narrow screen exposed clipped top controls; labels were shortened while full accessibility descriptions remained. Boss portraits now show a legible initial while the existing Comic Vine loader resolves.
- The emulator did not resolve remote Comic Vine portraits during capture. The portrait loaders, IDs, attribution and fallbacks remain wired; live editorial quality must be reviewed when the branch is installed on a networked device.
- No backend, gameplay rules, rewards, provider keys or user data were modified. The production phone and `main` were not changed.

## Publication

- `git diff --check` and `git diff --cached --check` passed; staged file list contained only Android UI/tests, spec/memory and four emulator captures. No `.env`, key, APK or cache was committed.
- Conventional Commit `3f06b71` (`feat(ui): redesign Android app with playful visual system`) was pushed to `origin/feat/playful-design` on 2026-10-05. The follow-up documentation commit records closure.
- `origin/main` remained at `eee2fee`. No merge, Render deploy or installation on the physical phone was performed.
