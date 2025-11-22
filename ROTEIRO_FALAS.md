# 🎬 Roteiro de Falas - Projeto Nexus / MindTrack

---

## 🎯 PARTE 1: INTRODUÇÃO E APRESENTAÇÃO

Olá! Bem-vindos ao meu projeto Nexus, também conhecido como MindTrack. Uma API REST completa desenvolvida em Java com Spring Boot. O objetivo é monitorar saúde mental e produtividade de profissionais de TI.

Profissionais de TI enfrentam altos índices de burnout e estresse. Falta de ferramentas para monitorar e prevenir problemas de saúde mental. Dificuldade em correlacionar humor, produtividade e hábitos. Nexus resolve isso com uma plataforma completa de monitoramento.

Nexus é uma API REST completa que pode ser integrada com qualquer aplicação. React, Vue, Angular, React Native, Flutter - qualquer tecnologia frontend. Não tem interface própria, mas tem Swagger UI para testes.

---

## 🚀 PARTE 2: TECNOLOGIAS E STACK

Utilizamos Java 17, versão moderna do Java. Spring Boot 3.3.6 como framework principal. Spring Security com JWT para autenticação e autorização. Spring Data JPA para persistência de dados. Oracle Database como banco de dados empresarial. E MapStruct para conversão automática entre DTOs e entidades.

Para IA e integração, temos OpenAI GPT-4o-mini para análises inteligentes e assistente pessoal. Theokanning SDK para integração com OpenAI. Sistema com fallback para HttpClient, garantindo robustez e redundância. E Google Gemini preparado como fallback alternativo para o futuro.

Na infraestrutura, RabbitMQ para mensageria assíncrona de alertas. Caffeine Cache para cache em memória e performance. SpringDoc com Swagger para documentação interativa da API. Internacionalização com suporte a PT-BR e EN-US. Docker para containerização. E Azure Pipelines para CI/CD.

---

## 🏗️ PARTE 3: ARQUITETURA DO PROJETO

Seguimos Clean Architecture com separação clara de responsabilidades. Na camada Domain/Model temos as entidades JPA: Usuario, Humor, Sprint, Habito e Badge. Infrastructure/Repository é responsável pelo acesso a dados usando Spring Data JPA. Application/DTO contém os objetos de transferência de dados. Application/Mapper faz a conversão automática com MapStruct. Modules contém os módulos de negócio: usuarios, humor, sprints, habitos, badges, ia e alertas. E Config tem todas as configurações do Spring: Security, Cache, RabbitMQ e Swagger.

O fluxo de uma requisição funciona assim: o cliente faz uma requisição HTTP, por exemplo POST /api/humor. O JwtAuthenticationFilter valida o token JWT. O SecurityConfig verifica as permissões. O Controller recebe e valida o DTO. O Service executa a lógica de negócio. O Repository persiste no banco. O Mapper converte a Entity para DTO. E o Controller retorna o JSON.

Utilizamos vários padrões de design: Repository Pattern para abstração de acesso a dados. Service Layer para isolar a lógica de negócio. DTO Pattern para transferência de dados entre camadas. Builder Pattern para construção de objetos complexos com Lombok. E Dependency Injection com inversão de controle do Spring.

---

## 💡 PARTE 4: FUNCIONALIDADES PRINCIPAIS

Temos um sistema completo de autenticação JWT. Login, registro e geração de tokens. Tudo com segurança usando Spring Security.

No módulo de humor, os usuários registram seu humor diariamente. O sistema detecta padrões de burnout. Envia alertas via RabbitMQ quando detecta risco. E faz análise baseada em múltiplos fatores.

Para sprints e produtividade, temos registro de sprints e tarefas. Cálculo de produtividade. E correlação entre humor e produtividade.

O módulo de hábitos permite rastreamento de hábitos saudáveis. Gamificação com badges. E incentivo a práticas positivas.

O módulo de IA Generativa é um dos nossos destaques. É um assistente pessoal de saúde mental com IA. Oferece 5 tipos de conteúdo: curiosidade, prevenção, motivação, dica prática e reflexão. Faz análise de agenda e extração de tarefas. Gera feedback empático baseado no histórico do usuário. Sistema dual com SDK Theokanning e HttpClient como fallback. E tratamento robusto de erros, incluindo quota e API key inválida.

Também temos monitoramento de pausas com visão computacional. Detecção de movimento nativa em Java. Sugestões de alongamento após 1 hora sentado. E contagem de pausas e tempo sentado.

O sistema de badges e gamificação oferece conquistas. Badges por consistência, hábitos e produtividade. E gamificação para engajamento.

---

## 🧪 PARTE 5: DEMONSTRAÇÃO PRÁTICA

A aplicação Spring Boot está iniciando. Conectando ao banco de dados. Carregando configurações.

O Swagger UI é nossa interface web para testes. Aqui podemos ver todos os endpoints disponíveis. Documentação interativa e completa.

Primeiro, precisamos fazer login. Recebemos um token JWT. Este token será usado para autenticar todas as requisições. Vamos autorizar no Swagger.

Agora vamos testar o assistente de IA. Solicitando uma mensagem motivacional. A IA analisa o contexto do usuário e gera conteúdo personalizado. Aqui temos o título, conteúdo e ações práticas na resposta.

Agora vamos testar a extração de tarefas. O sistema usa IA para extrair tarefas de uma mensagem natural. Retorna lista estruturada de tarefas. Aqui vemos o array de tarefas extraídas.

Este endpoint monitora pausas usando visão computacional. Recebe frames de vídeo em Base64. Detecta movimento e sugere alongamentos. Não identifica pessoas, apenas detecta variação de pixels.

---

## ✨ PARTE 6: DIFERENCIAIS E INOVAÇÃO

Nossos diferenciais técnicos incluem sistema dual de chamadas à API OpenAI com SDK e HttpClient. Tratamento robusto de erros e fallbacks. Visão computacional nativa em Java, sem bibliotecas pesadas. Arquitetura escalável e preparada para crescimento. Cache inteligente para performance. E mensageria assíncrona para alertas.

Nos diferenciais de negócio, temos abordagem holística combinando humor, produtividade, hábitos e pausas. IA contextualizada com histórico do usuário. Prevenção proativa de burnout. Gamificação para engajamento. E API REST pronta para integração.

O impacto e inovação são significativos. Burnout afeta 70% dos profissionais de TI. Oferecemos uma solução tecnológica acessível. Modelo replicável para outras áreas. Combina tecnologias modernas como IA, mensageria e cache. E previne problemas antes que se agravem.

---

## 📊 PARTE 7: TESTES E QUALIDADE

O projeto tem cobertura de testes unitários. Testes para serviços principais. Uso de JUnit 5 e Mockito. E 15 testes passando com sucesso.

O código está limpo e bem organizado. Seguindo boas práticas Java e Spring. Documentação completa. E padrões de design aplicados.

---

## 🎯 PARTE 8: CONCLUSÃO

Nexus é uma API REST completa para monitoramento de saúde mental. Desenvolvida com Java 17 e Spring Boot 3.3.6. Integração com IA para análises inteligentes. Arquitetura escalável e bem estruturada. E pronta para ser consumida por qualquer frontend.

Pode ser integrado com aplicações web ou mobile. Expandir funcionalidades de IA. Adicionar mais análises e insights. E melhorar gamificação.

Obrigado por assistir! Código disponível no GitHub. Qualquer dúvida, deixem nos comentários. Até a próxima!


