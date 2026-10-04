# 021 — Android release candidate — Evidence

- Build: `assembleRelease` completed successfully offline on 2026-09-28 (AGP/Gradle and SDK already present).
- Unsigned compile smoke succeeded before creating the release identity; that artifact was not delivered.
- Current signed artifact rebuilt 2026-09-28: `artifacts/Marvel-Ruptura-Infinita-release.apk`, versionName 0.2.0/versionCode 2, 4,840,290 bytes; SHA-256 `8DA29343000CB79B00142A07B80D6CEFC73CBA8CFC36F66BAF3D357D217BED0F`.
- `apksigner verify`: successful with v2; certificate SHA-256 `A83204728D8669E0D497B45917C7F0A8220F3AB83914BB67692F328A340F1374`. `zipalign -c -v 4` passed.
- `aapt dump badging`: application ID `com.erickbarbosa.rupturainfinita`, version 0.2.0/code 2, minSdk 26, targetSdk 36, launch Activity present. Manifest includes INTERNET permission for configurable HTTPS API calls; cleartext traffic remains disabled.
- `scripts/validate-local.ps1 -Target android` passed after signing configuration was added. `scripts/build-release.ps1` passed `testDebugUnitTest`, `lintRelease`, `assembleRelease`, alignment and signature verification.
- Keystore: `%LOCALAPPDATA%\RupturaInfinita\release-signing\ruptura-release.jks`; RSA 3072; alias `ruptura-release`. Password is random and stored DPAPI-encrypted for the current Windows profile in `password.dpapi`; file/directory ACLs are restricted to current user, SYSTEM and Administrators. No secret value was printed. No portable credential backup exists.
- Current functional scope includes local daily challenge/campaign loop, Forge and Gauntlet/variant progression. Android backend client and backend Comic Vine/Groq routes exist, but no deployed URL or Comic Vine key is configured. Groq live smoke returned a sanitized error; provider connectivity/key acceptance is unknown. A fresh release starts with empty inventory; gameplay rewards populate it.
- Debug app UI was rendered and captured in AVD; repository instrumentation passed 10/10. The signed release was not installed on the AVD because its same-ID debug install would need removal. Physical handset installation/UX smoke remains pending.
