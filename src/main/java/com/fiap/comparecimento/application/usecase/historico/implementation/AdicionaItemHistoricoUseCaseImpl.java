package com.fiap.comparecimento.application.usecase.historico.implementation;

import com.fiap.comparecimento.application.gateway.HistoricoGateway;
import com.fiap.comparecimento.application.usecase.historico.AdicionaItemHistoricoUseCase;
import com.fiap.comparecimento.domain.model.HistoricoDomain;

public class AdicionaItemHistoricoUseCaseImpl implements AdicionaItemHistoricoUseCase {

    private final HistoricoGateway historicoGateway;

    public AdicionaItemHistoricoUseCaseImpl(HistoricoGateway historicoGateway) {
        this.historicoGateway = historicoGateway;
    }

    @Override
    public void adicionaItemHistorico(HistoricoDomain historicoDomain) {
        historicoGateway.adiciona(historicoDomain);
    }
}
