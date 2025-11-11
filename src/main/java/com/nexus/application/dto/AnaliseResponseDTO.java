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
public class AnaliseResponseDTO {
    private String resumoSemanal;
    private String riscoBurnout; // "baixo", "medio", "alto"
    private List<String> sugestoes;
    private LocalDateTime timestamp;
}
