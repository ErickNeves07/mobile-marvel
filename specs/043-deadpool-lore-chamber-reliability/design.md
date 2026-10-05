# 043 - Design

## Decisões

- O pedido de corrigir Deadpool, lore e Câmara aprova o escopo; a solução visual da Câmara foi delegada ao agente.
- `POST /v1/ai/deadpool-line` continua sendo o caminho principal. Em HTTP 422, o cliente tenta um corpo legado com `context_id=nexus` e prompt factual compacto limitado a 300 caracteres. O cliente não faz retry para erros 5xx nem repete chamadas de IA.
- Se o endpoint/provedor não responder, um catálogo local de falas curtas e determinísticas atende pedidos gerais, opinião sobre antagonistas e perguntas sobre equipe/progresso. A UI marca resposta local de reserva; nenhum estado de jogo é alterado.
- O payload completo permanece allowlisted, até 6.000 caracteres, sem PII, chaves ou respostas editoriais. O backend continua fonte apenas de narrativa.
- A cena usa locutor/ID editorial por linha. Um token de geração impede resposta de retrato antigo. Um único finalizador é compartilhado por Continuar, Pular e Voltar; Voltar conclui a cena e executa a continuação da recompensa.
- A Câmara passa a ser o lugar visual de Reed/Estranho: explicação animada uma vez na primeira abertura de variantes, opção de rever, papéis explícitos e imagens atribuídas. Remover retratos deles da área Nexus.

## Dados e persistência

- Sem migração SQLite. Preferência local `chamber_briefing_seen` controla a primeira apresentação; cenas pós-batalha permanecem ligadas ao fluxo existente.
- Compras/recompensas e regras de combate ficam fora desta spec.

## UX

- Loading é limitado à chamada assíncrona; resposta mantém o campo e botão utilizáveis.
- Cena mantém título/local, locutor, retrato com crédito, diálogo, progresso e ações acessíveis. Desligar animações mostra imediatamente a mesma informação.
- Câmara explica: Reed mapeia/calibra a assinatura variante; Strange mantém cada realidade isolada com selos; os heróis continuam sob controle do jogador.

## Segurança

- Chaves ficam apenas no backend. Não registrar prompt, `game_context` ou PII nos logs.
- Fallback legado é restrito a 300 caracteres; não contorna validações de status diferentes de 422.

## Verificação

- JVM: limites/retenção de conteúdo e variação das respostas locais; compatibilidade 422/contexto legado.
- Backend: schema, allowlist, sanitização, Gemini→Groq→fallback, todos com transporte simulado.
- Android: transição de cena, callback único, voltar/pular/recompensa e resposta de retrato atual.
- Build, lint e instrumentação AVD; sem chamada paga, instalação física ou deploy.
