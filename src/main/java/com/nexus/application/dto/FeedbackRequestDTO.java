package com.nexus.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackRequestDTO {
    
    @NotNull(message = "ID do usuário é obrigatório")
    private Integer usuarioId;
    
    @Min(value = 1, message = "Nível de humor deve ser entre 1 e 5")
    @Max(value = 5, message = "Nível de humor deve ser entre 1 e 5")
    private Integer humor;
    
    private String produtividade; // "alta", "media", "baixa"
}
