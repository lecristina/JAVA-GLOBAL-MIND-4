package com.nexus.messaging.producer;

import com.nexus.config.RabbitMQConfig;
import com.nexus.messaging.events.BurnoutAlertEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlertProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendBurnoutAlert(BurnoutAlertEvent event) {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.BURNOUT_ALERT_QUEUE, event);
            log.info("Alerta de burnout enviado para a fila: {}", event);
        } catch (Exception e) {
            log.error("Erro ao enviar alerta de burnout para a fila", e);
        }
    }
}



