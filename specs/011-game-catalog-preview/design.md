# 011 — Catálogo local do jogo na Coleção: Design

## Arquivo compartilhado

`shared/game_catalog.json` contém `{items:[{id,name,group_id,variants:[{id,tier_id,name}]}]}` em ordem dos quatro grupos do domínio. Identificadores internos kebab-case explícitos; tiers usam IDs fixos `origin`, `ascension`, `legendary`, `multiversal`, `infinity`. A ordem das cinco variantes é a ordem oficial da tabela. Nomes são copiados da fonte, sem derivação automática.

FastAPI carrega o JSON uma vez na inicialização para `/v1/game/characters`. O Gradle inclui `shared/` como Android assets. Android abre e parseia o asset com `org.json` padrão. Não há socket, endereço, cleartext ou INTERNET.

## UI da Coleção

Cabeçalho explica `Catálogo do jogo` e que conteúdo editorial é separado. Seção por grupo e cartões por personagem; cada cartão mostra o nome e uma lista em cinco linhas `Origem: <nome>`, `Ascensão: <nome>` etc. Ordenação acompanha JSON, evitando duplicar conteúdo em strings Java.

Cartões usam tokens atuais. Todo conteúdo fica no ScrollView existente; nenhum item é clicável nem marca uma variante como possuída/equipada.

## Validação

- Python testa JSON tipado, cardinalidade 21 × 5, IDs únicos, grupos/nomes ordenados e ausência de campos externos.
- Backend testa JSON e schema/resposta; Android compila o parser e AVD valida asset empacotado e render. Não há unit test JVM direto para AssetManager/org.json desta tela.
- Build/lint offline e testes backend.

## Segurança e limites

O fixture é catálogo estático de jogo; não contém texto editorial, Comic Vine IDs, URL de imagem ou dados de usuário. O app não solicita rede. Mudança nos docs deve atualizar fixture e testes no mesmo incremento.
