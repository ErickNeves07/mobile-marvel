# 037 — Acabamento visual e contexto do jogo

## Objetivo

Dar acabamento aos fluxos de despertar da Manopla, forja, batalha, coleção e Deadpool sem mudar economia, regras de combate ou recompensas.

## Escopo

- Cards selecionáveis para desbloqueio/evolução; feedback visual distinto (cadeado destravando e feixe de luz), seguido de abertura da Coleção focada no personagem/variante afetado.
- Mescla 2:1 sem confirmação, com animação de partículas e voo da Joia completa até a Manopla.
- Efeito visual breve por ação de round, secundário ao “Krakoom” dos especiais; dicas narrativas sutis e um efeito visual de emboscada sem alterar cálculos de batalha.
- Modo claro/escuro com alternância persistida no cabeçalho.
- Seleção direta de variantes por cards destacados; remover botão “Ver detalhes e fusões” e mensagem de configuração do backend no catálogo.
- Deadpool com retrato alternado e contexto factual do estado local do jogo (personagens/variantes desbloqueados, equipe salva, campanha/batalha e recursos), com prompt contextual robusto.
- Efeitos sonoros curtos em batalhas com alternância persistida.
- Curiosidades Comic Vine por personagem com contagem de aparições, primeira publicação e equipes quando fornecidas; omitir campos ausentes.

## Fora de escopo

- Mudança de economia, custos, recompensas, atributos, chances, ordem de turnos ou resultado de combate.
- IA ou Comic Vine decidirem fatos de jogo.
- Novos assets de áudio binários ou novo provedor pago.

## Critérios de aceite

- AC-01 A escolha concluída destaca o tipo de despertar e abre o perfil do personagem afetado.
- AC-02 Mesclas válidas começam sem diálogo de confirmação; os itens continuam transacionais e a animação respeita redução de movimento.
- AC-03 Cada ação tem feedback visual discreto; especial mantém maior destaque; dicas não prescrevem a ação certa.
- AC-04 Tema claro/escuro sobrevive à reinicialização e o controle está no topo do app.
- AC-05 Cards de variante são diretamente tocáveis e têm estado selecionado/posse claro.
- AC-06 Deadpool recebe contexto limitado, estruturado e apenas de jogo; respostas não inventam dados fora do contexto e o retrato alterna.
- AC-07 Efeitos de batalha podem ser desligados; nenhum som fora da batalha.
- AC-08 Catálogo não exibe o texto de configuração removido; curiosidades exibem somente dados editoriais retornados.
- AC-09 Backend e Android testam contexto/DTO, metadados ausentes, preferências, fluxo de merge e regressões de combate.

## Dúvidas

Nenhuma regra crítica em aberto. Premissas reversíveis: modo escuro inicial para preservar a aparência atual; efeitos sonoros ligados inicialmente em volume baixo e com controle para desligar.
