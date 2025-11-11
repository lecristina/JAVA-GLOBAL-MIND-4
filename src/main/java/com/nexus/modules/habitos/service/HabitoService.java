package com.nexus.modules.habitos.service;

import com.nexus.application.dto.HabitoDTO;
import com.nexus.application.mapper.HabitMapper;
import com.nexus.domain.model.Badge;
import com.nexus.domain.model.Habito;
import com.nexus.domain.model.Usuario;
import com.nexus.domain.model.UsuarioBadge;
import com.nexus.domain.model.UsuarioBadgeId;
import com.nexus.infrastructure.repository.BadgeRepository;
import com.nexus.infrastructure.repository.HabitoRepository;
import com.nexus.infrastructure.repository.UsuarioBadgeRepository;
import com.nexus.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HabitoService {

    private final HabitoRepository habitoRepository;
    private final UsuarioRepository usuarioRepository;
    private final BadgeRepository badgeRepository;
    private final UsuarioBadgeRepository usuarioBadgeRepository;
    private final HabitMapper habitMapper;

    @Transactional
    @CacheEvict(value = "habitos", allEntries = true)
    public HabitoDTO criar(HabitoDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Habito habito = habitMapper.toEntity(dto);
        habito.setUsuario(usuario);
        habito.setDataHabito(dto.getDataHabito() != null ? dto.getDataHabito() : LocalDate.now());
        
        // Definir pontuação padrão se não fornecida
        if (habito.getPontuacao() == null) {
            habito.setPontuacao(calcularPontuacaoPadrao(habito.getTipoHabito()));
        }

        Habito saved = habitoRepository.save(habito);
        habitoRepository.flush(); // Garantir que os dados sejam persistidos imediatamente
        
        // Verificar e atribuir badges
        verificarEAtribuirBadges(usuario.getIdUsuario());

        log.info("Hábito criado e salvo no banco: ID={}, Usuário={}, Tipo={}", saved.getIdHabito(), saved.getUsuario().getIdUsuario(), saved.getTipoHabito());
        return habitMapper.toDTO(saved);
    }

    @Cacheable(value = "habitos", key = "#idUsuario + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<HabitoDTO> listarPorUsuario(Integer idUsuario, Pageable pageable) {
        return habitoRepository.findByUsuario_IdUsuario(idUsuario, pageable)
                .map(habitMapper::toDTO);
    }

    public HabitoDTO buscarPorId(Integer id) {
        Habito habito = habitoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hábito não encontrado"));
        return habitMapper.toDTO(habito);
    }

    @Transactional
    @CacheEvict(value = "habitos", allEntries = true)
    public HabitoDTO atualizar(Integer id, HabitoDTO dto) {
        Habito habito = habitoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hábito não encontrado"));

        habito.setTipoHabito(dto.getTipoHabito());
        habito.setDataHabito(dto.getDataHabito());
        habito.setPontuacao(dto.getPontuacao() != null ? dto.getPontuacao() : 
                calcularPontuacaoPadrao(dto.getTipoHabito()));

        Habito updated = habitoRepository.save(habito);
        verificarEAtribuirBadges(habito.getUsuario().getIdUsuario());

        return habitMapper.toDTO(updated);
    }

    @Transactional
    @CacheEvict(value = "habitos", allEntries = true)
    public void deletar(Integer id) {
        Habito habito = habitoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hábito não encontrado"));
        Integer idUsuario = habito.getUsuario().getIdUsuario();
        habitoRepository.deleteById(id);
        verificarEAtribuirBadges(idUsuario);
    }

    public Integer obterPontuacaoTotal(Integer idUsuario) {
        Integer total = habitoRepository.calcularPontuacaoTotal(idUsuario);
        return total != null ? total : 0;
    }

    private Integer calcularPontuacaoPadrao(String tipoHabito) {
        return switch (tipoHabito.toUpperCase()) {
            case "EXERCICIO" -> 10;
            case "MEDITACAO" -> 8;
            case "SONO_ADEQUADO" -> 7;
            case "ALIMENTACAO_SAUDAVEL" -> 6;
            case "PAUSA_TRABALHO" -> 5;
            default -> 5;
        };
    }

    private void verificarEAtribuirBadges(Integer idUsuario) {
        Integer pontuacaoTotal = obterPontuacaoTotal(idUsuario);
        List<Badge> badgesElegiveis = badgeRepository.findByPontosRequeridosLessThanEqualOrderByPontosRequeridosAsc(pontuacaoTotal);

        for (Badge badge : badgesElegiveis) {
            UsuarioBadgeId id = new UsuarioBadgeId(idUsuario, badge.getIdBadge());
            if (!usuarioBadgeRepository.existsById(id)) {
                Usuario usuario = usuarioRepository.findById(idUsuario)
                        .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
                
                UsuarioBadge usuarioBadge = UsuarioBadge.builder()
                        .id(id)
                        .usuario(usuario)
                        .badge(badge)
                        .dataConquista(LocalDate.now())
                        .build();

                usuarioBadgeRepository.save(usuarioBadge);
                log.info("Badge '{}' atribuído ao usuário {}", badge.getNomeBadge(), idUsuario);
            }
        }
    }
}

