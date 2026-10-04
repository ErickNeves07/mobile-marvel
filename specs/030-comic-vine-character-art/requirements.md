# 030 — Retratos Comic Vine na coleção

Status: escopo solicitado por Erick em 2026-10-04 para a entrega de 2026-10-05. Imagens devem vir da Comic Vine em tempo de execução, com crédito e link; sem copiar seus arquivos para o APK.

## Objetivo

Mostrar personagens reconhecíveis em lugar de silhuetas na Coleção, detalhe/comparação de variantes e demais cartões de personagem, mesmo sem artes exclusivas para cada uma das 105 variantes.

## Contrato

- Associar de forma verificada os 21 IDs internos aos personagens Marvel da Comic Vine. Dado editorial nunca altera atributo de jogo, tier ou recompensa.
- Consultar imagem HTTPS pelo backend, que mantém a chave de API; o APK recebe apenas URL de imagem e página de origem, sem chave.
- Usar uma imagem editorial por personagem em todas as cinco variantes quando não houver uma imagem distinta verificável para a variante. Nome/estado da variante permanece separado e a UI informa que o retrato é editorial do personagem.
- Crédito visível “Fonte: Comic Vine” e link para a página de origem. Não salvar o arquivo de imagem no APK nem enviá-lo para geração/edição.
- Estado de carregamento, ausência, falha e cache local bounded para experiência de uso sem internet; nenhum bloqueio do loop offline do jogo.
- Aplicar a imagem nos cards da Coleção e na comparação, mantendo o enquadramento/cores do Lovable.

## Aceite

- [x] 21 vínculos de personagem verificados por ID/nome/editora; nenhum DC ou personagem incorreto entra na resposta.
- [x] Backend retorna imagem HTTPS, origem e atribuição sem expor segredo; cache e rate limit permanecem.
- [ ] Android carrega imagem fora da main thread, limita tamanho/cache e mostra crédito/link; código e fallback testados, falta validar rede no APK com host HTTPS.
- [x] Erro/host ausente não derruba Coleção, comparação, batalha ou progressão.
- [x] Testes backend/Android após última edição e release reconstruída; screenshots offline registradas. Screenshot com rede permanece em T04.

## Fora de escopo

- Prometer 105 retratos de trajes específicos quando a Comic Vine não os disponibiliza.
- Embutir imagens ou chave da Comic Vine no APK.
- Publicação de backend sem acesso/URL HTTPS e autorização externa apropriada.
