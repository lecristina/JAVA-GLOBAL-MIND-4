package com.nexus.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "t_mt_badges")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_badge")
    private Integer idBadge;

    @Column(name = "nome_badge", nullable = false, length = 50)
    private String nomeBadge;

    @Column(name = "descricao", length = 150)
    private String descricao;

    @Column(name = "pontos_requeridos", nullable = false)
    @Min(value = 0, message = "Pontos requeridos deve ser maior ou igual a 0")
    private Integer pontosRequeridos;
}


