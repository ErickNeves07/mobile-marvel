# 008 — Prévia local de campanhas: Design

## Conteúdo

Lista vertical sob o título neutro `Campanhas planejadas`, com dois cartões: `X-Men` (equipe X-Men; chefe Magneto) e `Quarteto Fantástico` (equipe Quarteto Fantástico). Ambos mostram o rótulo textual `Campanha em desenvolvimento`. O rótulo evita sugerir ação indisponível. O chefe é omitido no Quarteto porque a fonte não o definiu.

## Componentes

`MainActivity` chama `renderCampaignPreviews(page)` somente quando o destino selecionado é `CAMPAIGNS`. Cada cartão usa `surface_elevated`, `border_subtle`, raio/tokens já existentes e textos em estilos nomeados. Cartões não são clicáveis e não acrescentam uma nova rota.

Os demais destinos continuam no placeholder shell. Conteúdo repete fatos do domínio de forma mínima e é rotulado como prévia offline; não há consulta implícita ao endpoint backend.

## Acessibilidade, segurança e persistência

- Todos os fatos estão em `strings.xml`; fontes em `sp` e superfícies/dimensões em tokens.
- Ordem e leitura linear de textos permitem navegação por leitor de tela; não usar cor como único estado.
- Sem permissão de rede, ação interativa, secret ou dado pessoal.
- Sem estado persistente.

## Verificação

Unit tests/build/lint; instalação em AVD; navegar para Campanhas e verificar ambos os cartões, repetir com fonte ampliada/perfil compacto e salvar screenshot em `.validation-output` ignorado.
