package com.nexus.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertaIADTO {
    private Integer idAlerta;
    private Integer idUsuario;
    private LocalDate dataAlerta;
    private String tipoAlerta;
    private String mensagem;
    private Integer nivelRisco;
}



