# 009 — Hub local do Nexus: Design

## Estrutura

Depois do cartão hero e nota placeholder existentes, Nexus ganha seção `Acessos rápidos` com quatro cartões na ordem Campanhas, Forja, Coleção, Deadpool. Cada linha mostra o nome do destino e seu resumo já aprovado em `AppDestination`/`strings.xml`.

Cartões verticais de largura total, alturas mínimas de toque de 48dp, superfície/borda/raios existentes, tipografia em recursos `sp`. A lista fica no ScrollView existente.

## Navegação

Um método único `selectDestination(AppDestination)` altera o estado e chama `renderShell`. Barra inferior e atalhos chamam esse método. O estado é salvo conforme o comportamento atual e não há nova stack.

Atalho usa `contentDescription` de ação, é focável/clicável e marcado selecionado quando a tela Nexus está ativa? Cartões representam rotas, portanto não carregam estado selected; a barra indica o destino atual. Texto visível inclui o destino e resumo para leitura.

## Não metas

Sem valor de progresso, atividade recente, recompensa, objetivo, disponibilidade online ou conteúdo que não exista no modelo atual. Os atalhos apenas navegam.

## Verificação

Unit tests para ordem de atalhos/mapeamento de destinos se viável sem dependências adicionais; AVD toca cada rota, volta ao Nexus, verifica seleção da barra e registra fonte 1.3/compact.
