package com.nexus.ai;

import lombok.Data;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.ImageIO;

/**
 * Serviço de Monitoramento de Pausas e Movimento
 * 
 * Detecta presença/ausência do usuário através de análise de movimento em frames de vídeo.
 * Não identifica pessoa especificamente, apenas detecta variação de pixels entre frames.
 * 
 * Funcionalidades:
 * - Detecta movimento entre frames consecutivos
 * - Rastreia tempo sentado (sem movimento)
 * - Detecta ausência após X minutos sem movimento
 * - Registra pausas automaticamente
 * - Sugere alongamentos quando detecta muito tempo sentado
 */
@Service
@Slf4j
public class PausaMonitorService {

    // Armazena o último frame processado por usuário
    private final Map<Integer, FrameData> ultimoFramePorUsuario = new ConcurrentHashMap<>();
    
    // Armazena estatísticas de sessão por usuário
    private final Map<Integer, SessaoMonitoramento> sessoes = new ConcurrentHashMap<>();
    
    // Configurações
    private static final int MOTION_THRESHOLD = 20000; // Quantidade mínima de pixels diferentes para considerar movimento
    private static final int ABSENCE_LIMIT_SECONDS = 60 * 5; // 5 minutos sem movimento = ausência
    private static final int SITTING_ALERT_MINUTES = 60; // Alerta após 1 hora sentado
    private static final int GAUSSIAN_BLUR_SIZE = 21; // Tamanho do blur para reduzir ruído
    
    /**
     * Processa um frame de vídeo e detecta movimento
     * 
     * @param usuarioId ID do usuário
     * @param frameBytes Bytes da imagem (JPEG, PNG, etc)
     * @return Resultado da análise de movimento
     */
    public ResultadoMonitoramento processarFrame(Integer usuarioId, byte[] frameBytes) {
        try {
            log.debug("Processando frame para usuário {} - Tamanho: {} bytes", usuarioId, frameBytes.length);
            
            // Converter bytes para BufferedImage
            BufferedImage frameAtual = ImageIO.read(new ByteArrayInputStream(frameBytes));
            if (frameAtual == null) {
                log.warn("Não foi possível ler a imagem do frame");
                return criarResultadoErro("Erro ao processar imagem");
            }
            
            // Obter ou criar sessão de monitoramento
            SessaoMonitoramento sessao = sessoes.computeIfAbsent(usuarioId, 
                k -> SessaoMonitoramento.builder()
                    .usuarioId(usuarioId)
                    .inicioSessao(LocalDateTime.now())
                    .ultimoMovimento(LocalDateTime.now())
                    .tempoSentadoMinutos(0)
                    .totalPausas(0)
                    .build());
            
            // Obter frame anterior
            FrameData frameAnterior = ultimoFramePorUsuario.get(usuarioId);
            
            boolean movimentoDetectado = false;
            int quantidadeMovimento = 0;
            
            if (frameAnterior != null && frameAnterior.getFrame() != null) {
                // Calcular diferença entre frames
                quantidadeMovimento = calcularDiferencaFrames(frameAnterior.getFrame(), frameAtual);
                movimentoDetectado = quantidadeMovimento > MOTION_THRESHOLD;
                
                log.debug("Diferença detectada: {} pixels - Movimento: {}", quantidadeMovimento, movimentoDetectado);
            } else {
                // Primeiro frame - não há comparação possível
                log.debug("Primeiro frame recebido para usuário {}", usuarioId);
                movimentoDetectado = true; // Considera como movimento inicial
            }
            
            // Atualizar estatísticas
            LocalDateTime agora = LocalDateTime.now();
            
            if (movimentoDetectado) {
                // Movimento detectado - usuário presente
                sessao.setUltimoMovimento(agora);
                
                // Se estava ausente e agora detectou movimento, registra retorno
                if (sessao.isAusente()) {
                    log.info("✅ Usuário {} retornou após {} minutos ausente", 
                        usuarioId, sessao.getMinutosAusente());
                    sessao.setAusente(false);
                    sessao.setTotalPausas(sessao.getTotalPausas() + 1);
                }
            } else {
                // Sem movimento - verificar se está ausente
                long segundosSemMovimento = java.time.Duration.between(sessao.getUltimoMovimento(), agora).getSeconds();
                
                if (segundosSemMovimento >= ABSENCE_LIMIT_SECONDS && !sessao.isAusente()) {
                    log.info("⚠️ Usuário {} ausente há {} segundos (limite: {})", 
                        usuarioId, segundosSemMovimento, ABSENCE_LIMIT_SECONDS);
                    sessao.setAusente(true);
                    sessao.setInicioAusencia(agora);
                }
                
                // Calcular tempo sentado (sem movimento significativo)
                long minutosSentado = java.time.Duration.between(sessao.getInicioSessao(), agora).toMinutes();
                sessao.setTempoSentadoMinutos((int) minutosSentado);
            }
            
            // Salvar frame atual para próxima comparação
            ultimoFramePorUsuario.put(usuarioId, FrameData.builder()
                .frame(frameAtual)
                .timestamp(agora)
                .build());
            
            // Verificar se precisa sugerir alongamento
            boolean sugerirAlongamento = sessao.getTempoSentadoMinutos() >= SITTING_ALERT_MINUTES 
                && sessao.getTempoSentadoMinutos() % 30 == 0; // Sugerir a cada 30 minutos após 1 hora
            
            // Construir resposta
            return ResultadoMonitoramento.builder()
                .usuarioId(usuarioId)
                .movimentoDetectado(movimentoDetectado)
                .quantidadeMovimento(quantidadeMovimento)
                .presente(!sessao.isAusente())
                .tempoSentadoMinutos(sessao.getTempoSentadoMinutos())
                .totalPausas(sessao.getTotalPausas())
                .sugerirAlongamento(sugerirAlongamento)
                .mensagem(gerarMensagem(sessao, movimentoDetectado, quantidadeMovimento))
                .sugestoes(gerarSugestoes(sessao, sugerirAlongamento))
                .timestamp(agora)
                .build();
                
        } catch (IOException e) {
            log.error("Erro ao processar frame para usuário {}", usuarioId, e);
            return criarResultadoErro("Erro ao processar imagem: " + e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado ao processar frame para usuário {}", usuarioId, e);
            return criarResultadoErro("Erro inesperado: " + e.getMessage());
        }
    }
    
    /**
     * Calcula diferença entre dois frames usando processamento de imagem
     * Converte para escala de cinza, aplica blur gaussiano e calcula diferença absoluta
     */
    private int calcularDiferencaFrames(BufferedImage frame1, BufferedImage frame2) {
        try {
            // Redimensionar se necessário (para performance)
            int width = Math.min(frame1.getWidth(), frame2.getWidth());
            int height = Math.min(frame1.getHeight(), frame2.getHeight());
            
            // Converter para escala de cinza e aplicar blur
            BufferedImage gray1 = converterParaCinza(frame1, width, height);
            BufferedImage gray2 = converterParaCinza(frame2, width, height);
            
            BufferedImage blur1 = aplicarBlurGaussiano(gray1);
            BufferedImage blur2 = aplicarBlurGaussiano(gray2);
            
            // Calcular diferença absoluta
            int diferencaTotal = 0;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pixel1 = blur1.getRGB(x, y) & 0xFF; // Apenas canal de intensidade
                    int pixel2 = blur2.getRGB(x, y) & 0xFF;
                    int diferenca = Math.abs(pixel1 - pixel2);
                    
                    // Threshold: considerar apenas diferenças significativas (> 25)
                    if (diferenca > 25) {
                        diferencaTotal++;
                    }
                }
            }
            
            return diferencaTotal;
        } catch (Exception e) {
            log.error("Erro ao calcular diferença entre frames", e);
            return 0;
        }
    }
    
    /**
     * Converte imagem para escala de cinza
     */
    private BufferedImage converterParaCinza(BufferedImage original, int width, int height) {
        BufferedImage gray = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        java.awt.Graphics2D g = gray.createGraphics();
        g.drawImage(original, 0, 0, width, height, null);
        g.dispose();
        return gray;
    }
    
    /**
     * Aplica blur gaussiano simples (aproximação usando média ponderada)
     * Reduz ruído na imagem para melhor detecção de movimento
     */
    private BufferedImage aplicarBlurGaussiano(BufferedImage original) {
        int width = original.getWidth();
        int height = original.getHeight();
        BufferedImage blurred = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        
        int kernelSize = GAUSSIAN_BLUR_SIZE;
        int radius = kernelSize / 2;
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                long soma = 0;
                int count = 0;
                
                // Aplicar kernel de blur
                for (int ky = -radius; ky <= radius; ky++) {
                    for (int kx = -radius; kx <= radius; kx++) {
                        int px = x + kx;
                        int py = y + ky;
                        
                        if (px >= 0 && px < width && py >= 0 && py < height) {
                            int pixel = original.getRGB(px, py) & 0xFF;
                            soma += pixel;
                            count++;
                        }
                    }
                }
                
                int media = (int) (soma / count);
                int grayPixel = (media << 16) | (media << 8) | media;
                blurred.setRGB(x, y, grayPixel);
            }
        }
        
        return blurred;
    }
    
    /**
     * Gera mensagem descritiva do estado atual
     */
    private String gerarMensagem(SessaoMonitoramento sessao, boolean movimento, int quantidadeMovimento) {
        if (sessao.isAusente()) {
            return String.format("Usuário ausente há %d minutos. Aguardando retorno...", 
                sessao.getMinutosAusente());
        }
        
        if (movimento) {
            return String.format("Movimento detectado (%d pixels diferentes). Usuário presente.", 
                quantidadeMovimento);
        }
        
        if (sessao.getTempoSentadoMinutos() >= SITTING_ALERT_MINUTES) {
            return String.format("Sem movimento significativo há %d minutos. Considere fazer uma pausa!", 
                sessao.getTempoSentadoMinutos());
        }
        
        return "Monitoramento ativo. Ambiente estável.";
    }
    
    /**
     * Gera sugestões baseadas no estado atual
     */
    private java.util.List<String> gerarSugestoes(SessaoMonitoramento sessao, boolean sugerirAlongamento) {
        java.util.List<String> sugestoes = new java.util.ArrayList<>();
        
        if (sugerirAlongamento) {
            sugestoes.add("💡 Você está sentado há " + sessao.getTempoSentadoMinutos() + " minutos. Hora de se alongar!");
            sugestoes.add("🏃 Faça uma pausa de 5 minutos: levante-se, caminhe e alongue braços e pernas");
            sugestoes.add("👀 Descanse os olhos: olhe para longe por 20 segundos a cada 20 minutos");
            sugestoes.add("💧 Beba água e aproveite para se movimentar");
        }
        
        if (sessao.getTempoSentadoMinutos() >= 90) {
            sugestoes.add("⏰ Você está há mais de 1h30 sentado. É recomendado fazer uma pausa mais longa (15-20 min)");
        }
        
        if (sessao.getTotalPausas() == 0 && sessao.getTempoSentadoMinutos() >= 60) {
            sugestoes.add("📊 Você ainda não fez pausas hoje. Lembre-se: pausas regulares melhoram produtividade!");
        }
        
        if (sugestoes.isEmpty()) {
            sugestoes.add("✅ Continue monitorando. Lembre-se de fazer pausas a cada 90 minutos");
        }
        
        return sugestoes;
    }
    
    /**
     * Cria resultado de erro
     */
    private ResultadoMonitoramento criarResultadoErro(String mensagem) {
        return ResultadoMonitoramento.builder()
            .movimentoDetectado(false)
            .presente(false)
            .mensagem("Erro: " + mensagem)
            .sugestoes(java.util.List.of("Verifique se a imagem foi enviada corretamente"))
            .timestamp(LocalDateTime.now())
            .build();
    }
    
    /**
     * Reseta sessão de monitoramento para um usuário
     */
    public void resetarSessao(Integer usuarioId) {
        sessoes.remove(usuarioId);
        ultimoFramePorUsuario.remove(usuarioId);
        log.info("Sessão de monitoramento resetada para usuário {}", usuarioId);
    }
    
    /**
     * Obtém estatísticas da sessão atual
     */
    public SessaoMonitoramento obterEstatisticas(Integer usuarioId) {
        return sessoes.get(usuarioId);
    }
    
    // Classes internas para armazenamento de dados
    
    @Data
    @Builder
    static class FrameData {
        private BufferedImage frame;
        private LocalDateTime timestamp;
    }
    
    @Data
    @Builder
    public static class SessaoMonitoramento {
        private Integer usuarioId;
        private LocalDateTime inicioSessao;
        private LocalDateTime ultimoMovimento;
        private boolean ausente;
        private LocalDateTime inicioAusencia;
        private int tempoSentadoMinutos;
        private int totalPausas;
        
        public int getMinutosAusente() {
            if (!ausente || inicioAusencia == null) {
                return 0;
            }
            return (int) java.time.Duration.between(inicioAusencia, LocalDateTime.now()).toMinutes();
        }
    }
    
    @Data
    @Builder
    public static class ResultadoMonitoramento {
        private Integer usuarioId;
        private boolean movimentoDetectado;
        private int quantidadeMovimento;
        private boolean presente;
        private int tempoSentadoMinutos;
        private int totalPausas;
        private boolean sugerirAlongamento;
        private String mensagem;
        private java.util.List<String> sugestoes;
        private LocalDateTime timestamp;
    }
}

