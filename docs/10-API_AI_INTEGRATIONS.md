# Comic Vine e IA

## Comic Vine

Base: `https://comicvine.gamespot.com/api/`. Todo pedido inclui chave de backend, formato JSON e User-Agent descritivo. O proxy FastAPI expõe `GET /v1/editorial/characters?q=&limit=&offset=` e `GET /v1/editorial/characters/{id}`. A busca limita tamanho/paginação, valida editora Marvel no servidor, pede campos mínimos, usa cache e aplica no máximo 100 chamadas por recurso por hora e uma chamada por segundo. Respostas incluem fonte, link editorial e publisher. Android não acessa Comic Vine diretamente e nunca recebe a chave.

Comic Vine deve continuar separado dos atributos e balanceamento definidos para o jogo. A UI Android permite busca editorial quando uma URL de backend é configurada com `-PriApiBaseUrl=https://...`; respostas GET são cacheadas localmente e a falha mostra indisponibilidade.

## IA

Groq é acessado somente pelo backend via `GROQ_API_KEY`. `POST /v1/ai/deadpool-line` aceita allowlist de contexto e prompt curto, usa contexto factual aprovado, sanitiza saída e devolve resposta roteirizada se o provider falhar. A narrativa não pode alterar fatos canônicos, combate, dificuldade, recompensa ou progresso. Nenhum segredo é incluído no app.

Android faz HTTP fora da thread da UI. `BackendClient` tem timeout, limite de tamanho e cache local para conteúdo GET. O APK sem URL funciona offline; Comic Vine indica erro de configuração e Deadpool informa resposta roteirizada. Release para API remota exige URL HTTPS pública; cleartext HTTP para Render/LAN não está habilitado.

## Evidência e limites

Comic Vine/Groq têm testes com transporte simulado. Uma chave `COMIC_VINE_API_KEY` e uma API acessível são necessárias para conteúdo editorial real. Groq real depende da variável `GROQ_API_KEY` e de conectividade no ambiente de execução. Nenhuma chamada paga ou deploy foi feito nesta implementação.
