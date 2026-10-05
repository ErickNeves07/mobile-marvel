# 043 - Evidence

Status: implementacao local concluida; aguardando push e deploy.

## Auditoria inicial

- App envia `context_id=app` e `game_context`. Em 422, remove o campo e tenta novamente usando `app`; versões antigas podem rejeitar o identificador também. O segundo 422 vira erro visível.
- `DeadpoolPrompt.legacy` já limita o prompt total a 300 caracteres, mas não garante que a versão antiga aceite o `context_id` atual.
- A resposta local do backend é fixa por `context_id`; a exceção final do cliente mostra mensagem de indisponibilidade sem fala alternativa.
- A cena de vitória é cancelada por Back porque o callback global volta ao shell sem executar a continuação para a tela de recompensas.
- O Nexus mostra Reed/Estranho como imagens de companhia; suas funções só aparecem em texto descritivo da Manopla.
- Verificação live do Render foi tentada por `/ready` e `/openapi.json`, mas a rede deste ambiente recusou conexão. Nenhum POST/IA paga foi feito.

## Após implementação

Preencher comandos/testes, estado da build e capturas AVD. Não declarar Render/Gemini verificados antes de o Erick publicar e validar.

## Resultado apos implementacao

- T01-T03 implementados: retry legado usa `context_id=nexus`, prompt ate 300 caracteres preserva fatos prioritarios, fallback local contextual varia falas, back finaliza cena/continua recompensa, navega??o volta a aparecer, Nexus deixou de exibir Reed/Estranho sem contexto e briefing da Camara e reapresentavel.
- T04: 44 testes JVM Android passaram (incluindo Deadpool/story), lint e assemble passaram; backend: `pip check` limpo e `pytest` 58/58. Testes instrumentados compilam, mas nao executaram por `INSTALL_FAILED_UPDATE_INCOMPATIBLE` no unico AVD (assinatura ja instalada difere); dados nao foram apagados.
- T05: validado em 2026-10-05. Render ao vivo nao acessivel nesta sessao; apos deploy verificar `/ready`, contrato legado/atual e `GEMINI_API_KEY`. Nenhum POST ao provedor foi feito.
