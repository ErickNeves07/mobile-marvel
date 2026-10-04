# 021 — Android release candidate — Design

## Assinatura

`android-app/app/build.gradle` adiciona signing opt-in por quatro variáveis de processo `RI_RELEASE_*`. O arquivo de keystore e credenciais permanecem fora do repositório. Ausência parcial/total gera variante release unsigned. Não compartilhar segredos entre testes/artefatos.

## Artefato

Validar `assembleRelease` offline; saídas intermediárias unsigned permanecem no diretório temporário de build. Artefato instalável assinado é copiado para `artifacts/` somente após `zipalign` e `apksigner verify --print-certs`. Assinatura usa key persistente de Erick; Android exige mesma identidade de assinatura para atualizar instalação anterior.

## Produto

Release é apenas um build mode, não fechamento das features. App offline inclui navegação, catálogo interno, tema Nexus, placeholders de Campanhas/Deadpool e Forja SQLite. APIs Comic Vine, chamadas IA, campanhas/batalhas/desafios e conexão Android–backend continuam fora da build funcional.

## Segurança

Não imprimir, testar por força bruta, incluir em argumentos visíveis, código, Markdown ou screenshots os valores da keystore/senha. Não mover keystore para dentro do workspace versionado. Password file atual usa DPAPI, portanto é recuperável somente no perfil Windows atual. Nenhuma chamada externa.
