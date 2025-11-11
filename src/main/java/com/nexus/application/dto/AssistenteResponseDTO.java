package com.nexus.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssistenteResponseDTO {
    private String titulo;
    private String conteudo;
    private String tipo; // "curiosidade", "prevencao", "motivacao", "dica_pratica", "reflexao"
    private List<String> acoesPraticas; // Como aplicar na vida real
    private String reflexao; // Pergunta ou reflexão para o usuário
    private LocalDateTime timestamp;
}

