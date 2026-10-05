# 045 - Design

## Decisões delegadas

- O pedido de compra de Fragmentos com XP/créditos delegou valores. Cada compra é de um Fragmento `FRAGMENT`, 2:1 permanece no fluxo da Forja.
- Marcos de XP seguem a progressão cumulativa das cinco primeiras vitórias aprovadas em `CampaignReward`: 0, 840, 1.760, 2.860, 3.720 e 4.720.
- Preço progride de 800 a 1.300 créditos em passos de 100 conforme a tabela de requirements.

## UX

- Inserir botão compacto dourado/roxo no canto esquerdo da faixa superior comum dos destinos; tema e áudio ficam à direita.
- Tela Loja usa cards das seis Joias com quantidade atual, bloqueio XP, preço e ação comprar. Atualizar o card/saldo sem sair da loja.
- Em item bloqueado mostrar XP atual e mínimo. Em erro de crédito/inventário mostrar mensagem inline/Toast clara e saldo inalterado.

## Persistência e transação

- Subir `ForgeRepository` para schema v8 e criar ledger `shop_purchases(operation_id PRIMARY KEY, stone, amount, credits_spent)`.
- `purchaseFragment(operationId, stone)` roda uma transação: conferir XP mínimo, créditos, limite 999, ledger; inserir recibo; decrementar créditos; incrementar o estágio FRAGMENT; commit.
- Migration v7→v8 só cria ledger; preserva player_resources e todos os estágios do inventário.
- XP não é consumido. Preços/tier são código local determinístico, nunca IA/API.

## Testes

- Compra válida por cor/marco; insuficiência de créditos/XP; cap 999; operação repetida; rollback; migração de DB v7 preserva estado.
