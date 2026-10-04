# Release 0.3.0 — candidato de avaliação (2026-10-04)

APK assinado: `artifacts/Marvel-Ruptura-Infinita-release.apk` · 5.119.606 bytes · SHA-256 `C948C701A437BB85EDC32C2529AD4C06AED72C1B126B6C5F966785514F512CA5` · `versionCode 3`/`versionName 0.3.0`. URL compilada: `https://mobile-marvel-8qex.onrender.com`. `zipalign` e assinatura APK v2 verificados.

O Run comum do Android Studio agora também compila essa URL por padrão. Antes dessa correção, o debug sem `-PriApiBaseUrl` ficava offline, explicando retratos/Deadpool ausentes mesmo com o backend disponível. Para testar offline de propósito, use `-PriApiBaseUrl=`.

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
