@echo off
cd /d "C:\Users\ASUS\Documents\DClass\SimCristoRey"
mvn clean package -DskipTests
java -jar target\bottleneck-buster-1.0.0.jar
pause