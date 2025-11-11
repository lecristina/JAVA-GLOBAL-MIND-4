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
public class AnaliseAmbienteResponseDTO {
    private String nivelFoco; // "alto", "medio", "baixo"
    private String organizacao; // "excelente", "boa", "regular", "ruim"
    private String iluminacao; // "excelente", "adequada", "insuficiente"
    private List<String> objetosDetectados;
    private List<String> sugestoes;
    private String resumoAnalise;
    private LocalDateTime timestamp;
    private Integer idAlerta;
}

