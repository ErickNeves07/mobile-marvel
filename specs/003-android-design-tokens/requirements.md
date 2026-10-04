# 003 — Android Design Tokens: Requirements

Status: **aprovada para implementação em 2026-09-27 por autorização explícita do usuário; direção visual delegada ao agente**.

## Objetivo

Definir e centralizar a identidade visual inicial delegada por Erick, aplicar os tokens no shell Android e torná-los reutilizáveis nas telas futuras.

## Requisitos relacionados

- R-USER-001, R-USER-002, R-USER-003
- RNF-003, RNF-005, RNF-007
- Roadmap MVP 0: design tokens.

## Escopo

- Criar paleta, escalas tipográfica e de espaçamento e estilos base em recursos Android.
- Aplicar os tokens no shell definido na spec 002.
- Documentar intenção e uso semântico dos tokens para telas futuras.

## Fora de escopo

- Fonte de terceiros, assets Marvel, animações complexas ou linguagem final específica de cada facção.
- Features completas de qualquer destino.
- Assets de terceiros, arte Marvel, serviços remotos e dependências novas.

## Fluxo principal

1. Activity inicial carrega recursos nomeados centralizados.
2. Shell aplica a direção visual delegada por Erick e aprovada em design.
3. Validação automatizada confirma recursos/build/lint e testes.

## Erros e casos limite

- Recurso referenciado ausente falha no build/lint.
- Escala de fonte do sistema não deve ser desativada; layout deve continuar dimensionado em `sp`.
- Novos valores visuais devem usar um token existente ou ser nomeados semanticamente; não duplicar literais sem motivo.

## Critérios de aceite

- [x] AC-01 — tokens de cor, tipografia, espaçamento e superfície estão nomeados semanticamente em recursos Android.
- [x] AC-02 — shell usa tokens em vez de literais visuais repetidos.
- [x] AC-03 — direção mantém base cinematográfica + quadrinhos, fundo escuro e acessibilidade; nenhuma fonte/arte externa é adicionada.
- [x] AC-04 — teste unitário e build debug passam; lint termina sem erros. Quatro avisos conhecidos estão explicados na evidência.
- [x] AC-05 — tokens não introduzem dependências, rede, persistência ou segredos.
- [x] AC-06 — intenção e regras de uso dos tokens estão documentadas em design/evidence.

## Dependências

- Bootstrap Android Java existente.
- Autorização temporária de Erick em 2026-09-27 para iniciar implementação sem aguardar aprovação formal individual de cada spec, preservando bloqueios críticos.

## Dúvidas

- Nenhuma bloqueante para a direção visual inicial. Evolução de linguagem específica por facção será tratada nas specs das telas de produto.
