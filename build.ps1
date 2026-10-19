# Local build and test runner for Windows Powershell
$ErrorActionPreference = "Stop"

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host " Building Stranger Things Project (Powershell)" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

# Ensure script executes from project root
Set-Location -Path $PSScriptRoot

.\mvnw.cmd clean test

if ($LASTEXITCODE -eq 0)
{
    Write-Host "`nBUILD & TESTS SUCCESSFUL!" -ForegroundColor Green
}
 else
{
    Write-Host "`nBUILD FAILED!" -ForegroundColor Red
    exit 1
}