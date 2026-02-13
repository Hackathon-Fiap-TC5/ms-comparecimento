package com.fiap.comparecimento.domain.domainService;

import com.fiap.comparecimento.domain.model.EventoAgendamentoMessageDomain;

public interface FilaVivaAsyncDomainService {

    void processarFilaViva(EventoAgendamentoMessageDomain evento);
}
