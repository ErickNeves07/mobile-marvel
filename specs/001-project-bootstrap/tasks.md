# 001-project-bootstrap — Tasks

Status: **G0/G1/G2 aprovados; bootstrap e importação no Android Studio confirmados; shell posterior também validado em 2026-09-27**.

- [x] **T01 — Resolver discovery e aprovar a spec**
  - Requisitos: BOOT-R-001, BOOT-R-002, BOOT-R-009, BOOT-R-010; AC-01, AC-02, AC-03
  - Dependências: Q-007 e Q-009 a Q-014 resolvidas
  - Arquivos esperados: `specs/001-project-bootstrap/*.md`, `memory/*.md`
  - Pronto quando: dúvidas bloqueantes estiverem respondidas, decisões registradas e a matriz/ações exatas estiverem autorizadas. **Concluído em 2026-09-27.**

- [x] **T02 — Confirmar e preparar o toolchain aprovado**
  - Requisitos: BOOT-R-001, BOOT-R-005, BOOT-R-009; AC-03
  - Dependências: T01; autorização específica para consulta externa, instalação ou configuração
  - Arquivos esperados: `specs/001-project-bootstrap/evidence.md`; sem alteração global silenciosa
  - Pronto quando: Git, JDK, Android SDK e Python necessários estiverem utilizáveis; matriz exata de versões estiver documentada com fonte/data; nenhuma versão dinâmica ou release proibida for escolhida.
  - Estado: JBR 21.0.8, Python 3.13.15, Git, SDK/AVD e distribuição Gradle 8.13 locais utilizados. Nenhuma instalação ou acesso à rede.

- [x] **T03 — Gerar o esqueleto Android Java mínimo**
  - Requisitos: BOOT-R-003, BOOT-R-005, BOOT-R-006; AC-04
  - Dependências: T02; namespace e níveis SDK aprovados
  - Arquivos esperados: `android-app/`, Gradle Wrapper, Manifest, Activity Java, layout XML, teste unitário
  - Pronto quando: projeto importar e compilar via Wrapper, código de produção estiver em Java e teste mínimo passar sem segredos.
  - Estado: arquivos Java/XML, Wrapper, teste unitário, lint e APK debug foram produzidos e verificados; Erick confirmou que o projeto importa visualmente no Android Studio.

- [x] **T04 — Verificar o app Android em runtime**
  - Requisitos: BOOT-R-003, BOOT-R-007; AC-05
  - Dependências: T03; emulador/dispositivo aprovado disponível
  - Arquivos esperados: `specs/001-project-bootstrap/evidence.md`
  - Pronto quando: Activity mínima abrir sem crash e a verificação manual estiver registrada sem capturar dados sensíveis.
  - Estado: AVD `Medium_Phone_API_36.1` iniciou; APK instalado; `MainActivity` permaneceu como Activity no topo e o processo ficou ativo.

- [x] **T05 — Criar o esqueleto FastAPI mínimo**
  - Requisitos: BOOT-R-004, BOOT-R-005, BOOT-R-006; AC-06, AC-07
  - Dependências: T01; workflow Python aprovado
  - Arquivos esperados: `backend/app/`, `backend/tests/`, manifesto/lock de dependências aprovado, `backend/README.md`
  - Pronto quando: ambiente isolado reproduzir a instalação, servidor iniciar em loopback e teste de `/health` passar sem credenciais.
  - Estado: instalação limpa em `backend/.venv-repro` com hashes, `pip check`, pytest e startup `/health` passaram; `.venv` original preservada.

- [x] **T06 — Aplicar guardrails de configuração e segredos**
  - Requisitos: BOOT-R-005, BOOT-R-006, BOOT-R-008; AC-08
  - Dependências: T03, T05
  - Arquivos esperados: `.gitignore` e exemplos mínimos de configuração, somente se necessários
  - Pronto quando: verificações não encontrarem segredo, arquivo local sensível ou versão dinâmica nos artefatos do bootstrap.
  - Estado: padrões comuns de credenciais não foram encontrados no código, testes ou APK; busca por versões dinâmicas não retornou resultados; `.venv`, caches e saídas de validação estão ignorados.

- [x] **T07 — Documentar execução reproduzível**
  - Requisitos: BOOT-R-007; AC-09
  - Dependências: T03, T05, T06
  - Arquivos esperados: README(s) do Android/backend e referências mínimas no README raiz se necessário
  - Pronto quando: comandos documentados de build/teste funcionarem no workspace Windows real após a última edição.
  - Estado: README Android documenta variáveis e comando Wrapper offline com diretório de saída configurável; o comando completo passou após essa configuração.

- [x] **T08 — Verificar e encerrar a spec**
  - Requisitos: BOOT-R-007, BOOT-R-010; AC-10
  - Dependências: T04, T07
  - Arquivos esperados: `specs/001-project-bootstrap/evidence.md`, `memory/CURRENT_STATE.md`, `memory/SESSION_LOG.md`, `memory/DECISIONS.md`, `memory/OPEN_QUESTIONS.md`
  - Pronto quando: aceite for revisado item a item, testes finais passarem, limitações forem registradas e nenhum commit/push tiver ocorrido sem pedido explícito.
  - Estado: testes automatizados, instalação limpa, runtime e confirmação visual do Android Studio foram concluídos. O shell posterior à spec também foi validado em matriz Android offline; detalhes em `specs/002-android-app-shell/evidence.md`.
