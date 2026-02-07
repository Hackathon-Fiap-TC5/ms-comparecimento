package com.fiap.comparecimento.infrastructure.database.projection;

import java.math.BigDecimal;

public interface RelatorioAbsenteismoProjection {

    Integer getTotalPessoas();

    BigDecimal getIccMedio();

    Integer getTotalConsultas();

    Integer getTotalFaltas();

    BigDecimal getTaxaAbsenteismo();
}