#requires -Version 5.1
<#
.SYNOPSIS
Runs local Android and/or backend checks without installing dependencies.
.EXAMPLE
powershell -ExecutionPolicy Bypass -File scripts/validate-local.ps1
.EXAMPLE
powershell -ExecutionPolicy Bypass -File scripts/validate-local.ps1 -Target android
#>
[CmdletBinding()]
param(
    [ValidateSet('all', 'android', 'backend')]
    [string] $Target = 'all'
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$repoRoot = Split-Path -Parent $PSScriptRoot
$androidRoot = Join-Path $repoRoot 'android-app'
$backendRoot = Join-Path $repoRoot 'backend'
$pythonExe = Join-Path $backendRoot '.venv/Scripts/python.exe'
$wrapper = Join-Path $androidRoot 'gradlew.bat'
$initScript = Join-Path $androidRoot 'gradle/isolated-builds.init.gradle'

function Invoke-CheckedCommand {
    param(
        [Parameter(Mandatory = $true)][string] $FilePath,
        [Parameter(Mandatory = $true)][string[]] $Arguments,
        [Parameter(Mandatory = $true)][string] $Label
    )

    Write-Host "`n== $Label ==" -ForegroundColor Cyan
    & $FilePath @Arguments
    if ($LASTEXITCODE -ne 0) {
        throw "$Label falhou com código $LASTEXITCODE."
    }
}

function Assert-LocalPath {
    param(
        [Parameter(Mandatory = $true)][string] $Path,
        [Parameter(Mandatory = $true)][string] $Description,
        [Parameter(Mandatory = $true)][ValidateSet('Leaf', 'Container')][string] $PathType
    )

    try {
        $exists = Test-Path -LiteralPath $Path -PathType $PathType -ErrorAction Stop
    } catch {
        throw "Acesso negado ao $Description em '$Path'."
    }
    if (-not $exists) {
        throw "$Description não encontrado em '$Path'."
    }
}

$environmentNames = @('JAVA_HOME', 'ANDROID_HOME', 'GRADLE_USER_HOME', 'RI_VALIDATION_DIR')
$originalEnvironment = @{}
foreach ($name in $environmentNames) {
    $originalEnvironment[$name] = [Environment]::GetEnvironmentVariable($name, 'Process')
}

try {
    if ($Target -in @('all', 'android')) {
        $studioJbr = 'C:\Program Files\Android\Android Studio\jbr'
        Assert-LocalPath -Path $wrapper -Description 'Gradle Wrapper' -PathType Leaf
        Assert-LocalPath -Path $initScript -Description 'Init script de saída isolada' -PathType Leaf
        Assert-LocalPath -Path $studioJbr -Description 'JBR do Android Studio' -PathType Container

        $sdkPath = if ($env:ANDROID_HOME) { $env:ANDROID_HOME } else { Join-Path $env:LOCALAPPDATA 'Android/Sdk' }
        Assert-LocalPath -Path $sdkPath -Description 'Android SDK local' -PathType Container

        $gradleHome = if ($env:GRADLE_USER_HOME) { $env:GRADLE_USER_HOME } else { Join-Path $env:USERPROFILE '.gradle' }
        $validationDir = if ($env:RI_VALIDATION_DIR) {
            $env:RI_VALIDATION_DIR
        } else {
            Join-Path $env:TEMP 'Marvel-Ruptura-Infinita-validation'
        }
        if (-not (Test-Path -LiteralPath $validationDir -PathType Container)) {
            New-Item -ItemType Directory -Path $validationDir -Force | Out-Null
        }

        $env:JAVA_HOME = $studioJbr
        $env:ANDROID_HOME = $sdkPath
        $env:GRADLE_USER_HOME = $gradleHome
        $env:RI_VALIDATION_DIR = $validationDir
        Push-Location $androidRoot
        try {
            Invoke-CheckedCommand -FilePath $wrapper -Arguments @(
                '--offline', '-I', 'gradle/isolated-builds.init.gradle',
                'testDebugUnitTest', 'assembleDebugAndroidTest', 'lintDebug', 'assembleDebug'
            ) `
                -Label 'Android: testes, lint e assemble debug'
        } finally {
            Pop-Location
        }
    }

    if ($Target -in @('all', 'backend')) {
        Assert-LocalPath -Path $pythonExe -Description 'Python da venv local (sem fallback global)' -PathType Leaf
        $venvConfig = Join-Path $backendRoot '.venv/pyvenv.cfg'
        Assert-LocalPath -Path $venvConfig -Description 'Configuração da venv' -PathType Leaf
        $baseInterpreterLine = Get-Content -LiteralPath $venvConfig |
            Where-Object { $_ -match '^executable\s*=\s*(.+)$' } |
            Select-Object -First 1
        if (-not $baseInterpreterLine) {
            throw "O caminho do Python base não está registrado em $venvConfig"
        }
        if ($baseInterpreterLine -match '^executable\s*=\s*(.+)$') {
            $baseInterpreter = $Matches[1].Trim()
        } else {
            throw "O caminho do Python base está em formato inválido em $venvConfig"
        }
        try {
            $baseInterpreterAccessible = Test-Path -LiteralPath $baseInterpreter -PathType Leaf -ErrorAction Stop
        } catch {
            throw "Acesso negado ao Python base exigido pela venv: $baseInterpreter"
        }
        if (-not $baseInterpreterAccessible) {
            throw "Python base exigido pela venv não encontrado: $baseInterpreter"
        }
        Assert-LocalPath -Path (Join-Path $backendRoot 'requirements-dev.txt') `
            -Description 'Lock de desenvolvimento do backend' -PathType Leaf

        Invoke-CheckedCommand -FilePath $pythonExe -Arguments @('-m', 'pip', 'check') -Label 'Backend: pip check'
        Push-Location $backendRoot
        try {
            Invoke-CheckedCommand -FilePath $pythonExe -Arguments @('-m', 'pytest') -Label 'Backend: pytest'
        } finally {
            Pop-Location
        }
    }

    Write-Host "`nValidações selecionadas concluídas: $Target" -ForegroundColor Green
} catch {
    Write-Error $_
    exit 1
} finally {
    foreach ($name in $environmentNames) {
        [Environment]::SetEnvironmentVariable($name, $originalEnvironment[$name], 'Process')
    }
}
