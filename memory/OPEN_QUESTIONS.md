# Dúvidas abertas

## Atualização do deploy e aparelho — 2026-10-04

- **Q-047 — Resolvida para o backend:** Erick fez deploy e `/ready` passou a mostrar Gemini ativo. POST hospedado do Deadpool retornou `fallback=false`. A interação na tela do telefone ainda requer toque manual porque o Android bloqueou `adb shell input tap` com `INJECT_EVENTS`.
- **Q-028 — Instalação 0.4.0 verificada:** o mesmo celular `C6OFVWYD4DZTBA5H` recebeu e abriu o APK debug 0.4.0. O pacote anterior não estava instalado quando a revisão foi aplicada; os dados locais antigos não estavam presentes. Captura física mostra Campanhas e imagens editoriais.

## Specs 033–035 — atualização 2026-10-04

- **Q-045 — Resolvida em 2026-10-04:** após Manopla completa, escolher um personagem novo ou a próxima variante; cada opção consome as seis Joias Completas.
- **Q-047 — Diagnóstico atualizado:** a chamada real autorizada à rota publicada do Deadpool retornou `fallback=true`. Uma chamada Groq local com a chave existente retornou HTTP 403; a chave Gemini isolada em Downloads listou modelos e respondeu a `gemini-3.5-flash-lite` com HTTP 200. O adapter Gemini local já produziu `fallback=false`; Render ainda precisa receber `GEMINI_API_KEY` no painel privado.
- **Q-048 — Resolvida em 2026-10-04:** vitórias de batalha e do desafio diário entregam somente Fragmentos faltantes das seis Joias; créditos/XP de campanha seguem valores aprovados.
- **Q-049 — Resolvida em 2026-10-04:** substituir as seis batalhas por nove capítulos em ordem Lovable.
- **Q-050 — Resolvida em 2026-10-04:** cada desbloqueio consome uma Joia Completa de cada tipo e preserva componentes intermediários.
- **Q-051 — Resolvida em 2026-10-04:** capas de edições Comic Vine aprovadas como imagens distintas para as variantes, com crédito editorial.

## Spec 032 — revisão física de gameplay e design (2026-10-04)

- **Q-045 — Resolvida em 2026-10-04:** após Manopla completa, escolher um personagem novo ou a próxima variante; cada opção consome as seis Joias Completas.
- **Q-046 — Resolvida em 2026-10-04:** adotar os nove capítulos publicados no Lovable como batalhas individuais, em ordem.
- **Q-047 — Teste Groq live:** a revisão automática rejeitou o POST de smoke em `/v1/ai/deadpool-line` por poder gerar cobrança sem autorização explícita para a chamada paga. `/ready` só prova presença da chave. Implementação e testes mockados podem avançar; o teste real depende de autorização específica após a correção estar pronta.

## Spec 029 — novos ajustes Lovable (2026-10-04)

- **Q-042 — Resolvida em 2026-10-04:** Erick publicou a versão nova. Bundles `batalha-BBpUQYMu.js`, `comparar-Dy7nbkjE.js` e `characters-BL6XEGoz.js` inspecionados; capturas móveis atualizadas. Contrato mapeado na spec 029.
- **Q-044 — Resolvida em 2026-10-04:** Erick confirmou que a equipe escolhida influencia o resultado e aprovou derrota com nova tentativa. Spec 029 implementa dano conforme poder authored do trio salvo; derrota não libera especial nem recompensa.

## Entrega 2026-10-05

- **Q-043 — Validação visual no dispositivo:** build/lint, backend **41/41** e Android instrumentado **16/16** passaram em 2026-10-04, mas o smoke navegável foi impedido por ANR recorrente do System UI do AVD mesmo após reinício e reversão de modo imersivo. Capturas instrumentadas verificaram as views de batalha/Manopla; validar toque manual em AVD estável ou aparelho físico antes de prometer navegação completa. APK assinado atual é candidato de avaliação, não produto completo.

## Spec 027 — refatoração visual Lovable (2026-10-02)

- **Q-036 — Resolvida e ampliada em 2026-10-02:** Erick aprovou reprodução fiel do design e, depois, autorizou adotar o que está no Lovable também para regras. Contradições e lacunas de recompensa não são decisões inferíveis; ver spec 028/Q-040.
- **Q-037 — Resolvida para referência visual pública em 2026-10-02:** Chrome headless com emulação móvel capturou Nexus, Forja, Campanhas, Coleção, Deadpool e subfluxos em 390 × 844. Editor/fonte Lovable seguem indisponíveis, mas o site publicado agora pode ser comparado visualmente. Capturas locais em `reports/lovable-research/`.
- **Q-038 — Resolvida em 2026-10-02:** todas as 105 variantes precisam de artes distintas com visual reconhecível do personagem. Se não houver imagem adequada/utilizável, o agente pode criar a ilustração. Registrar procedência e não usar imagens Comic Vine como assets estáticos do jogo em desacordo com os termos.
- **Q-039 — Resolvida em 2026-10-04:** Erick confirmou aplicar 2:1 daqui em diante, sem compensação por fusões antigas 3:1. Regra/textos/testes atualizados na spec 028; saldos e IDs persistidos são preservados.
- **Q-040 — Resolvida em 2026-10-04:** Erick confirmou os seis pacotes de campanha da spec 028 e escolheu exatamente um Fragmento da Joia determinística do dia por primeira vitória diária; sem ajuste retroativo. Implementado e validado em testes Android.
- **Q-041 — Resolvida para a entrega de 2026-10-05:** Erick dispensou temporariamente as 105 artes distintas e aprovou retratos Comic Vine por personagem, repetidos nas cinco variantes quando necessário. Os 21 IDs Marvel foram verificados e a spec 030 implementou proxy, UI, crédito e fallback. O host HTTPS e a inspeção visual das imagens reais no APK ainda faltam; geração futura permanece opcional.

Não implemente áreas afetadas até resposta quando marcadas como **CRÍTICA**.

- **Q-001 — Adiada, não bloqueia desenvolvimento:** Erick esclareceu que o anonimato vale somente para o vídeo. A data/hora de entrega não é necessária para trabalho local; obter quando for preparar o cronograma final.
- **Q-002 — Resolvida em 2026-09-28:** anonimato obrigatório somente no vídeo; o app/pacote/metadados não precisam ser anônimos por este requisito.
- **Q-003 — Resolvida sob declaração do usuário em 2026-09-28:** Erick declarou ter autorização para usar qualquer imagem encontrada. Para cada asset incorporado, registrar URL de origem, autoria e licença/termo aplicável; esta declaração não altera as restrições próprias de Comic Vine sobre seus dados.
- **Q-004 — Resolvida em 2026-09-28:** manter Comic Vine atrás do proxy FastAPI, como já decidido em DEC-003. Chamadas diretas pelo APK exporiam a chave no aplicativo e dificultariam cache, filtro e controle central de uso.
- **Q-005 — Resolvida em 2026-09-28 por decisão delegada:** limitar o proxy a 100 chamadas por hora por recurso e no máximo uma chamada por segundo, com cache e respeito a 429/indisponibilidade. Isso deixa margem abaixo do limite oficial publicado de 200 chamadas por recurso por hora e da detecção de velocidade. Rever se Comic Vine mudar os termos.
- **Q-006 — Resolvida em 2026-09-28:** usar Groq com a variável local `GROQ_API_KEY`, conforme confirmação explícita de Erick. A chave Gemini colada no chat não deve ser reutilizada e deve ser revogada.
- **Q-008 — Resolvida por delegação em 2026-09-28:** priorizar Homem de Ferro, Homem-Aranha, Wolverine, Mulher Invisível, Surfista Prateado e Deadpool, cobrindo os quatro grupos e o destino Deadpool. A coleta de assets deve guardar fonte e licença por arquivo.
## Resolvidas em 2026-09-27

- **Q-015 — Resolvida em 2026-09-27:** os executáveis locais foram acessados e usados sem instalar ferramentas. O wrapper inválido foi recomposto dos JARs Gradle 8.13 já disponíveis; atribuições `set` do `gradlew.bat` foram protegidas contra o `&` em `J&F`; saídas Android foram isoladas para contornar bloqueios do OneDrive. `.venv` e Git também foram acessados, e as verificações passaram. Nenhuma instalação, download, autenticação, chamada externa ou commit ocorreu.

- **Q-007:** `minSdk` 26, Android 16/API 36 como referência, smartphones portrait, telas compactas/grandes de telefone, referência 390 × 844 dp.
- **Q-009:** namespace/application ID `com.erickbarbosa.rupturainfinita`.
- **Q-010:** `venv` + pip com lock.
- **Q-011:** autorizada consulta somente a documentação oficial para seleção de versões.
- **Q-012:** autorizado o plano operacional exato: Python 3.13.15 side-by-side, `.venv`, dependências Python fixadas e downloads Gradle/Maven; sem mudanças globais silenciosas.
- **Q-013:** opção A — primeiro ciclo limitado a smartphones/janelas com `smallestWidth < 600dp`, portrait; tablets e foldables abertos ficam fora do escopo inicial.
- **Q-014:** autorizada instalação do Python 3.13.15 64-bit por usuário, lado a lado e sem alterar PATH/associações globais.

## Pendências técnicas de validação (não são dúvidas de produto)

- **TalkBack falado:** nós, rótulos, estado selecionado e bounds foram inspecionados via UIAutomator; ainda não foi realizado ensaio com o serviço TalkBack falando. Não bloqueia shell MVP 0.
- **Git fora do workspace:** leitura do Git pelo processo com sandbox restrito foi negada; nenhuma operação Git foi necessária para concluir as specs locais. Não houve commit.
- **Encoding do validador no terminal:** a sessão exibiu alguns acentos como mojibake apesar do script UTF-8 funcionar e passar. Conferir em outro host PowerShell antes de tratar como defeito de produto.

- **Q-025 — Parcialmente resolvida em 2026-09-28:** Erick confirmou que a conexão deve funcionar também em dispositivo físico. Configurar builds locais para AVD e dispositivo; o host físico deve ser configurável para a rede de desenvolvimento, nunca embutido como endereço de produção. Endereço LAN e condições de rede serão fornecidos/confirmados ao executar o teste em dispositivo; não bloqueiam o desenho local.

- **Q-026 — Resolvida em 2026-10-04:** Erick criou o Web Service Render `https://mobile-marvel-8qex.onrender.com`. `/health=ok`, `/ready=ok`, 21/21 rotas editoriais e catálogos hospedados passaram. APK assinado foi reconstruído com essa URL e testado no AVD; commit/push da evidência final ainda será registrado.
- **Q-027 — Parcialmente resolvida em 2026-10-04:** a chave Comic Vine permanece no `.env` local ignorado pelo Git. Busca e detalhes live confirmaram 21 personagens Marvel com imagem HTTPS; a rota Wolverine respondeu 200. Render ainda precisa receber segredo no painel privado.
- **Q-028 — Parcialmente resolvida em 2026-10-04:** telefone físico conectado recebeu atualização debug `0.1.0` → `0.2.0` com `adb install -r`; Coleção e navegação foram observadas via UIAutomator e a tela antiga sumiu. Fluxos de campanha/batalha/Forja, retratos HTTPS e Deadpool ainda precisam de teste físico completo. Progresso local SQLite não migra automaticamente entre aparelhos; trocar assinatura debug por release também não preserva dados por instalação direta.
- **Q-029 — Backup/portabilidade da assinatura:** a chave release atual está segura neste perfil Windows e permite reconstruir o APK por `scripts/build-release.ps1`, mas a senha está protegida com DPAPI e não existe backup recuperável noutro perfil/máquina. Antes de migrar/reinstalar Windows, é necessário preparar um backup protegido portável e validar a restauração; até lá preservar keystore e perfil atuais.
- **Q-030 - Release final completa do briefing ainda nao existe:** APK assinado 0.2.0 inclui loop jogavel local, Forja e Manopla/variantes; build/testes locais passaram. Comic Vine key/live, Groq live (smoke sem sucesso), URL publica/deploy e teste em aparelho fisico ainda pendentes. Nao apresentar a release de teste como final/completa do briefing.

- **Q-031 — Resolvida para validação local em 2026-09-28:** SDK/Gradle acessados por execução elevada já autorizada; Python 3.13 venv executou a suíte completa.
- **Q-032 — Parcialmente resolvida:** Comic Vine foi validada localmente e no Render em 21/21 vínculos; AVD carregou retratos reais na Coleção e Comparação. `/ready` confirma presença booleana das chaves no Render, sem expor valores. A chamada Groq real ainda não foi validada após o deploy; conectividade/aceitação permanece pendente. Teste em telefone físico segue Q-028.
- **Q-033 — Resolvida localmente em 2026-09-28:** spec 026 implementa Manopla/Câmara e progressão persistente, validada por build/lint, 10 instrumented tests e capturas AVD. Efeito de tier em stats de combate segue fora do escopo.
- **Q-034 — Resolvida para build local em 2026-09-28:** `testDebugUnitTest`, `assembleDebugAndroidTest`, `lintDebug` e `assembleDebug` passaram; APK release foi reconstruído/assinado. Teste físico não ocorreu.
- **Q-035 — Resolvida para suíte backend em 2026-09-28:** venv Python 3.13 acessível; `pytest -q` passou 39 testes e `pip check` não encontrou dependências quebradas.

## Spec 002 — Shell de navegação Android

- **Q-016 — Resolvida por Erick em 2026-09-27:** destinos na ordem Nexus, Campanhas, Forja, Coleção e Deadpool; Forja é o destino inicial.
- **Q-017 — Resolvida por Erick em 2026-09-27:** cada destino deve cumprir seu papel e requisitos; nesta fatia os painéis podem ser placeholders. Funcionalidade completa pertence a specs próprias.
- **Q-018 — Resolvida por Erick em 2026-09-27:** escolha do padrão delegada ao agente; adotada barra inferior como solução apropriada a cinco destinos em telefone portrait.
- **Q-019 — Resolvida por Erick em 2026-09-27:** identidade visual e tokens delegados ao agente; a direção será definida e documentada pelo Visual/UX Director, respeitando a direção cinematográfica + quadrinhos e os requisitos de acessibilidade.

## Spec 005 — Merge das Joias

- **Q-020 — Resolvida em 2026-09-28:** proporção uniforme 3:1 em cada transição para todas as seis Joias: 3 Estilhaços → 1 Fragmento; 3 Fragmentos → 1 Núcleo Instável; 3 Núcleos Instáveis → 1 Joia Completa.
- **Q-021 — Resolvida por delegação em 2026-09-28:** inventário inicial vazio; Estilhaços poderão vir de conclusão de desafios diários e campanhas. O agente define uma recompensa local determinística coerente com a receita, mantendo recompensas fora da IA. Distribuição exata registrada no design da implementação.
- **Q-022 — Resolvida em 2026-09-28:** merge confirmado é irreversível e não tem custo adicional definido. Não oferecer desfazer; apresentar prévia e confirmação antes da operação.
- **Q-023 — Resolvida em 2026-09-28:** persistir após cada ação do usuário. A implementação deve gravar consumo e produto atomicamente numa transação local e manter o estado anterior em caso de falha.
- **Q-024 — Resolvida em 2026-09-28:** máximo de 999 itens por combinação de Joia e estágio. Bloquear crédito que excederia o máximo sem consumir entrada; operações de merge permanecem atômicas.

## Encerramento da entrega de 2026-10-04

- Q-048 a Q-051 foram respondidas por Erick: Fragmentos faltantes, fusões manuais, três itens iniciais por Joia somente em instalações novas, aprovação de capas Comic Vine por variante e valores crescentes de créditos/XP para os capítulos finais.
- Q-055: resolvida pelo bundle Lovable publicado — Thanos no capítulo 9; Ameaça Tecnológica é a entidade nomeada em Wakanda.
- Q-056: resolvida por Erick — não havia usuários anteriores; sem plano de migração de campanha.
