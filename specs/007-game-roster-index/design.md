# 007 — Índice local do roster jogável: Design

## Contrato

`GET /v1/game/characters`

The response schema shown below is the original base example. Spec 011 extends each item with a `variants` array containing the five ordered game tiers; `shared/game_catalog.json` is the current canonical source.

```json
{
  "items": [
    {"id": "iron-man", "name": "Homem de Ferro", "group_id": "avengers-allies"},
    {"id": "captain-america", "name": "Capitão América", "group_id": "avengers-allies"}
  ]
}
```

A lista integral acompanha ordem e agrupamento do roster em `docs/09-DOMAIN_UX_CATALOG.md`. Slugs são IDs internos permanentes; não são identificadores externos nem créditos Comic Vine. Nomes permanecem grafados conforme a fonte de domínio.

## Componentes

- Schemas Pydantic frozen `GameCharacterSummary`/`GameVariantSummary`, com `extra="forbid"`; `variants` foi acrescentado pela spec 011.
- Frozen response envelope com tupla ordenada.
- Fonte estática canônica em `shared/game_catalog.json`, carregada e validada pelo backend.
- Rota FastAPI em `backend/app/main.py`.

IDs explícitos são atribuídos um a um para evitar algoritmo de transliteração ambíguo. Grupos: `avengers-allies`, `x-men`, `fantastic-four`, `cosmic-specials`.

## Segurança e teste

Sem dado pessoal, credencial, rede, DB ou mutação. O catálogo não inclui chefes. Testes fixam o conjunto de nomes, cardinalidade, unicidade de IDs, associação de grupo, Xavier/Magneto e rejeição de campos fora do contrato.
