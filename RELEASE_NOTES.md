# Release 0.2.0 — candidato de avaliação (2026-10-04)

O APK assinado em `artifacts/Marvel-Ruptura-Infinita-release.apk` foi recompilado após os ajustes de Batalha, Comparação, Manopla, fusão 2:1, recompensas e retratos editoriais. Tem **5.107.462 bytes**, SHA-256 `9382C92F2228F700FB52EA09DD6A46924F5CC67583CAC62D08BEAE3290F6DAA5`; `zipalign` e `apksigner` confirmaram assinatura v2. Build JVM, lint debug/release, `assembleRelease`, **22/22** testes instrumentados Android e **45/45** testes backend passaram. A comparação permite escolher uma variante em cada lado. A primeira missão X-Men apresenta quatro decisões, efeitos e especial; a equipe salva altera o resultado, pode perder e tentar novamente sem prêmio. A Manopla apresenta seis encaixes e progresso derivados do inventário real. Fusões novas consomem duas peças; fusões antigas não são compensadas. Cada primeira vitória futura de campanha concede quatro Fragmentos de uma Joia própria, créditos e XP conforme a tabela aprovada na spec 028. A primeira vitória diária concede um Fragmento da Joia determinística do dia. Nenhuma vitória anterior recebe ajuste retroativo; os saldos aparecem no Nexus.

**Limite de entrega:** este build ainda não reproduz integralmente o protótipo Lovable. Os 21 retratos Comic Vine foram verificados e ligados às 105 variantes, repetindo o retrato do personagem conforme decisão de Erick; Coleção/Forja/subfluxos seguem parcialmente divergentes. Sem host HTTPS público, este APK mostra fallback editorial offline, não os retratos reais. Créditos e XP persistem, mas seus usos futuros não foram definidos. A chave Comic Vine está apenas no `.env` local ignorado pelo Git. O teste instrumentado exercitou comparação, vitória, derrota/retry, Manopla, recompensa e fallback de retratos programaticamente; o smoke manual foi bloqueado por ANR recorrente do System UI do AVD. Este APK é candidato de avaliação e precisará ser reconstruído com `-PriApiBaseUrl` após o deploy Render.

## Histórico do build anterior (2026-09-28)

`artifacts/Marvel-Ruptura-Infinita-release.apk` was rebuilt from the current source and signed with the existing local release key. This is a signed test build for local/device evaluation; Comic Vine live access and a public backend host are not configured, so online editorial/provider features have not been validated end to end.

- Application ID: `com.erickbarbosa.rupturainfinita`
- versionCode: 2; versionName: 0.2.0
- Size: 4,840,290 bytes
- SHA-256: `8DA29343000CB79B00142A07B80D6CEFC73CBA8CFC36F66BAF3D357D217BED0F`
- APK Signature Scheme v2: verified.
- `aapt dump badging`: versionName 0.2.0, versionCode 2, minSdk 26, targetSdk 36.
- Certificate SHA-256: `A83204728D8669E0D497B45917C7F0A8220F3AB83914BB67692F328A340F1374`

The build includes daily challenge, local campaigns/battles/rewards, Forge persistence, Gauntlet activation and per-character variant progression/equipment, plus configurable HTTPS API integration. Progression and gameplay remain local SQLite. Gauntlet requires one complete copy of all six Stones without consuming them; each next variant tier requires but does not consume its mapped complete Stone.

## Verification

- `scripts/validate-local.ps1 -Target android`: JVM tests, Android test APK compilation, lint, and debug APK assemble passed.
- `scripts/run-android-instrumentation.ps1`: 10/10 repository tests passed on the connected AVD, including v3→v4 migration, Gauntlet gate/non-consumption, and sequential/idempotent variant unlock.
- `backend/.venv/Scripts/python.exe -m pytest -q`: 39 tests passed; `pip check` reported no broken requirements.
- `scripts/build-release.ps1`: release build/lint passed and `apksigner` verified v2 signing.
- AVD screenshots of Forge Gauntlet requirements and pre-activation locked variants are in `specs/026-gauntlet-variant-progression/`.

## Remaining limits

- No Render deployment or public HTTPS URL exists in this environment. `COMIC_VINE_API_KEY` and Render access are absent; Groq live smoke returned a sanitized error, so provider connectivity/key acceptance remains unknown.
- No physical handset test was performed. The AVD passed repository instrumentation and the app rendered Forge/Collection screenshots.
- Preserve the signing keystore at `%LOCALAPPDATA%\RupturaInfinita\release-signing`; its DPAPI-protected password is tied to this Windows user profile. A portable backup is not yet validated.
