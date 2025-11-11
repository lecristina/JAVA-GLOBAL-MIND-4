package com.nexus.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "t_mt_sprints", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"id_usuario", "nome_sprint"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sprint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sprint")
    private Integer idSprint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "nome_sprint", nullable = false, length = 100)
    private String nomeSprint;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Column(name = "produtividade", precision = 5, scale = 2)
    private BigDecimal produtividade;

    @Column(name = "tarefas_concluidas")
    private Integer tarefasConcluidas;

    @Column(name = "commits")
    private Integer commits;
}



