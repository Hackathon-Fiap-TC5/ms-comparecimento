package com.fiap.comparecimento.application.usecase;

import com.fiap.comparecimento.domain.model.PacienteDomain;

public interface ConsultarIndiceComparecimentoPacienteUseCase {

    PacienteDomain consultar(String cns);
}
