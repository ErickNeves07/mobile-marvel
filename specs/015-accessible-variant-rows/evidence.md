# 015 — Variantes como linhas acessíveis na Coleção: Evidence

Status: **implementada e validada em 2026-09-28.**

| Data | Verificação | Resultado |
|---|---|---|
| 2026-09-28 | `scripts/validate-local.ps1 -Target android` | `BUILD SUCCESSFUL`; testes unitários, lint e assemble passaram; sem dependência nova. |
| 2026-09-28 | UIAutomator/AVD, viewport padrão | Agregação em 17 posições roladas encontrou 21 nomes de personagens, 105 textos tier individuais e `Infinito: Deadpool — Não Canônico`; a ordem visual começa Origem e termina Infinito. |
| 2026-09-28 | AVD fonte 1.3 e viewport 1024×2216 | Capturas de fim de lista mostram cada linha legível e cartão Deadpool completo acima da barra fixa: `collection-a11y-variants-font130.png`, `collection-a11y-variants-compact.png`. |
| 2026-09-28 | AVD fonte padrão | Captura inicial mostra cabeçalho editorial, grupo Vingadores e cinco linhas independentes do Homem de Ferro: `collection-a11y-variants-standard.png`. |
| 2026-09-28 | Configuração AVD | TalkBack continua desligado, font_scale 1.0 e resolução 1080×2400 depois da inspeção. |

## Limitações

- Não foi feita captura acústica de cada variante; geração TTS e semântica geral do shell têm evidência separada na spec 002.
