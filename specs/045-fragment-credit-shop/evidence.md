# 045 - Evidence

Status: implementacao local concluida; aguardando push e deploy.

## Auditoria inicial

- `ForgeRepository` está no schema v7; `player_resources` guarda créditos/XP; `inventory` guarda quatro estágios por Joia e limita quantidades em 0..999.
- `CampaignReward` define XP/custos ganhos e a migração atual v6→v7 cria recibos de recompensas. Nenhuma compra existe.
- O topo de cada destino contém uma faixa com os controles de tema e áudio.

## Após implementação

Anexar resultado da suíte, teste transacional/migração e captura da tela Loja.

## Resultado apos implementacao

- Loja no topo esquerdo e disponivel em todos os destinos principais. Oferta compra um Fragmento por vez; custo e XP minimo: Espaco 800/0, Mente 900/840, Realidade 1.000/1.760, Poder 1.100/2.860, Tempo 1.200/3.720, Alma 1.300/4.720. XP nao e gasto; niveis existentes da Forja continuam exigindo fusao manual.
- Schema migra v7 para v8 com tabela de recibos. `purchaseFragment` debita credito e incrementa inventario atomicamente; rejeita XP/credito insuficiente e limite 999; operation id e idempotente.
- Cobertura inclui oferta, purchase, idempotencia, erros, limite, migracao v7 e UI. Testes JVM Android, lint e assemble passaram. Testes instrumentados nao executaram devido assinatura do app instalada no unico AVD; nada foi desinstalado.
