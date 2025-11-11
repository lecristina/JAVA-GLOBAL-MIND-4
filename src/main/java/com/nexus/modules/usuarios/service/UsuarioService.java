package com.nexus.modules.usuarios.service;

import com.nexus.application.dto.LoginRequest;
import com.nexus.application.dto.LoginResponse;
import com.nexus.application.dto.UsuarioDTO;
import com.nexus.application.mapper.UserMapper;
import com.nexus.domain.model.Usuario;
import com.nexus.infrastructure.repository.UsuarioRepository;
import com.nexus.security.CustomUserDetailsService;
import com.nexus.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    @Transactional
    public UsuarioDTO registrar(UsuarioDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }

        Usuario usuario = userMapper.toEntity(dto);
        usuario.setSenhaHash(passwordEncoder.encode(dto.getSenha()));
        usuario.setDataCadastro(LocalDate.now());
        
        Usuario saved = usuarioRepository.save(usuario);
        usuarioRepository.flush(); // Garantir que os dados sejam persistidos imediatamente
        log.info("Usuário registrado e salvo no banco: ID={}, Email={}", saved.getIdUsuario(), saved.getEmail());
        
        return userMapper.toDTO(saved);
    }

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getSenha()
                )
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtService.generateToken(userDetails);

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return LoginResponse.builder()
                .token(token)
                .tipoToken("Bearer")
                .usuario(userMapper.toDTO(usuario))
                .build();
    }

    public UsuarioDTO buscarPorId(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return userMapper.toDTO(usuario);
    }
}



