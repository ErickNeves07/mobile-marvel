# Workflow SDD

## Unidade de trabalho

Cada feature usa `specs/<NNN-slug>/` com quatro arquivos. Copie `specs/_template/`.

## 1. Requirements

Defina:

- problema e resultado;
- requisitos estáveis com IDs;
- escopo e fora de escopo;
- critérios observáveis;
- fluxos principal, erro e bordas;
- dúvidas e dependências externas.

Se existir dúvida crítica, pare aqui.

## 2. Design

Defina:

- decisões e alternativas;
- contratos e modelos;
- estados de UI;
- persistência e migração;
- segurança e privacidade;
- telemetria;
- testes e plano de rollback.

## 3. Tasks

Tarefas pequenas, demonstráveis, ordenadas por dependência. Cada tarefa inclui requisito coberto, arquivos esperados e definição de pronto.

## 4. Implementação

- Marque uma tarefa em progresso.
- Leia arquivos e regras locais.
- Faça o menor diff suficiente.
- Teste após a última alteração.
- Registre evidências.
- Não misture tarefas independentes no mesmo commit.

## 5. Review

Revise requisito por requisito e anexe evidências. `feito` sem teste ou screenshot não é evidência.

## Gates

- **G0 Discovery**: dúvidas críticas resolvidas.
- **G1 Requirements**: aceite testável.
- **G2 Design**: contratos, UX, segurança e testes definidos.
- **G3 Build**: tarefas concluídas.
- **G4 Verification**: testes e critérios aprovados.
- **G5 Release**: autorização humana para ação externa.

## Exceção temporária autorizada pelo usuário

Em 2026-09-27, Erick autorizou explicitamente, pelas próximas horas desta sessão de desenvolvimento, iniciar novas specs e implementar trabalho local reversível sem aguardar aprovação individual de G0/G1/G2. Registrar esta autorização na memória e na spec afetada. A exceção remove somente a espera pela aprovação formal do usuário; ela **não** autoriza inventar decisões críticas. Quando um detalhe crítico não puder ser derivado de requisitos existentes, avançar em tarefas independentes e manter somente a parte dependente documentada como bloqueada. A exceção não dispensa criar/atualizar requirements, design, tasks e evidence, revisar e testar após cada incremento, nem autoriza ações externas, instalação global, autenticação, publicação, deploy, push, PR, merge ou commit sem a autorização específica exigida pelas regras do projeto.

A exceção é limitada à sessão atual e às próximas horas explicitamente mencionadas pelo usuário; não alterar silenciosamente a regra padrão para sessões futuras. G5 permanece obrigatório para release/ações externas.

## Definition of Done

- aceite coberto;
- testes relevantes passam;
- lint/build passam;
- estados de erro existem;
- acessibilidade revisada;
- sem segredo ou dado sensível;
- docs/spec/memória atualizadas;
- limitações declaradas.
