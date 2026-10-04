# 008 — Prévia local de campanhas: Evidence

Status: **implementado e validado em 2026-09-27.**

## Verificações

| Data | Comando/verificação | Resultado |
|---|---|---|
| 2026-09-27 | `scripts/validate-local.ps1 -Target all` após a implementação | Android `BUILD SUCCESSFUL` (`testDebugUnitTest`, lint, assemble); backend `pip check` limpo e 7 testes aprovados.
| 2026-09-27 | APK recém-construído instalado no AVD; UIAutomator após tocar Campanhas | Cabeçalho, X-Men, chefe Magneto, Quarteto Fantástico e dois estados de desenvolvimento presentes. Quarteto não recebe chefe.
| 2026-09-27 | Fonte do sistema 1.3 e perfil compacto 390×844 dp; rolagem até o segundo cartão | Ambos os status permanecem na hierarquia e visualmente terminam acima da barra de navegação; fonte e resolução restauradas.
| 2026-09-27 | Forçar parada e reabrir o APK final | UIAutomator confirmou `Forja, selecionado` como destino inicial.

Screenshot final (artefato em pasta ignorada pelo Git): `android-app/.validation-output/campaigns-preview.png`. Capturas de legibilidade/rolagem: `campaigns-font130-scrolled.png`, `campaigns-compact-scrolled.png` no mesmo diretório.

## Limites

Não há seleção ou execução de campanha, missão, objetivo, recompensa, progressão nem dado de rede. Placeholders não afirmam disponibilidade jogável.
