package com.fiap.comparecimento.application.usecase.calcula.comparecimento;

import com.fiap.comparecimento.domain.model.EventoAgendamentoMessageDomain;
import com.fiap.comparecimento.domain.model.PacienteDomain;

public interface CalculaComparecimentoUseCase {

    void calculaComparecimento(PacienteDomain pacienteDomain, EventoAgendamentoMessageDomain eventoAgendamentoMessageDomain);
}
