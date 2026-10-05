# 041 — Lore jogável, pré-carga visual e trilha do aplicativo

Solicitado por Erick em 2026-10-05.

## Objetivos

- Deadpool responde a perguntas cotidianas e pedidos de humor com personalidade, sem comentar a tela aberta. Para perguntas sobre o jogo, considera equipe, coleção e campanhas reais.
- Começar a carregar imagens editoriais assim que a abertura aparece, reutilizando-as em memória ao navegar.
- Explicar que Reed monitora assinaturas e estabilização de variantes e que Doutor Estranho mantém isoladas as realidades/selos enquanto a Câmara opera; eles não controlam os heróis.
- Criar abertura narrativa e uma cena após cada vitória, com a equipe escolhida, figuras relevantes e retratos Comic Vine; capítulo 9 termina em conclusão clara, coerente com a visão existente.
- Tornar música e efeitos audíveis em volume de aparelho habitual e ampliar feedback sonoro além das batalhas.
- Exibir um ponto de interrogação central no card do desafio diário enquanto o personagem estiver oculto.

## Limites

- Seguir a lore aprovada em `docs/01-PRODUCT_VISION.md`: Reed criou a tecnologia da Câmara; Strange estabiliza rupturas; Thanos está preso; a ressonância das Joias enfraquece a prisão; Doom fragmentou as Joias e quer fundir realidades.
- A narrativa não altera regras, recompensas, resultado de batalha ou progressão. Comic Vine é apenas fonte visual/editorial.
- Não empacotar nem persistir binários Comic Vine. Pré-carga apenas em memória, com limite LRU, respeitando HTTPS, atribuição e rate limit do gateway.
- Imagens não garantidas: cena mantém nome e balão de fala se o retrato estiver offline.

## Aceite

- Deadpool responde a piadas/perguntas gerais; não traz nome da tela automaticamente e não volta a Magneto por padrão.
- A Câmara explica com texto e identidade visual o trabalho de Reed e Strange e o objeto da estabilização.
- Há prólogo antes da primeira entrada no Nexus, cenas após cada vitória e encerramento depois da nona, todas puláveis e compatíveis com animações reduzidas.
- Pré-carga inicia durante a abertura, prioriza trio inicial, Reed/Estranho e nove chefes, e reutiliza memória ao abrir as áreas.
- O card do desafio diário mostra “?” até o personagem ser revelado.
- Áudio possui controle único, volume significativamente aumentado e feedback em navegação, merge, progressão, desafios, coleção e campanhas; trilha ambiente não interfere no foco de áudio do usuário.
