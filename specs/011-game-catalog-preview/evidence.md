# 011 — Catálogo local do jogo na Coleção: Evidence

Status: **implementada e validada localmente em 2026-09-27.**

## Fonte

- `docs/09-DOMAIN_UX_CATALOG.md`, roster e tabela com cinco variantes por personagem.

## Verificações

| Data | Comando/verificação | Resultado |
|---|---|---|
| 2026-09-27 | `scripts/validate-local.ps1 -Target all` | Android `testDebugUnitTest`, `lintDebug`, `assembleDebug`: `BUILD SUCCESSFUL`, 6 testes/0 falhas. Backend `pip check` limpo; pytest: 8 aprovados. Sem novas dependências. |
| 2026-09-27 | Testes backend `test_game_characters.py` | Fonte compartilhada corresponde ao payload; 21 personagens em quatro grupos, IDs únicos, cinco tiers ordenados por personagem, 105 variantes únicas; conteúdo editorial ausente, Magneto excluído e Xavier incluído. |
| 2026-09-27 | AVD `Medium_Phone_API_36.1`, 1080×2400, fonte 1.3 | Coleção leu o asset do APK e exibiu o último grupo e o cartão Deadpool completo, incluindo `Infinito`, acima da barra fixa. Screenshot: `android-app/.validation-output/collection-game-catalog-font130.png`. |
| 2026-09-27 | AVD, viewport compacto 1024×2216, fonte padrão | Os 21 nomes e 105 variantes foram percorridos na hierarquia; screenshot do último grupo/cartão: `android-app/.validation-output/collection-game-catalog-compact.png`. |
| 2026-09-27 | Revisão da fonte e APK | Mesmo `shared/game_catalog.json` incluído como asset e carregado pelo backend; app offline, sem permissão INTERNET nem dado editorial. |

## Limitações

- A tela apresenta nomes de jogo sem inventário, posse, busca ou detalhes.
- TalkBack was temporarily enabled 2026-09-28; TTS synthesized during navigation. Locale en-US and no acoustic capture leave Portuguese pronunciation unverified.
- A captura compacta já existente contém o cartão Deadpool totalmente visível; a captura de fonte 1.3 foi atualizada ao final da sessão.
- There is no JVM unit test directly for this screen parser/AssetManager; Android asset validation is compile plus AVD/hierarchy inspection.
