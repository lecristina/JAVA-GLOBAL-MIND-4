package com.nexus.modules.alertas.controller;

import com.nexus.application.dto.AlertaIADTO;
import com.nexus.modules.alertas.service.AlertaIAService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alertas")
@RequiredArgsConstructor
@Tag(name = "Alertas IA", description = "Gerenciamento de alertas de IA e análises")
@SecurityRequirement(name = "bearerAuth")
public class AlertaIAController {

    private final AlertaIAService alertaIAService;

    @GetMapping("/usuario/{idUsuario}")
    @Operation(summary = "Listar alertas por usuário (paginado)")
    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'GESTOR')")
    public ResponseEntity<Page<AlertaIADTO>> listarPorUsuario(
            @PathVariable("idUsuario") Integer idUsuario,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(alertaIAService.listarPorUsuario(idUsuario, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar alerta por ID")
    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'GESTOR')")
    public ResponseEntity<AlertaIADTO> buscarPorId(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(alertaIAService.buscarPorId(id));
    }

    @GetMapping("/usuario/{idUsuario}/mensagem-empatica")
    @Operation(summary = "Obter mensagem empática gerada por IA")
    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'GESTOR')")
    public ResponseEntity<String> obterMensagemEmpatica(@PathVariable("idUsuario") Integer idUsuario) {
        return ResponseEntity.ok(alertaIAService.obterMensagemEmpatica(idUsuario));
    }

    @GetMapping("/usuario/{idUsuario}/analise-risco")
    @Operation(summary = "Obter análise de risco de burnout gerada por IA")
    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'GESTOR')")
    public ResponseEntity<String> obterAnaliseRisco(@PathVariable("idUsuario") Integer idUsuario) {
        return ResponseEntity.ok(alertaIAService.obterAnaliseRisco(idUsuario));
    }
}


