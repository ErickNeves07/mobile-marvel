# 027 — Refatoração visual baseada no protótipo Lovable — Requirements

Status: **G0/G1/G2 aprovados por Erick em 2026-10-02**. Implementação autorizada; validação visual renderizada ainda depende de capturas/acesso ao navegador.

## Objetivo

Reproduzir com fidelidade a apresentação do protótipo publicado em `https://marvel-ruptura-infinita.lovable.app` no app Android Java. A única alteração visual explicitada por Erick é substituir as silhuetas dos personagens por representações com seus designs reais.

## Requisitos relacionados

- R-USER-001/002/003: Android Java, portrait-first, visual cinematográfico + quadrinhos.
- R-OFF-008: experiência intuitiva, visualmente forte e funcional.
- RNF-003/005/006: acessibilidade, desempenho e verificação.
- DEC-020: cinco destinos na ordem Nexus, Campanhas, Forja, Coleção, Deadpool; Forja inicial.

## Escopo proposto

- Reproduzir composição, espaçamentos, tipografia, cores, hierarquia, componentes, efeitos e navegação visual da referência, incluindo abertura e telas acessíveis do protótipo que correspondam aos fluxos Android.
- Aplicar tokens observados na publicação Lovable: fundo escuro, painéis em camadas, ciano Nexus, dourado Forja, vermelho Deadpool e seis cores das Joias.
- Recriar em recursos Android os elementos visuais do protótipo: recortes angulares, brilho, linha de ruptura, barras de progresso, ícones e tratamento dos cabeçalhos.
- Reproduzir as telas e estados correspondentes aos fluxos nativos: desafio diário, campanhas/equipe/batalha/recompensa, inventário/merge/Manopla, catálogo/progressão de variantes/editorial e Deadpool.
- Para a entrega atual, substituir a silhueta por retratos editoriais reconhecíveis obtidos da Comic Vine em tempo de execução, com crédito e link. Erick autorizou repetir o retrato do personagem nas cinco variantes enquanto não houver designs próprios. Os 105 designs específicos ficam como evolução futura opcional; nenhum retrato da API será incorporado ao APK.
- Manter textos de fonte editorial e dados de jogo claramente separados e identificados.

## Fora de escopo

- Portar React/CSS ou estado de demonstração do Lovable para o APK.
- Alterar economia, recompensas, campanhas, batalha, desbloqueios, persistência ou APIs fora da spec 028 e sem contrato aprovado para cada mudança crucial.
- Publicar, autenticar, exportar conteúdo privado ou incorporar assets de terceiros sem procedência registrada.

## Fluxo principal

O jogador abre o app na Forja, navega pelos cinco destinos e executa os fluxos existentes com uma linguagem visual coerente com o protótipo, em tela de telefone portrait.

## Erros e casos limite

- Conteúdo offline e erros de backend continuam legíveis e com ação de retry quando já existente.
- Fontes ampliadas, tela compacta, TalkBack e redução de movimento não podem ocultar conteúdo ou ações.
- Imagens e efeitos decorativos não podem bloquear interação nem substituir rótulos semânticos.
- O protótipo possui fluxo de abertura e estado de demonstração diferentes do app atual. A abertura/navegação visual seguem a referência. Erick autorizou adotar as mecânicas do Lovable em 2026-10-02; a spec 028 trata de regras/recompensas e mantém pendentes detalhes que o próprio protótipo contradiz. Saldos e progresso pré-carregados da demonstração não são progresso real do jogador.
- Ausência de imagem de um personagem/variante deve resultar em fallback visual explícito, nunca numa silhueta como representação final.

## Critérios de aceite propostos

- [ ] AC-01 — As telas Android correspondentes reproduzem visualmente as telas Lovable, incluindo composição, tipografia, cor, formas, conteúdo visual e estados; diferenças são apenas as aprovadas e documentadas.
- [ ] AC-02 — Navegação, estado selecionado, cartões, botões, cabeçalhos e cores das seis Joias são comparados lado a lado em 390 × 844 dp e telefone compacto.
- [ ] AC-03 — Regras e quantidades aprovadas na spec 028 são refletidas na UI; demais fluxos preservam seus contratos até decisão específica. Testes funcionais relevantes passam.
- [ ] AC-04 — Contraste, alvos >= 48dp, fonte 1.3, TalkBack e redução de movimento são verificados.
- [ ] AC-05 — Capturas AVD das telas e fluxos críticos são comparadas lado a lado com capturas Lovable; cada diferença remanescente tem justificativa aceita.
- [ ] AC-06 — As 105 variantes podem mostrar o retrato editorial reconhecível do personagem, inclusive repetido entre tiers, sem silhueta; a origem Comic Vine é visível/linkada e nenhum segredo/imagem da API entra no APK. A validação live no APK depende de host HTTPS.

## Dependências

- G0/G1/G2 aprovados por Erick em 2026-10-02.
- Para comparação visual exata e possível reaproveitamento de código-fonte: export ou repositório conectado e/ou capturas. O site publicado expõe HTML/CSS/bundles compilados; o link do projeto no editor (`https://lovable.dev/projects/lovp_252yras1v388ht85hfar7wgg1c`) não disponibilizou o fonte no acesso atual. A falta de capturas bloqueia a aceitação de paridade exata, mas não a implementação guiada por esses recursos.

## Decisões e pendências

- Q-036 resolvida: reproduzir design, abertura e navegação visual; preservar regras/recompensas do Android e perguntar antes de qualquer mudança crucial.
- Q-038 revista em 2026-10-04: Erick adiou as 105 artes distintas e escolheu retratos Comic Vine repetíveis por personagem como solução de entrega.
- Q-037 resolvida para G4: Chrome headless com emulação móvel capturou as rotas públicas em 390 × 844; comparações de screenshot podem prosseguir. A interface privada/editável Lovable continua inacessível, sem impedir a referência publicada.
