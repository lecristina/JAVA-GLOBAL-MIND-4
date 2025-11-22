package com.nexus.modules.badges.service;

import com.nexus.application.dto.BadgeDTO;
import com.nexus.application.mapper.BadgeMapper;
import com.nexus.domain.model.Badge;
import com.nexus.infrastructure.repository.BadgeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final BadgeMapper badgeMapper;

    @Transactional
    @CacheEvict(value = "badges", allEntries = true)
    public BadgeDTO criar(BadgeDTO dto) {
        log.debug("🗑️ Cache 'badges' invalidado - novo badge sendo criado");
        Badge badge = badgeMapper.toEntity(dto);
        Badge saved = badgeRepository.save(badge);
        badgeRepository.flush(); // Garantir que os dados sejam persistidos imediatamente
        log.info("Badge criado e salvo no banco: ID={}, Nome={}", saved.getIdBadge(), saved.getNomeBadge());
        return badgeMapper.toDTO(saved);
    }

    @Cacheable(value = "badges")
    public List<BadgeDTO> listarTodos() {
        log.debug("🔍 Buscando badges - Verificando cache primeiro...");
        List<BadgeDTO> result = badgeRepository.findAll().stream()
                .map(badgeMapper::toDTO)
                .collect(Collectors.toList());
        log.debug("✅ Dados retornados do cache ou banco de dados");
        return result;
    }

    public BadgeDTO buscarPorId(Integer id) {
        Badge badge = badgeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Badge não encontrado"));
        return badgeMapper.toDTO(badge);
    }

    @Transactional
    @CacheEvict(value = "badges", allEntries = true)
    public BadgeDTO atualizar(Integer id, BadgeDTO dto) {
        Badge badge = badgeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Badge não encontrado"));

        badge.setNomeBadge(dto.getNomeBadge());
        badge.setDescricao(dto.getDescricao());
        badge.setPontosRequeridos(dto.getPontosRequeridos());

        Badge updated = badgeRepository.save(badge);
        return badgeMapper.toDTO(updated);
    }

    @Transactional
    @CacheEvict(value = "badges", allEntries = true)
    public void deletar(Integer id) {
        badgeRepository.deleteById(id);
    }
}



