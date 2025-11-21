package com.nexus.modules.sprints.service;

import com.nexus.application.dto.SprintDTO;
import com.nexus.domain.model.Sprint;
import com.nexus.domain.model.Usuario;
import com.nexus.infrastructure.repository.SprintRepository;
import com.nexus.infrastructure.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SprintServiceTest {

    @Mock
    private SprintRepository sprintRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private com.nexus.application.mapper.SprintMapper sprintMapper;

    @InjectMocks
    private SprintService sprintService;

    private SprintDTO sprintDTO;
    private Usuario usuario;
    private Sprint sprint;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .idUsuario(1)
                .email("teste@example.com")
                .nome("Teste Usuario")
                .build();

        sprintDTO = SprintDTO.builder()
                .idUsuario(1)
                .nomeSprint("Sprint 1")
                .dataInicio(LocalDate.now())
                .dataFim(LocalDate.now().plusDays(14))
                .produtividade(new BigDecimal("75.5"))
                .build();

        sprint = Sprint.builder()
                .idSprint(1)
                .usuario(usuario)
                .nomeSprint("Sprint 1")
                .dataInicio(LocalDate.now())
                .dataFim(LocalDate.now().plusDays(14))
                .produtividade(new BigDecimal("75.5"))
                .build();
    }

    @Test
    void testCriarSprint() {
        when(usuarioRepository.findById(anyInt())).thenReturn(Optional.of(usuario));
        when(sprintRepository.existsByUsuario_IdUsuarioAndNomeSprint(anyInt(), anyString())).thenReturn(false);
        when(sprintMapper.toEntity(any(SprintDTO.class))).thenReturn(sprint);
        when(sprintRepository.save(any(Sprint.class))).thenReturn(sprint);
        when(sprintMapper.toDTO(any(Sprint.class))).thenReturn(sprintDTO);

        SprintDTO result = sprintService.criar(sprintDTO);

        assertNotNull(result);
        assertEquals(sprintDTO.getProdutividade(), result.getProdutividade());
        verify(sprintRepository, times(1)).save(any(Sprint.class));
    }

    @Test
    void testListarPorUsuario() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Sprint> sprintPage = new PageImpl<>(List.of(sprint), pageable, 1);

        when(sprintRepository.findByUsuario_IdUsuario(anyInt(), any(Pageable.class)))
                .thenReturn(sprintPage);
        when(sprintMapper.toDTO(any(Sprint.class))).thenReturn(sprintDTO);

        Page<SprintDTO> result = sprintService.listarPorUsuario(1, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(sprintRepository, times(1)).findByUsuario_IdUsuario(anyInt(), any(Pageable.class));
    }

    @Test
    void testBuscarPorId() {
        sprintDTO.setIdSprint(1);
        when(sprintRepository.findById(anyInt())).thenReturn(Optional.of(sprint));
        when(sprintMapper.toDTO(any(Sprint.class))).thenReturn(sprintDTO);

        SprintDTO result = sprintService.buscarPorId(1);

        assertNotNull(result);
        assertEquals(1, result.getIdSprint());
        verify(sprintRepository, times(1)).findById(anyInt());
    }

    @Test
    void testAtualizarSprint() {
        sprintDTO.setProdutividade(new BigDecimal("85.0"));
        sprint.setProdutividade(new BigDecimal("85.0"));

        when(sprintRepository.findById(anyInt())).thenReturn(Optional.of(sprint));
        when(sprintRepository.save(any(Sprint.class))).thenReturn(sprint);
        when(sprintMapper.toDTO(any(Sprint.class))).thenReturn(sprintDTO);

        SprintDTO result = sprintService.atualizar(1, sprintDTO);

        assertNotNull(result);
        assertEquals(new BigDecimal("85.0"), result.getProdutividade());
        verify(sprintRepository, times(1)).save(any(Sprint.class));
    }

    @Test
    void testDeletarSprint() {
        doNothing().when(sprintRepository).deleteById(anyInt());

        assertDoesNotThrow(() -> sprintService.deletar(1));
        verify(sprintRepository, times(1)).deleteById(anyInt());
    }

    @Test
    void testBuscarPorIdNaoEncontrado() {
        when(sprintRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> sprintService.buscarPorId(999));
    }
}

