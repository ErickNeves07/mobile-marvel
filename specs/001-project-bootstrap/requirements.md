# 001-project-bootstrap — Requirements

Status: **G0/G1/G2 aprovados por Erick em 2026-09-27; implementação autorizada**.

## Objetivo

Preparar uma fundação mínima, reproduzível e testável para o app Android nativo em Java e para o backend FastAPI, sem implementar features de produto nem acessar Comic Vine, Gemini ou qualquer outro serviço externo.

## Requisitos relacionados

- R-OFF-001, R-OFF-002, R-OFF-003, R-OFF-004 — a fundação deve permitir a integração futura via backend sem expor a chave Comic Vine.
- R-USER-001 — Android Studio e Java.
- R-USER-013 — backend Python/FastAPI, preparado para LangChain e Gemini atrás de adapter em spec posterior.
- R-USER-016 — segredos nunca entram no APK ou Git.
- R-USER-017 — dúvida crítica bloqueia implementação.
- RNF-004 — TLS em produção, segredos no backend e logs seguros.
- RNF-006 — base para testes unitários e de integração.
- RNF-007 — dependências em versões exatas.

## Requisitos da spec

- **BOOT-R-001:** registrar as versões e lacunas de Git, JDK, Android Studio, Android SDK, Gradle, Python e Codex encontradas antes da implementação.
- **BOOT-R-002:** selecionar e registrar perfis Codex principal e rápido usando somente IDs realmente presentes no catálogo local da conta.
- **BOOT-R-003:** criar, após aprovação, `android-app/` como projeto Android nativo cuja linguagem de produção seja Java.
- **BOOT-R-004:** criar, após aprovação, `backend/` como aplicação FastAPI executável em ambiente virtual isolado.
- **BOOT-R-005:** fixar todas as versões de plugins e dependências; nenhuma faixa dinâmica, snapshot ou release com menos de sete dias sem justificativa de segurança.
- **BOOT-R-006:** manter credenciais fora de código, APK, arquivos versionados, logs, testes e evidências; exemplos devem conter apenas nomes de variáveis e valores fictícios inequívocos.
- **BOOT-R-007:** incluir verificações mínimas automatizadas para provar que os dois esqueletos compilam/inicializam no ambiente suportado.
- **BOOT-R-008:** não incluir nesta spec integração real com Comic Vine, Gemini/LangChain, banco Room, gameplay, coleção, merge, campanhas ou UI final.
- **BOOT-R-009:** nenhuma instalação, autenticação, chamada externa, mudança global de ambiente, commit ou push ocorre sem autorização explícita.
- **BOOT-R-010:** a implementação só começa depois da resolução das dúvidas bloqueantes e da aprovação explícita dos documentos desta spec.
- **BOOT-R-011:** configurar `namespace`/`applicationId` como `com.erickbarbosa.rupturainfinita`, `minSdk` 26, `compileSdk` 36 e `targetSdk` 36.
- **BOOT-R-012:** projetar inicialmente para smartphones em portrait, cobrindo tamanhos compactos e grandes de telefone, com referência visual de 390 × 844 dp; o tratamento de janelas `sw600dp` ou maiores depende da Q-013.

## Escopo

- inventário e saneamento planejado do toolchain local;
- projeto Android de um módulo `app`, com Activity Java e layout XML mínimos;
- Gradle Wrapper versionado e configuração de build reproduzível;
- backend FastAPI mínimo com endpoint local de saúde;
- testes mínimos de bootstrap;
- arquivos de configuração seguros e documentação para execução local;
- atualização de evidências e memória do projeto.

## Fora de escopo

- chamadas à Comic Vine ou Gemini;
- inclusão de chaves reais;
- LangChain e adapters de IA;
- contratos de catálogo, campanhas, combate, merge ou progressão;
- Room, Retrofit, cache editorial ou navegação completa;
- identidade visual final, assets Marvel ou materiais licenciados;
- CI remoto, deploy, publicação, autenticação, remoto Git ou release.

## Fluxo principal

1. Erick resolve as dúvidas bloqueantes e aprova requirements/design/tasks.
2. O ambiente e as versões estáveis compatíveis são confirmados em fontes oficiais.
3. Com autorização específica, pré-requisitos ausentes são instalados ou configurados.
4. O esqueleto Android Java é gerado, compilado e testado.
5. O esqueleto FastAPI é criado em ambiente isolado, iniciado localmente e testado.
6. Evidências posteriores à última edição são registradas e os critérios são revisados.

## Erros e casos limite

- Git ausente ou fora do `PATH`: bloquear operações Git e instalação até autorização.
- Android SDK ausente, inacessível ou incompleto: não gerar/pinar níveis de SDK por presunção.
- `JAVA_HOME` incompatível com o Java efetivo: escolher explicitamente o JDK do projeto antes do build; não alterar configuração global sem autorização.
- Gradle global ausente: usar o Wrapper do projeto após escolher versões compatíveis; não depender de Gradle global.
- rede, proxy ou provider indisponível: não usar integração externa como critério do bootstrap.
- porta local ocupada: permitir porta configurável para o backend e registrar a usada no teste.
- caminho com espaços, acentos e diretório sincronizado: todos os scripts e comandos devem funcionar no workspace Windows real.
- segredo detectado: interromper, remover da entrega e registrar o incidente sem reproduzir o valor.

## Critérios de aceite

- [x] **AC-01** — inventário local inicial e suas limitações estão registrados em `evidence.md`.
- [x] **AC-02** — modelos visíveis e perfis Codex exatos estão registrados em `memory/DECISIONS.md`.
- [x] **AC-03** — a spec está explicitamente aprovada por Erick e todas as dúvidas bloqueantes, inclusive as descobertas na verificação oficial, estão respondidas.
- [x] **AC-04** — build e teste Java pelo Wrapper passaram; Erick confirmou a importação visual do projeto no Android Studio em 2026-09-27.
- [x] **AC-05** — app instalado e aberto no AVD; processo ativo e Activity no topo. Busca de padrões de chaves no APK sem ocorrências.
- [x] **AC-06** — instalação limpa em `backend/.venv-repro` com `--require-hashes` concluída; `pip check`, pytest e startup local passaram. A venv original do app foi preservada.
- [x] **AC-07** — `GET /health` respondeu HTTP 200 e payload estável no TestClient e em servidor local.
- [x] **AC-08** — buscas de padrões comuns de segredos e de versões dinâmicas não retornaram ocorrências nos artefatos verificados.
- [x] **AC-09** — comandos documentados passaram no workspace Windows real usando Wrapper offline e saída de validação isolada.
- [x] **AC-10** — evidence revisada requisito a requisito com comandos, resultados e limitações desta execução.

## Dependências

- decisões resolvidas Q-007 e Q-009 a Q-014 registradas em `memory/OPEN_QUESTIONS.md`;
- aprovação humana de G0/G1/G2;
- Git utilizável;
- Android SDK e componentes aprovados;
- JDK/AGP/Gradle compatíveis e com versões exatas;
- Python compatível e workflow de dependências escolhido;
- autorização separada para instalações ou consultas externas necessárias.

## Dúvidas

- **Resolvido:** `minSdk` 26, `compileSdk`/`targetSdk` 36, smartphones portrait, referência 390 × 844 dp.
- **Resolvido:** namespace/application ID `com.erickbarbosa.rupturainfinita`.
- **Resolvido:** `venv` + pip, com lock transitivo e hashes gerado por `pip-tools`.
- **Resolvido:** consulta online somente a fontes oficiais autorizada.
- **Resolvido:** SDK Android existe com Platform 36, Build Tools 36.0.0 e AVD API 36.1.
- **Resolvido:** primeiro ciclo exclui `sw600dp+`; tablets e foldables abertos ficam fora do escopo.
- **Resolvido:** Python 3.13.15 side-by-side e plano operacional exato autorizados.
