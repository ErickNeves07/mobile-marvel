# 013 — Acentos visuais nos cartões de campanha: Evidence

Status: **implementada e validada em 2026-09-28.**

| Data | Verificação | Resultado |
|---|---|---|
| 2026-09-28 | `scripts/validate-local.ps1 -Target android` | Build/assemble/lint passaram; 6 testes Android, 0 falhas. Nenhuma dependência nova. |
| 2026-09-28 | AVD 1080×2400, fonte padrão | X-Men mostra filete gold; Quarteto, cyan. Textos, chefe Magneto, status em desenvolvimento e barra permanecem iguais. Screenshot `campaign-accent-standard.png`. |
| 2026-09-28 | AVD, fonte 1.3, rolagem até o fim | Ambos os cartões/status aparecem completos; o segundo fica acessível por rolagem acima da barra fixa. Screenshot `campaign-accent-font130.png`. |
| 2026-09-28 | AVD 1024×2216 compacto, rolagem até o fim | Os dois cartões/status aparecem inteiros e sem corte. Screenshot `campaign-accent-compact.png`. |
| 2026-09-28 | Hierarquia UIAutomator | Seleção da barra continua `Campanhas, selecionado`; linha decorativa não tem texto, descrição, foco ou clique. |

## Limitações

- Filete é somente diferenciação visual, sem significado semântico, ação ou estado de campanha.
