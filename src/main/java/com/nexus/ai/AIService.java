package com.nexus.ai;

import com.nexus.domain.model.Humor;
import com.nexus.domain.model.Habito;
import com.nexus.domain.model.Sprint;
import com.nexus.infrastructure.repository.HumorRepository;
import com.nexus.infrastructure.repository.HabitoRepository;
import com.nexus.infrastructure.repository.SprintRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIService {

    private final HumorRepository humorRepository;
    private final HabitoRepository habitoRepository;
    private final SprintRepository sprintRepository;

    public String gerarMensagemEmpatica(Integer idUsuario) {
        try {
            List<Humor> ultimosHumor = humorRepository.findByUsuario_IdUsuarioAndDataRegistroBetween(
                    idUsuario, LocalDate.now().minusDays(7), LocalDate.now());
            
            // TODO: Implementar com Spring AI quando disponível
            // Por enquanto retorna mensagem baseada no contexto
            if (ultimosHumor.isEmpty()) {
                return "Estamos aqui para apoiá-lo. Lembre-se de cuidar de si mesmo.";
            }
            
            // Análise simples baseada nos dados
            double mediaHumor = ultimosHumor.stream()
                    .mapToInt(Humor::getNivelHumor)
                    .average()
                    .orElse(3.0);
            
            if (mediaHumor <= 2) {
                return "Notamos que você está passando por um momento difícil. Lembre-se de que é importante cuidar de si mesmo. Considere fazer uma pausa e buscar apoio se necessário.";
            } else if (mediaHumor <= 3) {
                return "Estamos aqui para apoiá-lo. Lembre-se de cuidar de si mesmo e buscar equilíbrio entre trabalho e descanso.";
            } else {
                return "Continue mantendo esse equilíbrio! Você está no caminho certo.";
            }
        } catch (Exception e) {
            log.error("Erro ao gerar mensagem empática", e);
            return "Estamos aqui para apoiá-lo. Lembre-se de cuidar de si mesmo.";
        }
    }

    public String gerarMensagemMotivacional(Integer idUsuario) {
        try {
            List<Sprint> sprints = sprintRepository.findByUsuario_IdUsuario(idUsuario, 
                    org.springframework.data.domain.Pageable.unpaged()).getContent();
            
            // TODO: Implementar com Spring AI quando disponível
            if (sprints.isEmpty()) {
                return "Continue focado e determinado. Você está no caminho certo!";
            }
            
            // Análise simples baseada nos dados
            double mediaProdutividade = sprints.stream()
                    .filter(s -> s.getProdutividade() != null)
                    .mapToDouble(s -> s.getProdutividade().doubleValue())
                    .average()
                    .orElse(0.0);
            
            if (mediaProdutividade > 50) {
                return "Excelente trabalho! Sua produtividade está em alta. Continue assim!";
            } else if (mediaProdutividade > 30) {
                return "Bom trabalho! Continue focado e determinado. Você está no caminho certo!";
            } else {
                return "Continue se esforçando! Cada sprint é uma oportunidade de crescimento.";
            }
        } catch (Exception e) {
            log.error("Erro ao gerar mensagem motivacional", e);
            return "Continue focado e determinado. Você está no caminho certo!";
        }
    }

    public String analisarRiscoBurnout(Integer idUsuario) {
        try {
            List<Humor> ultimosHumor = humorRepository.findByUsuario_IdUsuarioAndDataRegistroBetween(
                    idUsuario, LocalDate.now().minusDays(14), LocalDate.now());
            
            List<Habito> habitos = habitoRepository.findByUsuario_IdUsuarioAndDataHabitoBetween(
                    idUsuario, LocalDate.now().minusDays(14), LocalDate.now());

            // TODO: Implementar com Spring AI quando disponível
            // Análise simples baseada nos dados
            if (ultimosHumor.isEmpty()) {
                return "Risco: Baixo. Recomendamos monitorar seus níveis de humor e energia regularmente.";
            }
            
            double mediaHumor = ultimosHumor.stream()
                    .mapToInt(Humor::getNivelHumor)
                    .average()
                    .orElse(3.0);
            
            double mediaEnergia = ultimosHumor.stream()
                    .mapToInt(Humor::getNivelEnergia)
                    .average()
                    .orElse(3.0);
            
            int totalHabitos = habitos.size();
            
            double score = (mediaHumor + mediaEnergia) / 2;
            
            if (score <= 2 || totalHabitos < 3) {
                return "Risco: ALTO. Detectamos sinais de possível burnout. Recomendamos fortemente buscar apoio profissional e fazer pausas regulares.";
            } else if (score <= 3) {
                return "Risco: MÉDIO. Alguns sinais de estresse foram detectados. Recomendamos monitorar seus níveis de humor e energia, e manter hábitos saudáveis.";
            } else {
                return "Risco: BAIXO. Seus indicadores estão positivos. Continue mantendo hábitos saudáveis e monitoramento regular.";
            }
        } catch (Exception e) {
            log.error("Erro ao analisar risco de burnout", e);
            return "Análise de risco não disponível no momento. Recomendamos monitorar seus níveis de humor e energia regularmente.";
        }
    }
}
