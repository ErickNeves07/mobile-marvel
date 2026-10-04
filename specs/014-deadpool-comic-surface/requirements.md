# 014 — Superfície em linguagem de quadrinhos para Deadpool: Requirements

Status: **fatia visual autorizada por DEC-020; sem texto narrativo ou IA nova.**

## Objetivo

Diferenciar o hero do destino Deadpool com geometria estática de quadro de quadrinhos, mantendo o placeholder, identidade híbrida e acessibilidade.

## Escopo

- Criar fundo nativo Canvas com filetes cinéticos e recortes angulares discretos no topo do cartão.
- Reutilizar gradiente/borda e tokens existentes de superfície, ciano e ouro.
- Aplicar apenas a `AppDestination.DEADPOOL`.
- Preservar o ícone de fala, descrição/status genéricos, layout, semântica e ações.

## Fora de escopo

- Falas/piadas, fatos de lore, conexão Gemini, interação/IA, imagens licenciadas, som/animação.
- Alterar outros destinos.

## Requisitos

- **DP-COMIC-R-001:** hero de Deadpool tem detalhes estáticos de quadrinhos reconhecíveis e discretos.
- **DP-COMIC-R-002:** decoração fica na faixa superior, não cruza texto e não cria nó/foco/ação.
- **DP-COMIC-R-003:** usa cores tokenizadas existentes sem dependência externa.
- **DP-COMIC-R-004:** tela continua legível com escala de fonte Android e janela compacta.

## Critérios de aceite

- [x] AC-01 — fundo comic aplicado somente no hero Deadpool.
- [x] AC-02 — geometria fica nos bounds e não interfere em conteúdo acessível.
- [x] AC-03 — Android tests/lint/assemble passam.
- [x] AC-04 — AVD normal/compacto/fonte 1.3 inspecionados; demais telas sem mudança.
