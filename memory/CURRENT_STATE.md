# Current state - 2026-10-05

## Spec 048 - redesign infantil/confortável em branch separada

- Erick confirmou o alcance no app inteiro. A branch `feat/playful-design` parte de `origin/main` em `eee2fee`; a `main` permanece com o design anterior.
- Abertura, Nexus e mapa de nove capítulos foram recompostos com base nas três páginas públicas do Lovable. Paleta, tipografia, cards, botões, navegação e cantos arredondados alcançam Forja, Coleção, batalha, desafio, Deadpool e subfluxos. Retratos continuam via Comic Vine; números e bloqueios vêm do banco local.
- APK experimental identificado como `versionCode=13` / `0.13.0-playful`. Testes Android JVM, lint e debug passaram; instrumentação no emulador **42/42** após a última mudança de UI. Capturas revisadas em `reports/playful-design/` para abertura, Nexus e campanhas nos modos escuro/claro.
- Commit `3f06b71` foi enviado a `origin/feat/playful-design`. `origin/main` permanece em `eee2fee`. Próximo: Erick pode revisar as capturas e, quando quiser, pedir instalação da branch no celular. Não houve merge, deploy Render nem instalação física nesta tarefa.

## Spec 047 - regressões Deadpool, Nexus e recompensa corrigidas localmente

- Causa do Deadpool: o cliente permitia 6.000 caracteres de `game_context`, mas os adapters Gemini/Groq rejeitavam mensagens acima de 4.000. Como o backend acrescenta instruções e fatos, a chamada real caía para a resposta local. Limite agora é 8.000; há teste de rota pelo validador real com Gemini mockado.
- Retry HTTP 422 do app preserva o `context_id` original (`app`), como antes da regressão introduzida em `05170f7`.
- Nexus remove o token `%1$s` da string de acessibilidade. Vitória protege a cena contra callbacks atrasados da animação; o botão indica processamento e volta a ficar ativo em falha. Repetir missão mostra a cena, mas os recursos seguem idempotentes.
- Validação em 2026-10-05: backend `pip check` limpo e **60 testes pytest passaram**; Android `testDebugUnitTest`, `assembleDebugAndroidTest`, `lintDebug` e `assembleDebug` passaram offline usando cache local. Testes instrumentados compilados; execução em aparelho não feita.
- Render precisa receber o backend atualizado para Deadpool usar contexto completo. Nenhuma chamada ao provider, instalação, commit, push ou deploy foi feita nesta sessão. Próximo: Erick faz deploy do backend e solicita instalação quando quiser testar o APK.

## Spec 046 - implemented locally, awaiting deployment

- Restored Reed Richards and Doctor Strange portraits/roles in the Nexus Chamber panel. The one-time Chamber briefing now uses `chamber_briefing_seen_v2`; its replay button is removed.
- New battle claims play the lore scene then show rewards. Duplicate claims open the recorded fragment receipt directly, without replaying the scene or showing new credits/XP.
- Deadpool keeps generated backend text when `fallback=false`, differentiates provider fallback from network failure, and backend logs sanitized Gemini/Groq codes only.
- Battle active hero and shared Super are more prominent. Shared primary buttons and the fragment shop use dark/gold styling in dark mode; light mode retains its prior primary palette.
- Final Android validation passed (`testDebugUnitTest`, `lintDebug`, `assembleDebug`, instrumentation-test compile). Instrumentation runtime was not executed. Backend `pip check` and pytest passed (59 tests). One approved minimal live request earlier returned `fallback=false`.
- Commit `873d55c` was pushed to `origin/main`. APK 0.12.0 was installed on phone `C6OFVWYD4DZTBA5H` with `adb install -r`, preserving app data, and launched successfully (process confirmed). Render deployment was not performed. Next: deploy backend diagnostics and manually verify the Chamber, duplicate receipt, battle, shop, and both themes.

## Specs 043-045 - implementadas e enviadas, aguardando deploy

- Ajustes autorizados e documentados nas specs 043-045. Deadpool: retry legado/contexto curto e fallback local; lore: back conclui a cena e briefing reapresentavel da Camara. Chefes: +6% HP/+8% dano. Loja: Fragmentos por creditos, XP desbloqueia ofertas, DB v8.
- Validacao local 2026-10-05: Android `testDebugUnitTest`, `lintDebug`, `assembleDebug` passaram; backend `pip check` e `pytest` passaram (58 testes). Testes instrumentados no unico AVD compilaram mas nao executaram porque a assinatura instalada difere; os dados do AVD foram preservados.
- Render nao foi acessivel nesta sessao. Confirmar apos deploy que `/ready` responde e `GEMINI_API_KEY` esta configurada para IA online; o fallback local continua funcional sem backend. Nenhuma chamada paga, instalacao no telefone ou deploy foi feita.
- Commits enviados para `origin/main`: `05170f7`, `6ce5c4e`, `ac9c363`, `c5c9fa4`; push concluido. Erick faz deploy; instalacao no celular somente apos nova solicitacao.

## GitHub handoff

- The active-hero battle and expanded Deadpool context changes are in `0e7387c` (`feat(gameplay): add active-hero battles and Deadpool context`). Android compile fix `25db6bf` defines missing `space_5=20dp`. No Render deployment was started.
- Android 0.11.0 was built successfully after defining the missing 20dp `space_5` dimension. `testDebugUnitTest`, `lintDebug`, and `assembleDebug` passed; it was installed and launched on phone `C6OFVWYD4DZTBA5H` (package versionCode 11, versionName 0.11.0). Backend pytest remains unverified because the configured Python venv was unavailable. Local Gradle download residue is untracked in `android-app/.gradle-tmp/` and `android-app/gradle-home/`; it is not part of the remote commit.

## Spec 042 - implementation in validation

- Erick resolved Q-059: free switch, boss damages active hero only, reserve HP persists, HP uses Vida/9, bosses target trio strength, shared super and no action cap. DEC-093 records the rule.
- Android combat now models 3 fighters with independent HP, required replacement after KO, character-specific super moves, attack/defense/control visuals, and recap on victory/reward and defeat.
- Deadpool local context includes current battle, owned characters and variants, authored stats, campaigns, resources, inventory and daily challenge state. Local backend schema accepts 6,000 characters; Render still needs the code deployed for full context (Q-057).
- Targeted Java domain smoke passed switching, HP persistence, active-only damage, shared super after switching, KO/replacement/defeat, counter-based victory and an 8-action battle. Python syntax compilation passed. Full Gradle/backend suites remain blocked by SDK, venv and network access restrictions. No external actions performed.

# Estado atual - 2026-10-05

## Spec 042 — aguardando decisão crítica sobre combate

- Rascunhada `specs/042-active-hero-battle-deadpool-context/`. A auditoria confirma: batalha atual agrega HP/força, encerra em seis rounds; personagem/vida por membro ainda não existem. `VariantStats` já fornece Vida/Ataque/Defesa/Velocidade authored.
- Q-059 pergunta custo de troca, alvo dos golpes e retenção de HP, normalização de Vida e escala de chefes. Sem resposta, nenhuma implementação de combate iniciada.
- Deadpool está parcialmente contextualizado: equipe, progresso, desbloqueios/tiers e próxima missão; não recebe números de atributos, recursos, status do desafio nem estado detalhado da luta. O spec propõe ampliar o payload factual para recomendar equipe e comentar força sem delegar regras à IA.
- **Próximo passo:** aguardar resposta Q-059 e finalizar requirements/design antes do código de combate.

## Spec 041 — narrativa, Câmara, áudio, imagens e desafio diário

- Implementados prompt/UX amplo do Deadpool, sem referência automática à tela; contexto de equipe, coleção e campanhas continua sendo enviado pelo cliente. O Render ainda precisa do deploy do backend que aceita `game_context` (Q-057).
- Imagens Comic Vine começam a ser aquecidas no início: trio inicial, Reed/Estranho/Deadpool, nove chefes e roster. Cache de bitmaps é LRU em memória (20 MiB); metadados são regulados a uma chamada por segundo. Não há persistência dos binários.
- Câmara mostra cartões Comic Vine atribuídos com funções operacionais de Reed e Doutor Estranho. Prólogo aparece antes da primeira entrada no Nexus; cada vitória mostra sua cena; capítulo 9 fecha a ruptura mantendo o gancho da prisão. Desafio diário mostra “?” até a revelação.
- Trilha ambiente nos cinco destinos principais e mais alta em batalha; efeitos de ação e interface aumentados, com o toggle existente.
- Compilação local não validada: Wrapper baixou Gradle 8.13, mas AGP 8.13.2 não pôde ser resolvido porque as conexões a Google Maven, Maven Central e Plugin Portal falharam com `SocketException: Permission denied`. A saída foi redirecionada para `.validation-output/spec041`; `--no-problems-report` contornou a gravação negada no diretório de build do OneDrive. Nenhum teste foi executado nesta sessão.
- Próximo passo executável: repetir `scripts/validate-local.ps1 -Target android` com cache de dependências acessível e destino de build local, revisar o APK resultante e, depois do deploy Render, Erick testar IA. Não foi feito push/deploy ou instalação nesta tarefa.

# Estado anterior - 2026-10-04

- **2026-10-05:** APK debug 0.9.0 instalado por cima de 0.8.0 no telefone `C6OFVWYD4DZTBA5H` com `adb install -r`, sem limpeza de dados. `dumpsys` confirmou `versionCode=9`/`versionName=0.9.0`; MainActivity abriu, processo ativo e nenhum erro fatal no log consultado. Render ainda expõe `DeadpoolLineRequest` apenas com `context_id,prompt`, portanto precisa de deploy do commit `5769b41` para contexto pleno.

- Spec 040 concluída localmente: Deadpool deixa de usar atalhos fixos Magneto/X-Men, recebe missão/equipe/variantes/progresso reais; avisos das nove batalhas agora correspondem a cada adversário. Contrato backend local `game_context` e fatos authored incluem Thanos.
- Android 0.9.0 passou JVM/lint/debug/release, 34/34 testes instrumentados no AVD; backend 58/58. Release assinada v2 instalou e abriu no AVD. APKs e hashes em RELEASE_NOTES.
- GET do Render ainda mostrava `DeadpoolLineRequest` apenas com `context_id,prompt`; revisão do backend precisa chegar ao serviço. POST pago foi rejeitado pela revisão automática por falta de autorização específica. O celular físico não estava conectado, portanto 0.9.0 não foi instalado nele.
- Commit `0829bd6` enviado a `origin/main`. GET após o push ainda mostrava contrato antigo no Render; não havia sessão de navegador disponível para acionar o deploy manual. Próximo passo: publicar esse commit no Render, verificar `game_context` no OpenAPI e, com autorização específica para uma chamada de IA, testar Thanos/equipe. Conectar o celular para instalar 0.9.0 e testar manualmente a resposta.


- Spec 039 concluida localmente: efeitos sonoros de interface, musica original discreta em batalhas, feedback mais visivel, Forja simplificada e mensagens de batalha mais claras sem mexer nas regras.
- Deadpool estava enviando game_context para um Render cujo OpenAPI ainda rejeita campos adicionais (contrato legado). Android 0.8.0 tenta de novo sem o campo em HTTP 422; contexto dinamico depende de atualizar o backend publicado.
- Gradle unit/lint/build passaram. Instrumentacao completa passou 33/33 no APK final.
- APK local: artifacts/Marvel-Ruptura-Infinita-debug-0.8.0.apk. Nao houve instalacao no telefone, push ou deploy.
- Smoke POST pago segue pendente de autorizacao; /ready e /openapi foram consultados somente por GET.
- Proximo passo: Erick pode instalar/testar o APK 0.8.0; depois publicar o backend para contexto completo do Deadpool. Somente fazer POST de smoke depois da autorizacao.

# Estado atual

Última atualização: 2026-10-04

## Spec 038 - splash de abertura - 2026-10-04

- Splash nativa AndroidX 1.2.0 com tema Starting API 26+, vetor animado da ruptura, seis gemas e pulso ciano/ouro; saída de 220 ms e remoção direta com animações do sistema desligadas. A tela de introdução existente e os contratos do app foram mantidos.
- `testDebugUnitTest`, `lintDebug`, `assembleDebug`, `assembleDebugAndroidTest` passaram; instrumentação AVD **33/33**. Captura AVD em `reports/lovable-research/app-splash-0.7.0.png`.
- APK debug 0.7.0: `artifacts/Marvel-Ruptura-Infinita-debug-0.7.0.apk`, 6.249.992 bytes, SHA-256 `ACA37C4BB278ED6EB64ABBA886BC3B6BDBE8E623DB56D526302ABA81FD90C9AA`.
- Erick pediu instalação limpa no telefone conectado. Após o HyperOS rejeitar a primeira tentativa de instalação isolada, `adb install -r` concluiu; dados locais foram apagados pela desinstalação anterior. Telefone `C6OFVWYD4DZTBA5H`: versionCode 7/versionName 0.7.0, processo ativo, sem fatal exception no log consultado.
- Erick informou que publicou o backend. Nenhum push/deploy novo foi feito nesta tarefa, que só altera o cliente Android.

## Spec 037 - acabamento visual, Deadpool e curiosidades - 2026-10-04

- Último ajuste da Forja: a animação de Joia completa ganhou painel central, luvas, gema colorida com brilho e convergência animada; captura visual conferida em `reports/lovable-research/forge-merge-0.6.0.png`. APK debug 0.6.0 recompilado: 6.334.722 bytes, SHA-256 `E37023573C9C7D00A13EB0251CE2F4F4EE7FF255F43DD49D88A3D1283D8221E9`.
- Após essa edição, `testDebugUnitTest`, `lintDebug`, `assembleDebug`, `assembleDebugAndroidTest` e instrumentação AVD **32/32** passaram. Backend tinha passado **57/57** na validação da spec; backend não sofreu alteração posterior.
- Implementados cards clicaveis de desbloqueio/evolucao com feedback visual e abertura do detalhe do personagem na Colecao. Detalhes de variante agora tem abas Variantes/Curiosidades; cards owned equipam no toque. Forja mostra receitas sem o expansor antigo; fusoes sao imediatas e animadas; regra transacional 2:1 mantida.
- Adicionado modo claro/escuro persistido (escuro inicial), controles de tema e som no topo; efeitos sinteticos de batalha podem ser desligados. Eventos visuais por acao ficam abaixo do especial. Dicas de combate sao indiretas e ha evento narrativo/visual de armadilha sem alterar dano, escolhas ou dificuldade.
- Deadpool recebe ate 1.200 caracteres de estado local limitado: personagens/tiers possuidos, equipe e variantes equipadas na batalha atual, capitulo/round e batalhas concluidas. Retrato alterna capas por variante. Comic Vine agora fornece numero de aparicoes e data da primeira edicao como campos opcionais.
- Removidos botao de detalhes da Forja e bloco de configuracao editorial no Catalogo. Backend 57/57; Android unit/lint/build passaram; AVD instrumentado 32/32. APK debug 0.6.0 em artifacts/Marvel-Ruptura-Infinita-debug-0.6.0.apk, SHA-256 D261B0BDCC995D1088144843B18E80491D812A9CEB593059BD86634A66B5D0AB.
- Backend alterado localmente; publicar esta revisao no Render antes de usar o contexto do Deadpool e as curiosidades novas. APK está atualizado em artifacts. Nenhum push/deploy ou instalação física ocorreu nesta etapa.

## Instalação física 0.4.0 após deploy Gemini — 2026-10-04

- Erick informou que fez o deploy e pediu para rodar no mesmo celular. Render `/ready` agora tem `gemini=true`, `comic_vine=true`, `groq=true`; POST real de Deadpool retornou HTTP 200, `fallback=false` e 202 caracteres, sem expor texto/chave. O app foi marcado `versionCode 4`/`0.4.0`; `testDebugUnitTest`, `lintDebug` e `assembleDebug` passaram. APK debug verificado em `artifacts/Marvel-Ruptura-Infinita-debug-0.4.0.apk`, SHA-256 `DE07D522FCB45E0B050C01C83E90A6ADA6BD95A47C344601C9A4B3FE0F51156E`.
- O telefone `C6OFVWYD4DZTBA5H` estava conectado mas não listava o pacote anterior, então a instalação foi nova (`firstInstallTime=2026-10-04 17:52:01`). `adb install -r` respondeu `Success`; `am start` abriu `MainActivity`, `dumpsys` confirmou 0.4.0 e o processo permaneceu ativo sem exceção fatal. Captura física em `reports/lovable-research/phone-0.4.0.png` mostra Campanhas com retratos carregados de Magneto e Sentinelas. O Android do aparelho bloqueou `adb shell input tap` (`INJECT_EVENTS`), impedindo acionar Deadpool por automação; testar manualmente a aba no telefone.
- Esta instalação atende ao pedido imediato de rodar a revisão atual no celular. As regras Q-048–Q-051 ainda aguardam resposta e não fazem parte do APK 0.4.0.

## Atualização 2026-10-04 — IA Gemini, combate tático e filtro

- A rota Deadpool no Render retornou `fallback=true` numa chamada real. A chave Groq local retornou HTTP 403; a chave Gemini de `Downloads/.env` funcionou com `gemini-3.5-flash-lite` (HTTP 200). O backend agora prioriza Gemini, tenta Groq e só então usa fallback. Testes backend **53/53** passaram; chamada local retornou `fallback=false`. Código foi enviado a `origin/main` em `970c5c6` sem segredo. O Render ainda mostra `/ready` antigo, sem campo `gemini`; Erick precisa configurar `GEMINI_API_KEY` no painel privado e publicar a revisão para validar a IA no telefone.
- O Android tem filtro **Desbloqueados** no Catálogo, especial authored conforme a equipe e seis batalhas com intenções anunciadas, seis turnos, derrota possível e dificuldade crescente. Defender continuamente perde. `testDebugUnitTest`, `lintDebug`, `assembleDebug`, `assembleDebugAndroidTest` e **27/27** testes instrumentados no AVD passaram após a última edição. Commit `018145b` foi enviado a `origin/main`. O celular físico não recebeu este build parcial.
- Regras pendentes Q-048–Q-051: substituição dos quatro Fragmentos por Manopla completa, mapa final de capítulos, consumo exato dos itens e aceitação de capas de edições como imagem distinta. A pergunta consolidada foi enviada ao Erick. Até a resposta, não alterar economia, banco de progressão ou representação editorial. A instalação final no mesmo aparelho `C6OFVWYD4DZTBA5H` permanece após essas mudanças, testes e incremento de versão.

## Spec 032 — recuperação de gameplay e design (em andamento, 2026-10-04)

- Erick tentou instalar e recebeu 0.1.0 porque havia dois APKs antigos nas saídas de build dentro do projeto; ambos foram identificados por `aapt` e removidos individualmente. Telefone `C6OFVWYD4DZTBA5H` foi atualizado para debug 0.3.0 com `adb install -r`, sem limpar dados (`firstInstallTime` preservado). A nova navegação abriu e a frase antiga não apareceu. APK debug normal sem `testOnly` está em `artifacts/Marvel-Ruptura-Infinita-debug-0.3.0.apk`, 6.202.280 bytes, SHA-256 `BB7A14F48B5407AEE3B81E4F586D809C424DE34EA6B1EE264D0FCDC978D4E639`. Próximo teste físico: imagens, batalha, merge e variantes; não rodar instrumentação mutável no aparelho.

- O Android agora inicia um novo banco v6 com **Homem-Aranha, Wolverine e Tocha Humana** possuídos na Origem. Migração preserva variantes já possuídas; a regra para adquirir os outros 18 depende de Q-045. Coleção raiz mostra 21 personagens gerais; detalhe mostra cinco variantes, atributos de jogo e fatos editoriais Comic Vine.
- As seis missões com recompensas já aprovadas aparecem como seis capítulos individuais. Cada capítulo abre escolha visual de três personagens possuídos de qualquer facção; o poder da variante equipada altera o combate. Há quatro rounds, decisões, efeitos, derrota/nova tentativa e recompensa só na primeira vitória. A possível substituição pelos nove capítulos do Lovable depende de Q-046; não foram inventados três pacotes extras.
- Forja mostra receitas 2:1 disponíveis sem expandir detalhes; botão debug que criava itens foi removido. Personagem inicial pode ativar Manopla completa a partir do detalhe e subir para o próximo patamar; teste instrumentado confirmou Wolverine Ascensão sem consumir a Joia.
- Retratos usam Comic Vine via proxy, com ajuste de limite para o Homem-Aranha de 8,29 MB, pré-carregamento de trio/Reed/Estranho, alinhamento central e retry. AVD live verificou Homem de Ferro, Homem-Aranha e Tocha Humana; Nexus capturado com Reed/Estranho carregados. Backend tem nova rota para oponentes, ainda não publicada no Render. Desafio diário ganhou tela própria fiel à hierarquia Lovable, palpite vazio obrigatório e fala visual. Deadpool usa modelo padrão novo porque o anterior foi descontinuado; geração real ainda não testada devido Q-047.
- Após última edição de gameplay/UI/teste, backend **47/47**, Android JVM/lint/debug e instrumentação AVD **27/27** passaram. AVD segue com ANR do System UI para toque manual, mas a captura direta das views funcionou. `versionCode 3`/`0.3.0` passou build release, lint release, zipalign e assinatura v2: APK 5.119.606 bytes, SHA-256 `C948C701A437BB85EDC32C2529AD4C06AED72C1B126B6C5F966785514F512CA5`. Telefone físico estava desconectado. O código Android/backend foi enviado ao GitHub em `37a78d2` e `afa518f`; a rota nova do backend ainda retornou 404 no Render após o push.
- Corrigido o build comum do Android Studio: sem `-PriApiBaseUrl`, debug e release usam `https://mobile-marvel-8qex.onrender.com` (confirmado em `BuildConfig.java`). Matriz direta sem propriedade passou JVM/lint/debug e **27/27** instrumentados; release assinada repetida sem override e manteve o mesmo hash. Próximos passos: enviar esta última correção/documentação ao GitHub, verificar Render após deploy, instalar debug sobre 0.2.0 no telefone quando conectado, obter respostas Q-045/Q-046 e autorização específica para POST Groq se desejada.

## Telefone físico atualizado de 0.1.0 para 0.2.0 — 2026-10-04

- A tela “Fundação Android pronta para a próxima ruptura” vinha do APK `0.1.0` ainda instalado no aparelho, mesmo após o build local do projeto `0.2.0`. A assinatura desse APK antigo corresponde à chave debug local; a release usa outra assinatura.
- Gerei debug `0.2.0` com URL HTTPS do Render e instalei por cima via `adb install -r`, sem desinstalar o app. O aparelho confirmou `versionCode` 2, `MainActivity` abriu, e UIAutomator leu Coleção, filtros e cinco abas; a frase antiga sumiu. Teste completo de gameplay e retratos no telefone ainda não foi feito.
- Build Project no Android Studio só compila; para atualizar o celular, usar Run `app`. Manter a assinatura debug para futuras atualizações que preservem dados; instalar a release assinada por outra chave exige fluxo de migração ou desinstalação.

## Correção de build direto no Android Studio — 2026-10-04

- Erick reportou `AccessDeniedException` em `:app:generateDebugBuildConfig` na saída `android-app/app/build` sincronizada pelo OneDrive. A spec 031 mudou o build Gradle comum para `%LOCALAPPDATA%\RupturaInfinita\gradle-builds\<id-do-checkout>`, preservando `RI_VALIDATION_DIR` para scripts existentes. A pasta antiga foi mantida intacta.
- O Wrapper direto, sem init script, passou testes JVM, lint e assemble debug; BuildConfig e APK apareceram na saída local. Validador Android e release assinada também passaram. APK release com Render manteve SHA-256 `3D6FFAA8FFE241737B5BFC01C0929E1BB03A9495EDD8D597F7FD49AD0C232ACA`.
- Próximo passo: sincronizar o Android Studio e repetir Run no telefone. A instalação/interação em aparelho físico segue Q-028; usar o APK release assinado para testar o backend publicado.

## Atualização mais recente — Render publicado e APK online

- Erick definiu **1 Fragmento da Joia do dia** na primeira vitória diária, sem retroatividade. Banco concede de forma idempotente e transacional; testes cobrem persistência e limite 999. As seis recompensas de campanha e fusão 2:1 prospectiva continuam conforme spec 028.
- Erick aprovou usar retratos Comic Vine por personagem, repetidos nas variantes por enquanto. O backend `https://mobile-marvel-8qex.onrender.com` respondeu `/health=ok`, `/ready=ok`, catálogo 21 personagens/duas campanhas e **21/21** rotas de retrato com game ID/fonte/host corretos. AVD com essa URL mostrou Homem de Ferro na Coleção e Wolverine/Professor Xavier na Comparação, com crédito e link; capturas em `specs/030-comic-vine-character-art/`.
- A Coleção agora usa 105 cards ordenados por patamar, grade de duas colunas, destaque a cada cinco, filtros de grupo/patamar e detalhe com cinco variantes, preservando desbloqueio/equipamento. O acesso à Comparação está no cabeçalho. Teste AVD percorreu filtros, detalhe, retorno e Comparação; capturas locais com retratos foram conferidas contra Lovable, ainda com diferenças de proporção/composição.
- Após a última edição de produção/teste, `validate-local.ps1 -Target android` passou, instrumentação AVD online **22/22**, backend pytest **45/45** e release build/lint/zipalign/assinatura v2 passaram. APK candidato online: **5.111.366 bytes**, SHA-256 `3D6FFAA8FFE241737B5BFC01C0929E1BB03A9495EDD8D597F7FD49AD0C232ACA`; URL pública confirmada no DEX. Os testes online cobrem retrato real na Coleção e nos dois lados da Comparação.
- Erick autorizou GitHub/Render; implementação e evidência textual enviadas a `origin/main` até o commit `e67b1ac`, sem `.env`, APK, keystore ou capturas que reproduzem retratos editoriais. Render Web Service foi criado por Erick; os secrets aparecem configurados em `/ready`, mas a resposta real do Groq ainda não foi validada. `render.yaml` e `docs/11-RENDER-DEPLOY.md` documentam a configuração manual.
- Paridade visual Lovable permanece parcial nas proporções da Coleção, Forja e subfluxos. Teste manual em aparelho físico e Groq live também seguem pendentes. O APK acima é candidato de avaliação, não entrega 100% concluída.

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

## Implementação das nove campanhas e ciclo completo da Manopla — 2026-10-04

- Erick confirmou Fragmentos faltantes como prêmios e fusões manuais; três Fragmentos iniciais por Joia somente em instalações novas; capas Comic Vine para variantes; seis pares de créditos/XP originais e três novos pares progressivos. Confirmou que não havia usuários anteriores, portanto não há migração de progresso de campanhas.
- Android: nove campanhas em ordem Lovable, poder recomendado exato por capítulo, cards compactos de mapa e ficha separada. Manopla exige seis Joias completas e desbloquear/evoluir consome uma de cada, atomicamente. Banco novo recebe 3 Fragmentos por Joia; bancos existentes não recebem.
- Backend: rota editorial de capa específica por variante. Testes locais validam Comic Vine. Ela só estará disponível no app após o deploy do backend Render.
- Verificações: backend 56/56; JVM/lint/build Android passaram; instrumentação AVD 32/32. Captura reports/lovable-research/campaign-nine-android.png. APK debug 0.5.0 instalado diretamente, tamanho 6,213,152, SHA-256 6F38679B635C71D1F5E2CCC60AD9FB8D2EDABE1132BD14F68EC24996876E1CDF.
- Próximo: conferir diff, commit/push autorizado; Erick faz deploy Render; após confirmação, verificar a rota pública e instalar APK 0.5.0 no mesmo celular.
## Publicação GitHub — 2026-10-04

- Commit de implementação 49c535e enviado com sucesso a origin/main no repositório mobile-marvel.git. Arquivos .env, APK e capturas ficaram fora do commit.
- Erick deve publicar o serviço do Render para ativar as capas das variantes e das nove batalhas. Após confirmação, verificar a API pública e instalar o APK 0.5.0 no mesmo celular; nenhum APK foi instalado no telefone nesta etapa.
- APK local: artifacts/Marvel-Ruptura-Infinita-debug-0.5.0.apk, 6,213,152 bytes, SHA-256 6F38679B635C71D1F5E2CCC60AD9FB8D2EDABE1132BD14F68EC24996876E1CDF.
