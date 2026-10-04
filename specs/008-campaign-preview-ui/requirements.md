# 008 — Prévia local de campanhas na navegação: Requirements

Status: **conteúdo e destino aprovados; escopo de prévia deriva dos docs e DEC-020.**

## Objetivo

Dar ao destino Campanhas uma apresentação útil e honesta das duas campanhas próprias já requeridas, mantendo o fluxo demonstrável offline enquanto as missões não estão implementadas.

## Fontes

- `docs/02-REQUIREMENTS.md`, R-USER-011/R-USER-012: campanha própria dos X-Men, Magneto chefe; campanha própria do Quarteto Fantástico.
- `docs/09-DOMAIN_UX_CATALOG.md`: grupos, campanha X-Men e Magneto como chefe.
- `specs/002-android-app-shell/requirements.md`: placeholders locais autorizados, sem resposta remota simulada.
- DEC-020: navegação Campanhas, barra inferior e placeholders nesta fatia.

## Escopo

- No destino Campanhas, exibir cartões informativos locais para X-Men e Quarteto Fantástico.
- Indicar em texto que as campanhas estão em desenvolvimento.
- Mostrar equipe/franquia; mostrar Magneto como chefe somente no cartão X-Men.
- Manter layout acessível, escalável e sem botões que prometam gameplay.

## Fora de escopo

- Lista de missões, descrição narrativa, sequência, objetivos, roster específico, dificuldade, recompensas, combate, imagens, conexão backend/API, persistência.
- Alterações dos outros quatro destinos.

## Requisitos

- **CAMPAIGN-UI-001:** tocar Campanhas mostra cartões locais das duas campanhas aprovadas.
- **CAMPAIGN-UI-002:** estado de desenvolvimento é comunicado explicitamente; nenhum controle sugere missão executável.
- **CAMPAIGN-UI-003:** Magneto consta apenas como chefe dos X-Men; o Quarteto não ganha chefe presumido.
- **CAMPAIGN-UI-004:** layout segue tokens e continua legível/rolável em tela compacta e fonte maior.
- **CAMPAIGN-UI-005:** tela é determinística, offline e não acessa rede/persistência.

## Critérios de aceite

- [x] AC-01 — os dois cartões aparecem após tocar Campanhas e não alteram outros destinos.
- [x] AC-02 — estado em desenvolvimento e dados de facção/chefe corretos aparecem no cartão e semântica acessível.
- [x] AC-03 — escala de fonte ampliada e perfil de telefone aprovados permitem rolar até os dois cartões sem cobrir a barra inferior.
- [x] AC-04 — build, unit tests e lint passam sem novo warning introduzido.
- [x] AC-05 — screenshot da tela é arquivado em evidência local; nenhum dado/arte não aprovado.
