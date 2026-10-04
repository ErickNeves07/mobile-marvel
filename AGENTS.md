# AGENTS.md — Marvel: Ruptura Infinita

Este arquivo é autoritativo para Codex, subagentes e novas sessões.

## Missão

Construir um app Android em Java, visualmente marcante e estável, que consuma dados reais da Comic Vine e combine coleção, merge das Joias do Infinito, progressão de variantes, batalhas, desafios diários e IA narrativa com Deadpool.

## Regra zero: dúvidas bloqueiam

Se houver ambiguidade sobre requisito, regra de negócio, segurança, contrato, UX, licença, escopo ou operação externa:

1. não implemente a parte duvidosa;
2. registre em `memory/OPEN_QUESTIONS.md`;
3. explique impacto e opções;
4. pergunte ao Erick;
5. retome só depois da resposta.

Não invente decisões críticas. Premissas triviais e reversíveis devem ser declaradas no handoff.

## Ordem de leitura obrigatória

1. `README.md`
2. `memory/CURRENT_STATE.md`
3. `docs/01-PRODUCT_VISION.md`
4. `docs/02-REQUIREMENTS.md`
5. `docs/03-ARCHITECTURE.md`
6. `docs/04-SDD_WORKFLOW.md`
7. spec ativa em `specs/`
8. demais documentos citados pela spec

## Fluxo SDD obrigatório

Nenhum código de feature sem spec aprovada. Para cada feature, criar `specs/<id>/` com:

- `requirements.md`: objetivo, escopo, fora de escopo, aceite e casos limite;
- `design.md`: decisões, contratos, dados, UX, segurança e testes;
- `tasks.md`: tarefas pequenas, dependências e definição de pronto;
- `evidence.md`: comandos, testes, screenshots e limitações.

Fluxo: requisitos → dúvidas resolvidas → design → tarefas → implementação → evidências → revisão → conclusão.

## Engenharia

- Android Studio, Java e Gradle.
- Arquitetura definida em `docs/03-ARCHITECTURE.md`.
- Leia código e contratos antes de editar.
- Diff mínimo e coerente; não refatore fora do escopo.
- Versões de dependências sempre exatas, nunca `+`, `latest`, `^`, `~` ou snapshots.
- Não adote release com menos de 7 dias sem justificativa de segurança.
- Segredos nunca entram no APK, Git, logs, screenshots ou prompts.
- Dados da Comic Vine são editoriais; atributos e balanceamento são dados do jogo. Nunca misture as fontes.
- IA não define fatos canônicos, recompensas, dificuldade ou regras.
- Toda mudança de comportamento exige testes e evidência após a última edição.

## Git

- Branch principal: `main`.
- Conventional Commits em inglês: `feat`, `fix`, `refactor`, `test`, `docs`, `build`, `ci`, `chore`, `perf`, `style`, `revert`.
- Formato: `type(scope): imperative summary`.
- Commits pequenos, coesos e sem segredos.
- Antes de commit: `git status`, `git diff`, `git log --oneline -10` e testes relevantes.
- Não commit, push, PR, merge, tag ou remote sem pedido explícito do usuário.
- Nunca force-push, `reset --hard` ou `clean -fd`.

## Subagentes e sessões paralelas

Podem ser usados quando o trabalho for independente. Cada delegação deve informar:

- objetivo e spec;
- diretório/worktree;
- arquivos permitidos;
- arquivos proibidos;
- critérios de aceite;
- verificações obrigatórias;
- formato de retorno.

Não permita dois agentes editando os mesmos arquivos. Prefira worktrees/branches separadas. O agente principal integra, revisa e testa; resultado de subagente não é automaticamente confiável.

Personas estão em `docs/06-AGENT_PERSONAS.md`.

## Memória e encerramento de sessão

No começo, leia `memory/CURRENT_STATE.md`, `memory/DECISIONS.md`, `memory/OPEN_QUESTIONS.md` e a spec ativa.

Ao terminar uma sessão:

1. atualize `memory/CURRENT_STATE.md`;
2. acrescente uma entrada em `memory/SESSION_LOG.md`;
3. registre decisões em `memory/DECISIONS.md`;
4. registre bloqueios em `memory/OPEN_QUESTIONS.md`;
5. atualize tarefas/evidências da spec;
6. deixe o próximo passo executável.

Não use memória como substituta da spec. A spec registra o que deve existir; a memória registra onde o trabalho parou.

## Ações externas

Peça autorização antes de autenticar, criar conta, enviar dados, publicar, fazer deploy, push ou chamar serviço pago. Operações locais e reversíveis seguem quando o pedido estiver claro.
