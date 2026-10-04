# 004 — Local Validation Runner: Evidence

Status: **implementado e executado localmente sem instalação ou acesso de rede.**

## Verificações

| Data | Comando | Resultado |
|---|---|---|
| 2026-09-27 | Parser PowerShell / `Get-Help` | Sem erro de parse; ajuda consultável. |
| 2026-09-27 | `scripts/validate-local.ps1 -Target all` | `BUILD SUCCESSFUL`; Android unit tests, lint e assemble; backend `pip check` limpo e pytest `1 passed`. |
| 2026-09-27 | `scripts/validate-local.ps1 -Target android` | `BUILD SUCCESSFUL`; apenas frente Android executada. |
| 2026-09-27 | Comparação das variáveis do processo antes/depois de `-Target android` | JAVA_HOME, ANDROID_HOME, GRADLE_USER_HOME e RI_VALIDATION_DIR restaurados exatamente aos valores prévios (inclusive unset). |
| 2026-09-27 | Execuções anteriores dos preflights | Mensagens identificaram SDK/cache/venv inacessíveis no contexto restrito; os mesmos toolchains locais foram posteriormente verificados com acesso autorizado, sem instalação. |

## Limitações

- A saída do PowerShell exibiu caracteres acentuados com encoding incorreto no terminal desta sessão; os comandos concluíram com exit code zero. O comportamento visual em outros hosts PowerShell ainda pode variar por code page.
- Build reporta aviso de features Gradle depreciadas para Gradle 9, além dos quatro avisos lint documentados nas specs 002/003. Nenhum erro de build/lint/teste.
