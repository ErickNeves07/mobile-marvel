# 014 — Superfície em linguagem de quadrinhos para Deadpool: Evidence

Status: **implementada e validada em 2026-09-28.**

| Data | Verificação | Resultado |
|---|---|---|
| 2026-09-28 | `scripts/validate-local.ps1 -Target android` | Build/assemble/lint passaram; 6 testes Android/0 falhas, sem dependência nova. |
| 2026-09-28 | AVD 1080×2400, fonte padrão | Deadpool selecionado; detalhe angular ciano/ouro está no topo direito do hero; cópia/status mantidos. Screenshot `deadpool-comic-standard.png`. |
| 2026-09-28 | AVD, fonte 1.3 | Texto do hero e barra legíveis; quadro segue fora da descrição. Screenshot `deadpool-comic-font130.png`. |
| 2026-09-28 | AVD 1024×2216 compacto | Hero, placeholder e seleção permanecem visíveis. Screenshot `deadpool-comic-compact.png`. |
| 2026-09-28 | Destino controle Forja | Forja mantém superfície padrão; UIAutomator confirma `Forja, selecionado`. Screenshot `deadpool-comic-forge-control.png`. |
| 2026-09-28 | Hierarquia UIAutomator | Fundo é o drawable do cartão existente; nenhuma view textual/clicável/focável nova foi criada. |

## Limitações

- A decoração não substitui a integração de IA/narrativa real; conteúdo continua placeholder conforme spec 002/DEC-020.
- Otimização comum aos drawables registrada em 2026-09-28 e validada na matriz final: shader reutilizado por bounds e nenhuma camada offscreen no caso opaco.
