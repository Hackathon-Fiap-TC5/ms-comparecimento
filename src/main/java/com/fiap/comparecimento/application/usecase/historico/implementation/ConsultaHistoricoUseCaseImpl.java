package com.fiap.comparecimento.application.usecase.historico.implementation;

import com.fiap.comparecimento.application.gateway.HistoricoGateway;
import com.fiap.comparecimento.application.usecase.historico.ConsultaHistoricoUseCase;
import com.fiap.comparecimento.domain.model.HistoricoDomain;

import java.util.List;

public class ConsultaHistoricoUseCaseImpl implements ConsultaHistoricoUseCase {

    private final HistoricoGateway historicoGateway;

    public ConsultaHistoricoUseCaseImpl(HistoricoGateway historicoGateway) {
        this.historicoGateway = historicoGateway;
    }

    @Override
    public List<HistoricoDomain> getHistorico(Long idAgendamento, String cns) {
        return historicoGateway.getHistorico(idAgendamento, cns);
    }
}
