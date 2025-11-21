package com.nexus.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnaliseAmbienteRequestDTO {
    
    @NotNull(message = "ID do usuário é obrigatório")
    private Integer usuarioId;
}







