# 038 — Design da splash de abertura

## Decisões

- Usar `androidx.core:core-splashscreen` em versão estável exata para reproduzir a SplashScreen nativa nos dispositivos API 26+.
- Configurar um tema `Theme.RupturaInfinita.Starting` que aponta para o tema normal da Activity depois da transição.
- Criar um AnimatedVectorDrawable nativo: aro/fenda quebrada, seis gemas e pulso de luz nos acentos ciano e ouro. Sem bitmap externo ou rede.
- Instalar a splash no início de `MainActivity.onCreate`, antes de `super.onCreate`.
- Usar saída de escala/fade breve no ícone; se `ValueAnimator.areAnimatorsEnabled()` retornar false, remover a splash sem animar.
- Preservar a tela de introdução/entrada atual após o splash, sem alterar flags persistidas.

## Estados

1. Android exibe a tela inicial do tema Starting usando fundo cósmico e vetor animado.
2. Activity pronta: o ícone faz saída curta e a tela de entrada existente fica visível.
3. Animações desativadas: remoção imediata e a mesma tela de entrada.

## Segurança, dados e performance

- Não usar rede, chaves ou dados pessoais.
- Não reter o splash à espera de Comic Vine, Deadpool ou banco local.
- Drawable vetorial pequeno, sem dependências de mídia.

## Estratégia de testes

- Confirmar que o componente launcher usa o tema Starting e que o tema de retorno é o tema normal.
- Abrir `MainActivity` pelo runner e confirmar que a tela existente é alcançada; validar que não há bloqueio de inicialização.
- `testDebugUnitTest`, `lintDebug`, `assembleDebug`, `assembleDebugAndroidTest` e instrumentação no AVD.

## Rollback

Remover a dependência, retornar o tema da Activity para `Theme.RupturaInfinita` e retirar a chamada de instalação; gameplay e armazenamento não são afetados.
