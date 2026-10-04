# 028 — Economia e recompensas do protótipo Lovable — Design

## Forja

`ForgePolicy.INPUT_COUNT=2` já aplica a receita nova. `applied_merges` mantém retries idempotentes, inclusive IDs antigos. Não houve compensação de operações 3:1 anteriores.

## Recompensas de campanha aprovadas

| Campanha | Missão | Joia dos 4 Fragmentos | Créditos | XP |
|---|---|---|---:|---:|
| X-Men | 1 Magneto | Mente | 3.000 | 840 |
| X-Men | 2 Sentinelas | Espaço | 3.300 | 920 |
| X-Men | 3 Genosha | Tempo | 3.800 | 1.100 |
| Quarteto | 1 Robôs de Latveria | Poder | 3.100 | 860 |
| Quarteto | 2 Cerco de Destino | Realidade | 3.500 | 1.000 |
| Quarteto | 3 Trono de Latveria | Alma | 4.000 | 1.200 |

Uma tabela authored imutável `CampaignReward` é indexada por `campaignId`/missão. O repositório recebe apenas esse par e obtém o pacote, sem aceitar uma Joia arbitrária da UI. A primeira vitória insere `completed_missions` e `applied_rewards`, acrescenta quatro ao estoque da etapa `FRAGMENT`, credita moedas e XP e desbloqueia a missão seguinte numa única transação. Antes de escrever, checa o limite de 999 do inventário e overflow dos saldos. Retry encontra a missão concluída e retorna `false`. Uma derrota não chama o repositório. A tela de vitória mostra o pacote aprovado para a missão e os três itens; `Homem-Aranha +1` não aparece.

Banco v5: criar `player_resources(singleton_id=1, credits INTEGER NOT NULL, xp INTEGER NOT NULL)` inicializado com zero. A migração 4→5 não toca `completed_missions`, `applied_rewards` ou inventário; portanto missões antigas não são pagas novamente. Creditar saldos só na nova transação de conclusão. Os saldos aparecem no Nexus; seus usos futuros dependem de regra própria.

Desafio diário: `submitGuess` preserva a Joia escolhida por hash da data local e o ledger `daily:<date>`, mas a vitória concede exatamente um item em `FRAGMENT`. O evento e o inventário mudam na mesma transação; vitória repetida e data antiga concluída não são reprocessadas. Verificar cap 999 antes da escrita. Não há migração retroativa do desafio anterior.

Dados de economia são do jogo. Não incluir segredos no APK, usar Comic Vine para atributos ou deixar a IA conceder prêmios. Instrumentação cobre as seis linhas, retry, persistência, bloqueio sequencial e migração v4→v5 com missão antiga.
