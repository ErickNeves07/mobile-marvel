# 011 — Catálogo local do jogo na Coleção: Requirements

Status: **derivado do roster e da tabela de variantes já documentados; acesso editorial externo permanece separado.**

## Objetivo

Apresentar os 21 personagens e as 105 variantes nomeadas do catálogo de jogo na aba Coleção, usando o mesmo arquivo de dados estáticos que o backend local.

## Fontes

- `docs/09-DOMAIN_UX_CATALOG.md`: grupos, lista de personagens jogáveis e cinco nomes de variante por personagem.
- `docs/02-REQUIREMENTS.md`: R-USER-007 separa editoriais/jogo; R-USER-010 define 21 personagens e cinco variantes cada.
- `docs/03-ARCHITECTURE.md`: GameCharacter/EditorialCharacter/CharacterLink permanecem separados e dados de jogo são orientados a dados.
- `specs/007-game-roster-index`: namespace interno `/v1/game/characters`.

## Escopo

- Criar um único JSON local `shared/game_catalog.json` com os 21 personagens, IDs internos, grupos, e os cinco níveis/nome de variante aprovados.
- Atualizar `GET /v1/game/characters` para servir esse catálogo com dados de variantes.
- Empacotar o mesmo arquivo como asset local Android; a Coleção lê o arquivo offline sem conectar ao backend.
- Renderizar grupo, personagem e cinco nomes de variantes em lista rolável; identificar como dados de jogo.
- Exibir texto neutro de que informações editoriais são uma categoria separada ainda não incluída nesta prévia.

## Fora de escopo

- Comic Vine IDs, biografias, capas, imagens, poderes, estatísticas, comparações editoriais, inventário/equipamento do usuário.
- Busca, filtros, paginação, detalhes, favorito, persistência, rede, binding de `CharacterLink`.
- Variantes fora das cinco registradas no documento de domínio.

## Requisitos

- **COLL-R-001:** catálogo contém exatamente 21 personagens em quatro grupos e exatamente cinco variantes (Origem, Ascensão, Lendário, Multiversal, Infinito) cada.
- **COLL-R-002:** nomes/grupos/nome de variante correspondem à tabela do documento de domínio; IDs internos são únicos e não se passam por IDs externos.
- **COLL-R-003:** um único arquivo JSON é a fonte de dados backend e asset Android.
- **COLL-R-004:** UI dá acesso aos 21 registros e seus 105 nomes via lista rolável/legível.
- **COLL-R-005:** dados editoriais permanecem explicitamente ausentes/separados; nenhum dado factual/editorial ou atributo de gameplay é inventado.
- **COLL-R-006:** catálogo é estático, offline, sem banco, credenciais, dependências ou permissão INTERNET.

## Critérios de aceite

- [x] AC-01 — backend valida cardinalidade, ordem, IDs, grupos e 105 variantes contra a fonte de conteúdo.
- [x] AC-02 — Android lê o mesmo JSON incluído no APK e apresenta todos os grupos, personagens e cinco variantes.
- [x] AC-03 — testes impedem nomes extras/ausentes e mistura editorial/gameplay.
- [x] AC-04 — build, lint, testes Android/backend passam sem dependência nova.
- [x] AC-05 — screenshots AVD registram primeiro e último grupo em telas compactas e fonte maior.
