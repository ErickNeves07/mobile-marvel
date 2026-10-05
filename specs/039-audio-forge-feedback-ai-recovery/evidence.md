# 039 - Evidencias

## Implementacao

- Nenhuma regra de batalha, recompensa, custo, quantidade concedida ou receita de fusao foi alterada.
- Audio: tons curtos nas navegacoes/acoes, trilha original sintetizada em loop somente na batalha, volume baixo, desligavel pelo controle de audio existente e parada ao sair/pausar.
- Batalha: avisos e impactos ficam mais contrastados e permanecem por mais tempo; mensagens ficaram um pouco mais claras sem alterar calculos.
- Forja: removidas cadeia explicativa e linhas de 0 a 999; inventario virou cartoes de pedra/tipo/quantidade, com toque direto para fundir.
- Deadpool: HTTP 422 com game_context gera uma segunda tentativa sem esse campo, para compatibilidade com Render antigo. O cliente continua incluindo contexto quando o servidor novo aceita.

## Verificacao

| Data | Comando/consulta | Resultado |
|---|---|---|
| 2026-10-04 | Gradle testDebugUnitTest, lintDebug, assembleDebug, assembleDebugAndroidTest --offline | Passou apos a ultima alteracao de producao |
| 2026-10-04 | Android instrumentation completa no APK final | 33/33 passaram em 202,0 s |
| 2026-10-04 | LovableScreensTest#chosenTeamCanLoseAndRetryWithoutReward | Passou isoladamente apos alinhar o intervalo |
| 2026-10-04 | LovableScreensTest#forgeShowsTopModeControlsAndCompletedStoneAnimation | Passou no APK final |
| 2026-10-04 | GET /ready em Render | status=ok; Comic Vine, Groq e Gemini configurados |
| 2026-10-04 | GET /openapi.json em Render | Contrato publicado ainda lista somente context_id e prompt; nao expoe game_context e fecha campos adicionais |
| 2026-10-04 | APK debug 0.8.0 | Copia local em artifacts; conferir hash no RELEASE_NOTES |

## Limites pendentes

- Nao foi enviada chamada POST a Deadpool: pode consumir cota. A causa contratual da indisponibilidade foi confirmada por OpenAPI e o app agora tem retry compativel.
- O contexto completo do jogador so chega a Deadpool depois de o Render publicar a versao nova do backend.
