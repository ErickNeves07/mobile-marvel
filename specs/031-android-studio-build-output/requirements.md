# 031 — Android Studio build output: Requirements

Status: approved by Erick's request to fix the phone build failure on 2026-10-04.

## Goal

Make an ordinary Android Studio or direct Gradle build write generated files outside the OneDrive project tree, avoiding the reported `AccessDeniedException` at `:app:generateDebugBuildConfig`.

## Scope

- Redirect root and app Gradle build directories to a stable local path outside the project when `RI_VALIDATION_DIR` is unset.
- Keep validation and release scripts using their existing `RI_VALIDATION_DIR` output contract.
- Document where Android Studio finds the debug APK after the change.

## Out of scope

- Game behavior, API configuration, signing credentials, and app data migration.
- Deleting or changing the existing ignored `android-app/**/build` folders.

## Acceptance

- [x] Direct `gradlew.bat --offline :app:assembleDebug` succeeds without an init script or `RI_VALIDATION_DIR`.
- [x] Its `:app:generateDebugBuildConfig` and APK output are outside the OneDrive project tree.
- [x] Existing local validation and release build commands still use `RI_VALIDATION_DIR`.
- [x] No credentials or generated artifacts are committed.

## Edge cases

- Separate checkouts must use separate default output folders.
- If `LOCALAPPDATA` is unavailable, use the host temp directory.
