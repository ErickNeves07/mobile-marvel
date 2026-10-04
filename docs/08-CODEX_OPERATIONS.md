# Operação do Codex

## Situação local

Na criação deste repositório, `codex` não estava disponível no PATH. Não foi instalado automaticamente.

## Política de modelos

Como os identificadores variam por conta/versão, a primeira sessão deve listar os modelos disponíveis no ambiente e registrar a escolha em `memory/DECISIONS.md`. Não invente um model ID.

Perfis obrigatórios:

| Perfil | Uso | Thinking |
|---|---|---|
| Principal | arquitetura, specs, integrações, segurança, debugging complexo e revisão final | high |
| Rápido | busca, documentação, testes isolados e tarefas mecânicas de baixo risco | medium |
| Crítico | decisões irreversíveis ou revisão adversarial excepcional | xhigh/max apenas se suportado |

Seleção:

1. escolha o modelo Codex mais capaz disponível para `principal`;
2. escolha uma variante mini/econômica disponível para `rápido`;
3. não use modelo genérico inferior para integrar código crítico;
4. registre nome exato, data e motivo;
5. se os modelos não puderem ser listados, pare e peça ajuda ao usuário.

## Comando inicial

Após instalar/autenticar o Codex, confirme a sintaxe com `codex --help`. Use o equivalente suportado a:

```bash
codex --model <MODELO_CODEX_PRINCIPAL> --config model_reasoning_effort="high"
```

Se a versão usar outra flag/config, adapte somente após verificar o help local.

## Sessões

- Uma sessão = uma spec ou um objetivo coeso.
- Comece com `prompts/SESSION_START.md`.
- Finalize com `prompts/SESSION_HANDOFF.md`.
- Não dependa apenas do histórico do chat; grave decisões no repositório.
- Use compactação/resumo só depois de atualizar a memória.

## Subagentes

Use para frentes independentes e com arquivos separados. O principal mantém a spec e integra. Sessões paralelas sem isolamento são proibidas.

## Autonomia

Pode ler, pesquisar, editar e testar localmente. Deve pedir autorização antes de autenticar, enviar dados, instalar ferramenta, publicar, fazer deploy, criar remoto, push, PR, merge, excluir dados ou executar ação destrutiva.
