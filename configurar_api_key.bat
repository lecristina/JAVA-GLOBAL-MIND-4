@echo off
REM Script Batch para configurar OPENAI_API_KEY no Windows
REM Execute como Administrador para configurar permanentemente

echo ========================================
echo Configuracao da API Key do OpenAI
echo ========================================
echo.

REM Solicita a API Key
set /p API_KEY="Digite sua API Key do OpenAI (sk-...): "

if "%API_KEY%"=="" (
    echo.
    echo ERRO: API Key nao fornecida. Operacao cancelada.
    pause
    exit /b 1
)

REM Configura para o usuário atual (permanente)
setx OPENAI_API_KEY "%API_KEY%"

echo.
echo API Key configurada com sucesso!
echo.
echo IMPORTANTE:
echo - A variavel foi configurada para o usuario atual
echo - Voce precisa REINICIAR o terminal/IDE para que a variavel seja reconhecida
echo.
echo Para verificar, execute no CMD:
echo   echo %%OPENAI_API_KEY%%
echo.

pause

