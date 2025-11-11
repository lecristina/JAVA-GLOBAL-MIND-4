package com.nexus.modules.ia.controller;

import com.nexus.application.dto.AnaliseAmbienteResponseDTO;
import com.nexus.application.dto.AnaliseRequestDTO;
import com.nexus.application.dto.AnaliseResponseDTO;
import com.nexus.application.dto.AssistenteRequestDTO;
import com.nexus.application.dto.AssistenteResponseDTO;
import com.nexus.application.dto.ChatRequestDTO;
import com.nexus.application.dto.ChatResponseDTO;
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

    // DESABILITADO: Funcionalidade de análise de imagem removida temporariamente
    /*
    @PostMapping(value = "/analise-ambiente", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Analisar ambiente de trabalho usando Visão Computacional (Deep Learning)",
        description = "DESABILITADO TEMPORARIAMENTE"
    )
    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'GESTOR')")
    public ResponseEntity<AnaliseAmbienteResponseDTO> analisarAmbienteTrabalho(
            @RequestParam("foto") MultipartFile foto,
            @RequestParam("usuarioId") Integer usuarioId) {
        throw new RuntimeException("Funcionalidade de análise de imagem desabilitada temporariamente");
    }
    */

    @PostMapping("/chat")
    @Operation(
        summary = "Chat conversacional com IA - Conversa dinâmica e contextual",
        description = "Permite conversar com a IA de forma natural e dinâmica. " +
                     "A IA mantém o histórico da conversa e contexto do usuário. " +
                     "Você pode fazer perguntas, pedir conselhos, discutir problemas, etc. " +
                     "A conversa é salva automaticamente para manter contexto.\n\n" +
                     "**Como usar:**\n" +
                     "1. Primeira mensagem: envie apenas 'usuarioId' e 'mensagem' (sem 'idConversaPai')\n" +
                     "2. Continuar conversa: use o 'idConversaPai' retornado na resposta anterior\n" +
                     "3. Nova conversa: não envie 'idConversaPai' ou aguarde 2 horas\n\n" +
                     "**Exemplos de mensagens:**\n" +
                     "- 'Estou me sentindo muito estressado no trabalho'\n" +
                     "- 'Como posso melhorar minha produtividade?'\n" +
                     "- 'Me dê dicas para evitar burnout'\n" +
                     "- 'O que você acha sobre trabalhar remotamente?'"
    )
    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'GESTOR')")
    public ResponseEntity<ChatResponseDTO> chat(@Valid @RequestBody ChatRequestDTO request) {
        ChatResponseDTO response = iaService.chatConversacional(request);
        return ResponseEntity.ok(response);
    }
}

