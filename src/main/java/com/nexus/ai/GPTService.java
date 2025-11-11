package com.nexus.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Serviço para integração com GPT via API OpenAI
 * Funciona sem Spring AI, usando HttpClient diretamente
 */
@Service
@Slf4j
public class GPTService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    private String apiKey;
    private String model;
    private Double temperature;

    public GPTService(
            @Value("${spring.ai.openai.api-key:your-api-key-here}") String apiKey,
            @Value("${spring.ai.openai.chat.options.model:gpt-3.5-turbo}") String model,
            @Value("${spring.ai.openai.chat.options.temperature:0.7}") Double temperature) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        this.objectMapper = new ObjectMapper();
        this.apiKey = apiKey;
        this.model = model;
        this.temperature = temperature;
    }

    /**
     * Gera feedback empático usando GPT
     */
    public String gerarFeedbackEmpatico(Integer humor, String produtividade) {
        try {
            if (apiKey == null || apiKey.equals("your-api-key-here") || apiKey.isEmpty()) {
                log.warn("API Key do OpenAI não configurada. Retornando feedback padrão.");
                return gerarFeedbackPadrao(humor, produtividade);
            }

            String prompt = String.format(
                "Você é um assistente pessoal de saúde mental e bem-estar no trabalho de TI. " +
                "Gere uma mensagem curta, empática e profissional (máximo 150 caracteres) " +
                "para um usuário com humor=%d/5 e produtividade=%s. " +
                "Seja positivo, encorajador, prático e ofereça uma sugestão acionável. " +
                "A mensagem deve ser como um ajudante pessoal que realmente se importa. " +
                "Responda APENAS com a mensagem, sem explicações adicionais.",
                humor, produtividade
            );

            return chamarGPT(prompt);
        } catch (Exception e) {
            log.error("Erro ao gerar feedback empático com GPT", e);
            return gerarFeedbackPadrao(humor, produtividade);
        }
    }

    /**
     * Gera conteúdo personalizado do assistente pessoal
     */
    public AssistentePersonalizado gerarConteudoAssistente(String tipoConsulta, String contextoUsuario) {
        try {
            if (apiKey == null || apiKey.equals("your-api-key-here") || apiKey.isEmpty()) {
                log.warn("API Key do OpenAI não configurada. Retornando conteúdo padrão.");
                return gerarConteudoPadrao(tipoConsulta);
            }

            String prompt = construirPromptAssistente(tipoConsulta, contextoUsuario);
            String resposta = chamarGPT(prompt);
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
        StringBuilder prompt = new StringBuilder();
        prompt.append("Você é um assistente pessoal de saúde mental e bem-estar profissional. ");
        prompt.append("Seu objetivo é ajudar pessoas a melhorarem sua qualidade de vida no trabalho de TI. ");
        prompt.append("Seja empático, prático e encorajador.\n\n");
        
        prompt.append("CONTEXTO DO USUÁRIO:\n");
        prompt.append(contextoUsuario);
        prompt.append("\n\n");

        switch (tipoConsulta != null ? tipoConsulta.toLowerCase() : "motivacao") {
            case "curiosidade":
                prompt.append("Gere uma CURIOSIDADE interessante e relevante sobre saúde mental, produtividade ou bem-estar no trabalho de TI. ");
                prompt.append("A curiosidade deve ser educativa, surpreendente e útil. ");
                prompt.append("Inclua uma reflexão sobre como isso pode ser aplicado na vida do usuário.\n\n");
                break;
                
            case "prevencao":
                prompt.append("Gere dicas de PREVENÇÃO de burnout e estresse baseadas no contexto do usuário. ");
                prompt.append("Seja específico e prático. Inclua ações concretas que o usuário pode tomar HOJE.\n\n");
                break;
                
            case "motivacao":
                prompt.append("Gere uma MENSAGEM MOTIVACIONAL personalizada baseada no contexto do usuário. ");
                prompt.append("Seja positivo, mas realista. Reconheça os desafios e ofereça encorajamento genuíno.\n\n");
                break;
                
            case "dica_pratica":
                prompt.append("Gere uma DICA PRÁTICA específica e acionável para melhorar o bem-estar. ");
                prompt.append("A dica deve ser algo que o usuário pode implementar imediatamente. ");
                prompt.append("Inclua passos claros de como aplicar.\n\n");
                break;
                
            case "reflexao":
                prompt.append("Gere uma REFLEXÃO profunda e construtiva baseada no contexto do usuário. ");
                prompt.append("Faça perguntas que ajudem o usuário a pensar sobre seus hábitos e escolhas. ");
                prompt.append("Seja gentil e não julgador.\n\n");
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
     * Gera análise semanal usando GPT
     */
    public AnaliseGPT gerarAnaliseSemanal(String dadosHistoricos) {
        try {
            if (apiKey == null || apiKey.equals("your-api-key-here") || apiKey.isEmpty()) {
                log.warn("API Key do OpenAI não configurada. Retornando análise padrão.");
                return gerarAnalisePadrao(dadosHistoricos);
            }

            String prompt = String.format(
                "Você é um assistente pessoal especializado em saúde mental e produtividade no trabalho de TI. " +
                "Analise os seguintes dados históricos de um usuário e gere uma análise completa e acionável:\n\n" +
                "%s\n\n" +
                "Seja um verdadeiro ajudante pessoal: seja empático, prático e ofereça insights valiosos. " +
                "Responda APENAS em formato JSON válido com as seguintes chaves:\n" +
                "- \"resumo\": resumo semanal em 2-3 frases, reconhecendo o contexto do usuário\n" +
                "- \"risco\": nível de risco de burnout (\"baixo\", \"medio\" ou \"alto\")\n" +
                "- \"sugestoes\": array com 3-5 sugestões práticas, específicas e acionáveis que o usuário pode implementar HOJE\n\n" +
                "As sugestões devem ser como conselhos de um amigo experiente, não apenas recomendações genéricas.\n\n" +
                "Exemplo de resposta:\n" +
                "{\"resumo\": \"...\", \"risco\": \"medio\", \"sugestoes\": [\"...\", \"...\", \"...\"]}",
                dadosHistoricos
            );

            String resposta = chamarGPT(prompt);
            return parsearRespostaAnalise(resposta);
        } catch (Exception e) {
            log.error("Erro ao gerar análise semanal com GPT", e);
            return gerarAnalisePadrao(dadosHistoricos);
        }
    }

    /**
     * Chama a API do OpenAI via HTTP
     */
    private String chamarGPT(String prompt) throws Exception {
        String requestBody = String.format(
            "{\n" +
            "  \"model\": \"%s\",\n" +
            "  \"messages\": [\n" +
            "    {\"role\": \"user\", \"content\": \"%s\"}\n" +
            "  ],\n" +
            "  \"temperature\": %s,\n" +
            "  \"max_tokens\": 500\n" +
            "}",
            model, prompt.replace("\"", "\\\""), temperature
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .timeout(Duration.ofSeconds(30))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            log.error("Erro na API OpenAI: Status {} - {}", response.statusCode(), response.body());
            throw new RuntimeException("Erro ao chamar API OpenAI: " + response.statusCode());
        }

        JsonNode jsonResponse = objectMapper.readTree(response.body());
        return jsonResponse.get("choices").get(0).get("message").get("content").asText().trim();
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
}

