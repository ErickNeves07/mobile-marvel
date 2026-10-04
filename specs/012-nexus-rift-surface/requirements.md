# 012 — Assinatura visual de ruptura no Nexus: Requirements

Status: **fatia visual local, sem mudança funcional, autorizada pela delegação de identidade visual registrada em DEC-020.**

## Objetivo

Dar ao cartão principal do Nexus uma assinatura visual própria, mantendo leitura, navegação, conteúdo e tokens atuais.

## Escopo

- Desenhar uma superfície escura com luzes geométricas sutis usando Canvas Android nativo.
- Usar cores semânticas existentes; não importar imagens, fonte ou biblioteca.
- Aplicar somente ao cartão principal do Nexus.
- Manter forma, dimensões, textos, alvos e ações atuais.

## Fora de escopo

- Novas telas/ações, lore, progressão, dados de usuário, animação, rede, assets licenciados.
- Alterar as superfícies de Campanhas, Forja, Coleção ou Deadpool.

## Requisitos

- **RIFT-R-001:** o cartão hero do Nexus tem tratamento visual reconhecível e coerente com a base cinematográfica escura/comic-tech já documentada.
- **RIFT-R-002:** decoração não captura eventos, não entra na árvore semântica e não cobre ou reduz legibilidade do texto.
- **RIFT-R-003:** paleta usa tokens existentes, sem cor hardcoded ou dependência adicional.
- **RIFT-R-004:** os outros destinos mantêm as superfícies e comportamento atuais.

## Critérios de aceite

- [x] AC-01 — camada decorativa limitada aos bounds do cartão Nexus e excluída da acessibilidade.
- [x] AC-02 — conteúdo/contraste permanece legível em perfil padrão, compacto e fonte 1.3.
- [x] AC-03 — Android offline `testDebugUnitTest`, lint e assemble passam.
- [x] AC-04 — inspeção visual AVD confirma que demais destinos continuam inalterados.
