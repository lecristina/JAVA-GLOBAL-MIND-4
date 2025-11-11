# 🧪 Testes da API - MindTrack / Nexus

Este documento contém exemplos de testes para todos os endpoints da API.

**Base URL:** `http://localhost:8080`

---

## 📋 Índice

1. [Autenticação](#autenticação)
2. [Humor e Energia](#humor-e-energia)
3. [Hábitos Saudáveis](#hábitos-saudáveis)
4. [Badges](#badges)
5. [Sprints e Produtividade](#sprints-e-produtividade)
6. [Alertas IA](#alertas-ia)
7. [IA Generativa e Visão Computacional](#ia-generativa-e-visão-computacional)

---

## 🔐 Autenticação

### 1. Registrar Novo Usuário

**Endpoint:** `POST /api/auth/registro`

**Autenticação:** Não requerida

**Request Body:**
```json
{
  "nome": "João Silva",
  "email": "joao.silva@example.com",
  "senha": "senha123",
  "perfil": "PROFISSIONAL",
  "empresa": "TechCorp"
}
```

**Response 201 Created:**
```json
{
  "idUsuario": 1,
  "nome": "João Silva",
  "email": "joao.silva@example.com",
  "perfil": "PROFISSIONAL",
  "dataCadastro": "2024-11-11",
  "empresa": "TechCorp"
}
```

**cURL:**
```bash
curl -X POST http://localhost:8080/api/auth/registro \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "email": "joao.silva@example.com",
    "senha": "senha123",
    "perfil": "PROFISSIONAL",
    "empresa": "TechCorp"
  }'
```

---

### 2. Login e Obter Token JWT

**Endpoint:** `POST /api/auth/login`

**Autenticação:** Não requerida

**Request Body:**
```json
{
  "email": "joao.silva@example.com",
  "senha": "senha123"
}
```

**Response 200 OK:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tipo": "Bearer",
  "email": "joao.silva@example.com",
  "perfil": "PROFISSIONAL"
}
```

**cURL:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao.silva@example.com",
    "senha": "senha123"
  }'
```

**⚠️ IMPORTANTE:** Guarde o token retornado para usar nos próximos endpoints que requerem autenticação.

---

## 😊 Humor e Energia

**Base URL:** `/api/humor`

**Autenticação:** Requerida (Bearer Token)
**Roles:** PROFISSIONAL, GESTOR

### 1. Criar Registro de Humor

**Endpoint:** `POST /api/humor`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "idUsuario": 1,
  "dataRegistro": "2024-11-11",
  "nivelHumor": 4,
  "nivelEnergia": 3,
  "comentario": "Dia produtivo, mas um pouco cansado"
}
```

**Response 201 Created:**
```json
{
  "idHumor": 1,
  "idUsuario": 1,
  "dataRegistro": "2024-11-11",
  "nivelHumor": 4,
  "nivelEnergia": 3,
  "comentario": "Dia produtivo, mas um pouco cansado"
}
```

**cURL:**
```bash
curl -X POST http://localhost:8080/api/humor \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "idUsuario": 1,
    "dataRegistro": "2024-11-11",
    "nivelHumor": 4,
    "nivelEnergia": 3,
    "comentario": "Dia produtivo, mas um pouco cansado"
  }'
```

---

### 2. Listar Registros de Humor por Usuário (Paginado)

**Endpoint:** `GET /api/humor/usuario/{idUsuario}?page=0&size=10&sort=dataRegistro,desc`

**Headers:**
```
Authorization: Bearer {token}
```

**Parâmetros de Query:**
- `page`: Número da página (padrão: 0)
- `size`: Tamanho da página (padrão: 20)
- `sort`: Campo de ordenação (ex: `dataRegistro,desc`)

**Response 200 OK:**
```json
{
  "content": [
    {
      "idHumor": 1,
      "idUsuario": 1,
      "dataRegistro": "2024-11-11",
      "nivelHumor": 4,
      "nivelEnergia": 3,
      "comentario": "Dia produtivo, mas um pouco cansado"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 1,
  "totalPages": 1
}
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/api/humor/usuario/1?page=0&size=10&sort=dataRegistro,desc" \
  -H "Authorization: Bearer {token}"
```

---

### 3. Buscar Registro de Humor por ID

**Endpoint:** `GET /api/humor/{id}`

**Headers:**
```
Authorization: Bearer {token}
```

**Response 200 OK:**
```json
{
  "idHumor": 1,
  "idUsuario": 1,
  "dataRegistro": "2024-11-11",
  "nivelHumor": 4,
  "nivelEnergia": 3,
  "comentario": "Dia produtivo, mas um pouco cansado"
}
```

**cURL:**
```bash
curl -X GET http://localhost:8080/api/humor/1 \
  -H "Authorization: Bearer {token}"
```

---

### 4. Atualizar Registro de Humor

**Endpoint:** `PUT /api/humor/{id}`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "idUsuario": 1,
  "dataRegistro": "2024-11-11",
  "nivelHumor": 5,
  "nivelEnergia": 4,
  "comentario": "Atualizado: Dia excelente!"
}
```

**Response 200 OK:**
```json
{
  "idHumor": 1,
  "idUsuario": 1,
  "dataRegistro": "2024-11-11",
  "nivelHumor": 5,
  "nivelEnergia": 4,
  "comentario": "Atualizado: Dia excelente!"
}
```

**cURL:**
```bash
curl -X PUT http://localhost:8080/api/humor/1 \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "idUsuario": 1,
    "dataRegistro": "2024-11-11",
    "nivelHumor": 5,
    "nivelEnergia": 4,
    "comentario": "Atualizado: Dia excelente!"
  }'
```

---

### 5. Deletar Registro de Humor

**Endpoint:** `DELETE /api/humor/{id}`

**Headers:**
```
Authorization: Bearer {token}
```

**Response 204 No Content**

**cURL:**
```bash
curl -X DELETE http://localhost:8080/api/humor/1 \
  -H "Authorization: Bearer {token}"
```

---

## 🏃 Hábitos Saudáveis

**Base URL:** `/api/habitos`

**Autenticação:** Requerida (Bearer Token)
**Roles:** PROFISSIONAL, GESTOR

### 1. Criar Hábito

**Endpoint:** `POST /api/habitos`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "idUsuario": 1,
  "tipoHabito": "Hidratação",
  "dataHabito": "2024-11-11",
  "pontuacao": 10
}
```

**Response 201 Created:**
```json
{
  "idHabito": 1,
  "idUsuario": 1,
  "tipoHabito": "Hidratação",
  "dataHabito": "2024-11-11",
  "pontuacao": 10
}
```

**cURL:**
```bash
curl -X POST http://localhost:8080/api/habitos \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "idUsuario": 1,
    "tipoHabito": "Hidratação",
    "dataHabito": "2024-11-11",
    "pontuacao": 10
  }'
```

---

### 2. Listar Hábitos por Usuário (Paginado)

**Endpoint:** `GET /api/habitos/usuario/{idUsuario}?page=0&size=10&sort=dataHabito,desc`

**Headers:**
```
Authorization: Bearer {token}
```

**Response 200 OK:**
```json
{
  "content": [
    {
      "idHabito": 1,
      "idUsuario": 1,
      "tipoHabito": "Hidratação",
      "dataHabito": "2024-11-11",
      "pontuacao": 10
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 1,
  "totalPages": 1
}
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/api/habitos/usuario/1?page=0&size=10&sort=dataHabito,desc" \
  -H "Authorization: Bearer {token}"
```

---

### 3. Buscar Hábito por ID

**Endpoint:** `GET /api/habitos/{id}`

**Headers:**
```
Authorization: Bearer {token}
```

**Response 200 OK:**
```json
{
  "idHabito": 1,
  "idUsuario": 1,
  "tipoHabito": "Hidratação",
  "dataHabito": "2024-11-11",
  "pontuacao": 10
}
```

**cURL:**
```bash
curl -X GET http://localhost:8080/api/habitos/1 \
  -H "Authorization: Bearer {token}"
```

---

### 4. Atualizar Hábito

**Endpoint:** `PUT /api/habitos/{id}`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "idUsuario": 1,
  "tipoHabito": "Meditação",
  "dataHabito": "2024-11-11",
  "pontuacao": 15
}
```

**Response 200 OK:**
```json
{
  "idHabito": 1,
  "idUsuario": 1,
  "tipoHabito": "Meditação",
  "dataHabito": "2024-11-11",
  "pontuacao": 15
}
```

**cURL:**
```bash
curl -X PUT http://localhost:8080/api/habitos/1 \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "idUsuario": 1,
    "tipoHabito": "Meditação",
    "dataHabito": "2024-11-11",
    "pontuacao": 15
  }'
```

---

### 5. Deletar Hábito

**Endpoint:** `DELETE /api/habitos/{id}`

**Headers:**
```
Authorization: Bearer {token}
```

**Response 204 No Content**

**cURL:**
```bash
curl -X DELETE http://localhost:8080/api/habitos/1 \
  -H "Authorization: Bearer {token}"
```

---

### 6. Obter Pontuação Total do Usuário

**Endpoint:** `GET /api/habitos/usuario/{idUsuario}/pontuacao`

**Headers:**
```
Authorization: Bearer {token}
```

**Response 200 OK:**
```json
150
```

**cURL:**
```bash
curl -X GET http://localhost:8080/api/habitos/usuario/1/pontuacao \
  -H "Authorization: Bearer {token}"
```

---

## 🏆 Badges

**Base URL:** `/api/badges`

**Autenticação:** Requerida (Bearer Token)
**Roles:** 
- Criar/Atualizar/Deletar: GESTOR
- Listar/Buscar: PROFISSIONAL, GESTOR

### 1. Criar Badge

**Endpoint:** `POST /api/badges`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "nomeBadge": "Equilíbrio Mental",
  "descricao": "Conquistado ao manter humor estável por 7 dias",
  "pontosRequeridos": 100
}
```

**Response 201 Created:**
```json
{
  "idBadge": 1,
  "nomeBadge": "Equilíbrio Mental",
  "descricao": "Conquistado ao manter humor estável por 7 dias",
  "pontosRequeridos": 100
}
```

**cURL:**
```bash
curl -X POST http://localhost:8080/api/badges \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "nomeBadge": "Equilíbrio Mental",
    "descricao": "Conquistado ao manter humor estável por 7 dias",
    "pontosRequeridos": 100
  }'
```

---

### 2. Listar Todos os Badges

**Endpoint:** `GET /api/badges`

**Headers:**
```
Authorization: Bearer {token}
```

**Response 200 OK:**
```json
[
  {
    "idBadge": 1,
    "nomeBadge": "Equilíbrio Mental",
    "descricao": "Conquistado ao manter humor estável por 7 dias",
    "pontosRequeridos": 100
  },
  {
    "idBadge": 2,
    "nomeBadge": "Dev Zen",
    "descricao": "Manteve produtividade alta por 2 semanas",
    "pontosRequeridos": 200
  }
]
```

**cURL:**
```bash
curl -X GET http://localhost:8080/api/badges \
  -H "Authorization: Bearer {token}"
```

---

### 3. Buscar Badge por ID

**Endpoint:** `GET /api/badges/{id}`

**Headers:**
```
Authorization: Bearer {token}
```

**Response 200 OK:**
```json
{
  "idBadge": 1,
  "nomeBadge": "Equilíbrio Mental",
  "descricao": "Conquistado ao manter humor estável por 7 dias",
  "pontosRequeridos": 100
}
```

**cURL:**
```bash
curl -X GET http://localhost:8080/api/badges/1 \
  -H "Authorization: Bearer {token}"
```

---

### 4. Atualizar Badge

**Endpoint:** `PUT /api/badges/{id}`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "nomeBadge": "Equilíbrio Mental Plus",
  "descricao": "Conquistado ao manter humor estável por 14 dias",
  "pontosRequeridos": 150
}
```

**Response 200 OK:**
```json
{
  "idBadge": 1,
  "nomeBadge": "Equilíbrio Mental Plus",
  "descricao": "Conquistado ao manter humor estável por 14 dias",
  "pontosRequeridos": 150
}
```

**cURL:**
```bash
curl -X PUT http://localhost:8080/api/badges/1 \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "nomeBadge": "Equilíbrio Mental Plus",
    "descricao": "Conquistado ao manter humor estável por 14 dias",
    "pontosRequeridos": 150
  }'
```

---

### 5. Deletar Badge

**Endpoint:** `DELETE /api/badges/{id}`

**Headers:**
```
Authorization: Bearer {token}
```

**Response 204 No Content**

**cURL:**
```bash
curl -X DELETE http://localhost:8080/api/badges/1 \
  -H "Authorization: Bearer {token}"
```

---

## 🚀 Sprints e Produtividade

**Base URL:** `/api/sprints`

**Autenticação:** Requerida (Bearer Token)
**Roles:** PROFISSIONAL, GESTOR

### 1. Criar Sprint

**Endpoint:** `POST /api/sprints`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "idUsuario": 1,
  "nomeSprint": "Sprint 1 - Novembro",
  "dataInicio": "2024-11-01",
  "dataFim": "2024-11-15",
  "produtividade": 85.50,
  "tarefasConcluidas": 12,
  "commits": 45
}
```

**Response 201 Created:**
```json
{
  "idSprint": 1,
  "idUsuario": 1,
  "nomeSprint": "Sprint 1 - Novembro",
  "dataInicio": "2024-11-01",
  "dataFim": "2024-11-15",
  "produtividade": 85.50,
  "tarefasConcluidas": 12,
  "commits": 45
}
```

**cURL:**
```bash
curl -X POST http://localhost:8080/api/sprints \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "idUsuario": 1,
    "nomeSprint": "Sprint 1 - Novembro",
    "dataInicio": "2024-11-01",
    "dataFim": "2024-11-15",
    "produtividade": 85.50,
    "tarefasConcluidas": 12,
    "commits": 45
  }'
```

---

### 2. Listar Sprints por Usuário (Paginado)

**Endpoint:** `GET /api/sprints/usuario/{idUsuario}?page=0&size=10&sort=dataInicio,desc`

**Headers:**
```
Authorization: Bearer {token}
```

**Response 200 OK:**
```json
{
  "content": [
    {
      "idSprint": 1,
      "idUsuario": 1,
      "nomeSprint": "Sprint 1 - Novembro",
      "dataInicio": "2024-11-01",
      "dataFim": "2024-11-15",
      "produtividade": 85.50,
      "tarefasConcluidas": 12,
      "commits": 45
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 1,
  "totalPages": 1
}
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/api/sprints/usuario/1?page=0&size=10&sort=dataInicio,desc" \
  -H "Authorization: Bearer {token}"
```

---

### 3. Buscar Sprint por ID

**Endpoint:** `GET /api/sprints/{id}`

**Headers:**
```
Authorization: Bearer {token}
```

**Response 200 OK:**
```json
{
  "idSprint": 1,
  "idUsuario": 1,
  "nomeSprint": "Sprint 1 - Novembro",
  "dataInicio": "2024-11-01",
  "dataFim": "2024-11-15",
  "produtividade": 85.50,
  "tarefasConcluidas": 12,
  "commits": 45
}
```

**cURL:**
```bash
curl -X GET http://localhost:8080/api/sprints/1 \
  -H "Authorization: Bearer {token}"
```

---

### 4. Atualizar Sprint

**Endpoint:** `PUT /api/sprints/{id}`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "idUsuario": 1,
  "nomeSprint": "Sprint 1 - Novembro",
  "dataInicio": "2024-11-01",
  "dataFim": "2024-11-15",
  "produtividade": 90.25,
  "tarefasConcluidas": 15,
  "commits": 52
}
```

**Response 200 OK:**
```json
{
  "idSprint": 1,
  "idUsuario": 1,
  "nomeSprint": "Sprint 1 - Novembro",
  "dataInicio": "2024-11-01",
  "dataFim": "2024-11-15",
  "produtividade": 90.25,
  "tarefasConcluidas": 15,
  "commits": 52
}
```

**cURL:**
```bash
curl -X PUT http://localhost:8080/api/sprints/1 \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "idUsuario": 1,
    "nomeSprint": "Sprint 1 - Novembro",
    "dataInicio": "2024-11-01",
    "dataFim": "2024-11-15",
    "produtividade": 90.25,
    "tarefasConcluidas": 15,
    "commits": 52
  }'
```

---

### 5. Deletar Sprint

**Endpoint:** `DELETE /api/sprints/{id}`

**Headers:**
```
Authorization: Bearer {token}
```

**Response 204 No Content**

**cURL:**
```bash
curl -X DELETE http://localhost:8080/api/sprints/1 \
  -H "Authorization: Bearer {token}"
```

---

### 6. Obter Mensagem Motivacional

**Endpoint:** `GET /api/sprints/usuario/{idUsuario}/motivacao`

**Headers:**
```
Authorization: Bearer {token}
```

**Response 200 OK:**
```json
"Parabéns! Você está mantendo uma excelente produtividade. Continue assim!"
```

**cURL:**
```bash
curl -X GET http://localhost:8080/api/sprints/usuario/1/motivacao \
  -H "Authorization: Bearer {token}"
```

---

## 🤖 Alertas IA

**Base URL:** `/api/alertas`

**Autenticação:** Requerida (Bearer Token)
**Roles:** PROFISSIONAL, GESTOR

### 1. Listar Alertas por Usuário (Paginado)

**Endpoint:** `GET /api/alertas/usuario/{idUsuario}?page=0&size=10&sort=dataAlerta,desc`

**Headers:**
```
Authorization: Bearer {token}
```

**Response 200 OK:**
```json
{
  "content": [
    {
      "idAlerta": 1,
      "idUsuario": 1,
      "dataAlerta": "2024-11-11",
      "tipoAlerta": "RISCO_BURNOUT",
      "mensagem": "Níveis de estresse elevados detectados",
      "nivelRisco": 3
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 1,
  "totalPages": 1
}
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/api/alertas/usuario/1?page=0&size=10&sort=dataAlerta,desc" \
  -H "Authorization: Bearer {token}"
```

---

### 2. Buscar Alerta por ID

**Endpoint:** `GET /api/alertas/{id}`

**Headers:**
```
Authorization: Bearer {token}
```

**Response 200 OK:**
```json
{
  "idAlerta": 1,
  "idUsuario": 1,
  "dataAlerta": "2024-11-11",
  "tipoAlerta": "RISCO_BURNOUT",
  "mensagem": "Níveis de estresse elevados detectados",
  "nivelRisco": 3
}
```

**cURL:**
```bash
curl -X GET http://localhost:8080/api/alertas/1 \
  -H "Authorization: Bearer {token}"
```

---

### 3. Obter Mensagem Empática Gerada por IA

**Endpoint:** `GET /api/alertas/usuario/{idUsuario}/mensagem-empatica`

**Headers:**
```
Authorization: Bearer {token}
```

**Response 200 OK:**
```json
"Entendemos que você está passando por um momento desafiador. Lembre-se de cuidar de si mesmo e fazer pausas regulares. Você não está sozinho nisso."
```

**cURL:**
```bash
curl -X GET http://localhost:8080/api/alertas/usuario/1/mensagem-empatica \
  -H "Authorization: Bearer {token}"
```

---

### 4. Obter Análise de Risco de Burnout

**Endpoint:** `GET /api/alertas/usuario/{idUsuario}/analise-risco`

**Headers:**
```
Authorization: Bearer {token}
```

**Response 200 OK:**
```json
"Análise de risco: Nível médio de burnout detectado. Recomenda-se reduzir carga de trabalho e aumentar pausas. Considere atividades de relaxamento."
```

**cURL:**
```bash
curl -X GET http://localhost:8080/api/alertas/usuario/1/analise-risco \
  -H "Authorization: Bearer {token}"
```

---

## 🤖 IA Generativa e Visão Computacional

**Base URL:** `/ia`

**Autenticação:** Requerida (Bearer Token)
**Roles:** PROFISSIONAL, GESTOR

### 1. Gerar Feedback Empático usando GPT

**Endpoint:** `POST /ia/feedback`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "usuarioId": 1,
  "humor": 2,
  "produtividade": "baixa"
}
```

**Response 201 Created:**
```json
{
  "mensagem": "Você parece cansado hoje. Tente fazer uma pausa curta e respirar fundo. Estamos aqui para apoiá-lo.",
  "timestamp": "2024-11-11T15:30:00",
  "idAlerta": 123
}
```

**cURL:**
```bash
curl -X POST http://localhost:8080/ia/feedback \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "usuarioId": 1,
    "humor": 2,
    "produtividade": "baixa"
  }'
```

---

### 2. Gerar Análise Semanal Inteligente usando GPT

**Endpoint:** `POST /ia/analise`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "usuarioId": 1
}
```

**Response 200 OK:**
```json
{
  "resumoSemanal": "Analisando seus dados dos últimos 7 dias, você manteve uma média de humor de 3.2/5 e energia de 3.5/5. Sua produtividade está estável. Recomendamos manter hábitos saudáveis e fazer pausas regulares.",
  "riscoBurnout": "medio",
  "sugestoes": [
    "Mantenha hábitos saudáveis de sono e alimentação",
    "Faça pausas regulares durante o trabalho",
    "Monitore seus níveis de humor e energia diariamente"
  ],
  "timestamp": "2024-11-11T15:30:00"
}
```

**cURL:**
```bash
curl -X POST http://localhost:8080/ia/analise \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "usuarioId": 1
  }'
```

---

### 3. Assistente Pessoal - Conteúdo Personalizado

**Endpoint:** `POST /ia/assistente`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "usuarioId": 1,
  "tipoConsulta": "curiosidade"
}
```

**Tipos disponíveis:**
- `curiosidade` - Curiosidades educativas
- `prevencao` - Dicas de prevenção de burnout
- `motivacao` - Mensagens motivacionais
- `dica_pratica` - Dicas práticas acionáveis
- `reflexao` - Reflexões profundas

**Response 200 OK:**
```json
{
  "titulo": "Curiosidade: O Poder das Pausas",
  "conteudo": "Estudos mostram que fazer pausas de 5-10 minutos a cada 90 minutos de trabalho pode aumentar a produtividade em até 30%. O cérebro precisa de momentos de descanso para processar informações e manter o foco.",
  "tipo": "curiosidade",
  "acoesPraticas": [
    "Configure lembretes para pausas a cada 90 minutos",
    "Use a técnica Pomodoro (25min trabalho, 5min pausa)",
    "Durante as pausas, faça algo completamente diferente do trabalho"
  ],
  "reflexao": "Como você pode incorporar pausas regulares na sua rotina?",
  "timestamp": "2024-11-11T15:30:00"
}
```

**cURL:**
```bash
curl -X POST http://localhost:8080/ia/assistente \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "usuarioId": 1,
    "tipoConsulta": "curiosidade"
  }'
```

---

### 4. 👁️ Analisar Ambiente de Trabalho usando Visão Computacional (Deep Learning)

**Endpoint:** `POST /ia/analise-ambiente`

**⚠️ IMPORTANTE:** Este endpoint usa **multipart/form-data** para upload de imagem.

**Headers:**
```
Authorization: Bearer {token}
Content-Type: multipart/form-data
```

**Request (multipart/form-data):**
- `foto`: Arquivo de imagem (JPEG, PNG, etc)
- `usuarioId`: ID do usuário (integer)

**Response 200 OK:**
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
    "Mantenha o ambiente organizado para melhorar a produtividade",
    "Faça pausas regulares para descansar os olhos",
    "Considere adicionar plantas para melhorar o ambiente"
  ],
  "resumoAnalise": "Análise realizada com modelo de Deep Learning. Detectados 3 elementos no ambiente. Nível de foco: alto. Organização: boa. Iluminação: excelente.",
  "timestamp": "2024-11-11T15:30:00",
  "idAlerta": 124
}
```

#### Como Testar no Swagger UI:

1. Acesse: `http://localhost:8080/swagger-ui.html`
2. Faça login em `/api/auth/login` para obter o token
3. Clique em "Authorize" e cole o token (sem "Bearer")
4. Vá para o endpoint `POST /ia/analise-ambiente`
5. Clique em "Try it out"
6. Preencha:
   - `usuarioId`: 1
   - `foto`: Clique em "Choose File" e selecione uma imagem
7. Clique em "Execute"
8. Veja a resposta com a análise do ambiente

#### Como Testar com cURL:

```bash
curl -X POST http://localhost:8080/ia/analise-ambiente \
  -H "Authorization: Bearer {token}" \
  -F "foto=@/caminho/para/sua/imagem.jpg" \
  -F "usuarioId=1"
```

**Exemplo com imagem local:**
```bash
# Windows (PowerShell)
curl -X POST http://localhost:8080/ia/analise-ambiente `
  -H "Authorization: Bearer {token}" `
  -F "foto=@C:\Users\crist\Downloads\ambiente-trabalho.jpg" `
  -F "usuarioId=1"

# Linux/Mac
curl -X POST http://localhost:8080/ia/analise-ambiente \
  -H "Authorization: Bearer {token}" \
  -F "foto=@/home/usuario/ambiente-trabalho.jpg" \
  -F "usuarioId=1"
```

#### Como Testar com Postman:

1. **Método:** POST
2. **URL:** `http://localhost:8080/ia/analise-ambiente`
3. **Headers:**
   - `Authorization`: `Bearer {seu-token}`
4. **Body:**
   - Selecione `form-data`
   - Adicione campo `foto` do tipo `File` e selecione sua imagem
   - Adicione campo `usuarioId` do tipo `Text` com valor `1`
5. Clique em "Send"

#### Como Testar com JavaScript (Fetch):

```javascript
const formData = new FormData();
formData.append('foto', document.getElementById('fileInput').files[0]);
formData.append('usuarioId', 1);

fetch('http://localhost:8080/ia/analise-ambiente', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`
  },
  body: formData
})
.then(response => response.json())
.then(data => {
  console.log('Análise do ambiente:', data);
  console.log('Nível de foco:', data.nivelFoco);
  console.log('Organização:', data.organizacao);
  console.log('Objetos detectados:', data.objetosDetectados);
  console.log('Sugestões:', data.sugestoes);
})
.catch(error => console.error('Erro:', error));
```

#### Como Testar com Python (requests):

```python
import requests

url = "http://localhost:8080/ia/analise-ambiente"
token = "seu-token-aqui"

headers = {
    "Authorization": f"Bearer {token}"
}

files = {
    "foto": open("ambiente-trabalho.jpg", "rb")
}

data = {
    "usuarioId": 1
}

response = requests.post(url, headers=headers, files=files, data=data)
print(response.json())
```

#### Formatos de Imagem Suportados:

- JPEG (.jpg, .jpeg)
- PNG (.png)
- WebP (.webp)
- Tamanho recomendado: até 10MB

#### O que o Endpoint Faz:

1. **Recebe a imagem** do ambiente de trabalho
2. **Processa com Deep Learning** usando modelo Google ViT-Base via Hugging Face
3. **Detecta objetos** na imagem (mesa, computador, monitor, etc)
4. **Analisa características:**
   - Nível de foco (alto/médio/baixo)
   - Organização (excelente/boa/regular/ruim)
   - Iluminação (excelente/adequada/insuficiente)
5. **Gera sugestões** práticas para melhorar o ambiente
6. **Salva no banco** de dados Oracle na tabela `t_mt_alertas_ia` com tipo `ANALISE_AMBIENTE`

#### Exemplo de Resposta com Ambiente Organizado:

```json
{
  "nivelFoco": "alto",
  "organizacao": "excelente",
  "iluminacao": "excelente",
  "objetosDetectados": [
    "desk (98.5%)",
    "monitor (95.2%)",
    "keyboard (92.1%)",
    "window (88.7%)"
  ],
  "sugestoes": [
    "Mantenha o ambiente organizado para melhorar a produtividade",
    "Faça pausas regulares para descansar os olhos",
    "Considere adicionar plantas para melhorar o ambiente"
  ],
  "resumoAnalise": "Análise realizada com modelo de Deep Learning. Detectados 4 elementos no ambiente. Nível de foco: alto. Organização: excelente. Iluminação: excelente.",
  "timestamp": "2024-11-11T15:30:00",
  "idAlerta": 125
}
```

#### Exemplo de Resposta com Ambiente Desorganizado:

```json
{
  "nivelFoco": "baixo",
  "organizacao": "regular",
  "iluminacao": "adequada",
  "objetosDetectados": [
    "clutter (85.3%)",
    "desk (72.1%)",
    "papers (68.9%)"
  ],
  "sugestoes": [
    "Organize seu espaço de trabalho para melhorar o foco",
    "Considere remover distrações visuais do ambiente",
    "Mantenha o ambiente organizado para melhorar a produtividade"
  ],
  "resumoAnalise": "Análise realizada com modelo de Deep Learning. Detectados 3 elementos no ambiente. Nível de foco: baixo. Organização: regular. Iluminação: adequada.",
  "timestamp": "2024-11-11T15:30:00",
  "idAlerta": 126
}
```

#### Troubleshooting:

**Erro 503 (Service Unavailable):**
- O modelo Hugging Face pode estar carregando
- O sistema usa análise heurística como fallback
- Tente novamente em alguns segundos

**Erro 400 (Bad Request):**
- Verifique se o arquivo é uma imagem válida
- Verifique se o `usuarioId` é um número válido

**Erro 401 (Unauthorized):**
- Verifique se o token JWT está válido
- Faça login novamente para obter um novo token

---

## 📝 Notas Importantes

### Autenticação

Todos os endpoints (exceto `/api/auth/registro` e `/api/auth/login`) requerem autenticação via JWT Bearer Token.

**Como obter o token:**
1. Faça login em `/api/auth/login`
2. Copie o token da resposta
3. Use no header: `Authorization: Bearer {token}`

### Validações

- **Níveis de Humor/Energia/Risco:** Devem estar entre 1 e 5
- **Email:** Deve ser um email válido
- **Perfil:** Deve ser `PROFISSIONAL` ou `GESTOR`
- **Data:** Formato `YYYY-MM-DD`

### Códigos de Resposta

- `200 OK`: Requisição bem-sucedida
- `201 Created`: Recurso criado com sucesso
- `204 No Content`: Recurso deletado com sucesso
- `400 Bad Request`: Dados inválidos
- `401 Unauthorized`: Token inválido ou ausente
- `403 Forbidden`: Sem permissão para acessar o recurso
- `404 Not Found`: Recurso não encontrado
- `500 Internal Server Error`: Erro interno do servidor

### Paginação

Para endpoints paginados, use os parâmetros:
- `page`: Número da página (começa em 0)
- `size`: Tamanho da página (padrão: 20)
- `sort`: Campo de ordenação (ex: `dataRegistro,desc`)

---

## 🧪 Exemplo de Fluxo Completo

### 1. Registrar Usuário
```bash
curl -X POST http://localhost:8080/api/auth/registro \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Maria Santos",
    "email": "maria.santos@example.com",
    "senha": "senha123",
    "perfil": "PROFISSIONAL",
    "empresa": "TechCorp"
  }'
```

### 2. Fazer Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "maria.santos@example.com",
    "senha": "senha123"
  }'
```

### 3. Criar Registro de Humor (usando o token obtido)
```bash
curl -X POST http://localhost:8080/api/humor \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "idUsuario": 1,
    "dataRegistro": "2024-11-11",
    "nivelHumor": 4,
    "nivelEnergia": 3,
    "comentario": "Dia produtivo"
  }'
```

---

## 🔧 Ferramentas Recomendadas

- **Postman**: Para testar endpoints com interface gráfica
- **Insomnia**: Alternativa ao Postman
- **cURL**: Linha de comando (exemplos acima)
- **Swagger UI**: `http://localhost:8080/swagger-ui.html` (interface interativa)

---

**Última atualização:** 11/11/2024


