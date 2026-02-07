package com.fiap.comparecimento.application.usecase;

import com.fiap.comparecimento.domain.model.EventoAgendamentoMessageDomain;

public interface CalculaComparecimentoUseCase {

    void calculaComparecimento(EventoAgendamentoMessageDomain eventoAgendamentoMessageDomain);
}
