# Backend

Backend FastAPI local. Além de campanhas e roster estáticos, oferece busca/detalhe Comic Vine em `/v1/editorial/characters` (variável `COMIC_VINE_API_KEY`) e fala contextual Deadpool em `/v1/ai/deadpool-line` (variável `GROQ_API_KEY`). Não coloque chaves no app nem nos logs. As integrações têm testes com transporte simulado; endpoints externos retornam fallback/indisponibilidade quando não configurados.

## Ambiente Windows

Os comandos abaixo descrevem o ambiente usado para gerar a venv original. Se o caminho/base do Python não estiver disponível neste Windows, recrie `.venv` localmente a partir do Python 3.13 já instalado e instale o lock com hashes conforme `requirements-dev.txt`.

```powershell
C:\Users\erickbarbosa-ieg\AppData\Local\Programs\Python\Python313\python.exe -m venv .venv
.\.venv\Scripts\python.exe -m pip install --require-hashes -r requirements-dev.txt
.\.venv\Scripts\python.exe -m pytest
.\.venv\Scripts\python.exe -m uvicorn app.main:app --host 127.0.0.1 --port 8000
```

Saúde local:

```text
GET http://127.0.0.1:8000/health
GET http://127.0.0.1:8000/ready
```

Resposta esperada:

```json
{"status":"ok","service":"ruptura-infinita-backend"}
```

`/health` confirma somente que o processo HTTP responde e é usado pelo health check do Render. `/ready` informa `status: ok` quando `COMIC_VINE_API_KEY` e `GROQ_API_KEY` estão configuradas; caso contrário, responde `degraded` e retorna apenas booleanos, nunca os valores dos segredos. O serviço continua respondendo durante degradação porque catálogo e fallback narrativo são locais.

No Render, crie as variáveis marcadas `sync: false` no blueprint pelo painel de configuração do serviço e cadastre os valores diretamente como secrets. Nunca os escreva em `render.yaml`, commits, APK ou logs. Configure o Gradle com `-PriApiBaseUrl=https://<dominio-do-servico>` para uma build apontar ao host publicado; esta URL ainda não está definida.

Índices de conteúdo local:

```text
GET http://127.0.0.1:8000/v1/campaigns
GET http://127.0.0.1:8000/v1/game/characters
```

`/v1/campaigns` contém os resumos de X-Men e Quarteto Fantástico. `/v1/game/characters` carrega de `../shared/game_catalog.json` os 21 personagens internos e suas cinco variantes nomeadas. Endpoint editorial requer `COMIC_VINE_API_KEY`; Deadpool usa fallback quando Groq não estiver configurado. O Android consome as rotas quando compilado com `-PriApiBaseUrl=https://seu-host`.
