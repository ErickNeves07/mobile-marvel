# 010 — Prévia informativa da Forja: Requirements

Status: **conteúdo deriva dos requisitos estáveis; nenhuma regra das Q-020–Q-024 é definida.**

## Objetivo

Dar conteúdo próprio à tela Forja enquanto o merge funcional aguarda decisões de economia/transação.

## Fontes

- R-USER-004/R-USER-005: seis Joias independentes e cadeia Estilhaço → Fragmento → Núcleo Instável → Joia Completa.
- `docs/09-DOMAIN_UX_CATALOG.md`: nomes Espaço, Mente, Realidade, Poder, Tempo, Alma.
- spec 005: merge funcional bloqueado por Q-020 a Q-024.
- DEC-020: Forja destino inicial; placeholders permitidos nesta fatia.

## Escopo

- Exibir as seis Joias, cada uma num cartão informativo local.
- Exibir a cadeia aprovada uma vez como explicação da Forja.
- Indicar claramente que progresso/merge estão em desenvolvimento.
- Não representar inventário nem estado do jogador.

## Fora de escopo

- Quantidades, saldos, custos, capacidade, taxas, botões de merge, recompensa, aquisição, persistência.
- Condições de Manopla/Câmara, animação, arte de terceiros, rede ou estado de gameplay.

## Requisitos

- **FORGE-UI-001:** os seis nomes aparecem em ordem estável aprovada.
- **FORGE-UI-002:** cadeia de quatro estágios é mostrada literalmente, sem proporção ou contagem.
- **FORGE-UI-003:** UI declara que progresso/merge ainda estão em desenvolvimento e não apresenta valor de inventário.
- **FORGE-UI-004:** cartões são não interativos, localizados e roláveis em viewport/fonte maior.
- **FORGE-UI-005:** offline, sem persistência, crédito ou chamada de serviço.

## Critérios de aceite

- [x] AC-01 — tela Forja exibe os seis nomes e os quatro estágios aprovados.
- [x] AC-02 — aviso de desenvolvimento está visível/acessível; nenhum contador, custo ou ação aparece.
- [x] AC-03 — conteúdo completo pode ser rolado em compacto e fonte 1.3, sem sobrepor a barra inferior.
- [x] AC-04 — build, lint e testes passam; teste unitário fixa os seis tipos/ordem.
- [x] AC-05 — screenshot AVD da tela e perfis está registrado.
