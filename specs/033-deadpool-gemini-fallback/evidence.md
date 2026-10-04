# 033 — Deadpool com Gemini: evidências

2026-10-04: `GET /ready` no Render respondeu `status=ok`, `comic_vine=true`, `groq=true`; rota editorial nova já respondeu 200. Um `POST /v1/ai/deadpool-line` autorizado pelo pedido do usuário retornou `fallback=true` (75 caracteres). Chave Groq local retornou HTTP 403 para `openai/gpt-oss-120b` sem mensagem de erro registrada. O `.env` em Downloads contém uma chave Gemini isolada; `GET /v1beta/models` com `x-goog-api-key` respondeu 200 e listou `gemini-3.5-flash-lite`; uma geração curta com esse modelo respondeu HTTP 200, `finishReason=STOP`, 36 caracteres. Nenhum valor de segredo ou texto de resposta foi gravado.

Fontes oficiais consultadas: https://ai.google.dev/api , https://ai.google.dev/gemini-api/docs/models/gemini-3.5-flash-lite , https://ai.google.dev/gemini-api/docs/pricing , https://console.groq.com/docs/api-reference .

Após a implementação: `backend/.venv/Scripts/python.exe -m pytest -q` passou **53/53**. FastAPI `TestClient` com as chaves locais no ambiente chamou `/v1/ai/deadpool-line`: HTTP 200, `fallback=false`, texto de 198 caracteres. O valor da chave e a frase gerada não foram exibidos ou gravados. O Render ainda não recebeu a variável privada `GEMINI_API_KEY`.
