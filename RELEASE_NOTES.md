# Candidato local 0.11.0 - 2026-10-05 (Android instalado; Render pendente)

- Combate usa tres herois com HP individual, troca gratuita, dano do chefe apenas no ativo, super compartilhado e batalhas sem limite de acoes.
- Se um heroi cair, o jogador escolhe um reserva vivo. Cards mostram HP e especial por personagem; ataque, defesa e desestabilizacao recebem animacoes proprias.
- Vitoria/recompensa e derrota mostram resumo dos acontecimentos sem alterar economia ou premios.
- Deadpool recebe roster completo, desbloqueios, variantes, atributos authored, equipe/luta atual, campanha, recursos, Forja e desafio diario. O backend local aceita contexto ate 6.000 caracteres.
- `testDebugUnitTest`, `lintDebug` e `assembleDebug` passaram. APK debug versionCode 11 / 0.11.0 instalado e aberto no telefone `C6OFVWYD4DZTBA5H`; `space_5=20dp` corrigiu a falha de compilação.
- Código em `0e7387c`; correção Android `25db6bf` define o recurso faltante. Render ainda precisa receber o backend que aceita `game_context`; pytest backend não foi executado por indisponibilidade da venv. Nenhum deploy foi iniciado.

# Candidato local 0.10.0 — 2026-10-05 (ainda sem APK)

- Deadpool agora aceita temas gerais e humor sem comentar a tela atual; o contexto enviado pelo jogo mantém equipe, coleção, variantes e campanhas para perguntas sobre o app.
- Imagens Comic Vine são pré-carregadas para o trio inicial, personagens da Câmara, chefes e roster; bitmaps ficam somente em cache LRU em memória limitado a 20 MiB.
- Prólogo antes do Nexus, cena narrativa após cada vitória e conclusão depois de Titã; a Câmara mostra o papel de Reed e Doutor Estranho com retratos atribuídos.
- Trilha ambiente nos destinos e volume maior nas batalhas; efeitos da interface/batalha ampliados. Desafio diário mostra “?” antes de revelar o personagem.
- Build debug pendente: rede bloqueou download do Wrapper e o modo offline não encontrou AGP 8.13.2 no cache local; OneDrive também negou a gravação do relatório Gradle. Nenhum teste foi executado.
- Nenhum APK 0.10.0 foi produzido, instalado, enviado ao GitHub ou publicado no Render nesta etapa.

# Candidato local 0.8.0 - 2026-10-04

- Audio de interface no app e trilha original discreta somente durante batalhas; ambos podem ser desligados pelo controle de audio.
- Avisos e impactos de rounds mais contrastados e duradouros; animacoes de fusao mais claras e com som proprio por resultado.
- Forja simplificada: sem cadeia explicativa nem linhas vazias ate 999. Pedras aparecem em cartoes; toque no cartao para fundir quando ha pares.
- Mensagens de batalha um pouco mais claras. Nenhum calculo, receita, recompensa ou custo foi alterado.
- Deadpool: compatibilidade com Render legado; apos HTTP 422 ao enviar game_context, repete no formato antigo. OpenAPI atual ainda nao anuncia game_context; contexto dinamico completo requer publicar backend novo.
- Gradle unit/lint/build passaram; suite instrumentada completa passou 33/33 no emulador.
- APK debug: artifacts/Marvel-Ruptura-Infinita-debug-0.8.0.apk; 6,893,711 bytes; SHA-256 FFDDEFACF4CBE6A01CD152A0D93652F9A27FC7E0B37778F53974C4FD8FBA0B47.
- Nenhum POST de IA, instalacao no telefone, push ou deploy nesta tarefa.

# Candidato local 0.7.0 — 2026-10-04

- Splash AndroidX compatível com API 26+: fenda vetorial animada, seis gemas e núcleo pulsante com transição curta. A introdução e o fluxo de jogo foram mantidos.
- APK debug: `artifacts/Marvel-Ruptura-Infinita-debug-0.7.0.apk` · 6.249.992 bytes · SHA-256 `ACA37C4BB278ED6EB64ABBA886BC3B6BDBE8E623DB56D526302ABA81FD90C9AA` · versionCode 7/versionName 0.7.0.
- Build/lint/unit passaram; **33/33** testes instrumentados passaram no AVD. Captura: `reports/lovable-research/app-splash-0.7.0.png`.
- Instalado no telefone `C6OFVWYD4DZTBA5H` após limpeza local autorizada; Android confirmou 0.7.0 e processo ativo sem erro fatal.
- Não foi feito novo push ou deploy nesta tarefa.

# Release 0.3.0 — candidato de avaliação (2026-10-04)

# Candidato local 0.6.0 — 2026-10-04

- APK debug: `artifacts/Marvel-Ruptura-Infinita-debug-0.6.0.apk` · 6.334.722 bytes · SHA-256 `E37023573C9C7D00A13EB0251CE2F4F4EE7FF255F43DD49D88A3D1283D8221E9` · versionCode 6/versionName 0.6.0.
- Build/lint, testes JVM e **32/32** instrumentados no AVD passaram após a última edição; backend pytest **57/57**.
- A captura de QA da Forja está em `reports/lovable-research/forge-merge-0.6.0.png`: mostra a gema azul brilhante centralizada entrando nas luvas, com o inventário visível ao fundo.
- Para Deadpool receber estado atual do jogo e para as curiosidades Comic Vine exibirem contagem/data de primeira edição, publicar também a revisão do backend deste workspace no Render antes de distribuir o APK.
- Este APK não foi instalado no aparelho físico.

APK assinado: `artifacts/Marvel-Ruptura-Infinita-release.apk` · 5.119.606 bytes · SHA-256 `C948C701A437BB85EDC32C2529AD4C06AED72C1B126B6C5F966785514F512CA5` · `versionCode 3`/`versionName 0.3.0`. URL compilada: `https://mobile-marvel-8qex.onrender.com`. `zipalign` e assinatura APK v2 verificados.

O Run comum do Android Studio agora também compila essa URL por padrão. Antes dessa correção, o debug sem `-PriApiBaseUrl` ficava offline, explicando retratos/Deadpool ausentes mesmo com o backend disponível. Para testar offline de propósito, use `-PriApiBaseUrl=`.

APK debug para atualizar a instalação de desenvolvimento no celular: `artifacts/Marvel-Ruptura-Infinita-debug-0.3.0.apk` · 6.202.280 bytes · SHA-256 `BB7A14F48B5407AEE3B81E4F586D809C424DE34EA6B1EE264D0FCDC978D4E639`. Gerado sem `testOnly`, instalado com `adb install -r` no aparelho de Erick; Android confirmou `versionCode=3`, `versionName=0.3.0` e preservou `firstInstallTime`. Dois APKs gerados 0.1.0 nas pastas antigas `android-app/app/build` e `.validation-output` foram removidos para evitar seleção errada. Os APKs em `artifacts` são ignorados pelo Git.

- Novos jogadores possuem Homem-Aranha, Wolverine e Tocha Humana desde o início. Personagens anteriores já possuídos permanecem após a migração do banco v5→v6.
- Coleção raiz apresenta 21 personagens gerais; detalhe permite inspecionar/equipar variantes, ver atributos de jogo e fatos editoriais Comic Vine. Carregamento de retratos ganhou centralização, retry, pré-carregamento e limite suficiente para a imagem de 8,29 MB do Homem-Aranha.
- As seis missões de recompensa aprovadas são batalhas individuais. Equipe de três personagens possuídos pode cruzar facções; a variante equipada altera o poder. Cada batalha tem decisões, efeitos, retratos, derrota e nova tentativa; somente a primeira vitória paga o pacote da missão.
- Forja mostra fusões disponíveis 2:1 sem abrir detalhes. A Manopla completa pode ser ativada na página de personagem e libera avanço sequencial de variante sem consumir a Joia exigida. Botão de recompensa debug removido.
- Nexus mostra Reed, Doutor Estranho e a Manopla com estado real. Desafio diário tem página dedicada e exige escolher um herói antes do primeiro palpite. Deadpool usa modelo Groq atualizado após descontinuação do anterior, com fallback indicado na interface.

Verificação local: backend **47/47**, Android instrumentado AVD **27/27**, testes JVM, lint debug/release, debug/release build, alinhamento e assinatura v2 aprovados. Retratos live de Homem de Ferro, Homem-Aranha, Tocha Humana, Reed e Doutor Estranho foram vistos nas capturas AVD. O AVD apresentou ANR do System UI no teste manual; a instrumentação desenhou e validou as views do app.

Após a correção da URL padrão, a matriz Android foi repetida sem passar propriedade de URL: **27/27** instrumentados, JVM, lint debug e `assembleDebug` aprovados; `scripts/build-release.ps1` também passou sem override. O hash do APK assinado permaneceu o informado acima.

Pendências para a entrega: o backend Render ainda precisa receber esta versão com rota editorial dos oponentes e modelo Groq novo; não houve smoke pago do Groq após rejeição automática de aprovação. O telefone físico estava desconectado e precisa instalar o debug 0.3.0 com a mesma assinatura da 0.2.0 para preservar os dados. Erick ainda precisa decidir como obter os outros 18 personagens (Q-045) e se os seis capítulos atuais serão substituídos pelos nove do Lovable e seus três pacotes adicionais (Q-046). A release assinada usa certificado diferente do APK debug já instalado; não a instale por cima do debug esperando preservar dados.

## Histórico: release 0.2.0

O APK assinado em `artifacts/Marvel-Ruptura-Infinita-release.apk` foi recompilado com o backend publicado em `https://mobile-marvel-8qex.onrender.com`. Tem **5.111.366 bytes**, SHA-256 `3D6FFAA8FFE241737B5BFC01C0929E1BB03A9495EDD8D597F7FD49AD0C232ACA`; `zipalign` e `apksigner` confirmaram assinatura v2. A URL HTTPS foi encontrada no DEX do APK. Build JVM, lint debug/release, `assembleRelease`, **22/22** testes instrumentados Android com rede e **45/45** testes backend passaram. O Render respondeu `/health=ok`, `/ready=ok`, serviu 21 personagens, duas campanhas e **21/21** retratos Comic Vine. A Coleção agora apresenta 105 cards de variantes em grade com dois filtros, destaque a cada cinco, retrato/crédito e detalhe funcional; teste AVD percorreu filtro de patamar, detalhe, retorno e acesso à Comparação. Capturas locais confirmam imagem e crédito na Coleção e nos dois lados da Comparação. A comparação permite escolher uma variante em cada lado. A primeira missão X-Men apresenta quatro decisões, efeitos e especial; a equipe salva altera o resultado, pode perder e tentar novamente sem prêmio. A Manopla apresenta seis encaixes e progresso derivados do inventário real. Fusões novas consomem duas peças; fusões antigas não são compensadas. Cada primeira vitória futura de campanha concede quatro Fragmentos de uma Joia própria, créditos e XP conforme a tabela aprovada na spec 028. A primeira vitória diária concede um Fragmento da Joia determinística do dia. Nenhuma vitória anterior recebe ajuste retroativo; os saldos aparecem no Nexus.

**Limite de entrega:** este build ainda não reproduz integralmente o protótipo Lovable. Os retratos Comic Vine dos 21 personagens se repetem nas cinco variantes conforme decisão de Erick; a grade da Coleção melhorou, mas proporções/cards, Forja e subfluxos ainda divergem parcialmente. Créditos e XP persistem, mas seus usos futuros não foram definidos. As chaves ficam no Render e no `.env` local ignorado pelo Git; não estão no APK. A presença da chave Groq foi confirmada por `/ready`, mas uma resposta real do provedor ainda não foi validada. O teste instrumentado exercitou comparação, vitória, derrota/retry, Manopla, recompensa, filtros/detalhe da Coleção e retratos online; o smoke manual em aparelho físico continua pendente. Este APK é candidato de avaliação.

## Histórico do build anterior (2026-09-28)

`artifacts/Marvel-Ruptura-Infinita-release.apk` was rebuilt from the current source and signed with the existing local release key. This is a signed test build for local/device evaluation; Comic Vine live access and a public backend host are not configured, so online editorial/provider features have not been validated end to end.

- Application ID: `com.erickbarbosa.rupturainfinita`
- versionCode: 2; versionName: 0.2.0
- Size: 4,840,290 bytes
- SHA-256: `8DA29343000CB79B00142A07B80D6CEFC73CBA8CFC36F66BAF3D357D217BED0F`
- APK Signature Scheme v2: verified.
- `aapt dump badging`: versionName 0.2.0, versionCode 2, minSdk 26, targetSdk 36.
- Certificate SHA-256: `A83204728D8669E0D497B45917C7F0A8220F3AB83914BB67692F328A340F1374`

The build includes daily challenge, local campaigns/battles/rewards, Forge persistence, Gauntlet activation and per-character variant progression/equipment, plus configurable HTTPS API integration. Progression and gameplay remain local SQLite. Gauntlet requires one complete copy of all six Stones without consuming them; each next variant tier requires but does not consume its mapped complete Stone.

## Verification

- `scripts/validate-local.ps1 -Target android`: JVM tests, Android test APK compilation, lint, and debug APK assemble passed.
- `scripts/run-android-instrumentation.ps1`: 10/10 repository tests passed on the connected AVD, including v3→v4 migration, Gauntlet gate/non-consumption, and sequential/idempotent variant unlock.
- `backend/.venv/Scripts/python.exe -m pytest -q`: 39 tests passed; `pip check` reported no broken requirements.
- `scripts/build-release.ps1`: release build/lint passed and `apksigner` verified v2 signing.
- AVD screenshots of Forge Gauntlet requirements and pre-activation locked variants are in `specs/026-gauntlet-variant-progression/`.

## Remaining limits

- No Render deployment or public HTTPS URL exists in this environment. `COMIC_VINE_API_KEY` and Render access are absent; Groq live smoke returned a sanitized error, so provider connectivity/key acceptance remains unknown.
- No physical handset test was performed. The AVD passed repository instrumentation and the app rendered Forge/Collection screenshots.
- Preserve the signing keystore at `%LOCALAPPDATA%\RupturaInfinita\release-signing`; its DPAPI-protected password is tied to this Windows user profile. A portable backup is not yet validated.
# Candidato 0.9.0 — 2026-10-04

- Deadpool usa a missão selecionada/próxima e a equipe salva real; inclui posse, variantes equipadas, progresso e os nove adversários, inclusive Thanos. O backend novo aceita esse contexto. O Render publicado precisa receber a revisão.
- Avisos dos seis padrões de combate foram escritos para cada uma das nove campanhas; Ultron não exibe pistas de Magneto.
- Backend: 58/58 testes. Android JVM, lint, debug e release passaram; emulador 34/34 testes instrumentados. Release 0.9.0 instalou e abriu no emulador sem exceção fatal.
- APK debug: `artifacts/Marvel-Ruptura-Infinita-debug-0.9.0.apk` — 6.778.296 bytes — SHA-256 `51960BB0ABD0BE551377B710C8B5AB2FE3CBF667E66F5C1B4B60A428AA410BFA`.
- APK assinado: `artifacts/Marvel-Ruptura-Infinita-release.apk` — 5.692.132 bytes — SHA-256 `8AA74277970068755C066C3E2078844E3E16BE9CAC3BEE1018FE1B8E08488B7A` — assinatura v2 e zipalign verificados.
- O telefone não estava conectado nesta validação. Uma chamada real POST do Deadpool foi barrada pela revisão automática por possível consumo de cota paga sem autorização específica; não houve contorno.
