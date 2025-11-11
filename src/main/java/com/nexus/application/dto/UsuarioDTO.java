package com.nexus.application.dto;

import com.nexus.domain.model.Usuario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    @Schema(description = "ID do usuário (gerado automaticamente, não enviar no POST)", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer idUsuario;
    
    @NotBlank(message = "{usuario.nome.obrigatorio}")
    private String nome;
    
    @NotBlank(message = "{usuario.email.obrigatorio}")
    @Email(message = "{usuario.email.invalido}")
    private String email;
    
    private String senha;
    
    @NotNull(message = "{usuario.perfil.obrigatorio}")
    private Usuario.PerfilUsuario perfil;
    
    private LocalDate dataCadastro;
    private String empresa;
}


