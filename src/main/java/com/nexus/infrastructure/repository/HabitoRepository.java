package com.nexus.infrastructure.repository;

import com.nexus.domain.model.Habito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HabitoRepository extends JpaRepository<Habito, Integer> {
    Page<Habito> findByUsuario_IdUsuario(Integer idUsuario, Pageable pageable);
    List<Habito> findByUsuario_IdUsuarioAndDataHabitoBetween(
        Integer idUsuario, LocalDate inicio, LocalDate fim);
    
    @Query("SELECT SUM(h.pontuacao) FROM Habito h WHERE h.usuario.idUsuario = :idUsuario")
    Integer calcularPontuacaoTotal(@Param("idUsuario") Integer idUsuario);
}

