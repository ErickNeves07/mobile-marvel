# 009 — Hub local do Nexus: Requirements

Status: **derivado da função de Nexus nos docs; navegação da spec 002 permanece inalterada.**

## Objetivo

Dar função útil de ponto de partida ao Nexus com atalhos locais para os demais destinos, mantendo Forja como entrada do app.

## Fontes

- `docs/09-DOMAIN_UX_CATALOG.md`: Nexus inicia o fluxo demonstrável.
- `docs/02-REQUIREMENTS.md`: cinco destinos e escopo portrait-first.
- DEC-020 / spec 002: barra inferior e ordem de destinos aprovadas; Forja inicial; placeholders locais permitidos.

## Escopo

- Exibir no painel Nexus quatro cartões/atalhos para Campanhas, Forja, Coleção e Deadpool.
- Tocar um atalho seleciona o destino correspondente, atualiza painel e barra inferior e comunica seleção semântica.
- Usar nomes e resumos existentes por destino; texto de ação explícito.
- Manter a barra inferior como navegação persistente.

## Fora de escopo

- Dashboard, estado do jogador, cronômetros, conquistas, recursos, vitórias, alertas, dados do backend ou recomendações dinâmicas.
- Alterar destino inicial Forja ou ordem da navegação.
- Acesso externo, persistência ou novo recurso de gameplay.

## Requisitos

- **NEXUS-R-001:** Nexus oferece acesso direto a cada outro destino do shell.
- **NEXUS-R-002:** atalhos e barra selecionam a mesma enumeração `AppDestination` e mantêm um único estado selecionado.
- **NEXUS-R-003:** atalhos têm rótulos/semântica acessíveis, target de ao menos 48dp e fonte escalável.
- **NEXUS-R-004:** painel não exibe métricas ou progresso inventados.
- **NEXUS-R-005:** interação funciona offline e não muda persistência nem destination inicial.

## Critérios de aceite

- [x] AC-01 — os quatro atalhos são mostrados no Nexus e cada um abre destino/cópia correspondente.
- [x] AC-02 — barra selecionada acompanha atalho; a navegação compartilha a rota já existente.
- [x] AC-03 — cada alvo tem ao menos 48dp; o atalho de fim da lista continua visível, acessível e acionável com fonte 1.3 e viewport compacta.
- [x] AC-04 — testes/build/lint passam após implementação; os cinco destinos anteriores continuam funcionando.
- [x] AC-05 — evidência AVD demonstra quatro rotas e Forge permanece tela inicial.
