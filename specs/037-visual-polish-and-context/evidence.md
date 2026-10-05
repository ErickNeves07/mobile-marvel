# 037 — Evidências

Spec registrada antes das alterações de comportamento.

## Implementação e validação — 2026-10-04

- Android `testDebugUnitTest`, `lintDebug`, `assembleDebug` e `assembleDebugAndroidTest`: sucesso.
- Instrumentação em `emulator-5554`: **32/32** testes passaram após a última edição. Os asserts dependentes do serviço editorial real agora são opcionais via argumento `liveEditorial`; o AVD não alcançou as imagens públicas nesta sessão, enquanto loading/fallback e tela permaneceram testáveis offline.
- Backend `pytest -q`: **57/57** testes passaram; inclui contagem de aparições, primeira publicação, limite de contexto e uso de estado de jogo permitido pelo Deadpool.
- Captura após 450 ms confirmou o diamante azul brilhante entrando no centro da luva: `reports/lovable-research/forge-merge-0.6.0.png` (ignorada pelo Git).
- APK debug 0.6.0: `artifacts/Marvel-Ruptura-Infinita-debug-0.6.0.apk`, 6.334.722 bytes, SHA-256 `E37023573C9C7D00A13EB0251CE2F4F4EE7FF255F43DD49D88A3D1283D8221E9`; application ID, versionCode 6, versionName 0.6.0, minSdk 26 e targetSdk 36 permanecem iguais.
- Limitação de entrega: alterações no contrato Deadpool e nos fatos Comic Vine estão apenas no workspace. O backend Render precisa receber esta revisão antes que o APK use `game_context`, `issue_count` e `first_appearance`. Nenhum push, deploy ou instalação física ocorreu nesta etapa.
