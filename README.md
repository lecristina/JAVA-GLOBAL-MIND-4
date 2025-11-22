# MindTrack / Nexus API

API completa para monitoramento de saúde mental e produtividade no trabalho de TI.
---
## Integrantes

- ANDRÉ ROGÉRIO VIEIRA PAVANELA ALTOBELLI ANTUNES RM: 554764
- LETICIA CRISTINA DOS SANTOS PASSOS RM: 555241
- ENRICO FIGUEIREDO DEL GUERRA RM: 558604

## 🚀 Tecnologias

- **Java 17**
- **Spring Boot 3.5.7**
- **Maven**
- **Oracle Database**
- **Spring Security + JWT**
- **Spring Data JPA**
- **MapStruct**
- **Spring AI (OpenAI)**
- **RabbitMQ**
- **Caffeine Cache**
- **SpringDoc/Swagger**
- **Internacionalização (i18n) PT-BR e EN-US**

## 📋 Funcionalidades

### ✅ Módulos Implementados

1. **Usuários**
   - Registro com hash de senha (BCrypt)
   - Login com JWT
   - Perfis: PROFISSIONAL, GESTOR

2. **Humor e Energia**
   - CRUD de registros diários
   - Paginação
   - Cache para listagem
   - Trigger automático de alerta de burnout

3. **Sprints e Produtividade**
   - Registro de produtividade por sprint
   - Cálculo automático de performance
   - Mensagens motivacionais via IA

4. **Hábitos Saudáveis**
   - CRUD com pontuação
   - Sistema automático de badges

5. **Badges (Gamificação)**
   - Sistema de conquistas
   - Atribuição automática baseada em pontuação

6. **Alertas IA**
   - Mensageria RabbitMQ
   - Análise de risco de burnout
   - Mensagens empáticas e motivacionais

## 🏗️ Arquitetura

```
src/main/java/com/nexus
  ├── config          # Configurações (Security, Cache, RabbitMQ, Swagger, i18n)
  ├── security        # JWT, Authentication, Authorization
  ├── domain/model    # Entidades JPA
  ├── infrastructure/repository  # Repositórios
  ├── application
  │   ├── dto         # DTOs
  │   └── mapper      # MapStruct Mappers
  ├── modules
  │   ├── usuarios    # Módulo de Usuários
  │   ├── humor       # Módulo de Humor
  │   ├── sprints     # Módulo de Sprints
  │   ├── habitos     # Módulo de Hábitos
  │   ├── badges      # Módulo de Badges
  │   └── alertas     # Módulo de Alertas
  ├── ai              # Serviço Spring AI
  ├── messaging        # RabbitMQ (Producer/Consumer/Events)
  └── shared/exception # GlobalExceptionHandler
```

## 🔧 Configuração

### Banco de Dados Oracle

As credenciais estão configuradas em `application.properties`:
- Host: br.com.fiap.oracle
- Port: 1521
- Service: ORCL
- User: rm555241
- Password: 230205

### Variáveis de Ambiente

Para produção, configure:
- `OPENAI_API_KEY`: Chave da API OpenAI
- `JWT_SECRET`: Chave secreta para JWT
- `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USERNAME`, `RABBITMQ_PASSWORD`

## 🚀 Como Executar

1. **Compilar o projeto:**
```bash
cd nexus
mvn clean install
```

2. **Executar a aplicação:**
```bash
mvn spring-boot:run
```

3. **Acessar Swagger:**
```
http://localhost:8080/swagger-ui.html
```

## 📝 Endpoints Principais

### Autenticação
- `POST /api/auth/registro` - Registrar novo usuário
- `POST /api/auth/login` - Login e obter JWT

### Humor
- `POST /api/humor` - Criar registro
- `GET /api/humor/usuario/{idUsuario}` - Listar (paginado)
- `GET /api/humor/{id}` - Buscar por ID
- `PUT /api/humor/{id}` - Atualizar
- `DELETE /api/humor/{id}` - Deletar

### Sprints
- `POST /api/sprints` - Criar sprint
- `GET /api/sprints/usuario/{idUsuario}` - Listar (paginado)
- `GET /api/sprints/usuario/{idUsuario}/motivacao` - Mensagem motivacional via IA

### Hábitos
- `POST /api/habitos` - Criar hábito
- `GET /api/habitos/usuario/{idUsuario}` - Listar (paginado)
- `GET /api/habitos/usuario/{idUsuario}/pontuacao` - Pontuação total

### Badges
- `GET /api/badges` - Listar todos
- `POST /api/badges` - Criar (apenas GESTOR)

### Alertas IA
- `GET /api/alertas/usuario/{idUsuario}` - Listar alertas (paginado)
- `GET /api/alertas/usuario/{idUsuario}/mensagem-empatica` - Mensagem empática via IA
- `GET /api/alertas/usuario/{idUsuario}/analise-risco` - Análise de risco via IA

## 🐳 Docker

```bash
docker build -t nexus-mindtrack .
docker run -p 8080:8080 nexus-mindtrack
```

## ☁️ Deploy Azure

O projeto inclui:
- `Dockerfile` para containerização
- `azure-pipelines.yml` para CI/CD

## 🧪 Testes

Testes unitários básicos incluídos:
- `UsuarioServiceTest`
- `HumorServiceTest`

Execute com:
```bash
mvn test
```

## 📚 Documentação

A documentação completa da API está disponível via Swagger em:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## 🌍 Internacionalização

A API suporta:
- **PT-BR** (padrão)
- **EN-US**

Configure via header `Accept-Language` ou use o padrão configurado.

## 🔐 Segurança

- Autenticação JWT obrigatória para endpoints protegidos
- Roles: `ROLE_PROFISSIONAL`, `ROLE_GESTOR`
- Senhas criptografadas com BCrypt

## 📦 Dependências Principais

- Spring Boot 3.5.7
- Oracle JDBC Driver
- JWT (io.jsonwebtoken)
- MapStruct 1.5.5
- Spring AI OpenAI
- SpringDoc OpenAPI
- Caffeine Cache
- RabbitMQ

---

**Desenvolvido com ❤️ seguindo Clean Architecture e SOLID**



