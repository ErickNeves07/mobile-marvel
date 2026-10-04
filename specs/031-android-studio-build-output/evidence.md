# 031 — Android Studio build output: Evidence

2026-10-04, Windows/OneDrive checkout:

- With `RI_VALIDATION_DIR` removed and no `-I`, ran `gradlew.bat --offline :app:testDebugUnitTest :app:lintDebug :app:assembleDebug`: **BUILD SUCCESSFUL**, 50 tasks, 1m 4s. The previously failing `:app:generateDebugBuildConfig` completed.
- Verified generated BuildConfig and `app-debug.apk` under `%LOCALAPPDATA%\RupturaInfinita\gradle-builds\a1cc1bfd\app`; the Gradle problems report was under the matching `root` directory. The existing OneDrive `android-app/app/build` was not removed.
- Ran `powershell -ExecutionPolicy Bypass -File scripts/validate-local.ps1 -Target android`: **BUILD SUCCESSFUL**, 77 tasks, output under `%TEMP%\Marvel-Ruptura-Infinita-validation`.
- Ran `scripts/build-release.ps1` with `ORG_GRADLE_PROJECT_riApiBaseUrl=https://mobile-marvel-8qex.onrender.com`: **BUILD SUCCESSFUL**, lint release passed, zipalign and APK v2 signature verified. The candidate APK remained 5,111,366 bytes, SHA-256 `3D6FFAA8FFE241737B5BFC01C0929E1BB03A9495EDD8D597F7FD49AD0C232ACA`.
- `git diff --check` passed (only Git's CRLF conversion notices).

Android Studio's graphical Run flow and a physical phone were unavailable to this execution. The direct Wrapper command exercises the same project configuration and formerly failing task.
