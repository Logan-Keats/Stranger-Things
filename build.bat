@'
@echo off
title Building Stranger Things Project
echo ==========================================
echo  Building Stranger Things Project (Win CMD)
echo ==========================================

cd /d "%~dp0"
call mvnw.cmd clean test

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo BUILD FAILED!
    exit /b %ERRORLEVEL%
)

echo.
echo BUILD & TESTS SUCCESSFUL!
pause
'@ | Out-File -FilePath .\build.bat -Encoding ascii