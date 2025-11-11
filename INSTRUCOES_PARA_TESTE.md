# 📋 Instruções para Teste - Professor e Testadores

## ✅ Boa Notícia: A API Key Já Está Configurada!

A API key do OpenAI **já está configurada** no arquivo `application.properties`.

## 🚀 Como Testar

### 1. Executar a Aplicação

```bash
# No diretório do projeto
mvn spring-boot:run
```

**Pronto!** A API key já está configurada e funcionará automaticamente.

### 2. Acessar o Swagger

Abra no navegador:
```
http://localhost:8080/swagger-ui.html
```

### 3. Testar o Chat com IA

1. **Faça login primeiro:**
   - Endpoint: `POST /api/auth/login`
   - Use as credenciais de teste (veja `TESTES_API.md`)

2. **Copie o token JWT** retornado

3. **Clique em "Authorize"** no Swagger e cole o token

4. **Teste o chat:**
   - Endpoint: `POST /ia/chat`
   - Body:
     ```json
     {
       "usuarioId": 1,
       "mensagem": "Estou me sentindo muito estressado no trabalho"
     }
     ```

5. **Teste a análise de imagem:**
   - Endpoint: `POST /ia/ambiente`
   - Envie uma foto do ambiente de trabalho

## ⚠️ Importante

### Se a API Key Não Funcionar

Se você receber a mensagem:
```
"Olá! Para usar o chat com IA, é necessário configurar a API Key do OpenAI..."
```

**Soluções:**

1. **Verifique se a API key está no arquivo:**
   - `src/main/resources/application.properties`
   - Procure por `spring.ai.openai.api-key`

2. **Ou configure a variável de ambiente:**
   ```bash
   # PowerShell
   $env:OPENAI_API_KEY = "SUA_API_KEY_AQUI"
   
   # CMD
   set OPENAI_API_KEY=SUA_API_KEY_AQUI
   ```

## 📝 Resumo

- ✅ **API Key já configurada** no arquivo `application.properties`
- ✅ **Não precisa configurar nada** - apenas executar a aplicação
- ✅ **Funciona imediatamente** após iniciar a aplicação
- ✅ **Todos podem testar** sem configuração adicional
- ✅ **Professor/testadores** não precisam fazer nada - apenas executar e testar

## 🔍 Verificar se Está Funcionando

Após iniciar a aplicação, teste o endpoint de chat. Se funcionar, você verá uma resposta da IA. Se não funcionar, você verá uma mensagem informativa explicando como configurar.

---

**⚠️ Nota Importante:** 
- A API key está no `application.properties` para facilitar testes
- **Para produção**, use variável de ambiente `OPENAI_API_KEY` no servidor
- Esta configuração é apenas para desenvolvimento/teste local

