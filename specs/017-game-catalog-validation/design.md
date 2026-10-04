# 017 — Validação defensiva do catálogo de jogo: Design

Android usa `org.json` e o modelo de domínio atual. `GameCatalogParser.parse` valida presença e conteúdo de campos com trim, unicidade de IDs e tiers conhecidos/ordenados. O parser já impõe as cardinalidades 21 personagens e cinco variantes por personagem; agora também impõe IDs de variante globalmente únicos.

Backend mantém schemas Pydantic congelados e `extra="forbid"`. Validadores rejeitam strings vazias após strip e IDs duplicados em qualquer personagem ou variante no catálogo.

Testes exercitam fixtures válidas e inválidas no parser Android e no schema backend, além da igualdade entre endpoint e fonte compartilhada. A biblioteca `org.json:json:20250517` fica em `testImplementation`, não é empacotada no APK e permite testar o parser real no host.
