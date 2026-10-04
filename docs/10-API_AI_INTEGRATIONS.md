# Comic Vine e IA

## Comic Vine

Base: `https://comicvine.gamespot.com/api/`. Todo pedido inclui chave de backend, formato JSON e User-Agent descritivo. O proxy FastAPI expõe `GET /v1/editorial/characters?q=&limit=&offset=`, `GET /v1/editorial/characters/{id}` e `GET /v1/editorial/game-characters/{game_id}` para os retratos do elenco. A busca limita tamanho/paginação, valida editora Marvel no servidor, pede campos mínimos, usa cache e aplica no máximo 100 chamadas por recurso por hora e uma chamada por segundo. Respostas incluem fonte, link editorial e publisher. Android não acessa a API Comic Vine diretamente e nunca recebe a chave; baixa apenas a imagem pública HTTPS indicada pelo backend.

Comic Vine deve continuar separado dos atributos e balanceamento definidos para o jogo. O build Android usa o serviço Render publicado por padrão; `-PriApiBaseUrl=https://...` permite substituí-lo e `-PriApiBaseUrl=` força modo offline. Respostas GET são cacheadas localmente e a falha mostra indisponibilidade. Oponentes possuem rota editorial separada de `game-characters` e não entram no catálogo jogável.

## IA

Groq é acessado somente pelo backend via `GROQ_API_KEY`. `POST /v1/ai/deadpool-line` aceita allowlist de contexto e prompt curto, usa contexto factual aprovado, sanitiza saída e devolve resposta roteirizada se o provider falhar. A narrativa não pode alterar fatos canônicos, combate, dificuldade, recompensa ou progresso. Nenhum segredo é incluído no app.

Android faz HTTP fora da thread da UI. `BackendClient` tem timeout, limite de tamanho e cache local para conteúdo GET. O APK sem URL funciona offline; Comic Vine indica erro de configuração e Deadpool informa resposta roteirizada. Release para API remota exige URL HTTPS pública; cleartext HTTP para Render/LAN não está habilitado.

## Evidência e limites

Comic Vine/Groq têm testes com transporte simulado. O backend `https://mobile-marvel-8qex.onrender.com` respondeu saúde/readiness e 21/21 retratos Comic Vine; o APK assinado foi construído com essa URL e o AVD carregou imagens reais na Coleção/Comparação. Groq aparece configurado em `/ready`, mas a resposta real do provedor permanece sem validação. Nenhuma chave está no APK/Git; teste físico e abertura manual dos links ainda faltam.
