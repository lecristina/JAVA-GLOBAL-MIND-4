# 🔑 Como Resolver Problema de Cota da API OpenAI

## ❌ Problema Identificado

Você está recebendo o erro:
```
Status 429 - insufficient_quota
"You exceeded your current quota, please check your plan and billing details"
```

Isso significa que a API key excedeu a cota ou não tem créditos disponíveis.

---

## ✅ Soluções

### Opção 1: Adicionar Créditos à Conta OpenAI (Recomendado)

1. **Acesse o painel de billing da OpenAI:**
   - URL: https://platform.openai.com/account/billing
   - Faça login com sua conta OpenAI

2. **Verifique o saldo atual:**
   - Veja quantos créditos você tem disponíveis
   - Verifique quando foi a última cobrança

3. **Adicione créditos:**
   - Clique em "Add payment method" ou "Add credits"
   - Adicione um método de pagamento (cartão de crédito)
   - Adicione créditos à sua conta

4. **Verifique o plano:**
   - Veja qual plano você está usando (Free, Pay-as-you-go, etc.)
   - Considere fazer upgrade se necessário

---

### Opção 2: Aguardar Reset do Período de Cobrança

Se você está em um plano com limite mensal:

1. **Verifique quando o período reseta:**
   - Acesse: https://platform.openai.com/account/billing
   - Veja quando o próximo ciclo de cobrança começa

2. **Aguarde o reset:**
   - O limite será resetado no início do novo período
   - Você poderá usar a API novamente

---

### Opção 3: Criar uma Nova API Key (Se a atual foi revogada)

1. **Acesse o painel de API keys:**
   - URL: https://platform.openai.com/api-keys
   - Faça login com sua conta

2. **Crie uma nova API key:**
   - Clique em "Create new secret key"
   - Dê um nome descritivo (ex: "Nexus - Desenvolvimento")
   - Copie a nova API key (ela só aparece uma vez!)

3. **Atualize no projeto:**
   - Edite `src/main/resources/application.properties`
   - Substitua a API key antiga pela nova:
     ```properties
     spring.ai.openai.api-key=sk-nova-chave-aqui
     ```

4. **Reinicie a aplicação**

---

### Opção 4: Usar uma Conta Diferente

Se você tem acesso a outra conta OpenAI:

1. **Obtenha a API key da outra conta:**
   - Acesse: https://platform.openai.com/api-keys
   - Crie uma nova API key

2. **Atualize no projeto:**
   - Edite `src/main/resources/application.properties`
   - Substitua pela nova API key

---

### Opção 5: Usar um Modelo Alternativo (Temporário)

Se você não pode adicionar créditos agora, pode usar um modelo alternativo ou desabilitar temporariamente o chat:

1. **Desabilitar chat temporariamente:**
   - O chat retornará uma mensagem informativa
   - Outros recursos (feedback, análise semanal) continuam funcionando

2. **Usar modelo alternativo:**
   - Alguns modelos são mais baratos
   - Pode reduzir o consumo de créditos

---

## 🔍 Como Verificar o Status da Conta

### 1. Verificar Créditos Disponíveis

Acesse: https://platform.openai.com/account/billing

Você verá:
- **Credits remaining**: Créditos restantes
- **Usage this month**: Uso no mês atual
- **Next billing date**: Próxima data de cobrança

### 2. Verificar Uso da API

Acesse: https://platform.openai.com/usage

Você verá:
- **Requests**: Número de requisições
- **Tokens used**: Tokens consumidos
- **Cost**: Custo total

### 3. Verificar Limites do Plano

Acesse: https://platform.openai.com/account/limits

Você verá:
- **Rate limits**: Limites de requisições por minuto/hora
- **Usage limits**: Limites de uso mensal
- **Model access**: Modelos disponíveis no seu plano

---

## 💰 Planos e Preços da OpenAI

### Plano Gratuito (Free Tier)
- **Limite**: Muito baixo ou nenhum crédito
- **Ideal para**: Testes iniciais
- **Limitação**: Pode ter limite de requisições

### Pay-as-you-go
- **Custo**: Pago por uso
- **Ideal para**: Desenvolvimento e produção
- **Vantagem**: Sem limite fixo, paga apenas o que usa

### Team/Enterprise
- **Custo**: Mensal fixo
- **Ideal para**: Uso em produção com volume alto
- **Vantagem**: Limites maiores e suporte prioritário

---

## 🚀 Após Resolver o Problema

1. **Teste novamente:**
   - Acesse o Swagger: http://localhost:8080/swagger-ui.html
   - Teste o endpoint: `POST /ia/chat`
   - Deve funcionar normalmente

2. **Monitore o uso:**
   - Verifique regularmente o uso de créditos
   - Configure alertas se necessário

3. **Otimize o uso:**
   - Use modelos mais baratos quando possível
   - Reduza `max_tokens` se não precisar de respostas muito longas
   - Cache respostas quando apropriado

---

## 📝 Configuração Atual

A API key está configurada em:
- Arquivo: `src/main/resources/application.properties`
- Propriedade: `spring.ai.openai.api-key`

Para atualizar:
1. Edite o arquivo `application.properties`
2. Substitua a API key
3. Reinicie a aplicação

---

## ⚠️ Importante

1. **Nunca compartilhe sua API key** publicamente
2. **Não commite** a API key no Git (já está no `.gitignore`)
3. **Monitore o uso** regularmente para evitar surpresas na fatura
4. **Configure limites** na conta OpenAI se necessário

---

## 🔗 Links Úteis

- **Billing**: https://platform.openai.com/account/billing
- **API Keys**: https://platform.openai.com/api-keys
- **Usage**: https://platform.openai.com/usage
- **Limits**: https://platform.openai.com/account/limits
- **Pricing**: https://openai.com/pricing
- **Documentation**: https://platform.openai.com/docs

---

## 💡 Dicas para Economizar Créditos

1. **Use modelos mais baratos:**
   - `gpt-3.5-turbo` é mais barato que `gpt-4`
   - Configure no `application.properties`:
     ```properties
     spring.ai.openai.chat.options.model=gpt-3.5-turbo
     ```

2. **Reduza max_tokens:**
   - Respostas mais curtas = menos créditos
   - Atual: 500 tokens (já está otimizado)

3. **Use cache quando possível:**
   - Cache respostas similares
   - Evite chamadas repetidas

4. **Monitore e otimize:**
   - Veja quais endpoints usam mais créditos
   - Otimize prompts para serem mais concisos

---

## ✅ Resumo Rápido

**Problema:** API key sem créditos ou cota excedida

**Solução Rápida:**
1. Acesse: https://platform.openai.com/account/billing
2. Adicione créditos ou verifique quando reseta
3. Atualize a API key se necessário
4. Reinicie a aplicação
5. Teste novamente

**Precisa de ajuda?** Verifique os logs da aplicação para mensagens mais específicas sobre o erro.

