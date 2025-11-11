package com.nexus.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgeDTO {
    @Schema(description = "ID do badge (gerado automaticamente, não enviar no POST)", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer idBadge;
    
    @NotBlank(message = "{badge.nome.obrigatorio}")
    private String nomeBadge;
    
    private String descricao;
    
    @NotNull(message = "{badge.pontos.obrigatorio}")
    @Min(value = 0, message = "{badge.pontos.minimo}")
    private Integer pontosRequeridos;
}


