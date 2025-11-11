# Como Configurar Google Gemini API (Alternativa Gratuita)

O sistema agora suporta **Google Gemini API** como alternativa gratuita à OpenAI. Quando a OpenAI não estiver disponível (sem créditos, cota excedida, etc.), o sistema automaticamente tenta usar o Gemini.

## 🎯 Vantagens do Gemini

- ✅ **GRATUITO** até certo limite (60 requisições/minuto)
- ✅ Alternativa automática quando OpenAI falha
- ✅ Mesma qualidade de resposta
- ✅ Fácil configuração

## 📋 Passo a Passo

### 1. Obter API Key do Gemini

1. Acesse: https://makersuite.google.com/app/apikey
2. Faça login com sua conta Google
3. Clique em "Create API Key"
4. Copie a chave gerada (começa com `AIza...`)

### 2. Configurar no Windows

#### Opção A: Variável de Ambiente (Recomendado)

```powershell
# No PowerShell (como Administrador)
[System.Environment]::SetEnvironmentVariable("GEMINI_API_KEY", "SUA_CHAVE_AQUI", "User")
```

#### Opção B: Arquivo application.properties

Edite o arquivo `nexus/src/main/resources/application.properties`:

```properties
# Google Gemini API (Alternativa gratuita - Fallback quando OpenAI não disponível)
gemini.api-key=SUA_CHAVE_AQUI
gemini.model=gemini-pro
```

### 3. Reiniciar a Aplicação

Após configurar, reinicie a aplicação para que as mudanças tenham efeito.

## 🔄 Como Funciona

1. **Primeira tentativa**: Sistema tenta usar OpenAI
2. **Se OpenAI falhar** (sem créditos, cota excedida, etc.):
   - Sistema detecta o erro automaticamente
   - Tenta usar Gemini como fallback
   - Se Gemini estiver configurado, usa ele
   - Se não, mostra mensagem de erro

## ✅ Verificar se Está Funcionando

Quando a aplicação iniciar, você verá nos logs:

```
✅ GeminiService inicializado com API Key: AIza...
```

Se não estiver configurado:

```
⚠️ GeminiService inicializado SEM API Key. Configure gemini.api-key para usar.
```

## 🚨 Troubleshooting

### Gemini não está sendo usado como fallback?

1. Verifique se a API key está configurada corretamente
2. Verifique os logs para ver se há erros
3. Certifique-se de que a aplicação foi reiniciada após configurar

### Erro ao usar Gemini?

- Verifique se a API key está correta
- Verifique se você não excedeu o limite de requisições (60/min)
- Verifique sua conexão com a internet

## 📚 Documentação Oficial

- Google Gemini API: https://ai.google.dev/docs
- Limites e Quotas: https://ai.google.dev/pricing

