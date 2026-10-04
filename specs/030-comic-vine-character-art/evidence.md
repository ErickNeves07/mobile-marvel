# 030 — Evidências

## Auditoria Comic Vine, 2026-10-04

`scripts/audit_comic_vine_roster.py` foi executado com a chave somente no ambiente local; a saída não continha segredo. Busca e detalhe verificaram editora Marvel e `image_url` HTTPS nos 21 IDs abaixo. Para Loki, a busca com um resultado encontrou um personagem não Marvel; limite 5 identificou a entrada Marvel correta. Nenhum arquivo de imagem foi baixado para o projeto.

| ID de jogo | ID Comic Vine | Nome retornado |
|---|---:|---|
| homem-de-ferro | 1455 | Iron Man |
| capitao-america | 1442 | Captain America |
| thor | 2268 | Thor |
| hulk | 2267 | Hulk |
| feiticeira-escarlate | 1466 | Scarlet Witch |
| pantera-negra | 1477 | Black Panther |
| homem-aranha | 1443 | Spider-Man |
| doutor-estranho | 1456 | Doctor Strange |
| wolverine | 1440 | Wolverine |
| ciclope | 1459 | Cyclops |
| jean-grey | 3552 | Jean Grey |
| professor-xavier | 1505 | Professor X |
| senhor-fantastico | 2151 | Mr. Fantastic |
| mulher-invisivel | 2190 | Invisible Woman |
| tocha-humana | 2120 | Human Torch |
| coisa | 2114 | Thing |
| rocket-raccoon | 32814 | Rocket Raccoon |
| groot | 24341 | Groot |
| surfista-prateado | 2502 | Silver Surfer |
| loki | 4324 | Loki |
| deadpool | 7606 | Deadpool |

`GET /v1/editorial/game-characters/wolverine` com provider real respondeu HTTP 200, nome Wolverine, fonte Comic Vine e domínio de imagem `comicvine.gamespot.com`. A URL pública do retrato respondeu HEAD 200, `image/jpeg`, 89.200 bytes. Nenhuma chave foi registrada em saída, APK ou Git.

## Verificação local

- FastAPI `python -m pytest -q` no diretório `backend`: **45 testes passaram**, incluindo mapeamento 21/21, DTO, ID inválido e erro sanitizado. Primeira tentativa a partir da raiz falhou apenas na coleta por `PYTHONPATH`/diretório; repetida na pasta correta com sucesso.
- Após a última alteração no carregador de retratos, Android `validate-local.ps1 -Target android`: build/testes JVM/lint passaram. Instrumentação AVD **22/22**; inclui 105 slots de variantes, fallback offline e Fragmento diário. `scripts/build-release.ps1` passou build/lint release, zipalign e assinatura v2. APK offline: 5.107.462 bytes, SHA-256 `9382C92F2228F700FB52EA09DD6A46924F5CC67583CAC62D08BEAE3290F6DAA5`.
- Capturas `collection-portraits.png` e `compare-variant.png` mostram os slots sem host. Após configurar a URL Render, as capturas locais ignoradas pelo Git `collection-portraits-live.png`, `collection-detail-live.png` e `compare-variant-live.png` mostram personagens reais com crédito/link na grade, no detalhe e nos dois lados da Comparação. Elas não são reproduzidas no repositório por conterem retratos editoriais. O teste AVD aguardou o primeiro retrato da grade e os dois da Comparação, percorreu filtro de patamar, detalhe, retorno e acesso à Comparação, e passou **22/22** após a última edição. O link foi conferido pelo código e presença/clickability na UI; abertura no navegador externo não foi ensaiada no aparelho.

## Integração hospedada, 2026-10-04

- Render `https://mobile-marvel-8qex.onrender.com`: `/health` respondeu `ok`; `/ready` respondeu `ok`, Comic Vine e Groq configurados; catálogos retornaram 21 personagens e duas campanhas. Consulta de todos os IDs por `/v1/editorial/game-characters/{id}`: **21/21** game IDs, fonte Comic Vine e host de imagem `comicvine.gamespot.com` corretos. A presença da chave Groq não prova resposta real do provedor.
- `scripts/build-release.ps1` com `ORG_GRADLE_PROJECT_riApiBaseUrl` gerou APK assinado de **5.111.366 bytes**, SHA-256 `3D6FFAA8FFE241737B5BFC01C0929E1BB03A9495EDD8D597F7FD49AD0C232ACA`; URL confirmada no DEX, `zipalign` e assinatura v2 verificados. `validate-local.ps1 -Target android` e `run-android-instrumentation.ps1` passaram com a mesma URL após a refatoração da grade e a última versão dos testes.

## Pendências para encerramento

- Abrir o link de origem e exercer fallback sem rede em aparelho físico; a implementação e o estado offline foram testados no AVD, mas o toque manual no telefone ainda falta.
- A grade em duas colunas, destaque e filtro de patamar foram implementados; proporções e composição de cards ainda divergem do Lovable. Paridade global segue aberta na spec 027.
