package com.nexus.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SprintDTO {
    @Schema(description = "ID da sprint (gerado automaticamente, não enviar no POST)", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer idSprint;
    
    @Schema(description = "ID do usuário (obrigatório para criação)", required = true)
    @NotNull(message = "ID do usuário é obrigatório")
    private Integer idUsuario;
    
    @NotBlank(message = "{sprint.nome.obrigatorio}")
    private String nomeSprint;
    
    @NotNull(message = "{sprint.dataInicio.obrigatoria}")
    private LocalDate dataInicio;
    
    private LocalDate dataFim;
    private BigDecimal produtividade;
    private Integer tarefasConcluidas;
    private Integer commits;
}


