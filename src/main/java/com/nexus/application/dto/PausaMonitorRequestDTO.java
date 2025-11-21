package com.nexus.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para requisição de monitoramento de pausas
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requisição para monitoramento de pausas e movimento")
public class PausaMonitorRequestDTO {

    @NotNull(message = "ID do usuário é obrigatório")
    @Schema(description = "ID do usuário", example = "1", required = true)
    private Integer usuarioId;

    @NotNull(message = "Frame é obrigatório")
    @Schema(description = "Frame de vídeo em base64 (JPEG ou PNG)", example = "iVBORw0KGgoAAAANS...", required = true)
    private String frameBase64;

    @Schema(description = "Resetar sessão de monitoramento (opcional)", example = "false")
    private Boolean resetarSessao;
}


