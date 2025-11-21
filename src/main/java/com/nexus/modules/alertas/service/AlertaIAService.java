package com.nexus.modules.alertas.service;

import com.nexus.ai.AIService;
import com.nexus.application.dto.AlertaIADTO;
import com.nexus.domain.model.AlertaIA;
import com.nexus.infrastructure.repository.AlertaIARepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AlertaIAService {

    private final AlertaIARepository alertaIARepository;
    
    @Autowired(required = false)
    @Lazy
    private AIService aiService;
    
    public AlertaIAService(AlertaIARepository alertaIARepository) {
        this.alertaIARepository = alertaIARepository;
    }

    public Page<AlertaIADTO> listarPorUsuario(Integer idUsuario, Pageable pageable) {
        return alertaIARepository.findByUsuario_IdUsuario(idUsuario, pageable)
                .map(this::toDTO);
    }

    public AlertaIADTO buscarPorId(Integer id) {
        AlertaIA alerta = alertaIARepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alerta não encontrado"));
        return toDTO(alerta);
    }
    
    private AlertaIADTO toDTO(AlertaIA alerta) {
        AlertaIADTO dto = new AlertaIADTO();
        dto.setIdAlerta(alerta.getIdAlerta());
        dto.setIdUsuario(alerta.getUsuario().getIdUsuario());
        dto.setTipoAlerta(alerta.getTipoAlerta());
        dto.setMensagem(alerta.getMensagem());
        dto.setNivelRisco(alerta.getNivelRisco());
        dto.setDataAlerta(alerta.getDataAlerta());
        return dto;
    }

    public String obterMensagemEmpatica(Integer idUsuario) {
        if (aiService != null) {
            try {
                return aiService.gerarMensagemEmpatica(idUsuario);
            } catch (Exception e) {
                log.warn("Erro ao usar AIService, usando fallback", e);
            }
        }
        return "Estamos aqui para apoiá-lo. Lembre-se de cuidar de si mesmo.";
    }

    public String obterAnaliseRisco(Integer idUsuario) {
        if (aiService != null) {
            try {
                return aiService.analisarRiscoBurnout(idUsuario);
            } catch (Exception e) {
                log.warn("Erro ao usar AIService, usando fallback", e);
            }
        }
        return "Risco: MÉDIO. Recomendamos monitoramento contínuo do bem-estar.";
    }
}

