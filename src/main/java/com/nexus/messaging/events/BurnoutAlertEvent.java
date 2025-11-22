package com.nexus.messaging.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BurnoutAlertEvent implements Serializable {
    private Integer idUsuario;
    private Integer nivelHumor;
    private Integer nivelEnergia;
    private String comentario;
    private String tipoAlerta;
    private Integer nivelRisco;
}










