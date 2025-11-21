package com.nexus.application.dto;

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
public class AssistantAnalisarRequestDTO {
    
    @NotNull(message = "ID do usuário é obrigatório")
    private Integer usuarioId;
    
    @NotBlank(message = "O tipo é obrigatório")
    private String tipo; // "agenda" ou "conteudo" ou "motivacao"
    
    @NotBlank(message = "A mensagem é obrigatória")
    private String mensagem;
}




