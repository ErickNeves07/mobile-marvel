# 013 — Acentos visuais nos cartões de campanha: Requirements

Status: **fatia de design autorizada pela delegação DEC-020; nenhuma regra de campanha muda.**

## Objetivo

Dar um detalhe visual distinto às prévias de X-Men e Quarteto Fantástico usando a identidade documentada, sem acrescentar fatos nem ações.

## Escopo

- Filete estático discreto no topo de cada cartão de campanha.
- X-Men usa `accent_gold` como acento âmbar do brutalismo tecnológico azul/âmbar; Quarteto usa `accent_cyan` como acento azul do retrofuturismo azul/branco.
- Tokens e layout Android existentes; não adicionar dependências/assets externos.
- Título, equipe, chefe/status e comportamento permanecem idênticos.

## Fora de escopo

- Novas campanhas, objetivos, recompensas, detalhes/ações, imagens ou integração backend.
- Interpretar cor como estado, raridade ou disponibilidade.

## Requisitos

- **CAM-VIS-R-001:** as duas campanhas são visualmente distinguíveis pelo filete e mantêm a mesma hierarquia textual.
- **CAM-VIS-R-002:** cor é decorativa, texto continua expressando todo conteúdo/status e a linha não recebe foco/acessibilidade.
- **CAM-VIS-R-003:** somente X-Men recebe gold e Quarteto Fantástico cyan conforme direção visual existente.
- **CAM-VIS-R-004:** card, barra fixa e conteúdo continuam usáveis em tamanhos/fonte ampliada.

## Critérios de aceite

- [x] AC-01 — tokens escolhidos aplicados aos respectivos cartões, sem hardcode.
- [x] AC-02 — linha decorativa não altera árvore semântica ou alvos.
- [x] AC-03 — Android tests/lint/assemble passam.
- [x] AC-04 — AVD padrão, compacto e fonte 1.3 inspecionados.
