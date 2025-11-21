package com.nexus.modules.ia.service;

import com.nexus.ai.AIService;
import com.nexus.ai.GPTService;
import com.nexus.ai.HistoricoIAService;
import com.nexus.ai.PausaMonitorService;
// import com.nexus.ai.VisionService; // DESABILITADO TEMPORARIAMENTE
import com.nexus.application.dto.AnaliseAmbienteResponseDTO;
import com.nexus.application.dto.AnaliseRequestDTO;
import com.nexus.application.dto.AnaliseResponseDTO;
import com.nexus.application.dto.AssistenteRequestDTO;
import com.nexus.application.dto.AssistenteResponseDTO;
import com.nexus.application.dto.ChatRequestDTO;
import com.nexus.application.dto.ChatResponseDTO;
import com.nexus.application.dto.AssistantAnalisarRequestDTO;
import com.nexus.application.dto.CoPlannerRequestDTO;
import com.nexus.application.dto.CoPlannerResponseDTO;
import com.nexus.application.dto.FeedbackRequestDTO;
import com.nexus.application.dto.FeedbackResponseDTO;
import com.nexus.application.dto.PausaMonitorRequestDTO;
import com.nexus.application.dto.PausaMonitorResponseDTO;
import com.nexus.application.dto.TarefaDTO;
import com.nexus.domain.model.AlertaIA;
import com.nexus.domain.model.ConversaIA;
import com.nexus.domain.model.Humor;
import com.nexus.domain.model.Usuario;
import com.nexus.infrastructure.repository.AlertaIARepository;
import com.nexus.infrastructure.repository.ConversaIARepository;
import com.nexus.infrastructure.repository.HumorRepository;
import com.nexus.infrastructure.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class IAService {

    @Autowired(required = false)
    private AIService aiService;
    // private final VisionService visionService; // DESABILITADO TEMPORARIAMENTE - removido do construtor
    private final HistoricoIAService historicoIAService;
    private final AlertaIARepository alertaIARepository;
    private final ConversaIARepository conversaIARepository;
    private final UsuarioRepository usuarioRepository;
    private final HumorRepository humorRepository;
    private final PausaMonitorService pausaMonitorService;
    
    // Injeção opcional do GPTService
    @Autowired(required = false)
    private GPTService gptService;
    
    // Construtor explícito para evitar problemas com Lombok e DevTools
    public IAService(HistoricoIAService historicoIAService,
                     AlertaIARepository alertaIARepository,
                     ConversaIARepository conversaIARepository,
                     UsuarioRepository usuarioRepository,
                     HumorRepository humorRepository,
                     PausaMonitorService pausaMonitorService) {
        this.historicoIAService = historicoIAService;
        this.alertaIARepository = alertaIARepository;
        this.conversaIARepository = conversaIARepository;
        this.usuarioRepository = usuarioRepository;
        this.humorRepository = humorRepository;
        this.pausaMonitorService = pausaMonitorService;
    }

    @Transactional
    public FeedbackResponseDTO gerarFeedback(FeedbackRequestDTO request) {
        // Busca o usuário
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Integer humor = request.getHumor() != null ? request.getHumor() : 3;
        String produtividade = request.getProdutividade() != null ? request.getProdutividade() : "media";

        // 🧠 MELHORIA: Busca histórico para personalização e variação
        List<String> historico = historicoIAService.buscarHistoricoFeedback(usuario.getIdUsuario(), humor, produtividade);
        String contextoHistorico = historicoIAService.gerarContextoHistorico(historico);
        String variacaoAbordagem = historicoIAService.gerarVariacaoAbordagem(historico);
        double temperatura = historicoIAService.calcularTemperaturaDinamica(historico);

        // Adiciona variação de abordagem ao contexto
        if (variacaoAbordagem != null && !variacaoAbordagem.isEmpty()) {
            contextoHistorico += "\n" + variacaoAbordagem;
        }

        // Gera feedback usando GPT com histórico e variação
        GPTService gptService = getGptService();
        String mensagem;
        if (gptService != null) {
            mensagem = gptService.gerarFeedbackEmpatico(humor, produtividade, contextoHistorico, temperatura);
        } else if (aiService != null) {
            try {
                mensagem = aiService.gerarFeedbackEmpatico(humor, produtividade);
            } catch (Exception e) {
                log.warn("Erro ao usar AIService, usando fallback", e);
                mensagem = "Continue cuidando de si mesmo. Lembre-se de manter o equilíbrio entre trabalho e descanso.";
            }
        } else {
            mensagem = "Continue cuidando de si mesmo. Lembre-se de manter o equilíbrio entre trabalho e descanso.";
        }

        // Calcula nível de risco baseado no humor
        Integer nivelRisco = calcularNivelRisco(request.getHumor());

        // Salva o feedback no banco de dados
        AlertaIA alerta = AlertaIA.builder()
                .usuario(usuario)
                .dataAlerta(LocalDate.now())
                .tipoAlerta("FEEDBACK_EMPATICO")
                .mensagem(mensagem)
                .nivelRisco(nivelRisco)
                .build();

        AlertaIA saved = alertaIARepository.save(alerta);
        alertaIARepository.flush();

        log.info("Feedback gerado e salvo: ID={}, Usuário={}, Temperatura={}, Histórico={} interações", 
                saved.getIdAlerta(), usuario.getIdUsuario(), temperatura, historico.size());

        return FeedbackResponseDTO.builder()
                .mensagem(mensagem)
                .timestamp(LocalDateTime.now())
                .idAlerta(saved.getIdAlerta())
                .build();
    }

    public AnaliseResponseDTO gerarAnalise(AnaliseRequestDTO request) {
        // Busca o usuário
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Gera dados históricos (método interno do AIService)
        String dadosHistoricos = gerarDadosHistoricosParaAnalise(request.getUsuarioId());

        // 🧠 MELHORIA: Busca histórico para personalização e variação
        List<String> historico = historicoIAService.buscarHistoricoAnalise(usuario.getIdUsuario());
        String contextoHistorico = historicoIAService.gerarContextoHistorico(historico);
        String variacaoAbordagem = historicoIAService.gerarVariacaoAbordagem(historico);
        double temperatura = historicoIAService.calcularTemperaturaDinamica(historico);

        // Adiciona variação de abordagem ao contexto
        if (variacaoAbordagem != null && !variacaoAbordagem.isEmpty()) {
            contextoHistorico += "\n" + variacaoAbordagem;
        }

        // Gera análise usando GPT com histórico e variação
        GPTService gptService = getGptService();
        GPTService.AnaliseGPT analiseGPT;
        if (gptService != null) {
            analiseGPT = gptService.gerarAnaliseSemanal(dadosHistoricos, contextoHistorico, temperatura);
        } else if (aiService != null) {
            analiseGPT = aiService.gerarAnaliseSemanal(request.getUsuarioId());
        } else {
            // Fallback se nenhum serviço de IA estiver disponível
            analiseGPT = GPTService.AnaliseGPT.builder()
                .resumo("Análise não disponível no momento. Por favor, configure a API Key do OpenAI.")
                .risco("medio")
                .sugestoes(java.util.List.of("Configure a API Key do OpenAI para obter análises personalizadas."))
                .build();
        }

        // Salva a análise no banco de dados
        AlertaIA alerta = AlertaIA.builder()
                .usuario(usuario)
                .dataAlerta(LocalDate.now())
                .tipoAlerta("ANALISE_SEMANAL")
                .mensagem(analiseGPT.getResumo())
                .nivelRisco(calcularNivelRiscoAnalise(analiseGPT.getRisco()))
                .build();

        alertaIARepository.save(alerta);
        alertaIARepository.flush();

        log.info("Análise gerada e salva: Usuário={}, Temperatura={}, Histórico={} análises", 
                usuario.getIdUsuario(), temperatura, historico.size());

        // Converte para DTO
        return AnaliseResponseDTO.builder()
                .resumoSemanal(analiseGPT.getResumo())
                .riscoBurnout(analiseGPT.getRisco())
                .sugestoes(analiseGPT.getSugestoes())
                .timestamp(LocalDateTime.now())
                .build();
    }

    private Integer calcularNivelRiscoAnalise(String risco) {
        return switch (risco != null ? risco.toLowerCase() : "medio") {
            case "alto" -> 5;
            case "medio" -> 3;
            case "baixo" -> 1;
            default -> 3;
        };
    }

    public AssistenteResponseDTO gerarConteudoAssistente(AssistenteRequestDTO request) {
        // Busca o usuário
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Se recebeu tipo e mensagem, processa como mensagem do assistant
        if (request.getTipo() != null && request.getMensagem() != null && !request.getMensagem().trim().isEmpty()) {
            log.info("🔄 Processando como mensagem do assistant. Tipo: {}, Mensagem: {}", request.getTipo(), request.getMensagem());
            return processarMensagemComoAssistente(request);
        }

        String tipoConsulta = request.getTipoConsulta() != null ? request.getTipoConsulta() : "motivacao";

        // Gera contexto do usuário (método interno)
        String contextoUsuario = gerarContextoUsuarioParaAssistente(request.getUsuarioId());

        // 🧠 MELHORIA: Busca histórico para personalização e variação
        List<String> historico = historicoIAService.buscarHistoricoAssistente(usuario.getIdUsuario(), tipoConsulta);
        String contextoHistorico = historicoIAService.gerarContextoHistorico(historico);
        String variacaoAbordagem = historicoIAService.gerarVariacaoAbordagem(historico);
        double temperatura = historicoIAService.calcularTemperaturaDinamica(historico);

        // Adiciona variação de abordagem ao contexto
        if (variacaoAbordagem != null && !variacaoAbordagem.isEmpty()) {
            contextoHistorico += "\n" + variacaoAbordagem;
        }

        // Gera conteúdo usando GPT com histórico e variação
        GPTService gptService = getGptService();
        GPTService.AssistentePersonalizado conteudo;
        if (gptService != null) {
            conteudo = gptService.gerarConteudoAssistente(tipoConsulta, contextoUsuario, contextoHistorico, temperatura);
        } else {
            conteudo = aiService.gerarConteudoAssistente(request.getUsuarioId(), tipoConsulta);
        }

        // Salva o conteúdo no banco de dados
        AlertaIA alerta = AlertaIA.builder()
                .usuario(usuario)
                .dataAlerta(LocalDate.now())
                .tipoAlerta("ASSISTENTE_" + tipoConsulta.toUpperCase())
                .mensagem(conteudo.getTitulo() + ": " + conteudo.getConteudo())
                .nivelRisco(3)
                .build();

        alertaIARepository.save(alerta);
        alertaIARepository.flush();

        log.info("Conteúdo do assistente gerado e salvo: Usuário={}, Tipo={}, Temperatura={}, Histórico={} interações", 
                usuario.getIdUsuario(), tipoConsulta, temperatura, historico.size());

        // Converte para DTO
        return AssistenteResponseDTO.builder()
                .titulo(conteudo.getTitulo())
                .conteudo(conteudo.getConteudo())
                .tipo(conteudo.getTipo())
                .acoesPraticas(conteudo.getAcoesPraticas())
                .reflexao(conteudo.getReflexao())
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Processa mensagem do assistant e retorna AssistenteResponseDTO
     * Quando tipo é "agenda", retorna resposta JSON no campo conteudo
     */
    private AssistenteResponseDTO processarMensagemComoAssistente(AssistenteRequestDTO request) {
        GPTService gptService = getGptService();
        if (gptService == null) {
            log.error("❌ GPTService não disponível. API Key do OpenAI não configurada ou inválida.");
            // Retorna resposta padrão de erro
            return AssistenteResponseDTO.builder()
                    .titulo("Erro")
                    .conteudo("Serviço de IA não disponível. Verifique a configuração da API Key.")
                    .tipo("erro")
                    .acoesPraticas(List.of())
                    .reflexao("")
                    .timestamp(LocalDateTime.now())
                    .build();
        }

        try {
            String respostaJson = gptService.processarMensagemAssistant(request.getTipo(), request.getMensagem());
            
            if (respostaJson == null || respostaJson.trim().isEmpty()) {
                log.error("❌ Resposta vazia da IA");
                return AssistenteResponseDTO.builder()
                        .titulo("Erro")
                        .conteudo("Não foi possível processar a mensagem. Tente novamente.")
                        .tipo("erro")
                        .acoesPraticas(List.of())
                        .reflexao("")
                        .timestamp(LocalDateTime.now())
                        .build();
            }

            // Se for agenda, retorna a resposta JSON diretamente no conteudo
            if ("agenda".equalsIgnoreCase(request.getTipo())) {
                return AssistenteResponseDTO.builder()
                        .titulo("Agenda Processada")
                        .conteudo(respostaJson) // JSON com tasks
                        .tipo("agenda")
                        .acoesPraticas(List.of())
                        .reflexao("")
                        .timestamp(LocalDateTime.now())
                        .build();
            }

            // Para outros tipos, tenta parsear o JSON e extrair os campos
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(respostaJson);
                
                return AssistenteResponseDTO.builder()
                        .titulo(jsonNode.has("titulo") ? jsonNode.get("titulo").asText() : "Resposta")
                        .conteudo(jsonNode.has("conteudo") ? jsonNode.get("conteudo").asText() : respostaJson)
                        .tipo(jsonNode.has("tipo") ? jsonNode.get("tipo").asText() : request.getTipo())
                        .acoesPraticas(jsonNode.has("acoes_praticas") ? 
                                mapper.convertValue(jsonNode.get("acoes_praticas"), new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {}) : 
                                List.of())
                        .reflexao(jsonNode.has("reflexao") ? jsonNode.get("reflexao").asText() : "")
                        .timestamp(LocalDateTime.now())
                        .build();
            } catch (Exception e) {
                log.warn("⚠️ Erro ao parsear JSON da resposta, retornando resposta bruta: {}", e.getMessage());
                return AssistenteResponseDTO.builder()
                        .titulo("Resposta")
                        .conteudo(respostaJson)
                        .tipo(request.getTipo())
                        .acoesPraticas(List.of())
                        .reflexao("")
                        .timestamp(LocalDateTime.now())
                        .build();
            }
        } catch (Exception e) {
            log.error("❌ Erro ao processar mensagem do assistant: {}", e.getMessage(), e);
            return AssistenteResponseDTO.builder()
                    .titulo("Erro")
                    .conteudo("Erro ao processar mensagem: " + e.getMessage())
                    .tipo("erro")
                    .acoesPraticas(List.of())
                    .reflexao("")
                    .timestamp(LocalDateTime.now())
                    .build();
        }
    }

    // DESABILITADO TEMPORARIAMENTE: Funcionalidade de análise de imagem removida
    @Transactional
    public AnaliseAmbienteResponseDTO analisarAmbienteTrabalho(org.springframework.web.multipart.MultipartFile foto, Integer usuarioId) {
        throw new RuntimeException("Funcionalidade de análise de imagem desabilitada temporariamente. Use o chat para conversar com a IA.");
    }

    // DESABILITADO TEMPORARIAMENTE
    /*
    private String enriquecerAnaliseComGPT(VisionService.AnaliseAmbiente analise, Integer usuarioId) {
        try {
            // Busca dados históricos do usuário para contexto
            List<Humor> ultimosHumor = humorRepository.findByUsuario_IdUsuarioAndDataRegistroBetween(
                    usuarioId, LocalDate.now().minusDays(7), LocalDate.now());
            
            StringBuilder contexto = new StringBuilder();
            contexto.append("ANÁLISE DE AMBIENTE DE TRABALHO (Visão Computacional - Deep Learning):\n");
            contexto.append(String.format("- Nível de foco detectado: %s\n", analise.getNivelFoco()));
            contexto.append(String.format("- Organização do ambiente: %s\n", analise.getOrganizacao()));
            contexto.append(String.format("- Iluminação: %s\n", analise.getIluminacao()));
            contexto.append(String.format("- Objetos detectados pela IA: %s\n", String.join(", ", analise.getObjetosDetectados())));
            
            if (!ultimosHumor.isEmpty()) {
                double mediaHumor = ultimosHumor.stream()
                        .mapToInt(Humor::getNivelHumor)
                        .average()
                        .orElse(3.0);
                double mediaEnergia = ultimosHumor.stream()
                        .mapToInt(Humor::getNivelEnergia)
                        .average()
                        .orElse(3.0);
                contexto.append(String.format("\nCONTEXTO DO USUÁRIO (ÚLTIMOS 7 DIAS):\n"));
                contexto.append(String.format("- Média de humor: %.1f/5\n", mediaHumor));
                contexto.append(String.format("- Média de energia: %.1f/5\n", mediaEnergia));
            }
            
            // Chama GPT para enriquecer a análise com insights profundos
            GPTService gptService = getGptService();
            if (gptService != null) {
                // Construir prompt mais específico sobre organização
                String statusOrganizacao = analise.getOrganizacao();
                String enfaseOrganizacao = "";
                if ("ruim".equals(statusOrganizacao)) {
                    enfaseOrganizacao = "⚠️ ATENÇÃO: O ambiente foi detectado como DESORGANIZADO pela visão computacional. " +
                                      "Você DEVE mencionar claramente que o ambiente está desorganizado e isso pode afetar negativamente a produtividade. " +
                                      "Seja direto e específico sobre os problemas de organização detectados.\n\n";
                } else if ("regular".equals(statusOrganizacao)) {
                    enfaseOrganizacao = "ℹ️ O ambiente foi detectado como PARCIALMENTE ORGANIZADO. " +
                                      "Mencione que há espaço para melhorias na organização.\n\n";
                } else if ("excelente".equals(statusOrganizacao)) {
                    enfaseOrganizacao = "✅ O ambiente foi detectado como MUITO ORGANIZADO. " +
                                      "Reconheça positivamente essa organização e como ela contribui para o bem-estar.\n\n";
                }
                
                String prompt = String.format(
                    "Você é um especialista em ergonomia, produtividade e bem-estar no ambiente de trabalho, " +
                    "com formação em psicologia organizacional e neurociência aplicada. " +
                    "Você analisa ambientes de trabalho com profundidade e oferece insights valiosos.\n\n" +
                    "DADOS DA ANÁLISE DE AMBIENTE (Visão Computacional - Deep Learning):\n" +
                    "%s\n\n" +
                    "%s" +
                    "INSTRUÇÕES CRÍTICAS PARA ANÁLISE INTELIGENTE:\n" +
                    "1. Você DEVE mencionar explicitamente o nível de ORGANIZAÇÃO detectado (ruim/regular/excelente)\n" +
                    "2. Se a organização for 'ruim', você DEVE ser claro e direto sobre os problemas de desorganização\n" +
                    "3. Se a organização for 'excelente', reconheça positivamente essa organização\n" +
                    "4. Analise a RELAÇÃO entre o ambiente físico e o bem-estar do usuário\n" +
                    "5. Identifique CONEXÕES entre organização/iluminação e produtividade\n" +
                    "6. Correlacione o ambiente com os dados de humor/energia do usuário (se disponíveis)\n" +
                    "7. Gere um resumo PROFUNDO e INTELIGENTE (2-3 frases) que:\n" +
                    "   - MENCIONE CLARAMENTE o nível de organização detectado\n" +
                    "   - Reconheça pontos fortes e fracos do ambiente de forma específica\n" +
                    "   - Conecte o ambiente físico com o bem-estar mental (mencione os dados)\n" +
                    "   - Seja específico e acionável (não genérico)\n" +
                    "   - Use linguagem empática, profissional e como um mentor experiente\n" +
                    "   - Ofereça insights valiosos baseados em ciência e experiência\n\n" +
                    "EXEMPLOS DE TOM (use como referência, mas seja específico):\n" +
                    "- Se ambiente DESORGANIZADO: 'A análise detectou que seu ambiente está desorganizado, o que pode reduzir sua produtividade em até 30%%. " +
                    "Organizar o espaço pode melhorar significativamente seu foco e bem-estar.'\n" +
                    "- Se ambiente ORGANIZADO mas humor baixo: 'Seu ambiente está bem organizado, mas seus dados mostram humor baixo. " +
                    "Considere adicionar elementos que tragam alegria ao espaço, como plantas ou objetos pessoais significativos.'\n" +
                    "- Se iluminação ruim: 'A iluminação detectada pode estar contribuindo para sua fadiga. " +
                    "Melhorar a luz pode aumentar sua energia em até 20%%.'\n\n" +
                    "IMPORTANTE: Seja DIRETO e ESPECÍFICO sobre a organização. Não seja genérico ou vago.\n\n" +
                    "Responda APENAS com o resumo (2-3 frases), sem explicações adicionais.",
                    contexto.toString(),
                    enfaseOrganizacao
                );
                
                String respostaGPT = gptService.chamarGPT(prompt);
                if (respostaGPT != null && !respostaGPT.isEmpty() && !respostaGPT.trim().isEmpty()) {
                    log.info("✅ Análise enriquecida com GPT: {}", respostaGPT.substring(0, Math.min(100, respostaGPT.length())));
                    return String.format("✅ Análise realizada com modelo de Deep Learning (IA REAL). %s", respostaGPT.trim());
                }
            } else {
                log.warn("⚠️ GPT não disponível para enriquecer análise. Usando resumo direto da visão computacional.");
            }
        } catch (Exception e) {
            log.warn("Erro ao enriquecer análise com GPT, usando análise padrão", e);
        }
        
        // Retorna resumo direto da análise de visão computacional (já inclui informações de organização)
        String resumoDireto = analise.getResumoAnalise();
        // Garantir que o resumo mencione claramente a organização
        if (!resumoDireto.toLowerCase().contains("organização") && !resumoDireto.toLowerCase().contains("organiz")) {
            resumoDireto += String.format(" Organização: %s.", analise.getOrganizacao());
        }
        return resumoDireto;
    }
    */

    /**
     * Gera dados históricos para análise (método auxiliar)
     */
    private String gerarDadosHistoricosParaAnalise(Integer idUsuario) {
        try {
            List<Humor> ultimosHumor = humorRepository.findByUsuario_IdUsuarioAndDataRegistroBetween(
                    idUsuario, LocalDate.now().minusDays(7), LocalDate.now());
            
            StringBuilder dadosHistoricos = new StringBuilder();
            dadosHistoricos.append("DADOS DOS ÚLTIMOS 7 DIAS:\n\n");
            
            dadosHistoricos.append("HUMOR E ENERGIA:\n");
            if (ultimosHumor.isEmpty()) {
                dadosHistoricos.append("- Nenhum registro de humor nos últimos 7 dias\n");
            } else {
                double mediaHumor = ultimosHumor.stream()
                        .mapToInt(Humor::getNivelHumor)
                        .average()
                        .orElse(3.0);
                double mediaEnergia = ultimosHumor.stream()
                        .mapToInt(Humor::getNivelEnergia)
                        .average()
                        .orElse(3.0);
                dadosHistoricos.append(String.format("- Média de humor: %.1f/5\n", mediaHumor));
                dadosHistoricos.append(String.format("- Média de energia: %.1f/5\n", mediaEnergia));
                dadosHistoricos.append(String.format("- Total de registros: %d\n", ultimosHumor.size()));
            }
            
            return dadosHistoricos.toString();
        } catch (Exception e) {
            log.error("Erro ao gerar dados históricos para análise", e);
            return "Dados históricos não disponíveis.";
        }
    }

    /**
     * Gera contexto do usuário para assistente (método auxiliar)
     */
    private String gerarContextoUsuarioParaAssistente(Integer idUsuario) {
        try {
            List<Humor> ultimosHumor = humorRepository.findByUsuario_IdUsuarioAndDataRegistroBetween(
                    idUsuario, LocalDate.now().minusDays(7), LocalDate.now());
            
            StringBuilder contexto = new StringBuilder();
            contexto.append("DADOS DO USUÁRIO (ÚLTIMOS 7 DIAS):\n");
            
            if (ultimosHumor.isEmpty()) {
                contexto.append("- Nenhum registro de humor nos últimos 7 dias\n");
            } else {
                double mediaHumor = ultimosHumor.stream()
                        .mapToInt(Humor::getNivelHumor)
                        .average()
                        .orElse(3.0);
                double mediaEnergia = ultimosHumor.stream()
                        .mapToInt(Humor::getNivelEnergia)
                        .average()
                        .orElse(3.0);
                contexto.append(String.format("- Média de humor: %.1f/5\n", mediaHumor));
                contexto.append(String.format("- Média de energia: %.1f/5\n", mediaEnergia));
                contexto.append(String.format("- Total de registros: %d\n", ultimosHumor.size()));
            }
            
            return contexto.toString();
        } catch (Exception e) {
            log.error("Erro ao gerar contexto do usuário para assistente", e);
            return "Dados do usuário não disponíveis.";
        }
    }

    /**
     * Chat conversacional com IA - mantém histórico e contexto
     */
    @Transactional
    public ChatResponseDTO chatConversacional(ChatRequestDTO request) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Integer idConversaPai = request.getIdConversaPai();
        LocalDateTime agora = LocalDateTime.now();

        // Se não tem conversa pai, cria uma nova (usa timestamp como ID único)
        if (idConversaPai == null) {
            // Busca última conversa para ver se deve continuar ou criar nova
            ConversaIA ultimaConversa = conversaIARepository.findFirstByUsuario_IdUsuarioOrderByDataMensagemDesc(usuario.getIdUsuario());
            if (ultimaConversa != null && ultimaConversa.getDataMensagem().isAfter(agora.minusHours(2))) {
                // Continua conversa recente (menos de 2 horas)
                idConversaPai = ultimaConversa.getIdConversaPai() != null ? 
                    ultimaConversa.getIdConversaPai() : ultimaConversa.getIdConversa();
            } else {
                // Cria nova conversa
                idConversaPai = null; // Será definido após salvar primeira mensagem
            }
        }

        // Salva mensagem do usuário
        ConversaIA mensagemUsuario = ConversaIA.builder()
                .usuario(usuario)
                .dataMensagem(agora)
                .tipoMensagem("USUARIO")
                .mensagem(request.getMensagem())
                .idConversaPai(idConversaPai)
                .build();

        ConversaIA mensagemUsuarioSalva = conversaIARepository.save(mensagemUsuario);
        conversaIARepository.flush();

        // Se é primeira mensagem, define o ID da conversa pai
        if (idConversaPai == null) {
            idConversaPai = mensagemUsuarioSalva.getIdConversa();
            mensagemUsuarioSalva.setIdConversaPai(idConversaPai);
            conversaIARepository.save(mensagemUsuarioSalva);
        }

        // Busca histórico da conversa (últimas 10 mensagens)
        List<ConversaIA> historicoConversa = conversaIARepository
                .findByUsuario_IdUsuarioAndIdConversaPaiOrderByDataMensagemAsc(
                        usuario.getIdUsuario(), idConversaPai);

        // Monta contexto histórico da conversa
        StringBuilder contextoHistorico = new StringBuilder();
        if (!historicoConversa.isEmpty()) {
            for (ConversaIA msg : historicoConversa) {
                if (msg.getIdConversa().equals(mensagemUsuarioSalva.getIdConversa())) {
                    continue; // Pula a mensagem atual
                }
                contextoHistorico.append(msg.getTipoMensagem()).append(": ")
                        .append(msg.getMensagem()).append("\n");
            }
        }

        // Gera contexto do usuário
        String contextoUsuario = gerarContextoUsuarioParaAssistente(usuario.getIdUsuario());

        // Calcula temperatura dinâmica baseada no histórico
        double temperatura = historicoIAService.calcularTemperaturaDinamica(
                historicoConversa.stream()
                        .map(ConversaIA::getMensagem)
                        .collect(java.util.stream.Collectors.toList()));

        // Gera resposta usando GPT
        GPTService gptServiceParaUsar = getGptService();
        String resposta;
        if (gptServiceParaUsar != null) {
            resposta = gptServiceParaUsar.gerarRespostaChat(
                    request.getMensagem(),
                    contextoHistorico.toString(),
                    contextoUsuario,
                    temperatura);
        } else {
            // Mensagem mais informativa quando API key não está configurada
            resposta = "Olá! Para usar o chat com IA, é necessário configurar a API Key do OpenAI. " +
                       "Por favor, configure a variável de ambiente OPENAI_API_KEY ou a propriedade spring.ai.openai.api-key no arquivo application.properties. " +
                       "Enquanto isso, você pode usar os outros recursos do sistema como feedback, análise semanal e análise de ambiente.";
            log.warn("⚠️ Chat: GPTService não disponível. API Key do OpenAI não configurada.");
        }

        // Salva resposta da IA
        ConversaIA mensagemIA = ConversaIA.builder()
                .usuario(usuario)
                .dataMensagem(LocalDateTime.now())
                .tipoMensagem("IA")
                .mensagem(resposta)
                .idConversaPai(idConversaPai)
                .contexto(contextoUsuario)
                .build();

        ConversaIA mensagemIASalva = conversaIARepository.save(mensagemIA);
        conversaIARepository.flush();

        log.info("Chat: Usuário={}, Conversa={}, Mensagens={}", 
                usuario.getIdUsuario(), idConversaPai, historicoConversa.size() + 2);

        return ChatResponseDTO.builder()
                .resposta(resposta)
                .idConversa(mensagemIASalva.getIdConversa())
                .idConversaPai(idConversaPai)
                .timestamp(mensagemIASalva.getDataMensagem())
                .contexto(contextoUsuario)
                .build();
    }

    /**
     * Obtém GPTService (tenta múltiplas formas)
     */
    protected GPTService getGptService() {
        // Primeiro tenta usar o GPTService injetado diretamente
        if (gptService != null) {
            log.debug("GPTService obtido via injeção direta");
            return gptService;
        }
        
        // Se não tiver, tenta obter via AIService usando reflexão
        try {
            if (aiService != null) {
                java.lang.reflect.Field field = AIService.class.getDeclaredField("gptService");
                field.setAccessible(true);
                GPTService gptServiceViaAIService = (GPTService) field.get(aiService);
                if (gptServiceViaAIService != null) {
                    log.debug("GPTService obtido via AIService");
                    return gptServiceViaAIService;
                }
            }
        } catch (Exception e) {
            log.debug("GPTService não disponível via AIService", e);
        }
        
        log.warn("⚠️ GPTService não disponível. Verifique se a API Key do OpenAI está configurada.");
        return null;
    }

    // DESABILITADO TEMPORARIAMENTE
    /*
    private Integer calcularNivelRiscoAmbiente(VisionService.AnaliseAmbiente analise) {
        int risco = 3; // Médio por padrão
        
        if ("baixo".equals(analise.getNivelFoco())) {
            risco = 4; // Alto
        }
        if ("ruim".equals(analise.getOrganizacao())) {
            risco = Math.max(risco, 4);
        }
        if ("insuficiente".equals(analise.getIluminacao())) {
            risco = Math.max(risco, 3);
        }
        
        return risco;
    }
    */

    /**
     * Calcula nível de risco baseado no humor (1-5)
     */
    private Integer calcularNivelRisco(Integer humor) {
        if (humor == null) return 3;
        if (humor <= 2) return 5; // Risco muito alto
        if (humor <= 3) return 4; // Risco alto
        return 3; // Risco médio
    }

    /**
     * Co-planejador de IA: extrai tarefas de mensagens em linguagem natural
     * Similar ao Tiimo AI co-planner
     */
    @Transactional
    public CoPlannerResponseDTO extrairTarefas(CoPlannerRequestDTO request) {
        // Verifica se o usuário existe, mas não bloqueia se não existir (permite teste)
        Usuario usuario = null;
        try {
            usuario = usuarioRepository.findById(request.getUsuarioId())
                    .orElse(null);
            if (usuario == null) {
                log.warn("⚠️ Usuário {} não encontrado, mas continuando com extração de tarefas", request.getUsuarioId());
            }
        } catch (Exception e) {
            log.warn("⚠️ Erro ao buscar usuário, continuando: {}", e.getMessage());
        }

        Integer usuarioId = usuario != null ? usuario.getIdUsuario() : request.getUsuarioId();
        log.info("🤖 Co-planejador: Extraindo tarefas da mensagem do usuário {}", usuarioId);

        GPTService gptServiceParaUsar = getGptService();
        List<TarefaDTO> tarefas = new java.util.ArrayList<>();

        if (gptServiceParaUsar != null) {
            try {
                log.info("🤖 Chamando GPTService para extrair tarefas...");
                String respostaJson = gptServiceParaUsar.extrairTarefas(request.getMensagem());
                
                if (respostaJson != null && !respostaJson.trim().isEmpty()) {
                    log.info("📥 Resposta recebida da IA (tamanho: {} chars). Primeiros 500 chars: {}", 
                            respostaJson.length(),
                            respostaJson.length() > 500 ? respostaJson.substring(0, 500) + "..." : respostaJson);
                    
                    // Parseia o JSON retornado
                    tarefas = parsearTarefasJson(respostaJson);
                    log.info("✅ Co-planejador: {} tarefa(s) extraída(s) com sucesso", tarefas.size());
                    
                    if (tarefas.isEmpty()) {
                        log.warn("⚠️ Nenhuma tarefa foi parseada. Resposta da IA: {}", respostaJson);
                    }
                } else {
                    log.error("❌ Co-planejador: Resposta da IA está vazia ou nula. Verifique se a API key está configurada corretamente.");
                }
            } catch (RuntimeException e) {
                // Captura erros específicos da API OpenAI (cota excedida, API key inválida, etc)
                String errorMessage = e.getMessage();
                if (errorMessage != null && (errorMessage.contains("cota") || errorMessage.contains("quota") || 
                    errorMessage.contains("créditos") || errorMessage.contains("insufficient_quota"))) {
                    log.error("❌ Erro de cota da OpenAI: {}", errorMessage);
                    // Retorna lista vazia com mensagem de erro, mas não quebra a aplicação
                    return CoPlannerResponseDTO.builder()
                            .tarefas(new java.util.ArrayList<>())
                            .mensagemOriginal(request.getMensagem())
                            .mensagem("A API da OpenAI excedeu a cota ou não tem créditos disponíveis. " +
                                    "Por favor, verifique sua conta em https://platform.openai.com/account/billing")
                            .totalTarefas(0)
                            .timestamp(java.time.LocalDateTime.now())
                            .build();
                }
                // Re-lança outras exceções
                log.error("❌ Erro ao extrair tarefas: {}", e.getMessage(), e);
            } catch (Exception e) {
                log.error("❌ Erro ao extrair tarefas: {}", e.getMessage(), e);
                log.error("❌ Stack trace completo:", e);
                // Retorna lista vazia em caso de erro
            }
        } else {
            log.error("❌ Co-planejador: GPTService não disponível. API Key do OpenAI não configurada ou inválida.");
        }

        return CoPlannerResponseDTO.builder()
                .tarefas(tarefas)
                .mensagemOriginal(request.getMensagem())
                .timestamp(java.time.LocalDateTime.now())
                .totalTarefas(tarefas.size())
                .build();
    }

    /**
     * Parseia o JSON de tarefas retornado pela IA
     */
    private List<TarefaDTO> parsearTarefasJson(String jsonResponse) {
        try {
            if (jsonResponse == null || jsonResponse.trim().isEmpty()) {
                log.warn("⚠️ Resposta da IA está vazia");
                return new java.util.ArrayList<>();
            }

            log.info("📝 Parseando JSON de tarefas. Tamanho: {} caracteres", jsonResponse.length());
            log.info("📝 JSON completo recebido: {}", jsonResponse);
            
            // Remove markdown code blocks se houver
            String jsonLimpo = jsonResponse.trim();
            
            // Remove ```json ou ``` do início
            if (jsonLimpo.startsWith("```json")) {
                jsonLimpo = jsonLimpo.substring(7).trim();
            } else if (jsonLimpo.startsWith("```")) {
                jsonLimpo = jsonLimpo.substring(3).trim();
            }
            
            // Remove ``` do final
            if (jsonLimpo.endsWith("```")) {
                jsonLimpo = jsonLimpo.substring(0, jsonLimpo.length() - 3).trim();
            }
            
            // Tenta encontrar o JSON dentro do texto se houver texto adicional
            int inicioJson = jsonLimpo.indexOf("{");
            int fimJson = jsonLimpo.lastIndexOf("}");
            if (inicioJson >= 0 && fimJson > inicioJson) {
                jsonLimpo = jsonLimpo.substring(inicioJson, fimJson + 1);
            }
            
            jsonLimpo = jsonLimpo.trim();
            log.info("📝 JSON limpo (primeiros 500 chars): {}", 
                    jsonLimpo.length() > 500 ? jsonLimpo.substring(0, 500) + "..." : jsonLimpo);

            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode json = objectMapper.readTree(jsonLimpo);

            List<TarefaDTO> tarefas = new java.util.ArrayList<>();

            // Tenta diferentes estruturas de resposta
            com.fasterxml.jackson.databind.JsonNode tarefasArray = null;
            
            if (json.has("tarefas") && json.get("tarefas").isArray()) {
                tarefasArray = json.get("tarefas");
                log.debug("📋 Tarefas encontradas em 'tarefas'");
            } else if (json.isArray()) {
                // Se a resposta é diretamente um array
                tarefasArray = json;
                log.debug("📋 Tarefas encontradas como array direto");
            } else if (json.has("tasks") && json.get("tasks").isArray()) {
                // Tenta "tasks" como alternativa
                tarefasArray = json.get("tasks");
                log.debug("📋 Tarefas encontradas em 'tasks'");
            } else {
                // Log detalhado para debug
                java.util.Iterator<String> fieldNames = json.fieldNames();
                java.util.List<String> chaves = new java.util.ArrayList<>();
                while (fieldNames.hasNext()) {
                    chaves.add(fieldNames.next());
                }
                log.error("❌ Estrutura JSON não reconhecida. Chaves disponíveis: {}", chaves);
                log.error("❌ JSON completo: {}", json.toString());
            }
            
            if (tarefasArray != null && tarefasArray.isArray()) {
                log.info("📋 Encontrado array de tarefas com {} itens", tarefasArray.size());
                
                for (com.fasterxml.jackson.databind.JsonNode tarefaNode : tarefasArray) {
                    // Extrai horário - aceita "horario" (formato esperado) ou "data_horario" (formato do Assistant)
                    String horario = null;
                    
                    // Tenta primeiro o formato esperado: "horario"
                    if (tarefaNode.has("horario") && !tarefaNode.get("horario").isNull()) {
                        String horarioStr = tarefaNode.get("horario").asText();
                        if (horarioStr != null && !horarioStr.trim().isEmpty() && !horarioStr.equals("null")) {
                            horario = horarioStr.trim();
                        }
                    }
                    // Se não encontrou, tenta o formato do Assistant: "data_horario"
                    else if (tarefaNode.has("data_horario") && !tarefaNode.get("data_horario").isNull()) {
                        String dataHorarioStr = tarefaNode.get("data_horario").asText();
                        if (dataHorarioStr != null && !dataHorarioStr.trim().isEmpty() && !dataHorarioStr.equals("null")) {
                            // Extrai apenas o horário do formato ISO (ex: "2022-05-25T14:00:00" -> "14:00")
                            try {
                                if (dataHorarioStr.contains("T")) {
                                    String[] partes = dataHorarioStr.split("T");
                                    if (partes.length > 1) {
                                        String horaCompleta = partes[1];
                                        if (horaCompleta.contains(":")) {
                                            String[] horaMinuto = horaCompleta.split(":");
                                            if (horaMinuto.length >= 2) {
                                                horario = horaMinuto[0] + ":" + horaMinuto[1];
                                            }
                                        }
                                    }
                                } else {
                                    horario = dataHorarioStr.trim();
                                }
                            } catch (Exception e) {
                                log.warn("⚠️ Erro ao extrair horário de data_horario: {}", dataHorarioStr);
                            }
                        }
                    }
                    
                    // Extrai descrição - aceita "descricao" (formato esperado) ou "titulo" (formato do Assistant)
                    String descricao = null;
                    if (tarefaNode.has("descricao")) {
                        descricao = tarefaNode.get("descricao").asText();
                    } else if (tarefaNode.has("titulo")) {
                        // Formato do Assistant usa "titulo"
                        descricao = tarefaNode.get("titulo").asText();
                    }
                    
                    // Extrai prioridade - normaliza para ALTA, MEDIA ou BAIXA
                    String prioridade = "MEDIA";
                    if (tarefaNode.has("prioridade")) {
                        String prioridadeStr = tarefaNode.get("prioridade").asText();
                        if (prioridadeStr != null && !prioridadeStr.trim().isEmpty()) {
                            prioridadeStr = prioridadeStr.trim().toUpperCase();
                            // Normaliza variações comuns
                            if (prioridadeStr.equals("ALTA") || prioridadeStr.equals("ALTO") || prioridadeStr.equals("HIGH")) {
                                prioridade = "ALTA";
                            } else if (prioridadeStr.equals("MEDIA") || prioridadeStr.equals("MÉDIA") || prioridadeStr.equals("MEDIO") || 
                                      prioridadeStr.equals("MÉDIO") || prioridadeStr.equals("MEDIUM")) {
                                prioridade = "MEDIA";
                            } else if (prioridadeStr.equals("BAIXA") || prioridadeStr.equals("BAIXO") || prioridadeStr.equals("LOW")) {
                                prioridade = "BAIXA";
                            } else {
                                prioridade = "MEDIA"; // Default
                            }
                        }
                    }

                    if (descricao != null && !descricao.trim().isEmpty()) {
                        TarefaDTO tarefa = TarefaDTO.builder()
                                .horario(horario)
                                .descricao(descricao.trim())
                                .prioridade(prioridade)
                                .build();
                        tarefas.add(tarefa);
                        log.debug("✅ Tarefa extraída: {} - {} - {}", horario, descricao, prioridade);
                    } else {
                        log.warn("⚠️ Tarefa sem descrição/título ignorada: {}", tarefaNode.toString());
                    }
                }
            } else {
                log.error("⚠️ JSON não contém array 'tarefas' ou não é um array. Estrutura completa: {}", json.toString());
                log.error("⚠️ Tentando parsear como estrutura alternativa...");
                
                // Tenta extrair tarefas de outras estruturas possíveis
                if (json.has("content")) {
                    String content = json.get("content").asText();
                    log.warn("⚠️ Resposta contém 'content': {}", content);
                    // Tenta parsear o content como JSON
                    try {
                        com.fasterxml.jackson.databind.JsonNode contentJson = objectMapper.readTree(content);
                        if (contentJson.has("tarefas") && contentJson.get("tarefas").isArray()) {
                            tarefasArray = contentJson.get("tarefas");
                            log.info("📋 Tarefas encontradas dentro de 'content'");
                        }
                    } catch (Exception e) {
                        log.warn("⚠️ Não foi possível parsear 'content' como JSON");
                    }
                }
            }

            log.info("✅ Total de tarefas parseadas: {}", tarefas.size());
            return tarefas;
        } catch (Exception e) {
            log.error("❌ Erro ao parsear JSON de tarefas: {}", e.getMessage(), e);
            log.error("📄 JSON recebido completo: {}", jsonResponse);
            return new java.util.ArrayList<>();
        }
    }

    /**
     * Processa mensagem do Assistant - Agenda ou Conteúdo
     * Similar ao endpoint /assistant/analisar solicitado
     */
    public String processarMensagemAssistant(AssistantAnalisarRequestDTO request) {
        log.info("🤖 Assistant: Processando mensagem. Tipo: {}, Usuário: {}", request.getTipo(), request.getUsuarioId());

        // Valida usuário (opcional - permite teste)
        try {
            Usuario usuario = usuarioRepository.findById(request.getUsuarioId()).orElse(null);
            if (usuario == null) {
                log.warn("⚠️ Usuário {} não encontrado, mas continuando com processamento", request.getUsuarioId());
            }
        } catch (Exception e) {
            log.warn("⚠️ Erro ao buscar usuário, continuando: {}", e.getMessage());
        }

        GPTService gptService = getGptService();
        if (gptService == null) {
            log.error("❌ GPTService não disponível. API Key do OpenAI não configurada ou inválida.");
            return "{\"erro\": \"Serviço de IA não disponível. Verifique a configuração da API Key.\"}";
        }

        try {
            String respostaJson = gptService.processarMensagemAssistant(request.getTipo(), request.getMensagem());
            
            if (respostaJson != null && !respostaJson.trim().isEmpty()) {
                log.info("✅ Assistant: Resposta recebida (tamanho: {} chars)", respostaJson.length());
                log.debug("📄 Resposta completa: {}", respostaJson);
                return respostaJson;
            } else {
                log.error("❌ Assistant: Resposta vazia da IA");
                return "{\"erro\": \"Não foi possível processar a mensagem. Tente novamente.\"}";
            }
        } catch (Exception e) {
            log.error("❌ Erro ao processar mensagem do assistant: {}", e.getMessage(), e);
            return "{\"erro\": \"Erro ao processar mensagem: " + e.getMessage() + "\"}";
        }
    }

    /**
     * Monitora pausas e movimento do usuário através de análise de frames de vídeo
     */
    public PausaMonitorResponseDTO monitorarPausa(PausaMonitorRequestDTO request) {
        log.info("📹 Monitoramento de pausa: Processando frame para usuário {}", request.getUsuarioId());
        
        // Validar usuário
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + request.getUsuarioId()));
        
        // Se solicitado, resetar sessão
        if (Boolean.TRUE.equals(request.getResetarSessao())) {
            pausaMonitorService.resetarSessao(request.getUsuarioId());
            log.info("🔄 Sessão de monitoramento resetada para usuário {}", request.getUsuarioId());
        }
        
        try {
            // Decodificar frame base64
            byte[] frameBytes = java.util.Base64.getDecoder().decode(request.getFrameBase64());
            
            // Processar frame
            PausaMonitorService.ResultadoMonitoramento resultado = 
                pausaMonitorService.processarFrame(request.getUsuarioId(), frameBytes);
            
            // Converter para DTO
            PausaMonitorResponseDTO response = PausaMonitorResponseDTO.builder()
                    .usuarioId(resultado.getUsuarioId())
                    .movimentoDetectado(resultado.isMovimentoDetectado())
                    .quantidadeMovimento(resultado.getQuantidadeMovimento())
                    .presente(resultado.isPresente())
                    .tempoSentadoMinutos(resultado.getTempoSentadoMinutos())
                    .totalPausas(resultado.getTotalPausas())
                    .sugerirAlongamento(resultado.isSugerirAlongamento())
                    .mensagem(resultado.getMensagem())
                    .sugestoes(resultado.getSugestoes())
                    .timestamp(resultado.getTimestamp())
                    .build();
            
            // Se sugerir alongamento, criar alerta opcional
            if (resultado.isSugerirAlongamento()) {
                try {
                    AlertaIA alerta = AlertaIA.builder()
                            .usuario(usuario)
                            .dataAlerta(LocalDate.now())
                            .tipoAlerta("PAUSA_SUGERIDA")
                            .mensagem("Sugestão de alongamento após " + resultado.getTempoSentadoMinutos() + " minutos sentado")
                            .nivelRisco(2) // Risco baixo - apenas sugestão
                            .build();
                    alertaIARepository.save(alerta);
                    log.info("✅ Alerta de pausa sugerida salvo para usuário {}", request.getUsuarioId());
                } catch (Exception e) {
                    log.warn("⚠️ Erro ao salvar alerta de pausa (não crítico): {}", e.getMessage());
                }
            }
            
            return response;
            
        } catch (IllegalArgumentException e) {
            log.error("❌ Erro ao decodificar frame base64: {}", e.getMessage());
            throw new RuntimeException("Frame base64 inválido: " + e.getMessage());
        } catch (Exception e) {
            log.error("❌ Erro ao monitorar pausa para usuário {}", request.getUsuarioId(), e);
            throw new RuntimeException("Erro ao processar monitoramento: " + e.getMessage());
        }
    }
}

