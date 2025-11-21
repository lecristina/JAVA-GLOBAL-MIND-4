package com.nexus.modules.ia.controller;

import com.nexus.application.dto.AnaliseRequestDTO;
import com.nexus.application.dto.AnaliseResponseDTO;
import com.nexus.application.dto.AssistenteRequestDTO;
import com.nexus.application.dto.AssistenteResponseDTO;
import com.nexus.application.dto.ChatRequestDTO;
import com.nexus.application.dto.ChatResponseDTO;
import com.nexus.application.dto.AssistantAnalisarRequestDTO;
import com.nexus.application.dto.CoPlannerRequestDTO;
import com.nexus.application.dto.CoPlannerResponseDTO;
import com.nexus.application.dto.FeedbackRequestDTO;
import com.nexus.application.dto.FeedbackResponseDTO;
import com.nexus.application.dto.PausaMonitorRequestDTO;
import com.nexus.application.dto.PausaMonitorResponseDTO;
import com.nexus.modules.ia.service.IAService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ia")
@Tag(name = "IA Generativa", description = "Endpoints de IA para feedback empático e análises inteligentes")
@SecurityRequirement(name = "bearerAuth")
public class IAController {

    private final IAService iaService;
    
    public IAController(IAService iaService) {
        this.iaService = iaService;
    }

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
                     "Também aceita 'tipo' e 'mensagem' para processar agenda ou conteúdo personalizado. " +
                     "Retorna título, conteúdo, ações práticas e reflexão para aplicar na vida real."
    )
    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'GESTOR')")
    public ResponseEntity<?> gerarConteudoAssistente(@Valid @RequestBody AssistenteRequestDTO request) {
        // Se for tipo "agenda", retorna JSON diretamente (como String)
        if (request.getTipo() != null && "agenda".equalsIgnoreCase(request.getTipo()) && 
            request.getMensagem() != null && !request.getMensagem().trim().isEmpty()) {
            AssistenteResponseDTO response = iaService.gerarConteudoAssistente(request);
            // Retorna o JSON do campo conteudo diretamente
            return ResponseEntity.ok()
                    .header("Content-Type", "application/json")
                    .body(response.getConteudo());
        }
        
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

    @PostMapping("/co-planner")
    @Operation(
        summary = "Co-planejador de IA - Extrai tarefas de mensagens em linguagem natural",
        description = "Transforma pensamentos e ideias em tarefas estruturadas. " +
                     "Similar ao Tiimo AI co-planner, este endpoint analisa mensagens em linguagem natural " +
                     "e extrai tarefas com horários, descrições e prioridades.\n\n" +
                     "**Como usar:**\n" +
                     "Envie uma mensagem descrevendo suas tarefas e compromissos. " +
                     "A IA irá extrair e estruturar as tarefas automaticamente.\n\n" +
                     "**Exemplos de mensagens:**\n" +
                     "- \"hoje preciso levar minha gata ao veterinário as 14 e preciso terminar a materia de java para o challenge\"\n" +
                     "- \"amanhã tenho reunião às 10h, preciso preparar o relatório e comprar presente para aniversário\"\n" +
                     "- \"preciso estudar para prova de matemática, fazer exercícios físicos e ligar para minha mãe\"\n\n" +
                     "**Resposta:**\n" +
                     "Retorna uma lista de tarefas estruturadas com:\n" +
                     "- horario: Horário no formato HH:mm (ex: \"14:00\") ou null se não especificado\n" +
                     "- descricao: Descrição clara e concisa da tarefa\n" +
                     "- prioridade: ALTA, MEDIA ou BAIXA"
    )
    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'GESTOR')")
    public ResponseEntity<CoPlannerResponseDTO> coPlanner(@Valid @RequestBody CoPlannerRequestDTO request) {
        CoPlannerResponseDTO response = iaService.extrairTarefas(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/assistant/analisar")
    @Operation(
        summary = "Assistant - Analisar mensagem e retornar JSON estruturado",
        description = "Processa mensagens do usuário e retorna JSON estruturado conforme o tipo solicitado.\n\n" +
                     "**Tipos disponíveis:**\n" +
                     "- `agenda`: Extrai compromissos e transforma em tasks com data, categoria e prioridade\n" +
                     "- `conteudo` ou `motivacao`: Gera conteúdo personalizado de apoio emocional/motivacional\n\n" +
                     "**Exemplo 1 - Agenda:**\n" +
                     "```json\n" +
                     "{\n" +
                     "  \"usuarioId\": 1,\n" +
                     "  \"tipo\": \"agenda\",\n" +
                     "  \"mensagem\": \"tenho cabeleireiro hoje às 14h, depilação na quarta-feira e viagem no final do ano\"\n" +
                     "}\n" +
                     "```\n\n" +
                     "**Resposta (Agenda):**\n" +
                     "```json\n" +
                     "{\n" +
                     "  \"tasks\": [\n" +
                     "    {\"titulo\": \"Cabeleireiro\", \"data\": \"2025-11-13T14:00:00\", \"categoria\": \"Beleza\", \"prioridade\": \"Normal\"},\n" +
                     "    {\"titulo\": \"Depilação\", \"data\": \"2025-11-15T10:00:00\", \"categoria\": \"Beleza\", \"prioridade\": \"Normal\"},\n" +
                     "    {\"titulo\": \"Viagem de fim de ano\", \"data\": \"2025-12-28T08:00:00\", \"categoria\": \"Pessoal\", \"prioridade\": \"Alta\"}\n" +
                     "  ]\n" +
                     "}\n" +
                     "```\n\n" +
                     "**Exemplo 2 - Conteúdo/Motivação:**\n" +
                     "```json\n" +
                     "{\n" +
                     "  \"usuarioId\": 1,\n" +
                     "  \"tipo\": \"motivacao\",\n" +
                     "  \"mensagem\": \"me manda algo pra me animar hoje, tô sem energia\"\n" +
                     "}\n" +
                     "```\n\n" +
                     "**Resposta (Conteúdo):**\n" +
                     "```json\n" +
                     "{\n" +
                     "  \"tipo\": \"motivacao\",\n" +
                     "  \"titulo\": \"A energia vem do propósito\",\n" +
                     "  \"conteudo\": \"Você não precisa estar 100% para dar o seu melhor — só precisa começar.\",\n" +
                     "  \"acoes_praticas\": [\"Faça algo pequeno por você hoje\", \"Lembre-se do motivo que te move\"],\n" +
                     "  \"reflexao\": \"O que me inspira a continuar mesmo nos dias difíceis?\"\n" +
                     "}\n" +
                     "```"
    )
    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'GESTOR')")
    public ResponseEntity<String> analisarMensagemAssistant(@Valid @RequestBody AssistantAnalisarRequestDTO request) {
        String resposta = iaService.processarMensagemAssistant(request);
        return ResponseEntity.ok(resposta);
    }

    @PostMapping("/pausa-monitor")
    @Operation(
        summary = "Monitoramento de Pausas e Movimento - Detecção de presença/ausência",
        description = "Monitora presença do usuário através de análise de movimento em frames de vídeo. " +
                     "Não identifica pessoa especificamente, apenas detecta variação de pixels entre frames.\n\n" +
                     "**Como funciona:**\n" +
                     "1. Envie frames de vídeo periodicamente (ex: a cada 5-10 segundos)\n" +
                     "2. O sistema compara frames consecutivos e detecta movimento\n" +
                     "3. Se não houver movimento por 5 minutos → ausência detectada\n" +
                     "4. Se detectar muito tempo sentado (1h+) → sugere alongamentos\n" +
                     "5. Pausas são registradas automaticamente quando usuário retorna\n\n" +
                     "**Request:**\n" +
                     "```json\n" +
                     "{\n" +
                     "  \"usuarioId\": 1,\n" +
                     "  \"frameBase64\": \"iVBORw0KGgoAAAANS...\"\n" +
                     "}\n" +
                     "```\n\n" +
                     "**Response:**\n" +
                     "```json\n" +
                     "{\n" +
                     "  \"usuarioId\": 1,\n" +
                     "  \"movimentoDetectado\": true,\n" +
                     "  \"quantidadeMovimento\": 25000,\n" +
                     "  \"presente\": true,\n" +
                     "  \"tempoSentadoMinutos\": 75,\n" +
                     "  \"totalPausas\": 3,\n" +
                     "  \"sugerirAlongamento\": true,\n" +
                     "  \"mensagem\": \"Movimento detectado. Usuário presente.\",\n" +
                     "  \"sugestoes\": [\n" +
                     "    \"💡 Você está sentado há 75 minutos. Hora de se alongar!\",\n" +
                     "    \"🏃 Faça uma pausa de 5 minutos: levante-se, caminhe e alongue braços e pernas\"\n" +
                     "  ],\n" +
                     "  \"timestamp\": \"2025-01-15T10:30:00\"\n" +
                     "}\n" +
                     "```\n\n" +
                     "**Tecnologias:**\n" +
                     "- Processamento de imagem nativo Java (BufferedImage)\n" +
                     "- Detecção de movimento por diferença de pixels\n" +
                     "- Blur gaussiano para reduzir ruído\n" +
                     "- Sem dependências externas pesadas (OpenCV não necessário)\n\n" +
                     "**Uso recomendado:**\n" +
                     "- Envie frames a cada 5-10 segundos durante o trabalho\n" +
                     "- Use webcam comum ou câmera de notebook\n" +
                     "- O sistema mantém sessão ativa por usuário\n" +
                     "- Para resetar sessão, envie `resetarSessao: true`"
    )
    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'GESTOR')")
    public ResponseEntity<PausaMonitorResponseDTO> monitorarPausa(@Valid @RequestBody PausaMonitorRequestDTO request) {
        PausaMonitorResponseDTO response = iaService.monitorarPausa(request);
        return ResponseEntity.ok(response);
    }
}

