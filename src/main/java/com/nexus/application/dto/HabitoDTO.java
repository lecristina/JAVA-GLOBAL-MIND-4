package com.nexus.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HabitoDTO {
    @Schema(description = "ID do hábito (gerado automaticamente, não enviar no POST)", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer idHabito;
    
    @Schema(description = "ID do usuário (obrigatório para criação)", required = true)
    @NotNull(message = "ID do usuário é obrigatório")
    private Integer idUsuario;
    
    @NotBlank(message = "{habito.tipo.obrigatorio}")
    private String tipoHabito;
    
    @NotNull(message = "{habito.data.obrigatoria}")
    private LocalDate dataHabito;
    
    private Integer pontuacao;
}


