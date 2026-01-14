@echo off
REM Markdown to PDF converter using markdown-pdf npm package
REM First time setup: npm install -g markdown-pdf

echo Converting CoreJavaGuide.md to PDF...

where npx >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo Error: Node.js/npm not found. Please install Node.js first.
    echo Download from: https://nodejs.org/
    pause
    exit /b 1
)

REM Check if markdown-pdf is installed
npx markdown-pdf --version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo Installing markdown-pdf...
    npm install -g markdown-pdf
)

REM Convert the file
npx markdown-pdf CoreJavaGuide.md -o CoreJavaGuide.pdf

if %ERRORLEVEL% EQU 0 (
    echo Success! CoreJavaGuide.pdf has been created.
) else (
    echo Conversion failed. Trying alternative method...

    REM Alternative: Use md-to-pdf
    npm install -g md-to-pdf 2>nul
    npx md-to-pdf CoreJavaGuide.md

    if %ERRORLEVEL% EQU 0 (
        echo Success! CoreJavaGuide.pdf has been created.
    ) else (
        echo Conversion failed. Please install one of the following:
        echo 1. Node.js with markdown-pdf: npm install -g markdown-pdf
        echo 2. Python with weasyprint: pip install markdown weasyprint
        echo 3. Pandoc: https://pandoc.org/installing.html
    )
)

pause

