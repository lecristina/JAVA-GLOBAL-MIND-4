package com.nexus.infrastructure.repository;

import com.nexus.domain.model.Humor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HumorRepository extends JpaRepository<Humor, Integer> {
    Page<Humor> findByUsuario_IdUsuario(Integer idUsuario, Pageable pageable);
    List<Humor> findByUsuario_IdUsuarioAndDataRegistroBetween(
        Integer idUsuario, LocalDate inicio, LocalDate fim);
}

