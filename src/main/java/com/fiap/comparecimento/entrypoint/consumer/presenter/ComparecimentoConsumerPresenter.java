package com.fiap.comparecimento.entrypoint.consumer.presenter;

import com.fiap.comparecimento.domain.model.EventoAgendamentoMessageDomain;
import com.fiap.comparecimento.entrypoint.consumer.mappers.ComparecimentoConsumerMapper;
import com.fiap.comparecimentoDomain.gen.model.EventoAgendamentoMessageDto;

public class ComparecimentoConsumerPresenter {

    public static EventoAgendamentoMessageDomain toEventoAgendamentoMessageDomain(EventoAgendamentoMessageDto dto) {
        return ComparecimentoConsumerMapper.INSTANCE.toEventoAgendamentoMessageDomain(dto);
    }
}
