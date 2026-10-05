# 043 - Deadpool, cenas de lore e Câmara

Status: aprovado pelo pedido explícito de Erick em 2026-10-05; valores e apresentação da Câmara foram delegados.

## Objetivo

Corrigir as falhas visíveis do Deadpool e das cenas pós-batalha; explicar a função de Reed Richards e Doutor Estranho dentro da Câmara de Variantes.

## Escopo

- [ ] Deadpool mantém resposta útil quando o backend rejeita `game_context` legado ou fica indisponível.
- [ ] Compatibilidade usa prompt limitado ao contrato antigo de até 300 caracteres e um `context_id` aceito em versões anteriores.
- [ ] Respostas locais de reserva são humorísticas, variam por assunto e usam apenas fatos locais allowlisted; não repetem sempre Magneto.
- [ ] Falhas/retries reabilitam o botão e não deixam a tela travada em loading.
- [ ] Cena pós-vitória mostra cada fala e retrato do locutor correspondentes; Continuar, Pular e Voltar concluem a mesma continuação exatamente uma vez.
- [ ] Voltar durante a cena não perde a tela de recompensa já concedida.
- [ ] Na primeira entrada da Câmara, uma sequência curta explica que Reed mapeia/calibra assinaturas de variantes e o Doutor Estranho sela ramificações para impedir colisões entre realidades.
- [ ] Retratos soltos de Reed/Estranho saem do Nexus; ambos permanecem representados junto da explicação operacional da Câmara.
- [ ] A explicação inicial aparece uma vez, pode ser pulada, e pode ser consultada novamente pela Câmara.

## Fora de escopo

- IA determinando fatos, combate, recompensas ou progressão.
- Fazer chamada paga a Gemini/Groq durante testes sem autorização específica.
- Alterar lore canônico além das funções de jogo já estabelecidas para Reed e Strange.

## Critérios de aceite

- AC-01: resposta atual do backend é mostrada sem deixar o botão desabilitado.
- AC-02: um HTTP 422 do contrato novo aciona compatibilidade limitada; também é coberto o caso de `context_id` desconhecido.
- AC-03: indisponibilidade de rede/provedor mostra uma resposta local contextual com aviso discreto de modo offline, nunca uma tela sem resposta.
- AC-04: limite de 300 caracteres e ausência de chaves/PII são testados.
- AC-05: cada controle de saída da cena chama a continuação uma vez e preserva o recibo/recompensa.
- AC-06: retratos atrasados não sobrescrevem o locutor da fala atual; movimento respeita redução de animações.
- AC-07: sequência da Câmara funciona uma vez, em sequência ordenada, e seu texto continua acessível sem animações.
- AC-08: Nexus deixa de mostrar Reed/Estranho como companheiros sem função; a Câmara explica claramente por que estão ali.

## Dependências e limites

- Backend local já aceita `game_context` até 6.000 caracteres e prioriza Gemini com fallback Groq/roteirizado.
- O ambiente desta tarefa não consegue consultar `https://mobile-marvel-8qex.onrender.com`; schema e presença de `GEMINI_API_KEY` publicados precisarão ser conferidos por Erick após o deploy.
