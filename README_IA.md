# Módulo IA Generativa - MindTrack/Nexus

## 📋 Visão Geral

Este módulo integra GPT (OpenAI) para gerar feedbacks empáticos e análises inteligentes baseadas nos dados de humor e produtividade dos usuários. O módulo funciona com integração direta à API OpenAI via HTTP, sem depender do Spring AI.

## 🚀 Como Rodar

### Pré-requisitos

1. **Java 17+**
2. **Maven 3.6+**
3. **Chave de API OpenAI** (obtenha em https://platform.openai.com/api-keys)

### Configuração

1. Configure a variável de ambiente ou adicione no `application.properties`:

```properties
spring.ai.openai.api-key=sua-chave-api-aqui
spring.ai.openai.chat.options.model=gpt-3.5-turbo
spring.ai.openai.chat.options.temperature=0.7
```

2. Ou via variável de ambiente:

```bash
export OPENAI_API_KEY=sua-chave-api-aqui
```

### Executar

```bash
mvn spring-boot:run
```

## 📡 Endpoints

### POST /ia/feedback

Gera feedback empático personalizado usando GPT.

**Request:**
```json
{
  "usuarioId": 1,
  "humor": 2,
  "produtividade": "baixa"
}
```

**Response:**
```json
{
  "mensagem": "Você parece cansado hoje. Tente fazer uma pausa curta e respirar fundo. Estamos aqui para apoiá-lo.",
  "timestamp": "2025-11-11T15:30:00",
  "idAlerta": 123
}
```

**Regras:**
- Envia dados para GPT usando Spring AI ou chamada HTTP direta
- Gera mensagem empática (máximo 150 caracteres)
- Armazena o feedback na tabela `t_mt_alertas_ia` do Oracle
- Retorna feedback padrão se API não estiver configurada

### POST /ia/analise

Gera análise semanal completa usando GPT.

**Request:**
```json
{
  "usuarioId": 1
}
```

**Response:**
```json
{
  "resumoSemanal": "Nos últimos 7 dias, você manteve uma média de humor de 3.2/5 e energia de 2.8/5. Sua produtividade está moderada com 3 sprints registradas.",
  "riscoBurnout": "medio",
  "sugestoes": [
    "Mantenha hábitos saudáveis de sono e alimentação",
    "Faça pausas regulares durante o trabalho",
    "Monitore seus níveis de humor e energia diariamente"
  ],
  "timestamp": "2025-11-11T15:30:00"
}
```

**Regras:**
- Busca dados históricos dos últimos 7 dias (humor, hábitos, sprints)
- Envia para GPT gerar análise interpretativa
- Retorna relatório estruturado como JSON
- Calcula risco de burnout automaticamente

## 🔧 Arquitetura

### Componentes Principais

1. **GPTService** (`com.nexus.ai.GPTService`)
   - Integração direta com API OpenAI via HTTP
   - Fallback para respostas padrão se API não disponível
   - Parsing de respostas JSON

2. **AIService** (`com.nexus.ai.AIService`)
   - Orquestra chamadas ao GPT
   - Coleta dados históricos do banco
   - Formata dados para envio ao GPT

3. **IAService** (`com.nexus.modules.ia.service.IAService`)
   - Lógica de negócio dos endpoints
   - Persistência de feedbacks no Oracle
   - Conversão de DTOs

4. **IAController** (`com.nexus.modules.ia.controller.IAController`)
   - Endpoints REST
   - Validação de entrada
   - Documentação Swagger

### Fluxo de Dados

```
Cliente → IAController → IAService → AIService → GPTService → OpenAI API
                                                      ↓
                                              Oracle Database
```

## 📝 Exemplo de Prompt Enviado ao GPT

### Para Feedback Empático:

```
Você é um assistente de saúde mental profissional. 
Gere uma mensagem curta, empática e profissional (máximo 150 caracteres) 
para um usuário com humor=2/5 e produtividade=baixa. 
Seja positivo, encorajador e ofereça uma sugestão prática. 
Responda APENAS com a mensagem, sem explicações adicionais.
```

### Para Análise Semanal:

```
Você é um analista de saúde mental e produtividade. 
Analise os seguintes dados históricos de um usuário e gere uma análise estruturada:

DADOS DOS ÚLTIMOS 7 DIAS:

HUMOR E ENERGIA:
- Média de humor: 2.5/5
- Média de energia: 2.8/5
- Total de registros: 5

HÁBITOS SAUDÁVEIS:
- Total de hábitos registrados: 3
- Pontuação total: 45

PRODUTIVIDADE (SPRINTS):
- Média de produtividade: 35.50
- Total de sprints: 2

Responda APENAS em formato JSON válido com as seguintes chaves:
- "resumo": resumo semanal em 2-3 frases
- "risco": nível de risco de burnout ("baixo", "medio" ou "alto")
- "sugestoes": array com 3 sugestões práticas e específicas
```

## 🗄️ Banco de Dados Oracle

### Tabela: `t_mt_alertas_ia`

```sql
CREATE TABLE t_mt_alertas_ia (
    id_alerta NUMBER PRIMARY KEY,
    id_usuario NUMBER NOT NULL,
    data_alerta DATE,
    tipo_alerta VARCHAR2(50) NOT NULL,
    mensagem VARCHAR2(255),
    nivel_risco NUMBER(1) CHECK (nivel_risco BETWEEN 1 AND 5),
    FOREIGN KEY (id_usuario) REFERENCES t_mt_usuarios(id_usuario)
);
```

O feedback gerado é automaticamente salvo nesta tabela com:
- `tipo_alerta`: "FEEDBACK_EMPATICO"
- `mensagem`: Mensagem gerada pelo GPT
- `nivel_risco`: Calculado baseado no humor (1-5)

## 🔐 Segurança

- Todos os endpoints requerem autenticação JWT
- Permissões: `PROFISSIONAL` ou `GESTOR`
- API Key do OpenAI armazenada em variável de ambiente (nunca commitada)

## 🧪 Testando no Swagger

1. Acesse: `http://localhost:8080/swagger-ui.html`
2. Faça login em `/api/auth/login` para obter o token
3. Clique em "Authorize" e cole o token (sem "Bearer")
4. Teste os endpoints:
   - `POST /ia/feedback`
   - `POST /ia/analise`

## 📱 Integração com App Mobile

### Exemplo de Chamada REST (Android/Kotlin):

```kotlin
// Gerar Feedback
val feedbackRequest = FeedbackRequest(
    usuarioId = 1,
    humor = 2,
    produtividade = "baixa"
)

val response = apiService.gerarFeedback(
    token = "seu-jwt-token",
    request = feedbackRequest
)

// Gerar Análise
val analiseRequest = AnaliseRequest(usuarioId = 1)
val analise = apiService.gerarAnalise(
    token = "seu-jwt-token",
    request = analiseRequest
)
```

### Exemplo de Chamada REST (React Native/JavaScript):

```javascript
// Gerar Feedback
const feedbackResponse = await fetch('http://localhost:8080/ia/feedback', {
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

// Gerar Análise
const analiseResponse = await fetch('http://localhost:8080/ia/analise', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  },
  body: JSON.stringify({
    usuarioId: 1
  })
});
```

## 🧠 Deep Learning - Visão Computacional (Esqueleto)

### Preparação para Análise de Ambiente de Trabalho

O módulo está preparado para integração futura com modelos de visão computacional (ex: MobileNet) para análise de foco e ambiente de trabalho.

#### Estrutura Preparada:

1. **Endpoint Opcional** (comentado em `IAController.java`):
```java
@PostMapping("/analise-ambiente")
@Operation(summary = "Analisar ambiente de trabalho via visão computacional")
public ResponseEntity<AnaliseAmbienteResponseDTO> analisarAmbiente(
    @RequestParam("foto") MultipartFile foto) {
    // TODO: Implementar com MobileNet ou outro modelo
    // 1. Enviar foto para serviço de visão computacional
    // 2. Analisar nível de foco, organização, iluminação
    // 3. Retornar sugestões baseadas na análise
}
```

2. **Serviço de Visão Computacional** (esqueleto em `VisionService.java`):
```java
@Service
public class VisionService {
    
    /**
     * Analisa foto do ambiente de trabalho usando modelo de deep learning
     * Modelo sugerido: MobileNet (leve para mobile) ou ResNet50
     */
    public AnaliseAmbiente analisarAmbienteTrabalho(MultipartFile foto) {
        // TODO: Implementar
        // 1. Pré-processar imagem (redimensionar, normalizar)
        // 2. Enviar para modelo TensorFlow Lite ou ONNX Runtime
        // 3. Classificar: foco (alto/médio/baixo), organização, iluminação
        // 4. Retornar análise estruturada
    }
}
```

#### Como Implementar (Futuro):

1. **Treinar Modelo**:
   - Dataset: Fotos de ambientes de trabalho classificadas
   - Modelo: MobileNetV2 (leve) ou EfficientNet (preciso)
   - Framework: TensorFlow Lite para mobile, ONNX Runtime para servidor

2. **Integração**:
   - Salvar modelo em `src/main/resources/models/`
   - Usar TensorFlow Java ou ONNX Runtime Java
   - Processar imagem antes de enviar ao modelo

3. **Exemplo de Prompt para GPT com Dados de Visão**:
```
Analise o ambiente de trabalho do usuário:
- Nível de foco detectado: médio
- Organização: boa
- Iluminação: adequada

Gere sugestões para melhorar a produtividade baseado nessa análise.
```

## 🐛 Troubleshooting

### API Key não configurada
- **Sintoma**: Retorna feedback padrão
- **Solução**: Configure `OPENAI_API_KEY` no `application.properties` ou variável de ambiente

### Erro 401 na API OpenAI
- **Sintoma**: Logs mostram "Erro na API OpenAI: Status 401"
- **Solução**: Verifique se a API key está correta e ativa

### Timeout na chamada
- **Sintoma**: Erro após 30 segundos
- **Solução**: Verifique conexão com internet e se a API OpenAI está acessível

## 📚 Dependências

- Spring Boot 3.3.6
- Jackson (para parsing JSON)
- Java HTTP Client (Java 11+)
- Oracle JDBC Driver

## 🔄 Compatibilidade

- ✅ Funciona sem Spring AI (usa HTTP direto)
- ✅ Fallback automático se API não disponível
- ✅ Mantém compatibilidade com endpoints antigos (`/api/alertas`)
- ✅ Funciona com ou sem chave de API configurada

## 📞 Suporte

Para dúvidas ou problemas:
1. Verifique os logs em `logs/application.log`
2. Teste os endpoints no Swagger
3. Verifique se a API key está configurada corretamente

---

**Desenvolvido para MindTrack/Nexus - Global Solution 2025**

