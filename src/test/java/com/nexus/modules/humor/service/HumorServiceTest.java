package com.nexus.modules.humor.service;

import com.nexus.application.dto.HumorDTO;
import com.nexus.domain.model.Humor;
import com.nexus.domain.model.Usuario;
import com.nexus.infrastructure.repository.HumorRepository;
import com.nexus.infrastructure.repository.UsuarioRepository;
import com.nexus.messaging.producer.AlertProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HumorServiceTest {

    @Mock
    private HumorRepository humorRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AlertProducer alertProducer;

    @InjectMocks
    private HumorService humorService;

    private HumorDTO humorDTO;
    private Usuario usuario;
    private Humor humor;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .idUsuario(1)
                .email("teste@example.com")
                .build();

        humorDTO = HumorDTO.builder()
                .idUsuario(1)
                .nivelHumor(3)
                .nivelEnergia(4)
                .dataRegistro(LocalDate.now())
                .build();

        humor = Humor.builder()
                .idHumor(1)
                .usuario(usuario)
                .nivelHumor(3)
                .nivelEnergia(4)
                .dataRegistro(LocalDate.now())
                .build();
    }

    @Test
    void testCriarHumor() {
        when(usuarioRepository.findById(anyInt())).thenReturn(Optional.of(usuario));
        when(humorRepository.save(any(Humor.class))).thenReturn(humor);

        HumorDTO result = humorService.criar(humorDTO);

        assertNotNull(result);
        verify(humorRepository, times(1)).save(any(Humor.class));
    }

    @Test
    void testCriarHumorComAlertaBurnout() {
        humorDTO.setNivelHumor(1);
        humorDTO.setNivelEnergia(2);
        humor.setNivelHumor(1);
        humor.setNivelEnergia(2);

        when(usuarioRepository.findById(anyInt())).thenReturn(Optional.of(usuario));
        when(humorRepository.save(any(Humor.class))).thenReturn(humor);

        HumorDTO result = humorService.criar(humorDTO);

        assertNotNull(result);
        verify(alertProducer, times(1)).sendBurnoutAlert(any());
    }
}



