# Arquitetura técnica

## Topologia

```text
Android Java
  ├─ presentation (Activities/Fragments, ViewModels, UI state)
  ├─ domain (use cases, entities, policies)
  ├─ data (repositories, Room, network DTO/mappers)
  └─ core (result, errors, telemetry, design tokens)
          │ HTTPS
          ▼
FastAPI backend
  ├─ API facade para o app
  ├─ Comic Vine gateway + cache
  ├─ game content/config
  ├─ AI director (LangChain + provider adapter)
  └─ validation, rate limit e observability
          ├─ Comic Vine API
          └─ Provedor LLM: Groq via adapter no FastAPI
```

## Android

Tecnologias-alvo, confirmadas antes de pinagem:

- Java, Gradle e AndroidX;
- Android views + Repository SQLite local; gameplay/Forja continuam persistentes offline;
- `HttpURLConnection` bounded client; não adiciona dependência de rede ao APK;
- DTOs JSON locais para APIs backend;
- Navigation Component;
- WorkManager somente para trabalho persistente real;
- biblioteca de imagens escolhida por spike e fixada em versão exata.

Não fazer rede na main thread. UI observa estados explícitos (`loading`, `content`, `empty`, `error`). DTOs não escapam da camada data.

## Backend

- Python + FastAPI.
- Adapter Groq stdlib e rota FastAPI `POST /v1/ai/deadpool-line` implementados; narrativa usa contexto allowlisted e fallback, sem decidir regras do jogo.
- O adapter lê `GROQ_API_KEY` somente em runtime backend. Nunca expor a chave no APK, resposta, logs ou repositório.
- Pydantic valida request/response.
- Timeout, retry limitado com jitter, circuit breaker simples e fallback roteirizado.
- Cache reduz dependência e latência da Comic Vine.
- Segredos apenas em variáveis de ambiente/secret manager local; `.env` ignorado.

## Limite de confiança da IA

A IA recebe somente fatos aprovados e retorna JSON estruturado. O backend valida:

- schema;
- tamanho;
- IDs existentes;
- tom permitido;
- ausência de fatos fora do contexto;
- objetivos/modificadores de allowlist.

Recompensa, balanceamento, vitória e progressão são determinísticos.

## Dados editoriais x jogo

- `EditorialCharacter`: ID Comic Vine, nome, descrição, imagem, powers, teams, issues e publisher.
- `GameCharacter`: ID interno, papel, atributos, habilidades, sinergias e variante equipada.
- `CharacterLink`: associação explícita entre ambos.

Nunca apresentar atributo inventado como informação oficial.

## Contratos iniciais

- `GET /v1/game/characters` — índice local dos 21 personagens jogáveis e cinco nomes de variante por personagem, carregado de `shared/game_catalog.json`; separado de dados editoriais.
- `GET /v1/campaigns` — resumos locais das campanhas definidas.
- `GET /v1/editorial/characters?q=&limit=&offset=` and `GET /v1/editorial/characters/{id}` — Comic Vine server proxy; validates Marvel publisher.
- `POST /v1/ai/deadpool-line` — short contextual narrative with scripted fallback.
- `GET /v1/characters?query=&team=&limit=&cursor=`
- `GET /v1/characters/{id}`
- `GET /v1/campaigns`
- `POST /v1/ai/briefings`
- `POST /v1/ai/team-analysis`
- `POST /v1/ai/daily-hints`

Contratos definitivos pertencem à spec da feature e exigem exemplos de sucesso/erro.

## Persistência

Room é o destino arquitetural para dados do app quando sua integração for introduzida com dependência compatível. A spec 005 usa SQLite nativo transacional para o primeiro inventário local, sem dependência nova; migração para Room deve ser versionada e testada. Dados de jogo estáticos devem ser orientados a dados, não espalhados em condicionais.

## Estrutura futura sugerida

```text
android-app/
backend/
docs/
specs/
memory/
prompts/
```

A criação dos projetos só ocorre após specs de bootstrap e confirmação das versões instaladas.
