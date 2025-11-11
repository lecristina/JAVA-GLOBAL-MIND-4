package com.nexus.modules.sprints.controller;

import com.nexus.application.dto.SprintDTO;
import com.nexus.modules.sprints.service.SprintService;
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
@RequestMapping("/api/sprints")
@RequiredArgsConstructor
@Tag(name = "Sprints e Produtividade", description = "Gerenciamento de sprints e produtividade")
@SecurityRequirement(name = "bearerAuth")
public class SprintController {

    private final SprintService sprintService;

    @PostMapping
    @Operation(summary = "Criar sprint")
    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'GESTOR')")
    public ResponseEntity<SprintDTO> criar(@Valid @RequestBody SprintDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sprintService.criar(dto));
    }

    @GetMapping("/usuario/{idUsuario}")
    @Operation(summary = "Listar sprints por usuário (paginado)")
    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'GESTOR')")
    public ResponseEntity<Page<SprintDTO>> listarPorUsuario(
            @PathVariable("idUsuario") Integer idUsuario,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(sprintService.listarPorUsuario(idUsuario, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar sprint por ID")
    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'GESTOR')")
    public ResponseEntity<SprintDTO> buscarPorId(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(sprintService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar sprint")
    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'GESTOR')")
    public ResponseEntity<SprintDTO> atualizar(
            @PathVariable("id") Integer id,
            @Valid @RequestBody SprintDTO dto) {
        return ResponseEntity.ok(sprintService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar sprint")
    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'GESTOR')")
    public ResponseEntity<Void> deletar(@PathVariable("id") Integer id) {
        sprintService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usuario/{idUsuario}/motivacao")
    @Operation(summary = "Obter mensagem motivacional baseada em sprints")
    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'GESTOR')")
    public ResponseEntity<String> obterMensagemMotivacional(@PathVariable("idUsuario") Integer idUsuario) {
        return ResponseEntity.ok(sprintService.obterMensagemMotivacional(idUsuario));
    }
}


