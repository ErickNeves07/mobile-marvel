# 002 — Android App Shell: Design

Status: **G0/G1/G2 aprovados por Erick em 2026-09-27; design fechado para shell placeholder.**

## Contexto

O app atual é uma Activity Java com layout XML mínimo. Erick aprovou os cinco destinos, placeholders e a abertura na Forja; delegou ao agente a escolha da interação e identidade visual. Esta spec implementa o shell, enquanto as funcionalidades completas dos destinos terão specs próprias.

## Decisões

- Preservar Java + Views/XML; sem Compose/Kotlin e sem dependência nova.
- Sem rede, credencial, conteúdo remoto simulado ou persistência.
- Escopo: telefones portrait com `smallestWidth < 600dp`.
- Ordem: Nexus, Campanhas, Forja, Coleção, Deadpool; Forja é inicial.
- Barra inferior persistente com cinco destinos, labels visíveis, estado selecionado sem depender só de cor e alvo mínimo de 48dp.
- Ícones de navegação são vetores originais locais criados para o app, sem arte licenciada de terceiros.
- Cada destino mostra painel local com título, resumo de propósito derivado dos documentos existentes e aviso “EM DESENVOLVIMENTO”. Sem botões que prometam funcionalidade indisponível.
- Visual cinematográfico escuro: fundo azul-noite, superfícies azul-grafite, texto claro, ouro existente como assinatura e ciano/teal para estado de interação; bordas finas e contraste alto. Tipografia do sistema com hierarquia por escala/weight; sem fontes de terceiros.
- Tokens centralizados em recursos Android (cores, dimensões, estilos e strings). Ícones podem ser símbolos vetoriais locais simples, sem arte Marvel ou recurso externo.
- Sem pilha interna nesta spec; o botão Voltar usa o comportamento padrão do Android no nível raiz e nunca troca para outro destino arbitrariamente.
- Sem motion essencial; animações decorativas dispensáveis nesta fatia.

## Alternativas e trade-offs

A barra inferior mantém cinco áreas sempre visíveis em telefone portrait, com um gesto curto e estado reconhecível. Ícones acompanham labels para reduzir ambiguidade. Layout feito com Views nativas evita dependência adicional no shell inicial.

## Arquitetura e componentes

- `MainActivity` hospeda barra inferior e troca o painel de conteúdo.
- Modelo pequeno de destinos usa IDs estáveis `nexus`, `campaigns`, `forge`, `collection`, `deadpool`; textos ficam em `strings.xml`.
- Cores, dimensões e estilos ficam em `values/`; hierarquia em XML.
- Estado do destino vive na Activity e inicia em `forge`; nenhum dado persistente.

## Contratos e modelos

Sem contrato de rede. Modelo local: ID, label, resumo de propósito e status placeholder. Os resumos não afirmam progresso, dados de catálogo ou resposta de backend.

## Estados de UI

- Cada destino tem um painel placeholder distinto, título e indicação explícita de indisponibilidade da feature completa.
- Seleção combina label, semântica de seleção e tratamento visual.
- Nenhum loading/retry vazio, pois não há operação assíncrona.
- Ao trocar de destino, foco/semântica permanecem utilizáveis por TalkBack.

## Persistência/migração

Nenhuma. Inicialização/recriação abre Forja.

## Segurança e privacidade

Sem rede, autenticação, coleta ou logs com dados pessoais. Nenhum asset licenciado.

## Acessibilidade e performance

- Alvos da barra de pelo menos 48dp; foco e descrição semântica.
- Labels visíveis, contraste alvo WCAG AA e seleção comunicada por forma/ícone além de cor.
- Textos usam `sp` e permitem escala do sistema.
- Sem animação necessária para entender ou concluir navegação; sem imagens remotas/trabalho pesado na main thread.
- Rever layouts em telefone compacto e grande, e TalkBack quando ferramentas disponíveis.

## Telemetria

Nenhuma.

## Estratégia de testes

- Teste de unidade do catálogo/ordem de destinos e destino inicial.
- Teste de UI instrumentado não será adicionado se depender de novas bibliotecas; testar seleção via teste local de modelo e verificação no AVD.
- Rodar `testDebugUnitTest`, `lintDebug` e `assembleDebug` pelo Wrapper offline.
- Inspecionar estados no AVD compacto/grande e registrar evidências visuais locais.

## Rollback/fallback

Reverter arquivos do shell/tokens introduzidos nesta spec, sem migração/dados. Se houver incompatibilidade visual por tamanho, ajustar recursos responsivos dentro do escopo de telefone; não ampliar para tablet/foldable.
