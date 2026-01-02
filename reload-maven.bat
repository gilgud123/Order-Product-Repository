@echo off
REM Maven Reload Helper Script for Windows
REM This script helps IntelliJ IDEA recognize Maven dependencies

echo.
echo ====================================================
echo   Maven Project Reload Helper
echo ====================================================
echo.

echo Step 1: Cleaning project...
call mvnw.cmd clean
if %ERRORLEVEL% NEQ 0 (
    echo Error: Clean failed
    pause
    exit /b 1
)

echo.
echo Step 2: Downloading dependencies...
call mvnw.cmd dependency:resolve
if %ERRORLEVEL% NEQ 0 (
    echo Error: Dependency resolution failed
    pause
    exit /b 1
)

echo.
echo Step 3: Compiling project...
call mvnw.cmd compile
if %ERRORLEVEL% NEQ 0 (
    echo Error: Compilation failed
    pause
    exit /b 1
)

echo.
echo ====================================================
echo   SUCCESS! Now reload Maven in IntelliJ IDEA:
echo   1. Right-click on pom.xml
echo   2. Select "Maven" -^> "Reload project"
echo   OR
echo   3. Click the Maven refresh icon in Maven tool window
echo ====================================================
echo.
pause

