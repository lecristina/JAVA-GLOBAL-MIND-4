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
public class AssistenteRequestDTO {
    
    @NotNull(message = "ID do usuário é obrigatório")
    private Integer usuarioId;
    
    private String tipoConsulta; // "curiosidade", "prevencao", "motivacao", "dica_pratica", "reflexao"
}

