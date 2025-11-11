package com.nexus.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "t_mt_habitos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Habito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_habito")
    private Integer idHabito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "tipo_habito", nullable = false, length = 50)
    private String tipoHabito;

    @Column(name = "data_habito")
    private LocalDate dataHabito;

    @Column(name = "pontuacao")
    private Integer pontuacao;
}


