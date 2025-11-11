package com.nexus.messaging.consumer;

import com.nexus.domain.model.AlertaIA;
import com.nexus.domain.model.Usuario;
import com.nexus.infrastructure.repository.AlertaIARepository;
import com.nexus.infrastructure.repository.UsuarioRepository;
import com.nexus.messaging.events.BurnoutAlertEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "spring.rabbitmq.host")
public class AlertConsumer {

    private final AlertaIARepository alertaIARepository;
    private final UsuarioRepository usuarioRepository;

    @RabbitListener(queues = "${spring.rabbitmq.queue.burnout-alert:burnout.alert.queue}")
    public void consumeBurnoutAlert(BurnoutAlertEvent event) {
        try {
            log.info("Processando alerta de burnout: {}", event);
            
            Usuario usuario = usuarioRepository.findById(event.getIdUsuario())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + event.getIdUsuario()));

            AlertaIA alerta = AlertaIA.builder()
                    .usuario(usuario)
                    .dataAlerta(LocalDate.now())
                    .tipoAlerta(event.getTipoAlerta())
                    .mensagem("Alerta de burnout detectado - Humor: " + event.getNivelHumor() + 
                             ", Energia: " + event.getNivelEnergia())
                    .nivelRisco(event.getNivelRisco())
                    .build();

            alertaIARepository.save(alerta);
            log.info("Alerta de burnout salvo com sucesso: {}", alerta.getIdAlerta());
        } catch (Exception e) {
            log.error("Erro ao processar alerta de burnout", e);
        }
    }
}

