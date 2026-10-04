# 015 — Variantes como linhas acessíveis na Coleção: Requirements

Status: **aprimoramento de acessibilidade derivado de R-USER-007/RNF-003; conteúdo não muda.**

## Objetivo

Expor cada variante do catálogo como um item de texto independente e em ordem lógica, em vez de agrupar cinco linhas em um único bloco.

## Escopo

- Renderizar cinco TextViews simples por personagem, na ordem dos tiers do JSON.
- Manter o texto visível `Tier: Nome` e estilo atual do corpo.
- Preservar grupos, cabeçalho de personagem, rolagem e conteúdo offline.
- Garantir ordem semântica: grupo → personagem → rótulo Variantes → Origem…Infinito → próximo personagem.

## Fora de escopo

- Seleção/posse/favorito, edição, ações e estados de jogo.
- Novas dependências, labels sonoros, imagens ou alterações no JSON/backend.

## Requisitos

- **A11Y-VAR-R-001:** cada um dos 105 nomes é um nó textual independente na hierarquia acessível.
- **A11Y-VAR-R-002:** texto e ordenação permanecem iguais ao JSON e à tabela de domínio.
- **A11Y-VAR-R-003:** linhas não são clicáveis, selecionáveis ou anunciadas como possuídas.
- **A11Y-VAR-R-004:** lista segue rolável e legível em viewport compacto/fonte 1.3.

## Critérios de aceite

- [x] AC-01 — hierarquia AVD expõe 21 personagens e 105 nós de variante separados, em ordem.
- [x] AC-02 — conteúdo visual permanece legível e completo por rolagem em fonte padrão/1.3 e viewport compacto.
- [x] AC-03 — Android tests/lint/assemble passam sem dependência nova.
