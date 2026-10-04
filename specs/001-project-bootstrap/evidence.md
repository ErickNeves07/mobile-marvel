# 001-project-bootstrap — Evidence

Status: **AC-01 a AC-10 cobertos; importação visual do Android Studio confirmada por Erick; shell posterior validado na spec 002**.

## Verificação automatizada

### Discovery inicial (antes da geração dos esqueletos)

| Data | Comando/verificação | Resultado |
|---|---|---|
| 2026-09-27 | leitura da documentação obrigatória e de `specs/_template/` | Concluída; repositório confirmado em fase documental, sem app/backend. |
| 2026-09-27 | `codex --version` e `codex --help` | Codex CLI `0.157.1`; opção `--model` e configuração por `-c` suportadas. |
| 2026-09-27 | catálogo local `~/.codex/models_cache.json` | Cache da conta obtido em `2026-09-27T22:00:21.655998200Z`, cliente `0.158.0`; 7 modelos visíveis listados abaixo. |
| 2026-09-27 | `codex doctor --summary --no-color --ascii` | Runtime/instalação do Codex OK; alertas de Git ausente, HOME/CODEX_HOME não resolvido no sandbox, configuração não carregada e reachability externa falha. Nenhuma correção executada. |
| 2026-09-27 | `git --version`, `git status --short --branch`, `git log --oneline -10` | Não executados: `git` não está no `PATH`. Diretório `.git` existe. |
| 2026-09-27 | `java -version` e `JAVA_HOME` | `java` no `PATH`: Oracle Java `26.0.1`; `JAVA_HOME`: `C:\Program Files\Java\jre1.8.0_451`. Configuração inconsistente. |
| 2026-09-27 | JBR do Android Studio `java.exe -version` | OpenJDK `21.0.8` embutido no Android Studio. |
| 2026-09-27 | `gradle --version` | Gradle global não está no `PATH`; futuro projeto deve usar Wrapper. |
| 2026-09-27 | `python --version`, `python -m pip --version` | Python `3.11.2`; pip `22.3.1`. |
| 2026-09-27 | `pip show fastapi/uvicorn/pydantic/langchain` | Pacotes não instalados no Python ativo; nenhuma instalação realizada. |
| 2026-09-27 | Android Studio `product-info.json` | Android Studio `AI-252.28238.7.2523.14688667`, build `252.28238.7.2523.14688667`. |
| 2026-09-27 | `ANDROID_HOME`, `ANDROID_SDK_ROOT`, `adb`, `sdkmanager`, `avdmanager` | Variáveis vazias e comandos ausentes do `PATH`. O caminho padrão do SDK ficou inacessível ao sandbox; ausência do SDK **não** foi concluída. |
| 2026-09-27 | inspeção elevada somente leitura de `C:\Users\erickbarbosa-ieg\AppData\Local\Android\Sdk` | SDK existe: plataformas 35/36; Build Tools 35.0.0/36.0.0/36.1.0; Platform Tools/ADB 36.0.2; emulator instalado; command-line tools ausentes. |
| 2026-09-27 | inspeção somente leitura de `~/.android/avd` | AVD `Medium_Phone_API_36.1` já existe e aponta para `android-36.1`. |
| 2026-09-27 | inspeção de Git por caminho absoluto | Git `2.49.0.windows.1` existe em `%LOCALAPPDATA%\Programs\Git\cmd\git.exe`; branch `main`, ainda sem commits; todo o conteúdo aparece como não rastreado. |
| 2026-09-27 | documentação oficial Android/Gradle | Android Studio 2025.2.2 suporta AGP até 8.13; AGP 8.13.2 suporta API 36.1 e exige Gradle 8.13/JDK >=17; Gradle 8.13 executa em Java 21. |
| 2026-09-27 | documentação oficial Android 16 | `compileSdk`/`targetSdk` 36 confirmados; portrait é ignorado em displays `sw600dp` ou maiores, criando Q-013. |
| 2026-09-27 | Python.org, PyPI e documentação pip | Matriz Python proposta com versões exatas e releases >=7 dias; Uvicorn 0.54.0 rejeitado por ter apenas 2 dias. |
| 2026-09-27 | inspeção dos arquivos locais Android/backend | Esqueletos Java/XML, Gradle Wrapper, FastAPI, teste `/health`, requirements e locks estão presentes; não eram refletidos pela memória anterior. |
### Revalidação final

| 2026-09-27 | `android-app/gradlew.bat --offline testDebugUnitTest lintDebug assembleDebug` com JBR 21.0.8 | Falhou antes do build: `ClassNotFoundException: org.gradle.wrapper.GradleWrapperMain`. Wrapper JAR está presente (43.705 bytes); nenhum download ou reparo executado. |
| 2026-09-27 | `backend/.venv/Scripts/python.exe -m pytest` | Não iniciou: `pyvenv.cfg` aponta para Python 3.13.15 em caminho cujo acesso foi negado pelo sandbox. Nenhuma instalação ou recriação executada. |
| 2026-09-27 | tentativa de Git por caminho absoluto | Acesso ao executável negado pelo sandbox; status/diff não puderam ser coletados. `git` não está no PATH. |
| 2026-09-27 | Wrapper JAR inválido e recuperação | O JAR original continha classes Gradle CLI, sem `GradleWrapperMain`. O JAR final foi composto com as classes CLI preservadas e os JARs wrapper-main/shared da distribuição 8.13 já presente em cache. `GradleWrapperMain --offline --version` informou Gradle 8.13/JBR 21.0.8. O JAR original e intermediário foram guardados em `.validation-output/wrapper-recovery/`. |
| 2026-09-27 | correção de `android-app/gradlew.bat` | Atribuições de `DIRNAME`, `APP_HOME` e `CLASSPATH` alteradas para `set "VAR=value"`; o `&` em `Instituto J&F` era interpretado pelo CMD como separador quando as atribuições não eram protegidas. |
| 2026-09-27 | `gradlew.bat --offline -I gradle/isolated-builds.init.gradle testDebugUnitTest lintDebug assembleDebug` | `BUILD SUCCESSFUL`; 49 tarefas executadas na primeira execução, 48 up-to-date na repetição final. JUnit XML registra 1 teste, 0 falhas. Saída configurada por `RI_VALIDATION_DIR` em caminho temporário ASCII para não substituir saídas existentes protegidas pelo OneDrive. |
| 2026-09-27 | repetição final do comando exatamente como documentado em `android-app/README.md` | `BUILD SUCCESSFUL` em 2s, 49 tarefas, 48 up-to-date; `testDebugUnitTest`, `lintDebug` e `assembleDebug` aprovados. |
| 2026-09-27 | instalação Python limpa pelo lock | Criada `backend/.venv-repro` e executado `.venv-repro/Scripts/python.exe -m pip install --require-hashes --index-url https://pypi.org/simple -r requirements-dev.txt`; todos os pacotes foram instalados conforme hashes do lock. `pip check`: sem requisitos quebrados; pytest: `1 passed in 0.86s`. Nenhum pacote foi instalado globalmente e `.venv` original foi preservada. |
| 2026-09-27 | AVD e APK debug | AVD `Medium_Phone_API_36.1` passou a `device`; `adb install -r` retornou `Success`; `am start` abriu `MainActivity`; `pidof` confirmou processo e `dumpsys` mostrou a Activity no topo. |
| 2026-09-27 | APK: varredura local | 387 entradas ZIP inspecionadas para padrões comuns de credenciais (tokens conhecidos e chave privada); nenhum resultado. |
| 2026-09-27 | `backend/.venv/Scripts/python.exe -m pytest` no diretório `backend/` | `1 passed in 0.74s`. A tentativa anterior na raiz falhou por diretório de trabalho incorreto; a execução documentada a partir de `backend/` passou. |
| 2026-09-27 | `pip check` e `pip install --dry-run --no-index --require-hashes -r requirements-dev.txt` | Sem dependências quebradas; todas as dependências fixadas já estavam satisfeitas na `.venv`. Dry-run não instalou nem baixou pacotes. |
| 2026-09-27 | startup real FastAPI | Uvicorn iniciou em `127.0.0.1:18765`; `GET /health` retornou `200` e `{"status":"ok","service":"ruptura-infinita-backend"}`; o processo iniciado pelo teste foi encerrado em seguida. |
| 2026-09-27 | pins e segredos em código/configuração | Busca em fontes por tokens/chaves comuns e em Gradle/requirements por `SNAPSHOT`, `latest` e `+` não encontrou ocorrências. `.venv`, caches e saídas de validação estão cobertos por ignores. |
| 2026-09-27 | status do repositório | Git 2.49 acessível por caminho absoluto; branch `main`, sem commits anteriores. Nenhum commit foi criado. |

### Modelos Codex visíveis no catálogo local

| ID | Display | Thinking suportado |
|---|---|---|
| `gpt-6-astra` | GPT-6-Astra | low, medium, high, xhigh, max, ultra |
| `gpt-6-sol` | GPT-6-Sol | low, medium, high, xhigh, max, ultra |
| `gpt-6-luna` | GPT-6-Luna | low, medium, high, xhigh, max |
| `gpt-5.6-sol` | GPT-5.6-Sol | low, medium, high, xhigh, max, ultra |
| `gpt-5.6-terra` | GPT-5.6-Terra | low, medium, high, xhigh, max, ultra |
| `gpt-5.6-luna` | GPT-5.6-Luna | low, medium, high, xhigh, max |
| `gpt-5.5` | GPT-5.5 | low, medium, high, xhigh |

O cache é específico da instalação/conta, mas a conectividade externa falhou no diagnóstico; portanto, ele não foi atualizado nesta sessão. Não foi feita autenticação nem chamada de modelo.

## Verificação manual

- Confirmado que o workspace ativo é `C:\Users\erickbarbosa-ieg\OneDrive - Instituto J&F\Área de Trabalho\Marvel-Ruptura-Infinita`.
- Android Studio/JBR, SDK/AVD, distribuição Gradle e `.venv` existentes foram usados. Dependências Python foram instaladas somente na nova `backend/.venv-repro`, a partir do lock com hashes.
- O app foi observado ativo no AVD com `MainActivity` no topo.
- Erick confirmou em 2026-09-27 que o projeto importa visualmente no Android Studio. O agente não inspecionou a captura diretamente nesta sessão.
- Nenhuma autenticação, chamada Comic Vine/Gemini, serviço pago, commit, push ou limpeza de diretório foi executada. O único acesso externo foi PyPI para instalar dependências fixadas por hash na venv isolada, conforme autorização de Erick.

## Screenshots/artefatos

- Nenhum screenshot: inspeção de UI Windows não estava disponível pelo plugin nesta sessão.
- Artefatos verificados: APK debug temporário em `%TEMP%\Marvel-Ruptura-Infinita-validation\app\outputs\apk\debug\app-debug.apk`; relatório lint em `%TEMP%\Marvel-Ruptura-Infinita-validation\app\reports\lint-results-debug.html`; relatórios de testes Gradle na mesma pasta. Evidência runtime via ADB descrita acima.

## Revisão por requisito

| Requisito/AC | Estado | Evidência |
|---|---|---|
| BOOT-R-001 / AC-01 | Atendido para discovery inicial | Inventário acima. |
| BOOT-R-002 / AC-02 | Atendido | Catálogo acima e DEC-009. |
| BOOT-R-003 / AC-04 | Atendido por confirmação do usuário | Wrapper, Java, teste, lint e APK passaram; Erick confirmou a importação visual no Android Studio em 2026-09-27. |
| BOOT-R-004 / AC-06 | Atendido | Instalação limpa em venv isolada com hashes, `pip check`, pytest e startup local sem credenciais reais. |
| BOOT-R-004 / AC-07 | Atendido | TestClient e startup local retornaram HTTP 200 com JSON estável. |
| BOOT-R-005/006 / AC-08 | Atendido nos artefatos verificados | Pins, guardrails, APK e arquivos locais foram inspecionados; buscas não encontraram padrões conhecidos de segredo/versão dinâmica. |
| BOOT-R-007 / AC-05 | Atendido | APK instalado no AVD; Activity e processo ativos, sem crash observado. |
| BOOT-R-007 / AC-09 | Atendido | Comandos registrados nos READMEs passaram no workspace Windows. |
| BOOT-R-010 / AC-10 | Atendido | Revisão executada e esta evidence atualizada após validação; limitação do import IDE registrada. |

## Limitações e riscos residuais

- O agente não observou diretamente a IDE nesta sessão; confirmação de AC-04 veio explicitamente de Erick.
- Git não está no `PATH`, mas funciona por caminho absoluto; nenhum commit foi feito.
- As saídas default de build no OneDrive podem falhar quando o sincronizador mantém artefatos ocupados. O init script documentado envia novas saídas para `RI_VALIDATION_DIR`.
- O Android Gradle Plugin reporta `android.overridePathCheck=true` como experimental e o Gradle reporta recursos deprecated para Gradle 9.0; não impedem a matriz atual.
- A instalação limpa foi feita em `.venv-repro`, separada da `.venv` original e ignorada pelo Git.
