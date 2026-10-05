# 038 — Tarefas

- [x] T01 — Definir a compatibilidade, arte vetorial, fluxo de retorno e aceite da splash antes da implementação.
- [x] T02 — Configurar dependência exata, tema inicial e recursos vetoriais animados.
  - Requisitos: AC-01, AC-02
  - Dependências: T01
  - Pronto quando: cold starts usam a SplashScreen API e retornam ao tema/app existentes.
- [x] T03 — Instalar splash antes do ciclo da Activity e adicionar saída curta acessível a movimento reduzido.
  - Requisitos: AC-02, AC-03, AC-04
  - Dependências: T02
  - Pronto quando: sem rede/delay artificial; configurações do sistema são respeitadas.
- [x] T04 — Testar, conferir lançamento e registrar screenshot/evidências e memória.
  - Requisitos: AC-05
  - Dependências: T02, T03
  - Pronto quando: lint/build/unit/instrumentação passam e tema de retorno está verificado.
