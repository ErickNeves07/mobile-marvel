# 041 — Evidências

## Implementação — 2026-10-05

- Deadpool recebe contexto do jogo sem mencionar a tela atual. O campo de texto convida perguntas gerais/piadas e as rotas rápidas usam o contexto `app`.
- `EditorialPortraitLoader` aceita pré-carga em lote. Os IDs são enfileirados começando pelo trio inicial, núcleo da Câmara, Deadpool e chefes; bitmaps reamostrados ficam apenas em LRU de 20 MiB. Metadados são limitados a 1 requisição por segundo.
- O painel da Câmara explica o que Reed mede/calibra e o que Strange mantém isolado; cartões exibem retratos e atribuição Comic Vine.
- `CampaignStory` apresenta prólogo uma vez, cena após cada vitória e epílogo/conclusão após a nona campanha; cenas têm retratos Comic Vine, falas e avançar/pular. Recompensa e batalha mantêm seus contratos existentes.
- Trilha ambiente é iniciada nos destinos principais, volume maior durante batalha; efeitos de interface e batalha mais audíveis permanecem no toggle já existente.
- O card do desafio diário exibe “?” central enquanto o personagem não foi revelado.
- `:app:assembleDebug` **não concluiu**: primeira tentativa foi impedida pela rede ao tentar obter Gradle 8.13. A distribuição foi baixada para dentro do projeto, mas a tentativa autorizada de resolução do AGP 8.13.2 recebeu `SocketException: Permission denied: connect` ao acessar `dl.google.com`, Maven Central e Gradle Plugin Portal. Em execução anterior, Gradle reportou `AccessDeniedException` ao escrever o relatório de problemas no diretório de build sincronizado pelo OneDrive; init script isolou a saída e `--no-problems-report` retirou esse conflito da última tentativa.
- Testes não foram executados nesta sessão. Próxima ação executável: repetir compilação por `scripts/validate-local.ps1 -Target android` em diretório local de build permitido, depois revisar/install no dispositivo mediante novo pedido.
