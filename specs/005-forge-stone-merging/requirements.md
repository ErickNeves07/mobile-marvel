# 005 — Forja: Merge das Joias do Infinito

Status: **implementada e validada localmente; integração com campanhas/desafios aguarda esses fluxos.**

## Objetivo

Permitir que o jogador combine recursos da mesma Joia na Forja com progresso local persistente, determinístico e sem perda/duplicação acidental.

## Requisitos relacionados

- R-USER-004: cadeia Estilhaço → Fragmento → Núcleo Instável → Joia Completa.
- R-USER-005: as seis Joias completas ocupam encaixes separados.
- R-USER-006: Manopla de Contenção abre a Câmara de Variantes após progresso requerido pelos docs.
- RNF-003, RNF-006, RNF-007.

## Escopo

- Inventário inicial vazio, isolado por `(Joia, estágio)`.
- Receita uniforme 3:1 para cada transição e cada Joia.
- Operações locais atômicas e persistidas após toda ação concluída.
- Limite inclusivo de 999 itens por combinação `(Joia, estágio)`.
- Recompensas determinísticas de Estilhaços acionadas por conclusão de desafio diário ou campanha; nenhuma decisão de recompensa vem da IA.
- Forja exibe progresso e exige confirmação explícita antes de cada merge irreversível.

## Fora de escopo

- Compras, moeda premium, energia, loot boxes ou qualquer serviço externo.
- Entregar recompensas antes que um fluxo real de conclusão de campanha/desafio invoque o contrato local de recompensa.
- IA, Comic Vine ou assets externos como fonte de regras.

## Requisitos

- **FORGE-R-001:** cada Joia mantém sua própria contagem para Shard, Fragment, Unstable Core e Complete.
- **FORGE-R-002:** cada merge consome 3 itens de um estágio e produz 1 do imediatamente seguinte.
- **FORGE-R-003:** entradas devem ser da mesma Joia e estágio.
- **FORGE-R-004:** Complete é terminal e não pode ser consumida.
- **FORGE-R-005:** resultado e recompensas são regras determinísticas do jogo, nunca definidos pela IA.
- **FORGE-R-006:** merge é irreversível; a interface apresenta uma prévia e uma confirmação antes da alteração.
- **FORGE-R-007:** cada ação persistida é atômica; em falha, o estado anterior permanece íntegro.
- **FORGE-R-008:** qualquer contagem persistida fica entre 0 e 999 por `(Joia, estágio)`; recompensa que ultrapasse o limite é rejeitada sem alteração.
- **FORGE-R-009:** inventário novo contém zero de todos os itens.
- **FORGE-R-010:** evento repetido de recompensa com o mesmo ID não concede itens novamente.

## Política determinística de recompensas

- Conclusão de desafio diário elegível concede exatamente 3 Shards da Joia indicada no registro local desse desafio.
- Conclusão de campanha elegível concede exatamente 3 Shards da Joia indicada no registro local dessa campanha.
- Joia e identificador de conclusão pertencem aos dados/evento de jogo e são validados localmente. A IA não escolhe nem altera esses campos.
- Enquanto Campanhas e Desafios não tiverem fluxos de conclusão reais, nenhum item é concedido por abrir ou visualizar suas prévias.

## Critérios de aceite

- [x] AC-01 — inventário inicial zero e seis Joias isoladas.
- [x] AC-02 — as três transições usam exatamente 3:1.
- [x] AC-03 — merge insuficiente, terminal ou acima do cap não altera o inventário; itens de estágios diferentes não podem compor a entrada.
- [x] AC-04 — saída é confirmada pelo usuário; cancelamento não altera estado.
- [x] AC-05 — consumo e produção são atômicos, duráveis e não duplicam em repetição.
- [x] AC-06 — contrato local concede 3 Shards por evento válido e deduplica; nenhum placeholder emite conclusão.
- [x] AC-07 — UI identifica Joia, estágio, contagens, custo, resultado, irreversibilidade e limite.
- [x] AC-08 — testes cobrem limites, transações, persistência, recompensa e apresentação acessível.
- [x] AC-09 — nenhuma dependência de Comic Vine, LLM ou imagens é necessária para regras.

## Decisões registradas

DEC-042 e DEC-043 em `memory/DECISIONS.md`; Q-020 a Q-024 estão resolvidas em `memory/OPEN_QUESTIONS.md`.
