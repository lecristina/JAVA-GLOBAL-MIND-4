package com.nexus.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponseDTO {
    
    private String resposta;
    private Integer idConversa;
    private Integer idConversaPai;
    private LocalDateTime timestamp;
    private String contexto;
}








