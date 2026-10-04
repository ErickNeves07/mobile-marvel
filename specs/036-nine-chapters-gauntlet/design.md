# 036 — Design

- `BattleMission` será a fonte authored local dos nove nós, na ordem apresentada, com ID estável, chefe, localização, dificuldade e retrato editorial via proxy. A UI será uma trilha vertical cósmica conectada, com status por nó e imagem do adversário. IA/Comic Vine não definem regras.
- `campaign_progress` e `completed_missions` persistem progressão. Uma migração explícita converterá IDs antigos somente se Erick solicitar; não marcar vitória nova que o jogador não venceu.
- `ForgeRepository` concederá prêmio numa única transação por evento, computando para cada Joia somente os Fragmentos necessários até uma Joia Completa possível, considerando Fragmentos/Núcleos e Estilhaços convertíveis pela receita 2:1. O jogador faz as fusões manualmente. Saldo de créditos/XP é parte da mesma transação. A tela mostra os seis valores realmente concedidos.
- `onCreate` inicia cada Joia com três Fragmentos; `onUpgrade` não injeta esses Fragmentos em perfis existentes.
- Um ciclo de Manopla requer `COMPLETE >= 1` nas seis Joias. Desbloqueio de personagem ou próxima variante valida alvo/posse/ordem, debita uma Joia de cada tipo e concede o alvo na mesma transação. A Câmara pode continuar acessível depois do primeiro uso, mas o próximo desbloqueio exige outro ciclo cheio.
- O cache de retratos permanece editorial e remoto, nunca embarca binários Comic Vine no APK. Boss IDs serão auditados antes de expor links.
- Créditos/XP dos seis primeiros nós permanecem na sequência aprovada: 3000/840, 3300/920, 3800/1100, 3100/860, 3500/1000, 4000/1200. Os três finais usam 4500/1400, 5200/1650 e 6200/2100, em ordem crescente de ameaça.
- Testes instrumentados de banco cobrem migração, prêmio idempotente, seleção, consumo, rollback e alvo inválido. Testes JVM cobrem ordem/nove capítulos e dificuldade. AVD verifica mapa e imagens; telefone somente depois de deploy confirmado por Erick.

## Atualização visual e editorial
- O mapa compacto mostra capa Comic Vine, título, chefe, poder recomendado publicado, estado e progress bar colorida. O toque abre a ficha com imagem/credito, prêmios da primeira vitória e início/replay.
- Valores de poder foram lidos do bundle Lovable e estão cobertos por BattleMissionTest; combate continua com dificuldade progressiva e regras locais authored.
