#requires -Version 5.1
[CmdletBinding()]
param([string] $Serial = $env:ANDROID_SERIAL)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'
$repoRoot = Split-Path -Parent $PSScriptRoot
$sdkRoot = if ($env:ANDROID_HOME) { $env:ANDROID_HOME } else { Join-Path $env:LOCALAPPDATA 'Android/Sdk' }
$adb = Join-Path $sdkRoot 'platform-tools/adb.exe'
$validationRoot = if ($env:RI_VALIDATION_DIR) {
    $env:RI_VALIDATION_DIR
} else {
    Join-Path $env:TEMP 'Marvel-Ruptura-Infinita-validation'
}
$appApk = Join-Path $validationRoot 'app/outputs/apk/debug/app-debug.apk'
$testApk = Join-Path $validationRoot 'app/outputs/apk/androidTest/debug/app-debug-androidTest.apk'

foreach ($path in @($adb, $appApk, $testApk)) {
    if (-not (Test-Path -LiteralPath $path -PathType Leaf)) {
        throw "Arquivo local necessário não encontrado: $path. Rode validate-local.ps1 -Target android antes."
    }
}

$devices = @(& $adb devices | Select-Object -Skip 1 | Where-Object { $_ -match "^\S+\s+device$" })
if (-not $Serial) {
    if ($devices.Count -ne 1) {
        throw "Conecte um dispositivo Android ou defina ANDROID_SERIAL. Dispositivos prontos: $($devices.Count)."
    }
    $Serial = ($devices[0] -split '\s+')[0]
}

& $adb -s $Serial install -r $appApk
if ($LASTEXITCODE -ne 0) { throw 'Falha ao instalar o APK principal de teste.' }
& $adb -s $Serial install -r $testApk
if ($LASTEXITCODE -ne 0) { throw 'Falha ao instalar o APK instrumentado.' }

$output = @(& $adb -s $Serial shell am instrument -w -r `
    'com.erickbarbosa.rupturainfinita.test/androidx.test.runner.AndroidJUnitRunner' 2>&1)
$output | ForEach-Object { Write-Output $_ }
if ($LASTEXITCODE -ne 0 -or ($output -join "`n") -notmatch 'OK \(\d+ tests?\)') {
    throw 'A suíte instrumentada não confirmou execução bem-sucedida.'
}
