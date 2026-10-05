# 038 — Evidências

## Verificação automatizada

| Data | Comando | Resultado |
|---|---|---|
| 2026-10-04 | `testDebugUnitTest lintDebug assembleDebug assembleDebugAndroidTest --offline` | Passou |
| 2026-10-04 | `scripts/run-android-instrumentation.ps1 -Serial emulator-5554` | **33/33** testes passaram |
| 2026-10-04 | `aapt dump badging artifacts/Marvel-Ruptura-Infinita-debug-0.7.0.apk` | versionCode 7/versionName 0.7.0, minSdk 26/targetSdk 36 |

## Verificação manual

- Cold start capturado em `reports/lovable-research/app-splash-0.7.0.png`: fenda vetorial em órbita, seis gemas e núcleo pulsante.
- A instrumentação confirmou o tema Starting, o ícone animado e a chegada à introdução atual.
- Instalação limpa no telefone `C6OFVWYD4DZTBA5H`; Android confirmou versionCode 7/versionName 0.7.0 e processo ativo sem fatal exception.

## Revisão por requisito

| Requisito/AC | Estado | Evidência |
|---|---|---|
| AC-01 | Aprovado | Tema Starting e ícone animado confirmados pelo teste instrumentado |
| AC-02 | Aprovado | MainActivity alcança a introdução existente; 33 testes passam |
| AC-03 | Aprovado | Saída 220 ms; remove sem animação quando o sistema desativa animadores |
| AC-04 | Aprovado | Sem rede e sem mudanças de dados/onboarding |
| AC-05 | Aprovado | Unit, lint, build e 33 testes AVD |

## Limitações e riscos residuais

- AndroidX core-splashscreen 1.2.0 foi obtida do Maven Google e está fixada sem curinga.
- Splash inspecionada no AVD; app 0.7.0 instalado no telefone físico após desinstalação limpa autorizada pelo usuário.
