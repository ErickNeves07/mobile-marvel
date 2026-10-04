# 033 — Deadpool com Gemini: design

- Adapter `GeminiNarrativeAdapter` usa `urllib` stdlib, `POST /v1beta/models/gemini-3.5-flash-lite:generateContent`, cabeçalho `x-goog-api-key`, corpo JSON limitado e timeout. Converte o par de mensagens atuais em `systemInstruction` e `contents` e extrai somente `candidates[0].content.parts[].text`.
- Seleção em `main.py`: Gemini quando a variável existe; em falha, Groq se a chave existe; por último frase local. Nenhum detalhe do erro upstream cruza a API pública.
- `render.yaml` declara `GEMINI_API_KEY` com `sync: false`; Erick precisa colocar o valor no painel privado do Web Service existente. `.env` local é ignorado. `/ready` informa presença, não saúde real do provedor.
- Limites de entrada/saída e fallback preservam o contrato Android `{text, fallback}`.
- Verificar via mock e chamada real sem expor segredo/conteúdo. Não registrar texto da resposta em evidência.
