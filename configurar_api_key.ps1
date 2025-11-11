# Script PowerShell para configurar OPENAI_API_KEY
# Execute como Administrador para configurar permanentemente

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Configuração da API Key do OpenAI" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Solicita a API Key
$apiKey = Read-Host "Digite sua API Key do OpenAI (sk-...)"

if ([string]::IsNullOrWhiteSpace($apiKey)) {
    Write-Host "❌ API Key não fornecida. Operação cancelada." -ForegroundColor Red
    exit 1
}

# Configura para o usuário atual (permanente)
[System.Environment]::SetEnvironmentVariable("OPENAI_API_KEY", $apiKey, [System.EnvironmentVariableTarget]::User)

Write-Host ""
Write-Host "✅ API Key configurada com sucesso!" -ForegroundColor Green
Write-Host ""
Write-Host "IMPORTANTE:" -ForegroundColor Yellow
Write-Host "- A variável foi configurada para o usuário atual" -ForegroundColor Yellow
Write-Host "- Você precisa REINICIAR o terminal/IDE para que a variável seja reconhecida" -ForegroundColor Yellow
Write-Host "- Ou execute: refreshenv (se tiver Chocolatey)" -ForegroundColor Yellow
Write-Host ""
Write-Host "Para verificar, execute no PowerShell:" -ForegroundColor Cyan
Write-Host '  echo $env:OPENAI_API_KEY' -ForegroundColor White
Write-Host ""

