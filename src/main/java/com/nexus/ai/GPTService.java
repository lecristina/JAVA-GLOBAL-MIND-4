package com.nexus.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

/**
 * Serviço para integração com GPT via API OpenAI
 * Usa SDK Theokanning para Chat Completions com fallback para HttpClient
 */
@Service
@Slf4j
public class GPTService {

    private HttpClient httpClient;
    private ObjectMapper objectMapper;
    private OpenAiService openAiService; // SDK Theokanning
    
    private String apiKey;
    private String model;
    private Double temperature;
    private String assistantId; // ID do Assistant para co-planner
    
    // Gemini removido temporariamente - usando apenas OpenAI
    
    /**
     * Inicializa o SDK Theokanning de forma defensiva
     * Se falhar, o serviço continua funcionando com HttpClient
     * NUNCA lança exceção - sempre retorna silenciosamente
     */
    private void inicializarSDKOpenAI(String apiKey) {
        // SEMPRE inicializa como null primeiro
        this.openAiService = null;
        
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your-api-key-here") || !apiKey.startsWith("sk-")) {
            log.warn("⚠️ API Key inválida, OpenAI SDK não será inicializado");
            return;
        }
        
        // Tenta inicializar o SDK, mas captura QUALQUER erro possível
        try {
            log.debug("🔧 Tentando inicializar OpenAI SDK (Theokanning)...");
            
            // Verifica se as classes do SDK estão disponíveis antes de tentar usar
            try {
                Class.forName("com.theokanning.openai.service.OpenAiService");
            } catch (ClassNotFoundException | NoClassDefFoundError e) {
                log.warn("⚠️ Classes do OpenAI SDK não encontradas no classpath. Usando HttpClient como fallback.");
                this.openAiService = null;
                return;
            } catch (Throwable e) {
                log.warn("⚠️ Erro ao verificar classes do SDK: {}. Usando HttpClient como fallback.", e.getMessage());
                this.openAiService = null;
                return;
            }
            
            // Tenta criar o serviço - com múltiplas camadas de proteção
            try {
                this.openAiService = new OpenAiService(apiKey, Duration.ofSeconds(30));
                log.info("✅ OpenAI SDK (Theokanning) inicializado com sucesso");
            } catch (LinkageError e) {
                log.warn("⚠️ Erro de classpath/linkagem ao criar cliente OpenAI SDK: {}. Usando HttpClient como fallback.", e.getMessage());
                this.openAiService = null;
            } catch (Throwable e) {
                log.warn("⚠️ Erro ao criar cliente OpenAI SDK: {}. Usando HttpClient como fallback.", e.getMessage());
                this.openAiService = null;
            }
            
        } catch (Throwable t) {
            // Captura QUALQUER erro ou exceção (Throwable é a classe base de tudo)
            log.warn("⚠️ Erro ao inicializar OpenAI SDK ({}: {}). Usando HttpClient como fallback.", 
                    t.getClass().getSimpleName(), 
                    t.getMessage() != null ? t.getMessage() : "sem mensagem");
            this.openAiService = null;
            
            // Log detalhado apenas em modo debug
            if (log.isDebugEnabled()) {
                log.debug("Stack trace completo do erro:", t);
            }
        } finally {
            // Garante que sempre está null se não foi inicializado
            if (this.openAiService == null) {
                log.debug("✅ OpenAI SDK não disponível - usando HttpClient como fallback");
            }
        }
    }

    public GPTService(
            @Value("${spring.ai.openai.api-key}") String apiKey,
            @Value("${spring.ai.openai.chat.options.model:gpt-4o-mini}") String model,
            @Value("${spring.ai.openai.chat.options.temperature:0.7}") Double temperature,
            @Value("${openai.assistant.id:}") String assistantId) {
        try {
            // Inicializa campos básicos primeiro
            this.httpClient = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(30))
                    .build();
            this.objectMapper = new ObjectMapper();
            this.apiKey = apiKey;
            this.model = model;
            this.temperature = temperature;
            this.assistantId = assistantId;
            this.openAiService = null; // Inicializa como null por padrão
            
            // Inicializa o SDK Theokanning de forma defensiva
            // Este método NUNCA lança exceção - sempre retorna silenciosamente
            inicializarSDKOpenAI(apiKey);
            
            // Log para debug (apenas primeiros caracteres por segurança)
            if (apiKey != null && !apiKey.isEmpty() && !apiKey.equals("your-api-key-here")) {
                String apiKeyPreview = apiKey.length() > 15 ? apiKey.substring(0, 15) + "..." : apiKey;
                log.info("✅ GPTService inicializado com API Key: {}", apiKeyPreview);
                log.info("✅ Modelo configurado: {}", model);
                log.info("✅ API Key completa (primeiros 20 chars): {}", apiKey.length() > 20 ? apiKey.substring(0, 20) + "..." : apiKey);
                if (assistantId != null && !assistantId.isEmpty()) {
                    log.info("✅ Assistant ID configurado: {}", assistantId);
                } else {
                    log.warn("⚠️ Assistant ID não configurado. O co-planner usará Chat Completions.");
                }
            } else {
                log.error("❌ GPTService inicializado SEM API Key válida. API Key recebida: {}", 
                        apiKey != null ? (apiKey.length() > 20 ? apiKey.substring(0, 20) + "..." : apiKey) : "null");
            }
        } catch (Throwable t) {
            // Captura QUALQUER erro no construtor e loga, mas permite que o bean seja criado
            log.error("❌ Erro crítico no construtor do GPTService: {} - {}", 
                    t.getClass().getSimpleName(), 
                    t.getMessage() != null ? t.getMessage() : "sem mensagem");
            if (log.isDebugEnabled()) {
                log.debug("Stack trace completo:", t);
            }
            // Garante que os campos básicos estejam inicializados mesmo em caso de erro
            try {
                if (this.httpClient == null) {
                    this.httpClient = HttpClient.newBuilder()
                            .connectTimeout(Duration.ofSeconds(30))
                            .build();
                }
            } catch (Throwable e) {
                log.error("❌ Erro ao criar HttpClient: {}", e.getMessage());
                // Tenta criar um HttpClient básico sem timeout
                try {
                    this.httpClient = HttpClient.newHttpClient();
                } catch (Throwable e2) {
                    log.error("❌ Erro crítico ao criar HttpClient básico: {}", e2.getMessage());
                }
            }
            if (this.objectMapper == null) {
                this.objectMapper = new ObjectMapper();
            }
            // Campos de configuração podem ser null em caso de erro, mas o serviço ainda funciona
            if (this.apiKey == null) {
                this.apiKey = "";
            }
            if (this.model == null) {
                this.model = "gpt-4o-mini";
            }
            if (this.temperature == null) {
                this.temperature = 0.7;
            }
        }
    }

    /**
     * Gera feedback empático usando GPT com histórico e variação
     */
    public String gerarFeedbackEmpatico(Integer humor, String produtividade) {
        return gerarFeedbackEmpatico(humor, produtividade, null, 0.7);
    }

    /**
     * Gera feedback empático usando GPT com histórico e variação
     */
    public String gerarFeedbackEmpatico(Integer humor, String produtividade, String contextoHistorico, double temperatura) {
        try {
            if (apiKey == null || apiKey.equals("your-api-key-here") || apiKey.isEmpty()) {
                log.warn("⚠️ API Key do OpenAI não configurada. Retornando feedback padrão (FALLBACK - não usa IA real).");
                return gerarFeedbackPadrao(humor, produtividade);
            }

            StringBuilder promptBuilder = new StringBuilder();
            promptBuilder.append("Você é um assistente pessoal especializado em saúde mental e bem-estar no trabalho de TI. ");
            promptBuilder.append("Você tem anos de experiência em psicologia organizacional, produtividade e prevenção de burnout. ");
            promptBuilder.append("Seu estilo é empático, profissional e acolhedor, como um mentor que realmente se importa.\n\n");
            
            promptBuilder.append("CONTEXTO DO USUÁRIO:\n");
            promptBuilder.append(String.format("- Nível de humor: %d/5 (1=muito triste, 5=muito feliz)\n", humor));
            promptBuilder.append(String.format("- Nível de produtividade: %s\n\n", produtividade));
            
            // Adiciona contexto de histórico se disponível
            if (contextoHistorico != null && !contextoHistorico.isEmpty()) {
                promptBuilder.append(contextoHistorico).append("\n\n");
            }
            
            promptBuilder.append("INSTRUÇÕES:\n");
            promptBuilder.append("1. Analise o contexto do usuário com profundidade\n");
            promptBuilder.append("2. Reconheça os sentimentos e desafios dele\n");
            promptBuilder.append("3. Gere uma mensagem curta (máximo 150 caracteres) que seja:\n");
            promptBuilder.append("   - Empática e acolhedora\n");
            promptBuilder.append("   - Prática e acionável\n");
            promptBuilder.append("   - Específica para o contexto dele\n");
            promptBuilder.append("   - Como um amigo experiente que oferece apoio genuíno\n");
            promptBuilder.append("   - ÚNICA e DIFERENTE de respostas anteriores (se houver histórico)\n");
            promptBuilder.append("4. Inclua uma sugestão concreta que ele pode fazer AGORA\n");
            promptBuilder.append("5. Varie a abordagem: use diferentes metáforas, exemplos, ou estruturas\n\n");
            
            promptBuilder.append("EXEMPLOS DE TOM (use como inspiração, mas seja criativo):\n");
            promptBuilder.append("- Se humor baixo: 'Entendo que está difícil hoje. Que tal uma pausa de 5min para respirar? Você merece.'\n");
            promptBuilder.append("- Se produtividade baixa: 'Dias assim acontecem. Pequenos passos contam. Comece com uma tarefa simples.'\n\n");
            
            promptBuilder.append("IMPORTANTE: Seja criativo e original. Evite repetir estruturas ou frases das respostas anteriores.\n\n");
            promptBuilder.append("Responda APENAS com a mensagem, sem explicações adicionais.");

            return chamarGPT(promptBuilder.toString(), temperatura);
        } catch (Exception e) {
            log.error("Erro ao gerar feedback empático com GPT", e);
            return gerarFeedbackPadrao(humor, produtividade);
        }
    }

    /**
     * Gera conteúdo personalizado do assistente pessoal
     */
    public AssistentePersonalizado gerarConteudoAssistente(String tipoConsulta, String contextoUsuario) {
        return gerarConteudoAssistente(tipoConsulta, contextoUsuario, null, 0.7);
    }

    /**
     * Gera conteúdo personalizado do assistente pessoal com histórico e variação
     */
    public AssistentePersonalizado gerarConteudoAssistente(String tipoConsulta, String contextoUsuario, String contextoHistorico, double temperatura) {
        try {
            if (apiKey == null || apiKey.equals("your-api-key-here") || apiKey.isEmpty()) {
                log.warn("API Key do OpenAI não configurada. Retornando conteúdo padrão.");
                return gerarConteudoPadrao(tipoConsulta);
            }

            String prompt = construirPromptAssistente(tipoConsulta, contextoUsuario, contextoHistorico, temperatura);
            String resposta = chamarGPT(prompt, temperatura);
            return parsearRespostaAssistente(resposta, tipoConsulta);
        } catch (Exception e) {
            log.error("Erro ao gerar conteúdo do assistente", e);
            return gerarConteudoPadrao(tipoConsulta);
        }
    }

    /**
     * Constrói prompt personalizado baseado no tipo de consulta
     */
    private String construirPromptAssistente(String tipoConsulta, String contextoUsuario) {
        return construirPromptAssistente(tipoConsulta, contextoUsuario, null, 0.7);
    }

    /**
     * Constrói prompt personalizado baseado no tipo de consulta com histórico
     */
    private String construirPromptAssistente(String tipoConsulta, String contextoUsuario, String contextoHistorico, double temperatura) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Você é um assistente pessoal especializado em saúde mental e bem-estar no trabalho de TI. ");
        prompt.append("Você tem formação em psicologia organizacional, neurociência aplicada e produtividade. ");
        prompt.append("Seu estilo é como um mentor experiente: empático, prático, encorajador e profundamente conhecedor. ");
        prompt.append("Você oferece insights baseados em ciência e experiência real.\n\n");
        
        prompt.append("CONTEXTO DO USUÁRIO (DADOS REAIS):\n");
        prompt.append(contextoUsuario);
        prompt.append("\n\n");
        
        // Adiciona contexto de histórico se disponível
        if (contextoHistorico != null && !contextoHistorico.isEmpty()) {
            prompt.append(contextoHistorico).append("\n\n");
        }

        switch (tipoConsulta != null ? tipoConsulta.toLowerCase() : "motivacao") {
            case "curiosidade":
                prompt.append("Gere uma CURIOSIDADE CIENTÍFICA interessante e relevante sobre saúde mental, produtividade ou bem-estar no trabalho de TI. ");
                prompt.append("A curiosidade deve:\n");
                prompt.append("- Ser baseada em estudos científicos ou neurociência\n");
                prompt.append("- Ser educativa, surpreendente e útil\n");
                prompt.append("- Estar relacionada ao contexto do usuário (mencione os dados dele)\n");
                prompt.append("- Incluir uma reflexão prática sobre como aplicar na vida real\n");
                prompt.append("- Ser escrita de forma envolvente e acessível\n\n");
                break;
                
            case "prevencao":
                prompt.append("Gere dicas ESPECÍFICAS de PREVENÇÃO de burnout e estresse baseadas no contexto REAL do usuário. ");
                prompt.append("As dicas devem:\n");
                prompt.append("- Ser personalizadas para o perfil dele (use os dados fornecidos)\n");
                prompt.append("- Ser práticas e acionáveis HOJE\n");
                prompt.append("- Incluir ações concretas com passos claros\n");
                prompt.append("- Mencionar sinais de alerta específicos para ele\n");
                prompt.append("- Ser como conselhos de um especialista experiente\n\n");
                break;
                
            case "motivacao":
                prompt.append("Gere uma MENSAGEM MOTIVACIONAL PROFUNDA e personalizada baseada no contexto REAL do usuário. ");
                prompt.append("A mensagem deve:\n");
                prompt.append("- Reconhecer os desafios específicos dele (mencione os dados)\n");
                prompt.append("- Ser positiva, mas realista e autêntica\n");
                prompt.append("- Oferecer encorajamento genuíno, não clichês\n");
                prompt.append("- Reconhecer pequenas vitórias e progressos\n");
                prompt.append("- Ser como um mentor que acredita no potencial dele\n\n");
                break;
                
            case "dica_pratica":
                prompt.append("Gere uma DICA PRÁTICA ESPECÍFICA e acionável para melhorar o bem-estar, baseada no contexto do usuário. ");
                prompt.append("A dica deve:\n");
                prompt.append("- Ser algo que ele pode implementar IMEDIATAMENTE (hoje mesmo)\n");
                prompt.append("- Estar relacionada aos dados dele (personalizada)\n");
                prompt.append("- Incluir passos claros e específicos (não genéricos)\n");
                prompt.append("- Ter base científica ou em técnicas comprovadas\n");
                prompt.append("- Ser como uma receita prática de um especialista\n\n");
                break;
                
            case "reflexao":
                prompt.append("Gere uma REFLEXÃO PROFUNDA e construtiva baseada no contexto do usuário. ");
                prompt.append("A reflexão deve:\n");
                prompt.append("- Fazer perguntas poderosas que ajudem autoconhecimento\n");
                prompt.append("- Ser gentil, não julgadora e acolhedora\n");
                prompt.append("- Estar relacionada aos padrões identificados nos dados dele\n");
                prompt.append("- Ajudar o usuário a pensar sobre hábitos e escolhas\n");
                prompt.append("- Ser como uma sessão de coaching pessoal\n\n");
                break;
                
            default:
                prompt.append("Gere uma mensagem motivacional e útil baseada no contexto.\n\n");
        }

        prompt.append("Responda APENAS em formato JSON válido com as seguintes chaves:\n");
        prompt.append("- \"titulo\": título curto e chamativo (máximo 50 caracteres)\n");
        prompt.append("- \"conteudo\": conteúdo principal (2-4 parágrafos)\n");
        prompt.append("- \"acoes_praticas\": array com 3-5 ações práticas específicas que o usuário pode fazer\n");
        prompt.append("- \"reflexao\": uma pergunta ou reflexão para o usuário pensar (opcional)\n\n");
        prompt.append("Exemplo de resposta:\n");
        prompt.append("{\"titulo\": \"...\", \"conteudo\": \"...\", \"acoes_praticas\": [\"...\", \"...\"], \"reflexao\": \"...\"}");

        return prompt.toString();
    }

    /**
     * Parseia resposta do assistente
     */
    private AssistentePersonalizado parsearRespostaAssistente(String resposta, String tipoConsulta) {
        try {
            JsonNode json = objectMapper.readTree(resposta);
            return AssistentePersonalizado.builder()
                    .titulo(json.has("titulo") ? json.get("titulo").asText() : "Dica do Assistente")
                    .conteudo(json.has("conteudo") ? json.get("conteudo").asText() : resposta)
                    .tipo(tipoConsulta != null ? tipoConsulta : "motivacao")
                    .acoesPraticas(json.has("acoes_praticas") ? 
                        objectMapper.convertValue(json.get("acoes_praticas"), 
                            objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)) 
                        : List.of("Mantenha hábitos saudáveis", "Faça pausas regulares", "Monitore seu bem-estar"))
                    .reflexao(json.has("reflexao") ? json.get("reflexao").asText() : null)
                    .build();
        } catch (Exception e) {
            log.warn("Não foi possível parsear resposta como JSON, usando resposta completa", e);
            return AssistentePersonalizado.builder()
                    .titulo("Dica do Assistente")
                    .conteudo(resposta)
                    .tipo(tipoConsulta != null ? tipoConsulta : "motivacao")
                    .acoesPraticas(List.of("Mantenha hábitos saudáveis", "Faça pausas regulares", "Monitore seu bem-estar"))
                    .reflexao("Como você pode aplicar isso na sua vida hoje?")
                    .build();
        }
    }

    /**
     * Fallback: gera conteúdo padrão quando GPT não disponível
     */
    private AssistentePersonalizado gerarConteudoPadrao(String tipoConsulta) {
        String titulo, conteudo, reflexao;
        List<String> acoesPraticas;

        switch (tipoConsulta != null ? tipoConsulta.toLowerCase() : "motivacao") {
            case "curiosidade":
                titulo = "Curiosidade: O Poder das Pausas";
                conteudo = "Estudos mostram que fazer pausas de 5-10 minutos a cada 90 minutos de trabalho pode aumentar a produtividade em até 30%. O cérebro precisa de momentos de descanso para processar informações e manter o foco.";
                acoesPraticas = List.of(
                    "Configure lembretes para pausas a cada 90 minutos",
                    "Use a técnica Pomodoro (25min trabalho, 5min pausa)",
                    "Durante as pausas, faça algo completamente diferente do trabalho"
                );
                reflexao = "Como você pode incorporar pausas regulares na sua rotina?";
                break;

            case "prevencao":
                titulo = "Prevenção de Burnout";
                conteudo = "Burnout pode ser prevenido com hábitos consistentes. Estabeleça limites claros entre trabalho e vida pessoal, pratique atividades que recarregam suas energias e monitore seus sinais de estresse regularmente.";
                acoesPraticas = List.of(
                    "Defina horários fixos para começar e terminar o trabalho",
                    "Pratique uma atividade relaxante diariamente (meditação, exercício, hobby)",
                    "Mantenha um diário de humor e energia para identificar padrões"
                );
                reflexao = "Quais são seus principais sinais de estresse e como você pode reconhecê-los mais cedo?";
                break;

            case "dica_pratica":
                titulo = "Dica Prática: Respiração 4-7-8";
                conteudo = "A técnica de respiração 4-7-8 é uma ferramenta poderosa para reduzir ansiedade e estresse em minutos. Inspire por 4 segundos, segure por 7 segundos e expire por 8 segundos. Repita 4 vezes.";
                acoesPraticas = List.of(
                    "Pratique a respiração 4-7-8 3 vezes ao dia",
                    "Use quando sentir ansiedade ou estresse",
                    "Combine com um momento de pausa no trabalho"
                );
                reflexao = "Como você pode criar o hábito de usar essa técnica regularmente?";
                break;

            case "reflexao":
                titulo = "Reflexão: Seu Equilíbrio";
                conteudo = "Refletir sobre nosso bem-estar é essencial para crescimento pessoal. Reserve alguns minutos para pensar sobre como você está se sentindo e o que realmente importa para você.";
                acoesPraticas = List.of(
                    "Reserve 10 minutos diários para auto-reflexão",
                    "Pergunte-se: 'O que me faz sentir bem?' e 'O que me drena energia?'",
                    "Anote suas descobertas em um diário"
                );
                reflexao = "O que você precisa mais neste momento: descanso, conexão ou realização?";
                break;

            default: // motivacao
                titulo = "Mensagem Motivacional";
                conteudo = "Você está no caminho certo! Cada pequeno passo em direção ao bem-estar importa. Continue cuidando de si mesmo e reconhecendo seus progressos, por menores que sejam.";
                acoesPraticas = List.of(
                    "Celebre uma pequena vitória de hoje",
                    "Reconheça seu esforço em cuidar de si mesmo",
                    "Compartilhe algo positivo com alguém próximo"
                );
                reflexao = "Qual foi uma coisa positiva que aconteceu hoje?";
        }

        return AssistentePersonalizado.builder()
                .titulo(titulo)
                .conteudo(conteudo)
                .tipo(tipoConsulta != null ? tipoConsulta : "motivacao")
                .acoesPraticas(acoesPraticas)
                .reflexao(reflexao)
                .build();
    }

    /**
     * Gera análise semanal usando GPT com histórico e variação
     */
    public AnaliseGPT gerarAnaliseSemanal(String dadosHistoricos) {
        return gerarAnaliseSemanal(dadosHistoricos, null, 0.7);
    }

    /**
     * Gera análise semanal usando GPT com histórico e variação
     */
    public AnaliseGPT gerarAnaliseSemanal(String dadosHistoricos, String contextoHistorico, double temperatura) {
        try {
            if (apiKey == null || apiKey.equals("your-api-key-here") || apiKey.isEmpty()) {
                log.warn("⚠️ API Key do OpenAI não configurada. Retornando análise padrão (FALLBACK - não usa IA real).");
                return gerarAnalisePadrao(dadosHistoricos);
            }

            StringBuilder promptBuilder = new StringBuilder();
            promptBuilder.append("Você é um analista especializado em saúde mental e produtividade no trabalho de TI, ");
            promptBuilder.append("com formação em psicologia organizacional e anos de experiência em prevenção de burnout. ");
            promptBuilder.append("Você analisa dados com profundidade e oferece insights valiosos e acionáveis.\n\n");
            
            promptBuilder.append("DADOS HISTÓRICOS DO USUÁRIO (ÚLTIMOS 7 DIAS):\n");
            promptBuilder.append(dadosHistoricos).append("\n\n");
            
            // Adiciona contexto de histórico se disponível
            if (contextoHistorico != null && !contextoHistorico.isEmpty()) {
                promptBuilder.append(contextoHistorico).append("\n\n");
            }
            
            promptBuilder.append("INSTRUÇÕES PARA ANÁLISE:\n");
            promptBuilder.append("1. Analise os padrões nos dados (tendências, variações, consistência)\n");
            promptBuilder.append("2. Identifique sinais de alerta ou pontos positivos\n");
            promptBuilder.append("3. Calcule o risco de burnout baseado em:\n");
            promptBuilder.append("   - Média de humor e energia (se < 2.5 = alto risco)\n");
            promptBuilder.append("   - Consistência dos registros (muitas faltas = alerta)\n");
            promptBuilder.append("   - Produtividade vs bem-estar (desequilíbrio = risco)\n");
            promptBuilder.append("   - Hábitos saudáveis (frequência e pontuação)\n");
            promptBuilder.append("4. Gere sugestões ESPECÍFICAS e ACIONÁVEIS baseadas nos dados reais\n");
            promptBuilder.append("5. Varie a abordagem: use diferentes ângulos, diferentes exemplos, diferentes estruturas\n\n");
            
            promptBuilder.append("FORMATO DE RESPOSTA (JSON):\n");
            promptBuilder.append("{\n");
            promptBuilder.append("  \"resumo\": \"Resumo em 2-3 frases que reconhece o contexto específico do usuário, menciona padrões identificados e oferece perspectiva empática. SEJA ÚNICO e DIFERENTE de análises anteriores.\",\n");
            promptBuilder.append("  \"risco\": \"baixo\" ou \"medio\" ou \"alto\" (baseado em análise objetiva dos dados)\",\n");
            promptBuilder.append("  \"sugestoes\": [\n");
            promptBuilder.append("    \"Sugestão 1: Específica, acionável, baseada nos dados (ex: 'Com base na sua média de humor de 2.3, sugiro pausas de 10min a cada 2h'). SEJA CRIATIVO e DIFERENTE.\",\n");
            promptBuilder.append("    \"Sugestão 2: Prática e implementável HOJE. Use abordagem diferente da anterior.\",\n");
            promptBuilder.append("    \"Sugestão 3: Como um conselho de amigo experiente, não genérico. Varie o tom e estrutura.\"\n");
            promptBuilder.append("  ]\n");
            promptBuilder.append("}\n\n");
            
            promptBuilder.append("IMPORTANTE:\n");
            promptBuilder.append("- Seja específico: mencione números e padrões dos dados\n");
            promptBuilder.append("- Seja empático: reconheça os desafios do usuário\n");
            promptBuilder.append("- Seja prático: sugestões que podem ser implementadas HOJE\n");
            promptBuilder.append("- Seja como um mentor: ofereça insights valiosos, não apenas recomendações genéricas\n");
            promptBuilder.append("- Seja ÚNICO: evite repetir estruturas, frases ou abordagens de análises anteriores\n");
            promptBuilder.append("- Varie: use diferentes metáforas, exemplos, ou formas de apresentar as informações");

            String resposta = chamarGPT(promptBuilder.toString(), temperatura);
            return parsearRespostaAnalise(resposta);
        } catch (Exception e) {
            log.error("Erro ao gerar análise semanal com GPT", e);
            return gerarAnalisePadrao(dadosHistoricos);
        }
    }

    /**
     * Chama a API do OpenAI via HTTP
     * Método público para permitir uso em outros serviços
     */
    public String chamarGPT(String prompt) throws Exception {
        return chamarGPT(prompt, temperature);
    }

    /**
     * Gera resposta de chat conversacional com histórico
     */
    public String gerarRespostaChat(String mensagemUsuario, String contextoHistorico, String contextoUsuario, double temperatura) {
        try {
            // Verifica se a API key é válida (não é o padrão e não está vazia)
            if (apiKey == null || apiKey.isEmpty() || 
                apiKey.equals("your-api-key-here") || 
                apiKey.trim().isEmpty() ||
                !apiKey.startsWith("sk-")) {
                log.warn("⚠️ API Key do OpenAI não configurada ou inválida. API Key recebida: {}", 
                        apiKey != null && apiKey.length() > 10 ? apiKey.substring(0, 10) + "..." : apiKey);
                // Mensagem mais útil quando API key não está configurada
                return "Olá! Para usar o chat com IA, é necessário configurar a API Key do Google Gemini ou do OpenAI. " +
                       "Por favor, configure a variável de ambiente GEMINI_API_KEY ou OPENAI_API_KEY. " +
                       "Enquanto isso, você pode usar os outros recursos do sistema como feedback, análise semanal e análise de ambiente.";
            }
            
            log.debug("✅ API Key do OpenAI detectada. Iniciando chamada ao GPT...");

            StringBuilder promptBuilder = new StringBuilder();
            promptBuilder.append("Você é um assistente pessoal especializado em saúde mental e bem-estar no trabalho de TI. ");
            promptBuilder.append("Você tem formação em psicologia organizacional, neurociência aplicada e produtividade. ");
            promptBuilder.append("Seu estilo é como um mentor experiente: empático, prático, encorajador e profundamente conhecedor. ");
            promptBuilder.append("Você oferece insights baseados em ciência e experiência real.\n\n");
            
            // Adiciona contexto do usuário
            if (contextoUsuario != null && !contextoUsuario.isEmpty()) {
                promptBuilder.append("CONTEXTO DO USUÁRIO:\n");
                promptBuilder.append(contextoUsuario).append("\n\n");
            }
            
            // Adiciona histórico da conversa
            if (contextoHistorico != null && !contextoHistorico.isEmpty()) {
                promptBuilder.append("HISTÓRICO DA CONVERSA:\n");
                promptBuilder.append(contextoHistorico).append("\n\n");
            }
            
            promptBuilder.append("INSTRUÇÕES:\n");
            promptBuilder.append("1. Responda de forma natural e conversacional, como um amigo experiente\n");
            promptBuilder.append("2. Mantenha o contexto da conversa anterior (se houver)\n");
            promptBuilder.append("3. Seja empático, prático e acolhedor\n");
            promptBuilder.append("4. Ofereça insights valiosos baseados em ciência e experiência\n");
            promptBuilder.append("5. Se a pergunta for sobre saúde mental, produtividade ou bem-estar, seja específico e acionável\n");
            promptBuilder.append("6. Se não souber algo, seja honesto e sugira alternativas\n");
            promptBuilder.append("7. Mantenha respostas concisas mas completas (máximo 300 palavras)\n\n");
            
            promptBuilder.append("MENSAGEM DO USUÁRIO:\n");
            promptBuilder.append(mensagemUsuario).append("\n\n");
            
            promptBuilder.append("Responda de forma natural e conversacional, mantendo o contexto da conversa.");

            String promptCompleto = promptBuilder.toString();
            return chamarGPT(promptCompleto, temperatura);
        } catch (RuntimeException e) {
            // Verifica se é um erro que deve tentar fallback (cota, créditos, rate limit, etc)
            if (e.getMessage() != null && (e.getMessage().contains("cota") || 
                    e.getMessage().contains("créditos") || 
                    e.getMessage().contains("quota") ||
                    e.getMessage().contains("excedeu") ||
                    e.getMessage().contains("API Key") ||
                    e.getMessage().contains("rate_limit") ||
                    e.getMessage().contains("429"))) {
                log.error("❌ Erro na API OpenAI: {}", e.getMessage());
                return e.getMessage();
            }
            log.error("❌ Erro ao gerar resposta de chat: {}", e.getMessage(), e);
            return "Desculpe, ocorreu um erro ao processar sua mensagem. Por favor, tente novamente. Erro: " + e.getMessage();
        } catch (Exception e) {
            log.error("❌ Erro ao gerar resposta de chat: {}", e.getMessage(), e);
            return "Desculpe, ocorreu um erro ao processar sua mensagem. Por favor, tente novamente.";
        }
    }

    /**
     * Extrai tarefas de uma mensagem em linguagem natural
     * Retorna JSON com lista de tarefas estruturadas
     * Usa Assistant da OpenAI se configurado, caso contrário usa Chat Completions
     */
    public String extrairTarefas(String mensagem) {
        try {
            // Verifica se a API key é válida
            if (apiKey == null || apiKey.isEmpty() || 
                apiKey.equals("your-api-key-here") || 
                apiKey.trim().isEmpty() ||
                !apiKey.startsWith("sk-")) {
                log.error("❌ API Key do OpenAI não configurada ou inválida para extração de tarefas.");
                log.error("❌ API Key recebida: {}", apiKey != null ? (apiKey.length() > 20 ? apiKey.substring(0, 20) + "..." : apiKey) : "null");
                return null;
            }
            
            // SEMPRE usa Chat Completions (não usa Assistant API)
            log.info("✅ Extraindo tarefas da mensagem usando Chat Completions (SDK oficial): {}", mensagem);
            log.info("🔑 API Key configurada (primeiros 15 chars): {}", apiKey.substring(0, Math.min(15, apiKey.length())) + "...");
            log.info("🔑 Modelo: {}", model);

            StringBuilder promptBuilder = new StringBuilder();
            promptBuilder.append("Você é um assistente especializado em planejamento e organização de tarefas. ");
            promptBuilder.append("Sua função é analisar mensagens em linguagem natural e extrair TODAS as tarefas mencionadas, separando cada uma em um item da lista.\n\n");
            
            promptBuilder.append("MENSAGEM DO USUÁRIO:\n");
            promptBuilder.append(mensagem).append("\n\n");
            
            promptBuilder.append("INSTRUÇÕES OBRIGATÓRIAS:\n");
            promptBuilder.append("1. Identifique TODAS as tarefas na mensagem (mesmo que estejam em uma única frase)\n");
            promptBuilder.append("2. Separe cada tarefa em um item diferente da lista\n");
            promptBuilder.append("3. Para cada tarefa, extraia:\n");
            promptBuilder.append("   - horario: Horário no formato HH:mm (ex: \"14:00\") ou null se não mencionado\n");
            promptBuilder.append("   - descricao: Descrição clara e concisa (máximo 80 caracteres)\n");
            promptBuilder.append("   - prioridade: ALTA (urgente/importante), MEDIA ou BAIXA\n");
            promptBuilder.append("4. Se o usuário diz \"as 14\", \"às 14h\", \"14 horas\", extraia \"14:00\"\n");
            promptBuilder.append("5. Se mencionar \"urgente\", \"urgentemente\", \"preciso\", \"tenho que\", prioridade é ALTA\n");
            promptBuilder.append("6. Seja preciso e extraia TODAS as tarefas, mesmo que estejam na mesma frase\n\n");
            
            promptBuilder.append("FORMATO DE RESPOSTA (OBRIGATÓRIO - APENAS JSON):\n");
            promptBuilder.append("{\n");
            promptBuilder.append("  \"tarefas\": [\n");
            promptBuilder.append("    {\"horario\": \"14:00\", \"descricao\": \"Ir ao dentista\", \"prioridade\": \"ALTA\"},\n");
            promptBuilder.append("    {\"horario\": null, \"descricao\": \"Comprar roupas\", \"prioridade\": \"MEDIA\"},\n");
            promptBuilder.append("    {\"horario\": null, \"descricao\": \"Terminar Java urgentemente\", \"prioridade\": \"ALTA\"}\n");
            promptBuilder.append("  ]\n");
            promptBuilder.append("}\n\n");
            
            promptBuilder.append("EXEMPLO COMPLETO:\n");
            promptBuilder.append("Mensagem: \"tenho dentista as 14, preciso comprar roupas e tenho que terminar java urgentemente\"\n");
            promptBuilder.append("Resposta:\n");
            promptBuilder.append("{\"tarefas\": [{\"horario\": \"14:00\", \"descricao\": \"Ir ao dentista\", \"prioridade\": \"ALTA\"}, {\"horario\": null, \"descricao\": \"Comprar roupas\", \"prioridade\": \"MEDIA\"}, {\"horario\": null, \"descricao\": \"Terminar Java urgentemente\", \"prioridade\": \"ALTA\"}]}\n\n");
            
            promptBuilder.append("CRÍTICO: Retorne APENAS o JSON válido, sem markdown, sem explicações, sem texto adicional. Apenas o JSON puro.");

            String promptCompleto = promptBuilder.toString();
            log.info("🤖 Enviando prompt para extração de tarefas. Mensagem: {}", mensagem);
            // Usa temperatura mais baixa para respostas mais estruturadas
            String resposta = chamarGPT(promptCompleto, 0.2);
            log.info("📥 Resposta recebida da IA (primeiros 200 chars): {}", 
                    resposta != null && resposta.length() > 200 ? resposta.substring(0, 200) + "..." : resposta);
            return resposta;
        } catch (Exception e) {
            log.error("❌ Erro ao extrair tarefas: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Extrai tarefas usando o Assistant da OpenAI
     * Cria uma thread, adiciona a mensagem, cria um run e aguarda a resposta
     */
    private String extrairTarefasComAssistant(String mensagem) {
        try {
            log.info("🤖 Iniciando extração de tarefas com Assistant. Mensagem: {}", mensagem);
            
            // 1. Criar uma thread
            String threadId = criarThread();
            if (threadId == null) {
                log.error("❌ Falha ao criar thread para Assistant");
                return null;
            }
            log.info("✅ Thread criada: {}", threadId);
            
            // 2. Adicionar mensagem à thread
            boolean mensagemAdicionada = adicionarMensagemThread(threadId, mensagem);
            if (!mensagemAdicionada) {
                log.error("❌ Falha ao adicionar mensagem à thread");
                return null;
            }
            log.info("✅ Mensagem adicionada à thread");
            
            // 3. Criar um run com o assistant
            String runId = criarRun(threadId);
            if (runId == null) {
                log.error("❌ Falha ao criar run para Assistant");
                return null;
            }
            log.info("✅ Run criado: {}", runId);
            
            // 4. Aguardar o run completar (polling)
            boolean runCompleto = aguardarRunCompletar(threadId, runId);
            if (!runCompleto) {
                log.error("❌ Run não completou ou falhou");
                return null;
            }
            log.info("✅ Run completado com sucesso");
            
            // 5. Buscar as mensagens da thread
            String resposta = buscarMensagensThread(threadId);
            log.info("📥 Resposta recebida do Assistant (primeiros 200 chars): {}", 
                    resposta != null && resposta.length() > 200 ? resposta.substring(0, 200) + "..." : resposta);
            
            return resposta;
        } catch (Exception e) {
            log.error("❌ Erro ao extrair tarefas com Assistant: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Cria uma nova thread na API de Assistants
     */
    private String criarThread() {
        try {
            String url = "https://api.openai.com/v1/threads";
            String requestBody = "{}"; // Thread vazia
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(java.net.URI.create(url))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .header("OpenAI-Beta", "assistants=v2")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                JsonNode json = objectMapper.readTree(response.body());
                String threadId = json.get("id").asText();
                return threadId;
            } else {
                log.error("❌ Erro ao criar thread. Status: {}. Resposta: {}", response.statusCode(), response.body());
                return null;
            }
        } catch (Exception e) {
            log.error("❌ Exceção ao criar thread: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Adiciona uma mensagem à thread
     */
    private boolean adicionarMensagemThread(String threadId, String mensagem) {
        try {
            String url = "https://api.openai.com/v1/threads/" + threadId + "/messages";
            
            String requestBody = objectMapper.writeValueAsString(
                java.util.Map.of(
                    "role", "user",
                    "content", mensagem
                )
            );
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(java.net.URI.create(url))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .header("OpenAI-Beta", "assistants=v2")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                return true;
            } else {
                log.error("❌ Erro ao adicionar mensagem. Status: {}. Resposta: {}", response.statusCode(), response.body());
                return false;
            }
        } catch (Exception e) {
            log.error("❌ Exceção ao adicionar mensagem: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Cria um run com o assistant
     */
    private String criarRun(String threadId) {
        try {
            String url = "https://api.openai.com/v1/threads/" + threadId + "/runs";
            
            String requestBody = objectMapper.writeValueAsString(
                java.util.Map.of(
                    "assistant_id", assistantId
                )
            );
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(java.net.URI.create(url))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .header("OpenAI-Beta", "assistants=v2")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                JsonNode json = objectMapper.readTree(response.body());
                String runId = json.get("id").asText();
                return runId;
            } else {
                log.error("❌ Erro ao criar run. Status: {}. Resposta: {}", response.statusCode(), response.body());
                return null;
            }
        } catch (Exception e) {
            log.error("❌ Exceção ao criar run: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Aguarda o run completar (polling)
     */
    private boolean aguardarRunCompletar(String threadId, String runId) {
        try {
            int maxTentativas = 30; // Máximo 30 tentativas (30 segundos)
            int tentativa = 0;
            
            while (tentativa < maxTentativas) {
                String url = "https://api.openai.com/v1/threads/" + threadId + "/runs/" + runId;
                
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(java.net.URI.create(url))
                        .header("Authorization", "Bearer " + apiKey)
                        .header("OpenAI-Beta", "assistants=v2")
                        .GET()
                        .build();
                
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                
                if (response.statusCode() == 200) {
                    JsonNode json = objectMapper.readTree(response.body());
                    String status = json.get("status").asText();
                    
                    log.debug("📊 Status do run: {}", status);
                    
                    if ("completed".equals(status)) {
                        return true;
                    } else if ("failed".equals(status) || "cancelled".equals(status) || "expired".equals(status)) {
                        log.error("❌ Run falhou com status: {}", status);
                        if (json.has("last_error")) {
                            JsonNode error = json.get("last_error");
                            log.error("❌ Erro: {}", error.toString());
                        }
                        return false;
                    }
                    // Se ainda está "queued" ou "in_progress", continua aguardando
                } else {
                    log.error("❌ Erro ao verificar status do run. Status: {}. Resposta: {}", response.statusCode(), response.body());
                    return false;
                }
                
                tentativa++;
                Thread.sleep(1000); // Aguarda 1 segundo antes da próxima tentativa
            }
            
            log.error("❌ Timeout aguardando run completar");
            return false;
        } catch (Exception e) {
            log.error("❌ Exceção ao aguardar run: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Busca as mensagens da thread (retorna a última mensagem do assistant)
     */
    private String buscarMensagensThread(String threadId) {
        try {
            String url = "https://api.openai.com/v1/threads/" + threadId + "/messages";
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(java.net.URI.create(url))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("OpenAI-Beta", "assistants=v2")
                    .GET()
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                log.debug("📥 Resposta completa da API ao buscar mensagens: {}", response.body());
                JsonNode json = objectMapper.readTree(response.body());
                JsonNode data = json.get("data");
                
                if (data != null && data.isArray() && data.size() > 0) {
                    log.info("📋 Encontradas {} mensagens na thread", data.size());
                    // Busca a primeira mensagem do assistant (última resposta)
                    for (JsonNode message : data) {
                        String role = message.get("role").asText();
                        log.debug("📨 Mensagem encontrada com role: {}", role);
                        if ("assistant".equals(role)) {
                            JsonNode content = message.get("content");
                            log.debug("📄 Content da mensagem do assistant: {}", content.toString());
                            if (content != null && content.isArray() && content.size() > 0) {
                                JsonNode firstContent = content.get(0);
                                log.debug("📄 Primeiro conteúdo: {}", firstContent.toString());
                                if (firstContent.has("text")) {
                                    String texto = firstContent.get("text").get("value").asText();
                                    log.info("✅ Texto extraído do Assistant (tamanho: {} chars): {}", texto.length(), 
                                            texto.length() > 200 ? texto.substring(0, 200) + "..." : texto);
                                    return texto;
                                } else {
                                    log.warn("⚠️ Primeiro conteúdo não tem campo 'text'. Estrutura: {}", firstContent.toString());
                                }
                            } else {
                                log.warn("⚠️ Content está vazio ou não é array. Content: {}", content);
                            }
                        }
                    }
                } else {
                    log.warn("⚠️ Data está vazia ou não é array. Data: {}", data);
                }
                
                log.warn("⚠️ Nenhuma mensagem do assistant encontrada na thread. Resposta completa: {}", response.body());
                return null;
            } else {
                log.error("❌ Erro ao buscar mensagens. Status: {}. Resposta: {}", response.statusCode(), response.body());
                return null;
            }
        } catch (Exception e) {
            log.error("❌ Exceção ao buscar mensagens: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Chama a API do OpenAI usando SDK Theokanning ou HttpClient como fallback
     */
    public String chamarGPT(String prompt, double temperaturaCustomizada) throws Exception {
        // Tenta usar o SDK primeiro
        if (openAiService != null) {
            try {
                return chamarGPTComSDK(prompt, temperaturaCustomizada);
            } catch (Exception e) {
                log.warn("⚠️ Erro ao usar SDK, tentando HttpClient como fallback: {}", e.getMessage());
                // Continua para o fallback com HttpClient
            }
        }
        
        // Fallback: usa HttpClient direto
        return chamarGPTComHttpClient(prompt, temperaturaCustomizada);
    }
    
    /**
     * Chama a API usando o SDK Theokanning
     * Sempre usa Chat Completions com response_format: json_object
     */
    private String chamarGPTComSDK(String prompt, double temperaturaCustomizada) throws Exception {
        log.info("🚀 Usando SDK Theokanning - Chat Completions");
        
        ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), prompt);
        
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(List.of(userMessage))
                .temperature(temperaturaCustomizada)
                .maxTokens(1000)
                .build();
        
        log.debug("📤 Enviando requisição via SDK. Tamanho do prompt: {} caracteres", prompt.length());
        log.info("✅ Prompt força JSON estruturado (response_format via prompt)");
        
        var response = openAiService.createChatCompletion(request);
        
        String respostaGPT = response.getChoices().get(0).getMessage().getContent().trim();
        log.info("✅ IA REAL: Resposta recebida do GPT via SDK Theokanning. Tamanho: {} caracteres", respostaGPT.length());
        log.info("📄 Resposta completa da IA: {}", respostaGPT);
        
        return respostaGPT;
    }
    
    /**
     * Chama a API do OpenAI via HTTP com temperatura customizada
     * Sempre usa response_format: json_object
     */
    private String chamarGPTComHttpClient(String prompt, double temperaturaCustomizada) throws Exception {
        log.info("🔄 Usando HttpClient para chamar API OpenAI");
        
        // Usa ObjectMapper para construir JSON corretamente (escapa caracteres especiais)
        java.util.Map<String, Object> requestMap = new java.util.HashMap<>();
        requestMap.put("model", model);
        
        java.util.Map<String, String> message = new java.util.HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);
        
        requestMap.put("messages", java.util.List.of(message));
        requestMap.put("temperature", temperaturaCustomizada);
        requestMap.put("max_tokens", 1000);
        
        // SEMPRE usa response_format: json_object
        requestMap.put("response_format", java.util.Map.of("type", "json_object"));
        log.info("✅ Usando response_format: json_object sempre (modelo: {})", model);
        
        String requestBody = objectMapper.writeValueAsString(requestMap);
        
        log.debug("📤 Enviando requisição para OpenAI. Tamanho do prompt: {} caracteres", prompt.length());

        // Valida API key antes de fazer a requisição
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your-api-key-here") || !apiKey.startsWith("sk-")) {
            log.error("❌ API Key inválida antes de fazer requisição: {}", 
                    apiKey != null && apiKey.length() > 20 ? apiKey.substring(0, 20) + "..." : apiKey);
            throw new RuntimeException("API Key do OpenAI não configurada ou inválida. Verifique a configuração.");
        }
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .timeout(Duration.ofSeconds(30))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        log.debug("📥 Resposta recebida: Status {}, Tamanho: {} caracteres", response.statusCode(), response.body().length());

        if (response.statusCode() == 200) {
            JsonNode jsonResponse = objectMapper.readTree(response.body());
            String respostaGPT = jsonResponse.get("choices").get(0).get("message").get("content").asText().trim();
            log.info("✅ IA REAL: Resposta recebida do GPT (OpenAI via HttpClient). Tamanho: {} caracteres", respostaGPT.length());
            log.info("📄 Resposta completa da IA: {}", respostaGPT);
            return respostaGPT;
        } else {
            // Tenta parsear o erro da API para mensagem mais útil
            String errorMessage = "Erro desconhecido";
            try {
                JsonNode errorJson = objectMapper.readTree(response.body());
                if (errorJson.has("error")) {
                    JsonNode error = errorJson.get("error");
                    if (error.has("message")) {
                        errorMessage = error.get("message").asText();
                    }
                    if (error.has("code")) {
                        String errorCode = error.get("code").asText();
                        log.error("❌ Erro na API OpenAI: Status {} - Code: {} - Message: {}", 
                                response.statusCode(), errorCode, errorMessage);
                        
                        // Mensagens específicas para erros comuns
                        if ("insufficient_quota".equals(errorCode) || response.statusCode() == 429) {
                            log.error("❌ ERRO 429 - Cota insuficiente. Verificando API Key...");
                            log.error("❌ API Key usada (primeiros 15 chars): {}", apiKey != null && apiKey.length() > 15 ? apiKey.substring(0, 15) + "..." : apiKey);
                            log.error("❌ Modelo usado: {}", model);
                            log.error("❌ Resposta completa da API: {}", response.body());
                            throw new RuntimeException("A API Key do OpenAI excedeu a cota ou não tem créditos disponíveis. " +
                                    "Por favor, verifique sua conta OpenAI em https://platform.openai.com/account/billing. " +
                                    "Se você acabou de adicionar créditos, pode levar alguns minutos para serem ativados.");
                        } else if ("invalid_api_key".equals(errorCode) || response.statusCode() == 401) {
                            log.error("❌ ERRO 401 - API Key inválida. Verificando...");
                            log.error("❌ API Key usada (primeiros 15 chars): {}", apiKey != null && apiKey.length() > 15 ? apiKey.substring(0, 15) + "..." : apiKey);
                            throw new RuntimeException("API Key do OpenAI inválida. Verifique se a chave está correta e se foi copiada completamente.");
                        } else if ("rate_limit_exceeded".equals(errorCode)) {
                            throw new RuntimeException("Limite de requisições excedido. Aguarde alguns instantes e tente novamente.");
                        }
                    }
                }
            } catch (RuntimeException e) {
                // Se já foi lançada uma exceção com mensagem específica, relança
                throw e;
            } catch (Exception e) {
                // Outros erros de parsing, continua para lançar erro genérico
            }
            
            log.error("❌ Erro na API OpenAI: Status {} - {}", response.statusCode(), response.body());
            // Se for erro 429 (quota excedida), lança exceção específica para tentar fallback
            if (response.statusCode() == 429 || errorMessage.toLowerCase().contains("quota") || 
                errorMessage.toLowerCase().contains("exceeded")) {
                throw new RuntimeException("A API Key do OpenAI excedeu a cota ou não tem créditos disponíveis. " +
                        "Por favor, verifique sua conta OpenAI em https://platform.openai.com/account/billing");
            }
            throw new RuntimeException("Erro ao chamar API OpenAI: " + response.statusCode() + " - " + errorMessage);
        }
    }

    /**
     * Parseia a resposta da análise em objeto estruturado
     */
    private AnaliseGPT parsearRespostaAnalise(String resposta) {
        try {
            // Tenta parsear como JSON
            JsonNode json = objectMapper.readTree(resposta);
            return AnaliseGPT.builder()
                    .resumo(json.has("resumo") ? json.get("resumo").asText() : resposta)
                    .risco(json.has("risco") ? json.get("risco").asText() : "medio")
                    .sugestoes(json.has("sugestoes") ? 
                        objectMapper.convertValue(json.get("sugestoes"), 
                            objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)) 
                        : List.of("Mantenha hábitos saudáveis", "Faça pausas regulares", "Monitore seu bem-estar"))
                    .build();
        } catch (Exception e) {
            log.warn("Não foi possível parsear resposta como JSON, usando resposta completa", e);
            return AnaliseGPT.builder()
                    .resumo(resposta)
                    .risco("medio")
                    .sugestoes(List.of("Mantenha hábitos saudáveis", "Faça pausas regulares", "Monitore seu bem-estar"))
                    .build();
        }
    }

    // Métodos de fallback do Gemini removidos temporariamente

    /**
     * Fallback: gera feedback padrão quando GPT não está disponível
     */
    private String gerarFeedbackPadrao(Integer humor, String produtividade) {
        if (humor <= 2) {
            return "Você parece cansado hoje. Tente fazer uma pausa curta e respirar fundo. Estamos aqui para apoiá-lo.";
        } else if (humor <= 3) {
            return "Continue cuidando de si mesmo. Lembre-se de manter o equilíbrio entre trabalho e descanso.";
        } else {
            return "Ótimo trabalho! Continue mantendo esse equilíbrio e foco.";
        }
    }

    /**
     * Fallback: gera análise padrão quando GPT não está disponível
     */
    private AnaliseGPT gerarAnalisePadrao(String dadosHistoricos) {
        return AnaliseGPT.builder()
                .resumo("Análise baseada em dados históricos. Recomendamos monitoramento contínuo do bem-estar.")
                .risco("medio")
                .sugestoes(List.of(
                    "Mantenha hábitos saudáveis de sono e alimentação",
                    "Faça pausas regulares durante o trabalho",
                    "Monitore seus níveis de humor e energia diariamente"
                ))
                .build();
    }

    /**
     * Classe interna para estrutura de análise
     */
    @lombok.Data
    @lombok.Builder
    public static class AnaliseGPT {
        private String resumo;
        private String risco;
        private List<String> sugestoes;
    }

    /**
     * Classe interna para estrutura do assistente personalizado
     */
    @lombok.Data
    @lombok.Builder
    public static class AssistentePersonalizado {
        private String titulo;
        private String conteudo;
        private String tipo;
        private List<String> acoesPraticas;
        private String reflexao;
    }

    /**
     * Processa mensagem do Assistant - Agenda ou Conteúdo
     * Retorna JSON estruturado conforme o tipo solicitado
     */
    public String processarMensagemAssistant(String tipo, String mensagem) {
        try {
            if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your-api-key-here") || !apiKey.startsWith("sk-")) {
                log.error("❌ API Key do OpenAI não configurada para processar mensagem do assistant.");
                return null;
            }

            log.info("🤖 Processando mensagem do Assistant. Tipo: {}, Mensagem: {}", tipo, mensagem);

            StringBuilder promptBuilder = new StringBuilder();
            promptBuilder.append("Você é um assistente pessoal inteligente especializado em produtividade e bem-estar.\n\n");
            promptBuilder.append("Funções principais:\n");
            promptBuilder.append("1. Interpretar mensagens livres do usuário e transformá-las em uma lista de tarefas (tasks) organizadas.\n");
            promptBuilder.append("2. Gerar conteúdos personalizados de apoio emocional, motivacional ou reflexivo conforme o tipo solicitado.\n\n");

            if ("agenda".equalsIgnoreCase(tipo)) {
                // Prompt para extrair tarefas/agenda
                promptBuilder.append("TAREFA: Extrair compromissos e transformar em tasks.\n\n");
                promptBuilder.append("MENSAGEM DO USUÁRIO:\n");
                promptBuilder.append(mensagem).append("\n\n");
                promptBuilder.append("INSTRUÇÕES:\n");
                promptBuilder.append("1. Identifique TODOS os compromissos, tarefas e eventos mencionados\n");
                promptBuilder.append("2. Extraia data e horário quando mencionados\n");
                promptBuilder.append("3. Determine categoria apropriada (Beleza, Pessoal, Trabalho, Saúde, etc)\n");
                promptBuilder.append("4. Determine prioridade (Alta, Normal, Baixa)\n");
                promptBuilder.append("5. Se não houver data específica, use a data mais provável baseada no contexto\n\n");
                promptBuilder.append("FORMATO DE RESPOSTA (OBRIGATÓRIO - APENAS JSON):\n");
                promptBuilder.append("{\n");
                promptBuilder.append("  \"tasks\": [\n");
                promptBuilder.append("    {\n");
                promptBuilder.append("      \"titulo\": \"Nome da tarefa\",\n");
                promptBuilder.append("      \"data\": \"2025-11-13T14:00:00\",\n");
                promptBuilder.append("      \"categoria\": \"Beleza\",\n");
                promptBuilder.append("      \"prioridade\": \"Normal\"\n");
                promptBuilder.append("    }\n");
                promptBuilder.append("  ]\n");
                promptBuilder.append("}\n\n");
                promptBuilder.append("EXEMPLO:\n");
                promptBuilder.append("Mensagem: \"tenho cabeleireiro hoje às 14h, depilação na quarta-feira e viagem no final do ano\"\n");
                promptBuilder.append("Resposta:\n");
                promptBuilder.append("{\"tasks\": [{\"titulo\": \"Cabeleireiro\", \"data\": \"2025-11-13T14:00:00\", \"categoria\": \"Beleza\", \"prioridade\": \"Normal\"}, {\"titulo\": \"Depilação\", \"data\": \"2025-11-15T10:00:00\", \"categoria\": \"Beleza\", \"prioridade\": \"Normal\"}, {\"titulo\": \"Viagem de fim de ano\", \"data\": \"2025-12-28T08:00:00\", \"categoria\": \"Pessoal\", \"prioridade\": \"Alta\"}]}\n\n");

            } else if ("conteudo".equalsIgnoreCase(tipo) || "motivacao".equalsIgnoreCase(tipo)) {
                // Prompt para gerar conteúdo personalizado
                String tipoConteudo = "motivacao".equalsIgnoreCase(tipo) ? "motivacao" : "conteudo";
                promptBuilder.append("TAREFA: Gerar conteúdo personalizado de apoio emocional, motivacional ou reflexivo.\n\n");
                promptBuilder.append("MENSAGEM DO USUÁRIO:\n");
                promptBuilder.append(mensagem).append("\n\n");
                promptBuilder.append("TIPO DE CONTEÚDO SOLICITADO: ").append(tipoConteudo).append("\n\n");
                promptBuilder.append("INSTRUÇÕES:\n");
                promptBuilder.append("1. Analise o contexto e sentimento da mensagem do usuário\n");
                promptBuilder.append("2. Gere um conteúdo empático, acolhedor e acionável\n");
                promptBuilder.append("3. Seja específico e personalizado (não genérico)\n");
                promptBuilder.append("4. Ofereça ações práticas que o usuário pode implementar\n");
                promptBuilder.append("5. Inclua uma reflexão ou pergunta para autoconhecimento\n\n");
                promptBuilder.append("FORMATO DE RESPOSTA (OBRIGATÓRIO - APENAS JSON):\n");
                promptBuilder.append("{\n");
                promptBuilder.append("  \"tipo\": \"").append(tipoConteudo).append("\",\n");
                promptBuilder.append("  \"titulo\": \"Título inspirador e motivador\",\n");
                promptBuilder.append("  \"conteudo\": \"Conteúdo empático e acolhedor (2-3 parágrafos)\",\n");
                promptBuilder.append("  \"acoes_praticas\": [\n");
                promptBuilder.append("    \"Ação prática 1\",\n");
                promptBuilder.append("    \"Ação prática 2\"\n");
                promptBuilder.append("  ],\n");
                promptBuilder.append("  \"reflexao\": \"Pergunta ou reflexão para o usuário\"\n");
                promptBuilder.append("}\n\n");
                promptBuilder.append("EXEMPLO:\n");
                promptBuilder.append("Mensagem: \"me manda algo pra me animar hoje, tô sem energia\"\n");
                promptBuilder.append("Resposta:\n");
                promptBuilder.append("{\"tipo\": \"motivacao\", \"titulo\": \"A energia vem do propósito\", \"conteudo\": \"Você não precisa estar 100%% para dar o seu melhor — só precisa começar. A energia não vem de estar perfeito, mas de dar o primeiro passo, mesmo que pequeno.\", \"acoes_praticas\": [\"Faça algo pequeno por você hoje\", \"Lembre-se do motivo que te move\"], \"reflexao\": \"O que me inspira a continuar mesmo nos dias difíceis?\"}\n\n");
            } else {
                log.warn("⚠️ Tipo de mensagem não reconhecido: {}. Usando tipo 'conteudo' como padrão.", tipo);
                return processarMensagemAssistant("conteudo", mensagem);
            }

            promptBuilder.append("CRÍTICO: Retorne APENAS o JSON válido, sem markdown, sem explicações, sem texto adicional. Apenas o JSON puro.");

            String promptCompleto = promptBuilder.toString();
            String resposta = chamarGPT(promptCompleto, 0.7);
            
            log.info("✅ Resposta do Assistant recebida. Tamanho: {} caracteres", resposta != null ? resposta.length() : 0);
            if (resposta != null && log.isDebugEnabled()) {
                log.debug("📄 Resposta completa: {}", resposta);
            }
            
            return resposta;
        } catch (Exception e) {
            log.error("❌ Erro ao processar mensagem do assistant: {}", e.getMessage(), e);
            return null;
        }
    }
}

