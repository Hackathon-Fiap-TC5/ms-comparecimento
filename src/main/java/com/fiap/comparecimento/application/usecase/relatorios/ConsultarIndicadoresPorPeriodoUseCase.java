package com.fiap.comparecimento.application.usecase.relatorios;

import com.fiap.comparecimento.domain.model.RelatorioAbsenteismoDomain;

import java.time.LocalDate;

public interface ConsultarIndicadoresPorPeriodoUseCase {

    RelatorioAbsenteismoDomain consultar(LocalDate dataInicio, LocalDate dataFim);
}
