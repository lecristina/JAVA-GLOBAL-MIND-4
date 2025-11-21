package com.nexus.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para resposta de monitoramento de pausas
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta do monitoramento de pausas e movimento")
public class PausaMonitorResponseDTO {

    @Schema(description = "ID do usuário", example = "1")
    private Integer usuarioId;

    @Schema(description = "Se movimento foi detectado no frame", example = "true")
    private boolean movimentoDetectado;

    @Schema(description = "Quantidade de pixels diferentes entre frames", example = "25000")
    private int quantidadeMovimento;

    @Schema(description = "Se usuário está presente (não ausente)", example = "true")
    private boolean presente;

    @Schema(description = "Tempo sentado em minutos", example = "75")
    private int tempoSentadoMinutos;

    @Schema(description = "Total de pausas registradas na sessão", example = "3")
    private int totalPausas;

    @Schema(description = "Se deve sugerir alongamento", example = "true")
    private boolean sugerirAlongamento;

    @Schema(description = "Mensagem descritiva do estado atual", example = "Movimento detectado. Usuário presente.")
    private String mensagem;

    @Schema(description = "Lista de sugestões baseadas no estado atual")
    private List<String> sugestoes;

    @Schema(description = "Timestamp da análise", example = "2025-01-15T10:30:00")
    private LocalDateTime timestamp;
}


