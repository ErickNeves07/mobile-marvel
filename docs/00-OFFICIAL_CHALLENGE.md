# Briefing oficial — resumo rastreável

Fonte: arquivo `Marvel API Challenge — Projeto Mobile.html` fornecido pelo usuário em 27/09/2026.

## O que é obrigatório

- Aplicativo mobile com dados reais da **Comic Vine API**.
- API key individual, `format=json` e `User-Agent` descritivo.
- Busca, filtros, paginação, imagens e detalhes ricos.
- Conteúdo Marvel validado pelo publisher **Marvel Comics**, ID `4010-31`.
- Uso criativo dos dados, não apenas uma listagem.

## Endpoints citados

`characters`, `issues`, `volumes`, `teams`, `story_arcs`, `publishers`, `movies`, `powers`, `locations` e detalhe `publisher/4010-31`.

## Avaliação

- experiência de uso;
- design visual;
- uso da API;
- criatividade;
- funcionalidade/estabilidade;
- apresentação.

O briefing não informa pesos objetivos.

## Entrega

- projeto compactado;
- vídeo anônimo;
- outubro de 2026, sem dia/horário no documento.

## Lacunas que exigem confirmação

- data e horário exatos;
- alcance do anonimato;
- quota/rate limit da API;
- tamanho máximo de página;
- política de licença para imagens e personagens;
- se backend intermediário é aceito explicitamente.

## Decisão deste projeto

Apesar do nome “Marvel API Challenge”, a fonte requerida é Comic Vine. O app Android Java consumirá uma API própria do backend FastAPI, que protege a chave e normaliza a Comic Vine. Essa arquitetura continua usando dados reais da API exigida e deve ser explicada na apresentação.
