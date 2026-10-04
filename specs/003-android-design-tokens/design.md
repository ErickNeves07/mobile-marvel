# 003 — Android Design Tokens: Design

Status: **aprovado para implementação; direção delegada por Erick e alinhada à visão do produto**.

## Direção visual

Base cinematográfica noturna com camadas azul-grafite, linhas de luz ciano e acentos de ouro infinito. Conteúdo e hierarquia lembram um painel editorial de quadrinhos por meio de recortes geométricos, filetes e títulos condensados via fonte sans-serif do sistema em pesos fortes. Não usar halftone pesado nem arte/licenças nesta etapa. Reservar ciano para ação/seleção, ouro para assinatura e teal para progresso futuro; cor nunca carrega significado sozinha.

## Decisões de sistema

- Recursos nativos Android em `values/colors.xml`, `dimens.xml`, `styles.xml` e `strings.xml`.
- Cores com papéis semânticos (canvas, surface, elevated surface, text primary/secondary, border, accent gold, accent cyan, status).
- Espaçamento em escala 4/8/12/16/24/32/48dp; alvos interativos >=48dp. Especificações de tamanhos tipográficos permanecem em `sp`.
- Tipografia baseada em `sans-serif`/`sans-serif-condensed` do sistema, em `sp`, escala definida por headline/title/body/label/caption.
- Superfícies com cantos moderados e borda; evitar sombras exageradas para manter legibilidade em telas pequenas.
- Sem fontes, bibliotecas, rede ou imagens externas.

## Arquitetura

Recursos `values/` centralizam os tokens e estilos. Layouts consomem recursos por nome. Valores inline são permitidos apenas para estrutura não visual que não seja compartilhada; não duplicar cores/dimensões/tipografia.

## Acessibilidade e contraste

Texto principal deve atingir contraste WCAG AA contra os fundos que usa; texto secundário continua legível e não é usado para instrução essencial. Foco/seleção combinam texto, estado semântico e tratamento gráfico. Preservar escala de fonte Android.

## Teste

Build/lint/test unitário Android e inspeção no AVD. Registrar paleta e amostras; validar contraste por cálculo local determinístico para pares de texto/fundo definidos.
