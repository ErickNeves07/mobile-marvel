# 021 — Android release candidate — Requirements

Status: **release 0.2.0 signed and locally verified; public integrations and physical-phone validation remain pending.**

## Objetivo

Gerar um artefato Android release instalável com identidade de assinatura estável e funcionalidades implementadas descritas com precisão.

## Escopo

- Configurar signing opt-in por variáveis de ambiente locais, sem segredos no Gradle/Git.
- Confirmar build release e tarefas de pacote locais.
- Gerar APK release assinado e verificar certificado antes de entregar.
- Auditar e declarar funcionalidades efetivamente incluídas e limitações.

## Fora de escopo

- Armazenar assinatura/segredos no repositório ou reutilizar a debug keystore.
- Publicar no Play Store, subir backend ou criar conta/serviço externo.
- Rotular placeholders como funcionalidades completas.

## Requisitos

- Release não incorpora chaves de API.
- Signing só ativa quando todos os quatro campos `RI_RELEASE_STORE_FILE`, `RI_RELEASE_STORE_PASSWORD`, `RI_RELEASE_KEY_ALIAS`, `RI_RELEASE_KEY_PASSWORD` estão presentes localmente.
- Sem signing config, build release é explicitamente unsigned e não é entregue como release de usuário.
- A chave usada precisa persistir e ser guardada pelo proprietário; substituir chave depois impede atualização no Android.
- Artefato assinado deve ter certificado verificável e ser instalado/testado no dispositivo antes de se declarar pronto.

## Critérios de aceite

- [ ] Release inclui todos os requisitos funcionais do produto/briefing; no estado atual Comic Vine, gameplay de campanhas/desafios, IA chamada pelo app, rede Android e progressão da Manopla permanecem incompletos.
- [x] APK assinado com keystore persistente, certificado verificado e identidade documentada sem material secreto.
- [ ] Instalação/upgrade físico e smoke de todas as telas confirmado.
- [x] Gradle não guarda segredo/chave no projeto e pode compilar variante unsigned para validação local.
- [ ] Keystore/credencial têm backup portátil protegido fora deste perfil; senha atual usa DPAPI do Windows deste usuário.

## Bloqueios conhecidos

- A keystore de release foi criada em `%LOCALAPPDATA%\RupturaInfinita\release-signing`; a senha está DPAPI-encriptada no perfil Windows atual. Ainda não existe backup portátil e a perda desse perfil impede novos updates.
- Campanhas/Desafios são placeholders; Comic Vine não está integrada; Android não chama FastAPI; Groq adapter não possui rota; combate, Câmara de Variantes e desafios diários ainda não estão implementados.
