# MVPs e roadmap

## Estratégia

Priorizar fatias verticais demonstráveis. O objetivo não é implementar 105 artes antes de provar o loop.

## MVP 0 — Fundação

- projetos Android/Java e FastAPI;
- CI local, lint e testes básicos;
- navegação e design tokens;
- contratos e dados fake;
- cofre local de configuração sem segredos no Git.

Índices locais implementados: resumos de campanha e catálogo jogável compartilhado de 21 personagens com 105 nomes de variantes (backend e asset Android). O catálogo editorial continua separado e depende de Comic Vine. A UI Android permanece offline; conectar cliente e backend depende de Q-025.

Saída: app abre/navega e endpoints locais fake passam por testes; consumo Android do backend aguarda ambiente/base URL Q-025.

## MVP 1 — Coleção real

- Comic Vine via backend;
- filtro Marvel `4010-31`;
- busca, filtros, paginação, detalhe, imagens e cache;
- coleção com dados editoriais separados dos dados do jogo;
- estados offline/erro.

Saída: atende o núcleo obrigatório do desafio.

## MVP 2 — Vertical slice impactante

- Nexus;
- Forja com merge roteirizado das Joias;
- Manopla de Contenção;
- Câmara de Variantes;
- uma equipe de três;
- campanha X-Men contra Magneto;
- batalha semiautomática curta;
- recompensa e progressão.

Saída: demo completa de 5–8 minutos.

## MVP 3 — IA e desafio diário

- Deadpool/IA real;
- briefing e análise de equipe estruturados;
- desafio diário tipo Marvledle;
- fallback sem rede/provider;
- limites e telemetria.

## MVP 4 — Conteúdo e polimento

- 21 personagens no catálogo;
- 105 variantes orientadas a dados;
- campanhas Quarteto, cósmica, mística e Latveria;
- animações, acessibilidade, performance e estabilidade;
- pós-créditos de Thanos.

## Prioridade de esforço

1. visual e experiência;
2. estabilidade do fluxo demonstrável;
3. uso rico da Comic Vine;
4. IA real, restrita e confiável;
5. amplitude de conteúdo.

## Anti-escopo

Não iniciar PvP, login, loja, ranking, multiplayer ou landscape completo antes do MVP 4 aprovado.
