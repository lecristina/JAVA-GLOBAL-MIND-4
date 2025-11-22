package com.nexus.modules.usuarios.service;

import com.nexus.application.dto.UsuarioDTO;
import com.nexus.domain.model.Usuario;
import com.nexus.infrastructure.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private com.nexus.application.mapper.UserMapper userMapper;

    @Mock
    private com.nexus.security.JwtService jwtService;

    @Mock
    private org.springframework.security.authentication.AuthenticationManager authenticationManager;

    @Mock
    private com.nexus.security.CustomUserDetailsService userDetailsService;

    @InjectMocks
    private UsuarioService usuarioService;

    private UsuarioDTO usuarioDTO;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuarioDTO = UsuarioDTO.builder()
                .nome("Teste Usuario")
                .email("teste@example.com")
                .senha("senha123")
                .perfil(Usuario.PerfilUsuario.PROFISSIONAL)
                .build();

        usuario = Usuario.builder()
                .idUsuario(1)
                .nome("Teste Usuario")
                .email("teste@example.com")
                .senhaHash("encodedPassword")
                .perfil(Usuario.PerfilUsuario.PROFISSIONAL)
                .build();
    }

    @Test
    void testRegistrarUsuario() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(userMapper.toEntity(any(UsuarioDTO.class))).thenReturn(usuario);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(userMapper.toDTO(any(Usuario.class))).thenReturn(usuarioDTO);

        UsuarioDTO result = usuarioService.registrar(usuarioDTO);

        assertNotNull(result);
        assertEquals(usuarioDTO.getEmail(), result.getEmail());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }
}

