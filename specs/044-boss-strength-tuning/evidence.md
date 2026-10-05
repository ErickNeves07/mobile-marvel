# 044 - Evidence

Status: implementacao local concluida; aguardando push e deploy.

## Auditoria inicial

- `LovableBattle` deriva HP do chefe por `150 + 26*difficulty + round(avgAttack * 0.55)`.
- Dano base recebido deriva de `8 + round(difficulty * 1.55) - defense/52`, seguido pelos modificadores de ação/contra.
- Recompensas estão separadas em `CampaignReward`/`ForgeRepository`.

## Após implementação

Preencher tabelas de valores e resultado dos testes depois da última edição.

## Resultado apos implementacao

- Implementado `BossBalance`: HP maximo arredondado para cima em +6%; dano final mitigado arredondado para cima em +8%, minimo 2. Padr?es, danos de ataque do jogador, recompensas e gates de progressao nao foram alterados.
- `LovableBattleTest` verifica os nove chefes, HP monotonicamente crescente e dano por tipo de contra-ataque. Validacao: testes JVM Android, lint e assemble passaram em 2026-10-05.
