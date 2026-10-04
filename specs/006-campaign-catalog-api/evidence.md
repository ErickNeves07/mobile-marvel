# 006 — Catálogo local de campanhas: Evidence

Status: **implementado e validado em 2026-09-27.**

## Verificações

| Data | Comando/verificação | Resultado |
|---|---|---|
| 2026-09-27 | `backend/.venv/Scripts/python.exe -m pytest` | 4 testes aprovados (health existente + 3 do catálogo).
| 2026-09-27 | `backend/.venv/Scripts/python.exe -m pip check` | `No broken requirements found.`
| 2026-09-27 | Inspeção local de `app.openapi()` para `GET /v1/campaigns` | 200 referencia schema `CampaignCatalogResponse`.
| 2026-09-27 | `scripts/validate-local.ps1 -Target all` após specs 006/007 | Android `BUILD SUCCESSFUL`; backend 7 testes passaram e `pip check` limpo.

## Registro

Resposta contém resumos de X-Men (chefe Magneto) e Quarteto Fantástico. Cada valor deriva dos requisitos globais; o chefe é omitido no segundo registro porque não foi especificado. Campos de objetivo, recompensa, dificuldade e elenco não são expostos.

## Segurança e limites

Sem chamada externa, credencial, dado de usuário, persistência, escrita em execução ou nova dependência. Coleção imutável em memória, limitada às duas campanhas explicitamente listadas.

Os testes usam a venv já disponível e não instalam pacotes.
