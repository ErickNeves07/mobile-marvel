# 004 — Local Validation Runner: Requirements

Status: **aprovada para execução local pela autorização temporária DEC-021**.

## Objetivo

Oferecer um comando PowerShell repetível para verificar os projetos Android e backend usando exclusivamente o Gradle Wrapper e o ambiente virtual Python já preparado.

## Requisitos relacionados

- Roadmap MVP 0: CI local, lint e testes básicos.
- BOOT-R-005, BOOT-R-007, BOOT-R-009; RNF-006, RNF-007.

## Escopo

- Script local com alvo `all`, `android` ou `backend`.
- Android executa `testDebugUnitTest`, `lintDebug` e `assembleDebug` em modo offline, com saída em pasta temporária isolada.
- Backend executa `pip check` e pytest no `backend/.venv` já instalado.
- Preservar/restaurar variáveis de ambiente alteradas pelo script, sempre que a execução terminar.
- Falhar com mensagem específica se ferramenta/venv necessária estiver ausente ou inacessível; nunca instalar.

## Fora de escopo

- Instalação/resolução/atualização de dependências.
- Rede, autenticação, upload de relatórios, Git remoto ou CI hospedado.
- Inicialização de serviço persistente ou exclusão de artefatos.

## Critérios de aceite

- [x] AC-01 — `all` executa ambos os projetos em sequência e propaga qualquer código de falha.
- [x] AC-02 — alvos individuais executam somente o projeto pedido.
- [x] AC-03 — o script usa Gradle Wrapper offline e Python da venv local; não invoca package manager para instalar.
- [x] AC-04 — variáveis que o script ajusta retornam ao valor anterior ao terminar, inclusive em falha. Alvo Android concluído e restauração comparada no PowerShell chamador.
- [x] AC-05 — uso/help e falha por pré-requisito são claros.
- [x] AC-06 — validações estáticas e testes locais passam após a última alteração.

## Decisões

- PowerShell 5.1+ por ser o shell suportado/documentado do workspace Windows.
- Saídas Gradle vão para `%TEMP%\Marvel-Ruptura-Infinita-validation` e não são limpas pelo script.
- Backend usa `backend/.venv/Scripts/python.exe`; não recorrer a Python global nem à venv de reprodução.

## Dependências e dúvidas

- Depende de acesso local permitido ao wrapper/cache Gradle e à venv Python. Se o sandbox negar essas leituras, registrar falha e não elevar acesso automaticamente.
- Nenhuma dúvida de produto.
