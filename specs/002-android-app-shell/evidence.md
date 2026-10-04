# 002 — Android App Shell: Evidence

Status: **implementado e validado em build, testes, lint e AVD; leitura falada em TalkBack permanece como verificação manual futura.**

## Verificação automatizada

| Data | Comando/verificação | Resultado |
|---|---|---|
| 2026-09-27 | `scripts/validate-local.ps1 -Target all` | `BUILD SUCCESSFUL`; `testDebugUnitTest`, `lintDebug`, `assembleDebug`, `pip check` e pytest concluídos. Android: 4 testes, 0 falhas; backend: `1 passed`. |
| 2026-09-27 | `scripts/validate-local.ps1 -Target android` | `BUILD SUCCESSFUL`; execução adicional após instalação do APK final. |
| 2026-09-27 | Android tests após specs 008/009/010 | 6 testes unitários, 0 falhas; runner combinado passou após adição das prévias e hub. |
| 2026-09-27 | APK debug instalado no AVD e `uiautomator dump` após a instalação final | Processo ativo, Activity principal em execução e nó `Forja, selecionado` presente. |
| 2026-09-27 | Reinstalação do APK após spec 008; force-stop e nova abertura | Forja voltou a iniciar selecionada; processo ativo. |

## Verificação visual e de interação

- AVD `Medium_Phone_API_36.1`, tela 1080×2400 px a 420 dpi (aprox. 411×914 dp): cinco destinos tocados e conteúdo correspondente exibido; estado selecionado anuncia o destino e selecionado.
- Perfil compacto: 1024×2216 px a 420 dpi (aprox. 390×844 dp). Todos os destinos e seleção da Forja permanecem visíveis; sem corte.
- Escala do sistema 1.3: destinos e seleção permanecem presentes e sem corte. Configurações de tela e fonte foram restauradas após as medições.
- Bounds da barra: cada alvo mede 48 dp de altura e aproximadamente 76.6–77 dp de largura. Nós incluem rótulo e estado `selecionado`.
- O AVD exibiu uma notificação de ANR no processo de `com.android.systemui`; o app permaneceu ativo e retomado. A inspeção da hierarquia do APK final confirma a Forja.
- TalkBack não foi ativado para validar leitura falada. Inspeção de hierarquia não equivale a um ensaio completo de leitor de tela.

Screenshots locais em `android-app/.validation-output/` (artefatos ignorados pelo Git):

- `shell-nexus.png`, `shell-campaigns.png`, `shell-forge.png`, `shell-collection.png`, `shell-deadpool.png`.
- `shell-forge-compact.png` e `shell-forge-font130.png`.

## Implementação observada

- Cinco painéis locais distintos; destinos tipados em ordem aprovada e Forja inicial.
- Navegação inferior, seleção visual e semântica; ícones vetoriais originais, sem dependência de fonte ou imagens de terceiros.
- Placeholders explicitamente em desenvolvimento, sem rede nem resposta remota simulada.
- Build do APK instalado corresponde às fontes finais após inclusão do ícone adaptativo e regras de backup.

## Limitações

- TalkBack falado não foi testado.
- Painéis permanecem placeholders; regras e dados das features pertencem a specs futuras.
- AVD apresentou instabilidade do System UI não atribuída ao processo do app.

## Ensaio local de acessibilidade — 2026-09-28

- TalkBack já estava instalado no AVD; foi ativado temporariamente e desligado ao fim. Nenhuma dependência foi instalada.
- `dumpsys accessibility` confirmou o serviço habilitado durante o ensaio; a hierarquia manteve rótulos de destinos e `Forja, selecionado`/`Coleção`.
- Navegação por teclado gerou síntese no mecanismo TTS (`GoogleTTSServiceImpl`); não há captura acústica nesta sessão. O locale do emulador é `en-US`, portanto a qualidade de pronúncia em português permanece sem validação.
- O prompt opcional de notificações do pacote TalkBack foi recusado e os flags de permissão foram restaurados ao estado anterior; TalkBack agora está desligado e o app reabriu em Forja.
