package com.fiap.comparecimento.application.usecase;

import com.fiap.comparecimento.entrypoint.controllers.dto.AgendamentoDTO;

public interface CalculaComparecimentoUseCase {

    void calculaComparecimento(AgendamentoDTO infos);
}
