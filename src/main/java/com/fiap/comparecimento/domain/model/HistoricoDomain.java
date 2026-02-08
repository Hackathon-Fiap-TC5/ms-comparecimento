package com.fiap.comparecimento.domain.model;

import java.time.OffsetDateTime;

public class HistoricoDomain {

    private String cns;
    private Long idAgendamento;
    private String statusConsulta;
    private String statusNotificacao;
    private OffsetDateTime dataEvento;

    public HistoricoDomain(String cns, Long idAgendamento, String statusConsulta,
                           String statusNotificacao, OffsetDateTime dataEvento) {
        this.cns = cns;
        this.idAgendamento = idAgendamento;
        this.statusConsulta = statusConsulta;
        this.statusNotificacao = statusNotificacao;
        this.dataEvento = dataEvento;
    }

    public String getCns() {
        return cns;
    }

    public void setCns(String cns) {
        this.cns = cns;
    }

    public Long getIdAgendamento() {
        return idAgendamento;
    }

    public void setIdAgendamento(Long idAgendamento) {
        this.idAgendamento = idAgendamento;
    }

    public String getStatusConsulta() {
        return statusConsulta;
    }

    public void setStatusConsulta(String statusConsulta) {
        this.statusConsulta = statusConsulta;
    }

    public String getStatusNotificacao() {
        return statusNotificacao;
    }

    public void setStatusNotificacao(String statusNotificacao) {
        this.statusNotificacao = statusNotificacao;
    }

    public OffsetDateTime getDataEvento() {
        return dataEvento;
    }

    public void setDataEvento(OffsetDateTime dataEvento) {
        this.dataEvento = dataEvento;
    }
}
