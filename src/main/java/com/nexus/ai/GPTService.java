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
                "Você é um assistente de saúde mental profissional. " +
                "Gere uma mensagem curta, empática e profissional (máximo 150 caracteres) " +
                "para um usuário com humor=%d/5 e produtividade=%s. " +
                "Seja positivo, encorajador e ofereça uma sugestão prática. " +
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
     * Gera análise semanal usando GPT
     */
    public AnaliseGPT gerarAnaliseSemanal(String dadosHistoricos) {
        try {
            if (apiKey == null || apiKey.equals("your-api-key-here") || apiKey.isEmpty()) {
                log.warn("API Key do OpenAI não configurada. Retornando análise padrão.");
                return gerarAnalisePadrao(dadosHistoricos);
            }

            String prompt = String.format(
                "Você é um analista de saúde mental e produtividade. " +
                "Analise os seguintes dados históricos de um usuário e gere uma análise estruturada:\n\n" +
                "%s\n\n" +
                "Responda APENAS em formato JSON válido com as seguintes chaves:\n" +
                "- \"resumo\": resumo semanal em 2-3 frases\n" +
                "- \"risco\": nível de risco de burnout (\"baixo\", \"medio\" ou \"alto\")\n" +
                "- \"sugestoes\": array com 3 sugestões práticas e específicas\n\n" +
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
}

