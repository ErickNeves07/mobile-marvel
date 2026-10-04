# 033 — Deadpool com Gemini: requisitos

Status: aprovado pelo pedido explícito de Erick em 2026-10-04. Groq respondeu fallback no Render e 403 no teste local; chave Gemini de Downloads foi testada com geração bem sucedida.

## Objetivo

Fazer as falas de Deadpool funcionarem no Android publicado sem colocar segredos no APK ou determinar regras do jogo por IA.

## Escopo e aceite

- O backend usa `GEMINI_API_KEY` quando configurada e, se a chamada falhar, pode tentar Groq configurado; se ambos falharem, mantém o fallback roteirizado e sinaliza `fallback=true`.
- Somente contextos allowlisted e fatos aprovados entram no prompt; resposta fica limitada a 500 caracteres e é sanitizada.
- `/ready` informa somente booleanos Comic Vine, Gemini e Groq; fica `ok` com Comic Vine e pelo menos um provedor de IA configurado. Chave nunca aparece em resposta, log, APK, teste ou commit.
- Gemini usa ID estável `gemini-3.5-flash-lite`, que a chave local listou e respondeu via `generateContent` em 2026-10-04.
- Testes simulados cobrem sucesso, erro, resposta inválida e seleção de provedor; smoke real local/backend e Render após configuração do secret.

## Fora de escopo

- IA resolver batalha, conceder recompensa ou afirmar fatos canônicos novos.
- Gerar texto diretamente do Android com chave embarcada.
