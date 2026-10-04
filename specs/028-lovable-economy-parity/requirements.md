# 028 — Economia e recompensas do protótipo Lovable — Requirements

Status: fusão 2:1 e campanha implementadas; Erick escolheu 1 Fragmento de Joia para o desafio diário em 2026-10-04.

## Objetivo

Alinhar a Forja e as recompensas ao visual do Lovable com progressão real, persistida e específica de cada missão.

## Escopo confirmado

- Duas peças iguais da mesma Joia e estágio produzem uma peça do estágio seguinte. A regra vale apenas para fusões novas; não compensar fusões antigas.
- Remover `Homem-Aranha +1` da recompensa.
- Cada uma das seis missões tem Joia, créditos e XP próprios na tabela aprovada do design. Quatro Fragmentos da Joia indicada substituem os três Estilhaços por missão.
- Conceder o pacote somente na primeira vitória de cada missão, em transação local idempotente. Derrotas e vitórias repetidas não concedem itens.
- Missões concluídas antes da atualização preservam as recompensas antigas, sem complemento retroativo.
- Exibir os três itens realmente concedidos na tela de recompensa. Saldos de créditos e XP devem sobreviver à reabertura do app.
- Uma vitória no desafio diário concede exatamente 1 Fragmento da Joia determinística do dia, uma vez por data; derrota não concede. Vitórias antigas não recebem complemento.

## Aceite

- [x] AC-01: fusão 2:1 transacional/idempotente e inventário anterior preservado.
- [x] AC-02: seis pacotes distintos por missão, sem dependência de Comic Vine ou IA para balanceamento.
- [x] AC-03: exatamente uma concessão por missão, com 4 Fragmentos, créditos e XP atômicos; repetição e derrota não premiam.
- [x] AC-04: migração do banco preserva missões e saldos anteriores; nenhum prêmio retroativo.
- [x] AC-05: UI mostra prêmio específico da missão e saldos persistidos.
- [x] AC-06: instrumentação, lint, build e evidência depois da última edição.
- [x] AC-07: desafio diário concede um Fragmento, idempotente e persistido, com texto coerente e sem retroatividade.

## Fora de escopo

- Copiar nível, inventário ou saldos de demonstração do Lovable.
- Recompensas, dificuldade ou atributos calculados por Comic Vine/IA.
