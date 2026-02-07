package com.fiap.comparecimento.entrypoint.controllers.listeners;

import com.fiap.comparecimento.application.usecase.CalculaComparecimentoUseCase;
import com.fiap.comparecimento.entrypoint.controllers.dto.AgendamentoDTO;
import com.fiap.comparecimento.infrastructure.config.usecase.rabbit.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class ComparecimentoConsumer {

    private final CalculaComparecimentoUseCase calculaComparecimentoUseCase;

    public ComparecimentoConsumer(CalculaComparecimentoUseCase calculaComparecimentoUseCase) {
        this.calculaComparecimentoUseCase = calculaComparecimentoUseCase;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void consumer(AgendamentoDTO dto){
         calculaComparecimentoUseCase.calculaComparecimento(dto);
    }
}
