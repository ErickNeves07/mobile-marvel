# 040 — Contexto do Deadpool e narrativa das campanhas

Status: aprovado pelo relato e pedido de correção do Erick em 2026-10-04.

## Objetivo

- Deadpool conhece a equipe salva, variantes equipadas, campanha atual e progresso real; responde a perguntas sobre os nove adversários, inclusive Thanos, com fatos do jogo.
- Cada aviso de batalha corresponde ao adversário e cenário da missão em curso; Ultron não menciona Magneto.
- Validar regressões de coleção, forja, desafio diário, nove batalhas, progressão e integração editorial/IA antes do APK final.

## Escopo e limites

- Corrigir cliente Android e prompt/fatos permitidos do backend sem alterar dano, receitas, recompensas ou persistência.
- Manter contrato legado apenas como recuperação temporária quando Render responder 422; a experiência completa requer publicar o backend com `game_context`.
- IA é narrativa. Não decide regras nem inventa fatos canônicos.
- Testes de provedor pago, publicação e validação manual no telefone dependem da autorização existente e da disponibilidade externa.

## Aceite

- Os cartões de briefing/equipe usam missão atual e equipe `rupture` salva, jamais um trio ou campanha X-Men fixos.
- O pedido de Thanos recebe os fatos de Titã em Colapso; contexto é limitado a 1.200 caracteres e não contém segredos ou PII.
- Todas as seis fases de cada uma das nove batalhas têm pistas coerentes com o adversário e o padrão de contra-ataque; sem menção cruzada a chefes.
- Testes de backend, JVM, lint, build e instrumentação passam após a última edição; APK/versionCode identificáveis.
