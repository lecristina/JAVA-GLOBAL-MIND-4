package com.nexus.modules.sprints.service;

import com.nexus.ai.AIService;
import com.nexus.application.dto.SprintDTO;
import com.nexus.application.mapper.SprintMapper;
import com.nexus.domain.model.Sprint;
import com.nexus.domain.model.Usuario;
import com.nexus.infrastructure.repository.SprintRepository;
import com.nexus.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class SprintService {

    private final SprintRepository sprintRepository;
    private final UsuarioRepository usuarioRepository;
    private final SprintMapper sprintMapper;
    private final AIService aiService;

    @Transactional
    @CacheEvict(value = "sprints", allEntries = true)
    public SprintDTO criar(SprintDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (sprintRepository.existsByUsuario_IdUsuarioAndNomeSprint(dto.getIdUsuario(), dto.getNomeSprint())) {
            throw new RuntimeException("Sprint com este nome já existe para este usuário");
        }

        Sprint sprint = sprintMapper.toEntity(dto);
        sprint.setUsuario(usuario);
        
        // Calcular produtividade automaticamente se não fornecida
        if (sprint.getProdutividade() == null && sprint.getTarefasConcluidas() != null && sprint.getCommits() != null) {
            BigDecimal produtividade = calcularProdutividade(sprint.getTarefasConcluidas(), sprint.getCommits());
            sprint.setProdutividade(produtividade);
        }

        Sprint saved = sprintRepository.save(sprint);
        sprintRepository.flush(); // Garantir que os dados sejam persistidos imediatamente
        log.info("Sprint criada e salva no banco: ID={}, Usuário={}, Nome={}", saved.getIdSprint(), saved.getUsuario().getIdUsuario(), saved.getNomeSprint());
        return sprintMapper.toDTO(saved);
    }

    @Cacheable(value = "sprints", key = "#idUsuario + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<SprintDTO> listarPorUsuario(Integer idUsuario, Pageable pageable) {
        return sprintRepository.findByUsuario_IdUsuario(idUsuario, pageable)
                .map(sprintMapper::toDTO);
    }

    public SprintDTO buscarPorId(Integer id) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sprint não encontrada"));
        return sprintMapper.toDTO(sprint);
    }

    @Transactional
    @CacheEvict(value = "sprints", allEntries = true)
    public SprintDTO atualizar(Integer id, SprintDTO dto) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sprint não encontrada"));

        sprint.setNomeSprint(dto.getNomeSprint());
        sprint.setDataInicio(dto.getDataInicio());
        sprint.setDataFim(dto.getDataFim());
        sprint.setTarefasConcluidas(dto.getTarefasConcluidas());
        sprint.setCommits(dto.getCommits());

        if (dto.getProdutividade() != null) {
            sprint.setProdutividade(dto.getProdutividade());
        } else if (sprint.getTarefasConcluidas() != null && sprint.getCommits() != null) {
            sprint.setProdutividade(calcularProdutividade(sprint.getTarefasConcluidas(), sprint.getCommits()));
        }

        Sprint updated = sprintRepository.save(sprint);
        return sprintMapper.toDTO(updated);
    }

    @Transactional
    @CacheEvict(value = "sprints", allEntries = true)
    public void deletar(Integer id) {
        sprintRepository.deleteById(id);
    }

    public String obterMensagemMotivacional(Integer idUsuario) {
        return aiService.gerarMensagemMotivacional(idUsuario);
    }

    private BigDecimal calcularProdutividade(Integer tarefas, Integer commits) {
        if (tarefas == null || commits == null || tarefas == 0) {
            return BigDecimal.ZERO;
        }
        // Fórmula simples: (tarefas * 0.6) + (commits * 0.4)
        BigDecimal tarefasScore = BigDecimal.valueOf(tarefas).multiply(BigDecimal.valueOf(0.6));
        BigDecimal commitsScore = BigDecimal.valueOf(commits).multiply(BigDecimal.valueOf(0.4));
        return tarefasScore.add(commitsScore).setScale(2, RoundingMode.HALF_UP);
    }
}

