# 029 — Batalha interativa e comparação de variantes — Requirements

Status: referência pública atualizada e observada em 2026-10-04; implementação autorizada por Erick.

## Objetivo

Reproduzir no Android Java os dois ajustes feitos no Lovable: batalha com mais efeitos visuais e decisões significativas do jogador; comparação que permita selecionar cada variante de um personagem, além de selecionar personagens.

## Escopo confirmado

- Reproduzir telas, escolhas, feedback, efeitos e estados da batalha atualizada no protótipo.
- Selecionar personagem e variante em ambos os lados da comparação.
- Separar atributos de jogo de dados editoriais Comic Vine; não derivar atributos de texto editorial nem deixar IA calcular balanceamento/recompensa.
- Manter progresso, retries e recompensas transacionais/idempotentes.

## Referência confirmada

- A rota pública agora expõe quatro intenções de Magneto, três decisões por turno (Investir, Proteger, Desestabilizar), barras de chefe/equipe/carga, feedback de impacto e especial Lança Psíquica ao fim da quarta rodada.
- A comparação oferece personagem e variante independentes em ambos os lados, quatro atributos escalados, diferenças, arquivo editorial do personagem e análise determinística.
- O atalho de demonstração “Pular batalha” do Lovable marca vitória sem luta; não será ligado à recompensa persistida Android até Erick decidir a regra econômica em Q-040. Abandono não concede prêmio.

## Lacunas críticas

- Recompensa por vitória segue pendente em Q-040; a spec 029 preserva o comportamento Android atual.
- Erick confirmou que a equipe escolhida deve influenciar o resultado e que a missão pode terminar em derrota e nova tentativa. O trio fixo do Lovable era somente demonstrativo.
- A rota publicada cobre somente a cena X-Men/Magneto; demais campanhas mantêm a batalha atual enquanto não houver contrato publicado para elas.

## Aceite proposto

- [x] AC-01: decisões da batalha correspondem às opções visíveis no Lovable atualizado; equipe escolhida e decisões determinam dano, vitória ou derrota com nova tentativa e feedback acessível.
- [ ] AC-02: efeitos visuais são reproduzidos em Android com caminho de movimento reduzido e sem ocultar controles/estado de batalha.
- [x] AC-03: comparação permite qualquer uma das 105 variantes em cada lado, inclusive bloqueadas como consulta no Lovable, e identifica atributos como dados de jogo authored.
- [x] AC-04: não há alteração acidental nas regras de recompensa; campanhas e progresso continuam idempotentes.
- [ ] AC-05: testes de decisão/resultado, variações de comparação e regressão instrumentada passam após última edição; capturas Android/Lovable são revisadas lado a lado.

## Fora de escopo

- Inventar habilidades, custos ou atributos que o Lovable não definiu.
- Usar IA/Comic Vine para determinar dano, dificuldade, XP ou atributos de variantes.
