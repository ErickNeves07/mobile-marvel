# 031 — Android Studio build output: Evidence

2026-10-04, Windows/OneDrive checkout:

- With `RI_VALIDATION_DIR` removed and no `-I`, ran `gradlew.bat --offline :app:testDebugUnitTest :app:lintDebug :app:assembleDebug`: **BUILD SUCCESSFUL**, 50 tasks, 1m 4s. The previously failing `:app:generateDebugBuildConfig` completed.
- Verified generated BuildConfig and `app-debug.apk` under `%LOCALAPPDATA%\RupturaInfinita\gradle-builds\a1cc1bfd\app`; the Gradle problems report was under the matching `root` directory. The existing OneDrive `android-app/app/build` was not removed.
- Ran `powershell -ExecutionPolicy Bypass -File scripts/validate-local.ps1 -Target android`: **BUILD SUCCESSFUL**, 77 tasks, output under `%TEMP%\Marvel-Ruptura-Infinita-validation`.
- Ran `scripts/build-release.ps1` with `ORG_GRADLE_PROJECT_riApiBaseUrl=https://mobile-marvel-8qex.onrender.com`: **BUILD SUCCESSFUL**, lint release passed, zipalign and APK v2 signature verified. The candidate APK remained 5,111,366 bytes, SHA-256 `3D6FFAA8FFE241737B5BFC01C0929E1BB03A9495EDD8D597F7FD49AD0C232ACA`.
- `git diff --check` passed (only Git's CRLF conversion notices).

At the initial build check, Android Studio's graphical Run flow and a physical phone were unavailable. The direct Wrapper command exercises the same project configuration and formerly failing task; the phone became available for the follow-up below.

## Follow-up on the connected phone

- Erick saw the old text “Fundação Android pronta para a próxima ruptura” after building. The connected phone was running `com.erickbarbosa.rupturainfinita` version `0.1.0` (`versionCode` 1); that text is absent from current source.
- The installed APK certificate SHA-256 `9F43E3D745294769DDBD6D3588B0DB6064F96A2414DC1A74B7525D30CC836C64` matched this machine's Android debug keystore. The release certificate differed, so the safe in-place update was a debug APK.
- Built `0.2.0` (`versionCode` 2) with `-PriApiBaseUrl=https://mobile-marvel-8qex.onrender.com`, verified package version, certificate and `BuildConfig.API_BASE_URL`, then ran `adb install -r`: **Success**. `dumpsys package` confirmed installed `0.2.0`.
- Launched `MainActivity`; UIAutomator observed the new Collection screen with `0/105`, group/tier filters, character card and five navigation items. The old text was absent. No fatal AndroidRuntime entry appeared in the recent logcat sample.
- This confirms installation and a rendered screen on the physical phone. A full physical gameplay pass and image/network check remain pending.
