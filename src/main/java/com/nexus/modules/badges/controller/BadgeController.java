package com.nexus.modules.badges.controller;

import com.nexus.application.dto.BadgeDTO;
import com.nexus.modules.badges.service.BadgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/badges")
@RequiredArgsConstructor
@Tag(name = "Badges", description = "Gerenciamento de badges e gamificação")
@SecurityRequirement(name = "bearerAuth")
public class BadgeController {

    private final BadgeService badgeService;

    @PostMapping
    @Operation(summary = "Criar badge")
    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'GESTOR')")
    public ResponseEntity<BadgeDTO> criar(@Valid @RequestBody BadgeDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(badgeService.criar(dto));
    }

    @GetMapping
    @Operation(summary = "Listar todos os badges")
    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'GESTOR')")
    public ResponseEntity<List<BadgeDTO>> listarTodos() {
        return ResponseEntity.ok(badgeService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar badge por ID")
    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'GESTOR')")
    public ResponseEntity<BadgeDTO> buscarPorId(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(badgeService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar badge")
    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'GESTOR')")
    public ResponseEntity<BadgeDTO> atualizar(
            @PathVariable("id") Integer id,
            @Valid @RequestBody BadgeDTO dto) {
        return ResponseEntity.ok(badgeService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar badge")
    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'GESTOR')")
    public ResponseEntity<Void> deletar(@PathVariable("id") Integer id) {
        badgeService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}



