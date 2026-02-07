package com.fiap.comparecimento.entrypoint.listeners.presenter;

import com.fiap.comparecimento.domain.model.EventoAgendamentoMessageDomain;
import com.fiap.comparecimento.entrypoint.listeners.mappers.ComparecimentoConsumerMapper;
import com.fiap.comparecimentoDomain.gen.model.EventoAgendamentoMessageDto;

public class ComparecimentoConsumerPresenter {

    public static EventoAgendamentoMessageDomain toEventoAgendamentoMessageDomain(EventoAgendamentoMessageDto dto) {
        return ComparecimentoConsumerMapper.INSTANCE.toEventoAgendamentoMessageDomain(dto);
    }
}
