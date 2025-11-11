# 💬 Como Usar o Chat Conversacional com IA

## 📋 Visão Geral

O sistema de chat conversacional permite conversar com a IA de forma natural e dinâmica. A IA mantém o histórico da conversa e contexto do usuário, permitindo conversas contínuas e personalizadas.

## 🚀 Endpoint

**POST** `/ia/chat`

## 📝 Request Body

```json
{
  "usuarioId": 1,
  "mensagem": "Estou me sentindo muito estressado no trabalho",
  "idConversaPai": null  // Opcional - para continuar conversa existente
}
```

### Campos:
- **usuarioId** (obrigatório): ID do usuário
- **mensagem** (obrigatório): Mensagem do usuário
- **idConversaPai** (opcional): ID da conversa para continuar. Se não fornecido, inicia nova conversa

## 📤 Response

```json
{
  "resposta": "Entendo que você está passando por um momento difícil...",
  "idConversa": 123,
  "idConversaPai": 100,
  "timestamp": "2025-11-11T16:45:00",
  "contexto": "DADOS DO USUÁRIO (ÚLTIMOS 7 DIAS):\n- Média de humor: 2.3/5\n..."
}
```

## 🎯 Como Funciona

### 1. **Primeira Mensagem (Nova Conversa)**
```json
{
  "usuarioId": 1,
  "mensagem": "Olá, como você pode me ajudar?"
}
```
- Não envie `idConversaPai`
- Sistema cria nova conversa automaticamente
- Retorna `idConversaPai` na resposta

### 2. **Continuar Conversa**
```json
{
  "usuarioId": 1,
  "mensagem": "Obrigado pela dica! Como posso implementar isso?",
  "idConversaPai": 100  // Use o idConversaPai da resposta anterior
}
```
- Use o `idConversaPai` retornado na resposta anterior
- IA mantém contexto da conversa
- Histórico completo é enviado para a IA

### 3. **Nova Conversa (Após 2 horas)**
- Se não enviar `idConversaPai` e última conversa foi há mais de 2 horas
- Sistema cria nova conversa automaticamente

## 💡 Exemplos de Uso

### Exemplo 1: Pergunta sobre Estresse
```json
{
  "usuarioId": 1,
  "mensagem": "Estou me sentindo muito estressado no trabalho. O que posso fazer?"
}
```

**Resposta:**
```json
{
  "resposta": "Entendo que você está passando por um momento difícil. O estresse no trabalho de TI é comum...",
  "idConversaPai": 100,
  ...
}
```

### Exemplo 2: Continuar Conversa
```json
{
  "usuarioId": 1,
  "mensagem": "Essas técnicas funcionam mesmo?",
  "idConversaPai": 100
}
```

**Resposta:**
```json
{
  "resposta": "Sim, essas técnicas são baseadas em estudos científicos...",
  "idConversaPai": 100,
  ...
}
```

### Exemplo 3: Pergunta sobre Produtividade
```json
{
  "usuarioId": 1,
  "mensagem": "Como posso melhorar minha produtividade?"
}
```

## 🔧 Testando no Swagger

1. Acesse: `http://localhost:8080/swagger-ui.html`
2. Faça login e autorize
3. Encontre o endpoint `POST /ia/chat`
4. Clique em "Try it out"
5. Preencha:
   - `usuarioId`: 1
   - `mensagem`: "Olá, como você pode me ajudar?"
   - `idConversaPai`: deixe vazio (null)
6. Clique em "Execute"
7. Copie o `idConversaPai` da resposta
8. Para continuar, use o mesmo `idConversaPai` na próxima requisição

## 🧠 Recursos do Chat

### ✅ Mantém Contexto
- IA lembra de mensagens anteriores
- Contexto do usuário (humor, energia, etc.) é incluído
- Conversas contínuas e naturais

### ✅ Personalização
- Respostas baseadas em dados do usuário
- Temperatura dinâmica (mais criativa com histórico)
- Abordagem empática e prática

### ✅ Persistência
- Todas as mensagens são salvas no banco
- Histórico completo disponível
- Conversas podem ser retomadas

## 📊 Estrutura do Banco de Dados

A tabela `t_mt_conversas_ia` armazena:
- Mensagens do usuário (`tipo_mensagem = 'USUARIO'`)
- Respostas da IA (`tipo_mensagem = 'IA'`)
- ID da conversa pai (para agrupar mensagens)
- Contexto adicional

## 🎨 Exemplos de Mensagens

### Saúde Mental
- "Estou me sentindo muito ansioso"
- "Como posso lidar com burnout?"
- "Me sinto sobrecarregado no trabalho"

### Produtividade
- "Como posso melhorar minha produtividade?"
- "Tenho dificuldade para focar"
- "Como gerenciar melhor meu tempo?"

### Bem-estar
- "Quais hábitos saudáveis você recomenda?"
- "Como manter equilíbrio entre trabalho e vida pessoal?"
- "Me dê dicas para melhorar meu bem-estar"

## ⚠️ Observações

1. **Limite de Contexto**: Últimas 10 mensagens são enviadas para a IA
2. **Timeout de Conversa**: Conversas são agrupadas se tiverem menos de 2 horas de diferença
3. **Temperatura Dinâmica**: Aumenta com histórico (mais criatividade)
4. **Fallback**: Se GPT não disponível, retorna mensagem padrão

## 🚀 Próximos Passos

1. Teste o endpoint no Swagger
2. Faça algumas perguntas
3. Continue a conversa usando `idConversaPai`
4. Observe como a IA mantém contexto

---

**Divirta-se conversando com a IA! 🎉**

