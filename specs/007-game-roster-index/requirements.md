# 007 — Índice local do roster jogável: Requirements

Status: **derivado do catálogo de domínio existente; implementação autorizada na janela DEC-021.**

## Objetivo

Oferecer índice estático dos 21 personagens jogáveis explicitamente listados, separado da coleção editorial Comic Vine.

## Fontes

- `docs/09-DOMAIN_UX_CATALOG.md`, seções Roster jogável e grupos.
- `docs/02-REQUIREMENTS.md`, R-USER-010 (21 personagens, cinco variantes no catálogo) e R-USER-011 (vilões chefes no primeiro ciclo; Xavier jogável como suporte; Magneto chefe).
- `docs/03-ARCHITECTURE.md`, separação `EditorialCharacter`, `GameCharacter` e `CharacterLink`.

## Escopo

- Endpoint interno `GET /v1/game/characters` para roster de jogo, distinto de `GET /v1/characters` (catálogo editorial futuro).
- Resumo base com `id`, `name`, `group_id`; desde a spec 011, resposta também inclui as cinco variantes de jogo para cada personagem a partir da fonte compartilhada. IDs internos estáveis em slug ASCII e ordem documental.
- 21 personagens listados, agrupados como no documento de domínio.
- Testes de cardinalidade, IDs únicos, grupo, ordem e exclusão de chefe não jogável Magneto.

## Fora do escopo

- Comic Vine IDs, biografias, imagens, fatos editoriais, vínculos editoriais.
- Papéis, atributos, poderes, habilidades, variantes equipadas ou progressão.
- Paginação, busca, persistência, mutação, rede externa ou implementação da coleção Android.

## Requisitos

- **ROSTER-R-001:** backend expõe lista de jogo estática em rota namespaceada `/v1/game/characters`.
- **ROSTER-R-002:** contém exatamente os 21 nomes e quatro grupos definidos pelo roster no doc 09.
- **ROSTER-R-003:** IDs internos não se passam por IDs Comic Vine e permanecem únicos/determinísticos.
- **ROSTER-R-004:** Magneto, declarado chefe e não jogável no primeiro ciclo, não aparece na lista.
- **ROSTER-R-005:** Xavier aparece como personagem jogável do grupo X-Men; nenhum papel adicional é atribuído.
- **ROSTER-R-006:** não inclui dados editoriais nem atributos/poderes de gameplay não especificados; serviço externo, banco ou dependência adicional não são usados.

## Critérios de aceite

- [x] AC-01 — GET responde 200 e schema `{items: [...]}` validado.
- [x] AC-02 — os 21 nomes/grupos estão completos, únicos e na ordem dos grupos documentados.
- [x] AC-03 — Magneto não aparece; Xavier consta no grupo X-Men.
- [x] AC-04 — resposta é determinística e campos editoriais/de jogo não especificados estão ausentes.
- [x] AC-05 — testes backend e `pip check` passam sem instalar dependências.
