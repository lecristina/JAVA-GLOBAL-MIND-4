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
import java.util.HashMap;
import java.util.Map;

/**
 * Serviço alternativo usando Google Gemini API (gratuita até certo limite)
 * Fallback quando OpenAI não está disponível ou sem créditos
 */
@Service
@Slf4j
public class GeminiService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;

    public GeminiService(
            @Value("${gemini.api-key:}") String apiKey,
            @Value("${gemini.model:gemini-pro}") String model) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        this.objectMapper = new ObjectMapper();
        this.apiKey = apiKey;
        this.model = model;
        
        if (apiKey != null && !apiKey.isEmpty()) {
            log.info("✅ GeminiService inicializado com API Key: {}", 
                    apiKey.length() > 20 ? apiKey.substring(0, 20) + "..." : apiKey);
        } else {
            log.warn("⚠️ GeminiService inicializado SEM API Key. Configure gemini.api-key para usar.");
        }
    }

    /**
     * Gera resposta de chat usando Google Gemini
     */
    public String gerarRespostaChat(String prompt, double temperatura) {
        try {
            if (apiKey == null || apiKey.isEmpty()) {
                log.warn("⚠️ API Key do Gemini não configurada.");
                return null;
            }

            // Construir requisição para Gemini API
            Map<String, Object> requestMap = new HashMap<>();
            Map<String, Object> contents = new HashMap<>();
            Map<String, Object> part = new HashMap<>();
            part.put("text", prompt);
            contents.put("parts", java.util.List.of(part));
            requestMap.put("contents", java.util.List.of(contents));
            
            // Configurações de geração
            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("temperature", temperatura);
            generationConfig.put("maxOutputTokens", 500);
            requestMap.put("generationConfig", generationConfig);

            String requestBody = objectMapper.writeValueAsString(requestMap);

            String url = String.format("https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s", 
                    model, apiKey);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .timeout(Duration.ofSeconds(30))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode jsonResponse = objectMapper.readTree(response.body());
                if (jsonResponse.has("candidates") && jsonResponse.get("candidates").size() > 0) {
                    JsonNode candidate = jsonResponse.get("candidates").get(0);
                    if (candidate.has("content") && candidate.get("content").has("parts")) {
                        JsonNode parts = candidate.get("content").get("parts");
                        if (parts.size() > 0 && parts.get(0).has("text")) {
                            String resposta = parts.get(0).get("text").asText().trim();
                            log.info("✅ Resposta recebida do Gemini. Tamanho: {} caracteres", resposta.length());
                            return resposta;
                        }
                    }
                }
                log.warn("⚠️ Resposta do Gemini não contém texto válido: {}", response.body());
                return null;
            } else {
                log.error("❌ Erro na API Gemini: Status {} - {}", response.statusCode(), response.body());
                return null;
            }
        } catch (Exception e) {
            log.error("❌ Erro ao chamar API Gemini", e);
            return null;
        }
    }

    /**
     * Verifica se o serviço está disponível
     */
    public boolean estaDisponivel() {
        return apiKey != null && !apiKey.isEmpty();
    }
}

