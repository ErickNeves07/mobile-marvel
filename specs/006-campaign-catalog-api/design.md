# 006 — Catálogo local de campanhas: Design

Status: **contrato restrito a fatos já aceitos nos requisitos globais.**

## Endpoint

`GET /v1/campaigns`

Sem query string. Resposta `application/json`; Pydantic mantém contrato explícito.

```json
{
  "items": [
    {
      "id": "x-men",
      "title": "X-Men",
      "factionId": "x-men",
      "bossId": "magneto"
    },
    {
      "id": "fantastic-four",
      "title": "Quarteto Fantástico",
      "factionId": "fantastic-four"
    }
  ]
}
```

Os campos identificam as duas campanhas explicitamente definidas nos requisitos e Magneto como chefe X-Men. Quarteto Fantástico não recebe chefe fictício. Não se declara que o chefe já é encontrável/jogável nem se define evento da história. Ausência de dado desconhecido é representada omitindo o campo.

## Componentes

- `CampaignSummary`: Pydantic `BaseModel` com quatro strings não vazias.
- `CampaignCatalogResponse`: envelope Pydantic contendo lista ordenada de summaries.
- Catálogo constante, tupla imutável em `backend/app/content/campaigns.py`.
- Rota FastAPI em `backend/app/main.py` injeta catálogo constante, sem dependência externa.

IDs usam a convenção kebab-case interna que a arquitetura já descreve para IDs estáveis; o identificador específico de campanha `x-men` e chefe `magneto` é normalização reversível dos nomes existentes nos docs. Nenhum ID Comic Vine é afirmado.

## Segurança e falhas

- Sem request body, credencial, PII, logs, rate limiting, I/O, rede ou mutação.
- Coleção local estática gera resposta 200. Falha de serialização/schema é regressão coberta por testes, sem fallback silencioso.
- Sem cache ou persistência: dado compilado em memória.

## Testes

- TestClient valida HTTP 200, shape e valores rastreáveis.
- Testa que campos não definidos não existem, payload repetido é igual e ordem não varia.
- Reexecuta testes backend e `pip check`; sem novo pacote.
