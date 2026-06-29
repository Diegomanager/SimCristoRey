# JENKINS - 5 INTERACCIONES AUTOMATIZADAS

## Interaccion 1: Trigger Automatico por Push
- **Evento**: Push a la rama main
- **Accion**: Jenkins detecta el cambio y ejecuta el pipeline
- **Comando**: git push origin main
- **Resultado**: Pipeline inicia automaticamente

## Interaccion 2: Compilacion Automatica
- **Evento**: Inicio del pipeline
- **Accion**: Jenkins ejecuta mvn clean compile
- **Comando**: mvn clean compile
- **Resultado**: Codigo compilado exitosamente o falla

## Interaccion 3: Ejecucion de Pruebas Unitarias
- **Evento**: Despues de compilacion exitosa
- **Accion**: Jenkins ejecuta mvn test
- **Comando**: mvn test
- **Resultado**: Todas las pruebas pasan o reporte de fallos

## Interaccion 4: Generacion de Reporte de Cobertura
- **Evento**: Despues de pruebas exitosas
- **Accion**: Jenkins ejecuta mvn jacoco:report
- **Comando**: mvn jacoco:report
- **Resultado**: Reporte de cobertura generado en target/site/jacoco/

## Interaccion 5: Notificacion por Email
- **Evento**: Fin del pipeline (exito o fallo)
- **Accion**: Jenkins envia email con resultados
- **Destinatario**: equipo@supermercado.com
- **Resultado**: Email con resumen del build

## Interaccion 6: Artefactos Generados (extra)
- **Evento**: Pipeline exitoso
- **Accion**: Jenkins archiva los artefactos
- **Artefactos**:
  - simulacion-supermercado-1.0.0.jar
  - Reporte de cobertura
  - Reporte de pruebas
- **Resultado**: Artefactos disponibles para descarga

## Interaccion 7: Limpieza de Workspace (extra)
- **Evento**: Fin del pipeline
- **Accion**: Jenkins limpia el workspace
- **Comando**: clean ws
- **Resultado**: Espacio liberado para proximo build
