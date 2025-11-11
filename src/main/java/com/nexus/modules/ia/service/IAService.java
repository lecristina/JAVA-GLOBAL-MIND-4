package com.nexus.modules.ia.service;

import com.nexus.ai.AIService;
import com.nexus.ai.GPTService;
import com.nexus.ai.HistoricoIAService;
// import com.nexus.ai.VisionService; // DESABILITADO TEMPORARIAMENTE
import com.nexus.application.dto.AnaliseAmbienteResponseDTO;
import com.nexus.application.dto.AnaliseRequestDTO;
import com.nexus.application.dto.AnaliseResponseDTO;
import com.nexus.application.dto.AssistenteRequestDTO;
import com.nexus.application.dto.AssistenteResponseDTO;
import com.nexus.application.dto.ChatRequestDTO;
import com.nexus.application.dto.ChatResponseDTO;
import com.nexus.application.dto.FeedbackRequestDTO;
import com.nexus.application.dto.FeedbackResponseDTO;
import com.nexus.application.mapper.AIAlertMapper;
import com.nexus.domain.model.AlertaIA;
import com.nexus.domain.model.ConversaIA;
import com.nexus.domain.model.Humor;
import com.nexus.domain.model.Usuario;
import com.nexus.infrastructure.repository.AlertaIARepository;
import com.nexus.infrastructure.repository.ConversaIARepository;
import com.nexus.infrastructure.repository.HumorRepository;
import com.nexus.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class IAService {

    private final AIService aiService;
    // private final VisionService visionService; // DESABILITADO TEMPORARIAMENTE - removido do construtor
    private final HistoricoIAService historicoIAService;
    private final AlertaIARepository alertaIARepository;
    private final ConversaIARepository conversaIARepository;
    private final UsuarioRepository usuarioRepository;
    private final HumorRepository humorRepository;
    private final AIAlertMapper aiAlertMapper;
    
    // Injeção opcional do GPTService
    private GPTService gptService;
    
    @org.springframework.beans.factory.annotation.Autowired(required = false)
    public void setGptService(GPTService gptService) {
        this.gptService = gptService;
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
        } else {
            mensagem = aiService.gerarFeedbackEmpatico(humor, produtividade);
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
        } else {
            analiseGPT = aiService.gerarAnaliseSemanal(request.getUsuarioId());
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
    private GPTService getGptService() {
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
}

