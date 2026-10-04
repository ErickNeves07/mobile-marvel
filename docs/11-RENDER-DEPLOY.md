# Publicar o backend no Render

O backend é um **Web Service** Python no plano Free. O `render.yaml` registra essa configuração para um Blueprint; ao criar pelo menu **New → Web Services**, preencha os mesmos campos manualmente. O Android precisa da URL HTTPS desse serviço para mostrar retratos Comic Vine e chamar o Deadpool. O progresso do jogo continua no SQLite do telefone; o Render não precisa de banco.

## Criar o serviço

1. Entre em [Render Dashboard](https://dashboard.render.com/) na sua conta e conecte o GitHub, concedendo acesso ao repositório `ErickNeves07/mobile-marvel`.
2. Escolha **New → Web Services** e selecione esse repositório. Use estes campos:

   | Campo | Valor |
   |---|---|
   | Name | `marvel-ruptura-infinita-api` (ou outro nome livre) |
   | Branch | `main` |
   | Language/Runtime | `Python 3` |
   | Root Directory | deixe vazio (raiz do repositório) |
   | Build Command | `python -m pip install --require-hashes -r backend/requirements.txt` |
   | Start Command | `cd backend && python -m uvicorn app.main:app --host 0.0.0.0 --port $PORT` |
   | Instance/Plan | `Free` |
   | Health Check Path (Advanced) | `/health` |

3. Em **Environment** ou **Advanced**, adicione `PYTHON_VERSION` com valor `3.13.15`. Adicione também `COMIC_VINE_API_KEY` e `GROQ_API_KEY` com os valores do `.env` local, somente no painel privado. Nunca cole as chaves em commit, issue, chat, URL ou parâmetros de build Android.
4. Clique **Create Web Service**, aguarde o deploy ficar **Live** e guarde a URL `https://<serviço>.onrender.com`. Como a criação foi manual, o Render usa os campos acima; ele não importa automaticamente o `render.yaml`.

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
