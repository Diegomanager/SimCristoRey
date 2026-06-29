pipeline {
    agent any
    
    tools {
        maven 'Maven-3.9'
        jdk 'JDK-17'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
                echo 'Codigo descargado'
            }
        }
        
        stage('Compilacion') {
            steps {
                sh 'mvn clean compile'
                echo 'Compilacion exitosa'
            }
        }
        
        stage('Pruebas Unitarias') {
            steps {
                sh 'mvn test'
                echo 'Pruebas ejecutadas'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Cobertura JaCoCo') {
            steps {
                sh 'mvn jacoco:report'
                echo 'Reporte de cobertura generado'
            }
            post {
                always {
                    publishHTML([
                        reportDir: 'target/site/jacoco',
                        reportFiles: 'index.html',
                        reportName: 'JaCoCo Coverage Report'
                    ])
                }
            }
        }
        
        stage('Pruebas de Regresion') {
            steps {
                sh 'mvn test -Dtest=*RegressionTest'
                echo 'Pruebas de regresion completadas'
            }
        }
        
        stage('Empaquetado') {
            steps {
                sh 'mvn package'
                echo 'JAR creado'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar'
                }
            }
        }
    }
    
    post {
        failure {
            echo 'Pipeline fallo'
            mail to: 'equipo@empresa.com',
                 subject: "Pipeline FAILED: ${env.JOB_NAME}",
                 body: "El pipeline ha fallado. Revisa los logs."
        }
        success {
            echo 'Pipeline completado con exito'
        }
    }
}