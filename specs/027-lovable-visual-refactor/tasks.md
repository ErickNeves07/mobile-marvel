# 027 — Refatoração visual baseada no protótipo Lovable — Tasks

Status: **G0/G1/G2 aprovados; implementação ativa**. Q-037 permanece como dependência de aceitação visual final.

- [x] T00 — Auditar app atual e recursos públicos da referência; registrar tokens, rotas e lacunas.
  - Requisitos: AC-01/02/05. Pronto: matriz inicial em `design.md` e `evidence.md`.
- [x] T01 — Fechar G0/G1/G2 com Erick: design/abertura/navegação fiéis, regras nativas preservadas, 105 artes distintas; Q-037 permanece para comparação renderizada.
  - Requisitos: Q-036/Q-038. Dependência: T00. Arquivos: requirements/design/memória. Pronto: decisão registrada em 2026-10-02; economia passou à spec 028.
- [ ] T02 — **Em progresso:** criar tokens, temas, tipografia e componentes visuais Android reutilizáveis. Paleta, fontes, painéis angulares e desenhos da abertura/Nexus já compilam; falta comparação renderizada e estados acessíveis.
  - Requisitos: AC-01/02/04/06. Dependência: T01. Arquivos: `android-app/app/src/main/res/values/*`, drawables e classes de UI. Pronto: build/lint e contraste verificados.
- [ ] T03 — **Em progresso:** refatorar shell, navegação e Nexus. Abertura, barra inferior e hero de anéis no AVD; composição abaixo do hero ainda não corresponde totalmente à referência.
  - Requisitos: AC-01/02/04. Dependência: T02. Arquivos: `MainActivity`, layout/componentes. Pronto: AVD e navegação funcional.
- [ ] T04 — **Em progresso:** refatorar Campanhas, equipe, batalha e recompensa existentes. Mapa em trilha com cartões compactos e expansão funcional no AVD; telas internas/batalha/recompensa ainda aguardam alinhamento.
  - Requisitos: AC-01/03/04. Dependência: T02. Pronto: fluxo funcional e capturas.
- [ ] T05 — **Em progresso:** refatorar Forja, merge e Manopla. Painel dos seis encaixes e resumo de inventário ligados ao estado real; detalhes/fusões em painel expansível. Composição e estados ainda divergem do protótipo; custo 2:1 aprovado, com implementação/contrato histórico na spec 028.
  - Requisitos: AC-01/02/03/04. Dependência: T02. Pronto: estado e ações preservados.
  - Incremento solicitado em 2026-10-04: criar tela Manopla com anéis/hexágonos luminosos, ressonância e seis linhas de estado a partir do SQLite real; comparar captura móvel, testar gate e acessibilidade.
- [ ] T06 — **Em progresso:** refatorar Coleção, variantes e editorial; criar/selecionar 105 artes distintas com procedência e substituir todas as silhuetas. Contador real, busca e filtros de grupo adicionados; grade/retratos/detalhe aguardam Q-041.
  - Requisitos: AC-01/03/04/06. Dependência: T02. Pronto: 105 artes por personagem/tier, sem silhuetas.
- [ ] T07 — **Em progresso:** refatorar Deadpool e estados de erro/loading/vazio. Superfície de papel/quadrinhos verificada no AVD; demais estados e comparação pendentes.
  - Requisitos: AC-01/03/04. Dependência: T02. Pronto: ação e fallback preservados.
- [ ] T08 — Verificar matriz final, capturas comparativas, acessibilidade e desempenho; registrar evidências.
  - Requisitos: AC-01–06. Dependência: T03–T07. Pronto: testes após a última edição, revisão por requisito e limitações.
