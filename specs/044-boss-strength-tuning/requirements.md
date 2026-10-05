# 044 - Ajuste leve de força dos chefes

Status: aprovado pelo pedido explícito de Erick em 2026-10-05 para deixar os chefes apenas um pouco mais fortes.

## Objetivo

Elevar levemente a resistência e a ameaça de cada chefe sem mudar os controles táticos nem alongar excessivamente os confrontos.

## Requisitos

- [ ] Aumentar HP máximo dos nove chefes em 6% (arredondamento determinístico para cima).
- [ ] Aumentar dano final causado pelo chefe em 8%.
- [ ] Manter progressão crescente de dificuldade entre as nove campanhas.
- [ ] Preservar ataque/defesa/desestabilização, contra-ataques, intents, troca de heróis, super, condições de vitória e derrotas.
- [ ] Preservar recompensas, XP, créditos e progressão de fragmentos.
- [ ] Registrar os novos valores de referência em testes; cobrir dano com defesa e contra-ataque.

## Fora de escopo

- Reequilibrar heróis, equipes, variantes, recompensas ou curva inteira de campanha.
- Acrescentar limite de ações/rounds ou regras dependentes de IA.

## Critérios de aceite

- AC-01: cada chefe possui mais HP e causa ligeiramente mais dano que antes.
- AC-02: ordem de ameaça das campanhas continua monotônica.
- AC-03: defesa e contra ainda mitigam dano; nenhum dano vira zero negativo/overflow.
- AC-04: as condições de vitória/derrota e dados da recompensa são idênticos às regras atuais.
