# 006 — Catálogo local de campanhas: Requirements

Status: **requisitos derivados dos endpoints de arquitetura já documentados; implementação autorizada pela janela DEC-021.**

## Objetivo

Expor pelo backend um catálogo local e determinístico de campanhas para firmar o primeiro contrato de produto além de `/health`, sem Comic Vine, IA, autenticação ou persistência.

## Fontes aprovadas

- `docs/03-ARCHITECTURE.md` prevê `GET /v1/campaigns`.
- `docs/09-DOMAIN_UX_CATALOG.md` define campanha X-Men e Magneto como chefe.
- `docs/02-REQUIREMENTS.md` determina campanha própria dos X-Men e Magneto como chefe no primeiro ciclo.

Essas fontes não definem objetivos de missão, sequência de fases, recompensa, dificuldade, elenco liberado ou balanceamento. O endpoint não os inventa.

## Escopo

- Criar schema tipado de resposta com versão/coleção de campanhas estáticas locais.
- Expor `GET /v1/campaigns` e retornar lista vazia ou itens locais determinísticos segundo o catálogo compilado.
- Cadastrar apenas o registro mínimo sustentado pelas fontes; o chefe é opcional quando não estiver definido.
- Testar payload, schema, ID estável, ordem determinística e isolamento sem serviço externo.

## Fora do escopo

- Objetivos, nós, missões, desafios, recompensa, combate, dificuldade, personagens jogáveis disponíveis.
- Dados Comic Vine, imagens, IA, persistência, autenticação, rede externa ou cadastro dinâmico.
- Consumo do endpoint no Android; requer spec própria após o contrato ser exercitado localmente.

## Requisitos

- **CAMP-R-001:** `GET /v1/campaigns` responde HTTP 200 com JSON validado por Pydantic.
- **CAMP-R-002:** a lista tem ordem e IDs determinísticos; campanhas X-Men e Quarteto Fantástico correspondem aos docs, e Magneto é chefe apenas da campanha X-Men.
- **CAMP-R-003:** campos editoriais/de jogo não misturam dados Comic Vine; ausência de informação não é preenchida por inferência.
- **CAMP-R-004:** resposta é estática em memória, sem mutação entre chamadas e sem banco.
- **CAMP-R-005:** endpoint não realiza chamadas externas, não depende de credenciais e funciona offline.

## Critérios de aceite

- [x] AC-01 — sucesso HTTP 200 e schema completo da coleção.
- [x] AC-02 — campanhas X-Men/Magneto e Quarteto Fantástico usam IDs estáveis e fatos rastreáveis; só X-Men inclui chefe.
- [x] AC-03 — campos não especificados (recompensas, objetivos, dificuldade e roster) não aparecem.
- [x] AC-04 — testes provam conteúdo, ordem e resposta determinística sem Comic Vine/Gemini.
- [x] AC-05 — `pip check` e suíte backend passam sem instalação ou mudança de dependências.

## Casos limite

- Coleção estática não tem paginação enquanto tamanho e crescimento do catálogo não justificarem contrato paginado.
- Nenhum detalhe de campanha significa ausência no JSON, não valor inventado ou placeholder enganoso.
- Mudança do catálogo em memória durante execução não é suportada; alterações exigem release e testes.
