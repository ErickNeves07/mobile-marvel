# 009 — Hub local do Nexus: Evidence

Status: **implementado, testado e inspecionado no AVD em 2026-09-27.**

## Verificações

| Data | Comando/verificação | Resultado |
|---|---|---|
| 2026-09-27 | `scripts/validate-local.ps1 -Target all` | Android `BUILD SUCCESSFUL`: 5 unit tests, 0 failures, lint e assemble; backend 7 testes aprovados e pip check limpo.
| 2026-09-27 | Android AVD + UIAutomator | Atalhos abriram Campanhas, Forja, Coleção e Deadpool; estado selecionado da barra acompanhou cada rota.
| 2026-09-27 | Atalho Deadpool sob viewport normal, fonte 1.3 e perfil compacto | Após rolagem, nó focável/clicável abriu Deadpool em todos os perfis; bounds medidos aproximadamente 76 dp (normal), 88 dp (fonte 1.3) e 102 dp (compacto).
| 2026-09-27 | Inspeção da hierarquia da barra no Nexus | Cinco nós de navegação mantêm bounds de 48 dp de altura e o Forja inicial continua confirmado após reinício.

Screenshots locais, ignorados pelo Git:

- `android-app/.validation-output/nexus-shortcuts.png`
- `android-app/.validation-output/nexus-shortcuts-scrolled.png`
- `android-app/.validation-output/nexus-shortcuts-font130.png`
- `android-app/.validation-output/nexus-shortcuts-compact.png`

## Limites

Atalhos só trocam destino. Nexus não mostra atividade, progresso, valores ou recomendações sem fonte aprovada. TalkBack falado não foi ensaiado; descrições de ação e hierarquia foram inspecionadas via UIAutomator.
