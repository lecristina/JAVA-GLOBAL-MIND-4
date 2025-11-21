package com.nexus.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TarefaDTO {
    
    /**
     * Horário da tarefa no formato HH:mm (ex: "14:00")
     * Pode ser null se não houver horário específico
     */
    private String horario;
    
    /**
     * Descrição da tarefa (ex: "Levar gata ao veterinário")
     */
    private String descricao;
    
    /**
     * Prioridade da tarefa (opcional)
     * Valores possíveis: "ALTA", "MEDIA", "BAIXA"
     */
    private String prioridade;
}

