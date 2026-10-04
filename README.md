# Marvel: Ruptura Infinita

Projeto Android Java + FastAPI. O fonte inclui gameplay offline de desafios/campanhas/recompensas, APIs Comic Vine/Deadpool e progressão local da Manopla/Câmara de Variantes. A release assinada 0.2.0 foi reconstruída e validada localmente; a publicação Render e o APK com URL pública continuam pendentes. Detalhes, hash e limites em [RELEASE_NOTES.md](RELEASE_NOTES.md).

## Desenvolvimento

1. Leia `AGENTS.md` e `memory/CURRENT_STATE.md`.
2. Android: consulte [android-app/README.md](android-app/README.md).
3. Backend: consulte [backend/README.md](backend/README.md).
4. Specs das funcionalidades em `specs/`, incluindo recompensas em `028` e retratos em `030`.
5. Chaves de provider somente no ambiente do backend: `COMIC_VINE_API_KEY` e `GROQ_API_KEY`. Configure a URL de build com `-PriApiBaseUrl=https://...`; nenhum segredo vai ao APK. Guia de publicação em [docs/11-RENDER-DEPLOY.md](docs/11-RENDER-DEPLOY.md).

## Documentação

- Produto: `docs/01-PRODUCT_VISION.md`, `docs/02-REQUIREMENTS.md`
- Arquitetura/integração: `docs/03-ARCHITECTURE.md`, `docs/10-API_AI_INTEGRATIONS.md`
- Spec SDD: `specs/`
- Memória: `memory/CURRENT_STATE.md`, `memory/OPEN_QUESTIONS.md`, `memory/SESSION_LOG.md`

## Estado e limitacoes

- Android: testes JVM, lint, debug/release assemble e instrumentação no AVD (22/22) passaram após fusão 2:1, recompensas de campanha e Fragmento diário. Release assinada 0.2.0 candidata foi reconstruída; veja [RELEASE_NOTES.md](RELEASE_NOTES.md).
- Backend: 45 testes passaram, inclusive proxy e vínculo dos 21 retratos Comic Vine; `pip check` foi validado anteriormente.
- Manopla/Camara e progressao de variantes foram implementadas e validadas localmente em `specs/026`.
- A chave Comic Vine está apenas no `.env` local ignorado pelo Git. Os 21 personagens foram conferidos na API real; o Android já possui slots, cache, crédito e link para os retratos, mas o APK ainda precisa de host HTTPS para mostrá-los. Smoke real Groq falhou com erro sanitizado. Teste em aparelho físico pendente; veja Q-026/Q-028/Q-032.
- A refatoração visual fiel ao protótipo Lovable está especificada em `specs/027-lovable-visual-refactor/` e segue parcial, especialmente na grade da Coleção e em subfluxos.
