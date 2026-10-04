# 031 — Android Studio build output: Design

## Decision

The root Gradle script sets `layout.buildDirectory` for root and subprojects. It honors `RI_VALIDATION_DIR` when present. Otherwise it uses `%LOCALAPPDATA%/RupturaInfinita/gradle-builds/<checkout-hash>` on Windows, or the JVM temp directory as fallback. The checkout hash comes from the normalized canonical project path, keeping multiple clones separate.

This runs during ordinary Android Studio sync and direct Wrapper invocation. The existing init script remains compatible because the validation and release scripts set `RI_VALIDATION_DIR` to the same target.

## Security and data

The path contains no key or token. Gradle outputs are generated locally; source files, secrets, APK signing and installed app data are unchanged. Existing ignored OneDrive build folders are left alone.

## Verification

Run direct offline debug assemble without `-I`; inspect `:app:generateDebugBuildConfig` and APK locations. Run the validation script and release build script to check their output contract. Record commands and outcomes in `evidence.md`.
