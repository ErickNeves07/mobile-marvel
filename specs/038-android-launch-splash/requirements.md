# 038 — Splash de abertura

## Objetivo

Exibir uma abertura curta, cinematográfica e reconhecível como Marvel: Ruptura Infinita sempre que o app for iniciado, sem atrasar a navegação além da animação padrão do Android.

## Escopo

- Usar a SplashScreen API AndroidX para compatibilidade uniforme do minSdk 26 até Android atual.
- Aplicar identidade cósmica do app: fundo escuro, fissura/infinito e seis luzes coloridas.
- Fazer transição breve da splash para a tela de entrada existente.
- Honrar a escala de animação/redução de movimento do sistema.

## Fora de escopo

- Alterar onboarding, tela inicial, navegação, preferências de tema, dados ou regras do jogo.
- Fazer chamadas de rede ou esperar carregamento de retratos na splash.
- Manter splash artificialmente enquanto tarefas demoradas carregam.

## Critérios de aceite

- [ ] AC-01 — O cold start usa o tema de partida AndroidX, com o ícone animado e fundo da marca.
- [ ] AC-02 — Ao fechar a splash, o app chega à tela de entrada atual sem telas vazias ou flash de cores.
- [ ] AC-03 — O efeito de saída é curto, funciona em API 26+ e é removido quando animações do sistema estão desativadas.
- [ ] AC-04 — Splash não carrega rede, não muda estado de onboarding e não afeta dados existentes.
- [ ] AC-05 — Gradle, lint e testes Android passam; verificar a configuração de tema e transição em teste instrumentado.

## Dúvidas

Nenhuma bloqueante. Premissa reversível: manter a tela de introdução atual depois da splash, pois ela contém a ação de entrada do usuário e não há pedido para removê-la.
