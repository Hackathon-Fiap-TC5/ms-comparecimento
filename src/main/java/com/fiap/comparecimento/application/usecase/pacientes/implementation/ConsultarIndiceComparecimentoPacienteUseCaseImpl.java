package com.fiap.comparecimento.application.usecase.pacientes.implementation;

import com.fiap.comparecimento.application.gateway.PacienteGateway;
import com.fiap.comparecimento.application.usecase.pacientes.ConsultarIndiceComparecimentoPacienteUseCase;
import com.fiap.comparecimento.domain.model.PacienteDomain;

public class ConsultarIndiceComparecimentoPacienteUseCaseImpl implements ConsultarIndiceComparecimentoPacienteUseCase {

    private final PacienteGateway pacienteGateway;

    public ConsultarIndiceComparecimentoPacienteUseCaseImpl(PacienteGateway pacienteGateway) {
        this.pacienteGateway = pacienteGateway;
    }

    @Override
    public PacienteDomain consultar(String cns) {
        return pacienteGateway.consultar(cns);
    }
}
