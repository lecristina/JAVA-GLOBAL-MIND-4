package com.nexus.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    
    @NotBlank(message = "{login.email.obrigatorio}")
    @Email(message = "{login.email.invalido}")
    private String email;
    
    @NotBlank(message = "{login.senha.obrigatorio}")
    private String senha;
}










