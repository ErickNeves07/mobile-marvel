# 037 — Design

## Decisões

- Manter as regras e persistência de jogo atuais. As animações são feedback apenas; reusar `ForgeRepository.merge` como fonte transacional.
- Usar `AppCompatDelegate` DayNight e preferência local; cores de superfície em `values/colors.xml` (claro) e `values-night/colors.xml` (escuro). Preservar paleta atual no escuro.
- Cartões inteiros acessíveis para escolher alvo e variante; manter rótulos de posse, seleção e tier como texto/semântica além de cor.
- Deadpool request inclui `context_id`, `prompt` e resumo estruturado limitado por allowlist (máximo de 1.200 caracteres) de posse, tiers, equipe salva, campanha/última missão e saldo agregado. Backend trata como fatos fornecidos pelo app, sem instruir IA a alterar regras. Sem PII.
- Acrescentar metadados opcionais à resposta editorial: contagem de aparições, data de capa da primeira edição e nomes de equipes. Campo ausente permanece null/lista vazia.
- Efeitos sonoros sintéticos via `ToneGenerator`, controlados por preferência local, somente durante ações de batalha.
- Animações usam `ValueAnimator`/View animation existentes e pulam quando o sistema desativa animações.

## Testes

- Backend: esquema bounded/extra-forbid, prompt recebe contexto e preserva fallback.
- Android: contexto factual, efeitos de batalha sem alteração de valores, preferências de tema/som, merge transacional sem diálogo, mapeamento de metadados opcionais e testes de regressão existentes.
- Build/lint/instrumentação; inspecionar capturas das telas alteradas.
