package com.fiap.comparecimento.entrypoint.consumer;

import com.fiap.comparecimento.application.usecase.calcula.comparecimento.ProcessarComparecimentoUseCase;
import com.fiap.comparecimento.domain.model.EventoAgendamentoMessageDomain;
import com.fiap.comparecimento.entrypoint.consumer.presenter.ComparecimentoConsumerPresenter;
import com.fiap.comparecimento.infrastructure.config.rabbit.RabbitMQConfig;
import com.fiap.comparecimentoDomain.gen.model.EventoAgendamentoMessageDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class ComparecimentoConsumer {

    private final ProcessarComparecimentoUseCase processarComparecimentoUseCase;

    public ComparecimentoConsumer(ProcessarComparecimentoUseCase processarComparecimentoUseCase) {
        this.processarComparecimentoUseCase = processarComparecimentoUseCase;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void consumer(EventoAgendamentoMessageDto dto){
        EventoAgendamentoMessageDomain eventoAgendamentoMessageDomain = ComparecimentoConsumerPresenter.toEventoAgendamentoMessageDomain(dto);
        processarComparecimentoUseCase.processaComparecimento(eventoAgendamentoMessageDomain);
    }
}