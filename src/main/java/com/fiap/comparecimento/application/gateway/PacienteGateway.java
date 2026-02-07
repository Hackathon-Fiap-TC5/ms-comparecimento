package com.fiap.comparecimento.application.gateway;

import com.fiap.comparecimento.domain.model.PacienteDomain;

public interface PacienteGateway {

    PacienteDomain consultar(String cns);

    void save(PacienteDomain domain);
}
