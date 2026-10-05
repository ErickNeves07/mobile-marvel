# 039 — Áudio, feedback e simplificação da Forja

## Objetivo

Aplicar os cinco ajustes solicitados: sons de interface em mais áreas, trilha ambiente opcional nas batalhas, feedback de batalha e fusão mais fácil de perceber, recuperação mais útil quando Deadpool/IA falha, Forja simplificada e dicas de combate um pouco mais claras.

## Escopo

- Adicionar sons curtos a navegação e ações principais e uma trilha instrumental original, discreta e em loop somente durante uma batalha ativa. Usar a preferência existente de som para ambos.
- Aumentar duração/contraste e acessibilidade visual dos resultados de ação em batalha e das fusões da Forja.
- Remover a explicação da cadeia Estilhaço → Fragmento → Núcleo → Joia e os resumos repetidos por Joia da tela Forja.
- Exibir o inventário como pedras/tipos de item em cartões táteis; apresentar uma ação de fusão por combinação disponível, sem botão azul genérico.
- Melhorar o texto de erro/retorno de Deadpool e investigar configuração, contrato e caminho de falha do backend sem expor chaves.
- Ajustar somente a clareza textual de intentos e resultados de batalha.

## Fora de escopo

- Alterar receitas 2:1, custos, inventário, recompensas, composição de equipe, fórmulas, escolhas, dificuldade, vitória/derrota ou balanceamento.
- Fazer chamadas pagas a provedores de IA sem autorização explícita.
- Publicar, fazer push, instalar no dispositivo ou executar deploy.

## Critérios de aceite

- [ ] AC-01 — Sons de interface e trilha de batalha são controlados pela preferência existente, com volume discreto e sem áudio de batalha fora do combate.
- [ ] AC-02 — Feedback de batalha aparece com maior contraste por tempo suficiente para leitura; leitores de tela recebem atualização acessível.
- [ ] AC-03 — Efeitos de fusão duram mais e deixam claro o resultado; fusão de Joia mostra sua chegada à Manopla.
- [ ] AC-04 — A Forja não contém cadeia educativa nem linhas numéricas por estágio/Joia; fusões continuam disponíveis de modo direto e os encaixes e estoque real continuam corretos.
- [ ] AC-05 — Deadpool explica a falha de modo acionável e a resposta roteirizada continua utilizável offline; o caminho online envia os campos do contrato atual.
- [ ] AC-06 — Mensagens de combate identificam com um pouco mais de clareza o que ocorreu e o tipo de resposta que tende a funcionar, sem alterar o estado nem entregar a solução completa.
- [ ] AC-07 — Unit tests, lint, APK e testes instrumentados passam; smoke HTTP GET pode confirmar saúde/configuração do backend sem custo.

## Premissas reversíveis

- A trilha ambiente é som original sintetizado para este app e permanece desligável no botão SOM.
- Texto levemente mais claro não altera regras nem resultados do combate.
