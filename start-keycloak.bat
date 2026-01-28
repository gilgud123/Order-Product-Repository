@echo off
echo ======================================
echo Product REST API - Keycloak Setup
echo ======================================
echo.

echo Stopping any existing containers...
docker-compose down

echo.
echo Starting Docker containers (PostgreSQL + Keycloak)...
docker-compose up -d

if errorlevel 1 (
    echo.
    echo ======================================
    echo ERROR: Failed to start containers!
    echo ======================================
    echo.
    echo Troubleshooting steps:
    echo 1. Make sure Docker Desktop is running
    echo 2. Try: docker-compose down -v
    echo 3. Try: docker-compose up -d --force-recreate
    echo 4. Check logs: docker-compose logs keycloak
    echo.
    pause
    exit /b 1
)

echo.
echo Waiting for services to be ready...
echo (This may take 30-60 seconds for Keycloak to initialize)
timeout /t 40 /nobreak >nul

echo.
echo Checking container status...
docker ps --filter "name=product-keycloak" --filter "name=productdb-postgres"

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
echo   - URL: http://localhost:8081
echo   - Username: admin
echo   - Password: admin
echo   - Note: Wait 30-60 seconds for Keycloak to fully initialize
echo.
echo ======================================
echo Next Steps:
echo ======================================
echo.
echo 1. Configure Keycloak:
echo    - Open http://localhost:8081
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
echo For troubleshooting, see KEYCLOAK-SETUP.md (Troubleshooting section)
echo.
echo To view logs: docker-compose logs -f keycloak
echo To stop services: docker-compose down
echo.
pause

