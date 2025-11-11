package com.nexus.modules.alertas.service;

import com.nexus.ai.AIService;
import com.nexus.application.dto.AlertaIADTO;
import com.nexus.application.mapper.AIAlertMapper;
import com.nexus.domain.model.AlertaIA;
import com.nexus.infrastructure.repository.AlertaIARepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertaIAService {

    private final AlertaIARepository alertaIARepository;
    private final AIAlertMapper aiAlertMapper;
    private final AIService aiService;

    public Page<AlertaIADTO> listarPorUsuario(Integer idUsuario, Pageable pageable) {
        return alertaIARepository.findByUsuario_IdUsuario(idUsuario, pageable)
                .map(aiAlertMapper::toDTO);
    }

    public AlertaIADTO buscarPorId(Integer id) {
        AlertaIA alerta = alertaIARepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alerta não encontrado"));
        return aiAlertMapper.toDTO(alerta);
    }

    public String obterMensagemEmpatica(Integer idUsuario) {
        return aiService.gerarMensagemEmpatica(idUsuario);
    }

    public String obterAnaliseRisco(Integer idUsuario) {
        return aiService.analisarRiscoBurnout(idUsuario);
    }
}

