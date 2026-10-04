# 016 — Baseline de inicialização Android: Design

Usar somente `adb` existente e AVD local. Para cada amostra: `am force-stop com.erickbarbosa.rupturainfinita`, depois `am start -W -n com.erickbarbosa.rupturainfinita/.MainActivity`. Guardar os campos do ActivityTaskManager, esperar estabilizar e repetir cinco vezes. Nesta imagem, `am start -W` reporta `TotalTime` e `WaitTime`, mas omite `ThisTime`; campo ausente deve constar como N/R, nunca como zero.

`TotalTime` e `WaitTime` são tempos reportados pelo Android para o lançamento/espera da Activity e não correspondem a benchmark de hardware ou medida isolada de first frame. Relatar os valores brutos e distribuição descritiva; não criar meta.

Não mudar configurações do usuário; registrar o estado do AVD e confirmá-lo ao final. O app deve retornar à Forja selecionada.