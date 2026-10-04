# 002 — Android App Shell: Tasks

Status: **implementação e validação automatizada concluídas; shell exercitado no AVD**.

- [x] **T01 — Resolver discovery e aprovar requirements/design**
  - Requisitos: AC-01, AC-02; Q-016 a Q-019
  - Dependências: respostas às decisões de UX e gates G0/G1/G2 de Erick
  - Arquivos esperados: `requirements.md`, `design.md`, `memory/OPEN_QUESTIONS.md`, `memory/DECISIONS.md`
  - Pronto quando: destinos, conteúdo placeholder e padrão de navegação estiverem decididos. Concluído com respostas explícitas em 2026-09-27.

- [x] **T02 — Implementar shell e tokens aprovados**
  - Requisitos: AC-02, AC-03, AC-06
  - Dependências: T01
  - Arquivos esperados: `android-app/app/src/main/` e testes do app estritamente necessários
  - Pronto quando: destinations e estilos refletem os documentos aprovados e rotas locais existem. Build, lint e testes aprovados.

- [x] **T03 — Validar acessibilidade e perfis de telefone**
  - Requisitos: AC-04, AC-05
  - Dependências: T02
  - Arquivos esperados: layouts, testes UI, evidências
  - Pronto quando: tamanhos compactos/grandes, escala de fonte e semântica forem verificados e limitações registradas. AVD: 411×914 dp, 390×844 dp e escala 1.3; alvos de 48 dp e estado/labels na hierarquia. TalkBack falado não foi ensaiado.

- [x] **T04 — Executar verificações e revisar**
  - Requisitos: AC-07, AC-08
  - Dependências: T02, T03
  - Arquivos esperados: `evidence.md`, `memory/CURRENT_STATE.md`, `memory/SESSION_LOG.md`, `memory/DECISIONS.md`, `memory/OPEN_QUESTIONS.md`
  - Pronto quando: build, lint e testes relevantes passarem após a última edição, e cada aceite tiver evidência adequada.
