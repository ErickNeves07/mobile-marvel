# 044 - Design

## Decisão delegada

Erick pediu apenas um aumento pequeno. Aplicar `ceil(HP_atual * 1.06)` e `ceil(dano_final_atual * 1.08)` por chefe, preservando as fórmulas e multiplicadores relativos de defesa/contra.

## Componentes

- Manter parâmetros de combate locais/authored em `LovableBattle`/`BattleMission`.
- Usar uma função de tuning única para evitar percentuais diferentes por tela ou missão.
- Arredondar para cima após aplicar cada escala e garantir dano mínimo atual de 1 quando houver acerto.

## Persistência/economia

- Sem migração, nenhum dado persistido novo.
- `CampaignReward`, `ForgeRepository`, recibos e idempotência não mudam.

## Testes

- Comparar HP/dano ante e pós-tuning por todas as missões.
- Checar monotonicidade de dificuldade, defesa, contra-ataque, super, vitória e derrota.
