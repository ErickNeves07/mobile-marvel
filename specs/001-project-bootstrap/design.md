# 001-project-bootstrap — Design

Status: **aprovado em 2026-09-27; implementação autorizada**.

## Contexto

O repositório contém apenas documentação. O bootstrap deve produzir duas fundações independentes e pequenas, em `android-app/` e `backend/`, mantendo a separação de confiança definida em `docs/03-ARCHITECTURE.md`. Esta spec prova o toolchain; não inicia features do produto.

O workspace efetivo desta sessão é Windows e contém espaços, acentos e sincronização pelo OneDrive. O caminho macOS presente no prompt original não existe neste ambiente e não será usado.

## Decisões

- Android nativo com um módulo `app`, código de produção em Java e layouts XML.
- Activity mínima com AndroidX; Compose/Kotlin não entra no bootstrap.
- Gradle Wrapper será a única interface de build documentada; Gradle global não será requisito.
- Matriz Android fixada na tabela abaixo a partir do ambiente real e de documentação oficial.
- Backend com layout pequeno de pacote, FastAPI e Pydantic; LangChain/Gemini ficam fora desta spec.
- O backend expõe apenas `GET /health` para validar inicialização e teste.
- Android e backend não serão conectados em rede nesta spec; essa integração exige contrato e política de rede/emulador próprios.
- Nenhum segredo real será necessário para executar os esqueletos.

## Alternativas e trade-offs

### UI Android

- **Proposta: Views/XML + Java.** Alinha-se a Activities/Fragments, ViewModel/LiveData e ao requisito Java.
- Compose foi descartado nesta etapa por ser centrado em Kotlin e ampliar tooling/escopo.

### JDK

- O Android Studio inclui JBR 21, enquanto o `java` do `PATH` é 26 e `JAVA_HOME` aponta para JRE 8.
- **Proposta:** usar explicitamente o JDK compatível configurado no projeto/Android Studio, preferindo o JBR embutido se a matriz oficial AGP/Gradle confirmar compatibilidade. Não alterar `JAVA_HOME` global nesta spec sem autorização.

### Dependências Python

- `venv` + `pip` reduz ferramentas adicionais, mas um lock transitivo reproduzível pode exigir `pip-tools`.
- `uv` ou Poetry oferecem resolução/lock mais forte, mas introduzem instalação e uma nova ferramenta.
- **Escolha aprovada:** `.venv` isolado + pip + `pip-tools` `7.6.1`; entradas diretas exatamente fixadas em `requirements.in`/`requirements-dev.in` e locks transitivos com hashes em `requirements.txt`/`requirements-dev.txt`.

## Matriz exata aprovada/proposta

| Componente | Versão/configuração | Estado |
|---|---|---|
| Android Studio | 2025.2.2, build `252.28238.7.2523.14688667` | já instalado |
| JDK do build | JBR OpenJDK `21.0.8` do Android Studio | já instalado; não alterar `JAVA_HOME` global |
| Java source/target | 17 | proposta para bytecode/código do app |
| Android Gradle Plugin | `8.13.2` | compatível com Android Studio 2025.2.2 e API 36.1 |
| Gradle Wrapper | `8.13` | exigido pela linha AGP 8.13 |
| SDK | `minSdk 26`, `compileSdk 36`, `targetSdk 36` | confirmado por Erick |
| SDK Build Tools | `36.0.0` | já instalado |
| Namespace/application ID | `com.erickbarbosa.rupturainfinita` | confirmado por Erick |
| AndroidX AppCompat | `1.7.1` | release estável |
| AndroidX Core | `1.17.0` | última linha estável anterior à exigência de `compileSdk` 36.1/37; compatível com a matriz API 36 |
| Teste unitário Android | JUnit `4.13.2` | release estável |
| Python base | `3.13.15` | instalação side-by-side autorizada; Python 3.11.2 atual não será usado |
| pip-tools | `7.6.1` | geração de lock com hashes |
| FastAPI | `0.141.1` | release de 2026-07-29 |
| Uvicorn | `0.53.0` | release de 2026-09-14; `0.54.0` rejeitado por ter menos de 7 dias |
| Pydantic | `2.13.5` | release de 2026-08-28 |
| pytest | `9.1.1` | release de 2026-06-19 |
| HTTPX2 | `2.13.0` | backend oficial atual do TestClient; 2.13.1 rejeitado por ter menos de 7 dias |

Fontes oficiais verificadas em 2026-09-27:

- Android Studio/AGP: <https://developer.android.com/build/releases/about-agp>
- AGP 8.13: <https://developer.android.com/build/releases/agp-8-13-0-release-notes>
- Android 16 SDK: <https://developer.android.com/about/versions/16/setup-sdk>
- Gradle/JDK: <https://docs.gradle.org/current/userguide/compatibility.html>
- AppCompat: <https://developer.android.com/jetpack/androidx/releases/appcompat>
- AndroidX Core: <https://developer.android.com/jetpack/androidx/releases/core>
- Python 3.13.15: <https://www.python.org/downloads/release/python-31315/>
- Pacotes Python: páginas oficiais dos projetos no PyPI.

## Arquitetura e componentes

```text
android-app/
  settings.gradle(.kts)          # DSL definida após compatibilidade; código do app permanece Java
  build.gradle(.kts)
  gradle/wrapper/
  gradlew / gradlew.bat
  app/
    build.gradle(.kts)
    src/main/AndroidManifest.xml
    src/main/java/<namespace>/MainActivity.java
    src/main/res/layout/activity_main.xml
    src/test/java/<namespace>/...

backend/
  README.md
  pyproject.toml ou requirements*.txt  # decisão Q-010
  app/
    __init__.py
    main.py
  tests/
    test_health.py
```

Arquivos exatos gerados pelo Android Studio/Gradle podem variar com as versões aprovadas. A implementação deve manter apenas o mínimo necessário e documentar qualquer diferença.

## Contratos e modelos

Contrato local proposto para provar o backend:

```http
GET /health
```

```json
{
  "status": "ok",
  "service": "ruptura-infinita-backend"
}
```

- Resposta: HTTP 200, JSON validado por modelo Pydantic.
- Sem data/hora variável, versão secreta, hostname ou detalhes internos.
- Nenhum endpoint Comic Vine/IA nesta spec.

## Estados de UI

A Activity mínima possui somente estado estático de shell para provar instalação e renderização. Loading, conteúdo, vazio e erro pertencem às specs das features que carregarem dados. Não usar assets finais ou imagens Marvel no bootstrap.

### Portrait e tamanhos de janela

- Referência visual: 390 × 844 dp.
- Para smartphones com `smallestWidth < 600dp`, o Manifest pode solicitar portrait.
- Ao mirar API 36, Android 16 ignora restrições de orientação em displays `sw600dp` ou maiores. Esses displays, tablets e foldables abertos ficam fora do primeiro ciclo; não será usado opt-out temporário.
- Layouts devem evitar dimensões rígidas e funcionar em telefones compactos/grandes dentro do escopo aprovado.

## Persistência/migração

Nenhuma persistência. Room, cache e migrações ficam fora do escopo.

## Segurança e privacidade

- `.env` e arquivos locais de IDE/SDK permanecem ignorados.
- Caso exista `.env.example`, ele contém apenas placeholders inequívocos e não é necessário ao `/health`.
- BuildConfig, resources, Manifest e testes não recebem chaves.
- Logs de bootstrap não imprimem ambiente completo nem valores de variáveis sensíveis.
- O bind padrão do servidor local deve ser loopback; exposição em `0.0.0.0` exige motivo e instrução explícita.
- Não chamar Comic Vine, Gemini ou serviços pagos.

## Acessibilidade e performance

- A tela mínima deve ter texto legível e não depender somente de cor.
- Não há animações, imagens remotas ou trabalho na main thread.
- O bootstrap deve manter inicialização simples e sem dependências desnecessárias.

## Telemetria

Nenhuma telemetria no bootstrap. Evidência local registra somente versões, comandos e resultados não sensíveis.

## Estratégia de testes

- Android: build debug, lint aplicável e teste unitário mínimo executados pelo Wrapper.
- Android manual: iniciar em dispositivo/emulador aprovado e confirmar Activity sem crash.
- Backend: teste de `GET /health` com cliente de teste compatível, validação de schema e status 200.
- Repositório: busca por padrões de segredo e versões dinâmicas nos novos arquivos.
- Reprodutibilidade: repetir os comandos documentados após a última alteração.

## Rollback/fallback

- Como não há dados nem migração, rollback consiste em reverter apenas os diretórios do bootstrap e documentação associada por operação Git não destrutiva/autorizada.
- Se uma versão aprovada falhar, não avançar para features: registrar evidência, revisar a matriz oficial e propor nova combinação exata.
- Se Android SDK ou Git não puderem ser preparados, o backend pode ser validado isoladamente, mas a spec permanece incompleta.
