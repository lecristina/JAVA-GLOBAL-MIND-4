package com.nexus.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class HumorDTO {
    @Schema(description = "ID do humor (gerado automaticamente, não enviar no POST)", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer idHumor;
    
    @Schema(description = "ID do usuário (obrigatório para criação)", required = true)
    @NotNull(message = "ID do usuário é obrigatório")
    private Integer idUsuario;
    
    @NotNull(message = "{humor.data.obrigatoria}")
    private LocalDate dataRegistro;
    
    @Min(value = 1, message = "{humor.nivel.minimo}")
    @Max(value = 5, message = "{humor.nivel.maximo}")
    private Integer nivelHumor;
    
    @Min(value = 1, message = "{humor.energia.minimo}")
    @Max(value = 5, message = "{humor.energia.maximo}")
    private Integer nivelEnergia;
    
    private String comentario;
}


