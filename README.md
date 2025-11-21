# MindTrack / Nexus API

API completa para monitoramento de saúde mental e produtividade no trabalho de TI.

## 🚀 Tecnologias

- **Java 17**
- **Spring Boot 3.3.6**
- **Maven**
- **Oracle Database**
- **Spring Security + JWT**
- **Spring Data JPA**
- **MapStruct**
- **OpenAI API (Theokanning SDK)**
- **RabbitMQ**
- **Caffeine Cache**
- **SpringDoc/Swagger**
- **Internacionalização (i18n) PT-BR e EN-US**

---

## 🌐 API REST - Pronta para Consumo

Esta é uma **API REST pura** desenvolvida com Spring Boot, projetada para ser consumida por aplicações frontend (web ou mobile) ou qualquer cliente HTTP.

### ✅ API Pronta para Consumo

A API está **100% funcional e pronta para integração** com:
- **Aplicações Web** (React, Vue, Angular, etc.)
- **Aplicações Mobile** (React Native, Flutter, iOS, Android)
- **Outros serviços** (microserviços, sistemas legados)
- **Ferramentas de integração** (Postman, Insomnia, cURL)

### 📖 Swagger UI - Interface Web para Testes

O projeto inclui **Swagger UI** como interface web interativa para testar e explorar todos os endpoints da API:

- **URL do Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **Documentação OpenAPI (JSON):** `http://localhost:8080/v3/api-docs`

#### Como usar o Swagger UI:

1. **Inicie a aplicação** (Spring Boot)
2. **Acesse:** `http://localhost:8080/swagger-ui.html`
3. **Faça login** usando o endpoint `/api/auth/login` para obter o token JWT
4. **Clique em "Authorize"** no topo da página e cole o token
5. **Explore e teste** todos os endpoints diretamente no navegador

O Swagger UI permite:
- ✅ Visualizar todos os endpoints disponíveis
- ✅ Ver documentação completa de cada endpoint
- ✅ Testar requisições diretamente no navegador
- ✅ Ver exemplos de request/response
- ✅ Validar schemas de DTOs
- ✅ Testar autenticação JWT

**Nota:** Esta API não utiliza Thymeleaf ou templates server-side, pois é uma API REST pura. O Swagger UI serve como interface web para testes e documentação interativa.

---

## 📚 Conceitos de Java Utilizados no Projeto

### 1. **Programação Orientada a Objetos (OOP)**

#### Classes e Objetos
- **Entidades (Domain Models)**: Classes que representam tabelas do banco de dados
  ```java
  @Entity
  @Table(name = "t_mt_usuarios")
  public class Usuario {
      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Integer idUsuario;
      private String nome;
      private String email;
  }
  ```

#### Encapsulamento
- Uso de modificadores de acesso (`private`, `public`, `protected`)
- Getters e Setters gerados automaticamente pelo Lombok (`@Data`, `@Getter`, `@Setter`)

#### Herança
- Classes que estendem funcionalidades do Spring Framework
- Implementação de interfaces (`UserDetails`, `UserDetailsService`)

#### Polimorfismo
- Uso de interfaces e implementações múltiplas
- Injeção de dependências via interfaces

### 2. **Anotações Java e Spring**

#### Anotações de Classe
- `@Service`: Marca classes de serviço (lógica de negócio)
- `@Repository`: Marca classes de acesso a dados
- `@RestController`: Marca controllers REST
- `@Entity`: Marca entidades JPA
- `@Component`: Componente genérico do Spring

#### Anotações de Método
- `@Transactional`: Gerencia transações de banco de dados
- `@Cacheable`: Cache de resultados
- `@CacheEvict`: Invalidação de cache
- `@PreAuthorize`: Controle de acesso baseado em roles
- `@PostMapping`, `@GetMapping`, `@PutMapping`, `@DeleteMapping`: Mapeamento de endpoints HTTP

#### Anotações de Campo
- `@Id`: Chave primária
- `@GeneratedValue`: Geração automática de IDs
- `@Column`: Mapeamento de colunas
- `@ManyToOne`, `@OneToMany`: Relacionamentos JPA
- `@Autowired`: Injeção de dependências
- `@Value`: Injeção de valores de propriedades

### 3. **Padrões de Projeto (Design Patterns)**

#### Builder Pattern
- Implementado via Lombok `@Builder`
- Facilita criação de objetos complexos
  ```java
  UsuarioDTO usuario = UsuarioDTO.builder()
      .nome("João")
      .email("joao@example.com")
      .perfil(PerfilUsuario.PROFISSIONAL)
      .build();
  ```

#### Repository Pattern
- Abstração de acesso a dados
- Interfaces que estendem `JpaRepository<T, ID>`
  ```java
  public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
      Optional<Usuario> findByEmail(String email);
      boolean existsByEmail(String email);
  }
  ```

#### Service Layer Pattern
- Separação de lógica de negócio da camada de apresentação
- Services contêm a lógica de negócio
- Controllers apenas recebem requisições e retornam respostas

#### DTO Pattern (Data Transfer Object)
- Objetos para transferência de dados entre camadas
- Evita expor entidades JPA diretamente
- Usa MapStruct para conversão automática

#### Dependency Injection
- Injeção de dependências via construtores ou `@Autowired`
- Facilita testes e desacoplamento
  ```java
  @Service
  @RequiredArgsConstructor  // Lombok gera construtor automaticamente
  public class UsuarioService {
      private final UsuarioRepository usuarioRepository;
      private final PasswordEncoder passwordEncoder;
  }
  ```

### 4. **Lombok - Redução de Boilerplate**

- `@Data`: Gera getters, setters, toString, equals, hashCode
- `@Builder`: Implementa Builder Pattern
- `@NoArgsConstructor`: Construtor sem argumentos
- `@AllArgsConstructor`: Construtor com todos os argumentos
- `@RequiredArgsConstructor`: Construtor com campos `final`
- `@Slf4j`: Gera logger `log` automaticamente
- `@Getter` / `@Setter`: Getters/Setters individuais

### 5. **Streams API e Lambda Expressions**

- Processamento de coleções de forma funcional
  ```java
  List<BadgeDTO> badges = badgeRepository.findAll().stream()
      .map(badgeMapper::toDTO)
      .collect(Collectors.toList());
  ```

### 6. **Optional**

- Tratamento seguro de valores nulos
  ```java
  Optional<Usuario> usuario = usuarioRepository.findById(id);
  usuario.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
  ```

### 7. **Generics**

- Tipos genéricos para reutilização de código
  ```java
  public interface JpaRepository<T, ID> {
      Optional<T> findById(ID id);
      List<T> findAll();
  }
  ```

### 8. **Reflection**

- Acesso a campos e métodos em tempo de execução
- Usado para obter `GPTService` via `AIService` quando necessário

### 9. **Exception Handling**

- Tratamento centralizado de exceções via `@ControllerAdvice`
- Exceções customizadas para casos específicos

### 10. **Concorrência e Assíncrono**

- RabbitMQ para processamento assíncrono de mensagens
- Cache para melhorar performance

---

## 🏗️ Arquitetura e Como Tudo Funciona

### Estrutura de Camadas (Clean Architecture)

```
src/main/java/com/nexus
  ├── config/              # Configurações do Spring
  │   ├── SecurityConfig      # Configuração de segurança e JWT
  │   ├── CacheConfig         # Configuração de cache (Caffeine)
  │   ├── RabbitMQConfig      # Configuração de mensageria
  │   ├── SwaggerConfig       # Documentação da API
  │   └── MessageSourceConfig # Internacionalização
  │
  ├── security/            # Autenticação e Autorização
  │   ├── JwtService              # Geração e validação de tokens JWT
  │   ├── JwtAuthenticationFilter # Filtro para validar JWT nas requisições
  │   └── CustomUserDetailsService # Carrega usuários do banco
  │
  ├── domain/model/        # Entidades JPA (Camada de Domínio)
  │   ├── Usuario.java
  │   ├── Humor.java
  │   ├── Sprint.java
  │   ├── Habito.java
  │   ├── Badge.java
  │   └── AlertaIA.java
  │
  ├── infrastructure/repository/  # Repositórios (Acesso a Dados)
  │   ├── UsuarioRepository.java
  │   ├── HumorRepository.java
  │   └── ...
  │
  ├── application/         # Camada de Aplicação
  │   ├── dto/             # Data Transfer Objects
  │   │   ├── UsuarioDTO.java
  │   │   ├── HumorDTO.java
  │   │   └── ...
  │   └── mapper/           # MapStruct Mappers (Conversão DTO <-> Entity)
  │       ├── UserMapper.java
  │       └── ...
  │
  ├── modules/             # Módulos de Negócio
  │   ├── usuarios/
  │   │   ├── controller/UsuarioController.java
  │   │   └── service/UsuarioService.java
  │   ├── humor/
  │   ├── sprints/
  │   ├── habitos/
  │   ├── badges/
  │   ├── alertas/
  │   └── ia/
  │       ├── controller/IAController.java
  │       └── service/IAService.java
  │
  ├── ai/                   # Serviços de IA
  │   ├── AIService.java         # Orquestrador de serviços de IA
  │   ├── GPTService.java        # Integração com OpenAI
  │   ├── GeminiService.java     # Integração com Gemini (futuro)
  │   ├── VisionService.java     # Análise de imagens
  │   └── HistoricoIAService.java # Histórico de conversas
  │
  ├── messaging/            # Mensageria RabbitMQ
  │   ├── producer/AlertProducer.java    # Envia mensagens
  │   ├── consumer/AlertConsumer.java    # Recebe mensagens
  │   └── events/BurnoutAlertEvent.java  # Eventos
  │
  └── shared/exception/     # Tratamento Global de Exceções
      └── GlobalExceptionHandler.java
```

### Fluxo de uma Requisição

1. **Cliente faz requisição HTTP** → `POST /api/humor`
2. **JwtAuthenticationFilter** → Valida token JWT no header `Authorization: Bearer <token>`
3. **SecurityConfig** → Verifica se usuário tem permissão (`@PreAuthorize`)
4. **Controller** → Recebe requisição, valida DTO (`@Valid`)
5. **Service** → Executa lógica de negócio:
   - Busca usuário no banco
   - Valida regras de negócio
   - Cria/atualiza entidade
   - Envia mensagem RabbitMQ se necessário
6. **Repository** → Persiste no banco de dados
7. **Mapper** → Converte Entity → DTO
8. **Controller** → Retorna resposta JSON

### Fluxo de Dados

```
HTTP Request (JSON)
    ↓
DTO (Data Transfer Object)
    ↓
Mapper (MapStruct) - Conversão automática
    ↓
Entity (JPA)
    ↓
Repository (Spring Data JPA)
    ↓
Database (Oracle)
```

### Integração com OpenAI

1. **Requisição chega no Controller** (`/ia/assistente`)
2. **IAService** processa e chama `GPTService`
3. **GPTService** usa SDK Theokanning ou HttpClient:
   - Tenta SDK primeiro (mais eficiente)
   - Fallback para HttpClient se SDK falhar
4. **OpenAI API** processa e retorna resposta
5. **Resposta é parseada** e retornada como DTO

### Sistema de Cache

- **Caffeine Cache**: Cache em memória para melhorar performance
- `@Cacheable`: Armazena resultado da primeira chamada
- `@CacheEvict`: Invalida cache quando dados são modificados
- Exemplo: Lista de badges é cacheada após primeira busca

### Mensageria RabbitMQ

- **Producer**: Envia mensagens quando evento ocorre (ex: burnout detectado)
- **Consumer**: Processa mensagens assincronamente
- **Eventos**: `BurnoutAlertEvent` - disparado quando humor/energia estão baixos

---

## 📝 Endpoints Completos da API

### 🔐 Autenticação

#### POST `/api/auth/registro`
Registra novo usuário no sistema.

**Request:**
```json
{
  "nome": "João Silva",
  "email": "joao@example.com",
  "senha": "senha123",
  "perfil": "PROFISSIONAL"
}
```

**Response:**
```json
{
  "idUsuario": 1,
  "nome": "João Silva",
  "email": "joao@example.com",
  "perfil": "PROFISSIONAL",
  "dataCadastro": "2025-01-15"
}
```

#### POST `/api/auth/login`
Autentica usuário e retorna token JWT.

**Request:**
```json
{
  "email": "joao@example.com",
  "senha": "senha123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tipoToken": "Bearer",
  "usuario": {
    "idUsuario": 1,
    "nome": "João Silva",
    "email": "joao@example.com"
  }
}
```

**Uso do Token:**
Adicione no header de todas as requisições protegidas:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

### 😊 Humor e Energia

#### POST `/api/humor`
Cria registro de humor e energia do dia.

**Request:**
```json
{
  "idUsuario": 1,
  "nivelHumor": 4,
  "nivelEnergia": 3,
  "dataRegistro": "2025-01-15"
}
```

**Response:**
```json
{
  "idHumor": 1,
  "idUsuario": 1,
  "nivelHumor": 4,
  "nivelEnergia": 3,
  "dataRegistro": "2025-01-15"
}
```

**Comportamento:**
- Se `nivelHumor <= 2` E `nivelEnergia <= 2` → Dispara alerta de burnout via RabbitMQ

#### GET `/api/humor/usuario/{idUsuario}?page=0&size=10`
Lista registros de humor paginados.

**Response:**
```json
{
  "content": [
    {
      "idHumor": 1,
      "nivelHumor": 4,
      "nivelEnergia": 3,
      "dataRegistro": "2025-01-15"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "size": 10,
  "number": 0
}
```

#### GET `/api/humor/{id}`
Busca registro específico por ID.

#### PUT `/api/humor/{id}`
Atualiza registro existente.

#### DELETE `/api/humor/{id}`
Remove registro.

---

### 🏃 Sprints e Produtividade

#### POST `/api/sprints`
Cria registro de sprint.

**Request:**
```json
{
  "idUsuario": 1,
  "nomeSprint": "Sprint 1",
  "tarefasCompletas": 8,
  "tarefasTotais": 10,
  "dataInicio": "2025-01-15",
  "dataFim": "2025-01-22"
}
```

**Response:**
```json
{
  "idSprint": 1,
  "idUsuario": 1,
  "nomeSprint": "Sprint 1",
  "tarefasCompletas": 8,
  "tarefasTotais": 10,
  "performance": 80.0,
  "dataInicio": "2025-01-15",
  "dataFim": "2025-01-22"
}
```

#### GET `/api/sprints/usuario/{idUsuario}?page=0&size=10`
Lista sprints do usuário (paginado).

#### GET `/api/sprints/usuario/{idUsuario}/motivacao`
Gera mensagem motivacional personalizada via IA.

**Response:**
```json
{
  "mensagem": "Parabéns! Você completou 80% das tarefas da Sprint 1. Continue assim!",
  "timestamp": "2025-01-15T10:30:00"
}
```

---

### 🎯 Hábitos Saudáveis

#### POST `/api/habitos`
Cria novo hábito.

**Request:**
```json
{
  "idUsuario": 1,
  "nomeHabito": "Exercitar-se",
  "descricao": "30 minutos de exercício diário",
  "pontuacao": 10
}
```

**Response:**
```json
{
  "idHabito": 1,
  "idUsuario": 1,
  "nomeHabito": "Exercitar-se",
  "descricao": "30 minutos de exercício diário",
  "pontuacao": 10,
  "dataCriacao": "2025-01-15"
}
```

#### GET `/api/habitos/usuario/{idUsuario}?page=0&size=10`
Lista hábitos do usuário (paginado).

#### GET `/api/habitos/usuario/{idUsuario}/pontuacao`
Retorna pontuação total do usuário.

**Response:**
```json
{
  "pontuacaoTotal": 150,
  "totalHabitos": 5
}
```

**Comportamento:**
- Sistema verifica automaticamente se usuário ganhou badges baseado na pontuação

---

### 🏆 Badges (Gamificação)

#### GET `/api/badges`
Lista todos os badges disponíveis (com cache).

**Response:**
```json
[
  {
    "idBadge": 1,
    "nomeBadge": "Iniciante",
    "descricao": "Primeiros passos",
    "pontosRequeridos": 10
  },
  {
    "idBadge": 2,
    "nomeBadge": "Veterano",
    "descricao": "100 pontos conquistados",
    "pontosRequeridos": 100
  }
]
```

#### POST `/api/badges`
Cria novo badge (apenas GESTOR).

**Request:**
```json
{
  "nomeBadge": "Mestre",
  "descricao": "500 pontos conquistados",
  "pontosRequeridos": 500
}
```

---

### 🤖 IA Generativa

#### POST `/ia/feedback`
Gera feedback empático baseado no humor e produtividade do usuário.

**Request:**
```json
{
  "usuarioId": 1
}
```

**Response:**
```json
{
  "mensagem": "Vejo que você está passando por um momento difícil...",
  "tipoAlerta": "EMPATICO",
  "timestamp": "2025-01-15T10:30:00"
}
```

#### POST `/ia/analise`
Gera análise semanal completa (últimos 7 dias).

**Request:**
```json
{
  "usuarioId": 1
}
```

**Response:**
```json
{
  "resumoSemanal": "Esta semana você manteve um bom equilíbrio...",
  "riscoBurnout": "BAIXO",
  "sugestoes": [
    "Continue mantendo pausas regulares",
    "Pratique exercícios físicos"
  ],
  "timestamp": "2025-01-15T10:30:00"
}
```

#### POST `/ia/assistente`
Assistente pessoal de saúde mental com múltiplos tipos de conteúdo.

**Request (Tipo Consulta):**
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

**Request (Agenda):**
```json
{
  "usuarioId": 1,
  "tipo": "agenda",
  "mensagem": "tenho cabeleireiro hoje às 14h, depilação na quarta-feira e viagem no final do ano"
}
```

**Response (Agenda):**
```json
{
  "tasks": [
    {
      "titulo": "Cabeleireiro",
      "data": "2025-01-15T14:00:00",
      "categoria": "Beleza",
      "prioridade": "Normal"
    },
    {
      "titulo": "Depilação",
      "data": "2025-01-17T10:00:00",
      "categoria": "Beleza",
      "prioridade": "Normal"
    },
    {
      "titulo": "Viagem de fim de ano",
      "data": "2025-12-28T08:00:00",
      "categoria": "Pessoal",
      "prioridade": "Alta"
    }
  ]
}
```

**Response (Tipo Consulta):**
```json
{
  "titulo": "Curiosidade: O Poder das Pausas",
  "conteudo": "Estudos mostram que fazer pausas de 5-10 minutos...",
  "tipo": "curiosidade",
  "acoesPraticas": [
    "Configure lembretes para pausas a cada 90 minutos",
    "Use a técnica Pomodoro"
  ],
  "reflexao": "Como você pode incorporar pausas regulares na sua rotina?",
  "timestamp": "2025-01-15T10:30:00"
}
```

#### POST `/ia/chat`
Chat conversacional com IA - mantém contexto da conversa.

**Request (Primeira Mensagem):**
```json
{
  "usuarioId": 1,
  "mensagem": "Estou me sentindo muito estressado no trabalho"
}
```

**Request (Continuar Conversa):**
```json
{
  "usuarioId": 1,
  "idConversaPai": 123,
  "mensagem": "Como posso melhorar isso?"
}
```

**Response:**
```json
{
  "idConversa": 124,
  "idConversaPai": 123,
  "mensagemUsuario": "Como posso melhorar isso?",
  "respostaIA": "Aqui estão algumas estratégias...",
  "timestamp": "2025-01-15T10:30:00"
}
```

#### POST `/ia/co-planner`
Extrai tarefas estruturadas de mensagens em linguagem natural.

**Request:**
```json
{
  "usuarioId": 1,
  "mensagem": "hoje preciso levar minha gata ao veterinário as 14 e preciso terminar a materia de java para o challenge"
}
```

**Response:**
```json
{
  "tarefas": [
    {
      "horario": "14:00",
      "descricao": "Levar gata ao veterinário",
      "prioridade": "ALTA"
    },
    {
      "horario": null,
      "descricao": "Terminar matéria de Java para o challenge",
      "prioridade": "ALTA"
    }
  ],
  "totalTarefas": 2,
  "mensagemOriginal": "hoje preciso levar minha gata ao veterinário...",
  "timestamp": "2025-01-15T10:30:00"
}
```

#### POST `/ia/assistant/analisar`
Processa mensagens e retorna JSON estruturado conforme tipo.

**Request:**
```json
{
  "usuarioId": 1,
  "tipo": "agenda",
  "mensagem": "tenho cabeleireiro hoje às 14h"
}
```

**Response:**
```json
{
  "tasks": [
    {
      "titulo": "Cabeleireiro",
      "data": "2025-01-15T14:00:00",
      "categoria": "Beleza",
      "prioridade": "Normal"
    }
  ]
}
```

#### POST `/ia/pausa-monitor` 🆕
Monitora presença/ausência do usuário através de análise de movimento em frames de vídeo.

**Como funciona:**
- Detecta movimento comparando frames consecutivos
- Não identifica pessoa, apenas variação de pixels
- Se não houver movimento por 5 minutos → ausência detectada
- Se detectar muito tempo sentado (1h+) → sugere alongamentos
- Pausas são registradas automaticamente quando usuário retorna

**Request:**
```json
{
  "usuarioId": 1,
  "frameBase64": "iVBORw0KGgoAAAANS...",
  "resetarSessao": false
}
```

**Response:**
```json
{
  "usuarioId": 1,
  "movimentoDetectado": true,
  "quantidadeMovimento": 25000,
  "presente": true,
  "tempoSentadoMinutos": 75,
  "totalPausas": 3,
  "sugerirAlongamento": true,
  "mensagem": "Movimento detectado. Usuário presente.",
  "sugestoes": [
    "💡 Você está sentado há 75 minutos. Hora de se alongar!",
    "🏃 Faça uma pausa de 5 minutos: levante-se, caminhe e alongue braços e pernas",
    "👀 Descanse os olhos: olhe para longe por 20 segundos a cada 20 minutos"
  ],
  "timestamp": "2025-01-15T10:30:00"
}
```

**Tecnologias:**
- Processamento de imagem nativo Java (BufferedImage)
- Detecção de movimento por diferença de pixels
- Blur gaussiano para reduzir ruído
- Sem dependências externas pesadas (OpenCV não necessário)

**Uso recomendado:**
- Envie frames a cada 5-10 segundos durante o trabalho
- Use webcam comum ou câmera de notebook
- O sistema mantém sessão ativa por usuário
- Para resetar sessão, envie `resetarSessao: true`

**📖 Guia Completo de Testes:** Veja `COMO_TESTAR_PAUSA_MONITOR.md` para exemplos detalhados com cURL, Postman, JavaScript e Python.

**Exemplo de uso com JavaScript:**
```javascript
// Capturar frame da webcam e enviar
const video = document.getElementById('webcam');
const canvas = document.createElement('canvas');
const ctx = canvas.getContext('2d');

setInterval(async () => {
  canvas.width = video.videoWidth;
  canvas.height = video.videoHeight;
  ctx.drawImage(video, 0, 0);
  const frameBase64 = canvas.toDataURL('image/jpeg').split(',')[1];
  
  const response = await fetch('http://localhost:8080/ia/pausa-monitor', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({
      usuarioId: 1,
      frameBase64: frameBase64
    })
  });
  
  const resultado = await response.json();
  console.log('Movimento:', resultado.movimentoDetectado);
  console.log('Tempo sentado:', resultado.tempoSentadoMinutos, 'minutos');
  
  if (resultado.sugerirAlongamento) {
    alert(resultado.mensagem);
    resultado.sugestoes.forEach(sugestao => console.log(sugestao));
  }
}, 10000); // A cada 10 segundos
```

---

### 🚨 Alertas IA

#### GET `/api/alertas/usuario/{idUsuario}?page=0&size=10`
Lista alertas do usuário (paginado).

**Response:**
```json
{
  "content": [
    {
      "idAlerta": 1,
      "idUsuario": 1,
      "tipoAlerta": "BURNOUT",
      "mensagem": "Alerta: Seus níveis de humor e energia estão baixos...",
      "dataAlerta": "2025-01-15T10:30:00"
    }
  ],
  "totalElements": 1
}
```

#### GET `/api/alertas/usuario/{idUsuario}/mensagem-empatica`
Gera mensagem empática personalizada via IA.

#### GET `/api/alertas/usuario/{idUsuario}/analise-risco`
Gera análise de risco de burnout via IA.

---

## 🧪 Testes da API

### Estrutura de Testes

O projeto utiliza **JUnit 5** e **Mockito** para testes unitários.

**Localização:** `src/test/java/com/nexus/`

### Testes Implementados

#### 1. `UsuarioServiceTest`

**Localização:** `src/test/java/com/nexus/modules/usuarios/service/UsuarioServiceTest.java`

**O que testa:**
- Registro de novo usuário
- Validação de email duplicado
- Criptografia de senha com BCrypt

**Código do Teste:**
```java
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void testRegistrarUsuario() {
        // Arrange
        UsuarioDTO usuarioDTO = UsuarioDTO.builder()
                .nome("Teste Usuario")
                .email("teste@example.com")
                .senha("senha123")
                .perfil(PerfilUsuario.PROFISSIONAL)
                .build();

        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        UsuarioDTO result = usuarioService.registrar(usuarioDTO);

        // Assert
        assertNotNull(result);
        assertEquals(usuarioDTO.getEmail(), result.getEmail());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }
}
```

**Como executar:**
```bash
mvn test -Dtest=UsuarioServiceTest
```

#### 2. `HumorServiceTest`

**Localização:** `src/test/java/com/nexus/modules/humor/service/HumorServiceTest.java`

**O que testa:**
- Criação de registro de humor
- Disparo automático de alerta de burnout quando níveis estão baixos

**Código do Teste:**
```java
@ExtendWith(MockitoExtension.class)
class HumorServiceTest {

    @Mock
    private HumorRepository humorRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AlertProducer alertProducer;

    @InjectMocks
    private HumorService humorService;

    @Test
    void testCriarHumor() {
        // Arrange
        HumorDTO humorDTO = HumorDTO.builder()
                .idUsuario(1)
                .nivelHumor(3)
                .nivelEnergia(4)
                .dataRegistro(LocalDate.now())
                .build();

        when(usuarioRepository.findById(anyInt())).thenReturn(Optional.of(usuario));
        when(humorRepository.save(any(Humor.class))).thenReturn(humor);

        // Act
        HumorDTO result = humorService.criar(humorDTO);

        // Assert
        assertNotNull(result);
        verify(humorRepository, times(1)).save(any(Humor.class));
    }

    @Test
    void testCriarHumorComAlertaBurnout() {
        // Arrange - Níveis baixos (humor <= 2 e energia <= 2)
        humorDTO.setNivelHumor(1);
        humorDTO.setNivelEnergia(2);

        when(usuarioRepository.findById(anyInt())).thenReturn(Optional.of(usuario));
        when(humorRepository.save(any(Humor.class))).thenReturn(humor);

        // Act
        HumorDTO result = humorService.criar(humorDTO);

        // Assert - Verifica se alerta foi disparado
        assertNotNull(result);
        verify(alertProducer, times(1)).sendBurnoutAlert(any());
    }
}
```

**Como executar:**
```bash
mvn test -Dtest=HumorServiceTest
```

### Executando Todos os Testes

```bash
# Executa todos os testes
mvn test

# Executa testes com relatório de cobertura
mvn test jacoco:report

# Executa apenas testes de um pacote específico
mvn test -Dtest=com.nexus.modules.usuarios.*
```

### Conceitos de Teste Utilizados

#### 1. **Mockito**
- `@Mock`: Cria mocks de dependências
- `@InjectMocks`: Injeta mocks no objeto sendo testado
- `when().thenReturn()`: Define comportamento de mocks
- `verify()`: Verifica se métodos foram chamados

#### 2. **JUnit 5**
- `@Test`: Marca método como teste
- `@BeforeEach`: Executa antes de cada teste
- `@ExtendWith(MockitoExtension.class)`: Habilita Mockito

#### 3. **Assertions**
- `assertNotNull()`: Verifica se não é null
- `assertEquals()`: Verifica igualdade
- `verify()`: Verifica chamadas de métodos

### Exemplos de Testes com cURL

#### Teste de Registro de Usuário

```bash
curl -X POST http://localhost:8080/api/auth/registro \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "email": "joao@example.com",
    "senha": "senha123",
    "perfil": "PROFISSIONAL"
  }'
```

#### Teste de Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao@example.com",
    "senha": "senha123"
  }'
```

#### Teste de Criar Humor (com token)

```bash
TOKEN="seu-token-jwt-aqui"

curl -X POST http://localhost:8080/api/humor \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "idUsuario": 1,
    "nivelHumor": 4,
    "nivelEnergia": 3,
    "dataRegistro": "2025-01-15"
  }'
```

#### Teste de Assistente IA (com token)

```bash
TOKEN="seu-token-jwt-aqui"

curl -X POST http://localhost:8080/ia/assistente \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "usuarioId": 1,
    "tipoConsulta": "motivacao"
  }'
```

#### Teste de Co-Planner (com token)

```bash
TOKEN="seu-token-jwt-aqui"

curl -X POST http://localhost:8080/ia/co-planner \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "usuarioId": 1,
    "mensagem": "hoje preciso levar minha gata ao veterinário as 14 e preciso terminar a materia de java para o challenge"
  }'
```

### Testes com Postman

1. **Importe a coleção do Swagger:**
   - Acesse: `http://localhost:8080/v3/api-docs`
   - Copie o JSON
   - Importe no Postman

2. **Configure autenticação:**
   - Faça login primeiro (`/api/auth/login`)
   - Copie o token retornado
   - Configure no Postman: `Authorization` → `Bearer Token` → Cole o token

3. **Teste os endpoints:**
   - Todos os endpoints protegidos precisam do token no header

---

## 🔧 Configuração

### Banco de Dados Oracle

As credenciais estão configuradas em `application.properties`:
- Host: `br.com.fiap.oracle`
- Port: `1521`
- Service: `ORCL`
- User: `rm555241`
- Password: `230205`

### Variáveis de Ambiente

Para produção, configure:
- `OPENAI_API_KEY`: Chave da API OpenAI
- `JWT_SECRET`: Chave secreta para JWT
- `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USERNAME`, `RABBITMQ_PASSWORD`

### Configuração de API Key OpenAI

1. Obtenha sua chave em: https://platform.openai.com/api-keys
2. Configure no `application.properties`:
   ```properties
   spring.ai.openai.api-key=sua-chave-aqui
   ```
3. Ou via variável de ambiente:
   ```bash
   export OPENAI_API_KEY=sua-chave-aqui
   ```

**Importante:** Você precisa ter créditos na conta OpenAI para usar os endpoints de IA.

---

## 🚀 Como Executar

### 1. Compilar o Projeto

```bash
cd nexus
mvn clean install
```

### 2. Executar a Aplicação

```bash
mvn spring-boot:run
```

### 3. Acessar Swagger

```
http://localhost:8080/swagger-ui.html
```

### 4. Verificar Saúde da Aplicação

```
http://localhost:8080/actuator/health
```

---

## 🐳 Docker

```bash
# Build da imagem
docker build -t nexus-mindtrack .

# Executar container
docker run -p 8080:8080 nexus-mindtrack
```

---

## ☁️ Deploy Azure

O projeto inclui:
- `Dockerfile` para containerização
- `azure-pipelines.yml` para CI/CD

---

## 📚 Documentação Adicional

- **Documentação de IA:** Veja `README_IA.md` para detalhes completos sobre integração com OpenAI
- **Swagger UI (Interface Web):** `http://localhost:8080/swagger-ui.html` - Interface web interativa para testar todos os endpoints da API
- **OpenAPI JSON:** `http://localhost:8080/v3/api-docs` - Especificação OpenAPI em formato JSON

---

## 🌍 Internacionalização

A API suporta:
- **PT-BR** (padrão)
- **EN-US**

Configure via header `Accept-Language: en-US` ou use o padrão configurado.

---

## 🔐 Segurança

- **Autenticação JWT** obrigatória para endpoints protegidos
- **Roles:** `ROLE_PROFISSIONAL`, `ROLE_GESTOR`
- **Senhas criptografadas** com BCrypt
- **CORS** configurado para desenvolvimento

---

## 📦 Dependências Principais

- Spring Boot 3.3.6
- Oracle JDBC Driver
- JWT (io.jsonwebtoken) 0.12.3
- MapStruct 1.5.5
- OpenAI Java Client (Theokanning) 0.18.2
- SpringDoc OpenAPI 2.6.0
- Caffeine Cache
- RabbitMQ
- Lombok
- JUnit 5 + Mockito

---

## 🎓 Resumo dos Conceitos Java Aplicados

1. ✅ **OOP**: Classes, objetos, encapsulamento, herança, polimorfismo
2. ✅ **Anotações**: Spring Framework, JPA, Validação
3. ✅ **Padrões de Projeto**: Builder, Repository, Service Layer, DTO, Dependency Injection
4. ✅ **Lombok**: Redução de boilerplate
5. ✅ **Streams API**: Processamento funcional de coleções
6. ✅ **Optional**: Tratamento seguro de null
7. ✅ **Generics**: Reutilização de código
8. ✅ **Reflection**: Acesso dinâmico a classes
9. ✅ **Exception Handling**: Tratamento centralizado
10. ✅ **Concorrência**: RabbitMQ, Cache

---

## 🎯 Impacto e Inovação

### Problema Endereçado

O projeto **Nexus** aborda um desafio crítico na área de **saúde mental e produtividade no trabalho de TI**:

- **Burnout** é uma realidade crescente entre profissionais de TI
- **Falta de monitoramento** preventivo de sinais de esgotamento
- **Ausência de ferramentas** que combinem dados de humor, produtividade e hábitos
- **Necessidade de intervenção proativa** antes que problemas se agravem

### Solução Inovadora

A solução proposta é **inovadora** por combinar:

1. **Inteligência Artificial Generativa**
   - Análise personalizada de humor e produtividade
   - Geração de feedbacks empáticos e contextualizados
   - Planejamento inteligente de tarefas com IA
   - Análise de risco de burnout com recomendações

2. **Visão Computacional**
   - Detecção de movimento para monitorar pausas
   - Sugestões automáticas de alongamento
   - Registro automático de pausas quando usuário retorna

3. **Mensageria Assíncrona**
   - Alertas de burnout processados em background
   - Sistema não bloqueante para melhor performance
   - Escalabilidade para múltiplos usuários

4. **Arquitetura Moderna**
   - Clean Architecture com separação de responsabilidades
   - Cache inteligente para otimização de performance
   - Internacionalização para alcance global
   - API REST pronta para integração com qualquer frontend

### Potencial Impacto Positivo

#### Para Profissionais de TI:
- ✅ **Prevenção de burnout** através de monitoramento contínuo
- ✅ **Melhoria de bem-estar** com sugestões personalizadas
- ✅ **Aumento de produtividade** com planejamento inteligente
- ✅ **Consciência sobre hábitos** saudáveis e não saudáveis

#### Para Empresas:
- ✅ **Redução de absenteísmo** por questões de saúde mental
- ✅ **Aumento de retenção** de talentos
- ✅ **Melhoria de clima organizacional**
- ✅ **Dados para políticas** de bem-estar corporativo

#### Para a Sociedade:
- ✅ **Conscientização** sobre saúde mental no trabalho
- ✅ **Tecnologia acessível** para prevenção de problemas
- ✅ **Modelo replicável** para outras áreas profissionais

### Tecnologias Modernas e Emergentes

O projeto incorpora tecnologias de ponta:

- **Spring AI** para integração com modelos de linguagem
- **OpenAI GPT-4o-mini** para análise inteligente
- **RabbitMQ** para processamento assíncrono escalável
- **Caffeine Cache** para performance otimizada
- **Visão Computacional** nativa em Java (sem dependências pesadas)
- **JWT** para segurança moderna
- **Docker** e **Azure** para deploy em nuvem

### Diferenciais Competitivos

1. **Abordagem Holística**: Combina humor, produtividade, hábitos e pausas
2. **IA Contextualizada**: Análises personalizadas baseadas em histórico
3. **Prevenção Proativa**: Alertas antes que problemas se agravem
4. **Tecnologia Acessível**: API REST que pode ser integrada facilmente
5. **Escalável**: Arquitetura preparada para crescimento

---

## 📊 Diagrama de Arquitetura

```
┌─────────────────────────────────────────────────────────────────┐
│                         CLIENTE (Frontend/Mobile)                │
│                    React, Vue, Angular, React Native             │
└────────────────────────────┬────────────────────────────────────┘
                             │ HTTP/REST
                             │ JWT Token
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                      SPRING BOOT API (Nexus)                     │
│                                                                   │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │              Security Layer (JWT)                        │   │
│  │  - JwtAuthenticationFilter                               │   │
│  │  - SecurityConfig                                        │   │
│  └──────────────────────────────────────────────────────────┘   │
│                                                                   │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │              Controllers (REST Endpoints)                 │   │
│  │  - UsuarioController, HumorController,                  │   │
│  │    SprintController, IAController, etc.                  │   │
│  └──────────────────────────────────────────────────────────┘   │
│                             │                                     │
│                             ▼                                     │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │              Service Layer (Business Logic)               │   │
│  │  - UsuarioService, HumorService,                         │   │
│  │    IAService, SprintService, etc.                         │   │
│  └──────────────────────────────────────────────────────────┘   │
│                             │                                     │
│        ┌────────────────────┼────────────────────┐                │
│        ▼                    ▼                    ▼                │
│  ┌──────────┐      ┌──────────────┐    ┌──────────────┐        │
│  │   Cache  │      │   RabbitMQ   │    │   AI Layer   │        │
│  │ (Caffeine)│      │  (Messages)  │    │  (OpenAI)   │        │
│  └──────────┘      └──────────────┘    └──────────────┘        │
│        │                    │                    │                │
│        └────────────────────┼────────────────────┘                │
│                             ▼                                     │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │              Repository Layer (Data Access)              │   │
│  │  - UsuarioRepository, HumorRepository,                   │   │
│  │    SprintRepository, etc.                                 │   │
│  └──────────────────────────────────────────────────────────┘   │
│                             │                                     │
│                             ▼                                     │
└─────────────────────────────────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                    ORACLE DATABASE                              │
│  - t_mt_usuarios, t_mt_humor, t_mt_sprints,                     │
│    t_mt_habitos, t_mt_alertas_ia, etc.                         │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                    EXTERNAL SERVICES                            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐        │
│  │   OpenAI     │  │   RabbitMQ   │  │   Swagger UI │        │
│  │   (GPT-4)    │  │   (Queues)   │  │  (Docs/Test) │        │
│  └──────────────┘  └──────────────┘  └──────────────┘        │
└─────────────────────────────────────────────────────────────────┘
```

### Fluxo de Dados Principal

1. **Cliente** → Requisição HTTP com JWT
2. **Security Layer** → Valida token e autorização
3. **Controller** → Recebe e valida DTO
4. **Service** → Executa lógica de negócio
5. **Cache/RabbitMQ/AI** → Serviços auxiliares (se necessário)
6. **Repository** → Acesso ao banco de dados
7. **Oracle Database** → Persistência
8. **Response** → Retorna DTO para cliente

---

## 🧪 Cobertura de Testes

### Testes Implementados

O projeto possui **testes unitários** abrangentes usando **JUnit 5** e **Mockito**:

#### Serviços Testados:
- ✅ `UsuarioServiceTest` - Registro, validação, criptografia
- ✅ `HumorServiceTest` - Criação, alertas de burnout
- ✅ `SprintServiceTest` - CRUD completo, paginação
- ✅ `HabitoServiceTest` - CRUD completo, paginação

#### Cobertura:
- **Criação** de entidades
- **Leitura** com paginação
- **Atualização** de dados
- **Exclusão** de registros
- **Validações** de negócio
- **Tratamento de erros**

### Executar Testes

```bash
# Executar todos os testes
mvn test

# Executar testes com relatório
mvn test surefire-report:report

# Executar teste específico
mvn test -Dtest=UsuarioServiceTest

# Executar testes de um pacote
mvn test -Dtest=com.nexus.modules.usuarios.*
```

### Estrutura de Testes

```
src/test/java/com/nexus/
├── modules/
│   ├── usuarios/service/UsuarioServiceTest.java
│   ├── humor/service/HumorServiceTest.java
│   ├── sprints/service/SprintServiceTest.java
│   └── habitos/service/HabitoServiceTest.java
└── NexusApplicationTests.java
```

### Conceitos de Teste Aplicados

- **Mockito**: Mocks de dependências
- **JUnit 5**: Framework de testes
- **Arrange-Act-Assert**: Padrão AAA
- **Testes isolados**: Cada teste é independente
- **Cobertura de casos**: Casos de sucesso e erro

---

**Desenvolvido com ❤️ seguindo Clean Architecture e SOLID**
