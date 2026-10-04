# 004 — Local Validation Runner: Design

Status: **design aprovado pela autorização DEC-021; sem serviço externo**.

## Contrato CLI

`powershell -ExecutionPolicy Bypass -File scripts/validate-local.ps1 [-Target all|android|backend]`

O alvo padrão é `all`. O script não muda política permanente do PowerShell. Em Android define somente no processo corrente `JAVA_HOME`, `ANDROID_HOME`, `GRADLE_USER_HOME` e `RI_VALIDATION_DIR`; restaura os valores originais no bloco `finally`.

## Fluxo

1. Resolver raiz por `$PSScriptRoot` e validar alvo.
2. Para Android, confirmar Wrapper, JBR e SDK; executar Wrapper offline com init script de saída isolada.
3. Para backend, confirmar `backend/.venv/Scripts/python.exe`; executar `python -m pip check` e `python -m pytest` no diretório backend.
4. Interromper no primeiro erro preservando código/diagnóstico; não instalar, apagar ou iniciar processo residente.

## Segurança e privacidade

Sem leitura/impressão de variáveis sensíveis; sem rede: Gradle usa `--offline`, pip apenas `check`, pytest apenas lê dependências já instaladas. Saídas de build são locais e ignoradas.

## Testes

- `-Target` deve restringir execução ao subconjunto selecionado.
- `-?`/Get-Help deve funcionar sem toolchain.
- Verificação estática das rotas e checagem de parser PowerShell.
- Execução completa quando o sandbox disponibilizar os diretórios externos usados pelo toolchain.

## Fallback

Se qualquer pré-requisito não for acessível, script termina não zero com indicação do caminho/comando negado; usar comandos documentados individualmente em um ambiente com acesso local apropriado. Nunca solicitar/escalar privilégios automaticamente.
