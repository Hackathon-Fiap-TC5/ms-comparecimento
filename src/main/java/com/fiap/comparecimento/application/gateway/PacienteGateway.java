package com.fiap.comparecimento.application.gateway;

import com.fiap.comparecimento.domain.model.PacienteDomain;
import com.fiap.comparecimento.domain.model.RelatorioAbsenteismoDomain;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface PacienteGateway {

    PacienteDomain consultar(String cns);

    Optional<PacienteDomain> verificaExistenciaPaciente(String cns);

    void criaOuAtualizarInformacoesPaciente(PacienteDomain domain);

    RelatorioAbsenteismoDomain consultarRelatorioAbsenteismo(OffsetDateTime dataInicioOffset, OffsetDateTime dataFimOffset);
}
