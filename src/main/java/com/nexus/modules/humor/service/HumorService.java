package com.nexus.modules.humor.service;

import com.nexus.application.dto.HumorDTO;
import com.nexus.application.mapper.MoodEntryMapper;
import com.nexus.domain.model.Humor;
import com.nexus.domain.model.Usuario;
import com.nexus.infrastructure.repository.HumorRepository;
import com.nexus.infrastructure.repository.UsuarioRepository;
import com.nexus.messaging.events.BurnoutAlertEvent;
import com.nexus.messaging.producer.AlertProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class HumorService {

    private final HumorRepository humorRepository;
    private final UsuarioRepository usuarioRepository;
    private final MoodEntryMapper moodEntryMapper;
    private final AlertProducer alertProducer;

    @Transactional
    @CacheEvict(value = "humor", allEntries = true)
    public HumorDTO criar(HumorDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Humor humor = moodEntryMapper.toEntity(dto);
        humor.setUsuario(usuario);
        humor.setDataRegistro(dto.getDataRegistro() != null ? dto.getDataRegistro() : LocalDate.now());

        Humor saved = humorRepository.save(humor);
        humorRepository.flush(); // Garantir que os dados sejam persistidos imediatamente

        // Verificar se deve gerar alerta de burnout
        verificarAlertaBurnout(saved);

        log.info("Humor criado com sucesso: ID={}, Usuário={}", saved.getIdHumor(), saved.getUsuario().getIdUsuario());
        return moodEntryMapper.toDTO(saved);
    }

    @Cacheable(value = "humor", key = "#idUsuario + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<HumorDTO> listarPorUsuario(Integer idUsuario, Pageable pageable) {
        return humorRepository.findByUsuario_IdUsuario(idUsuario, pageable)
                .map(moodEntryMapper::toDTO);
    }

    public HumorDTO buscarPorId(Integer id) {
        Humor humor = humorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Humor não encontrado"));
        return moodEntryMapper.toDTO(humor);
    }

    @Transactional
    @CacheEvict(value = "humor", allEntries = true)
    public HumorDTO atualizar(Integer id, HumorDTO dto) {
        Humor humor = humorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Humor não encontrado"));

        humor.setNivelHumor(dto.getNivelHumor());
        humor.setNivelEnergia(dto.getNivelEnergia());
        humor.setComentario(dto.getComentario());

        Humor updated = humorRepository.save(humor);
        verificarAlertaBurnout(updated);

        return moodEntryMapper.toDTO(updated);
    }

    @Transactional
    @CacheEvict(value = "humor", allEntries = true)
    public void deletar(Integer id) {
        humorRepository.deleteById(id);
    }

    private void verificarAlertaBurnout(Humor humor) {
        // Se humor ou energia estão baixos (<= 2), enviar alerta
        if (humor.getNivelHumor() != null && humor.getNivelEnergia() != null) {
            if (humor.getNivelHumor() <= 2 || humor.getNivelEnergia() <= 2) {
                int nivelRisco = calcularNivelRisco(humor.getNivelHumor(), humor.getNivelEnergia());
                
                BurnoutAlertEvent event = BurnoutAlertEvent.builder()
                        .idUsuario(humor.getUsuario().getIdUsuario())
                        .nivelHumor(humor.getNivelHumor())
                        .nivelEnergia(humor.getNivelEnergia())
                        .comentario(humor.getComentario())
                        .tipoAlerta("BURNOUT_RISK")
                        .nivelRisco(nivelRisco)
                        .build();

                alertProducer.sendBurnoutAlert(event);
                log.info("Alerta de burnout enviado para usuário: {}", humor.getUsuario().getIdUsuario());
            }
        }
    }

    private int calcularNivelRisco(Integer humor, Integer energia) {
        int soma = humor + energia;
        if (soma <= 3) return 5; // Risco muito alto
        if (soma <= 4) return 4; // Risco alto
        return 3; // Risco médio
    }
}

