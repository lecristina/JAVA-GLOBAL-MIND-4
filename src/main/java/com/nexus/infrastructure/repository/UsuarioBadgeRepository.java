package com.nexus.infrastructure.repository;

import com.nexus.domain.model.UsuarioBadge;
import com.nexus.domain.model.UsuarioBadgeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioBadgeRepository extends JpaRepository<UsuarioBadge, UsuarioBadgeId> {
    List<UsuarioBadge> findById_Usuario(Integer idUsuario);
    boolean existsById(UsuarioBadgeId id);
}

