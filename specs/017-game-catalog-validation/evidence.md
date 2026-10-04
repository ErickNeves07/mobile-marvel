# 017 — Validação defensiva do catálogo de jogo: Evidence

Status: **concluída; fonte e contrato de sucesso preservados.**

## Verificações

| Data | Comando/caso | Resultado |
|---|---|---|
| 2026-09-28 | `scripts/validate-local.ps1 -Target all` | `BUILD SUCCESSFUL`; Android 11 testes unitários, lint e assemble; backend `pip check` limpo e 11 testes aprovados. |
| 2026-09-28 | Parser Android | Catálogo válido parseado; testes rejeitam nome whitespace, IDs duplicados de personagem e variante, e tier desconhecido. |
| 2026-09-28 | Schema/backend | Rejeita strings whitespace e IDs repetidos; API continua igual ao JSON compartilhado com 21 personagens e 105 variantes. |

## Limitações

`org.json:json:20250517` é dependência somente de teste, fora do APK. O catálogo continua sendo um asset estático; atualização remota e sincronização não fazem parte desta spec.
