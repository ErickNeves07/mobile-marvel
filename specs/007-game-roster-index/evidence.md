# 007 — Índice local do roster jogável: Evidence

Status: **implementado e validado em 2026-09-27.**

## Verificações

| Data | Comando/verificação | Resultado |
|---|---|---|
| 2026-09-27 | `backend/.venv/Scripts/python.exe -m pytest` (implementação original) | 7 testes aprovados (health, campanhas e roster); a spec 011 ampliou o contrato com variantes e elevou a suíte a 8.
| 2026-09-27 | `backend/.venv/Scripts/python.exe -m pip check` | `No broken requirements found.`
| 2026-09-27 | OpenAPI local (implementação original) | Schema inicial continha `id`, `name`, `group_id`; extensão documentada na spec 011 acrescenta `variants`.
| 2026-09-27 | `scripts/validate-local.ps1 -Target all` após a implementação original | Android `BUILD SUCCESSFUL`; backend 7 testes passaram e `pip check` limpo; extensão posterior validada em spec 011.

## Dados conferidos

21 nomes na ordem documental: 8 Avengers e aliados, 4 X-Men, 4 Quarteto Fantástico e 5 cósmicos/especiais. Xavier está no grupo X-Men. Magneto não está no roster. IDs explícitos são únicos; o contrato não inclui campos de Comic Vine, atributos ou habilidades.

## Limitações

- Nenhuma imagem, fato editorial, papel além da distinção jogável conhecida ou estatística de gameplay foi incluída.
- Android ainda não consome esse endpoint; a base URL/transporte faz parte de futura spec.
