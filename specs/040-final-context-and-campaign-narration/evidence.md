# 040 — Evidências

- `backend/.venv/Scripts/python.exe -m pytest -q`: **58/58**.
- Gradle `testDebugUnitTest lintDebug assembleDebug assembleDebugAndroidTest --offline`: sucesso após a última edição de código e versão.
- `scripts/run-android-instrumentation.ps1 -Serial emulator-5554`: **34/34**; cobre banco, campanha, equipe, coleção, variantes, forja, desafio diário, navegação e contexto do Deadpool.
- `scripts/build-release.ps1`: `testDebugUnitTest lintRelease assembleRelease`, zipalign e APK Signature Scheme v2 passaram. Release instalada no AVD, `versionCode=9`, `versionName=0.9.0`; processo vivo sem fatal exception.
- Testes novos: `DeadpoolPromptTest`, `LovableScreensTest.deadpoolContextTracksSelectedMissionAndSavedTeam`, `LovableBattleTest.everyCampaignHasSixOpponentSpecificWarnings`, `test_deadpool_can_comment_on_thanos_and_current_team_without_inventing_game_rules`.
- Render GET `/ready`: ok e integrações configuradas; GET `/openapi.json`: contrato legado sem `game_context`. POST real rejeitado por revisão automática por possível cota paga; não foi executado. Telefone físico desconectado.
- APKs, tamanhos e SHA-256 em `RELEASE_NOTES.md`.
- Commit `0829bd6` enviado para `origin/main`; Render permaneceu no contrato legado na verificação subsequente. Falta publicação manual do backend pelo painel.
- 2026-10-05: debug 0.9.0 instalado no telefone `C6OFVWYD4DZTBA5H` sobre 0.8.0; `dumpsys` confirmou versionCode 9, MainActivity abriu, `pidof` ativo e log sem fatal. GET do Render ainda mostrou contrato legado.
