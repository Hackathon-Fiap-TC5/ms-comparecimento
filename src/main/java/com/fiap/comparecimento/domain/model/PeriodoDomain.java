package com.fiap.comparecimento.domain.model;

import java.time.OffsetDateTime;

public class PeriodoDomain {

    private OffsetDateTime dataInicio;
    private OffsetDateTime dataFim;

    public PeriodoDomain() {
    }

    public PeriodoDomain(OffsetDateTime dataInicio, OffsetDateTime dataFim) {
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }

    public OffsetDateTime getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(OffsetDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    public OffsetDateTime getDataFim() {
        return dataFim;
    }

    public void setDataFim(OffsetDateTime dataFim) {
        this.dataFim = dataFim;
    }
}
