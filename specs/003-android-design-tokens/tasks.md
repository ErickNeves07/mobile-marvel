# 003 — Android Design Tokens: Tasks

Status: **tokens implementados, renderizados no AVD e validados em build/lint/testes**.

- [x] **T01 — Definir identidade inicial delegada**
  - Requisitos: AC-01, AC-03, AC-06
  - Dependências: autorização do usuário registrada em DEC-020
  - Arquivos: `design.md`, `memory/DECISIONS.md`
  - Pronto quando: direção, papéis de cor, tipografia, escalas e acessibilidade estão definidos.

- [x] **T02 — Criar recursos nomeados de design**
  - Requisitos: AC-01, AC-02, AC-05
  - Dependências: T01
  - Arquivos: `android-app/app/src/main/res/values/`
  - Pronto quando: cores, dimensões e estilos tipográficos estão centralizados sem dependências externas. Recursos e referências Java passaram checagem estrutural local.

- [x] **T03 — Validar contraste e integração visual**
  - Requisitos: AC-03, AC-04, AC-06
  - Dependências: T02
  - Arquivos: `evidence.md`
  - Estado: pares calculados acima de AA; cinco destinos inspecionados no AVD em escalas/tamanhos distintos.

- [x] **T04 — Rodar build, lint e testes após a última alteração**
  - Requisitos: AC-04, AC-05
  - Dependências: T02, T03
  - Arquivos: `evidence.md`, memória
  - Pronto quando: a matriz local passar e as limitações forem registradas.
