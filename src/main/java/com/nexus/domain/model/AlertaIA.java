package com.nexus.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "t_mt_alertas_ia")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertaIA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alerta")
    private Integer idAlerta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "data_alerta")
    private LocalDate dataAlerta;

    @Column(name = "tipo_alerta", nullable = false, length = 50)
    private String tipoAlerta;

    @Column(name = "mensagem", length = 255)
    private String mensagem;

    @Column(name = "nivel_risco")
    @Min(value = 1, message = "Nível de risco deve ser entre 1 e 5")
    @Max(value = 5, message = "Nível de risco deve ser entre 1 e 5")
    private Integer nivelRisco;
}


