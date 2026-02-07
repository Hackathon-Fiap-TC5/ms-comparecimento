package com.fiap.comparecimento.application.usecase.relatorios.implementation;

import com.fiap.comparecimento.application.gateway.PacienteGateway;
import com.fiap.comparecimento.application.usecase.relatorios.ConsultarIndicadoresPorPeriodoUseCase;
import com.fiap.comparecimento.domain.model.RelatorioAbsenteismoDomain;
import com.fiap.comparecimento.utils.DateTimeUtils;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class ConsultarIndicadoresPorPeriodoUseCaseImpl implements ConsultarIndicadoresPorPeriodoUseCase {

    private final PacienteGateway pacienteGateway;

    public ConsultarIndicadoresPorPeriodoUseCaseImpl(PacienteGateway pacienteGateway) {
        this.pacienteGateway = pacienteGateway;
    }

    @Override
    public RelatorioAbsenteismoDomain consultar(LocalDate dataInicio, LocalDate dataFim) {
        OffsetDateTime dataInicioOffset = DateTimeUtils.inicioDoDia(dataInicio);
        OffsetDateTime dataFimOffset = DateTimeUtils.fimDoDia(dataFim);

        return pacienteGateway.consultarRelatorioAbsenteismo(dataInicioOffset, dataFimOffset);
    }
}
