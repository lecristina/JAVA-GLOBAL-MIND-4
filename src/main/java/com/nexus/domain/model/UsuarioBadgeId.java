package com.nexus.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UsuarioBadgeId implements Serializable {

    @Column(name = "id_usuario")
    private Integer usuario;

    @Column(name = "id_badge")
    private Integer badge;
}

