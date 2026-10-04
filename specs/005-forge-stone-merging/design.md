# 005 — Forja — Design

Status: **decisões fechadas; detalhamento da implementação local.**

## Modelo e receita

`InfinityStone`: SPACE, MIND, REALITY, POWER, TIME, SOUL. `ForgeStage`: SHARD, FRAGMENT, UNSTABLE_CORE, COMPLETE. Cada par guarda uma contagem inteira no intervalo inclusivo `[0,999]`. Novo banco inicia com 24 linhas a zero.

Para origem `s` abaixo de COMPLETE, a política consome 3 de `(stone,s)` e concede 1 de `(stone,next(s))`. Nenhuma outra entrada é aceita. COMPLETE é terminal.

## Recompensas e idempotência

Um evento de conclusão elegível contém ID único, tipo `DAILY_CHALLENGE` ou `CAMPAIGN` e uma Joia. Ele concede 3 SHARD daquela Joia, uma vez. IDs de conclusão são gravados na mesma transação que a recompensa. ID repetido é retorno idempotente sem crédito adicional. Chaves vazias, tipo ou Joia inválidos são rejeitados. Campanhas/desafios placeholder não criam eventos.

## Persistência Android

Usar `SQLiteOpenHelper` e SQLite nativo do Android para evitar adicionar dependência à matriz offline. Criar tabela de contagem com chave composta `(stone,stage)`, `CHECK count BETWEEN 0 AND 999`, tabela de IDs de recompensas e tabela de IDs de merges já aplicados. Inicialização em transação `INSERT OR IGNORE` das 24 linhas. Migração v1→v2 acrescenta somente a tabela de IDs de merge e preserva contagens/recompensas existentes.

Merge usa `beginTransaction`, valida entradas e cap antes de débito, atualiza as duas linhas dentro da mesma transação, lê resultado e só então marca sucesso. Qualquer erro causa rollback. Recompensa grava ID e atualiza shard na mesma transação. Todas as chamadas locais de escrita devem executar fora da thread de UI; não há rede na operação.

## Interface

Cada Joia aparece com estágio/contagem e estado textual. Seleção de ação mostra “consome 3 X” e “produz 1 Y”, seguida de confirmação explícita irreversível. Cancelar mantém inventário. Atualização somente ocorre após sucesso do repositório. Erros de insuficiência/limite são apresentados sem mutação. Inicialmente vazio é um estado informativo, não uma falha.

## Testes

- Testes puros para mapeamento de próximo estágio e quantidades.
- Instrumentação Android para SQLite: inicialização vazia, merge atômico, rejeições sem alteração, cap, persistência ao reabrir, deduplicação e rollback.
- Testes de UI/AVD: confirmação/cancelamento, estado vazio, contagens, acessibilidade e fonte ampliada.
- Sem chamadas Groq, Comic Vine ou outros serviços externos.

## Riscos/limites

SQLite do sistema atende as transações locais e não introduz biblioteca; se arquitetura evoluir para Room, isso exigirá migração versionada em spec própria. Recompensas são grant local confiável nesta etapa, não prova de conclusão assinada por servidor; só eventos produzidos pelos futuros fluxos de conclusão podem chamá-lo.
