# 040 — Design

- Usar `BattleMission` como fonte authored para identidade da missão. `LovableBattle.warning()` combina um detalhe do adversário com uma pista por padrão; o padrão e os cálculos permanecem intactos.
- Resumo Deadpool é montado no Android a partir do catálogo e `ForgeRepository`: missão corrente selecionada ou próximo capítulo desbloqueado, equipe salva e variantes equipadas, posse e vitórias. O resumo é enviado no campo `game_context` do contrato local.
- Em backend legado, reenvio bounded inclui um resumo compacto no `prompt` para recuperar informação útil, sem prometer fidelidade enquanto o serviço publicado permanecer antigo.
- Backend novo recebe fatos de jogo, incluindo o mapa de campanhas, no contexto permitido. Perguntas opinativas sobre adversários são permitidas como humor sobre os fatos fornecidos; não há afirmações canônicas sem fonte.
- Testes: matriz 9×6 de narração, contexto/prompt limitado, contratos de backend, testes de regressão Android e fluxos de UI existentes.
