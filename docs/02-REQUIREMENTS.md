# Requisitos

## Fonte e prioridade

- **R-OFF**: briefing oficial anexado, prioridade obrigatória.
- **R-USER**: decisões confirmadas pelo usuário nesta discovery.
- **R-PROD**: decisões de produto aceitas.

Em conflito, requisito oficial e decisão explícita do usuário vencem. Dúvidas bloqueiam.

## Requisitos oficiais

- **R-OFF-001**: entregar aplicativo mobile consumindo dados reais da Comic Vine API.
- **R-OFF-002**: usar `https://comicvine.gamespot.com/api/`.
- **R-OFF-003**: toda chamada Comic Vine deve incluir `api_key`, `format=json` e `User-Agent` descritivo.
- **R-OFF-004**: conteúdo Marvel deve ser validado por `publisher.name == "Marvel Comics"` e/ou publisher `4010-31`; filtro direto por publisher não deve ser presumido.
- **R-OFF-005**: explorar busca, filtros, paginação, detalhes ricos e imagens.
- **R-OFF-006**: explorar endpoints relevantes, entre eles characters, teams, issues, story_arcs, publishers e powers.
- **R-OFF-007**: otimizar payload com `field_list` quando aplicável.
- **R-OFF-008**: o app deve ser intuitivo, visualmente forte, criativo, funcional e apresentável.
- **R-OFF-009**: entrega prevista para outubro de 2026; dia e formato final ainda precisam ser confirmados.
- **R-OFF-010**: entregáveis citados: projeto compactado e vídeo anônimo; alcance do anonimato precisa ser confirmado.

## Requisitos do produto

- **R-USER-001**: Android Studio e Java.
- **R-USER-002**: orientação portrait-first.
- **R-USER-003**: visual híbrido cinematográfico + quadrinhos; visual é o principal diferencial.
- **R-USER-004**: merge de fragmentos em quatro estágios: Estilhaço → Fragmento → Núcleo Instável → Joia Completa.
- **R-USER-005**: Joias completas ocupam seis encaixes; não se fundem entre si.
- **R-USER-006**: Manopla de Contenção criada por Reed Richards e Doutor Estranho abre a Câmara de Variantes.
- **R-USER-007**: coleção com comparação e separação entre dados editoriais e dados do jogo.
- **R-USER-008**: desafios diários estilo Marvledle/Termo com recompensa.
- **R-USER-009**: batalhas e campanhas alimentam merge, coleção e progressão.
- **R-USER-010**: 21 personagens jogáveis e cinco variantes cada (105 no catálogo).
- **R-USER-011**: vilões são chefes no primeiro ciclo; Magneto é chefe X-Men e Xavier é jogável como suporte.
- **R-USER-012**: Quarteto Fantástico completo e campanha própria; X-Men têm campanha própria.
- **R-USER-013**: IA real por FastAPI e adapter Groq; Erick confirmou o uso de `GROQ_API_KEY`. Não expor chave no APK.
- **R-USER-014**: Deadpool apresenta IA com sarcasmo controlado e quebra da quarta parede.
- **R-USER-015**: IA pode gerar briefing, epílogo, análise de equipe e pistas; não decide fatos, regras, dificuldade nem recompensas.
- **R-USER-016**: segredo de Comic Vine e Gemini nunca fica no APK.
- **R-USER-017**: diante de dúvida crítica, parar e perguntar ao usuário.
- **R-USER-018**: desafio diário local deduz personagem do roster; até seis palpites, feedback por facção e ordem alfabética. A primeira vitória de cada data concede exatamente um Fragmento da Joia determinística do dia; derrota e repetição não concedem recompensa.
- **R-USER-019**: campanhas X-Men/Magneto e Quarteto Fantástico/Doutor Destino, equipes de três da facção, três missões determinísticas. A primeira vitória de cada missão concede quatro Fragmentos da Joia própria, créditos e XP específicos conforme `specs/028-lovable-economy-parity/design.md`; substitui três Estilhaços para novas vitórias, sem prêmio retroativo e sem `Homem-Aranha +1`.
- **R-USER-020**: recompensas são locais, persistidas e idempotentes; IA e Comic Vine não determinam regras/balanceamento.

## Requisitos não funcionais

- **RNF-001**: funcionamento offline degradado por cache/local fallback.
- **RNF-002**: estados de loading, vazio, erro, retry e conteúdo indisponível.
- **RNF-003**: acessibilidade: contraste AA, alvos >= 44dp, conteúdo não dependente só de cor e redução de movimento.
- **RNF-004**: segurança: TLS, segredos no backend/ambiente, logs sem PII/chaves e validação de entrada/saída.
- **RNF-005**: performance: listas paginadas, imagens com cache, animações dentro do orçamento de frame e startup medido.
- **RNF-006**: testes unitários, integração e UI para fluxos críticos.
- **RNF-007**: dependências com versões exatas e lockfiles/verification quando suportado.

## Fora do MVP inicial

- PvP, multiplayer, monetização, login social, ranking global, landscape completo e combate livre em tempo real.

## Dúvidas já conhecidas

Registradas em `memory/OPEN_QUESTIONS.md`. Nenhuma resposta deve ser presumida.
