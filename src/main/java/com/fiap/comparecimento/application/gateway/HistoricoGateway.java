package com.fiap.comparecimento.application.gateway;

import com.fiap.comparecimento.domain.model.HistoricoDomain;

import java.util.List;

public interface HistoricoGateway {

    List<HistoricoDomain> getHistorico(Long idAgendamento, String cns);

    void adiciona(HistoricoDomain historicoDomain);
}
