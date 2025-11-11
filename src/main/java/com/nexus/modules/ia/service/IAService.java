package com.nexus.modules.ia.service;

import com.nexus.ai.AIService;
import com.nexus.ai.GPTService;
import com.nexus.ai.VisionService;
import com.nexus.application.dto.AnaliseAmbienteResponseDTO;
import com.nexus.application.dto.AnaliseRequestDTO;
import com.nexus.application.dto.AnaliseResponseDTO;
import com.nexus.application.dto.AssistenteRequestDTO;
import com.nexus.application.dto.AssistenteResponseDTO;
import com.nexus.application.dto.FeedbackRequestDTO;
import com.nexus.application.dto.FeedbackResponseDTO;
import com.nexus.application.mapper.AIAlertMapper;
import com.nexus.domain.model.AlertaIA;
import com.nexus.domain.model.Usuario;
import com.nexus.infrastructure.repository.AlertaIARepository;
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
    private final VisionService visionService;
    private final AlertaIARepository alertaIARepository;
    private final UsuarioRepository usuarioRepository;
    private final AIAlertMapper aiAlertMapper;

    @Transactional
    public FeedbackResponseDTO gerarFeedback(FeedbackRequestDTO request) {
        // Busca o usuário
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Gera feedback usando GPT
        String mensagem = aiService.gerarFeedbackEmpatico(
                request.getHumor() != null ? request.getHumor() : 3,
                request.getProdutividade() != null ? request.getProdutividade() : "media"
        );

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

        log.info("Feedback gerado e salvo: ID={}, Usuário={}", saved.getIdAlerta(), usuario.getIdUsuario());

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

        // Gera análise usando GPT
        GPTService.AnaliseGPT analiseGPT = aiService.gerarAnaliseSemanal(request.getUsuarioId());

        // Converte para DTO
        return AnaliseResponseDTO.builder()
                .resumoSemanal(analiseGPT.getResumo())
                .riscoBurnout(analiseGPT.getRisco())
                .sugestoes(analiseGPT.getSugestoes())
                .timestamp(LocalDateTime.now())
                .build();
    }

    public AssistenteResponseDTO gerarConteudoAssistente(AssistenteRequestDTO request) {
        // Busca o usuário
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Gera conteúdo usando GPT
        GPTService.AssistentePersonalizado conteudo = aiService.gerarConteudoAssistente(
                request.getUsuarioId(),
                request.getTipoConsulta() != null ? request.getTipoConsulta() : "motivacao"
        );

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

    @Transactional
    public AnaliseAmbienteResponseDTO analisarAmbienteTrabalho(org.springframework.web.multipart.MultipartFile foto, Integer usuarioId) {
        // Busca o usuário
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        try {
            // Analisa a imagem usando Deep Learning
            VisionService.AnaliseAmbiente analise = visionService.analisarAmbienteTrabalho(foto.getBytes());

            // Calcula nível de risco baseado na análise
            Integer nivelRisco = calcularNivelRiscoAmbiente(analise);

            // Salva a análise no banco de dados
            AlertaIA alerta = AlertaIA.builder()
                    .usuario(usuario)
                    .dataAlerta(LocalDate.now())
                    .tipoAlerta("ANALISE_AMBIENTE")
                    .mensagem(analise.getResumoAnalise())
                    .nivelRisco(nivelRisco)
                    .build();

            AlertaIA saved = alertaIARepository.save(alerta);
            alertaIARepository.flush();

            log.info("Análise de ambiente salva: ID={}, Usuário={}, Foco={}, Organização={}", 
                    saved.getIdAlerta(), usuario.getIdUsuario(), analise.getNivelFoco(), analise.getOrganizacao());

            // Converte para DTO
            return AnaliseAmbienteResponseDTO.builder()
                    .nivelFoco(analise.getNivelFoco())
                    .organizacao(analise.getOrganizacao())
                    .iluminacao(analise.getIluminacao())
                    .objetosDetectados(analise.getObjetosDetectados())
                    .sugestoes(analise.getSugestoes())
                    .resumoAnalise(analise.getResumoAnalise())
                    .timestamp(LocalDateTime.now())
                    .idAlerta(saved.getIdAlerta())
                    .build();

        } catch (Exception e) {
            log.error("Erro ao analisar ambiente de trabalho", e);
            throw new RuntimeException("Erro ao processar imagem: " + e.getMessage());
        }
    }

    /**
     * Calcula nível de risco baseado na análise do ambiente
     */
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

