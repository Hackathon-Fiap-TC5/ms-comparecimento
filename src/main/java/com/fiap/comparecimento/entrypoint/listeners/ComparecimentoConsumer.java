package com.fiap.comparecimento.entrypoint.listeners;

import com.fiap.comparecimento.application.usecase.CalculaComparecimentoUseCase;
import com.fiap.comparecimento.domain.model.EventoAgendamentoMessageDomain;
import com.fiap.comparecimento.entrypoint.listeners.presenter.ComparecimentoConsumerPresenter;
import com.fiap.comparecimento.infrastructure.config.rabbit.RabbitMQConfig;
import com.fiap.comparecimentoDomain.gen.model.EventoAgendamentoMessageDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class ComparecimentoConsumer {

    private final CalculaComparecimentoUseCase calculaComparecimentoUseCase;

    public ComparecimentoConsumer(CalculaComparecimentoUseCase calculaComparecimentoUseCase) {
        this.calculaComparecimentoUseCase = calculaComparecimentoUseCase;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void consumer(EventoAgendamentoMessageDto dto){
        EventoAgendamentoMessageDomain eventoAgendamentoMessageDomain = ComparecimentoConsumerPresenter.toEventoAgendamentoMessageDomain(dto);
         calculaComparecimentoUseCase.calculaComparecimento(eventoAgendamentoMessageDomain);
    }
}