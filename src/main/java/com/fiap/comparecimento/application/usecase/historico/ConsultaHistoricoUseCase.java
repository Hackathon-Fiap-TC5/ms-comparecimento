package com.fiap.comparecimento.application.usecase.historico;

import com.fiap.comparecimento.domain.model.HistoricoDomain;

import java.util.List;

public interface ConsultaHistoricoUseCase {

    List<HistoricoDomain> getHistorico(Long idAgendamento, String cns);
}
