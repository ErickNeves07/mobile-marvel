# 028 — Economia e recompensas do protótipo Lovable — Evidence

Estado em 2026-10-04: fusão 2:1, seis pacotes de campanha e um Fragmento da Joia do dia na primeira vitória diária estão implementados.

## Fonte e decisões

- Bundle público `forja-7Sw7Y5vx.js` e captura Lovable: duas peças iguais por fusão. Erick aprovou 2:1 apenas daqui em diante.
- A rota pública `/recompensa` exibia quatro linhas de demonstração, mas seu código concedia apenas créditos/XP. Erick retirou `Homem-Aranha +1`, confirmou que o pacote substitui três Estilhaços por missão, exigiu valores distintos e aprovou a tabela de seis pacotes em `design.md`. Missões antigas não recebem a diferença.
- Erick escolheu exatamente um Fragmento da Joia determinística do dia, sem complemento por vitórias antigas.

## Implementação e teste

- `CampaignReward` fixa Joia/créditos/XP de cada missão. `ForgeRepository.completeMission` acrescenta quatro Fragmentos, créditos e XP no mesmo evento SQLite, com limite de estoque e idempotência. Banco v5 acrescenta `player_resources` sem pagar novamente missões existentes.
- `GameLoopRepositoryTest` verifica as seis Joias e totais, ordem da campanha, repetição e persistência. `ForgeRepositoryTest` verifica migração v4→v5 sem backfill e rollback quando o estoque de Fragmentos está cheio.
- `LovableScreensTest` percorre vitória/claim da primeira missão X-Men, confere os três itens reais e a ausência de `Homem-Aranha +1`; captura `campaign-reward.png`. A captura foi inspecionada lado a lado com `reports/lovable-research/lovable-recompensa-mobile.png`: três cartões angulares, cabeçalho central e botões amarelo/escuro seguem a referência publicada.
- Após a última edição de Java/XML: `scripts/validate-local.ps1 -Target android` passou (JVM, lint debug, assemble); `scripts/run-android-instrumentation.ps1` passou **22/22** no AVD. O teste cobre a concessão diária, idempotência, persistência e rollback no limite de estoque. Teste manual em aparelho físico continua pendente.
- `scripts/build-release.ps1` passou build/lint release, zipalign e assinatura v2. Hash e tamanho do APK atualizado estão em `RELEASE_NOTES.md`.

## Limites

- Vitórias diárias anteriores não recebem ajuste retroativo.
- Créditos e XP agora têm saldo persistido e aparecem no Nexus; não compram itens nem alteram nível, pois tais usos não foram definidos.
- O app usa retratos editoriais Comic Vine dos 21 personagens, repetidos nas variantes conforme decisão posterior de Erick; ainda precisa do host HTTPS e teste manual físico. A captura instrumentada não substitui navegação manual.
