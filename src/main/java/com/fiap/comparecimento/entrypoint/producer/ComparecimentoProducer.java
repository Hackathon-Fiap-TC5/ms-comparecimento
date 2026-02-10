package com.fiap.comparecimento.entrypoint.producer;

import com.fiap.comparecimento.infrastructure.config.rabbit.RabbitMQConfig;
import com.fiap.comparecimentoDomain.gen.model.EventoComparecimentoMessageDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class ComparecimentoProducer {

    private final RabbitTemplate rabbitTemplate;

    public ComparecimentoProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendSugestions(EventoComparecimentoMessageDto eventoComparecimentoMessageDto){
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY_ROLLBACK,
                eventoComparecimentoMessageDto);
    }
}
