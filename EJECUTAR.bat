# Crear archivo de ejecución
@"
@echo off
chcp 65001 > nul
java "-Dfile.encoding=UTF-8" -cp "target/classes;lib/*" com.supermercado.Main
"@ | Out-File -FilePath "ejecutar.bat" -Encoding ASCII