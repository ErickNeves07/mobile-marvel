# Android app

Aplicativo Android nativo em Java. O APK assinado listado em `../artifacts/Marvel-Ruptura-Infinita-release.apk` é a versão 0.1.0 anterior às funcionalidades desta sessão; não distribuir como versão jogável atualizada. Recompile e assine com `..\scripts\build-release.ps1` após revalidar os novos fluxos.

O código-fonte agora inclui: desafio diário e recompensas locais, campanhas X-Men/Quarteto, seleção de equipes e combates determinísticos, tela de busca Comic Vine e narrativa Deadpool. O gameplay funciona offline. Para integrar API remota, compile com `-PriApiBaseUrl=https://host-backend`; provider keys ficam exclusivamente no backend. HTTP cleartext não está permitido no APK.

## Toolchain

- Android Studio 2025.2.2
- JBR 21.0.8 do Android Studio
- Android Gradle Plugin 8.13.2
- Gradle Wrapper 8.13
- minSdk 26, compileSdk/targetSdk 36

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
