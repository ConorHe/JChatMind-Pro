@echo off
start "Backend" cmd /k "cd /d %~dp0jchatmind && mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local"
start "Frontend" cmd /k "cd /d %~dp0ui && npm run dev -- --open"
