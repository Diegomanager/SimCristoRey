# =============================================================================
# run.ps1 - Compila, empaqueta el uber-jar y lo ejecuta
# =============================================================================
$PROJECT_ROOT = "C:\Users\ASUS\Documents\DClass\SimCristoRey"
Set-Location $PROJECT_ROOT

Write-Host "=== Limpiando target para evitar artefactos viejos ===" -ForegroundColor Cyan
Remove-Item -Recurse -Force target -ErrorAction SilentlyContinue

Write-Host "=== Compilando y empaquetando (uber-jar) ===" -ForegroundColor Cyan
mvn clean compile assembly:single
if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] La compilación/empaquetado falló." -ForegroundColor Red
    Read-Host "Presiona Enter para salir"
    exit 1
}

$JAR = "target/bottleneck-buster-1.0.0-jar-with-dependencies.jar"
if (-not (Test-Path $JAR)) {
    Write-Host "[ERROR] No se encontró el jar: $JAR" -ForegroundColor Red
    Read-Host "Presiona Enter para salir"
    exit 1
}

Write-Host "=== Ejecutando SimCristoRey (uber-jar) ===" -ForegroundColor Cyan
java -jar $JAR