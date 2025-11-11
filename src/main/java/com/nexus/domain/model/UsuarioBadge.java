package com.nexus.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "t_mt_usuario_badges")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioBadge {

    @EmbeddedId
    private UsuarioBadgeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false, insertable = false, updatable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_badge", nullable = false, insertable = false, updatable = false)
    private Badge badge;

    @Column(name = "data_conquista")
    private LocalDate dataConquista;
}

