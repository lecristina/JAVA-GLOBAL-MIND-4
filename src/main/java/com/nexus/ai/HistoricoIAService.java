package com.nexus.ai;

import com.nexus.domain.model.AlertaIA;
import com.nexus.infrastructure.repository.AlertaIARepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço para gerenciar histórico de interações com IA
 * Permite personalização e variação de respostas baseado em histórico
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HistoricoIAService {

    private final AlertaIARepository alertaIARepository;

    /**
     * Busca histórico de feedbacks do usuário
     */
    public List<String> buscarHistoricoFeedback(Integer usuarioId, Integer humor, String produtividade) {
        LocalDate dataInicio = LocalDate.now().minusDays(30);
        return alertaIARepository.findByUsuario_IdUsuarioAndTipoAlertaAndDataAlertaAfter(
                usuarioId, "FEEDBACK_EMPATICO", dataInicio)
                .stream()
                .map(AlertaIA::getMensagem)
                .filter(msg -> msg != null && !msg.isEmpty())
                .limit(10) // Últimas 10 interações
                .collect(Collectors.toList());
    }

    /**
     * Busca histórico de análises do usuário
     */
    public List<String> buscarHistoricoAnalise(Integer usuarioId) {
        LocalDate dataInicio = LocalDate.now().minusDays(30);
        return alertaIARepository.findByUsuario_IdUsuarioAndTipoAlertaAndDataAlertaAfter(
                usuarioId, "ANALISE_SEMANAL", dataInicio)
                .stream()
                .map(AlertaIA::getMensagem)
                .filter(msg -> msg != null && !msg.isEmpty())
                .limit(5) // Últimas 5 análises
                .collect(Collectors.toList());
    }

    /**
     * Busca histórico de assistente do usuário
     */
    public List<String> buscarHistoricoAssistente(Integer usuarioId, String tipoConsulta) {
        LocalDate dataInicio = LocalDate.now().minusDays(30);
        return alertaIARepository.findByUsuario_IdUsuarioAndTipoAlertaAndDataAlertaAfter(
                usuarioId, "ASSISTENTE_" + tipoConsulta.toUpperCase(), dataInicio)
                .stream()
                .map(AlertaIA::getMensagem)
                .filter(msg -> msg != null && !msg.isEmpty())
                .limit(10) // Últimas 10 interações
                .collect(Collectors.toList());
    }

    /**
     * Gera contexto de histórico para evitar repetições
     */
    public String gerarContextoHistorico(List<String> historico) {
        if (historico == null || historico.isEmpty()) {
            return "Este é o primeiro feedback para este usuário. Seja criativo e original.";
        }

        StringBuilder contexto = new StringBuilder();
        contexto.append("HISTÓRICO DE INTERAÇÕES ANTERIORES (ÚLTIMAS ").append(historico.size()).append("):\n");
        
        for (int i = 0; i < historico.size(); i++) {
            String msg = historico.get(i);
            if (msg.length() > 100) {
                msg = msg.substring(0, 100) + "...";
            }
            contexto.append(String.format("%d. %s\n", i + 1, msg));
        }
        
        contexto.append("\nIMPORTANTE: Sua resposta DEVE ser DIFERENTE das anteriores. ");
        contexto.append("Use abordagens variadas, diferentes metáforas, diferentes exemplos. ");
        contexto.append("Seja criativo e evite repetir o mesmo tom ou estrutura. ");
        contexto.append("Varie entre: perguntas, afirmações, sugestões práticas, reflexões, curiosidades científicas.");
        
        return contexto.toString();
    }

    /**
     * Calcula temperatura dinâmica baseada em histórico
     * Mais histórico = maior temperatura (mais variação)
     */
    public double calcularTemperaturaDinamica(List<String> historico) {
        if (historico == null || historico.isEmpty()) {
            return 0.7; // Temperatura padrão
        }
        
        // Mais histórico = maior temperatura (mais criatividade)
        int tamanhoHistorico = historico.size();
        double temperatura = 0.7 + (tamanhoHistorico * 0.05);
        
        // Limitar entre 0.7 e 1.2
        return Math.min(1.2, Math.max(0.7, temperatura));
    }

    /**
     * Gera variação de abordagem baseada em histórico
     */
    public String gerarVariacaoAbordagem(List<String> historico) {
        if (historico == null || historico.isEmpty()) {
            return "Use uma abordagem direta e acolhedora.";
        }

        int tamanhoHistorico = historico.size();
        int variacao = tamanhoHistorico % 5; // Cicla entre 5 abordagens diferentes

        switch (variacao) {
            case 0:
                return "Use uma abordagem com PERGUNTAS REFLEXIVAS que ajudem o usuário a pensar sobre si mesmo.";
            case 1:
                return "Use uma abordagem com CURIOSIDADES CIENTÍFICAS e dados interessantes sobre bem-estar.";
            case 2:
                return "Use uma abordagem com METÁFORAS e analogias criativas para explicar conceitos.";
            case 3:
                return "Use uma abordagem com SUGESTÕES PRÁTICAS e acionáveis, passo a passo.";
            case 4:
                return "Use uma abordagem com REFLEXÕES PROFUNDAS e insights sobre produtividade e bem-estar.";
            default:
                return "Use uma abordagem empática e personalizada.";
        }
    }
}

