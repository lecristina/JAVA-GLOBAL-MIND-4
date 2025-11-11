# 🧪 Como Testar IA no Swagger - Guia Completo

**Resposta curta:** ✅ **SIM, você pode testar TUDO pelo Swagger!** Não precisa de frontend para testar.

---

## 🎯 Onde Testar?

### ✅ Swagger UI (Recomendado para Testes)
- **URL:** `http://localhost:8080/swagger-ui.html`
- **Vantagens:**
  - ✅ Interface visual e fácil
  - ✅ Todos os endpoints de IA disponíveis
  - ✅ Upload de imagem funciona perfeitamente
  - ✅ Não precisa de frontend
  - ✅ Testa tudo sem escrever código

### 🌐 Frontend (Depois, para Produção)
- Integre quando for fazer o app mobile/web
- Use os mesmos endpoints que testou no Swagger

---

## 📋 Passo a Passo Completo no Swagger

### 1️⃣ Iniciar a Aplicação

```bash
cd nexus
mvn spring-boot:run
```

Aguarde até ver:
```
Started NexusApplication in X.XXX seconds
```

### 2️⃣ Acessar o Swagger

Abra no navegador:
```
http://localhost:8080/swagger-ui.html
```

### 3️⃣ Fazer Login e Obter Token

1. **Procure por:** `POST /api/auth/login` (tag "Autenticação")
2. **Clique em:** "Try it out"
3. **Preencha o Request Body:**
   ```json
   {
     "email": "seu-email@example.com",
     "senha": "sua-senha"
   }
   ```
4. **Clique em:** "Execute"
5. **Copie o token** da resposta (campo `token`)

### 4️⃣ Autorizar no Swagger

1. **Clique no botão verde "Authorize"** (cadeado no topo da página)
2. **Cole o token** (SEM a palavra "Bearer")
3. **Clique em:** "Authorize"
4. **Clique em:** "Close"

Agora você está autenticado! ✅

---

## 🤖 Testando os Endpoints de IA

### ✅ 1. Feedback Empático (`POST /ia/feedback`)

**O que faz:** Gera mensagem empática usando GPT baseada no humor e produtividade.

**Como testar:**
1. Procure por `POST /ia/feedback` (tag "IA Generativa")
2. Clique em "Try it out"
3. Preencha:
   ```json
   {
     "usuarioId": 1,
     "humor": 2,
     "produtividade": "baixa"
   }
   ```
4. Clique em "Execute"
5. Veja a resposta com a mensagem empática gerada pelo GPT

**Resposta esperada:**
```json
{
  "mensagem": "Você parece cansado hoje. Tente fazer uma pausa...",
  "timestamp": "2024-11-11T15:30:00",
  "idAlerta": 123
}
```

---

### ✅ 2. Análise Semanal (`POST /ia/analise`)

**O que faz:** Analisa dados dos últimos 7 dias e gera relatório completo com GPT.

**Como testar:**
1. Procure por `POST /ia/analise`
2. Clique em "Try it out"
3. Preencha:
   ```json
   {
     "usuarioId": 1
   }
   ```
4. Clique em "Execute"
5. Veja a análise completa com resumo, risco de burnout e sugestões

**Resposta esperada:**
```json
{
  "resumoSemanal": "Analisando seus dados dos últimos 7 dias...",
  "riscoBurnout": "medio",
  "sugestoes": [
    "Mantenha hábitos saudáveis...",
    "Faça pausas regulares...",
    "Monitore seus indicadores..."
  ],
  "timestamp": "2024-11-11T15:30:00"
}
```

---

### ✅ 3. Assistente Pessoal (`POST /ia/assistente`)

**O que faz:** Gera conteúdo personalizado (curiosidades, prevenção, motivação, etc) usando GPT.

**Como testar:**
1. Procure por `POST /ia/assistente`
2. Clique em "Try it out"
3. Preencha:
   ```json
   {
     "usuarioId": 1,
     "tipoConsulta": "curiosidade"
   }
   ```
4. Clique em "Execute"
5. Veja o conteúdo personalizado gerado

**Tipos disponíveis:**
- `curiosidade` - Curiosidades educativas
- `prevencao` - Dicas de prevenção
- `motivacao` - Mensagens motivacionais
- `dica_pratica` - Dicas práticas
- `reflexao` - Reflexões profundas

**Resposta esperada:**
```json
{
  "titulo": "Curiosidade: O Poder das Pausas",
  "conteudo": "Estudos mostram que fazer pausas...",
  "tipo": "curiosidade",
  "acoesPraticas": [
    "Configure lembretes para pausas...",
    "Use a técnica Pomodoro...",
    "Durante as pausas, faça algo diferente..."
  ],
  "reflexao": "Como você pode incorporar pausas regulares na sua rotina?",
  "timestamp": "2024-11-11T15:30:00"
}
```

---

### ✅ 4. Visão Computacional - Upload de Imagem (`POST /ia/analise-ambiente`)

**O que faz:** Analisa foto do ambiente de trabalho usando Deep Learning (Hugging Face).

**Como testar:**
1. Procure por `POST /ia/analise-ambiente`
2. Clique em "Try it out"
3. Preencha os parâmetros:
   - `usuarioId`: `1` (número)
   - `foto`: Clique em "Choose File" e selecione uma imagem (JPEG, PNG, etc)
4. Clique em "Execute"
5. Aguarde alguns segundos (primeira chamada pode demorar - modelo carregando)
6. Veja a análise completa do ambiente

**Resposta esperada:**
```json
{
  "nivelFoco": "alto",
  "organizacao": "boa",
  "iluminacao": "excelente",
  "objetosDetectados": [
    "desk (95.23%)",
    "computer (87.45%)",
    "monitor (82.10%)"
  ],
  "sugestoes": [
    "Mantenha o ambiente organizado...",
    "Faça pausas regulares..."
  ],
  "resumoAnalise": "✅ Análise realizada com modelo de Deep Learning (IA REAL)...",
  "timestamp": "2024-11-11T15:30:00",
  "idAlerta": 124
}
```

**⚠️ Dica:** Se a primeira chamada retornar erro 503, aguarde alguns segundos e tente novamente (modelo carregando).

---

## 🎬 Fluxo Completo de Teste

### Teste Rápido (5 minutos):

1. **Login** → Obter token
2. **Autorizar** → Colar token
3. **Testar Feedback** → `POST /ia/feedback` com humor=2, produtividade="baixa"
4. **Testar Análise** → `POST /ia/analise` com usuarioId=1
5. **Testar Assistente** → `POST /ia/assistente` com tipoConsulta="curiosidade"
6. **Testar Visão** → `POST /ia/analise-ambiente` com uma foto

---

## 📸 Exemplo Visual - Swagger UI

```
┌─────────────────────────────────────────────────────────┐
│  Swagger UI - http://localhost:8080/swagger-ui.html   │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  [Authorize 🔒]  (clique aqui para colar o token)     │
│                                                         │
│  ┌─────────────────────────────────────────────────┐  │
│  │ IA Generativa                                    │  │
│  ├─────────────────────────────────────────────────┤  │
│  │                                                 │  │
│  │ POST /ia/feedback                               │  │
│  │ Gerar feedback empático usando GPT              │  │
│  │ [Try it out]                                    │  │
│  │                                                 │  │
│  │ POST /ia/analise                                │  │
│  │ Gerar análise semanal inteligente               │  │
│  │ [Try it out]                                    │  │
│  │                                                 │  │
│  │ POST /ia/assistente                            │  │
│  │ Assistente pessoal - Conteúdo personalizado    │  │
│  │ [Try it out]                                    │  │
│  │                                                 │  │
│  │ POST /ia/analise-ambiente                      │  │
│  │ Analisar ambiente usando Visão Computacional   │  │
│  │ [Try it out]                                    │  │
│  │                                                 │  │
│  │   usuarioId: [1        ]                       │  │
│  │   foto:      [Choose File]                    │  │
│  │                                                 │  │
│  │   [Execute]                                     │  │
│  └─────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
```

---

## 🔍 Verificando se Usa IA Real

### Nos Logs da Aplicação:

Procure por estas mensagens no console:

**✅ IA REAL (funcionando):**
```
✅ IA REAL: Resposta recebida do GPT (OpenAI). Tamanho: 150 caracteres
✅ IA REAL: Análise recebida do modelo de Deep Learning. Resultado: [...]
✅ Processando resultados REAIS do modelo de IA (3 itens detectados)
```

**⚠️ FALLBACK (sem IA):**
```
⚠️ API Key do OpenAI não configurada. Retornando feedback padrão (FALLBACK - não usa IA real).
⚠️ FALLBACK: Usando análise heurística (API não retornou resultados válidos)
```

### Na Resposta JSON:

- **Com IA Real:** `resumoAnalise` contém "✅ Análise realizada com modelo de Deep Learning (IA REAL)"
- **Com Fallback:** `resumoAnalise` contém "⚠️ Análise baseada em padrões comuns (fallback - IA não disponível)"

---

## 🌐 Integração no Frontend (Depois)

Quando for integrar no frontend, use os mesmos endpoints:

### React/Next.js:
```javascript
// Feedback
const response = await fetch('http://localhost:8080/ia/feedback', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  },
  body: JSON.stringify({
    usuarioId: 1,
    humor: 2,
    produtividade: 'baixa'
  })
});

// Upload de Imagem
const formData = new FormData();
formData.append('foto', file);
formData.append('usuarioId', 1);

const response = await fetch('http://localhost:8080/ia/analise-ambiente', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`
  },
  body: formData
});
```

### React Native:
```javascript
// Mesmo código, mas use a URL do servidor de produção
const API_URL = 'https://sua-api.com';
```

---

## ✅ Resumo

| Endpoint | Testa no Swagger? | Precisa Frontend? |
|----------|-------------------|-------------------|
| `/ia/feedback` | ✅ SIM | ❌ NÃO |
| `/ia/analise` | ✅ SIM | ❌ NÃO |
| `/ia/assistente` | ✅ SIM | ❌ NÃO |
| `/ia/analise-ambiente` | ✅ SIM | ❌ NÃO |

**Conclusão:** Você pode testar **TUDO** pelo Swagger! O frontend é opcional e só é necessário quando for fazer o app final.

---

## 🎯 Próximos Passos

1. ✅ **Teste no Swagger primeiro** (agora mesmo)
2. ✅ **Valide que está usando IA real** (verifique logs)
3. 🌐 **Integre no frontend depois** (quando for fazer o app)

---

**Última atualização:** 11/11/2024

