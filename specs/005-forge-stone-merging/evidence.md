# 005 — Forja — Evidence

Status: **implementação local validada; inspeção de fluxo no AVD concluída.**

## Decisions

- 2026-09-28 — Erick approved 3:1 at every merge transition, delegated deterministic daily/campaign shard rewards and approved cap 999. Details are in requirements/design and DEC-042/043.
- 2026-09-28 — Existing local Gradle cache contains only Room 2.2.5. To avoid introducing an old dependency or external download, local persistence uses platform SQLite transactions; this remains behind a repository boundary.
- 2026-09-28 — No reward is triggered by existing campaign preview cards. Integration waits for actual challenge/campaign completion flows.

## Verification

| Date | Command/case | Result |
|---|---|---|
| 2026-09-28 | `scripts/validate-local.ps1 -Target all` | Android unit tests, `assembleDebugAndroidTest`, lint e debug build passaram; backend `pip check` limpo e 18 pytest passaram. |
| 2026-09-28 | `scripts/run-android-instrumentation.ps1` | AndroidJUnitRunner direto no AVD: 7 testes passaram, incluindo rollback no limite, deduplicação, migração, inventário vazio e persistência. |
| 2026-09-28 | AVD manual | Confirmado/cancelado merge; cancelamento preservou entrada; merge persistiu após force-stop/reabertura. |

## Screenshots

- `forge-confirmation-avd.png` — confirmação irreversível antes da fusão.
- `forge-persisted-avd.png` — inventário após operação persistida no AVD.

## Limitations

Nenhuma chamada Groq, Comic Vine ou serviço externo faz parte da validação local. Teste em aparelho físico ainda não ocorreu. A suíte Android instrumentada rodou pelo AndroidJUnitRunner direto porque o adaptador UTP do Gradle não conseguiu anexar ao emulador neste ambiente.
