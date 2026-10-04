# 029 — Batalha interativa e comparação de variantes — Design

Status: contrato público publicado e observado em 2026-10-04.

## Baseline Android

`GameRules.battle(...)` resolve o combate inteiro em um loop determinístico sem entrada por turno. `MainActivity.renderCampaignState(...)` aciona a simulação no botão da missão e mostra apenas Toast de resultado; portanto, novas decisões exigem estado de cena/turno, persistência ou política de abandono e uma UI dedicada antes de conceder recompensa. `ForgeRepository.completeMission(...)` já vincula recompensa a missão concluída uma única vez.

A Coleção Android contém 21 personagens × cinco nomes de variantes e estado de posse/equipamento. Não há uma tela de comparação nativa nem atributos numéricos por variante no catálogo compartilhado. A comparação deve receber dados authored de jogo com fonte e versão explícitas, mantendo dados editoriais identificados separadamente.

## Referência pública observada em 2026-10-04

Bundles publicados: `batalha-BBpUQYMu.js`, `comparar-Dy7nbkjE.js`, `characters-BL6XEGoz.js`; capturas móveis em `reports/lovable-research/`.

### Matriz da batalha

Intenções em ordem: Estilhaços orbitais (contra Proteger), Escudo polarizado (contra Desestabilizar), Sobrecarga do núcleo (contra Investir), Colapso de Cerebro (contra Desestabilizar). Inicial: chefe 100, equipe 100, carga 20. Dano ao chefe na ação correta/incorreta: Investir 28/19, Proteger 16/9, Desestabilizar 22/13. Dano à equipe: Investir 5/14, Proteger 2/6, Desestabilizar 5/14. Carga: Investir 23/15, Proteger 23/15, Desestabilizar 34/23. HP mínimo chefe/equipe 8/12; carga máximo 100. Na quarta ação a carga chega a 100, e Lança Psíquica zera o chefe e vence. A UI mostra flash, impacto ascendente e anel psíquico no especial, com feedback textual por decisão. Toques durante animação são bloqueados.

O atalho `Pular batalha` da demo marca `battleDone` e avança para recompensa sem combate. Na campanha Android isso mudaria concessão persistida; omitir o atalho nesse fluxo até Q-040. Abandonar descarta sessão sem prêmio. Somente a primeira cena X-Men/Magneto usa a referência; outras campanhas preservam regra atual. `completeMission` permanece idempotente.

Erick esclareceu em 2026-10-04 que o trio fixo da referência era apenas demonstrativo e aprovou derrota com nova tentativa. A tela Android exibe os três membros da equipe salva. A soma dos poderes authored já usados em `GameRules` modifica o dano causado ao chefe (`round(dano_base × poder/31)`) e o dano recebido (`round(dano_base × [1 + max(0, 31−poder)/2])`), mantendo o trio demonstrativo de poder 31 na matriz publicada. Entre os quatro X-Men atuais, o trio mínimo válido soma 29; com quatro respostas erradas de dano alto, ele pode cair antes do especial, mas sobrevive com os quatro contra-ataques corretos. A vida pode chegar a zero; nesse caso, o combate acaba imediatamente, sem especial ou recompensa, e há botão para reiniciar com a equipe salva. Nenhum nome de personagem ausente da equipe aparece no feedback. Esses fatores são regras locais de jogo, não atributos da Comic Vine ou decisão da IA.

### Comparação

Cada lado escolhe livremente entre 21 personagens e cinco variantes, inclusive bloqueadas, como no protótipo. Mudança de personagem reinicia Origem. Atributos base authored em `characters-BL6XEGoz.js` e escala por tier: 1, 1,08, 1,17, 1,28, 1,4; arredondamento JS `Math.round`. Quatro atributos: Vida, Ataque, Defesa e Velocidade. Benchmarks de barras: 1900/650/600/600. A análise compara soma dos quatro, velocidade e defesa; empate usa mensagem de equilíbrio. Dados editoriais são do personagem e não mudam por variante. O Android distinguirá dados locais da API editorial real.

## Segurança e testes

Nenhum segredo no APK. Efeitos visuais devem ter fallback estático; ações devem ter rótulo TalkBack, alvo >= 48dp e respeitar redução de movimento. O estado de combate precisa impedir concessão duplicada de prêmio em retry/reentrada. Testar após a última edição, incluindo upgrade de progresso preexistente caso o esquema SQLite mude.
