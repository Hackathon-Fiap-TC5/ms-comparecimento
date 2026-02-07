package com.fiap.comparecimento.application.usecase.pacientes;

import com.fiap.comparecimento.domain.model.PacienteDomain;

public interface ConsultarIndiceComparecimentoPacienteUseCase {

    PacienteDomain consultar(String cns);
}
