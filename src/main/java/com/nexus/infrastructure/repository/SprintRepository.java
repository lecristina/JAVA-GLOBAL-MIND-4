package com.nexus.infrastructure.repository;

import com.nexus.domain.model.Sprint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SprintRepository extends JpaRepository<Sprint, Integer> {
    Page<Sprint> findByUsuario_IdUsuario(Integer idUsuario, Pageable pageable);
    Optional<Sprint> findByUsuario_IdUsuarioAndNomeSprint(Integer idUsuario, String nomeSprint);
    boolean existsByUsuario_IdUsuarioAndNomeSprint(Integer idUsuario, String nomeSprint);
}

