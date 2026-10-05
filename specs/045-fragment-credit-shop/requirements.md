# 045 - Loja de Fragmentos

Status: aprovado pelo pedido explícito de Erick em 2026-10-05; preço e marcos de XP delegados.

## Objetivo

Permitir comprar Fragmentos das Joias com créditos, com novas cores liberadas pelo XP acumulado.

## Catálogo definido

As compras concedem somente itens do estágio `FRAGMENT`; fusões continuam manuais 2:1. XP é requisito e não é consumido.

| Joia liberada | XP mínimo | Preço por Fragmento |
|---|---:|---:|
| Espaço | 0 | 800 créditos |
| Mente | 840 | 900 créditos |
| Realidade | 1.760 | 1.000 créditos |
| Poder | 2.860 | 1.100 créditos |
| Tempo | 3.720 | 1.200 créditos |
| Alma | 4.720 | 1.300 créditos |

## Requisitos

- [ ] Botão Loja no topo esquerdo dos destinos principais, ao lado dos controles de tema/áudio.
- [ ] Tela mostra saldo de créditos/XP, Fragmentos atuais, preço, requisito XP e estado bloqueado/liberado.
- [ ] Um toque compra exatamente um Fragmento da cor escolhida; não compra Estilhaço, Núcleo ou Joia completa.
- [ ] A compra debita créditos e soma um Fragmento na mesma transação SQLite.
- [ ] XP acumulado desbloqueia tipos, sem ser gasto.
- [ ] Insuficiência de XP/créditos, inventário cheio, saldo transacional e toques repetidos dão resultado claro e não corrompem estado.
- [ ] Migração preserva saldos/itens; não injeta prêmio inicial em jogadores atuais.

## Fora de escopo

- Compras reais, anúncios, moedas pagas, rede, login, novo recurso que altere valores de batalha.
- Compra de outros estágios ou de Joias completas.

## Critérios de aceite

- AC-01: botão abre a loja a partir dos cinco destinos principais.
- AC-02: catálogo respeita XP mínimo e preço da tabela.
- AC-03: compra bem-sucedida altera créditos e inventário atomicamente e atualiza a UI.
- AC-04: repetição do mesmo ID de operação não cobra/concede duas vezes.
- AC-05: erros de saldo, XP e limite preservam ambos os saldos.
- AC-06: nenhuma fusão ocorre automaticamente.
