package com.nexus.modules.habitos.service;

import com.nexus.application.dto.HabitoDTO;
import com.nexus.domain.model.Habito;
import com.nexus.domain.model.Usuario;
import com.nexus.infrastructure.repository.HabitoRepository;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HabitoServiceTest {

    @Mock
    private HabitoRepository habitoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private com.nexus.infrastructure.repository.BadgeRepository badgeRepository;

    @Mock
    private com.nexus.application.mapper.HabitMapper habitMapper;

    @InjectMocks
    private HabitoService habitoService;

    private HabitoDTO habitoDTO;
    private Usuario usuario;
    private Habito habito;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .idUsuario(1)
                .email("teste@example.com")
                .nome("Teste Usuario")
                .build();

        habitoDTO = HabitoDTO.builder()
                .idUsuario(1)
                .tipoHabito("Exercício físico")
                .pontuacao(10)
                .dataHabito(LocalDate.now())
                .build();

        habito = Habito.builder()
                .idHabito(1)
                .usuario(usuario)
                .tipoHabito("Exercício físico")
                .pontuacao(10)
                .dataHabito(LocalDate.now())
                .build();
    }

    @Test
    void testCriarHabito() {
        when(usuarioRepository.findById(anyInt())).thenReturn(Optional.of(usuario));
        when(habitMapper.toEntity(any(HabitoDTO.class))).thenReturn(habito);
        when(badgeRepository.findByPontosRequeridosLessThanEqualOrderByPontosRequeridosAsc(anyInt())).thenReturn(List.of());
        when(habitoRepository.save(any(Habito.class))).thenReturn(habito);
        when(habitMapper.toDTO(any(Habito.class))).thenReturn(habitoDTO);

        HabitoDTO result = habitoService.criar(habitoDTO);

        assertNotNull(result);
        assertEquals(habitoDTO.getTipoHabito(), result.getTipoHabito());
        assertEquals(habitoDTO.getPontuacao(), result.getPontuacao());
        verify(habitoRepository, times(1)).save(any(Habito.class));
    }

    @Test
    void testListarPorUsuario() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Habito> habitoPage = new PageImpl<>(List.of(habito), pageable, 1);

        when(habitoRepository.findByUsuario_IdUsuario(anyInt(), any(Pageable.class)))
                .thenReturn(habitoPage);
        when(habitMapper.toDTO(any(Habito.class))).thenReturn(habitoDTO);

        Page<HabitoDTO> result = habitoService.listarPorUsuario(1, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(habitoRepository, times(1)).findByUsuario_IdUsuario(anyInt(), any(Pageable.class));
    }

    @Test
    void testBuscarPorId() {
        habitoDTO.setIdHabito(1);
        when(habitoRepository.findById(anyInt())).thenReturn(Optional.of(habito));
        when(habitMapper.toDTO(any(Habito.class))).thenReturn(habitoDTO);

        HabitoDTO result = habitoService.buscarPorId(1);

        assertNotNull(result);
        assertEquals(1, result.getIdHabito());
        verify(habitoRepository, times(1)).findById(anyInt());
    }

    @Test
    void testAtualizarHabito() {
        habitoDTO.setPontuacao(15);
        habito.setPontuacao(15);

        when(habitoRepository.findById(anyInt())).thenReturn(Optional.of(habito));
        when(badgeRepository.findByPontosRequeridosLessThanEqualOrderByPontosRequeridosAsc(anyInt())).thenReturn(List.of());
        when(habitoRepository.save(any(Habito.class))).thenReturn(habito);
        when(habitMapper.toDTO(any(Habito.class))).thenReturn(habitoDTO);

        HabitoDTO result = habitoService.atualizar(1, habitoDTO);

        assertNotNull(result);
        assertEquals(15, result.getPontuacao());
        verify(habitoRepository, times(1)).save(any(Habito.class));
    }

    @Test
    void testDeletarHabito() {
        when(habitoRepository.findById(anyInt())).thenReturn(Optional.of(habito));
        doNothing().when(habitoRepository).deleteById(anyInt());

        assertDoesNotThrow(() -> habitoService.deletar(1));
        verify(habitoRepository, times(1)).deleteById(anyInt());
    }
}

