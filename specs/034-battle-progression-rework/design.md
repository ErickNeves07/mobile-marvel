# 034 — Batalhas e Manopla: design

- O combate continua deterministicamente local. `BattleMission` guarda oponentes e dificuldade crescente; `LovableBattle` usa a intenção anunciada, contra-ação, poder da equipe e limite de turnos. Defesa reduz dano mas não gera dano/carga suficiente para vencer sozinha. Especial consome carga e causa dano limitado; não mata automaticamente.
- A habilidade especial recebe nome/efeito authored conforme o primeiro herói selecionado. O texto menciona apoio dos demais sem atribuir a eles poderes que não têm. Regras e números continuam no Java, sem Comic Vine ou IA.
- `ForgeRepository` mantém um ciclo de Manopla: ativação significa Câmara liberada permanentemente; prontidão para novo desbloqueio exige uma unidade completa de cada Joia. Transação consome todas as seis e concede exatamente um personagem Origem ou próxima variante. Joias intermediárias não são apagadas; “esvazia” refere-se aos seis encaixes completos.
- Recompensas de campanha/diário preencherão apenas encaixes completos ausentes, sem gerar reserva automática; alterações exatas de Fragmentos/novos capítulos dependem de Q-048/Q-049. Vitórias já registradas não serão pagas retroativamente.
- UI de escolha de desbloqueio aparece quando a Manopla está pronta, com cards do elenco não possuído e patamares sequenciais disponíveis; saldo/estado atualizam após decisão.
- Testes Android JVM para estratégia Defesa, dificuldade, especial; instrumentados para filtro e transação de consumo/migração. Nenhum teste mutável no celular de Erick.
