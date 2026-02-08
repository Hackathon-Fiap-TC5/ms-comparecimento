package com.fiap.comparecimento.application.usecase.calcula.comparecimento;

import com.fiap.comparecimento.domain.model.EventoAgendamentoMessageDomain;

public interface ProcessarComparecimentoUseCase {

    void processaComparecimento(EventoAgendamentoMessageDomain eventoAgendamentoMessageDomain);
}
