@echo off
start "MCP Server" cmd /k "cd /d %~dp0mcp-server && node server.js"
start "Backend" cmd /k "cd /d %~dp0jchatmind && mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local"
start "Frontend" cmd /k "cd /d %~dp0ui && npm run dev -- --open"
