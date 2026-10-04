# 017 — Validação defensiva do catálogo de jogo: Requirements

Status: **concluída; hardening local do contrato, sem alteração de regras ou dados.**

## Objetivo

Garantir que Android e backend rejeitem catálogos malformados em campos de identidade e nas cinco variantes, preservando o contrato compartilhado.

## Escopo

- Exigir IDs, nomes e `group_id` não vazios após trim.
- Exigir IDs únicos de personagem e de variante globalmente.
- Exigir cinco tiers conhecidos, na ordem definida, com nomes não vazios.
- Preservar o JSON compartilhado e a resposta atual da API.
- Testar entradas válidas e malformadas nos parsers.

## Fora de escopo

- Alterar conteúdo, roster, endpoint ou regras de progressão.
- Sincronização runtime ou atualização remota do catálogo.

## Critérios de aceite

- [x] AC-01 — Android aceita o asset válido e rejeita campos obrigatórios vazios, IDs duplicados e tiers inválidos/desordenados.
- [x] AC-02 — Pydantic rejeita strings whitespace e IDs duplicados de personagens/variantes.
- [x] AC-03 — a fonte compartilhada e a resposta da API permanecem equivalentes.
- [x] AC-04 — testes, build, lint e validação conjunta passam.
