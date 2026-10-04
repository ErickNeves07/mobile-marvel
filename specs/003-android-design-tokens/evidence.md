# 003 — Android Design Tokens: Evidence

Status: **implementado e visualmente inspecionado; build, testes e lint concluídos.**

## Verificações

| Data | Comando/verificação | Resultado |
|---|---|---|
| 2026-09-27 | `scripts/validate-local.ps1 -Target all` | Android `testDebugUnitTest`, `lintDebug`, `assembleDebug`: BUILD SUCCESSFUL, 4 testes/0 falhas. Backend `pip check`: sem dependências quebradas; pytest `1 passed`. |
| 2026-09-27 | Resultado lint Android | 0 erros, 4 avisos: versão de AndroidX Core (DEC-016 mantém 1.17.0 por compatibilidade), orientação portrait e API correspondente (escopo aprovado DEC-013), e qualifier `anydpi-v26` do ícone adaptativo (necessário para resolução correta pelo AAPT com minSdk 26). |
| 2026-09-27 | Cálculo WCAG de contraste sRGB | texto principal/fundos 15.42:1–18.20:1; secundário/canvas 10.72:1; muted/canvas 8.07:1; ouro/canvas 12.50:1; ciano/superfícies/status 11.62:1/9.63:1. Pares calculados superam AA 4.5:1. |
| 2026-09-27 | Inspeção AVD e screenshots | Cinco destinos e Forja em perfil compacto e escala 1.3; sem corte, seleção perceptível e artefatos visuais registrados em `android-app/.validation-output/`. |
| 2026-09-27 | Parse dos recursos e resolução de referências | Recursos XML e referências Java compilaram no assemble; vetores, estilos, dimensões, cores e strings resolvidos pelo AAPT. |

## Tokens definidos

Paleta: canvas `#080B13`, surface `#101522`, surface elevada `#171E2D`, seleção `#202C39`, texto `#F4F6FC`, texto secundário `#B6C0D1`, borda `#303B4D`, ouro `#F6C85F`, ciano `#70E1D2`.

Tipografia sans do sistema em escala `sp`; espaçamento em escala 4/8/12/16/24/32/48 dp; alvo interativo mínimo 48 dp. Sem biblioteca, fonte ou imagem de terceiros.

## Limitações

- TalkBack falado não foi ensaiado; nós e descrições foram verificados na hierarquia de acessibilidade.
- Quatro avisos lint permanecem, sem erro de lint. Motivos e decisões de compatibilidade/escopo acima.
