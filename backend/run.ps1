# Script para ejecutar Spring Boot cargando variables de entorno en Windows
$ErrorActionPreference = "Stop"

$envFiles = @(
    (Join-Path $PSScriptRoot ".env"),
    (Join-Path (Split-Path $PSScriptRoot -Parent) ".env")
)

foreach ($envFile in $envFiles) {
    if (Test-Path $envFile) {
        Write-Host "Cargando variables desde: $envFile"
        Get-Content $envFile | ForEach-Object {
            if ($_ -match '^\s*#' -or $_ -match '^\s*$') { return }
            if ($_ -match '^([^#=]+)=(.*)$') {
                $name = $matches[1].Trim()
                $value = $matches[2].Trim()
                [Environment]::SetEnvironmentVariable($name, $value, 'Process')
            }
        }
        break
    }
}

if (-not $env:SUPABASE_DB_HOST) {
    Write-Host "ADVERTENCIA: No se encontró .env. Crea backend/.env o ../.env"
}

Write-Host "Conectando a: $($env:SUPABASE_DB_HOST):$($env:SUPABASE_DB_PORT)"
Set-Location $PSScriptRoot
.\mvnw.cmd spring-boot:run @args
