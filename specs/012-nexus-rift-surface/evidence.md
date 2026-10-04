# 012 — Assinatura visual de ruptura no Nexus: Evidence

Status: **implementada e validada em 2026-09-28.**

## Fonte

- Specs 003 e 009; DEC-020/DEC-024; restrições RNF-003.

## Verificações

| Data | Comando/verificação | Resultado |
|---|---|---|
| 2026-09-28 | `scripts/validate-local.ps1 -Target android` | `testDebugUnitTest`, `lintDebug` e `assembleDebug` passaram; `BUILD SUCCESSFUL`, 6 testes/0 falhas. Sem dependência nova. |
| 2026-09-28 | AVD 1080×2400, escala padrão | Nexus selecionado; arco e filete confinados ao topo direito do hero, sem cruzar descrição/status. Captura `android-app/.validation-output/nexus-rift-standard.png`. |
| 2026-09-28 | AVD, fonte 1.3 | Texto/status e barra seguem visíveis; geometria não interfere na leitura. Captura `android-app/.validation-output/nexus-rift-font130.png`. |
| 2026-09-28 | AVD 1024×2216, fonte padrão | Composição se ajusta ao cartão compacto e mantém conteúdo/barra legíveis. Captura `android-app/.validation-output/nexus-rift-compact.png`. |
| 2026-09-28 | Destino controle Forja | Hierarquia confirma `Forja, selecionado`; hero conserva superfície atual sem motivo de ruptura. Captura `android-app/.validation-output/nexus-rift-forge-control.png`. |
| 2026-09-28 | Acessibilidade da camada | Drawable é apenas background do cartão existente; não cria nó/evento e não recebe foco. Estado e contentDescription do Nexus mantidos. |

## Limitações

- Teste visual realizado no AVD instalado; não há instrumentação de screenshots automatizada.
- Motivo intencionalmente estático e de baixo contraste; nenhuma animação foi adicionada.
- Revisão de performance em 2026-09-28: shader passou a ser reutilizado enquanto os bounds não mudam; camada alpha offscreen só é usada se alpha < 255. Matriz conjunta Android/backend passou depois da otimização.
