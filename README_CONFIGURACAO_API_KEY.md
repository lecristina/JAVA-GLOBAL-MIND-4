# 🔑 Configuração da API Key do OpenAI

## 📋 Quem Precisa Configurar?

### ❌ **NÃO precisa configurar:**
- **Usuário final** - Apenas usa o aplicativo, não precisa fazer nada
- **Usuários do app mobile/web** - Apenas consomem a API, não precisam configurar nada

### ✅ **PRECISA configurar:**
- **Desenvolvedor** - Para testar localmente durante o desenvolvimento
- **Professor/Testador** - Se estiver testando localmente no computador dele
- **Administrador do servidor** - Para configurar no servidor de produção

---

## 🎯 Cenários de Uso

### 1️⃣ **Desenvolvimento Local (Você)**

**Quando:** Você está desenvolvendo e testando no seu computador

**O que fazer:**
1. Configure a variável de ambiente `OPENAI_API_KEY` no seu Windows
2. Ou edite `application.properties` e coloque a API key diretamente (apenas para desenvolvimento)

**Como:**
- Veja o arquivo `COMO_CONFIGURAR_API_KEY.md` para instruções detalhadas
- Ou edite `src/main/resources/application.properties`:
  ```properties
  spring.ai.openai.api-key=sk-sua-chave-aqui
  ```

---

### 2️⃣ **Teste do Professor (Swagger)**

**Cenário A: Professor testa localmente no computador dele**

**O que fazer:**
- Professor precisa configurar a API key no computador dele (mesmo processo do desenvolvedor)
- Ou você pode fornecer um servidor já configurado

**Cenário B: Professor testa em servidor já configurado**

**O que fazer:**
- **Nada!** A API key já está configurada no servidor
- Professor apenas acessa o Swagger e testa

**Recomendação:**
- Para apresentação/demonstração, configure a API key no servidor
- Assim o professor não precisa fazer nada, apenas testar

---

### 3️⃣ **Produção (Servidor)**

**O que fazer:**
- Configure a variável de ambiente `OPENAI_API_KEY` no servidor
- Ou configure no arquivo de configuração do servidor (Docker, Kubernetes, etc.)

**Exemplo Docker:**
```yaml
environment:
  - OPENAI_API_KEY=sk-sua-chave-aqui
```

**Exemplo Azure/AWS:**
- Configure como variável de ambiente no painel do serviço

---

## 🔧 Configuração Atual

A aplicação já está configurada para usar variável de ambiente:

```properties
spring.ai.openai.api-key=${OPENAI_API_KEY:your-api-key-here}
```

**Como funciona:**
1. Primeiro tenta usar a variável de ambiente `OPENAI_API_KEY`
2. Se não encontrar, usa o valor padrão `your-api-key-here` (que não funciona)
3. Se a API key não estiver configurada, o chat retorna uma mensagem informativa

---

## 💡 Soluções para Apresentação/Demonstração

### Opção 1: Configurar no Servidor (Recomendado)

Se você vai apresentar em um servidor:

1. Configure a API key no servidor antes da apresentação
2. Professor/testador não precisa fazer nada
3. Todos podem testar o Swagger sem configuração adicional

### Opção 2: Configurar no application.properties (Desenvolvimento)

Para desenvolvimento/teste local, você pode colocar diretamente no arquivo:

```properties
# ⚠️ APENAS PARA DESENVOLVIMENTO - NÃO COMMITAR NO GIT
spring.ai.openai.api-key=sk-sua-chave-aqui
```

**⚠️ IMPORTANTE:** 
- **NÃO commite** a API key no Git
- Adicione ao `.gitignore` se necessário
- Use apenas para desenvolvimento local

### Opção 3: Arquivo de Configuração Local (Não versionado)

Crie um arquivo `application-local.properties` (não versionado):

```properties
# application-local.properties (não versionado)
spring.ai.openai.api-key=sk-sua-chave-aqui
```

E execute com:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

---

## 📝 Para o Professor/Testador

### Se o servidor já estiver configurado:
- ✅ **Não precisa fazer nada**
- Apenas acesse o Swagger e teste

### Se estiver testando localmente:
- Precisa configurar a API key no computador dele
- Veja `COMO_CONFIGURAR_API_KEY.md` para instruções

---

## 🚀 Para Apresentação/Demonstração

**Recomendação:** Configure a API key no servidor antes da apresentação.

**Vantagens:**
- Professor/testador não precisa fazer nada
- Funciona imediatamente
- Experiência melhor para demonstração

**Como fazer:**
1. Configure a variável de ambiente no servidor
2. Ou configure no Docker/Kubernetes/Azure/AWS
3. Reinicie a aplicação
4. Pronto! Todos podem testar

---

## ⚠️ Importante

1. **Nunca commite** a API key no Git
2. **Nunca compartilhe** a API key publicamente
3. A API key é **pessoal** e deve ser mantida **secreta**
4. Se a API key for exposta, **revogue-a** imediatamente no site da OpenAI

---

## 📚 Resumo

| Pessoa | Precisa Configurar? | Quando |
|--------|-------------------|--------|
| Usuário final | ❌ Não | Nunca |
| Desenvolvedor | ✅ Sim | Desenvolvimento local |
| Professor (servidor) | ❌ Não | Se servidor já configurado |
| Professor (local) | ✅ Sim | Se testar no computador dele |
| Admin servidor | ✅ Sim | Produção |

---

## 🔍 Verificar se Está Configurado

### No código:
A aplicação verifica automaticamente e retorna mensagem informativa se não estiver configurada.

### No servidor:
```bash
# Linux/Mac
echo $OPENAI_API_KEY

# Windows PowerShell
echo $env:OPENAI_API_KEY

# Windows CMD
echo %OPENAI_API_KEY%
```

---

## 📞 Suporte

Se tiver dúvidas sobre configuração:
1. Veja `COMO_CONFIGURAR_API_KEY.md` para instruções detalhadas
2. Verifique os logs da aplicação
3. Teste se a variável está configurada corretamente

