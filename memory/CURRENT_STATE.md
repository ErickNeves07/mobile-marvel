# Estado atual

Última atualização: 2026-10-04

## Atualização mais recente — retratos, Fragmento diário e preparação Render

- Erick definiu **1 Fragmento da Joia do dia** na primeira vitória diária, sem retroatividade. Banco concede de forma idempotente e transacional; testes cobrem persistência e limite 999. As seis recompensas de campanha e fusão 2:1 prospectiva continuam conforme spec 028.
- Erick aprovou usar retratos Comic Vine por personagem, repetidos nas variantes por enquanto. Auditoria live confirmou **21/21** personagens Marvel com imagem HTTPS; backend `GET /v1/editorial/game-characters/{game_id}` e carregador Android com cache, crédito e link foram implementados na spec 030. Sem URL HTTPS de backend no APK, a UI mostra fallback editorial offline.
- Após a última edição de código, `validate-local.ps1 -Target android` passou, instrumentação AVD **22/22**, backend pytest **45/45** e release build/lint/zipalign/assinatura v2 passaram. APK candidato offline: **5.107.462 bytes**, SHA-256 `9382C92F2228F700FB52EA09DD6A46924F5CC67583CAC62D08BEAE3290F6DAA5`.
- `render.yaml` e guia `docs/11-RENDER-DEPLOY.md` estão preparados. Erick autorizou GitHub/Render; remoto `ErickNeves07/mobile-marvel` foi conectado (apenas LICENSE antes do push). Sem sessão/URL Render; a tentativa de controlar Chrome foi recusada pelo auto-review porque a janela disponível era WhatsApp. Aguarda painel Render aberto por Erick em aba própria. Após deploy, preencher secrets no painel, testar HTTPS e gerar APK com `ORG_GRADLE_PROJECT_riApiBaseUrl`.
- Paridade visual Lovable permanece parcial: a Coleção ainda usa cartões verticais extensos em vez da grade publicada, e Forja/subfluxos precisam de revisão visual. Teste manual em aparelho físico e Groq live também seguem pendentes. O APK acima é candidato de avaliação, não entrega 100% concluída.

## Atualização 2026-10-04 — entrega 2026-10-05

- **Recompensas de campanha concluídas:** Erick retirou `Homem-Aranha +1`, confirmou substituição dos três Estilhaços por missão, valores diferentes e nenhum ajuste retroativo. Aprovou os seis pacotes da spec 028. Android banco v5 persiste créditos/XP e concede quatro Fragmentos da Joia específica de cada missão numa transação idempotente; saldos aparecem no Nexus e a tela de recompensa tem três cartões fiéis ao Lovable. `validate-local.ps1 -Target android` passou; instrumentação **20/20** passou incluindo migração, seis pacotes e captura `specs/028-lovable-economy-parity/campaign-reward.png`. Release assinada candidata: **5.103.690 bytes**, SHA-256 `5F33258F54A79F3E58B08F70D3F85F0404FBC91257588300FC191DC4D8A172D5`. O estágio da Joia para o desafio diário continua Q-040; por isso o desafio ainda dá três Estilhaços.
- **Arte Wolverine:** o prompt completo de `wolverine-origin` está em `docs/07-CHARACTER-ART-WORKFLOW.md`. Erick relatou recusa do gerador no chat GPT. Leonardo.Ai Free continua como teste externo sugerido; imagem Comic Vine não deve ser editada/reproduzida como asset por restrição dos termos da API. Nenhum retrato foi incorporado.

- **Atualização após escolha 2:1 e prêmios Lovable:** Erick confirmou fusão 2:1 apenas para novas operações, sem compensar fusões antigas. Android/strings/teste da spec 028 atualizados; `validate-local.ps1 -Target android` passou e instrumentação **17/17**. Release assinada candidata atual tem 5.098.742 bytes, SHA-256 `2846E13085DBA6987383D01A15D9CC9C8B6C5E1E6FC524792DF8F9044F616954`. Erick escolheu os quatro prêmios exibidos no Lovable; Q-040 ainda pergunta se substituem os três Estilhaços por missão e o significado de `Homem-Aranha +1`. Recompensas não foram alteradas.
- **Arte gratuita:** hardware local Intel Iris Xe integrada (~2 GB reportados), 15,7 GB RAM. Pesquisa oficial aponta Leonardo.Ai Free hospedado com 150 tokens/dia e imagens públicas/licença de uso comercial não exclusiva; guia atualizado em `docs/07-CHARACTER-ART-WORKFLOW.md`. Nenhuma conta, upload ou imagem foi criada. 105 variantes continuam sem retratos.

- **Estado após a última resposta de Erick:** Q-044 resolvida: o trio salvo altera dano/resultado na cena Magneto, pode sofrer derrota e reiniciar; derrota não chama recompensa. `GameRules.teamPower` reutiliza dados authored, e o teste cobre um trio X-Men válido de poder 29. A Manopla ganhou tela nativa com anéis, seis encaixes e ressonância derivados do inventário real; captura comparada à referência Lovable. Guia de obtenção/criação de arte em `docs/07-CHARACTER-ART-WORKFLOW.md`. Uma consulta live revelou e permitiu corrigir URL de detalhe e ID da editora no proxy Comic Vine; backend passou **41/41** e retornou Wolverine com imagem editorial 781 × 1200 px.
- `scripts/validate-local.ps1 -Target android` passou (JVM/lint/debug), `scripts/run-android-instrumentation.ps1` passou **16/16** após a última edição Android e `scripts/build-release.ps1` passou lint/release/assinatura v2. APK candidato atual: `artifacts/Marvel-Ruptura-Infinita-release.apk`, 5.098.650 bytes, SHA-256 `B68BED194E4B8C6F2BE84B4B6C182F5B771640EFD40E2745A94467C2F610952A`. Backend pytest **41/41** após correção e busca real com chave local; sem host público. Sem teste manual no aparelho; AVD mantém ANR do System UI. Ainda faltam 105 artes, paridade visual completa e Q-039/Q-040.
- **Próximo passo executável:** obter a escolha de ferramenta/acesso para produzir uma amostra de arte, resolver compensação de fusões 3:1 antigas e contrato de recompensa, integrar as 105 artes e 2:1 aprovados, configurar/testar backend HTTPS e validar APK em telefone físico ou AVD estável. Não tratar o APK como produto final completo.

- Erick publicou os ajustes. Spec 029 mapeou os bundles públicos novos e trouxe comparação independente das 105 variantes e batalha interativa de quatro rounds da cena X-Men/Magneto ao Android. O time inicial inválido que continha Tempestade foi corrigido; equipes antigas inválidas são reparadas ao abrir a campanha. Q-042 resolvida.
- Na etapa inicial da spec 029, `scripts/validate-local.ps1 -Target android` e instrumentação **14/14** passaram; a matriz foi repetida após a integração da equipe, conforme atualização acima. Dados de atributos conferidos: 21/21 personagens iguais ao bundle Lovable. Backend anterior segue 39/39, sem mudança backend nesta etapa.
- O primeiro APK candidato da spec 029 foi substituído pelo build atual indicado acima; versionCode 2/versionName 0.2.0 foram mantidos.
- O AVD continua com ANR do System UI na frente da atividade. Um teste instrumentado desenhou as views do próprio app para capturas de comparação, batalha e vitória em `reports/lovable-research/`, permitindo inspeção sem a sobreposição. Esse teste percorreu escolhas/especial e conferiu o texto final, mas não substitui toque manual em aparelho físico.
- A referência visual ainda diverge por falta das 105 artes reconhecíveis e outras telas da spec 027. Q-039/Q-040/Q-041 seguem abertas; Q-044 foi resolvida. Backend público/Comic Vine live e aparelho físico continuam não validados.

## Sessão 2026-10-02 — refatoração visual Lovable em andamento

- **Atualização posterior (mesma data):** Erick autorizou adotar mecânicas do Lovable. A regra 2:1 foi confirmada, mas ainda não implementada porque a compensação de fusões antigas 3:1 está em Q-039. O protótipo contradiz sua própria tela de recompensa: exibe quatro prêmios e persiste só créditos/XP; Q-040 pergunta o contrato por vitória. Spec 028 criada. Recompensas atuais ainda são três Estilhaços e sem moeda/XP.
- **Referência renderizada obtida:** Chrome headless com CDP capturou rotas públicas Lovable em 390 × 844, inclusive Nexus, Forja, Campanhas, Coleção, Deadpool, Recompensa, desafio, Manopla, Câmara e Configurações. Q-037 está resolvida para comparação visual. O fonte editável privado segue inacessível.
- **Artes:** amostra oficial Marvel Rivals de Wolverine 1920 × 728 é nítida, mas não cobre as 105 variantes nem demonstrou licença para inclusão no APK. Nenhum asset de terceiro foi incorporado. A geração integrada recusou quatro tentativas; Q-041 aguarda a escolha entre ComfyUI, CLI OpenAI com chave própria/custo ou artes fornecidas. Avaliação em `specs/027-lovable-visual-refactor/art-source-assessment.md`.
- **Android:** foi tentado modo imersivo para alinhar o viewport, mas o AVD entrou em ANR do Pixel Launcher/System UI, inclusive após reinício. A alteração Java foi revertida. `scripts/validate-local.ps1 -Target android` passou após a reversão; inspeção AVD limpa e instrumentação atualizadas continuam pendentes.
- **Próximo passo executável:** receber respostas de Q-039/Q-040/Q-041; fechar spec 028 e implementar 2:1/recompensas conforme contrato; preparar pipeline e 105 artes na ferramenta escolhida; continuar T03–T07 da spec 027 comparando capturas 390 × 844, depois repetir AVD/testes.

- **Estado mais recente:** spec 027 G0/G1/G2 aprovada, implementação parcial em Android Java. Abertura, barra inferior, tokens Barlow Condensed/Sora, painéis angulares, Nexus com anéis/estrelas, Forja com inventário expansível, Campanhas em trilha, Coleção com progresso/busca/filtros e Deadpool em papel foram adicionados. Ainda não é uma cópia exata: grade e detalhe da Coleção, subfluxos e 105 artes precisam ser refatorados. Capturas AVD em `specs/027-lovable-visual-refactor/`.
- A Forja mostra seis encaixes e resumo do inventário real, com detalhes/fusões em painel expansível; Nexus mostra contagem real de Joias completas. O seletor escuro do desafio/campanha teve contraste corrigido. Merge 3:1 e recompensa de três Estilhaços foram preservados; Q-039/Q-040 pedem decisão antes de trocar por números do protótipo.
- `scripts/validate-local.ps1 -Target android` passou após a última edição Java/XML; `connectedDebugAndroidTest` passou **13/13** antes da última mudança estrutural da Coleção. Busca não correspondente ocultou os cartões no AVD; teste positivo por texto ficou inconclusivo devido à digitação instável do emulador. A chave Comic Vine continua somente no `.env` local ignorado pelo Git.
- A ferramenta integrada de geração de imagens recusou quatro tentativas de arte de personagem. Q-041 registra a opção pendente para criar ou obter 105 artes reconhecíveis. A sessão segue sem navegador visual Lovable; Erick recebeu pedido de capturas 390 × 844 ou navegador conectado para G4.
- **Próximo passo executável:** seguir T03–T07 da spec 027 no visual nativo sem alterar mecânicas; incorporar 105 artes após resolver Q-041; comparar lado a lado quando houver referência renderizada; repetir testes e evidências após a última edição.

- Criada `specs/027-lovable-visual-refactor/` em proposta, com requirements/design/tasks/evidence. Nenhum código Android foi alterado: o gate SDD exige spec aprovada e Q-036/Q-037 delimitam escopo e referência.
- O site publicado `https://marvel-ruptura-infinita.lovable.app` respondeu HTTP 200. HTML, CSS e módulos compilados públicos foram analisados em `reports/lovable-research/` (ignorado pelo Git). Identificados base `#05070c`, acentos Nexus/Forja/Deadpool, seis cores de Joias, Barlow Condensed/Sora, painéis angulares e cinco destinos. O fonte editável Lovable não foi obtido; autenticação não foi necessária para a pesquisa pública.
- O leitor web não abriu a URL e a sessão de UI não tem navegador disponível, então faltam capturas renderizadas para comparação exata. O HTML público apontou para o editor do projeto `lovp_252yras1v388ht85hfar7wgg1c`, mas o acesso sem sessão mostrou só o shell genérico, sem fonte. A diferença entre abertura/estado demo Lovable e Forja inicial/regras Android está em Q-036.
- Próximo passo executável: Erick revisa a spec 027, responde Q-036 e fornece projeto/export/capturas para Q-037; então executar T02–T08 em fatias com testes e screenshots AVD.
- Erick esclareceu que quer reprodução **exata** do design Lovable, com única alteração visual indicada: substituir silhuetas por arte reconhecível dos personagens. Spec 027 foi revisada para exigir comparação lado a lado e Q-038 registra a abrangência ainda indefinida da arte (21 personagens ou 105 variantes). O componente público `CharacterArt` usa caminho SVG de silhueta; o Android deve substituí-lo sem perder enquadramento e efeitos de fundo.
- Erick autorizou importar a chave Comic Vine de `Downloads/.env`. Ela foi adicionada ao `.env` raiz ignorado pelo Git sem alterar a entrada Groq. Teste local `/ready` retornou `comic_vine=true`, `groq=true`, `status=ok` após carregar o arquivo no processo; nenhuma chamada live ao provedor ou upload ao Render ocorreu. O backend não carrega `.env` automaticamente ao iniciar sem exportar as variáveis.
- Erick aprovou G0/G1/G2 da spec 027: design, abertura e navegação visual fiéis ao Lovable, sem alterar regras/recompensas nativas sem nova consulta. Exigiu artes distintas para as 105 variantes e autorizou criação quando não houver imagem adequada. Q-036/Q-038 resolvidas; Q-037 fica para verificação visual, pois esta sessão não dispõe de navegador renderizado.

## Fase

Specs 022-026 implementadas e verificadas localmente; spec 027 em implementação parcial. APK assinado 0.2.0/versionCode 2 em artifacts/ antecede a refatoração visual e não a representa. Smoke live Groq falhou com erro sanitizado; chave Comic Vine agora está no `.env` local, sem validação live. Faltam URL pública e acesso Render. Teste em telefone físico pendente.

Última execução (2026-09-28): Android unit tests, lint, debug assemble, Android instrumentation (13/13) e release assinada passaram. A suíte FastAPI passou 39 testes e `pip check` está limpo. Capturas AVD confirmam Forja e tiers bloqueadas antes da ativação. Groq live smoke retorna erro sanitizado. Deploy Render/Comic Vine live e aparelho físico seguem pendentes.

## Decisões ativas

- Catálogo local: `shared/game_catalog.json` alimenta `/v1/game/characters` e o asset Android offline; Coleção mostra 21 personagens e 105 nomes de variantes.

- Android nativo no Android Studio, linguagem principal Java; backend Python/FastAPI.
- Comic Vine permanece atrás do proxy FastAPI; configuração delegada é 100 chamadas por recurso/hora e no máximo uma por segundo, com cache. Chave presente apenas no `.env` local desde 2026-10-02; integração live e respeito aos termos ainda precisam de validação.
- Provedor confirmado: Groq por `GROQ_API_KEY`; rota Deadpool existe. Smoke provider real tentou usar a chave local do `.env`, mas o adapter falhou com erro sanitizado; causa não foi exposta nem confirmada.
- SDD continua obrigatório; a exceção temporária de oito horas foi uma permissão de 2026-09-27, não regra permanente. Manter specs/decisões e não inferir requisitos críticos.
- Shell: cinco destinos em ordem aprovada, abertura visual Lovable que entra no Nexus e barra inferior persistente após a abertura. O valor interno inicial de `AppDestination` ainda é Forja. Nexus, Campanhas, Forja, Coleção e Deadpool têm fluxos locais; spec 027 refatora suas apresentações, ainda sem paridade completa.
- Erick delegou ao agente a direção visual e confirmou importação do projeto no Android Studio.
- Builds Android no Windows usam `android.overridePathCheck=true` e `RI_VALIDATION_DIR`.
- `render.yaml` prepara deploy Render Free, agora declarando Comic Vine/Groq como secrets não sincronizados; `/health` verifica processo e `/ready` informa somente configuração booleana. Não publicado: sem token/URL/remoto Render. O APK offline não depende do host.
- Assinatura release: keystore persistente fora do workspace em `%LOCALAPPDATA%\RupturaInfinita\release-signing`; senha DPAPI só no perfil Windows atual. Rebuild assinado via `scripts/build-release.ps1`; sem backup portátil validado.

## Specs

- `specs/001-project-bootstrap/` — concluída. Matriz original passou; Erick confirmou import visual do Android Studio.
- `specs/002-android-app-shell/` — shell implementado, build/testes/lint aprovados e cinco destinos/perfis exercitados no AVD; TalkBack ativado temporariamente, TTS observado, pronúncia pt-BR ainda sem validação acústica.
- `specs/003-android-design-tokens/` — paleta/tokens implementados, contraste calculado e render AVD inspecionado; build/testes/lint aprovados com quatro avisos conhecidos.
- `specs/004-local-validation-runner/` — runner implementado; alvos `all` e `android` passaram; variáveis do processo foram conferidas como restauradas.
- `specs/005-forge-stone-merging/` — implementada: SQLite transacional, migração v1→v2, idempotência de merge/recompensa, cap 999, confirmação; 7 testes instrumentados passaram e o fluxo foi verificado no AVD. Recompensas ficam sem origem até campanhas/desafios terem conclusão real.
- `specs/006-campaign-catalog-api/` — endpoint local GET `/v1/campaigns` implementado; pytest (4 testes), pip check e schema OpenAPI verificados.
- `specs/007-game-roster-index/` — endpoint `/v1/game/characters` covers 21 names/groups; spec 011 extends it with five variants per character from shared source. Current backend suite: 8 tests.
- `specs/008-campaign-preview-ui/` — destino Campanhas mostra prévias locais de X-Men/Magneto e Quarteto Fantástico; build/lint/tests passam e tela foi inspecionada no AVD em fonte maior e tela compacta.
- `specs/009-nexus-shortcuts/` — Nexus apresenta quatro atalhos que navegam aos outros destinos; 5 testes Android passaram; quatro rotas AVD confirmadas, incluindo Deadpool após rolagem em fonte 1.3 e viewport compacto.
- `specs/010-forge-stone-preview/` — seis Joias/estágios permanecem apresentados pela UI; agora complementada pela spec 005 de inventário/merge funcional.
- `specs/011-game-catalog-preview/` — concluída; JSON compartilhado (21 personagens, quatro grupos, 105 variantes), schema/API backend, asset Android e lista offline; 6 testes Android/8 backend, build/lint/pytest aprovados. AVD conferiu o último cartão em fonte 1.3 e viewport compacto.
- `specs/012-nexus-rift-surface/` — concluída; `RuptureSurfaceDrawable` usa gradientes/acentos dos tokens no hero Nexus apenas. Build/testes/lint/assemble passaram; AVD padrão, compacto e fonte 1.3 conferidos; Forja de controle sem alterações.
- `specs/013-campaign-accent-treatment/` — concluída; cartões X-Men/Quarteto recebem filetes tokenizados gold/cyan, sem efeito em conteúdo/estado. Testes/build/lint passaram; AVD padrão, compacto e fonte 1.3 inspecionados.
- `specs/014-deadpool-comic-surface/` — concluída; `ComicPanelDrawable` aplica geometria de quadrinhos estática somente no hero Deadpool. Testes/build/lint passaram; AVD normal/compacto/fonte 1.3 e Forja de controle inspecionados.
- `specs/015-accessible-variant-rows/` — concluída; cada tier passou a ser um TextView separado. UIAutomator agregou 21 personagens e 105 linhas em 17 posições, com último tier Deadpool; perfis compacto/fonte 1.3 conferidos.
- `specs/016-android-startup-baseline/` — concluída sem alteração de código; cinco cold starts no AVD, `TotalTime` 1664/4510 ms (mediana 3228), `WaitTime` 1686/4544 ms (mediana 3378); `ThisTime` não reportado pela imagem. Valores são observacionais, sem SLO.
- `specs/017-game-catalog-validation/` — concluída; Android/backend rejeitam campos obrigatórios em branco e IDs duplicados; Python testa schema negativo e API preserva JSON de origem. Parser Android compila e asset tem cardinalidades conferidas; parsing inválido não é coberto na JVM por ausência de implementação org.json no cache offline.
- Última matriz em 2026-09-28: Android `BUILD SUCCESSFUL` (testes JVM, instrumented APK, lint e assemble); backend `pip check` limpo e 18 testes. AndroidJUnitRunner direto no AVD: 7/7 instrumentados. UTP Gradle não anexou, contornado pelo runner direto. APK copiado para `artifacts/`. Capturas de confirmação/persistência em `specs/005-forge-stone-merging/`.
- `specs/019-groq-narrative-adapter/` — adapter stdlib com limites/erros sanitizados, 7 testes mockados; sem chamadas externas ou rota FastAPI.
- `specs/020-physical-device-api-hosting/` ? blueprint Render, readiness e cliente Android configur?vel existem; deploy real aguarda acesso/token/URL. API route suite passou 39 testes.
- `specs/021-android-release-signing/` — release 0.2.0/versionCode 2 assinada, zipalign/apksigner/aapt validados; instalação em telefone físico e backup portátil da assinatura pendentes.
- `specs/022-playable-game-loop/` ? loop local de desafio/campanha/batalha/recompensa compilado; unit/build/lint passaram, com instrumentação do repositório em 13/13.
- `specs/023-comicvine-editorial-api/` — proxy Comic Vine implementado; suíte FastAPI passou anteriormente. Chave local disponível, live ainda não testado.
- `specs/024-android-service-integration/` ? cliente Android HTTPS configur?vel; compila??o/testes passaram, host remoto n?o configurado.
- `specs/025-groq-deadpool-api/` ? rota e UI Deadpool; su?te FastAPI passou. Smoke live retorna erro sanitizado sem diagn?stico.
- `specs/026-gauntlet-variant-progression/` ? gate das seis Joias, ativa??o persistente sem consumo, desbloqueios/equipamento local de variantes e UI; build/lint, instrumenta??o 10/10 e capturas AVD aprovados.
- Nesta sessão: `scripts/validate-local.ps1 -Target android` passou após corrigir duplicidade de cases em `GameRules` e variável capturada em `MainActivity`; `scripts/run-android-instrumentation.ps1` passou 10/10; backend `.venv` passou 39 testes e `pip check`. `/ready` testado sem exposição de secrets. `scripts/build-release.ps1` gerou APK assinado 0.2.0/versionCode 2, 4,840,290 bytes, SHA-256 `8DA29343000CB79B00142A07B80D6CEFC73CBA8CFC36F66BAF3D357D217BED0F`. Groq live falhou com erro sanitizado.

## Proximos passos

1. Resolver Q-041: obter uma fonte viável para 105 retratos/variantes distintos e reconhecíveis; depois concluir grade/detalhes da Coleção e procedência por asset.
2. Obter capturas renderizadas 390 × 844 do Lovable ou navegador conectado para comparação exata (Q-037), fechar T03–T08 e repetir testes/acessibilidade após a última edição.
3. Receber decisão de Erick em Q-039/Q-040 antes de qualquer alteração em receita de fusão ou recompensas.
4. Conectar Render e validar Comic Vine/Groq live via HTTPS; gerar APK final configurado e testar no telefone físico. Preparar backup portátil da assinatura (Q-029).

## Bloqueios

- Q-026/Q-032: falta acesso/token e URL do Render; publicacao HTTPS nao ocorreu.
- Q-027/Q-032: chave Comic Vine local disponível, mas sem teste live e sem configuração Render; smoke real Groq falha com mensagem sanitizada e sem diagnóstico.
- Q-028/Q-025: AVD foi validado; nao havia celular fisico conectado.
- Q-029: assinatura preservada no perfil atual; backup portavel recuperavel nao existe.
- Q-030: 0.2.0 e release assinada de teste local, nao versao final do produto; APIs reais e teste fisico pendentes.
- Q-031/Q-033/Q-034/Q-035: verificacoes locais passaram; Manopla/variantes nao alteram combate neste escopo.
- TalkBack/TTS foi exercitado; locale en-US e falta de verificacao acustica limitam a pronuncia pt-BR.

## Ambiente observado

- FastAPI lê catálogo de jogo do arquivo compartilhado; APK usa o mesmo conteúdo como asset offline, sem host backend ou permissão de rede.

- Windows/OneDrive, JBR 21.0.8, Gradle Wrapper 8.13, AGP 8.13.2, SDK 36, AVD `Medium_Phone_API_36.1`.
- Instalação backend limpa previamente verificada em venv com hashes; runner atual voltou a executar `pip check` e pytest com sucesso.
- FastAPI oferece health/readiness, catalogos, proxy Comic Vine e rota Groq. Android possui cliente HTTPS configuravel; sem URL publica, a build usa fallback/local.
- Campanhas e desafio diario sao jogaveis offline e persistidos em SQLite; Comic Vine/Groq continuam opcionais ate configurar backend publico.
- APK release 0.2.0 assinado esta em artifacts/; SHA-256 e instrucoes em RELEASE_NOTES.md. Endpoints live requerem URL/configuracao segura.
- Histórico antes do push atual; veja a atualização mais recente no topo.
