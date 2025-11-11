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
    private final GPTService gptService;

    /**
     * Gera feedback empático usando GPT com base em humor e produtividade
     */
    public String gerarFeedbackEmpatico(Integer humor, String produtividade) {
        return gptService.gerarFeedbackEmpatico(humor, produtividade);
    }

    /**
     * Gera mensagem empática baseada nos últimos registros do usuário
     */
    public String gerarMensagemEmpatica(Integer idUsuario) {
        try {
            List<Humor> ultimosHumor = humorRepository.findByUsuario_IdUsuarioAndDataRegistroBetween(
                    idUsuario, LocalDate.now().minusDays(7), LocalDate.now());
            
            if (ultimosHumor.isEmpty()) {
                return gptService.gerarFeedbackEmpatico(3, "media");
            }
            
            // Pega o humor mais recente
            Humor ultimoHumor = ultimosHumor.get(ultimosHumor.size() - 1);
            Integer humor = ultimoHumor.getNivelHumor();
            
            // Calcula produtividade baseada em sprints recentes
            String produtividade = calcularProdutividadeTexto(idUsuario);
            
            return gptService.gerarFeedbackEmpatico(humor, produtividade);
        } catch (Exception e) {
            log.error("Erro ao gerar mensagem empática", e);
            return "Estamos aqui para apoiá-lo. Lembre-se de cuidar de si mesmo.";
        }
    }

    /**
     * Calcula texto de produtividade baseado em sprints
     */
    private String calcularProdutividadeTexto(Integer idUsuario) {
        try {
            List<Sprint> sprints = sprintRepository.findByUsuario_IdUsuario(idUsuario, 
                    org.springframework.data.domain.Pageable.unpaged()).getContent();
            
            if (sprints.isEmpty()) {
                return "media";
            }
            
            double mediaProdutividade = sprints.stream()
                    .filter(s -> s.getProdutividade() != null)
                    .mapToDouble(s -> s.getProdutividade().doubleValue())
                    .average()
                    .orElse(0.0);
            
            if (mediaProdutividade > 50) return "alta";
            if (mediaProdutividade > 30) return "media";
            return "baixa";
        } catch (Exception e) {
            return "media";
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

    /**
     * Gera análise semanal completa usando GPT
     */
    public GPTService.AnaliseGPT gerarAnaliseSemanal(Integer idUsuario) {
        try {
            // Busca dados históricos
            List<Humor> ultimosHumor = humorRepository.findByUsuario_IdUsuarioAndDataRegistroBetween(
                    idUsuario, LocalDate.now().minusDays(7), LocalDate.now());
            
            List<Habito> habitos = habitoRepository.findByUsuario_IdUsuarioAndDataHabitoBetween(
                    idUsuario, LocalDate.now().minusDays(7), LocalDate.now());
            
            List<Sprint> sprints = sprintRepository.findByUsuario_IdUsuario(idUsuario, 
                    org.springframework.data.domain.Pageable.unpaged()).getContent();

            // Monta string com dados históricos
            StringBuilder dadosHistoricos = new StringBuilder();
            dadosHistoricos.append("DADOS DOS ÚLTIMOS 7 DIAS:\n\n");
            
            dadosHistoricos.append("HUMOR E ENERGIA:\n");
            if (ultimosHumor.isEmpty()) {
                dadosHistoricos.append("- Nenhum registro de humor nos últimos 7 dias\n");
            } else {
                double mediaHumor = ultimosHumor.stream()
                        .mapToInt(Humor::getNivelHumor)
                        .average()
                        .orElse(3.0);
                double mediaEnergia = ultimosHumor.stream()
                        .mapToInt(Humor::getNivelEnergia)
                        .average()
                        .orElse(3.0);
                dadosHistoricos.append(String.format("- Média de humor: %.1f/5\n", mediaHumor));
                dadosHistoricos.append(String.format("- Média de energia: %.1f/5\n", mediaEnergia));
                dadosHistoricos.append(String.format("- Total de registros: %d\n", ultimosHumor.size()));
            }
            
            dadosHistoricos.append("\nHÁBITOS SAUDÁVEIS:\n");
            dadosHistoricos.append(String.format("- Total de hábitos registrados: %d\n", habitos.size()));
            if (!habitos.isEmpty()) {
                int pontuacaoTotal = habitos.stream()
                        .filter(h -> h.getPontuacao() != null)
                        .mapToInt(Habito::getPontuacao)
                        .sum();
                dadosHistoricos.append(String.format("- Pontuação total: %d\n", pontuacaoTotal));
            }
            
            dadosHistoricos.append("\nPRODUTIVIDADE (SPRINTS):\n");
            if (sprints.isEmpty()) {
                dadosHistoricos.append("- Nenhuma sprint registrada\n");
            } else {
                double mediaProdutividade = sprints.stream()
                        .filter(s -> s.getProdutividade() != null)
                        .mapToDouble(s -> s.getProdutividade().doubleValue())
                        .average()
                        .orElse(0.0);
                dadosHistoricos.append(String.format("- Média de produtividade: %.2f\n", mediaProdutividade));
                dadosHistoricos.append(String.format("- Total de sprints: %d\n", sprints.size()));
            }

            return gptService.gerarAnaliseSemanal(dadosHistoricos.toString());
        } catch (Exception e) {
            log.error("Erro ao gerar análise semanal", e);
            return GPTService.AnaliseGPT.builder()
                    .resumo("Análise não disponível no momento.")
                    .risco("medio")
                    .sugestoes(List.of("Monitore seus indicadores", "Mantenha hábitos saudáveis", "Faça pausas regulares"))
                    .build();
        }
    }

    /**
     * Analisa risco de burnout (método legado mantido para compatibilidade)
     */
    public String analisarRiscoBurnout(Integer idUsuario) {
        GPTService.AnaliseGPT analise = gerarAnaliseSemanal(idUsuario);
        return String.format("Risco: %s. %s", 
                analise.getRisco().toUpperCase(), 
                analise.getResumo());
    }
}
