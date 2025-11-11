# Como Configurar a API Key do OpenAI

Este guia explica como configurar a variável de ambiente `OPENAI_API_KEY` no Windows para usar o chat com IA.

## 📋 Pré-requisitos

1. Ter uma conta no OpenAI (https://platform.openai.com/)
2. Obter sua API Key em: https://platform.openai.com/api-keys
3. A API Key começa com `sk-`

## 🪟 Método 1: Interface Gráfica do Windows (Recomendado)

### Passo a Passo:

1. **Abra o menu Iniciar** e digite: `variáveis de ambiente`
   - Ou pressione `Win + R`, digite `sysdm.cpl` e pressione Enter
   - Clique na aba "Avançado" → "Variáveis de Ambiente"

2. **Na janela "Variáveis de ambiente"**:
   - Em **"Variáveis do usuário"** (parte superior), clique em **"Novo..."**

3. **Preencha os campos**:
   - **Nome da variável**: `OPENAI_API_KEY`
   - **Valor da variável**: Cole sua API Key (ex: `sk-proj-...`)

4. **Clique em "OK"** em todas as janelas

5. **IMPORTANTE**: 
   - **Reinicie o terminal/IDE** (VS Code, IntelliJ, etc.) para que a variável seja reconhecida
   - Ou reinicie o Windows

### Verificar se funcionou:

Abra um **novo** PowerShell ou CMD e execute:

```powershell
# PowerShell
echo $env:OPENAI_API_KEY

# CMD
echo %OPENAI_API_KEY%
```

Se aparecer sua API Key, está configurado corretamente! ✅

---

## 💻 Método 2: PowerShell (Linha de Comando)

### Opção A: Configuração Permanente (Recomendado)

Abra o **PowerShell como Administrador** e execute:

```powershell
[System.Environment]::SetEnvironmentVariable("OPENAI_API_KEY", "sk-sua-chave-aqui", [System.EnvironmentVariableTarget]::User)
```

**Substitua** `sk-sua-chave-aqui` pela sua API Key real.

### Opção B: Usar o Script Fornecido

1. Execute o arquivo `configurar_api_key.ps1`:
   ```powershell
   .\configurar_api_key.ps1
   ```

2. Digite sua API Key quando solicitado

3. **Reinicie o terminal/IDE**

---

## 🖥️ Método 3: CMD (Prompt de Comando)

### Opção A: Configuração Permanente

Abra o **CMD como Administrador** e execute:

```cmd
setx OPENAI_API_KEY "sk-sua-chave-aqui"
```

**Substitua** `sk-sua-chave-aqui` pela sua API Key real.

### Opção B: Usar o Script Fornecido

1. Execute o arquivo `configurar_api_key.bat`:
   ```cmd
   configurar_api_key.bat
   ```

2. Digite sua API Key quando solicitado

3. **Reinicie o terminal/IDE**

---

## ⚠️ Configuração Temporária (Apenas para a Sessão Atual)

Se você quiser configurar apenas para a sessão atual do terminal (não permanente):

### PowerShell:
```powershell
$env:OPENAI_API_KEY = "sk-sua-chave-aqui"
```

### CMD:
```cmd
set OPENAI_API_KEY=sk-sua-chave-aqui
```

**Nota**: Esta configuração será perdida quando você fechar o terminal.

---

## ✅ Verificar se Está Funcionando

### 1. Verificar no Terminal:

**PowerShell:**
```powershell
echo $env:OPENAI_API_KEY
```

**CMD:**
```cmd
echo %OPENAI_API_KEY%
```

### 2. Verificar na Aplicação:

1. **Reinicie a aplicação Spring Boot**
2. Teste o endpoint de chat: `POST /ia/chat`
3. Se a API key estiver configurada corretamente, o chat funcionará
4. Se não estiver, você verá uma mensagem informativa explicando como configurar

---

## 🔧 Solução de Problemas

### Problema: A variável não aparece após configurar

**Solução:**
- Certifique-se de que **reiniciou o terminal/IDE** após configurar
- Verifique se configurou para o **usuário correto** (não sistema)
- Tente reiniciar o Windows

### Problema: A aplicação ainda não reconhece a API Key

**Solução:**
1. Verifique se a variável está configurada:
   ```powershell
   echo $env:OPENAI_API_KEY
   ```

2. Se não aparecer nada, a variável não está configurada corretamente

3. Verifique se a aplicação está lendo a variável:
   - Veja os logs da aplicação
   - Procure por mensagens como: "⚠️ API Key do OpenAI não configurada"

4. **Alternativa**: Configure diretamente no `application.properties`:
   ```properties
   spring.ai.openai.api-key=${OPENAI_API_KEY:sua-chave-aqui}
   ```

### Problema: Erro de permissão ao configurar

**Solução:**
- Execute o PowerShell ou CMD **como Administrador**
- Clique com botão direito → "Executar como administrador"

---

## 📝 Notas Importantes

1. **Nunca compartilhe sua API Key** publicamente
2. **Não commite** a API Key no Git (ela já está no `.gitignore`)
3. A API Key é **pessoal** e deve ser mantida **secreta**
4. Se sua API Key for exposta, **revogue-a imediatamente** no site da OpenAI

---

## 🚀 Próximos Passos

Após configurar a API Key:

1. **Reinicie a aplicação Spring Boot**
2. Teste o chat: `POST /ia/chat`
3. Teste outros recursos de IA:
   - Feedback empático: `POST /ia/feedback`
   - Análise semanal: `POST /ia/analise`
   - Assistente personalizado: `POST /ia/assistente`
   - Análise de ambiente: `POST /ia/ambiente`

---

## 📚 Referências

- [OpenAI Platform](https://platform.openai.com/)
- [OpenAI API Keys](https://platform.openai.com/api-keys)
- [Documentação Spring AI](https://docs.spring.io/spring-ai/reference/)

