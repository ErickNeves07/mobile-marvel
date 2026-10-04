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
- Capturas `collection-portraits.png` e `compare-variant.png` mostram os slots e estado sem host. O app compilado ainda não dispõe de URL HTTPS do backend, então a imagem real no APK e seu link não foram verificados visualmente; esse é o critério pendente.

## Pendências para encerramento

- Publicar o backend no Render, cadastrar secrets no painel e obter URL HTTPS.
- Recompilar Android com `-PriApiBaseUrl=<URL>`, verificar retratos reais dos 21 personagens, crédito/link e fallback no dispositivo, então reconstruir APK final assinado.
- Comparar a Coleção renderizada com Lovable. A grade e os cards ainda divergem do protótipo além da fonte de imagem; spec 027 continua aberta.
