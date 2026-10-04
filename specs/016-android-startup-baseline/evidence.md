# 016 — Baseline de inicialização Android: Evidence

Status: **concluída; medições descritivas do build local, sem SLO.**

Comando por amostra: `adb shell am force-stop com.erickbarbosa.rupturainfinita` seguido por `adb shell am start -W -n com.erickbarbosa.rupturainfinita/.MainActivity`. Cada resposta indicou `Status: ok`, `LaunchState: COLD` e `Activity: .../.MainActivity`.

| Amostra fria | ThisTime (ms) | TotalTime (ms) | WaitTime (ms) |
|---:|---:|---:|---:|
| 1 | N/R | 1664 | 1686 |
| 2 | N/R | 4510 | 4544 |
| 3 | N/R | 3228 | 3378 |
| 4 | N/R | 3505 | 3605 |
| 5 | N/R | 2108 | 2188 |
| Mínimo | N/R | 1664 | 1686 |
| Mediana | N/R | 3228 | 3378 |
| Máximo | N/R | 4510 | 4544 |

## Ambiente e limitações

- AVD `Medium_Phone_API_36.1`, app `com.erickbarbosa.rupturainfinita`, build debug local já instalado.
- Tela física 1080×2400, densidade 420 dpi, escala de fonte 1.0, acessibilidade do sistema desativada.
- `am start -W` desta imagem Android omite `ThisTime`; por isso consta N/R, não zero.
- `TotalTime`/`WaitTime` são tempos do ActivityTaskManager e não medem isoladamente first frame, hardware físico ou experiência percebida. A variação no emulador é descritiva; nenhum limite de performance foi inferido.
- Sem mudança de código/dependência e sem acesso de rede. Ao final, o AVD foi restaurado para Forja selecionada e configurações padrão de tela/fonte/acessibilidade.