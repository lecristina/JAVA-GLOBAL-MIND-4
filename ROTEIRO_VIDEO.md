# 🎬 Roteiro para Vídeo - Projeto Nexus / MindTrack

## 📋 Estrutura do Vídeo (Duração estimada: 15-20 minutos)

---

## 🎯 **PARTE 1: INTRODUÇÃO E APRESENTAÇÃO** (2-3 min)

### 1.1 Abertura
**O que falar:**
- "Olá! Bem-vindos ao meu projeto Nexus, também conhecido como MindTrack"
- "Uma API REST completa desenvolvida em Java com Spring Boot"
- "O objetivo é monitorar saúde mental e produtividade de profissionais de TI"

### 1.2 O Problema que Resolve
**O que falar:**
- "Profissionais de TI enfrentam altos índices de burnout e estresse"
- "Falta de ferramentas para monitorar e prevenir problemas de saúde mental"
- "Dificuldade em correlacionar humor, produtividade e hábitos"
- "Nexus resolve isso com uma plataforma completa de monitoramento"

### 1.3 Visão Geral
**O que mostrar:**
- Abrir o README.md no editor
- Mostrar a estrutura do projeto no explorador de arquivos
- Destacar: "É uma API REST pura, pronta para ser consumida por qualquer frontend"

**O que falar:**
- "Nexus é uma API REST completa que pode ser integrada com qualquer aplicação"
- "React, Vue, Angular, React Native, Flutter - qualquer tecnologia frontend"
- "Não tem interface própria, mas tem Swagger UI para testes"

---

## 🚀 **PARTE 2: TECNOLOGIAS E STACK** (3-4 min)

### 2.1 Stack Principal
**O que mostrar:**
- Abrir `pom.xml` e destacar as dependências principais

**O que falar:**
- **Java 17** - Versão moderna do Java
- **Spring Boot 3.3.6** - Framework principal
- **Spring Security + JWT** - Autenticação e autorização
- **Spring Data JPA** - Persistência de dados
- **Oracle Database** - Banco de dados empresarial
- **MapStruct** - Conversão automática DTO ↔ Entity

### 2.2 Tecnologias de IA e Integração
**O que mostrar:**
- Abrir `src/main/java/com/nexus/ai/GPTService.java` (mostrar imports)

**O que falar:**
- **OpenAI GPT-4o-mini** - Para análises inteligentes e assistente pessoal
- **Theokanning SDK** - Integração com OpenAI
- **Fallback para HttpClient** - Sistema robusto com redundância
- **Google Gemini** - Fallback alternativo (preparado para futuro)

### 2.3 Infraestrutura e Performance
**O que falar:**
- **RabbitMQ** - Mensageria assíncrona para alertas
- **Caffeine Cache** - Cache em memória para performance
- **SpringDoc/Swagger** - Documentação interativa da API
- **Internacionalização (i18n)** - Suporte PT-BR e EN-US
- **Docker** - Containerização
- **Azure Pipelines** - CI/CD

---

## 🏗️ **PARTE 3: ARQUITETURA DO PROJETO** (3-4 min)

### 3.1 Estrutura de Camadas (Clean Architecture)
**O que mostrar:**
- Abrir estrutura de pastas no IDE
- Navegar por: `config/`, `domain/model/`, `infrastructure/repository/`, `application/`, `modules/`

**O que falar:**
- "Seguimos Clean Architecture com separação clara de responsabilidades"
- **Domain/Model** - Entidades JPA (Usuario, Humor, Sprint, Habito, Badge)
- **Infrastructure/Repository** - Acesso a dados (Spring Data JPA)
- **Application/DTO** - Objetos de transferência de dados
- **Application/Mapper** - Conversão automática com MapStruct
- **Modules** - Módulos de negócio (usuarios, humor, sprints, habitos, badges, ia, alertas)
- **Config** - Configurações do Spring (Security, Cache, RabbitMQ, Swagger)

### 3.2 Fluxo de uma Requisição
**O que mostrar:**
- Diagrama no README.md (se houver) ou desenhar no quadro

**O que falar:**
1. "Cliente faz requisição HTTP → `POST /api/humor`"
2. "JwtAuthenticationFilter valida o token JWT"
3. "SecurityConfig verifica permissões"
4. "Controller recebe e valida o DTO"
5. "Service executa lógica de negócio"
6. "Repository persiste no banco"
7. "Mapper converte Entity para DTO"
8. "Controller retorna JSON"

### 3.3 Padrões de Design Utilizados
**O que falar:**
- **Repository Pattern** - Abstração de acesso a dados
- **Service Layer** - Lógica de negócio isolada
- **DTO Pattern** - Transferência de dados entre camadas
- **Builder Pattern** - Construção de objetos complexos (Lombok)
- **Dependency Injection** - Inversão de controle do Spring

---

## 💡 **PARTE 4: FUNCIONALIDADES PRINCIPAIS** (5-6 min)

### 4.1 Módulo de Usuários e Autenticação
**O que mostrar:**
- Abrir `src/main/java/com/nexus/modules/usuarios/controller/AuthController.java`

**O que falar:**
- "Sistema completo de autenticação JWT"
- "Login, registro, geração de tokens"
- "Segurança com Spring Security"

### 4.2 Módulo de Humor
**O que mostrar:**
- Abrir `src/main/java/com/nexus/modules/humor/service/HumorService.java`
- Mostrar lógica de detecção de burnout

**O que falar:**
- "Usuários registram seu humor diariamente"
- "Sistema detecta padrões de burnout"
- "Envia alertas via RabbitMQ quando detecta risco"
- "Análise baseada em múltiplos fatores"

### 4.3 Módulo de Sprints e Produtividade
**O que mostrar:**
- Abrir `src/main/java/com/nexus/modules/sprints/service/SprintService.java`

**O que falar:**
- "Registro de sprints e tarefas"
- "Cálculo de produtividade"
- "Correlação entre humor e produtividade"

### 4.4 Módulo de Hábitos
**O que mostrar:**
- Abrir `src/main/java/com/nexus/modules/habitos/service/HabitoService.java`

**O que falar:**
- "Rastreamento de hábitos saudáveis"
- "Gamificação com badges"
- "Incentivo a práticas positivas"

### 4.5 Módulo de IA Generativa ⭐ (DESTAQUE)
**O que mostrar:**
- Abrir `src/main/java/com/nexus/ai/GPTService.java`
- Mostrar método `processarMensagemAssistant`
- Abrir `src/main/java/com/nexus/modules/ia/controller/IAController.java`

**O que falar:**
- "Assistente pessoal de saúde mental com IA"
- "5 tipos de conteúdo: curiosidade, prevenção, motivação, dica prática, reflexão"
- "Análise de agenda e extração de tarefas"
- "Feedback empático baseado no histórico do usuário"
- "Sistema dual: SDK Theokanning + HttpClient fallback"
- "Tratamento robusto de erros (quota, API key inválida)"

**O que mostrar:**
- Abrir `src/main/java/com/nexus/ai/PausaMonitorService.java`

**O que falar:**
- "Monitoramento de pausas com visão computacional"
- "Detecção de movimento nativa em Java"
- "Sugestões de alongamento após 1 hora sentado"
- "Contagem de pausas e tempo sentado"

### 4.6 Sistema de Badges e Gamificação
**O que mostrar:**
- Abrir `src/main/java/com/nexus/modules/badges/service/BadgeService.java`

**O que falar:**
- "Sistema de conquistas"
- "Badges por consistência, hábitos, produtividade"
- "Gamificação para engajamento"

---

## 🧪 **PARTE 5: DEMONSTRAÇÃO PRÁTICA** (4-5 min)

### 5.1 Iniciando a Aplicação
**O que fazer:**
- Abrir terminal
- Executar: `mvn spring-boot:run`
- Aguardar aplicação iniciar
- Mostrar logs de inicialização

**O que falar:**
- "Aplicação Spring Boot iniciando"
- "Conectando ao banco de dados"
- "Carregando configurações"

### 5.2 Acessando o Swagger UI
**O que fazer:**
- Abrir navegador
- Acessar: `http://localhost:8080/swagger-ui.html`
- Mostrar interface do Swagger

**O que falar:**
- "Swagger UI é nossa interface web para testes"
- "Aqui podemos ver todos os endpoints disponíveis"
- "Documentação interativa e completa"

### 5.3 Testando Autenticação
**O que fazer:**
- Expandir endpoint `/api/auth/login`
- Preencher dados de login
- Clicar em "Execute"
- Mostrar resposta com token JWT
- Clicar em "Authorize" e colar o token

**O que falar:**
- "Primeiro, precisamos fazer login"
- "Recebemos um token JWT"
- "Este token será usado para autenticar todas as requisições"
- "Vamos autorizar no Swagger"

### 5.4 Testando Endpoint de IA
**O que fazer:**
- Expandir endpoint `/ia/assistente`
- Preencher JSON:
```json
{
  "usuarioId": 1,
  "tipoConsulta": "motivacao"
}
```
- Executar e mostrar resposta

**O que falar:**
- "Vamos testar o assistente de IA"
- "Solicitando uma mensagem motivacional"
- "A IA analisa o contexto do usuário e gera conteúdo personalizado"
- Mostrar resposta com título, conteúdo, ações práticas

### 5.5 Testando Extração de Tarefas (Co-Planner)
**O que fazer:**
- Expandir endpoint `/ia/co-planner`
- Preencher JSON:
```json
{
  "usuarioId": 1,
  "mensagem": "Preciso fazer: revisar código, escrever testes, atualizar documentação"
}
```
- Executar e mostrar resposta

**O que falar:**
- "Agora vamos testar a extração de tarefas"
- "O sistema usa IA para extrair tarefas de uma mensagem natural"
- "Retorna lista estruturada de tarefas"
- Mostrar resposta com array de tarefas

### 5.6 Testando Monitoramento de Pausa
**O que fazer:**
- Expandir endpoint `/ia/pausa-monitor`
- Explicar que precisa de uma imagem em Base64
- Mostrar exemplo de request

**O que falar:**
- "Este endpoint monitora pausas usando visão computacional"
- "Recebe frames de vídeo em Base64"
- "Detecta movimento e sugere alongamentos"
- "Não identifica pessoas, apenas detecta variação de pixels"

---

## ✨ **PARTE 6: DIFERENCIAIS E INOVAÇÃO** (2-3 min)

### 6.1 Diferenciais Técnicos
**O que falar:**
- "Sistema dual de chamadas à API OpenAI (SDK + HttpClient)"
- "Tratamento robusto de erros e fallbacks"
- "Visão computacional nativa em Java (sem bibliotecas pesadas)"
- "Arquitetura escalável e preparada para crescimento"
- "Cache inteligente para performance"
- "Mensageria assíncrona para alertas"

### 6.2 Diferenciais de Negócio
**O que falar:**
- "Abordagem holística: humor + produtividade + hábitos + pausas"
- "IA contextualizada com histórico do usuário"
- "Prevenção proativa de burnout"
- "Gamificação para engajamento"
- "API REST pronta para integração"

### 6.3 Impacto e Inovação
**O que falar:**
- "Problema real: burnout afeta 70% dos profissionais de TI"
- "Solução tecnológica acessível"
- "Modelo replicável para outras áreas"
- "Combina tecnologias modernas (IA, mensageria, cache)"
- "Prevenção antes que problemas se agravem"

---

## 📊 **PARTE 7: TESTES E QUALIDADE** (1-2 min)

### 7.1 Testes Unitários
**O que fazer:**
- Abrir pasta `src/test/java`
- Mostrar alguns testes
- Executar: `mvn test`
- Mostrar resultado (BUILD SUCCESS)

**O que falar:**
- "Projeto tem cobertura de testes unitários"
- "Testes para serviços principais"
- "Uso de JUnit 5 e Mockito"
- "15 testes passando com sucesso"

### 7.2 Qualidade de Código
**O que falar:**
- "Código limpo e bem organizado"
- "Seguindo boas práticas Java e Spring"
- "Documentação completa"
- "Padrões de design aplicados"

---

## 🎯 **PARTE 8: CONCLUSÃO** (1 min)

### 8.1 Resumo
**O que falar:**
- "Nexus é uma API REST completa para monitoramento de saúde mental"
- "Desenvolvida com Java 17 e Spring Boot 3.3.6"
- "Integração com IA para análises inteligentes"
- "Arquitetura escalável e bem estruturada"
- "Pronta para ser consumida por qualquer frontend"

### 8.2 Próximos Passos
**O que falar:**
- "Pode ser integrado com aplicações web ou mobile"
- "Expandir funcionalidades de IA"
- "Adicionar mais análises e insights"
- "Melhorar gamificação"

### 8.3 Encerramento
**O que falar:**
- "Obrigado por assistir!"
- "Código disponível no GitHub"
- "Qualquer dúvida, deixem nos comentários"
- "Até a próxima!"

---

## 📝 **DICAS PARA GRAVAÇÃO**

### Preparação
- ✅ Ter a aplicação rodando antes de gravar
- ✅ Ter dados de teste no banco (usuário criado)
- ✅ Ter API Key do OpenAI configurada (ou explicar que precisa)
- ✅ Swagger UI acessível
- ✅ Terminal pronto com comandos

### Durante a Gravação
- ✅ Falar pausadamente e com clareza
- ✅ Mostrar código relevante, mas não ficar muito tempo em detalhes
- ✅ Fazer pausas entre seções
- ✅ Destacar pontos importantes (use gestos ou zoom)
- ✅ Se errar, pause e refaça a parte

### Edição
- ✅ Adicionar transições entre seções
- ✅ Inserir títulos/legendas para cada parte
- ✅ Destacar código importante com zoom
- ✅ Adicionar música de fundo suave (opcional)
- ✅ Inserir timestamps na descrição do vídeo

---

## 🎬 **CHECKLIST PRÉ-GRAVAÇÃO**

- [ ] Aplicação Spring Boot compilada e funcionando
- [ ] Banco de dados configurado e com dados de teste
- [ ] Swagger UI acessível em `http://localhost:8080/swagger-ui.html`
- [ ] API Key do OpenAI configurada (ou preparar explicação)
- [ ] Terminal aberto e pronto
- [ ] IDE com projeto aberto
- [ ] Navegador aberto
- [ ] Áudio testado (microfone funcionando)
- [ ] Tela em resolução adequada (1920x1080 recomendado)
- [ ] Roteiro impresso ou em segunda tela

---

## 📚 **PONTOS DE DESTAQUE PARA ENFATIZAR**

1. **Arquitetura Limpa**: Separação clara de responsabilidades
2. **IA Integrada**: Sistema inteligente de análise e assistente pessoal
3. **Visão Computacional**: Detecção de movimento nativa em Java
4. **Robustez**: Sistema dual com fallbacks e tratamento de erros
5. **Escalabilidade**: Preparado para crescimento
6. **API REST Pura**: Pronta para qualquer frontend
7. **Documentação**: Swagger UI completo
8. **Testes**: Cobertura de testes unitários
9. **Tecnologias Modernas**: Stack atualizado e relevante
10. **Problema Real**: Solução para um problema importante

---

## ⏱️ **TIMING SUGERIDO**

| Parte | Duração | Acumulado |
|-------|---------|-----------|
| 1. Introdução | 2-3 min | 2-3 min |
| 2. Tecnologias | 3-4 min | 5-7 min |
| 3. Arquitetura | 3-4 min | 8-11 min |
| 4. Funcionalidades | 5-6 min | 13-17 min |
| 5. Demonstração | 4-5 min | 17-22 min |
| 6. Diferenciais | 2-3 min | 19-25 min |
| 7. Testes | 1-2 min | 20-27 min |
| 8. Conclusão | 1 min | 21-28 min |

**Total estimado: 20-25 minutos** (com edição pode ficar em 15-20 min)

---

## 🎥 **SUGESTÕES DE CENAS**

### Cena 1: Abertura
- Tela com logo/título do projeto
- Fade in para IDE com código

### Cena 2: Arquitetura
- Diagrama desenhado ou mostrado
- Zoom em partes importantes

### Cena 3: Código
- Split screen: código + explicação
- Highlight de partes importantes

### Cena 4: Demonstração
- Tela cheia do Swagger
- Mostrar requisições e respostas
- Destacar resultados interessantes

### Cena 5: Encerramento
- Resumo visual
- Links para GitHub
- Call to action

---

**Boa gravação! 🎬✨**


