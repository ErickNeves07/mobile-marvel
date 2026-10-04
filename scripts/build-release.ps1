#requires -Version 5.1
[CmdletBinding()]
param()

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$repoRoot = Split-Path -Parent $PSScriptRoot
$androidRoot = Join-Path $repoRoot 'android-app'
$keyRoot = Join-Path $env:LOCALAPPDATA 'RupturaInfinita/release-signing'
$keystore = Join-Path $keyRoot 'ruptura-release.jks'
$encryptedPasswordFile = Join-Path $keyRoot 'password.dpapi'
$sdkRoot = if ($env:ANDROID_HOME) { $env:ANDROID_HOME } else { Join-Path $env:LOCALAPPDATA 'Android/Sdk' }
$buildTools = Join-Path $sdkRoot 'build-tools/36.0.0'
$apksigner = Join-Path $buildTools 'apksigner.bat'
$zipalign = Join-Path $buildTools 'zipalign.exe'
$wrapper = Join-Path $androidRoot 'gradlew.bat'
$initScript = Join-Path $androidRoot 'gradle/isolated-builds.init.gradle'
$validationRoot = if ($env:RI_VALIDATION_DIR) {
    $env:RI_VALIDATION_DIR
} else {
    Join-Path $env:TEMP 'Marvel-Ruptura-Infinita-validation'
}
$artifactDir = Join-Path $repoRoot 'artifacts'

foreach ($path in @($keystore, $encryptedPasswordFile, $apksigner, $zipalign, $wrapper, $initScript)) {
    if (-not (Test-Path -LiteralPath $path -PathType Leaf)) {
        throw "Arquivo necessário ausente: $path"
    }
}

$environmentNames = @('JAVA_HOME', 'ANDROID_HOME', 'GRADLE_USER_HOME', 'RI_VALIDATION_DIR',
    'RI_RELEASE_STORE_FILE', 'RI_RELEASE_STORE_PASSWORD', 'RI_RELEASE_KEY_ALIAS', 'RI_RELEASE_KEY_PASSWORD')
$originalEnvironment = @{}
foreach ($name in $environmentNames) {
    $originalEnvironment[$name] = [Environment]::GetEnvironmentVariable($name, 'Process')
}
$pointer = [IntPtr]::Zero

try {
    $encryptedPassword = (Get-Content -LiteralPath $encryptedPasswordFile -Raw).Trim()
    $securePassword = ConvertTo-SecureString -String $encryptedPassword
    $pointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePassword)
    $plainPassword = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($pointer)

    $env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
    $env:ANDROID_HOME = $sdkRoot
    if (-not $env:GRADLE_USER_HOME) { $env:GRADLE_USER_HOME = Join-Path $env:USERPROFILE '.gradle' }
    $env:RI_VALIDATION_DIR = $validationRoot
    $env:RI_RELEASE_STORE_FILE = $keystore
    $env:RI_RELEASE_STORE_PASSWORD = $plainPassword
    $env:RI_RELEASE_KEY_ALIAS = 'ruptura-release'
    $env:RI_RELEASE_KEY_PASSWORD = $plainPassword

    Push-Location $androidRoot
    try {
        & $wrapper --offline -I $initScript testDebugUnitTest lintRelease assembleRelease
        if ($LASTEXITCODE -ne 0) { throw "Gradle release falhou com código $LASTEXITCODE." }
    } finally {
        Pop-Location
    }

    $apk = Join-Path $validationRoot 'app/outputs/apk/release/app-release.apk'
    if (-not (Test-Path -LiteralPath $apk -PathType Leaf)) {
        throw 'Gradle não produziu o APK release assinado esperado.'
    }
    & $zipalign -c -v 4 $apk | Out-Null
    if ($LASTEXITCODE -ne 0) { throw 'APK release não está alinhado.' }
    $verification = @(& $apksigner verify --verbose --print-certs $apk 2>&1)
    if ($LASTEXITCODE -ne 0 -or ($verification -join "`n") -notmatch 'Verifies') {
        throw 'apksigner não confirmou a assinatura do APK release.'
    }

    New-Item -ItemType Directory -Path $artifactDir -Force | Out-Null
    $deliverable = Join-Path $artifactDir 'Marvel-Ruptura-Infinita-release.apk'
    Copy-Item -LiteralPath $apk -Destination $deliverable -Force
    $verification | Where-Object { $_ -match 'Verifies|Signer #1 certificate SHA-256 digest|Verified using' } |
        ForEach-Object { Write-Output $_ }
    Get-Item -LiteralPath $deliverable | Select-Object FullName, Length
    Get-FileHash -LiteralPath $deliverable -Algorithm SHA256 | Select-Object Algorithm, Hash
} finally {
    foreach ($name in $environmentNames) {
        [Environment]::SetEnvironmentVariable($name, $originalEnvironment[$name], 'Process')
    }
    if ($pointer -ne [IntPtr]::Zero) {
        [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($pointer)
    }
    $plainPassword = $null
    $securePassword = $null
}
