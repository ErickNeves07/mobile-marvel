# Android app

Aplicativo Android nativo em Java. O APK assinado listado em `../artifacts/Marvel-Ruptura-Infinita-release.apk` é o candidato de avaliação 0.3.0. Recompile e assine com `..\scripts\build-release.ps1` após novas mudanças.

O código-fonte inclui desafio diário e recompensas locais, seis batalhas interativas, seleção de equipe possuída, busca Comic Vine e narrativa Deadpool. O gameplay funciona offline, mas o build comum do Android Studio usa por padrão `https://mobile-marvel-8qex.onrender.com` para imagens/editorial/IA. `-PriApiBaseUrl=https://host-backend` substitui o host e `-PriApiBaseUrl=` desliga o backend. Provider keys ficam exclusivamente no servidor; HTTP cleartext não é permitido no APK.

## Toolchain

- Android Studio 2025.2.2
- JBR 21.0.8 do Android Studio
- Android Gradle Plugin 8.13.2
- Gradle Wrapper 8.13
- minSdk 26, compileSdk/targetSdk 36

## Android Studio e telefone

Abra `android-app` como projeto no Android Studio, sincronize o Gradle e execute a configuração `app` no telefone. O build direto agora grava os arquivos gerados em `%LOCALAPPDATA%\RupturaInfinita\gradle-builds\<id-do-checkout>`, fora do OneDrive. O APK debug fica na subpasta `app\outputs\apk\debug\app-debug.apk`. Não é necessário limpar a antiga pasta `android-app\app\build` para usar a nova saída.

**Build Project** apenas compila; use **Run 'app'** para atualizar o aplicativo instalado. Confira a versão nas informações do app no telefone: esta implementação é `0.4.0` (`versionCode` 4). A tela com “Fundação Android pronta para a próxima ruptura” é da versão `0.1.0` antiga.

Se a sincronização já estava aberta antes desta correção, use **File > Sync Project with Gradle Files** e execute Run `app` para instalar o debug 0.3.0 no telefone com a mesma assinatura debug. O host Render já está configurado no build comum.

Se houver uma versão debug instalada, o APK release usa outra assinatura e não pode substituí-la preservando os dados. Para atualizar pelo terminal sem trocar a assinatura, gere o debug e instale com `adb install -r <caminho-do-app-debug.apk>`.

Nesta máquina, para instalação manual da avaliação 0.3.0, use `../artifacts/Marvel-Ruptura-Infinita-debug-0.3.0.apk` (assinatura debug, compatível com o debug já instalado). APKs são ignorados pelo Git; em outro checkout, gere o seu. Não escolha um `app-debug.apk` de uma pasta `build` antiga: a saída Gradle atual fica em `%LOCALAPPDATA%\RupturaInfinita\gradle-builds\...` e esses arquivos antigos podem ter outra versão. Confirme `0.3.0` nas informações do aplicativo após instalar.

## Verificação no Windows

Use o JBR e SDK já instalados sem alterar o ambiente global:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
$env:GRADLE_USER_HOME = "$env:USERPROFILE\.gradle"
$env:RI_VALIDATION_DIR = Join-Path $env:TEMP 'Marvel-Ruptura-Infinita-validation'
.\gradlew.bat --offline -I gradle/isolated-builds.init.gradle testDebugUnitTest lintDebug assembleDebug
```

Wrapper, Android SDK e dependências precisam estar disponíveis no cache local. O primeiro ciclo cobre telefones em portrait e menor que `sw600dp`.
