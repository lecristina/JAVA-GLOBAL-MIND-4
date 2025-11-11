package com.nexus.infrastructure.repository;

import com.nexus.domain.model.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Integer> {
    List<Badge> findByPontosRequeridosLessThanEqualOrderByPontosRequeridosAsc(Integer pontos);
}



