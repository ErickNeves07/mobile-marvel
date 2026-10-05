# 041 — Design

- Backend amplia o prompt factual do Deadpool: humor geral é permitido; factualidade de Marvel só é afirmada quando fornecida; instruções de tela/campanha não vazam como assunto de fala. Frases roteirizadas continuam fallback.
- `EditorialPortraitLoader` mantém bitmaps reamostrados em LRU limitado a 20 MiB, aquece no splash/abertura e prioriza trio inicial, Reed/Estranho/Deadpool, chefes e roster; metadados passam pelo limite de uma chamada por segundo. Não salva binários, não copia para o APK.
- `CampaignStory` contém diálogos authored: abertura em quatro falas; cena por vitória com a equipe ativa, Reed/Estranho/Deadpool e figuras pertinentes; capítulo final resolve a ruptura imediata e preserva a fissura da prisão de Thanos antecipada na visão do produto.
- A sequência reaproveita layout de retrato Comic Vine e cartões/balões nativos, anima um personagem por vez, tem avançar/pular e respeita `ANIMATOR_DURATION_SCALE`.
- A Câmara recebe um painel de operação com papéis e fluxo físico (mapear assinatura, selecionar variante, criar envelope de contenção, verificar estabilidade). Não adiciona ação/custo/mecânica.
- Áudio usa a preferência existente: elevar `ToneGenerator`, trilha ambiente nos cinco destinos e mais alta em batalha, além de sons curtos nas ações e navegação. Respeitar toggle e lifecycle.
- O desafio diário mostra “?” em grande destaque até revelar o personagem.
