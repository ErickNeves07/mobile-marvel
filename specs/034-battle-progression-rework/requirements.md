# 034 — Batalhas e Manopla: requisitos

Status: Erick aprovou explicitamente em 2026-10-04 o combate tático, a progressão de dificuldade, o filtro de personagens possuídos e uma Manopla completa como recurso consumível de desbloqueio. A estrutura exata de capítulos e o tratamento dos Fragmentos anteriores aguardam as respostas Q-048/Q-049.

## Confirmado

- O Catálogo oferece filtro de personagens já desbloqueados, combinável com busca e facção.
- Especiais devem corresponder aos poderes de um herói da equipe real; trio Homem-Aranha/Tocha Humana/Wolverine não usa ataque psíquico.
- Defender continuamente não pode ser uma estratégia vencedora automática. Cada batalha deve exigir escolhas táticas legíveis e variar adversário, sequência e dificuldade crescente. Equipe e variantes equipadas influenciam o resultado. IA pode narrar, mas não decide combate.
- Ao preencher as seis Joias, jogador escolhe entre desbloquear um dos personagens não possuídos ou o próximo patamar de um personagem possuído. A ação consome uma Joia completa de cada tipo e esvazia a Manopla usada, de forma atômica. Nova obtenção deve voltar a preencher encaixes. Posse anterior e recompensas já pagas persistem.
- Cada primeira vitória diária completa as Joias faltantes. Cada primeira vitória de batalha também deve completar as Joias faltantes; créditos/XP e Fragmentos antigos aguardam Q-048.
- Instalar a versão final desta rodada no mesmo telefone conectado, preservando dados, após testes.

## Dúvidas críticas

- **Q-048:** manter ou substituir os quatro Fragmentos por missão junto do preenchimento da Manopla; como tratar vitória com Manopla já cheia.
- **Q-049:** nove capítulos Lovable substituem seis existentes ou são acrescentados; IDs, ordem e recompensa dos capítulos adicionais.

## Aceite independente das dúvidas

- Filtro “Desbloqueados” mostra apenas personagens possuídos e combina com os demais filtros.
- Especial do trio inicial é coerente; testes exercitam outros líderes e não oferecem poder de outro personagem.
- Repetir somente Defesa não vence; ao menos um caminho tático vence a primeira batalha e missão posterior exige mais poder ou escolhas melhores.
- Banco migra sem perder posse/recursos; consumo de seis Joias para nova posse/variante é transacional e idempotente quando acionado novamente.
