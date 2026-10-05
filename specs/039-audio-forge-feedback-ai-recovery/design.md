# 039 — Design

## Decisões

- Manter a configuração de áudio existente (`battle_sounds`) como controle único para efeitos de interface, efeitos de fusão/batalha e música de combate.
- Usar `MediaPlayer` em recurso local original, volume baixo, loop durante a tela de batalha e encerramento imediato ao sair, desativar som, pausar ou destruir a Activity. Sons curtos existentes via `ToneGenerator` continuam sem dependência nova.
- Exibir mensagem de ação em uma faixa elevada com fundo/realce mais forte, contraste de cor, sombra e tempo visível aumentado. Manter semântica `LIVE_REGION_POLITE`.
- Alongar animações de fusão sem atrasar transação/persistência: a espera visual só ocorre depois do merge persistido.
- Simplificar a Forja removendo blocos explicativos e cards-resumo repetidos. Renderizar cada par Joia/estágio que tem estoque como chip/cartão visual com ícone, nome e quantidade; tocar no cartão inicia a fusão quando houver entrada suficiente. Um estado não acionável mostra a quantidade sem CTA redundante. Estoque e receita continuam fornecidos por `ForgeRepository`/`ForgePolicy`.
- Para Deadpool, distinguir indisponibilidade de rede/HTTP e resposta fallback do provedor, manter botão de tentar novamente e preservar a resposta offline. Mensagens de erro não incluem URL, token, request body nem detalhes sensíveis.
- Ajustar somente textos de `warning()`/`feedback` em `LovableBattle`; nenhuma variável de resultado ou ramo decisório muda.

## Estados e ciclo de vida do áudio

1. SOM ligado fora de combate: efeitos curtos em mudanças de destino e ações principais; sem música contínua.
2. Batalha aberta: trilha começa em loop e efeitos de round continuam.
3. SOM desligado, tela de batalha fechada, Activity pausada/destruída: liberar/parar trilha e não iniciar outros sons.
4. SOM reativado: atualizar shell e iniciar trilha apenas se batalha estiver aberta.

## Segurança e regras

- Não enviar credenciais no APK; backend continua concentrando as chaves de provedores.
- Uma chamada POST ao endpoint de Deadpool pode consumir cota. Smokes GET (`/health`, `/ready`) são seguros; POST live aguarda autorização já solicitada ao usuário.
- UI e narrativa não decidem regras, fatos canônicos ou recompensas.
- O inventário usa apenas valores locais já validados; limites, operações e transações permanecem inalterados.

## Testes

- Unit: as mesmas escolhas continuam produzindo os mesmos resultados e textos atualizados cobrem contra-ataque, defesa, erro, armadilha e especial.
- Android: Forja contém encaixes/inventário/merge e não contém explicação/contadores redundantes; toggle controla trilha/sons; mensagens de batalha têm destaque e duração configurados.
- Executar `testDebugUnitTest`, `lintDebug`, `assembleDebug`, `assembleDebugAndroidTest` e instrumentação no AVD.
- Verificar Render por GET. Fazer POST de teste somente após autorização explícita.
