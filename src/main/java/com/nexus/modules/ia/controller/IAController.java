package com.nexus.modules.ia.controller;

import com.nexus.application.dto.AnaliseAmbienteResponseDTO;
import com.nexus.application.dto.AnaliseRequestDTO;
import com.nexus.application.dto.AnaliseResponseDTO;
import com.nexus.application.dto.AssistenteRequestDTO;
import com.nexus.application.dto.AssistenteResponseDTO;
import com.nexus.application.dto.FeedbackRequestDTO;
import com.nexus.application.dto.FeedbackResponseDTO;
import com.nexus.modules.ia.service.IAService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/ia")
@RequiredArgsConstructor
@Tag(name = "IA Generativa", description = "Endpoints de IA para feedback empático e análises inteligentes")
@SecurityRequirement(name = "bearerAuth")
public class IAController {

    private final IAService iaService;

    @PostMapping("/feedback")
    @Operation(
        summary = "Gerar feedback empático usando GPT",
        description = "Gera uma mensagem empática personalizada baseada no humor e produtividade do usuário usando GPT. " +
                     "O feedback é armazenado na tabela t_mt_alertas_ia."
    )
    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'GESTOR')")
    public ResponseEntity<FeedbackResponseDTO> gerarFeedback(@Valid @RequestBody FeedbackRequestDTO request) {
        FeedbackResponseDTO response = iaService.gerarFeedback(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/analise")
    @Operation(
        summary = "Gerar análise semanal inteligente usando GPT",
        description = "Analisa dados históricos do usuário (últimos 7 dias) e gera um relatório completo " +
                     "com resumo semanal, risco de burnout e sugestões personalizadas usando GPT."
    )
    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'GESTOR')")
    public ResponseEntity<AnaliseResponseDTO> gerarAnalise(@Valid @RequestBody AnaliseRequestDTO request) {
        AnaliseResponseDTO response = iaService.gerarAnalise(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/assistente")
    @Operation(
        summary = "Assistente pessoal de saúde mental - Conteúdo personalizado",
        description = "Gera conteúdo personalizado do assistente pessoal baseado no tipo de consulta. " +
                     "Tipos disponíveis: 'curiosidade', 'prevencao', 'motivacao', 'dica_pratica', 'reflexao'. " +
                     "Retorna título, conteúdo, ações práticas e reflexão para aplicar na vida real."
    )
    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'GESTOR')")
    public ResponseEntity<AssistenteResponseDTO> gerarConteudoAssistente(@Valid @RequestBody AssistenteRequestDTO request) {
        AssistenteResponseDTO response = iaService.gerarConteudoAssistente(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/analise-ambiente", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Analisar ambiente de trabalho usando Visão Computacional (Deep Learning)",
        description = "Analisa uma foto do ambiente de trabalho usando modelo de Deep Learning (Hugging Face). " +
                     "Detecta objetos, analisa organização, iluminação e nível de foco. " +
                     "Retorna sugestões práticas para melhorar o ambiente de trabalho. " +
                     "A análise é salva na tabela t_mt_alertas_ia com tipo 'ANALISE_AMBIENTE'. " +
                     "\n\n" +
                     "**Como testar no Swagger:**\n" +
                     "1. Clique em 'Try it out'\n" +
                     "2. Preencha 'usuarioId' (ex: 1)\n" +
                     "3. Clique em 'Choose File' e selecione uma imagem\n" +
                     "4. Clique em 'Execute'\n" +
                     "5. Aguarde alguns segundos (primeira chamada pode demorar - modelo carregando)"
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Upload de imagem do ambiente de trabalho",
        required = true,
        content = @io.swagger.v3.oas.annotations.media.Content(
            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE
        )
    )
    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'GESTOR')")
    public ResponseEntity<AnaliseAmbienteResponseDTO> analisarAmbienteTrabalho(
            @io.swagger.v3.oas.annotations.Parameter(description = "Arquivo de imagem (JPEG, PNG, etc)", required = true)
            @RequestParam("foto") MultipartFile foto,
            @io.swagger.v3.oas.annotations.Parameter(description = "ID do usuário", required = true, example = "1")
            @RequestParam("usuarioId") Integer usuarioId) {
        
        AnaliseAmbienteResponseDTO response = iaService.analisarAmbienteTrabalho(foto, usuarioId);
        return ResponseEntity.ok(response);
    }
}

