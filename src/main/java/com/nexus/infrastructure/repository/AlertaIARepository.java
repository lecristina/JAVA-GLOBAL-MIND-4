package com.nexus.infrastructure.repository;

import com.nexus.domain.model.AlertaIA;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AlertaIARepository extends JpaRepository<AlertaIA, Integer> {
    Page<AlertaIA> findByUsuario_IdUsuario(Integer idUsuario, Pageable pageable);
    
    List<AlertaIA> findByUsuario_IdUsuarioAndTipoAlertaAndDataAlertaAfter(
            Integer idUsuario, String tipoAlerta, LocalDate dataAlerta);
}

