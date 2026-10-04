# Publicar o backend no Render

O arquivo `render.yaml` declara um Web Service Python no plano Free, com instalação travada por hashes, `uvicorn` e healthcheck `/health`. O Android precisa da URL HTTPS desse serviço para mostrar retratos Comic Vine e chamar o Deadpool. O progresso do jogo continua no SQLite do telefone; o Render não precisa de banco.

## Criar o serviço

1. Entre em [Render Dashboard](https://dashboard.render.com/) na sua conta e conecte o GitHub, concedendo acesso ao repositório `ErickNeves07/mobile-marvel`.
2. Escolha **New → Blueprint**, selecione `ErickNeves07/mobile-marvel`, branch `main`, e use o `render.yaml` da raiz. Confira nome `marvel-ruptura-infinita-api`, plano **Free**, build e start commands antes de aplicar.
3. Preencha `COMIC_VINE_API_KEY` e `GROQ_API_KEY` como variáveis secretas solicitadas pelo Blueprint. Copie os valores do `.env` local no painel privado do Render. Nunca cole as chaves em commit, issue, chat, URL ou parâmetros de build Android. `PYTHON_VERSION=3.13.15` já está no blueprint.
4. Aguarde o deploy ficar **Live**. Guarde a URL `https://<serviço>.onrender.com`.

## Conferir a API

Abra `<URL>/health` e `<URL>/ready`. `/health` deve responder `ok`; `/ready` deve indicar as duas configurações presentes. Em seguida abra `<URL>/v1/editorial/game-characters/wolverine`: a resposta deve ter `image_url` HTTPS, `site_url` e `source_name: Comic Vine`. A imagem deve abrir no navegador. Para testar Deadpool, use a UI Android após configurar o APK; o smoke local do provedor Groq ainda não confirmou uma resposta real.

O plano Free pode adormecer após inatividade, então a primeira chamada pode demorar. A chave Comic Vine fica somente no backend. Os retratos editoriais são carregados em tempo de execução, com crédito e link; nenhuma imagem Comic Vine é empacotada no APK.

## Gerar o APK que usa a URL publicada

No PowerShell, dentro da raiz do repositório:

```powershell
$env:ORG_GRADLE_PROJECT_riApiBaseUrl = 'https://<serviço>.onrender.com'
.\scripts\build-release.ps1
Remove-Item Env:ORG_GRADLE_PROJECT_riApiBaseUrl
```

O script gera `artifacts/Marvel-Ruptura-Infinita-release.apk` assinado. A URL é pública; não coloque chaves nesse parâmetro. Instale o APK em um telefone, abra Coleção e Comparação, confirme retrato, crédito/link, fallback sem rede, Manopla, batalha, recompensa de campanha e desafio diário. Registre o hash SHA-256 do APK efetivamente entregue. O APK offline produzido antes do deploy não buscará as imagens.
