# Git e qualidade

## Conventional Commits

Tipos:

- `feat`: nova capacidade observável;
- `fix`: correção;
- `refactor`: mudança interna sem alterar comportamento;
- `test`: testes;
- `docs`: documentação;
- `build`: build/dependências;
- `ci`: automação;
- `perf`: performance;
- `style`: formatação sem lógica;
- `chore`: manutenção;
- `revert`: reversão explícita.

Exemplos:

```text
feat(collection): add paginated character search
fix(forge): prevent merging completed infinity stones
refactor(ai): isolate Gemini provider adapter
test(campaign): cover Magneto victory rewards
docs(sdd): record Comic Vine proxy decision
```

## Branches

- `feat/<spec-id>-<slug>`
- `fix/<spec-id>-<slug>`
- `docs/<slug>`

Não criar branch sem necessidade. Trabalho individual curto pode ocorrer na branch atual quando autorizado.

## Checklist pré-commit

1. `git status --short`
2. `git diff --check`
3. `git diff`
4. testes/lint/build relevantes
5. `git log --oneline -10`
6. verificar segredos e arquivos gerados
7. atualizar spec e memória

## Qualidade

- unitários para domínio e mappers;
- integração para Room, API facade e IA estruturada;
- UI/instrumentados para fluxo crítico;
- contract tests entre Android e FastAPI;
- screenshot tests nas telas visuais estáveis;
- teste manual em dispositivo/emulador após mudanças de interação.

Um commit não deve misturar documentação de decisão, refatoração ampla e feature sem necessidade.
