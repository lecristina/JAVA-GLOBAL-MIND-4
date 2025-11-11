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
@Table(name = "t_mt_humor")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Humor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_humor")
    private Integer idHumor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "data_registro")
    private LocalDate dataRegistro;

    @Column(name = "nivel_humor")
    @Min(value = 1, message = "Nível de humor deve ser entre 1 e 5")
    @Max(value = 5, message = "Nível de humor deve ser entre 1 e 5")
    private Integer nivelHumor;

    @Column(name = "nivel_energia")
    @Min(value = 1, message = "Nível de energia deve ser entre 1 e 5")
    @Max(value = 5, message = "Nível de energia deve ser entre 1 e 5")
    private Integer nivelEnergia;

    @Column(name = "comentario", length = 255)
    private String comentario;
}


