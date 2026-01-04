@echo off
echo ======================================
echo Product REST API - Keycloak Setup
echo ======================================
echo.

echo Starting Docker containers (PostgreSQL + Keycloak)...
docker-compose up -d

echo.
echo Waiting for services to be ready (30 seconds)...
timeout /t 30 /nobreak >nul

echo.
echo ======================================
echo Services Started Successfully!
echo ======================================
echo.
echo PostgreSQL Database:
echo   - URL: jdbc:postgresql://localhost:5432/productdb
echo   - Username: postgres
echo   - Password: postgres
echo.
echo Keycloak Admin Console:
echo   - URL: http://localhost:8180
echo   - Username: admin
echo   - Password: admin
echo.
echo ======================================
echo Next Steps:
echo ======================================
echo.
echo 1. Configure Keycloak:
echo    - Open http://localhost:8180
echo    - Login with admin/admin
echo    - Create realm: product-rest-api
echo    - Create roles: USER, ADMIN
echo    - Create client: product-rest-client
echo    - Create users (see KEYCLOAK-SETUP.md)
echo.
echo 2. Start the application:
echo    mvnw.cmd spring-boot:run
echo.
echo 3. Access Swagger UI:
echo    http://localhost:8080/swagger-ui.html
echo.
echo For detailed setup instructions, see KEYCLOAK-SETUP.md
echo.
pause

