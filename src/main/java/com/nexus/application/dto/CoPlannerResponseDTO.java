package com.nexus.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoPlannerResponseDTO {
    
    /**
     * Lista de tarefas extraídas da mensagem
     */
    private List<TarefaDTO> tarefas;
    
    /**
     * Mensagem original do usuário
     */
    private String mensagemOriginal;
    
    /**
     * Timestamp da extração
     */
    private LocalDateTime timestamp;
    
    /**
     * Número total de tarefas extraídas
     */
    private Integer totalTarefas;
    
    /**
     * Mensagem de erro ou informação adicional
     */
    private String mensagem;
}

