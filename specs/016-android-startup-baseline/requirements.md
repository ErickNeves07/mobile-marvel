# 016 — Baseline de inicialização Android: Requirements

Status: **concluída como medição observacional derivada de RNF-005; não define meta de performance.**

## Objetivo

Registrar tempos de cold start do build local no AVD de referência para permitir comparação após mudanças de startup.

## Escopo

- Repetir cinco inicializações após `am force-stop` no AVD `Medium_Phone_API_36.1`.
- Registrar `TotalTime` e `WaitTime` reportados, resolução/densidade, escala de fonte e versão do build; `ThisTime` será indicado como não reportado quando ausente na saída do AVD.
- Calcular min/mediana/máximo e deixar explícito que estes números são baseline de emulador.
- Não alterar o app ou definir SLO/limiar sem requisito explícito.

## Fora de escopo

- Benchmark em dispositivo físico, tracing/profiling com ferramenta nova, rede ou carga de conteúdo externo.
- Aceitar/reprovar desempenho com base em limite não aprovado.

## Critérios de aceite

- [x] AC-01 — cinco amostras frias do mesmo APK e configuração AVD são documentadas.
- [x] AC-02 — min/mediana/máximo calculados e limitações do emulador explicitadas.
- [x] AC-03 — sem mudança de código/dependência nem dado externo.