# Android app

Aplicativo Android nativo em Java. O APK assinado listado em `../artifacts/Marvel-Ruptura-Infinita-release.apk` é o candidato de avaliação 0.2.0. Recompile e assine com `..\scripts\build-release.ps1` após novas mudanças.

O código-fonte agora inclui: desafio diário e recompensas locais, campanhas X-Men/Quarteto, seleção de equipes e combates determinísticos, tela de busca Comic Vine e narrativa Deadpool. O gameplay funciona offline. Para integrar API remota, compile com `-PriApiBaseUrl=https://host-backend`; provider keys ficam exclusivamente no backend. HTTP cleartext não está permitido no APK.

## Toolchain

- Android Studio 2025.2.2
- JBR 21.0.8 do Android Studio
- Android Gradle Plugin 8.13.2
- Gradle Wrapper 8.13
- minSdk 26, compileSdk/targetSdk 36

## Android Studio e telefone

Abra `android-app` como projeto no Android Studio, sincronize o Gradle e execute a configuração `app` no telefone. O build direto agora grava os arquivos gerados em `%LOCALAPPDATA%\RupturaInfinita\gradle-builds\<id-do-checkout>`, fora do OneDrive. O APK debug fica na subpasta `app\outputs\apk\debug\app-debug.apk`. Não é necessário limpar a antiga pasta `android-app\app\build` para usar a nova saída.

Se a sincronização já estava aberta antes desta correção, use **File > Sync Project with Gradle Files** e execute o build novamente. Para testar o backend publicado no telefone, use o APK release assinado em `../artifacts/Marvel-Ruptura-Infinita-release.apk`; o debug comum do Android Studio não recebe a URL de produção sem `-PriApiBaseUrl=https://mobile-marvel-8qex.onrender.com`.

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
