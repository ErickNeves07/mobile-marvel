# 002 — Android App Shell: Requirements

Status: **G0/G1/G2 aprovados por Erick em 2026-09-27; implementação autorizada pela decisão DEC-021**.

## Objetivo

Preparar a primeira experiência navegável do app Android e a base visual reutilizável do MVP 0, preservando a arquitetura Java/XML existente e sem depender de serviços remotos.

## Requisitos relacionados

- R-USER-001, R-USER-002, R-USER-003
- RNF-002, RNF-003, RNF-005, RNF-006, RNF-007
- Roadmap MVP 0: navegação e design tokens; saída local demonstrável que abre e navega.
- Domínio/UX: destinos conceituais Nexus, Campanhas, Forja, Coleção e Deadpool; orientação portrait-first; referência 390 × 844 dp.

## Escopo proposto

- Estrutura de navegação Android nativa compatível com Java e Views/XML.
- Tokens visuais centralizados para cores, tipografia, espaçamento, formas e elevação definidos na spec 003.
- Cinco destinos na ordem Nexus, Campanhas, Forja, Coleção e Deadpool; Forja é o destino inicial.
- Barra inferior com rótulos e estado selecionado acessível.
- Cada destino tem painel placeholder local, identificado como conteúdo demonstrativo; features completas pertencem a specs próprias.
- Direção visual e tokens definidos pelo agente dentro da direção cinematográfica + quadrinhos e requisitos de acessibilidade.
- Estados de shell e estado selecionado acessíveis, incluindo alvos de toque de ao menos 48dp e indicação que não dependa apenas de cor.
- Conteúdo local demonstrativo aprovado por Erick; nenhuma rede ou credencial.

## Fora de escopo

- Comic Vine, backend remoto, autenticação, Gemini ou qualquer acesso externo.
- Regras de jogo, combate, recompensas, progressão, merge ou conteúdo canônico novo.
- Assets Marvel de terceiros, arte final, áudio, animações complexas ou material cuja licença esteja em aberto.
- Tablet, foldable aberto, janela `sw600dp+` e landscape completo.
- Telas completas de campanha, coleção, Forja ou conversa com Deadpool.

## Fluxo principal

1. App abre na Forja.
2. Usuário percorre Nexus, Campanhas, Forja, Coleção e Deadpool pela barra inferior.
3. Destino atual apresenta seu placeholder local e tem estado selecionado identificável.
4. Mudança de destino atualiza a tela sem crash e preserva acessibilidade e layout em telefones compactos e grandes.

## Erros e casos limite

- Destino sem conteúdo implementado mostra placeholder aprovado, sem simular sucesso de rede.
- Rótulo longo ou fonte ampliada não deve ocultar o controle de navegação ou impedir sua operação.
- Navegação repetida e recriação de Activity não devem causar crash; destino atual restaura somente em `savedInstanceState`, sem persistência entre inicializações.
- Sistema com redução de movimento habilitada não recebe movimento essencial à compreensão.

## Critérios de aceite propostos

- [x] AC-01 — destinos, ordem, destino inicial e comportamento estão explícitos e aprovados antes da implementação (DEC-020).
- [x] AC-02 — tokens ficam centralizados em recursos Android e são aplicados consistentemente ao shell aprovado.
- [x] AC-03 — usuário navega entre todos os destinos; estado atual é perceptível sem depender só da cor.
- [x] AC-04 — layout funciona nos perfis compactos e grandes de telefone dentro da referência aprovada, sem corte de controles.
- [x] AC-05 — alvos de navegação têm pelo menos 44dp e rótulos/semântica são legíveis por TalkBack. Bounds e nós semânticos foram inspecionados; leitura falada em TalkBack não foi ensaiada.
- [x] AC-06 — fluxo funciona sem rede e placeholders não são apresentados como respostas remotas.
- [x] AC-07 — testes automatizados relevantes, lint e build passam após a última alteração.
- [x] AC-08 — evidência visual de cada estado principal é registrada em `evidence.md`, sem conteúdo licenciado não aprovado.

## Dependências

- Spec 001 fornece o projeto Android Java e toolchain local.
- Respostas de Erick a Q-016 a Q-019 registradas em DEC-020.
- Autorização temporária para desenvolvimento sem aprovação individual registrada em DEC-021.

## Dúvidas resolvidas

- Q-016: destinos/ordem/destino inicial.
- Q-017: conteúdo e comportamento local de cada destino.
- Q-018: padrão de navegação e estados de seleção.
- Q-019: tokens visuais aprovados.

Os painéis não implementam as funções completas dos destinos; cada feature exige requisitos/testes próprios. A exceção temporária permite avançar trabalho local documentado durante a janela autorizada.
