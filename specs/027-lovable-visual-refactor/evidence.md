# 027 — Refatoração visual baseada no protótipo Lovable — Evidence

Status: **implementação parcial; aceitação visual e 105 artes pendentes**.

## Verificação inicial — 2026-10-02

| Ação | Resultado |
|---|---|
| Leitura de README, memória, visão, requisitos, arquitetura, workflow e spec ativa 026 | concluída |
| Leitura de `MainActivity`, tokens, layout e `AppDestination` | UI nativa atual mapeada; cinco destinos e Forja inicial |
| Acesso à URL pública com `curl.exe -L -I` | HTTP 200 |
| Leitura do HTML, CSS e módulos JavaScript publicados | tokens, fontes, rotas e elementos visuais identificados |
| Link do editor localizado no HTML publicado | projeto `lovp_252yras1v388ht85hfar7wgg1c`; acesso sem sessão entregou apenas shell genérico, sem arquivos-fonte |
| Inspeção do roteador compilado | 19 caminhos publicados; mapeamento com cinco destinos nativos no `design.md` |
| Inspeção de `CharacterArt` | silhueta SVG única no primeiro plano; substituir por retrato/arte reconhecível mantendo enquadramento e efeitos de fundo |
| Revisão dos termos Comic Vine | [API oficial](https://comicvine.gamespot.com/api/) permite catálogo editorial não comercial com crédito e restringe redistribuição; imagens da API não serão copiadas como assets estáticos do jogo |
| Captura renderizada da URL pública | Chrome headless com CDP emulou viewport móvel 390 × 844 e capturou rotas principais/subfluxos; editor privado não foi necessário |

Os recursos baixados para pesquisa estão em `reports/lovable-research/`, pasta ignorada pelo Git. Não são fonte editável do projeto Lovable, não foram incorporados ao APK e não contêm credenciais observadas. A publicação pública permite analisar estilo e estrutura, mas não substitui a revisão visual de telas renderizadas nem o projeto original.

## Verificação automatizada — 2026-10-02

- `scripts/validate-local.ps1 -Target android`: passou após a última edição Java/XML; inclui testes JVM, compilação do APK instrumentado, lint e debug APK.
- `scripts/run-android-instrumentation.ps1`: **13/13** testes instrumentados passaram em 2026-10-04 após a última mudança estrutural da Coleção e a reversão do modo imersivo. Cobrem repositórios/regras, não navegação visual. O AVD apresentou ANR recorrente do System UI durante capturas de abertura, inclusive após boot limpo. `scripts/validate-local.ps1 -Target android` passou após a última edição Java/XML (JVM, compilação de instrumentação, lint e APK debug).
- A chave Comic Vine foi importada para `.env` ignorado pelo Git e `/ready` confirmou somente presença booleana de Comic Vine/Groq; nenhum valor de segredo foi registrado ou empacotado no APK.

## Verificação manual e screenshots

- `intro-avd.png`: abertura com linha central, três linhas de título, seis cores e ação de entrada.
- `nexus-avd.png`: hero do Nexus com gradientes, estrelas e anéis, barra inferior e prévia da Manopla com contagem real.
- `forge-avd.png` e `forge-open-avd.png`: painel dos seis encaixes e inventário lidos do estado real; fusões 3:1 continuam acessíveis no painel expansível.
- `deadpool-avd.png`: página clara, cartões de quadrinhos, campo e botão de chamada, antes das últimas alterações no Nexus/Forja (Deadpool não foi modificado depois dessa captura).
- `campaigns-avd.png` e `campaign-open-avd.png`: mapa em trilha de duas campanhas reais (X-Men/Quarteto), abertura do cartão e controles de equipe/missão preservados. A lista Lovable tem mais capítulos e dados de demonstração; estes não foram copiados como progresso/regras.
- `collection-avd.png`: contador real 0/105, busca e filtros horizontais sobre o catálogo local. A busca por texto não correspondente ocultou os cartões no AVD; entrada positiva não foi validada por instabilidade de digitação do emulador. A lista vertical e a ausência de artes ainda divergem da grade Lovable.
- `page-*.html` e bundles públicos em `reports/lovable-research/` permitiram inspecionar DOM, classes e textos. `capture.mjs` na mesma pasta gerou capturas renderizadas 390 × 844 do site público: Nexus, Forja, Campanhas, Coleção, Deadpool, Recompensa, abertura, desafio, Manopla, Câmara e Configurações. Os arquivos de pesquisa estão ignorados pelo Git e não foram incorporados ao APK. O fonte editável Lovable continua inacessível.
- Comparação visual inicial: o Android preserva hierarquia de cor e títulos, mas ainda diverge em telas internas, cartões compactos, inventário em grade e Coleção com retratos/grade. O viewport web ocupa a altura inteira; uma tentativa de modo imersivo Android compilou, mas o AVD apresentou ANR do Pixel Launcher/System UI na captura e após reinício. O ajuste foi revertido; a diferença de bordas continua pendente. Capturas inválidas do launcher/alerta foram removidas.

## Revisão por requisito

Atualização 2026-10-04: a tela própria da Manopla foi desenhada em Android Canvas com três anéis, seis encaixes hexagonais coloridos, painel central, ressonância e linhas individuais. A referência `/manopla` publicada e a captura instrumentada Android foram comparadas em `reports/lovable-research/lovable-manopla-mobile.png` e `android-gauntlet.png` (ignoradas pelo Git). A composição principal acompanha a referência, mas mostra `0/6` e `0%` porque o inventário do teste está vazio, enquanto a demo Lovable exibe `4/6` e `92%`. O estado Android vem do SQLite real; a ativação segue exigindo seis Joias Completas sem consumo. `validate-local.ps1 -Target android` e instrumentação **16/16** passaram após as últimas alterações de produção. A captura não substitui inspeção manual em dispositivo, impedida pelo ANR do System UI do AVD.

| Requisito/AC | Estado | Evidência |
|---|---|---|
| AC-01/02 | parcial | abertura, navegação, Nexus, Campanhas, Forja, Coleção e Deadpool começaram; capturas AVD acima, sem paridade lado a lado |
| AC-03 | parcial | 13 testes instrumentados passaram; regras e recompensas mantidas; Q-039/Q-040 abertas |
| AC-04 | pendente | lint passou; fonte ampliada, TalkBack e contraste da nova UI ainda não revistos |
| AC-05 | em progresso | capturas públicas Lovable 390 × 844 obtidas; comparação lado a lado e correções ainda pendentes |
| AC-06 | bloqueado | fontes Barlow Condensed/Sora com licenças OFL adicionadas; 105 artes ainda ausentes, Q-041 |

### Procedência das fontes incorporadas

Arquivos oficiais [Barlow Condensed](https://github.com/google/fonts/tree/main/ofl/barlowcondensed) e [Sora](https://github.com/google/fonts/tree/main/ofl/sora), sob SIL Open Font License 1.1, com cópias em `android-app/licenses/`. SHA-256: Barlow Regular `583CEC5DA3B84BC4DC7C9C72E2A565C94D34E431518B19D7E250B7830AD5F996`; Bold `E476562EC9C1E16CF16475895B511F08C804F438CC9A9F80A44EA50A0EEB5B65`; ExtraBold `724C9C25952D5F4A2D87185D9767AA006144C5F0D944DC05BF7D5D603551C260`; Sora variável `84FF7096AE3EC6C8BE47D906D1A0BA4DE7F2CE78C615275C77301964A316E16C`.

## Limitações e riscos residuais

Atualização 2026-10-04: o plano de 105 artes exclusivas foi substituído por decisão de Erick: retratos Comic Vine temporários por personagem, repetidos nas variantes. Render e APK online passaram no AVD; imagens e créditos reais estão em `specs/030-comic-vine-character-art/collection-portraits-live.png` e `compare-variant-live.png`. Comparação com `reports/lovable-research/lovable-colecao-mobile.png`: o retrato melhora o card, mas a Coleção Android ainda é uma lista longa; o Lovable mostra dois trilhos de filtros, card destacado e grade de duas colunas. Esta diferença visual permanece em T06, sem alterar as regras de jogo.

- A URL publicada não concede acesso ao fonte editável nem a uma sessão Lovable autenticada; capturas renderizadas públicas foram obtidas.
- A abertura e navegação foram aprovadas por Erick; regras e recompensas divergentes permanecem Q-039/Q-040.
- A geração integrada de arte retornou `moderation_blocked` em quatro tentativas. Q-041 define a dependência de uma alternativa viável para 105 retratos.
