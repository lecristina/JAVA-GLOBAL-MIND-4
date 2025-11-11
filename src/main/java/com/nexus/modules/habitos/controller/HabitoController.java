package com.nexus.modules.habitos.controller;

import com.nexus.application.dto.HabitoDTO;
import com.nexus.modules.habitos.service.HabitoService;
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
@RequestMapping("/api/habitos")
@RequiredArgsConstructor
@Tag(name = "Hábitos Saudáveis", description = "Gerenciamento de hábitos saudáveis")
@SecurityRequirement(name = "bearerAuth")
public class HabitoController {

    private final HabitoService habitoService;

    @PostMapping
    @Operation(summary = "Criar hábito")
    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'GESTOR')")
    public ResponseEntity<HabitoDTO> criar(@Valid @RequestBody HabitoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(habitoService.criar(dto));
    }

    @GetMapping("/usuario/{idUsuario}")
    @Operation(summary = "Listar hábitos por usuário (paginado)")
    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'GESTOR')")
    public ResponseEntity<Page<HabitoDTO>> listarPorUsuario(
            @PathVariable("idUsuario") Integer idUsuario,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(habitoService.listarPorUsuario(idUsuario, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar hábito por ID")
    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'GESTOR')")
    public ResponseEntity<HabitoDTO> buscarPorId(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(habitoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar hábito")
    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'GESTOR')")
    public ResponseEntity<HabitoDTO> atualizar(
            @PathVariable("id") Integer id,
            @Valid @RequestBody HabitoDTO dto) {
        return ResponseEntity.ok(habitoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar hábito")
    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'GESTOR')")
    public ResponseEntity<Void> deletar(@PathVariable("id") Integer id) {
        habitoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usuario/{idUsuario}/pontuacao")
    @Operation(summary = "Obter pontuação total do usuário")
    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'GESTOR')")
    public ResponseEntity<Integer> obterPontuacaoTotal(@PathVariable("idUsuario") Integer idUsuario) {
        return ResponseEntity.ok(habitoService.obterPontuacaoTotal(idUsuario));
    }
}


