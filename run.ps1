# =============================================================================
# run.ps1 - Compila, copia dependencias a target/lib (SIN fusionar nada) y
# ejecuta con java -jar (el manifest ya apunta a target/lib/ automaticamente).
# =============================================================================
$PROJECT_ROOT = "C:\Users\ASUS\Documents\DClass\SimCristoRey"
Set-Location $PROJECT_ROOT

Write-Host "=== Compilando y copiando dependencias a target/lib ===" -ForegroundColor Cyan
mvn clean package -DskipTests
if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] La compilacion fallo." -ForegroundColor Red
    exit 1
}

Write-Host "=== Ejecutando SimCristoRey (java -jar) ===" -ForegroundColor Cyan
java -jar "target\bottleneck-buster-1.0.0.jar"