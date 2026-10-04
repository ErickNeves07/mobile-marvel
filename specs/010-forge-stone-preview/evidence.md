# 010 — Prévia informativa da Forja: Evidence

Status: **implementado e validado em 2026-09-27.**

## Verificações

| Data | Comando/verificação | Resultado |
|---|---|---|
| 2026-09-27 | `scripts/validate-local.ps1 -Target all` | Android `BUILD SUCCESSFUL` (6 testes, 0 falhas, lint, assemble); backend 7 testes, `pip check` limpo.
| 2026-09-27 | APK instalado e UIAutomator na Forja | Forja selecionada; cadeia e seis cartões foram observados; Alma/status alcançados após rolagem; cinco abas seguem na hierarquia.
| 2026-09-27 | AVD, fonte 1.3 e viewport 390×844 dp | Último cartão e estado acessíveis por rolagem; screenshots inspecionados e fonte/resolução restauradas.

Capturas locais, ignoradas pelo Git:

- `android-app/.validation-output/forge-stones.png`
- `android-app/.validation-output/forge-stones-scrolled.png`
- `android-app/.validation-output/forge-stones-font130.png`
- `android-app/.validation-output/forge-stones-compact.png`

## Limitações

Nenhum inventário/quantidade, progresso do jogador, botão de merge, custo ou recompensa foi criado. Q-020–Q-024 continuam bloqueando o merge funcional.
