@echo off
cd /d "%~dp0"
echo Compilando y copiando dependencias a target/lib...
mvn clean package -DskipTests
if errorlevel 1 (
    echo Error en la compilacion.
    pause
    exit /b
)
echo Ejecutando SimCristoRey...
java -cp "target/classes;target/lib/*" com.supermercado.presentation.cooperativa.MainCooperativa
pause