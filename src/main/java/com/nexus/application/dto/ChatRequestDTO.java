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
public class ChatRequestDTO {
    
    @NotNull(message = "ID do usuário é obrigatório")
    private Integer usuarioId;
    
    @NotBlank(message = "A mensagem é obrigatória")
    private String mensagem;
    
    /**
     * ID da conversa pai (opcional - para continuar uma conversa existente)
     * Se não fornecido, inicia uma nova conversa
     */
    private Integer idConversaPai;
}

