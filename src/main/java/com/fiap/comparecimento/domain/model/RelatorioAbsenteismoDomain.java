package com.fiap.comparecimento.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;

public class RelatorioAbsenteismoDomain {

    private PeriodoDomain periodo;
    private Integer totalPessoas;
    private BigDecimal iccMedio;
    private Integer totalConsultas;
    private Integer totalFaltas;
    private BigDecimal taxaAbsenteismo;
    private OffsetDateTime dataGeracao;

    public RelatorioAbsenteismoDomain() {
    }

    public RelatorioAbsenteismoDomain(PeriodoDomain periodo, Integer totalPessoas, BigDecimal iccMedio, Integer totalConsultas, Integer totalFaltas, BigDecimal taxaAbsenteismo, OffsetDateTime dataGeracao) {
        this.periodo = periodo;
        this.totalPessoas = totalPessoas;
        this.iccMedio = iccMedio;
        this.totalConsultas = totalConsultas;
        this.totalFaltas = totalFaltas;
        this.taxaAbsenteismo = taxaAbsenteismo;
        this.dataGeracao = dataGeracao;
    }

    public PeriodoDomain getPeriodo() {
        return periodo;
    }

    public void setPeriodo(PeriodoDomain periodo) {
        this.periodo = periodo;
    }

    public Integer getTotalPessoas() {
        return totalPessoas;
    }

    public void setTotalPessoas(Integer totalPessoas) {
        this.totalPessoas = totalPessoas;
    }

    public BigDecimal getIccMedio() {
        return iccMedio;
    }

    public void setIccMedio(BigDecimal iccMedio) {
        this.iccMedio = iccMedio;
    }

    public Integer getTotalConsultas() {
        return totalConsultas;
    }

    public void setTotalConsultas(Integer totalConsultas) {
        this.totalConsultas = totalConsultas;
    }

    public Integer getTotalFaltas() {
        return totalFaltas;
    }

    public void setTotalFaltas(Integer totalFaltas) {
        this.totalFaltas = totalFaltas;
    }

    public BigDecimal getTaxaAbsenteismo() {
        return taxaAbsenteismo;
    }

    public void setTaxaAbsenteismo(BigDecimal taxaAbsenteismo) {
        this.taxaAbsenteismo = taxaAbsenteismo;
    }

    public OffsetDateTime getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(OffsetDateTime dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public void ajustarEscalas() {
        if (taxaAbsenteismo != null) {
            taxaAbsenteismo = taxaAbsenteismo
                    .setScale(2, RoundingMode.HALF_UP);
        }

        if (iccMedio != null) {
            iccMedio = iccMedio
                    .setScale(2, RoundingMode.HALF_UP);
        }
    }
}