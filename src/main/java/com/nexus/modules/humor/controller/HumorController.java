package com.nexus.modules.humor.controller;

import com.nexus.application.dto.HumorDTO;
import com.nexus.modules.humor.service.HumorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/humor")
@RequiredArgsConstructor
@Tag(name = "Humor e Energia", description = "Gerenciamento de registros de humor e energia")
@SecurityRequirement(name = "bearerAuth")
public class HumorController {

    private final HumorService humorService;

    @PostMapping
    @Operation(summary = "Criar registro de humor")
    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'GESTOR')")
    public ResponseEntity<HumorDTO> criar(@Valid @RequestBody HumorDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(humorService.criar(dto));
    }

    @GetMapping("/usuario/{idUsuario}")
    @Operation(summary = "Listar registros de humor por usuário (paginado)")
    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'GESTOR')")
    public ResponseEntity<Page<HumorDTO>> listarPorUsuario(
            @PathVariable("idUsuario") Integer idUsuario,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(humorService.listarPorUsuario(idUsuario, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar registro de humor por ID")
    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'GESTOR')")
    public ResponseEntity<HumorDTO> buscarPorId(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(humorService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar registro de humor")
    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'GESTOR')")
    public ResponseEntity<HumorDTO> atualizar(
            @PathVariable("id") Integer id,
            @Valid @RequestBody HumorDTO dto) {
        return ResponseEntity.ok(humorService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar registro de humor")
    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'GESTOR')")
    public ResponseEntity<Void> deletar(@PathVariable("id") Integer id) {
        humorService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}


