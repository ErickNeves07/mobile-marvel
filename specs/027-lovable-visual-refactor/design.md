# 027 — Refatoração visual baseada no protótipo Lovable — Design

Status: **aprovado por Erick em 2026-10-02 para reprodução fiel**. Em 2026-10-02, Erick também autorizou adotar mecânicas do Lovable; a spec 028 isola seus contratos e dúvidas de recompensa/histórico.

## Contexto e referência observada

Em 2026-10-02, a página pública Lovable respondeu HTTP 200. O HTML referencia CSS e módulos JavaScript públicos. Os recursos foram lidos para pesquisa local em `reports/lovable-research/` (ignorado pelo Git); código web compilado não será copiado para o Android.

| Área | Referência observada | Tradução Android proposta |
|---|---|---|
| Base | `#05070c`, superfícies azul grafite em camadas | `colors.xml`, tema e backgrounds nativos |
| Acentos | Nexus `#48e0ff`, Forja `#e0b45c`, Deadpool `#e53945` | tokens semânticos por destino |
| Joias | Espaço `#2e7bff`, Mente `#ffd84a`, Realidade `#e53945`, Poder `#9b5cff`, Tempo `#36d17e`, Alma `#ff8a3d` | recursos por Joia, sempre com texto/ícone |
| Tipografia | Barlow Condensed 500–800 em display; Sora 300–700 em texto | fontes Android licenciadas após registrar origem; fallback de sistema |
| Superfície | painéis escuros em gradiente, borda sutil, recorte angular | drawables Java/XML leves, sem dependência nova |
| Movimento | brilho, ruptura e pulso; CSS respeita `prefers-reduced-motion` | movimento discreto condicionado à preferência de redução |
| Navegação | barra inferior com Nexus, Campanhas, Forja, Coleção, Deadpool | manter a ordem aprovada e estado selecionado acessível |

O viewport publicado ocupa a altura integral de 390 × 844, sem barras de sistema. Uma tentativa de ocultá-las no Android compilou, mas coincidiu com ANRs repetidos do System UI no AVD mesmo após reinício. O ajuste foi revertido. A diferença de área útil permanece para G4; retomar em um emulador estável ou aparelho físico antes de adotar modo imersivo.

O protótipo web contém abertura animada e rotas adicionais (`/nexus`, `/campanhas`, `/forja`, `/colecao`, `/deadpool`, desafio, Manopla, Câmara e detalhes). Seus dados e sua progressão de demonstração diferem do app Android; a aparência e navegação visual são alvo de reprodução, enquanto mudanças de regras permanecem Q-036.

### Manopla — reforço visual solicitado em 2026-10-04

A rota pública `/manopla` foi recapturada em 390 × 844. Sua composição tem header compacto, painel de 300 dp com três anéis concêntricos, seis encaixes hexagonais nas cores das Joias e contador central, card de ressonância, seis linhas de estado e acesso à Câmara. Implementar uma tela nativa aberta pela Forja com desenho Canvas para anéis, brilho, partículas e hexágonos; usar apenas os seis estados reais de Joia Completa do SQLite. O `4/6`, `92%` e percentuais individuais da demonstração não são progresso real: o Android mostrará `N/6`, `N × 100 / 6%` e cada Joia como `100%` ou `0%` segundo inventário. Manter ativação sem consumo e acesso à Câmara conforme spec 026. Desenho decorativo não receberá foco; texto e linhas comunicam cada estado para TalkBack. Redução de movimento mostra cena estática.

O módulo público `CharacterArt-DY0gU2P_.js` desenha a figura como um único caminho SVG de silhueta sobre gradientes, anéis e partículas. A substituição deve preservar enquadramento, iluminação e camadas do card, colocando arte real do personagem no plano da silhueta. A coleção e os detalhes usam esse componente; a auditoria de todas as ocorrências será parte de T02/T06.

Na Coleção renderizada, a busca e dois trilhos de filtros precedem cartões em duas colunas, com um cartão destacado de largura total a cada cinco resultados. Enquanto as 105 artes não estiverem disponíveis, o plano do retrato pode ser um painel abstrato rotulado explicitamente como arte pendente, sem silhueta genérica. O detalhe de tiers e suas ações nativas deve continuar acessível ao tocar o cartão; a arte final substitui o plano abstrato sem alterar a regra de posse.

### Inventário de rotas publicado

O roteador público lista **19 caminhos**. A UI Android atual tem cinco destinos com subfluxos embutidos, então a fidelidade requer mapear também as telas secundárias antes de alterar navegação:

| Grupo | Caminhos Lovable | Correspondência Android atual |
|---|---|---|
| Abertura/Nexus | `/`, `/nexus`, `/desafio` | sem abertura; Nexus + desafio embutido |
| Campanha | `/campanhas`, `/campanha/$id`, `/missao/$id`, `/equipe`, `/briefing`, `/batalha`, `/recompensa`, `/final` | Campanhas, equipe/batalha/recompensa embutidas; briefing/epílogo via Deadpool |
| Joias | `/forja`, `/manopla`, `/camara` | Forja/Manopla embutidas; Câmara na Coleção |
| Personagens | `/colecao`, `/personagem/$id`, `/comparar` | Coleção e variantes embutidas; comparação editorial/jogo em painel |
| Outros | `/deadpool`, `/config` | Deadpool; sem Config dedicada |

Telas sem correspondência funcional existente podem receber apresentação fiel somente após definir a ação/estado real; não inventar fluxo ou regra de demonstração. Mudanças cruciais voltam a Erick conforme Q-036 resolvida.

## Decisões propostas

- Reproduzir o layout do protótipo em `res/values` e componentes/drawables Android reutilizáveis. Preservar Java + Views.
- Refatorar visualmente em fatias por componente e destino para manter o app executável após cada tarefa.
- Preservar IDs, repositórios, contratos de backend, dados de jogo e ações existentes. Mudanças de layout não devem criar novas recompensas ou desbloqueios.
- Redução de movimento e alto contraste terão caminhos explícitos; decoração não receberá foco de acessibilidade.
- Fontes e assets visuais só serão incorporados depois de documentar origem, autoria e licença/termos. Arte do personagem deve ser reconhecível, sem repetir a silhueta do protótipo.
- Imagens retornadas pela Comic Vine seguem o uso editorial atribuído via backend; o termo da API veda redistribuição em outra forma. Não empacotar automaticamente essas imagens no APK como arte de jogo. Para arte estática do jogo, usar arquivos fornecidos/licenciados por Erick ou outra origem com direito de incorporação documentado.
- Para a entrega atual, a spec 030 associa os 21 IDs de jogo a retratos Comic Vine auditados e reutiliza cada imagem nas cinco variantes. A fonte é marcada como editorial e não afirma que o traje da imagem corresponde ao tier. Artes próprias distintas podem substituir esse retrato no futuro, vinculadas ao ID do personagem/tier com procedência registrada.

## Alternativas e trade-offs

- WebView/porte React permitiria reproduzir CSS, mas introduziria outro runtime e duplicaria estado/contratos; incompatível com a refatoração nativa proposta.
- Copiar valores de cor isolados não atenderia a fidelidade solicitada; componentes e comparações lado a lado cobrem geometria, conteúdo e estados.
- O fonte Lovable editável pode melhorar fidelidade e entendimento de estados, porém não é necessário para definir a direção visual básica já observada.

## Arquitetura e componentes

`MainActivity` hoje concentra ~873 linhas e cria UI dinamicamente. Extrair componentes visuais e estilos sem mover lógica de domínio ou persistência nesta spec. Candidatos: header, painel angular, ação principal, chip de status, indicador de Joia, barra de progresso e navegação. A extração deve ser incremental e testada.

## Contratos e modelos

Nenhum contrato de rede, banco ou regra muda. Os componentes recebem rótulo, estado, cor semântica e callback existente. Conteúdo editorial permanece marcado como Comic Vine; atributos de jogo permanecem marcados como jogo.

## Estados de UI

Definir aparência para normal/selecionado/pressionado/desabilitado/foco, loading/vazio/erro/conteúdo e bloqueado/desbloqueado. Não depender de cor isolada para progresso ou disponibilidade.

## Persistência/migração

Nenhuma migração. Preferência de redução de movimento segue configuração do sistema; persistência própria exigiria spec separada.

## Segurança e privacidade

Sem autenticação ou envio de dados na implementação visual. Nenhum segredo no APK, logs, screenshot ou fonte. Assets externos precisam de procedência/termo antes de empacotar.

## Acessibilidade e performance

Alvos >= 48dp, contraste AA, labels semânticos e fontes escaláveis. Gradientes/efeitos são leves; listas não devem recriar toda a tela a cada atualização se isso causar custo perceptível. Medir no AVD e manter redução de movimento.

## Telemetria

Não acrescentar telemetria. Logs de diagnóstico existentes não devem expor dados pessoais ou chaves.

## Estratégia de testes

Após cada mudança de comportamento visual com impacto em interação: testes unitários/instrumentados relevantes, lint, assemble, inspeção AVD e capturas das telas em viewport padrão e compacto/fonte 1.3. Comparar lado a lado com capturas do Lovable na mesma viewport. No fim, repetir fluxos de desafio, campanha, merge, Manopla, variantes e navegação. Registrar diferença residual por tela, não apenas avaliação subjetiva.

## Rollback/fallback

Cada etapa deixa o app compilável. Recursos de fonte têm fallback de sistema; efeitos decorativos podem ser removidos sem afetar domínio. Reverter somente a fatia visual se houver regressão.
